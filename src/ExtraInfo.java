import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JTable;

import MyInterfaces.INotifiable;


public class ExtraInfo implements Runnable{

	private final String IMDB_ADDRESS = "www.imdb.com/title/";
	private JFrame frame;
	private MovieInfo mi;
	private static INotifiable listener;
	private JLabel lblTitle;
	private JLabel lblRating;

	/**
	 * Create the application.
	 */
	public ExtraInfo(MovieInfo m) {
		mi = m;
		initialize();
	}
	
	public void Show(){		
		EventQueue.invokeLater(this);
	}
	
	
	public void run(){
		initialize();
		try {								
			this.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.getContentPane().setLayout(null);
		
		lblTitle = new JLabel("Title");
		lblTitle.setBounds(12, 13, 151, 16);
		lblTitle.setText(mi.Title);
		frame.getContentPane().add(lblTitle);
		
		lblRating = new JLabel("Rating");
		lblRating.setBounds(12, 42, 76, 16);
		lblRating.setText(mi.imdbRating);
		frame.getContentPane().add(lblRating);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 71, 151, 101);
		frame.getContentPane().add(scrollPane);
		
		JTextArea textAreaPlot = new JTextArea(mi.Plot);
		scrollPane.setViewportView(textAreaPlot);
		textAreaPlot.setLineWrap(true);
		textAreaPlot.setWrapStyleWord(true);
		textAreaPlot.setOpaque(false);
		textAreaPlot.setEditable(false);
		
		JLabel lblImage = new JLabel("N/A");
		lblImage.setBounds(214, 11, 170, 161);
		frame.getContentPane().add(lblImage);
		ImageIcon poster;
		try {
			poster = new ImageIcon(new URL(mi.Poster));
			lblImage.setText("");
			lblImage.setIcon(Utils.FitImageToLabel(lblImage.getHeight(), poster));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			lblImage.setText(e1.getMessage());
		}
		
		JButton btnImdb = new JButton("IMDB");
		btnImdb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String imdbLink = String.format("%s%s", IMDB_ADDRESS, mi.imdbID);
				System.out.printf("imdb link  %s\n", imdbLink);
//				URI imdbURI;
//				try {
//					imdbURI = new URI(imdbLink);
//				} catch (URISyntaxException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//					JOptionPane.showMessageDialog(null, e1.getMessage(), "Error: " , JOptionPane.INFORMATION_MESSAGE);
//					return;
//				}
//				if (!DesktopApi.browse(imdbURI)){
//					JOptionPane.showMessageDialog(null, "Couldn't open link" + imdbLink, "Error: " , JOptionPane.INFORMATION_MESSAGE);
//				}
				
			
				String errorMessage = Utils.OpenWebPage(imdbLink);
				if (errorMessage != null) {
					JOptionPane.showMessageDialog(null, errorMessage, "Error: " , JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		btnImdb.setBounds(12, 199, 68, 25);
		frame.getContentPane().add(btnImdb);
		
		JLabel lblYear = new JLabel("New label");
		lblYear.setBounds(90, 42, 82, 16);
		lblYear.setText(String.format("Year: %s", mi.Year));
		frame.getContentPane().add(lblYear);
	}
}
