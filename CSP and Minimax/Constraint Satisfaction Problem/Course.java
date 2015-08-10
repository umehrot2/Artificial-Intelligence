import java.util.ArrayList;


public class Course {
	int fallPrice;
	int springPrice;
	int creditHours;
	ArrayList<Integer> preReq = new ArrayList<Integer>();
	
	Course(int fallPrice, int springPrice, int creditHours){
		this.fallPrice = fallPrice;
		this.springPrice = springPrice;
		this.creditHours = creditHours;
	}
}
