import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.StyledEditorKit.BoldAction;

import MyInterfaces.IFetcher;

public class PirateBaySearcher implements IFetcher {

	public static void main(String[] args) {
		PirateBaySearcher p = new PirateBaySearcher(10);
		HashMap<String, Boolean> m = p.Fetch();
		System.out.println("finished quering");
		System.out.println("overall collection " + m.size());
		for (String movie : m.keySet()) {
			System.out.println("got " + movie);
		}
	}

	public static String pirateBayLink = "https://fastpiratebay.co.uk";
	private ConcurrentHashMap<String, Boolean> movies;
	private MyInterfaces.IMovieFinder<MovieInfo> searcher;
	private ConcurrentLinkedQueue<String> m_MoviesQueue;
	private Thread urlQuerier;
	private Thread[] imdbQuerier;
	private Boolean v_IsRunning = new Boolean(true);
	private int m_NumOfThreads;

	public PirateBaySearcher(int num) {
		m_NumOfThreads = num;
		movies = new ConcurrentHashMap<String, Boolean>();
		searcher = new ImdbSearcher();
		m_MoviesQueue = new ConcurrentLinkedQueue<String>();			
	}
	
	private Thread[] initQueriers(int num) {
		Thread[] imdbQuerier = new Thread[num];
		for (int i = 0; i < imdbQuerier.length; i++) {
			imdbQuerier[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					String movie;
					while (v_IsRunning) {
						if (!m_MoviesQueue.isEmpty()) {
							movie = m_MoviesQueue.poll();
							Logger.GetInstance().Log("Queue Size: %d, got from queue %s", m_MoviesQueue.size(), movie);
							if (movie != null && movie != "") {
								Logger.GetInstance().Log("getting movies %s %s", movie, Thread.currentThread());
								getMovie(movie);
							}
						}
					}
					Logger.GetInstance().Log("%s is exiting", Thread.currentThread());
				}
			});
		}
		
		return imdbQuerier;
	}

	private void getMovie(String a) {
		String url = pirateBayLink + a;
		String page = WebPageDownloader.GetWebPageJsoup(url);

		String imdbPattern = "<pre>(.*?)((http|https)://www\\.(imdb|IMDB)\\.com/title/(.*?)/)(.*?)</pre>";
		Pattern p = Pattern.compile(imdbPattern);

		Matcher m = p.matcher(page);

		if (m.find()) {
			// System.out.println(m.group());
			// System.out.println(m.group(5));
			if (!movies.containsKey(m.group(5))) {
				movies.put(m.group(5), true);
				Logger.GetInstance().Log("Added %s %s", m.group(5), Thread.currentThread());
				// AddToHash(m.group(5));
				// movies.put(m.group(5), true);
			}
		}

		// return movies;
	}

	private HashMap<String, Boolean> GetMovies(ArrayList<String> a) {
		HashMap<String, Boolean> movies = new HashMap<>();
		String url = null;
		String page = null;
		for (String address : a) {
			url = pirateBayLink + address;
			page = WebPageDownloader.GetWebPageJsoup(url);

			String imdbPattern = "<pre>(.*?)((http|https)://www\\.(imdb|IMDB)\\.com/title/(.*?)/)(.*?)</pre>";
			Pattern p = Pattern.compile(imdbPattern);

			Matcher m = p.matcher(page);

			if (m.find()) {
				System.out.println(m.group());
				System.out.println(m.group(5));
				if (!movies.containsKey(m.group(5))) {
					AddToHash(m.group(5));
					// movies.put(m.group(5), true);
				}
			}
		}

		return movies;
	}

	private synchronized void AddToHash(String group) {
		this.movies.put(group, true);
	}

	private void getAddresses(String page) {
		ArrayList<String> addresses = new ArrayList<>();

		String patternForMovieTable = "<td>(.*?)<a href=\"(.*?)\"\\s+(.*?)</td>";
		System.out.println(patternForMovieTable);
		Pattern r = Pattern.compile(patternForMovieTable);

		Matcher m = r.matcher(page);
		while (m.find()) {
			System.out.println(m.group(2));
			addresses.add(m.group(2));
			m_MoviesQueue.add(m.group(2));
			System.out.println("Queue size: " + m_MoviesQueue.size());
		}
		System.out.println("got " + addresses.size());
	}

	@Override
	public HashMap<String, Boolean> Fetch() {
		System.out.println("Starting");
		v_IsRunning = true;
		final String piratePage = WebPageDownloader.GetWebPageJsoup(pirateBayLink + "/top/207");
		urlQuerier = new Thread(new Runnable() {

			@Override
			public void run() {
				getAddresses(piratePage);
				while (!m_MoviesQueue.isEmpty()) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						Logger.GetInstance().Log("couldnt sleep thread %s", e.getMessage());
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				v_IsRunning = false;

			}
		});
		urlQuerier.start();
		if (!urlQuerier.isAlive()) {
			Logger.GetInstance().Log("Could not start url query");
			return null;
		}
		imdbQuerier = initQueriers(m_NumOfThreads);
		for (int i = 0; i < imdbQuerier.length; i++) {
			imdbQuerier[i].start();
		}

		try {
			urlQuerier.join();
			Logger.GetInstance().Log("setting running to false");
			for (int i = 0; i < imdbQuerier.length; i++) {
				imdbQuerier[i].join();				
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.GetInstance().Log("Error when joining threads %s", e.getMessage());
		}
		// ArrayList<String> a = GetAddresses(piratePage);
		return new HashMap<String, Boolean>(this.movies);
	}

	private ArrayList<MovieInfo> getMovieInfos(HashMap<String, Boolean> movies) {
		return searcher.FindMovies(movies, true, MyEnums.ImdbSearcherEnum.ImdbID);
	}
}
