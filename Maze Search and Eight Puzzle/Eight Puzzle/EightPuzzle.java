import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.io.FileWriter;
import java.io.IOException;

class Values{
	int pathCost;
	int numNodes;
	//int[][] puzzle = new int[3][3];
	
//	Values(int[][] puz, int pathCost, int numNodes){
	Values(int pathCost, int numNodes){
		this.pathCost = pathCost;
		this.numNodes = numNodes;
	}
}

public class EightPuzzle {
	// hashtable to check if the int equivalent of this 8puzzle is already seen
	public static Hashtable<Integer, Integer> visitedSet;// = new Hashtable<Integer, Integer>();
	
	// hastable to store int equivalent of puzzle and its path cost, to later update the path cost in frontier
	public static Hashtable<Integer, Integer> inFrontier;// = new Hashtable<Integer, Integer>();
	public static int[][] solution;
	public static PriorityQueue<Puzzle> frontier;
	public static int heurisitc;
	
	public boolean isSolvable(int[][] input){
		int val = convertToInt(input);
		String temp = Integer.toString(val);
		int[] valArray = new int[temp.length()];
		for (int i = 0; i < temp.length(); i++)
		{
			valArray[i] = temp.charAt(i) - '0';
		}
		int count = 0;
		for(int i = 0; i < valArray.length; i++){
			int ind = valArray[i];
			for(int j = i+1; j < valArray.length; j++){
				if(ind > valArray[j] && valArray[j] != 0 && ind != 0)
					count++;
			}
		}
		return count%2 == 0;
	}
	
