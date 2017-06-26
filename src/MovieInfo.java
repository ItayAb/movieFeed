//import Gson.Gson;
import com.google.*;



public class MovieInfo {


	public String Title;
	public String Year;
	public String Rated ;
	public String Runtime ;
	public String Genre ;
	public String Director; 
	public String Writer; 
	public String Actors ;
	public String Plot; 
	public String Language ;
	public String Country; 
	public String Awards; 
	public String Poster; 
	public String Metascore; 
	public String imdbRating ;
	public String imdbVotes; 
	public String imdbID; 
	public String Response; 

	
	
	public MovieInfo(String json){
		
	}
	
	public static MovieInfo CreateMovieInfo(String json){		
		return new com.google.gson.Gson().fromJson(json, MovieInfo.class);
	}
	
	public String toString(){
		return String.format("%s %s", this.Title, this.imdbRating);
	}
	
	public static boolean IsValid(MovieInfo movieInfo){
		if (movieInfo == null) {
			return false;
		}
		if(movieInfo.Title == null){
			return false;
		}
		if (movieInfo.imdbRating == null) {
			return false;
		}
		if (movieInfo.Poster == null) {
			return false;
		}
		if (movieInfo.Year == "" || movieInfo.Year == null) {
			movieInfo.Year = "1970";
		}
		
		return true;
	}

}
