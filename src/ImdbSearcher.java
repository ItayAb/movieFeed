import java.awt.List;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import MyInterfaces.IMovieFinder;
import MyInterfaces.INotifiable;


public class ImdbSearcher implements IMovieFinder<MovieInfo>{

	private final String OMDB_API_ADDRESS = "http://www.omdbapi.com/";	
	public INotifiable Notify;
	private boolean cancel;
	
	public ImdbSearcher(){
		
	}
	
	public ImdbSearcher(INotifiable notify){
		this.Notify = notify;
	}
	
	public  ArrayList<MovieInfo> FindMovies(HashMap<String, Boolean> movies, boolean onlyHD, MyEnums.ImdbSearcherEnum searchBy)
	{
		ArrayList<MovieInfo> result = new ArrayList<MovieInfo>();		
		for (String key : movies.keySet()) {
			if (cancel) {
				cancel = false;
				return null;
			}
			if (onlyHD && !movies.get(key)) {
				continue;
			}
			System.out.println("title is " + key);
			SearchObj so;
			if (key.startsWith("tt")) {
				so = new SearchObj(key, MyEnums.ImdbSearcherEnum.ImdbID);			
			} else{
				so = new SearchObj(key, searchBy);
			}
			
			String param = so.CreateUrlSearchParam();
			String json = WebPageDownloader.GetWebPageJsoup(OMDB_API_ADDRESS + param);
			System.out.println("getting " +  OMDB_API_ADDRESS + param);
			MovieInfo mi = MovieInfo.CreateMovieInfo(json);
			result.add(mi);
			if (Notify != null) {
				Notify.Nofity(MovieFetcherEnum.Single_Movie ,mi);
			}
		}		
		
		if (Notify != null) {
			Notify.Nofity(MovieFetcherEnum.Search_Complete ,null);
		}
		
		return result;
	}
	
	public void StopSearch(){
		cancel = true;
	}
}
