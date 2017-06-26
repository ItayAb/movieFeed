package MyInterfaces;

import java.util.ArrayList;
import java.util.HashMap;

public interface IMovieFinder<T> {
	ArrayList<T> FindMovies(HashMap<String, Boolean> movies, boolean onlyHD, MyEnums.ImdbSearcherEnum searchBy);
}
