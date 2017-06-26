import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class WebPageDownloader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static String GetWebPageJsoup(String pageUrl){
		URL url;
		StringBuilder result = new StringBuilder();
		try {
			url = new URL(pageUrl);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent",
			        "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
			conn.connect();
			BufferedReader serverResponse = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line  = serverResponse.readLine()) != null) {
				result.append(line);				
			}			
			serverResponse.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error");
			System.out.println(e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error");
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}

		return result.toString();
	}
	
	
	public static String GetWebPage(String url){
	    URL webUrl;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    StringBuilder result = new StringBuilder();	    
        //web.UserAgent = @"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36";
        //request.Accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	    try {
	    	webUrl = new URL(url);
	        is = webUrl.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));

	        while ((line = br.readLine()) != null) {
	            System.out.println(line);
	            result.append(line);
	        }
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	            // nothing to see here
	        }
	    }
		
		return result.toString();
	}

}
