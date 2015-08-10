import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Stack;


public class BudgetAssignment {
	public static int numCourses, cMin, cMax, budget,nT;
	public static ArrayList<Integer> fillerCourses;
	public static ArrayList<Order> orderAssignmentFiller;
	public static int numCompleteAssignment = 0;
	public static int numIncompleteAssignment = 0;
	public static int maxSem = 8;

	// 	get all prereq of a given course
	public Stack<Integer> getAllPrereq(int courseID, Hashtable<Integer, Course> courseTable, Stack<Integer> cstack){
		cstack.push(courseID);
		if(courseTable.get(courseID).preReq.size() == 0)
			return cstack;
		for(int k = 0; k < courseTable.get(courseID).preReq.size(); k++){
			getAllPrereq(courseTable.get(courseID).preReq.get(k), courseTable, cstack);
		}
		return cstack;
	}

	// get order/ topological sort of assigning variables
	public ArrayList<Order> getAssignmentOrder(ArrayList<Integer> interestingCourses, Hashtable<Integer, Course> courseTable){
		
		ArrayList<Order> orderAssignment = new ArrayList<Order>();
		
		// add interesting courses which do not have any prerequisites first
		for(int i = 0; i < interestingCourses.size(); i++){
			int course = interestingCourses.get(i);
			if(courseTable.get(course).preReq.size() == 0)
				orderAssignment.add(new Order(course, false));
		}
		
		// add other interesting courses with corresponding prerequisites to the order assignment
		for(int i = 0; i < interestingCourses.size(); i++){
			int course = interestingCourses.get(i);
			
			// add the courses which are not present in orderAssignment only
			if(!orderAssignment.contains(new Order(course))){
				ArrayList<Integer> curPrereq = courseTable.get(course).preReq;
				Stack<Integer> cstack = new Stack<Integer>();
				cstack.push(course);
				
				// get all prerequisites of prerequiste courses
				for(int j = 0; j < curPrereq.size(); j++){
					getAllPrereq(curPrereq.get(j), courseTable, cstack);
				}
				
				// topological sort of the prerequisite courses
				int tempSize = cstack.size();
				for(int l = 0; l < tempSize; l++){
					int cval = cstack.pop();
					if(!orderAssignment.contains(new Order(cval))){
						orderAssignment.add(new Order(cval, false));
					}
				}
			}
		}
		
		
		return orderAssignment;
	}
	
	// check if assignment is complete
	public boolean checkAssignmentComplete(ArrayList<Order> orderAssignment, Hashtable<Integer, Assignment> result, ArrayList<SemOrder> fallOrder, ArrayList<SemOrder> springOrder){
		int totalCourses = orderAssignment.size();
		int count = 0;
		for(int l = 0; l < totalCourses; l++){
			if(orderAssignment.get(l).taken == true)
				count++;
		}
		
		int z = 0;
		for(z = result.size(); z >=1;z--){
			if(result.get(z) != null && result.get(z).totalCredits !=0){
				z--;
				break;
			}
		}
		for(;z>=1;z--){
			if(result.get(z) != null && result.get(z).totalCredits == 0)
				return false;
		}
				
		// check if cMin is satisfied for all the semesters
		boolean isMinCredits = true;
		for(int credits:result.keySet()){
			if(result.get(credits).totalCredits < cMin && result.get(credits).totalCredits != 0){
				isMinCredits = false;
				break;
			}
		}
		
		if(count == totalCourses && isMinCredits)
			return true;
		else
			return false;
	}
			
	// get next course to assign based on the semester
	public int getNextCourse(Hashtable<Integer, Assignment> result, ArrayList<SemOrder> fallOrder, ArrayList<SemOrder> springOrder, int curSem, Hashtable<Integer, Course> courseTable){
		
		int toTake = -1;
		
		// if sem is fall, choose not taken course from fall order
		if(curSem%2 != 0){
			for(int m = 0; m < fallOrder.size(); m++){
				int sIndex = springOrder.indexOf(new SemOrder(fallOrder.get(m).courseID));
				if(!fallOrder.get(m).taken && !springOrder.get(sIndex).taken){
					toTake = fallOrder.get(m).courseID;
					return toTake;
				}
			}
		}else{
			for(int m = 0; m < springOrder.size(); m++){
				int fIndex = fallOrder.indexOf(new SemOrder(springOrder.get(m).courseID));
				if(!springOrder.get(m).taken && !fallOrder.get(fIndex).taken){
					toTake = springOrder.get(m).courseID;
					return toTake;
				}
			}
		}
		
		return toTake;
	}
	
