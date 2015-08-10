import java.util.ArrayList;

public class SortedCourseOrder implements Comparable<SortedCourseOrder>{

	int totalCredits;
	int totalCost;
	ArrayList<Integer> courses;
	
	SortedCourseOrder(int totalCredits, int totalCost, ArrayList<Integer> subjects ){
		this.totalCredits = totalCredits;
		this.totalCost = totalCost;
		this.courses = new ArrayList<Integer>(subjects);
	}
	
	@Override
	public int compareTo(SortedCourseOrder o) {
		
		return this.totalCost - o.totalCost;
	}
	
}