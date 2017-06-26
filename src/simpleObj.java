import java.awt.List;

import javax.swing.ListModel;


public class simpleObj {

	
	public List list;
	private int counter;
	public simpleObj(){
		list = new List();
		counter = 0;
	}
	
	
	public String action(){
		counter++;
		return "Pressed " + counter + " times";
	}
}
