import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.acl.LastOwnerException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import MyInterfaces.INotifiable;

public class FetcherUI implements INotifiable {

	private JFrame frame;
	private JList<MovieInfo> scrollableList;
	private JLabel posterLabel;
	IceFilmsFetcher mfetch;
	ImdbSearcher searcher;
	ProgressBar2 progressBar;
	private JButton btnFetchMovies;
	SwingWorker<Void, Void> backgroundWorker;
	private JCheckBox chckbxHd;
	private Logger logger;
	private ArrayList<MyInterfaces.IFetcher> movieFetchers;
	private JLabel labelLastFetchTime;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FetcherUI window = new FetcherUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */

	public FetcherUI() {
		logger = Logger.GetInstance();
		mfetch = new IceFilmsFetcher();
		searcher = new ImdbSearcher(this);
		movieFetchers = new ArrayList<>();
		movieFetchers.add(new IceFilmsFetcher());
		movieFetchers.add(new KickassSearcher(10));
		movieFetchers.add(new PirateBaySearcher(15));
		initialize();
	}

	public void fetchMovies() throws InterruptedException, ExecutionException {
		final HashMap<String, Boolean> movies = new HashMap<>();
		for (MyInterfaces.IFetcher fetcher : this.movieFetchers) {
			for (Entry<String, Boolean> entry : fetcher.Fetch().entrySet()) {
				movies.put(entry.getKey(), entry.getValue());
			}
		}
		// final HashMap<String, Boolean> movies = mfetch.GetMovies();
		logger.Log("got %d movies", movies.size());
		progressBar = new ProgressBar2(this);
		int sumMovies = 0;
		for (String movie : movies.keySet()) {
			if (chckbxHd.isSelected() && !movies.get(movie)) {
				continue;
			}
			sumMovies++;
		}
		progressBar.Max = sumMovies;
		try {
			new Thread(progressBar).start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Failed to open progress bar", "ProgressBar", JOptionPane.INFORMATION_MESSAGE);
			logger.Log(e.getMessage());
		}

		logger.Log("searching movies in IMDB");
		final HashMap<String, Boolean> moviesFinal = movies;
		backgroundWorker = new SwingWorker<Void, Void>() {
			ArrayList<MovieInfo> movieList;

			@Override
			protected Void doInBackground() throws Exception {

				// TODO Auto-generated method stub
				movieList = searcher.FindMovies(moviesFinal, chckbxHd.isSelected(), MyEnums.ImdbSearcherEnum.Title);
				return null;
			}

			@Override
			protected void done() {
				btnFetchMovies.setEnabled(true);
				if (movieList == null) {
					return;
				}

				updateMovieList(movieList);
			}
		};
		backgroundWorker.execute();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 693, 460);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		btnFetchMovies = new JButton("Fetch Movies");
		btnFetchMovies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.Log("Fetching movies");
				btnFetchMovies.setEnabled(false);
				try {
					fetchMovies();
				} catch (InterruptedException e1) {
					logger.Log("interrupt" + e1.getMessage());
				} catch (ExecutionException e1) {
					logger.Log("ExecutionException" + e1.getMessage());
				}
			}
		});
		btnFetchMovies.setBounds(56, 28, 210, 32);
		frame.getContentPane().add(btnFetchMovies);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(56, 73, 210, 311);
		frame.getContentPane().add(scrollPane);

		scrollableList = new JList<MovieInfo>();
		scrollableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollableList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (scrollableList.getValueIsAdjusting()) {
					return;
				}
				MovieInfo selection = (MovieInfo) scrollableList.getSelectedValue();
				if (selection == null) {
					return;
				}
				displayImage(selection);
			}
		});
		scrollPane.setViewportView(scrollableList);

		posterLabel = new JLabel("");
		posterLabel.setBounds(347, 75, 243, 300);
		frame.getContentPane().add(posterLabel);

		chckbxHd = new JCheckBox("HD");
		chckbxHd.setSelected(true);
		chckbxHd.setEnabled(true);
		chckbxHd.setBounds(274, 32, 45, 25);
		frame.getContentPane().add(chckbxHd);
		JButton btnExtraInfo = new JButton("Extra Info");
		btnExtraInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Opening extra info");
				MovieInfo mi = (MovieInfo) scrollableList.getSelectedValue();
				if (mi == null) {
					System.out.println("choose movie for extra info");
					JOptionPane.showMessageDialog(null, "Choose movie for extra info", "Extra Info: ", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				new ExtraInfo(mi).Show();
			}
		});
		btnExtraInfo.setBounds(357, 388, 97, 25);
		frame.getContentPane().add(btnExtraInfo);

		labelLastFetchTime = new JLabel("");
		labelLastFetchTime.setBounds(57, -1, 201, 16);
		frame.getContentPane().add(labelLastFetchTime);
	}

	private void displayImage(MovieInfo mi) {
		Exception ex = null;
		try {
			ImageIcon posterIcon = new ImageIcon(new URL(mi.Poster));
			posterLabel.setIcon(Utils.FitImageToLabel(posterLabel.getHeight(), posterIcon));
		} catch (MalformedURLException e) {
			ex = e;
		} catch (IOException e) {
			ex = e;
		} finally {
			if (ex != null) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "InfoBox: ", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	@Override
	public void Nofity(Enum e, Object object) {
		if (!(e instanceof MovieFetcherEnum)) {
			System.out.println("Invalid enum value");
		}
		MovieFetcherEnum messageType = (MovieFetcherEnum) e;
		switch (messageType) {
		case Single_Movie:
			if (object instanceof MovieInfo) {
				if (progressBar != null) {
					logger.Log("stepping progess bar");
					progressBar.Step();
				}
			}
			break;
		case Search_Complete:
			progressBar.Close();
			break;
		case Cancel_Search:
			searcher.StopSearch();
			backgroundWorker.cancel(true);
			JOptionPane.showMessageDialog(null, "Cancelled search");
			btnFetchMovies.setEnabled(true);
			break;
		default:
			break;
		}
	}

	private void updateMovieList(ArrayList<MovieInfo> movieInfos) {
		ArrayList<MovieInfo> movieInfoFiltered = new ArrayList<>();
		int filteredMovieInfos = 0;
		for (MovieInfo mi : movieInfos) {
			if (MovieInfo.IsValid(mi)) {
				movieInfoFiltered.add(mi);
			} else {
				filteredMovieInfos++;
			}
		}
		Logger.GetInstance().Log("skipped %d null movie infos", filteredMovieInfos);
		Collections.sort(movieInfoFiltered, new Comparator<MovieInfo>() {

			@Override
			public int compare(MovieInfo o1, MovieInfo o2) {
				int diff = 0;
				try {
					if (o1 == null || o2 == null) {
						Logger.GetInstance().Log("one of them is null");
						if (o1 == null && o2 == null) {
							return 0;
						}
						if (o1 == null) {
							return -1;
						}
						return 1;
					}
					diff = Integer.parseInt(o1.Year) - Integer.parseInt(o2.Year);
				} catch (Exception e) {
					logger.Log(e.getMessage());
					logger.Log("year1: %s, year2: %s", o1.Year, o2.Year);
					return 0;
				}

				if (diff > 0) {
					return -1;
				} else if (diff < 0) {
					return 1;
				}
				return 0;
			}
		});
		DefaultListModel<MovieInfo> moviesModel = new DefaultListModel<>();
		for (MovieInfo mi : movieInfoFiltered) {
			if (!MovieInfo.IsValid(mi)) {
				continue;
			}
			moviesModel.addElement(mi);
		}

		scrollableList.setModel(moviesModel);
		String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date());
		labelLastFetchTime.setText(timeStamp);
		logger.Log("Updated UI with movie list %s", timeStamp);
	}
}
