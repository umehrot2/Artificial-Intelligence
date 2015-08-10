import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Stack;

public class OptimalAssignment {
	public static int numCourses, cMin, cMax, budget,nT;
	public static ArrayList<Integer> fillerCourses;
	public static ArrayList<Order> orderAssignmentFiller;
	public static ArrayList<ArrayList<Integer>> courseCombination;
	public static boolean isDone = false;
	public static int minAssignmentValue = Integer.MAX_VALUE;
	public static int numCompleteAssignment = 0;
	public static int numIncompleteAssignment = 0;
	public static int time1 = 0;
	public static Hashtable<Integer, Assignment> optimalAssignemnt;
	public static Hashtable<Integer, ArrayList<Integer>> finResult;
	
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
	public boolean checkAssignmentComplete1(ArrayList<Order> orderAssignment, Hashtable<Integer, Assignment> result){
		int totalCourses = orderAssignment.size();
		int count = 0;
		for(int l = 0; l < totalCourses; l++){
			if(orderAssignment.get(l).taken == true)
				count++;
		}
		
		// ensure there are no blank sem assignments
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
			
		
	// check if all prereq taken in previous semesters
	public boolean checkPrevPrereq(Hashtable<Integer, Assignment> result,  int courseToTake, int curSem, Hashtable<Integer, Course> courseTable){
		int semCheck = curSem;
		int prereqCount = 0;
		if(courseToTake == -1)
			return false;
		ArrayList<Integer> tempPrereq = new ArrayList<Integer>();
		try{
		tempPrereq = courseTable.get(courseToTake).preReq;
		}catch(Exception e){
			System.out.println("Exception here in checkprevpreq");
		}
		
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
			try{
			prevSemCourses = result.get(semCheck).courses;
			}catch(Exception e){
				System.out.println("Here exception "+semCheck);
			}
			for(int j = 0; j < tempPrereq.size(); j++){
				if(prevSemCourses.contains(tempPrereq.get(j))){
					prereqCount++;
				}
			}
			
			// if all prerequisite are met in the previous semester, then return the course
			if(prereqCount == tempPrereq.size()){
				return true;
			}
			semCheck--;
		}
		return false;
	}
	
	
	// get all combinations of valid courses sorted by price for optimal assignment
	public boolean isSame(ArrayList<Integer> a1, ArrayList<Integer> a2, int entrySize, int totCreds, Hashtable<Integer, Course> courseTable){
		for(int z = 0; z < entrySize; z++){
			if(a1.get(z) != a2.get(z))
				return false;			
		}
		
		// check if the new course added will be between cmin and cmax
		int ncourse = a2.get(entrySize);
		
		if((totCreds + courseTable.get(ncourse).creditHours >= cMin) && (totCreds + courseTable.get(ncourse).creditHours <= cMax))
			return true;
		else
			return false;
	}

		
	// check if 2 arrays are equal upto size-1
	public  boolean isSame1(ArrayList<Integer> a1, ArrayList<Integer> a2, int entrySize){
		for(int z = 0; z < entrySize; z++){
			if(a1.get(z) != a2.get(z))
				return false;			
		}
		return true;
	}
		
