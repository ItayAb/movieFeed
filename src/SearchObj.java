
public class SearchObj {

	public String Title ;
	public String Year; 
	public String Plot;
	public String ReturnType;
	public String ImdbId;
	private MyEnums.ImdbSearcherEnum searchBy;

	public SearchObj(String searchIdentifier, MyEnums.ImdbSearcherEnum searchBy)
	{
		if (searchBy == null) {
			this.searchBy = MyEnums.ImdbSearcherEnum.Title;
		}else {
			this.searchBy = searchBy;
		}
		
		if (searchBy == MyEnums.ImdbSearcherEnum.Title) {
			Title = searchIdentifier.trim();			
		} else {
			ImdbId = searchIdentifier.trim(); 
		}
			
	}

	public SearchObj(String title, String year, String plot, String returnType)
	{        
		this.Title = title;
		this.Year = year;
		this.Plot = plot;
		this.ReturnType = returnType;
	}

	
	public String CreateUrlSearchParam()
	{
		System.out.println("search is " + searchBy);
		if (searchBy == MyEnums.ImdbSearcherEnum.Title) {
			return "?t=" +this.Title.replace(" ", "%20") + "+&y="+ this.Year +"&plot=full&r=json";			
		} else{
			return "?i=" + this.ImdbId + "&plot=full"; 
		}
	}
}