	// check if the course toTake is in same sem as its prerequisite
	public boolean isSameSemPrereq(int toTake, Hashtable<Integer, Assignment> result, int curSem, Hashtable<Integer, Course> courseTable){
		ArrayList<Integer> coursePrereq = courseTable.get(toTake).preReq;
		
		if(result.size() == 0)
			return false;
		
		if(result.get(curSem) == null)
			return false;
		
		ArrayList<Integer> curSemCourses = result.get(curSem).courses;
		int curCourseSize = curSemCourses.size();
		
		for(int n = 0; n < curCourseSize; n++){
			if(coursePrereq.contains(curSemCourses.get(n)))
				return true;
		}
		
		int semCheck = curSem - 1;
		int prereqCount = 0;
		ArrayList<Integer> tempPrereq = courseTable.get(toTake).preReq;
		if(tempPrereq.size() == 0)
			return false;
		while(semCheck > 0){
			ArrayList<Integer> prevSemCourses = new ArrayList<Integer>();
			prevSemCourses = result.get(semCheck).courses;
			for(int j = 0; j < tempPrereq.size(); j++){
				if(prevSemCourses.contains(tempPrereq.get(j))){
					prereqCount++;
				}
			}
			// if all prerequisite are met in the previous semester, then return interesting course
			if(prereqCount == tempPrereq.size()){
				//takenIntersting.add(toTake);
				return false;
			}
			semCheck--;
		}
		return true;
	}
	
	// check if the current assignment exceeds allocated budget
	public boolean budgetExceed(int courseToTake, int curSem, Hashtable<Integer, Assignment> result, Hashtable<Integer, Course> courseTable){
		if(result.size() == 0)
			return false;
		
		int curBudget = 0;
		for(int val: result.keySet())
			curBudget += result.get(val).totalPrice;
		
		int coursePrice = 0;
			
		if(curSem %2 != 0)
			coursePrice = courseTable.get(courseToTake).fallPrice;
		else
			coursePrice = courseTable.get(courseToTake).springPrice;
		
		if(curBudget + coursePrice > budget)
			return true;
		else
			return false;
				
	}
	
	// check if all prereq taken in previous semesters
	public boolean checkPrevPrereq(Hashtable<Integer, Assignment> result,  int courseToTake, int curSem, Hashtable<Integer, Course> courseTable){
		int semCheck = curSem - 1;
		int prereqCount = 0;
		if(courseToTake == -1)
			return false;
		ArrayList<Integer> tempPrereq = new ArrayList<Integer>();
		
		tempPrereq = courseTable.get(courseToTake).preReq;
		
		
		if(result.size() == 0){
			if(tempPrereq.size() == 0)
				return true;
			else
				return false;
		}
		
		if(tempPrereq.size() == 0)
			return true;
		while(semCheck > 0){
			ArrayList<Integer> prevSemCourses = new ArrayList<Integer>();
			if(result.get(semCheck) == null){
				semCheck--;
				continue;
			}
			
			prevSemCourses = result.get(semCheck).courses;
			
			for(int j = 0; j < tempPrereq.size(); j++){
				if(prevSemCourses.contains(tempPrereq.get(j))){
					prereqCount++;
				}
			}
			// if all prerequisite are met in the previous semester, then return interesting course
			if(prereqCount == tempPrereq.size()){
				return true;
			}
			semCheck--;
		}
		return false;
	}
	
	// check if assignment is consistency
	public boolean consistentValue(int courseToTake, int i, Hashtable<Integer, Assignment> result, Hashtable<Integer, Course> courseTable){

		if(result.get(i) == null){
			if(!checkPrevPrereq(result, courseToTake, i, courseTable))
				return false;
			if(budgetExceed(courseToTake, i, result, courseTable))
				return false;
		
			
				return true;
		}
		if(result.get(i).totalCredits >=cMin)
			return false;
		
		if(result.get(i).totalCredits + courseTable.get(courseToTake).creditHours > cMax)
			return false;						
		try{	
		if(isSameSemPrereq(courseToTake, result, i, courseTable))
				return false;
		}catch(Exception e){
			
		}
		
		if(budgetExceed(courseToTake, i, result, courseTable))
			return false;
		
		return true;
	}
			
