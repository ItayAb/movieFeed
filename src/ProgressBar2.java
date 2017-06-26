import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

import MyInterfaces.INotifiable;

public class ProgressBar2 implements Runnable {

	private JFrame frame;
	private JProgressBar progressBar;
	public int Max;
	private int step;
	private int count;
	private INotifiable Notify;
	private boolean closing;

	/**
	 * Create the application.
	 */
	public ProgressBar2(INotifiable n) {
		Notify = n;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void Show() {
		initialize();
		this.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 699, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		progressBar = new JProgressBar();
		progressBar.setMaximum(Max);
		step = 1;
		progressBar.setBounds(45, 66, 569, 88);
		Border border = BorderFactory.createTitledBorder("Getting "+ Max + " movies");
		frame.getContentPane().add(progressBar);
		progressBar.setStringPainted(true);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelSearch();
			}

		});
		btnCancel.setBounds(45, 190, 97, 25);
		frame.addWindowListener(new WindowAdapter(){
            public void windowClosed(WindowEvent e){
            	if (closing) {            		
					return;
				}
                cancelSearch();
            }
        });
		frame.getContentPane().add(btnCancel);
	}

	public void Step() {
		progressBar.setValue(progressBar.getValue()+ step);
		progressBar.setString(String.format("%d/%d",count, Max));
		count++;		
		if (count == Max) {
			closing = true;
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
	}

	@Override
	public void run() {
		this.Show();
	}
	

	private void cancelSearch() {		
		if (Notify != null) {
			Notify.Nofity(MovieFetcherEnum.Cancel_Search, null);
		}
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		closing = true;
	}
	
	public void Close(){
		closing = true;
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
