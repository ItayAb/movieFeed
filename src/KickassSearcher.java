import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MyInterfaces.IFetcher;

public class KickassSearcher implements IFetcher {
	
	private int m_NumOfThreads;
	private ConcurrentHashMap<String, Boolean> movies;
	private ConcurrentLinkedQueue<String> m_addresses;
	
	public KickassSearcher(int numOfThreads){
		 m_NumOfThreads = numOfThreads;		 
		 m_addresses = new ConcurrentLinkedQueue<>();
	}
	
	@Override
	public HashMap<String, Boolean> Fetch(){
		movies = new ConcurrentHashMap<>();
		final String kickAssTorrent = "http://kickasstorrents.to/movies/";
		ArrayList<String> a = getAdd(kickAssTorrent);
		Thread urlGetter = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<String> pageTwo = getAdd(kickAssTorrent + "2/" );
				for (String add : pageTwo) {
					m_addresses.add(add);
				}
			}
		});
		
		Thread[] imdbQuerier = new Thread[m_NumOfThreads];
		for (int i = 0; i < imdbQuerier.length; i++) {
			imdbQuerier[i] = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (!m_addresses.isEmpty()) {
						String address = m_addresses.poll(); 
						if (address != null && address != "") {
							GetMovie(address);
						}
						
						try {
							Thread.sleep(200);
						} catch (Exception e) {
							// TODO: handle exception
							Logger.GetInstance().Log("couldnt sleep thread %s", e.getMessage());
						}
					}
				}
			});
		}
		
		urlGetter.start();
		for (int i = 0; i < imdbQuerier.length; i++) {
			imdbQuerier[i].start();
		}
		try {
			urlGetter.join();
			for (int i = 0; i < imdbQuerier.length; i++) {
				imdbQuerier[i].join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Logger.GetInstance().Log("Couldn't join threads %s", e.getMessage());
			e.printStackTrace();
		}
		Logger.GetInstance().Log("KickAss got %d movies", movies.size());		
		return new HashMap<String, Boolean>(movies);	
	}
	
	private ArrayList<MovieInfo> getMovieInfo(HashMap<String, Boolean> movies){
		for (Entry<String, Boolean> entry : movies.entrySet()) {
			System.out.println("got " + entry.getKey());			
		}
		
		ImdbSearcher iSearcher = new ImdbSearcher();
		ArrayList<MovieInfo> moviesInfo = iSearcher.FindMovies(movies, false, MyEnums.ImdbSearcherEnum.Title);
		
		for (MovieInfo movieInfo : moviesInfo) {
			System.out.printf("movie: %s, rating: %s\n", movieInfo.Title, movieInfo.imdbRating);
		}
		return moviesInfo;
	}
	
	
	private  void GetMovie(String a) {
	
		String moviePage = WebPageDownloader.GetWebPageJsoup("http://kickasstorrents.to" + a);

		String patternMovieData = "<ul class=\"block overauto botmarg0\">(.*?)</ul>";
        Pattern re = Pattern.compile(patternMovieData);
        
        Matcher movieMatches = re.matcher(moviePage);
       // System.out.println(m.group());            
        
        while (movieMatches.find()) {
        	//<strong>Detected quality:(.*?)<span(.*?)>(.*?)</span></a></li>
        	//<strong>Detected quality:(.*?)<span(.*?)>(.*?)<\/span><\/li>
        	String movieNamePattern = "<strong>Movie:(.*?)<span>(.*?)</span></a></li>";
        	String movieQualityPattern = "<strong>Detected quality:(.*?)<span(.*?)>(.*?)</span></li>";
            Pattern reName = Pattern.compile(movieNamePattern);
            Pattern reQuality = Pattern.compile(movieQualityPattern);
            
            Matcher movieName = reName.matcher(movieMatches.group(1));
            Matcher movieQuality = reQuality.matcher(movieMatches.group(1));
        	//System.out.println("movie info" + movieMatches.group(1));
        	if (movieName.find() && movieQuality.find()) {
        		String name = movieName.group(2);
        		String quality = movieQuality.group(3);
        		System.out.printf("movie: %s quality:%s isHD: %b\n", name, quality, isKickAssHD(quality));
				if (!movies.containsKey(name)) {
					movies.put(name, true);					
				} else if (!movies.get(name) && isKickAssHD(quality)) {
					System.out.printf("updating %s to be true HD\n", name);
					movies.put(name, true);
				}
				
				System.out.println("count:" + movies.size());
			}
		}          	
		
		System.out.println("Done!");		
	}
	
	private  HashMap<String, Boolean> GetMovies(ArrayList<String> a, String url) {
		HashMap<String, Boolean> movies = new HashMap<>();		
		for (String movieUrl : a) {
		
			String moviePage = WebPageDownloader.GetWebPageJsoup(url + movieUrl);

			String patternMovieData = "<ul class=\"block overauto botmarg0\">(.*?)</ul>";
            Pattern re = Pattern.compile(patternMovieData);
            
            Matcher movieMatches = re.matcher(moviePage);
           // System.out.println(m.group());            
            
            while (movieMatches.find()) {
            	//<strong>Detected quality:(.*?)<span(.*?)>(.*?)</span></a></li>
            	//<strong>Detected quality:(.*?)<span(.*?)>(.*?)<\/span><\/li>
            	String movieNamePattern = "<strong>Movie:(.*?)<span>(.*?)</span></a></li>";
            	String movieQualityPattern = "<strong>Detected quality:(.*?)<span(.*?)>(.*?)</span></li>";
                Pattern reName = Pattern.compile(movieNamePattern);
                Pattern reQuality = Pattern.compile(movieQualityPattern);
                
                Matcher movieName = reName.matcher(movieMatches.group(1));
                Matcher movieQuality = reQuality.matcher(movieMatches.group(1));
            	//System.out.println("movie info" + movieMatches.group(1));
            	if (movieName.find() && movieQuality.find()) {
            		String name = movieName.group(2);
            		String quality = movieQuality.group(3);
            		System.out.printf("movie: %s quality:%s isHD: %b\n", name, quality, isKickAssHD(quality));
					if (!movies.containsKey(name)) {
						movies.put(name, true);
					} else if (!movies.get(name) && isKickAssHD(quality)) {
						System.out.printf("updating %s to be true HD\n", name);
						movies.put(name, true);
					}
					
					System.out.println("count:" + movies.size());
				}
			}  
            
		}			
		
		System.out.println("Done!");
		return movies;
	}

	public static boolean isKickAssHD(String quality) {
		String[] hdQualities = new String[]{"HD", "hd", "BDRip", "HDTC", "720p", "1080p", "DVDRip", "WEB-DL"};
		
		for (String hdQuality : hdQualities) {
			if (quality.contains(hdQuality)) {
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<String> getAdd (String url){		
		ArrayList<String> addresses =  new ArrayList<>();
		System.out.println("kickass searcher");
		
		String kickassPage = WebPageDownloader.GetWebPageJsoup(url);
		String patternForEntireSection = "<div class=\"markeredBlock torType filmType\">(.*?)</div>";
        Pattern r = Pattern.compile(patternForEntireSection);
        
        Matcher m = r.matcher(kickassPage);          
        while (m.find()) {
        	String patternMovieNames = "<a href=\"(.*?\\.html)\"";
            Pattern re = Pattern.compile(patternMovieNames);
            
            Matcher movieMatches = re.matcher(m.group(0));
            
            while (movieMatches.find()) {            	
            	System.out.println("movie : "+ movieMatches.group(1));
            	//addresses.add(movieMatches.group(1));
            	m_addresses.add(movieMatches.group(1));
			}               	      
		}

		System.out.println("there are " + addresses.size());
		return addresses;
	}

}