	// recursive backtracking implementation
	public boolean recursiveBacktrack(Hashtable<Integer, Assignment> result, ArrayList<Order> orderAssignment, Hashtable<Integer, Course> courseTable, ArrayList<Integer> takenSoFar, int curSem, ArrayList<SemOrder> fallOrder, ArrayList<SemOrder> springOrder){
		
		if(checkAssignmentComplete(orderAssignment, result, fallOrder, springOrder)){
			numCompleteAssignment++;
			return true;
		}
			numIncompleteAssignment++;
		
		
		int courseToTake = getNextCourse(result, fallOrder, springOrder, curSem, courseTable);
		if(courseToTake == -1){
			return false;
		}
		
		for(int i = 1; i <= maxSem; i++){
			
			if(!consistentValue(courseToTake, i, result, courseTable)) 
				continue;
			
			// if consistent, Assign the value to result
			if(result.get(i) != null){
				int curCredits = result.get(i).totalCredits;
				int curPrice = result.get(i).totalPrice;
				
				ArrayList<Integer> curCourses = result.get(i).courses;
				
				int newCredits = curCredits + courseTable.get(courseToTake).creditHours;
				curCourses.add(courseToTake);	
				int totalPrice = 0;
				
				// Fall Semester
				if(i % 2 != 0)
					totalPrice = courseTable.get(courseToTake).fallPrice + curPrice;
				else
					totalPrice = courseTable.get(courseToTake).springPrice + curPrice;
				
				// mark course as taken
				if(i % 2 != 0){
					int cIndex = fallOrder.indexOf(new SemOrder(courseToTake));
					if(cIndex != -1)
						fallOrder.get(cIndex).taken = true;
					int orderCourseIndex1 = orderAssignment.indexOf(new Order(courseToTake));
					if(orderCourseIndex1 != -1 && !orderAssignment.get(orderCourseIndex1).taken)
						orderAssignment.get(orderCourseIndex1).taken = true;
				}else{
					int cIndex = springOrder.indexOf(new SemOrder(courseToTake));
					if(cIndex != -1)
						springOrder.get(cIndex).taken = true;
					int orderCourseIndex1 = orderAssignment.indexOf(new Order(courseToTake));
					if(orderCourseIndex1 != -1 && !orderAssignment.get(orderCourseIndex1).taken)
						orderAssignment.get(orderCourseIndex1).taken = true;
				}
				 				
				result.put(i, new Assignment(newCredits, curCourses, totalPrice));
			}else{
				
				int newCredits = courseTable.get(courseToTake).creditHours;
				ArrayList<Integer> curCourses = new ArrayList<Integer>();
				curCourses.add(courseToTake);
				int totalPrice = 0;
				// Fall Semester
				if(i % 2 != 0)
					totalPrice = courseTable.get(courseToTake).fallPrice;
				else
					totalPrice = courseTable.get(courseToTake).springPrice;
				
				// mark course as taken
				if(i % 2 != 0){
					int cIndex = fallOrder.indexOf(new SemOrder(courseToTake));
					if(cIndex != -1)
						fallOrder.get(cIndex).taken = true;
					int orderCourseIndex1 = orderAssignment.indexOf(new Order(courseToTake));
					if(orderCourseIndex1 != -1 && !orderAssignment.get(orderCourseIndex1).taken)
						orderAssignment.get(orderCourseIndex1).taken = true;
				}else{
					int cIndex = springOrder.indexOf(new SemOrder(courseToTake));
					if(cIndex != -1)
						springOrder.get(cIndex).taken = true;
					int orderCourseIndex1 = orderAssignment.indexOf(new Order(courseToTake));
					if(orderCourseIndex1 != -1 && !orderAssignment.get(orderCourseIndex1).taken)
						orderAssignment.get(orderCourseIndex1).taken = true;
				}
				
				result.put(i, new Assignment(newCredits, curCourses, totalPrice));
			}
			
			// if result is failure, remove previous assignment
			if(recursiveBacktrack(result,  orderAssignment, courseTable, takenSoFar, i, fallOrder, springOrder)){
				return true;
			}else{
				// mark course as not taken
				if(i % 2 != 0){
					int cIndex = fallOrder.indexOf(new SemOrder(courseToTake));
					if(cIndex != -1)
						fallOrder.get(cIndex).taken = false;
					int orderCourseIndex1 = orderAssignment.indexOf(new Order(courseToTake));
					if(orderCourseIndex1 != -1)
						orderAssignment.get(orderCourseIndex1).taken = false;
				}else{
					int cIndex = springOrder.indexOf(new SemOrder(courseToTake));
					if(cIndex != -1)
						springOrder.get(cIndex).taken = false;
					int orderCourseIndex1 = orderAssignment.indexOf(new Order(courseToTake));
					if(orderCourseIndex1 != -1)
						orderAssignment.get(orderCourseIndex1).taken = false;
				}
				
				int courseToRemoveIndex = result.get(i).courses.indexOf(courseToTake);
				if(courseToRemoveIndex != -1){
					result.get(i).courses.remove(courseToRemoveIndex);
					result.get(i).totalCredits -= courseTable.get(courseToTake).creditHours;
					
					// remove price for that course from result table
					if(i % 2 != 0){
						result.get(i).totalPrice -= courseTable.get(courseToTake).fallPrice;
					}else{
						result.get(i).totalPrice -= courseTable.get(courseToTake).springPrice;
					}
				}
			}
			
		}
		
		// remove already assigned value for backtracking
		int fallOrderIndex = fallOrder.indexOf(new SemOrder(courseToTake));
		int springOrderIndex = springOrder.indexOf(new SemOrder(courseToTake));
		
		if(fallOrderIndex != -1 && fallOrder.get(fallOrderIndex).taken)
			fallOrder.get(fallOrderIndex).taken = false;
		if(springOrderIndex != -1 && springOrder.get(springOrderIndex).taken)
			springOrder.get(springOrderIndex).taken = false;
		
		int orderCourseIndex1 = orderAssignment.indexOf(new Order(courseToTake));
		if(orderCourseIndex1 != -1)
			orderAssignment.get(orderCourseIndex1).taken = false;
		
		return false;
	}
			
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		BudgetAssignment slns_b = new BudgetAssignment();
		
