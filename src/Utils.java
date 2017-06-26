import java.awt.Desktop;
import java.awt.Image;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;


public class Utils {

	public static String OpenWebPage(String url){
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		try {
		    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
		    	desktop.browse(new URI(url));
		    }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return e.getMessage();
	    } 
		
		return null;
	}
	
	public static ImageIcon FitImageToLabel(int labelHeight, ImageIcon imageIcon) {
		System.out.println("width" + imageIcon.getIconWidth() + ", height" + imageIcon.getIconHeight());
		double posterRatio = (double) imageIcon.getIconWidth() / imageIcon.getIconHeight();
		System.out.println("ratio is " + posterRatio);
		int ratioWidth = (int) (posterRatio * labelHeight);

		return new ImageIcon(imageIcon.getImage().getScaledInstance(ratioWidth, labelHeight, Image.SCALE_SMOOTH));
	}
	
}