	public int[][] dcopyArray(int[][] a){
		int[][] newArr = new int[3][3];
		for(int i =0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				newArr[i][j] = a[i][j];
			}
		}
		return newArr;
	}
	
	public ArrayList<int[][]> generatePuzzle2(){
		int[][] start = {{0,1,2},{3,4,5},{6,7,8}};
		int startStep = 8;
		ArrayList<int[][]> genArray = new ArrayList<int[][]>();
		int zeroX = 0, zeroY = 0;
		// keep track of visited mazes
		Hashtable<Integer,Integer> visitedPuz = new Hashtable<Integer,Integer>();
		visitedPuz.put(convertToInt(start), 1);
		int track = 0;
		
		for(int i = 0; i < 13; i++){
			//int track = 0;
			System.out.println("In track "+i+" trackval "+track+" step val "+startStep);
			if( i == 3)
				System.out.println("In loop");
			//visitedPuz = new Hashtable<>();
			while(track < startStep){
				// check bottom
				if(zeroX + 1 < 3 && zeroY < 3){
					int tempVal = start[zeroX+1][zeroY];
					start[zeroX][zeroY] = tempVal;
					start[zeroX+1][zeroY] = 0;
					if(visitedPuz.get(convertToInt(start)) == null){
						visitedPuz.put(convertToInt(start), 1);
						//genArray.add(start);
						
						// update new zero coordinates
						zeroX = zeroX+1;
						//zeroY = zeroY;
						track++;
					}else{					
						// revert back current array for other movement check
						start[zeroX][zeroY] = 0;
						start[zeroX+1][zeroY] = tempVal;
					}
				}
				
				// check right
				if(zeroX < 3 && zeroY+1 < 3){
					int tempVal = start[zeroX][zeroY+1];
					start[zeroX][zeroY] = tempVal;
					start[zeroX][zeroY+1] = 0;
					if(visitedPuz.get(convertToInt(start)) == null){
						visitedPuz.put(convertToInt(start), 1);
						//genArray.add(start);
						
						// update new zero coordinates
						//zeroX = zeroX+1;
						zeroY = zeroY+1;
						track++;
					}else{					
						// revert back current array for other movement check
						start[zeroX][zeroY] = 0;
						start[zeroX][zeroY+1] = tempVal;
					}
				}
				
				// check top
				if(zeroX - 1 >= 0 && zeroY < 3){
					int tempVal = start[zeroX-1][zeroY];
					start[zeroX][zeroY] = tempVal;
					start[zeroX-1][zeroY] = 0;
					if(visitedPuz.get(convertToInt(start)) == null){
						visitedPuz.put(convertToInt(start), 1);
						//genArray.add(start);
						
						// update new zero coordinates
						zeroX = zeroX-1;
						//zeroY = zeroY+1;
						track++;
					}else{					
						// revert back current array for other movement check
						start[zeroX][zeroY] = 0;
						start[zeroX-1][zeroY] = tempVal;
					}
				}
				// check left
				if(zeroX < 3 && zeroY-1 >= 0){
					int tempVal = start[zeroX][zeroY-1];
					start[zeroX][zeroY] = tempVal;
					start[zeroX][zeroY-1] = 0;
					if(visitedPuz.get(convertToInt(start)) == null){
						visitedPuz.put(convertToInt(start), 1);
						//genArray.add(start);
						
						// update new zero coordinates
						//zeroX = zeroX-1;
						zeroY = zeroY-1;
						track++;
					}else{					
						// revert back current array for other movement check
						start[zeroX][zeroY] = 0;
						start[zeroX][zeroY-1] = tempVal;
					}
				}
			}
			
			// add all neighboring combinations to the arraylist
			genArray.add(dcopyArray(start));

			int stepTemp = startStep;
			startStep += (track-startStep)+5;
			track = stepTemp;
			if(startStep >= 23){
				startStep = 8;
				track = 0;
				visitedPuz = new Hashtable<>();
			}
			
		}
		System.out.println("Done");
		return genArray;
		
	}
	
	public ArrayList<int[][]> generatePuzzle(){
		int M = 3, N = 3;
		ArrayList<int[][]> ar = new ArrayList<int[][]>();
		Integer[] array = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
		int k = 0;
		int total = 35;
		int[][] puzzle = new int[M][N]; 
		while(total > 0){
			puzzle = new int[M][N];
			Collections.shuffle(Arrays.asList(array));
			for(int i = 0; i < M; i++){
				for(int j = 0; j < N; j++){
					puzzle[i][j] = array[k++];
				}
			}
			if(isSolvable(puzzle)){
				ar.add(puzzle);
				total--;
			}
			
			k = 0;
			
		}
		return ar;
	}
	
	public ArrayList<int[][]> fullPuzzleList(ArrayList<int[][]> tempArr){
		ArrayList<int[][]> finalList = new ArrayList<int[][]>();
		
		tempArr.add(new int[][]{{5,1,2},{3,4,0},{6,7,8}});// cost 12
		tempArr.add(new int[][]{{5,1,2},{3,4,8},{6,7,0}}); // cost 13
		tempArr.add(new int[][]{{1,2,5},{3,4,0},{6,7,8}}); // cost 4
		tempArr.add(new int[][]{{1,2,5},{3,4,8},{0,6,7}}); // cost 7
		tempArr.add(new int[][]{{3,1,2},{4,7,6},{0,8,5}}); // cost 13
		tempArr.add(new int[][]{{3,1,2},{6,4,5},{7,8,0}}); // cost 5
		tempArr.add(new int[][]{{4,1,7},{2,5,6},{8,0,3}}); // cost 26
		tempArr.add(new int[][]{{1,4,2},{3,0,5},{6,7,8}}); // cost 3
		tempArr.add(new int[][]{{5,4,2},{1,0,3},{6,7,8}}); // cost 13
		tempArr.add(new int[][]{{5,4,2},{1,0,3},{6,7,8}}); // cost 13
		tempArr.add(new int[][]{{7,6,2},{3,4,0},{8,1,5}}); // cost 18
		tempArr.add(new int[][]{{2,3,1},{0,5,4},{8,7,6}}); // cost 26
		tempArr.add(new int[][]{{3,1,2},{6,4,5},{0,7,8}}); // cost 3
		tempArr.add(new int[][]{{0,1,2},{4,6,5},{3,7,8}}); // cost 15
		//int[][] tinput ={{7,2,5},{4,6,1},{3,0,8}}; // cost 16
		tempArr.add(new int[][]{{7,2,5},{4,6,1},{3,0,8}});
		
		return tempArr;
	}
	
	public static void printArray(int[][] a){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				System.out.print(a[i][j]);
			}
			System.out.println();
		}
	}
	
	public boolean isSolution(Puzzle test, int[][] solution){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(test.P[i][j] != solution[i][j])
					return false;
			}
		}
		return true;
	}
	
	public static int convertToInt(int[][] md){
		int ss = 0;
		int mul = 100000000;
		for(int i =0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				ss+=(md[i][j] * mul);
				mul /= 10;
			}
		}		
		return ss;
	}
	
	public void moveTiles(Puzzle current){
		if(visitedSet.get(convertToInt(current.P)) == null){
			Puzzle newPuzzleState = new Puzzle(current.P, solution, current.path+1, heurisitc);
								
			newPuzzleState.parent = current;
			
			// check if the maze is already present in the frontier
			int frontierVal = convertToInt(newPuzzleState.P);
			
			// if the puzzle is already present in frontier, check its path cost
			if(inFrontier.get(frontierVal) != null){
				int curPathCost = inFrontier.get(frontierVal);
				
				// if path cost of puzzle in frontier is greater than that of new one, update the priority queue
				if(curPathCost > newPuzzleState.path){
					frontier.remove(current);
					frontier.add(newPuzzleState);
					
					//upadate hashtable with new lesser path cost
					inFrontier.put(frontierVal, newPuzzleState.path);
				}
			}
			else{
				inFrontier.put(frontierVal, newPuzzleState.path);
				frontier.add(newPuzzleState);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		int M = 3, N = 3, val = 0;
		
		System.out.println("In Eight Puzzle file");
		ArrayList<int[][]> tempArr = new ArrayList<int[][]>();
		ArrayList<int[][]> ar = new ArrayList<int[][]>();
		
		// hastable to store index and puzzle values for each of 50 puzzle to calculate average pathcost and num nodes
		Hashtable<Integer, Values> storePuzzle = new Hashtable<Integer, Values>();
		// 8 puzzle reference solution
		solution = new int[3][3];
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				solution[i][j] = val++;
			}
		}		
		
		EightPuzzle rp = new EightPuzzle();
				
		// generate 50 random puzzles of varying difficulty
		
		tempArr = rp.generatePuzzle();
		ar = rp.fullPuzzleList(tempArr);
		
		System.out.println("Array length is "+ar.size());
		
		int noSolutions = 0;
		
		// run for each heurisitic on the same 50 tiles
		int hval = 1;
		FileWriter writer;
		while(hval < 4){
		
		// heuristic to apply
		// 1. h = 1, misplaced tiles
		// 2. h = 2, manhattan distance
		// 3. h = 3, Gasching's heuristic
		
		
		heurisitc = hval;
		System.out.println("============For Heuristic val "+heurisitc+"============");
		
		// write to appropriate csv files for charting
		writer = new FileWriter(hval+".csv");
		 writer.append("Path Cost");
		 writer.append(',');
		 writer.append("Number of Nodes Expanded");
		 writer.append('\n');
		
		// loop for 50 instances of puzzle
		int tat = 1;
		for(int z = 0; z < ar.size(); z++){	
			int[][] first = ar.get(z);			
			val = 0;
			
			frontier = new PriorityQueue<Puzzle>();
			
			// pass solution to compute heuristic - h value
			Puzzle f1 = new Puzzle(first, solution, 1, heurisitc);
			frontier.add(f1);
			
			boolean foundSolution = false;
			
			// hashtable to check if the int equivalent of this 8puzzle is already seen
			visitedSet = new Hashtable<Integer, Integer>();
			
			// hastable to store int equivalent of puzzle and its path cost, to later update the path cost in frontier
			inFrontier = new Hashtable<Integer, Integer>();
			
			// add integer equivalent of 2-d array in hashtable, for visited comparision
			int firstInt = convertToInt(first);
						
			// add first puzzle to visitedSet and inFrontier set
			visitedSet.put(firstInt, 1);
			inFrontier.put(firstInt, 1);
			
			int numVisited = 0;
			
			// check till all puzzle statesa are removed from frontier
			while(!frontier.isEmpty()){
				Puzzle current = frontier.remove();
				
				// if solution is reached, write path cost and node expanded to csv
				if(rp.isSolution(current, solution)){
					System.out.println((tat++)+": Solution reached and cost is "+current.path+" Nodes expanded is "+numVisited);
					String cpp = Float.toString(current.path);
					String npp = Float.toString(numVisited);
					 writer.append(cpp);
					 writer.append(',');
					 writer.append(npp);
					 writer.append('\n');
					 
					// store pathcost and number of nodes visited for each puzzle to calculate average later
					storePuzzle.put(z, new Values(current.path, numVisited));
					
					// print solution path
					while(!rp.isSolution(current, f1.P)){
						current = current.parent;
					}
					
					foundSolution = true;
					break;
				}
				
				current.visited = true;
				numVisited++;
				int zeroX = current.x;
				int zeroY = current.y;
				
				visitedSet.put(convertToInt(current.P), 1);			
				
				// check bottom
				if(zeroX + 1 < 3 && zeroY < 3){
					int tempVal = current.P[zeroX+1][zeroY];
					current.P[zeroX][zeroY] = tempVal;
					current.P[zeroX+1][zeroY] = 0;
					
					rp.moveTiles(current);
					
					// revert back current array for other movement check
					current.P[zeroX][zeroY] = 0;
					current.P[zeroX+1][zeroY] = tempVal;
				}
						
				// check top
				if(zeroX - 1 >= 0 && zeroY < 3){
					int tempVal = current.P[zeroX-1][zeroY];
					current.P[zeroX][zeroY] = tempVal;
					current.P[zeroX-1][zeroY] = 0;
					
					rp.moveTiles(current);
	
					
					// revert back current array for other movement check
					current.P[zeroX][zeroY] = 0;
					current.P[zeroX-1][zeroY] = tempVal;
				}
							
				// check left
				if(zeroX < 3 && zeroY-1 >= 0){
					int tempVal = current.P[zeroX][zeroY-1];
					current.P[zeroX][zeroY] = tempVal;
					current.P[zeroX][zeroY-1] = 0;
					
					rp.moveTiles(current);
					
					// revert back current array for other movement check
					current.P[zeroX][zeroY] = 0;
					current.P[zeroX][zeroY-1] = tempVal;
				}
				
				// check right
				if(zeroX < 3 && zeroY+1 < 3){
					int tempVal = current.P[zeroX][zeroY+1];
					current.P[zeroX][zeroY] = tempVal;
					current.P[zeroX][zeroY+1] = 0;
					
					rp.moveTiles(current);				
					
					// revert back current array for other movement check
					current.P[zeroX][zeroY] = 0;
					current.P[zeroX][zeroY+1] = tempVal;
				}			
				
			}
			if(!foundSolution){
				noSolutions++;
				storePuzzle.put(z, new Values(0, 0));
			}
				
		}
		System.out.println("Puzzle solved");
		
		// calculate average path cost and number of nodes expanded
		int tempPath = 0, tempNodes = 0;
		String xlabel = "(";
		String ylabel = "(";
		for(int zz = 0; zz < storePuzzle.size(); zz++){
			tempPath += storePuzzle.get(zz).pathCost;
			xlabel += storePuzzle.get(zz).pathCost +", ";
			ylabel += storePuzzle.get(zz).numNodes +", ";
			tempNodes += storePuzzle.get(zz).numNodes;
		}
		double averagePathCost = tempPath/((storePuzzle.size() - noSolutions) * 1.0);
		double averageNodes = tempNodes/((storePuzzle.size() - noSolutions) * 1.0);
		xlabel += ")";
		ylabel += ")";
		System.out.println(xlabel);
		System.out.println(ylabel);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Number of puzzles - "+storePuzzle.size()+": no solution puzzles "+noSolutions);
		System.out.println("Average Path Cost: "+averagePathCost+", Average Nodes Expanded "+averageNodes);
		System.out.println("Total time taken "+(endTime-startTime));
		hval++;
		writer.flush();
	    writer.close();
	}
	}
}