		String[] files = {"firstScenario.txt", "secondScenario.txt", "thirdScenario.txt", "fourthScenario.txt"};
//		BufferedReader br = new BufferedReader(new FileReader(new File(files[3])));
		BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
		String[] words = br.readLine().split(" ");
		
		// read number of courses, cMin and cMax
		numCourses = Integer.parseInt(words[0]);
		cMin = Integer.parseInt(words[1]);
		cMax = Integer.parseInt(words[2]);
		System.out.println("numCourses = "+numCourses+", cMin = "+cMin+", cMax = "+cMax);
		
		// hashtable to store course information
		Hashtable<Integer, Course> courseTable = new Hashtable<Integer, Course>();
		for(int i = 1; i <= numCourses; i++){
			String[] contents = br.readLine().split(" ");
			courseTable.put(i, new Course(Integer.parseInt(contents[0]), Integer.parseInt(contents[1]), Integer.parseInt(contents[2])));			
		}
		
		// maintain an arrayList of prerequiste courses, this will help to find only filler courses
		ArrayList<Integer> prereqCourses = new ArrayList<Integer>();
				
		for(int i = 1; i <= numCourses; i++){
			String[] contents = br.readLine().split(" ");
			if(Integer.parseInt(contents[0]) != 0){
				Course tempCourseVal = courseTable.get(i);
				ArrayList<Integer> tempPrereq = new ArrayList<Integer>();
				
				for(int j = 1; j < contents.length; j++){
					tempPrereq.add(Integer.parseInt(contents[j]));
					
					if(!prereqCourses.contains(Integer.parseInt(contents[j])))
						prereqCourses.add(Integer.parseInt(contents[j]));
				}
				tempCourseVal.preReq = tempPrereq;
				
				// add course with prereq back to the hashtable
				courseTable.put(i, tempCourseVal);
			}
		}
		
			
		// get interesting courses info
		String[] contents = br.readLine().split(" ");
		ArrayList<Integer> interestingCourses = new ArrayList<Integer>();
		for(int i = 1 ; i < contents.length; i++){
			interestingCourses.add(Integer.parseInt(contents[i]));
		}
		
		for(int k: interestingCourses){		
			if(prereqCourses.contains(k)){
				prereqCourses.remove(prereqCourses.indexOf(k));
			}
		}
				
		budget = Integer.parseInt(br.readLine());
		br.close();
		

		// get filler course details
		fillerCourses = new ArrayList<Integer>();

		// get the topological order of assigning variables
		ArrayList<Order> orderAssignment = slns_b.getAssignmentOrder(interestingCourses, courseTable);
		System.out.println("Ordered Assignment");
		for(Order O: orderAssignment)
			System.out.print(O.courseID+" ");
		System.out.println("size is "+orderAssignment.size());
		for(int i = 1; i <= numCourses; i++){
			if(!orderAssignment.contains(new Order(i))){
				fillerCourses.add(i);
			}
		}
		
		// get the topological order of assigning Filler Courses
				orderAssignmentFiller = slns_b.getAssignmentOrder(fillerCourses, courseTable);
				
