import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.StyledEditorKit.BoldAction;

import MyInterfaces.IFetcher;


public class IceFilmsFetcher implements IFetcher{

	
	private HashMap<String, Boolean> GetMovies(){
		  String url = "http://www.icefilms.info/";
          String page = WebPageDownloader.GetWebPageJsoup(url);
          return this.extractNames(page);
	}
	
	private HashMap<String, Boolean> extractNames(String rawPage){
		HashMap<String, Boolean> results = new HashMap<String, Boolean>();      
        String patternForEntireSection = "<span class=\"altTitle\">Movies</span>(.*?)<span class=\"releasesTitle\">Latest Releases</span>";
        Pattern r = Pattern.compile(patternForEntireSection);
        
        Matcher m = r.matcher(rawPage);        
        if (m.find()) {			        	
            String patternMovieNames = "(<li>(<a href=(.*?)>(.*?)\\s*?(\\(\\d+\\))?</a>)\\s*?(<b.*?>(HD)</b>)*?</li>)"; 
            Pattern re = Pattern.compile(patternMovieNames);
            
            Matcher movieMatches = re.matcher(m.group(0));
            while (movieMatches.find()) {
            	Boolean isHd = false;
            	if(movieMatches.group(6) != null && !movieMatches.group(6).isEmpty()) {
            		isHd = true;
            	}
            	results.put(movieMatches.group(4), isHd);
			}               	      
		}

        return results;	
	}

	@Override
	public HashMap<String, Boolean> Fetch() {
		// TODO Auto-generated method stub
		return GetMovies();
	}

}