	// create all combinations of course to take in that semester
	public  void createCombination1(ArrayList<Integer> a, ArrayList<ArrayList<Integer>> rl, ArrayList<ArrayList<Integer>> temp, int k){
		
		if(isDone)
			return;
		
		if(k == 1){
			isDone = true;
		}
		
		if(k == a.size()){
			temp = new ArrayList<ArrayList<Integer>>();
			for(int i = 0; i < k; i++){
				for(int j = i+1; j < k; j++){
					ArrayList<Integer> tempRL = new ArrayList<Integer>();
					tempRL.add(a.get(i));
					tempRL.add(a.get(j));
					rl.add(tempRL);
					temp.add(tempRL);
				}
			}
			k--;
			createCombination1(a, rl, temp, k);
		}
		
		int size = temp.size() - 1;
		
		if(size <= 0){
			courseCombination = new ArrayList<ArrayList<Integer>>(rl);
			isDone = true;
			return;
		}
		int entrySize = temp.get(0).size() - 1;
		ArrayList<ArrayList<Integer>> ntemp = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < size; i++){
			for(int j = i+1; j < size; j++){
				if(isSame1(temp.get(i), temp.get(j), entrySize)){
					ArrayList<Integer> intrl = new ArrayList<Integer>(temp.get(i));
					intrl.add(temp.get(j).get(entrySize));
					ntemp.add(intrl);
					rl.add(intrl);
				}else{
					break;
				}
			}
		}
		k--;
		createCombination1(a, rl, ntemp, k);
	}
		
		
	// check atleast one interesting course is present in the course combination
	public boolean atleastOneInteresting(ArrayList<Integer> interList, ArrayList<Order> orderAssignment){
		for(int mk = 0; mk <interList.size(); mk++){
				if(orderAssignment.contains(new Order(interList.get(mk))))
					return true;
			}
		return false;
	}
		
	// get all course combination which satisfies cmin and cmax constratint for optimal assignment
	public void getFinalCourseCombo1(ArrayList<SortedCourseOrder> sortedCourseOrder, ArrayList<ArrayList<Integer>> courseCombination, int curSem1, int lastSem, Hashtable<Integer, Course> courseTable, Hashtable<Integer, Assignment> result, ArrayList<Order> orderAssignment){
		int curSem = curSem1;
		for(int ml = 0; ml < courseCombination.size(); ml++){
			int tsize = courseCombination.get(ml).size();
			ArrayList<Integer> interList = courseCombination.get(ml);
			int curCredits = 0;
			int totalCost = 0;
			for(int nl = 0; nl < tsize; nl++){
				curCredits += (courseTable.get(interList.get(nl)).creditHours);
			}
			
			// if cmin, cmax and the course combination has atleast one assignment add it to the combination result
			if(curCredits >= cMin && curCredits <= cMax && atleastOneInteresting(interList, orderAssignment)){
				if(curSem % 2 != 0){
					for(int nl = 0; nl < tsize; nl++){
						totalCost += (courseTable.get(interList.get(nl)).fallPrice);
					}
				}else{
					for(int nl = 0; nl < tsize; nl++){
						totalCost += (courseTable.get(interList.get(nl)).springPrice);
					}	
				}
				
				sortedCourseOrder.add(new SortedCourseOrder(curCredits, totalCost, interList));
			}
			
		}
	}
	
	// get next course order for optimal assignment
	public ArrayList<SortedCourseOrder> getNextCourseOrder1(Hashtable<Integer, Assignment> result, ArrayList<SemOrder> courseOrderToTake, int curSem, int lastSem, Hashtable<Integer, Course> courseTable,  ArrayList<Order> orderAssignment){
		
		ArrayList<Integer> listOfCourses = new ArrayList<Integer>();
		for(int m = 0; m < courseOrderToTake.size(); m++){
			int course = courseOrderToTake.get(m).courseID;
			
			// check if course does not have any prerequisite or all prerequisite are met in previous semesters
			if(!courseOrderToTake.get(m).taken){
				if(courseTable.get(course).preReq.size() == 0 || checkPrevPrereq(result, course, curSem, courseTable)){
					listOfCourses.add(course);
				}
			}
		}					
		
		ArrayList<ArrayList<Integer>> rl = new ArrayList<ArrayList<Integer>>();
		
		for(int mn = 0; mn < listOfCourses.size(); mn++){
			ArrayList<Integer> trl = new ArrayList<Integer>();
			trl.add(listOfCourses.get(mn));
			rl.add(trl);
		}
		
		ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
		int kk = listOfCourses.size(); 
		
		// get combinations of courses sorted by price for this semester
		isDone = false;
		courseCombination = new ArrayList<ArrayList<Integer>>();
		createCombination1(listOfCourses, rl, temp, kk);
		if(courseCombination == null){
			System.out.println("ERROR !! Null received in courseCombo");
			System.exit(0);
		}
		
		
		ArrayList<SortedCourseOrder> sortedCourseOrder = new ArrayList<SortedCourseOrder>();
		getFinalCourseCombo1(sortedCourseOrder, courseCombination, curSem, lastSem, courseTable, result,orderAssignment);
		Collections.sort(sortedCourseOrder);
		
		return sortedCourseOrder;
	}
	
	// new consistent value check for optimal assignment
	public boolean consistentValue1(int i, Hashtable<Integer, Assignment> result){
		if(result.get(i) == null)
			return true;
		if(result.get(i).totalCredits >= cMin)
			return false;
		
		return true;
	}
	
	
	// Outer Function to call for first set of possible assignment combinations
	public void outerFunc(Hashtable<Integer, Assignment> result, ArrayList<SemOrder> courseOrderToTake, Hashtable<Integer, Course> courseTable,ArrayList<Order> orderAssignment ){
		int semNo = 1;
		int lastSem = 0;
		
		finResult = new Hashtable<Integer, ArrayList<Integer>>();
		
		// get all the combinations of courses that can be taken in given semester
		ArrayList<SortedCourseOrder> sortedCourseInfo = new ArrayList<SortedCourseOrder>(getNextCourseOrder1(result, courseOrderToTake, semNo, lastSem, courseTable, orderAssignment));
		
		// sort the combinations according to the total price for that semester
		Collections.sort(sortedCourseInfo);
		
		// for each of the sorted course combination, find the assignment using Recursive Backtrack and keep track of minimum Budget so far
		for(SortedCourseOrder sco: sortedCourseInfo){
			
			// mark courses as taken
			for(int ij = 0; ij < sco.courses.size(); ij++){
				int orderToTakeIndex = courseOrderToTake.indexOf(new SemOrder(sco.courses.get(ij)));
				int orderAssignmentIndex = orderAssignment.indexOf(new Order(sco.courses.get(ij)));
				if(orderToTakeIndex == -1){
					System.out.println("Error finding orderToTakeIndex");
					System.exit(0);
				}				
				courseOrderToTake.get(orderToTakeIndex).taken = true;
				
				// mark required course as taken if it is in courseCombination
				if(orderAssignmentIndex != -1)
					orderAssignment.get(orderAssignmentIndex).taken = true;
				
			}
			
			result.put(semNo, new Assignment(sco.totalCredits, sco.courses, sco.totalCost));
			
			// prune inner if head node cost is greater than previous assignment
			
			int bb = 0;
			for(int val: result.keySet()){
				if(result.get(val).totalCredits != 0)
					bb += result.get(val).totalPrice;
			}
			
			if(bb > minAssignmentValue) {
				result.remove(semNo);
				
				// mark courses as not taken
				for(int ij = 0; ij < sco.courses.size(); ij++){
					int orderToTakeIndex = courseOrderToTake.indexOf(new SemOrder(sco.courses.get(ij)));
					int orderAssignmentIndex = orderAssignment.indexOf(new Order(sco.courses.get(ij)));
					if(orderToTakeIndex == -1){
						System.out.println("Error finding orderToTakeIndex");
						System.exit(0);
					}				
					courseOrderToTake.get(orderToTakeIndex).taken = false;
					
					// mark required course as taken if it is in courseCombination
					if(orderAssignmentIndex != -1)
						orderAssignment.get(orderAssignmentIndex).taken = false;
					
				}
				break;				
			}
			
			// prune inner if head node cost is greater than prev assignment
			
			// call recursive backtrack for initial course combination assignment to find all possible assignments
			innerRecurse(result, orderAssignment, ++semNo, courseTable, courseOrderToTake);
			
			result.remove(semNo);
			semNo--;
			
			// After recursion returns, mark courses as not taken
			for(int ij = 0; ij < sco.courses.size(); ij++){
				int orderToTakeIndex = courseOrderToTake.indexOf(new SemOrder(sco.courses.get(ij)));
				int orderAssignmentIndex = orderAssignment.indexOf(new Order(sco.courses.get(ij)));
				if(orderToTakeIndex == -1){
					System.out.println("Error finding orderToTakeIndex");
					System.exit(0);
				}				
				courseOrderToTake.get(orderToTakeIndex).taken = false;
				
				// mark required course as taken if it is in courseCombination
				if(orderAssignmentIndex != -1)
					orderAssignment.get(orderAssignmentIndex).taken = false;
				
			}
		}
		
	}
	
	// copy intermediate min budget result to finalResult
	public void copyAssignment(Hashtable<Integer, Assignment> result){
		finResult = new Hashtable<Integer, ArrayList<Integer>>();
		for(int sh : result.keySet()){
			finResult.put(sh, new ArrayList<Integer>(result.get(sh).courses));
		}
	}
	
	//inner recursive backtrack	 
	public void innerRecurse(Hashtable<Integer, Assignment> result, ArrayList<Order> orderAssignment, int semNo, Hashtable<Integer, Course> courseTable, ArrayList<SemOrder> courseOrderToTake){
		
		// check if the assignment so far is complete
		if(checkAssignmentComplete1(orderAssignment, result)){
			numCompleteAssignment++;
			int bb = 0;
			for(int val: result.keySet()){
				if(result.get(val).totalCredits != 0)
					bb += result.get(val).totalPrice;
			}
			if(bb < minAssignmentValue){
				minAssignmentValue = bb;
				copyAssignment( result);
			}
		}else{
			numIncompleteAssignment++;
		}
		int lastSem = 0;
		ArrayList<SortedCourseOrder> sortedCourseInfo = new ArrayList<SortedCourseOrder>(getNextCourseOrder1(result, courseOrderToTake, semNo, lastSem, courseTable, orderAssignment));
		for(SortedCourseOrder sco: sortedCourseInfo){
			
			
			// mark courses as taken
			for(int ij = 0; ij < sco.courses.size(); ij++){
				int orderToTakeIndex = courseOrderToTake.indexOf(new SemOrder(sco.courses.get(ij)));
				int orderAssignmentIndex = orderAssignment.indexOf(new Order(sco.courses.get(ij)));
				if(orderToTakeIndex == -1){
					System.out.println("Error finding orderToTakeIndex");
					System.exit(0);
				}				
				courseOrderToTake.get(orderToTakeIndex).taken = true;
				
				// mark required course as taken if it is in courseCombination
				if(orderAssignmentIndex != -1)
					orderAssignment.get(orderAssignmentIndex).taken = true;
				
			}
			
			result.put(semNo, new Assignment(sco.totalCredits, sco.courses, sco.totalCost));
			
			int bb = 0;
			for(int val: result.keySet()){
				if(result.get(val).totalCredits != 0)
					bb += result.get(val).totalPrice;
			}
			
			// check if budget so far exceeds minimum budget and prune out
			if(bb > minAssignmentValue) {
				result.remove(semNo);
				
				// mark courses as not taken
				for(int ij = 0; ij < sco.courses.size(); ij++){
					int orderToTakeIndex = courseOrderToTake.indexOf(new SemOrder(sco.courses.get(ij)));
					int orderAssignmentIndex = orderAssignment.indexOf(new Order(sco.courses.get(ij)));
					if(orderToTakeIndex == -1){
						System.out.println("Error finding orderToTakeIndex");
						System.exit(0);
					}				
					courseOrderToTake.get(orderToTakeIndex).taken = false;
					
					// mark required course as taken if it is in courseCombination
					if(orderAssignmentIndex != -1)
						orderAssignment.get(orderAssignmentIndex).taken = false;
					
				}
				break;				
			}

			
			innerRecurse(result, orderAssignment, ++semNo, courseTable, courseOrderToTake);
			
			result.remove(semNo);
			semNo--;
			
			// mark courses as not taken
			for(int ij = 0; ij < sco.courses.size(); ij++){
				int orderToTakeIndex = courseOrderToTake.indexOf(new SemOrder(sco.courses.get(ij)));
				int orderAssignmentIndex = orderAssignment.indexOf(new Order(sco.courses.get(ij)));
				if(orderToTakeIndex == -1){
					System.out.println("Error finding orderToTakeIndex");
					System.exit(0);
				}				
				courseOrderToTake.get(orderToTakeIndex).taken = false;
				
				// mark required course as taken if it is in courseCombination
				if(orderAssignmentIndex != -1)
					orderAssignment.get(orderAssignmentIndex).taken = false;
				
			}
			
		}
		
		
	}
	
	// print final result to the console
	public void printFinalResult(Hashtable<Integer, Course> courseTable){
		System.out.println(minAssignmentValue+" "+finResult.size());
		for(int i = 1; i <= finResult.size(); i++){
			System.out.print(finResult.get(i).size()+" ");
			for(int j: finResult.get(i))
				System.out.print(j+" ");
			System.out.println();
		}
		
		// calculate budget for each sem
		for(int i = 1; i <= finResult.size(); i++){
			int total = 0;
			
			// if sem is fall
			if(i % 2 != 0){
				for(int j : finResult.get(i))
					total += courseTable.get(j).fallPrice;
				System.out.print(total+" ");
			}else{
				for(int j : finResult.get(i))
					total += courseTable.get(j).springPrice;
				System.out.print(total+" ");
			}
		}
		System.out.println();
	}

			
	public static void main(String[] args) throws IOException {
		OptimalAssignment slns_b = new OptimalAssignment();
		long startTime   = System.currentTimeMillis();
		String[] files = {"firstScenario.txt", "secondScenario.txt", "thirdScenario.txt", "fourthScenario.txt"};
//		BufferedReader br = new BufferedReader(new FileReader(new File(files[1])));
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
		
		System.out.println("Interesting Courses are "+interestingCourses);
		System.out.println("size is "+interestingCourses.size());
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
		
		System.out.println("Filler courses are "+ fillerCourses);
		System.out.println("size is "+fillerCourses.size());
		
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
		for(SemOrder s: fillerCourseIncreasingPriceFall)
			fallOrder.add(s);
		
		ArrayList<SemOrder> courseOrderToTake = new ArrayList<SemOrder>(fallOrder);
		
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
		for(SemOrder s: fillerCourseIncreasingPriceSpring)
			springOrder.add(s);
		
		System.out.println("courseOrderToTake is ");
		for(SemOrder so : courseOrderToTake)
			System.out.print(so.courseID+" ");
		System.out.println();
		System.out.println("Recursive Bactracking Start");
		
		Hashtable<Integer, Assignment> result = new Hashtable<Integer, Assignment>();
		
		// call recursive outerFunc on all the courses to take to find optimal assignment
		slns_b.outerFunc(result,courseOrderToTake,courseTable,orderAssignment );
		System.out.println("minAssignmentValue is "+minAssignmentValue);
//		System.out.println("Finresult");
//		System.out.println(finResult);		
//		System.out.println("Number of semesters "+finResult.size());
//		System.out.println("Final output is");
		slns_b.printFinalResult(courseTable);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Number of Complete Assignments "+numCompleteAssignment);
		System.out.println("Number of Incomplete Assignments "+numIncompleteAssignment);
		System.out.println("Total time taken in milli seconds "+totalTime);
	}
}
