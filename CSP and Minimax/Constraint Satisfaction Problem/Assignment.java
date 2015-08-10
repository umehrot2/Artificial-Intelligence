import java.util.ArrayList;


public class Assignment {
	int totalCredits;
	ArrayList<Integer> courses = new ArrayList<Integer>();
	int totalPrice;
	
	Assignment(int totalCredits, ArrayList<Integer> courseInfo){
		this.totalCredits = totalCredits;
		this.courses = courseInfo;
	}

	Assignment(int totalCredits, ArrayList<Integer> courseInfo, int totalPrice){
		this.totalCredits = totalCredits;
		this.courses = courseInfo;
		this.totalPrice = totalPrice;
	}
}
