import java.util.ArrayList;
import java.util.HashMap;

import MyInterfaces.IFetcher;


public class MovieFetcher implements IFetcher{

	MyInterfaces.IMovieFinder<MovieInfo> searcher;
	MyInterfaces.IFetcher movieGetter;
	
	public MovieFetcher() {
		// TODO Auto-generated constructor stub
		searcher = new ImdbSearcher();
		movieGetter = new IceFilmsFetcher();
	}
	
	public ArrayList<MovieInfo> fetchMovies(){
		
		return searcher.FindMovies(movieGetter.Fetch(), true, MyEnums.ImdbSearcherEnum.Title);
	}

	@Override
	public HashMap<String, Boolean> Fetch() {
		// TODO Auto-generated method stub
		return movieGetter.Fetch();
	}

}