				for(int i = 1; i <= numCourses; i++){
					if(orderAssignment.contains(new Order(i)) && orderAssignmentFiller.contains(new Order(i))){
						int pindex = orderAssignmentFiller.indexOf(new Order(i));
						orderAssignmentFiller.remove(pindex);
					}
				}
				System.out.println("Ordered Filler Courses");
				for(Order O: orderAssignmentFiller)
					System.out.print(O.courseID+" ");
				System.out.println("size is "+orderAssignmentFiller.size());
				
		// get fall and spring semOrder
		ArrayList<SemOrder> fallOrder = new ArrayList<SemOrder>();
		ArrayList<SemOrder> springOrder = new ArrayList<SemOrder>();
		
		ArrayList<SemOrder> courseIncreasingPriceFall = new ArrayList<SemOrder>();
		for(Order O: orderAssignment){
			courseIncreasingPriceFall.add(new SemOrder(O.courseID, courseTable.get(O.courseID).fallPrice, false));
		}
		Collections.sort(courseIncreasingPriceFall);
		
		// sort fall filler courses
		ArrayList<SemOrder> fillerCourseIncreasingPriceFall = new ArrayList<SemOrder>();
		for(int i:fillerCourses)
			fillerCourseIncreasingPriceFall.add(new SemOrder(i, courseTable.get(i).fallPrice, false));
		Collections.sort(fillerCourseIncreasingPriceFall);
		
		for(Order s: orderAssignment)
			fallOrder.add(new SemOrder(s.courseID, courseTable.get(s.courseID).fallPrice, false));
		for(Order s: orderAssignmentFiller)
			fallOrder.add(new SemOrder(s.courseID, courseTable.get(s.courseID).fallPrice, false));
		
		// get spring course order
		ArrayList<SemOrder> courseIncreasingPriceSpring = new ArrayList<SemOrder>();
		for(Order O: orderAssignment){
			courseIncreasingPriceSpring.add(new SemOrder(O.courseID, courseTable.get(O.courseID).springPrice, false));
		}
		Collections.sort(courseIncreasingPriceSpring);
		
		// sort spring filler courses
		ArrayList<SemOrder> fillerCourseIncreasingPriceSpring = new ArrayList<SemOrder>();
		for(int i:fillerCourses)
			fillerCourseIncreasingPriceSpring.add(new SemOrder(i, courseTable.get(i).springPrice, false));
		Collections.sort(fillerCourseIncreasingPriceSpring);
		for(Order s: orderAssignment)
			springOrder.add(new SemOrder(s.courseID, courseTable.get(s.courseID).springPrice, false));
		for(Order s: orderAssignmentFiller)
			springOrder.add(new SemOrder(s.courseID, courseTable.get(s.courseID).fallPrice, false));
		
		System.out.println("Recursive Bactracking Start");
		
		Hashtable<Integer, Assignment> result = new Hashtable<Integer, Assignment>();
		ArrayList<Integer> takenSoFar = new ArrayList<Integer>();
		int curSem = 1;
		if(!slns_b.recursiveBacktrack(result, orderAssignment, courseTable, takenSoFar, curSem, fallOrder, springOrder)){
			System.out.println("NO SOLUTION FOUND ");
			for(int val: result.keySet())
				System.out.println(val+" "+result.get(val).courses+" Total Credits "+result.get(val).totalCredits+" Total Price "+result.get(val).totalPrice);
			System.exit(0);
		}
//		System.out.println("Final Result is");
//		for(int val: result.keySet())
//			System.out.println(val+" "+result.get(val).courses+" Total Credits "+result.get(val).totalCredits+" Total Price "+result.get(val).totalPrice);
		int bb = 0;
		for(int val: result.keySet())
			bb += result.get(val).totalPrice;
//		System.out.println("Total Budget is "+bb);

		System.out.println("Final Output");
		int numSems = 0;
		for(int i = 1; i <= result.size(); i++){
			if(result.get(i).totalCredits == 0)
				break;
			numSems++;
		}
		System.out.println(bb+" "+numSems);
		for(int i = 1; i <= numSems; i++){
			int numCourses = result.get(i).courses.size();
			System.out.print(numCourses+" ");
			for(int j = 0; j < numCourses; j++){
				System.out.print(result.get(i).courses.get(j)+" ");
			}
			System.out.println();
		}
		for(int i = 1; i <=numSems;i++){
			System.out.print(result.get(i).totalPrice+" ");
		}
		System.out.println();
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Number of Complete Assignments "+numCompleteAssignment);
		System.out.println("Number of Incomplete Assignments "+numIncompleteAssignment);
		System.out.println("Total time taken in milli seconds "+totalTime);
	}
}
