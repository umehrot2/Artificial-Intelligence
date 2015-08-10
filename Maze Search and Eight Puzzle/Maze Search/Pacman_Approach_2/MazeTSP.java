import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.PriorityQueue;

class AdjMST implements Comparable<AdjMST>{
	Integer value;
	Integer column;
	Integer from;
	
	AdjMST(int value, int column){
		this.value = value;
		this.column = column;
	}
	
	AdjMST(int value, int column, int from){
		this.value = value;
		this.column = column;
		this.from = from;
	}

	@Override
	public int compareTo(AdjMST o) {
		// TODO Auto-generated method stub
		return this.value.compareTo(o.value);
	}
}

public class MazeTSP {
	public static Hashtable<Integer, Point> goalTable;
	
	public static int nodesExpanded;
	
	private void initializePoints(Point points[][])
    {
        int rows = points.length;
        int cols = points[0].length;

        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                points[i][j] = new Point(i,j);
            }
        }
    }
    
    // read maze file to: 
    // 1. Read the maze from txt to maze[][] char array
    // 2. get start and end point i.e. P and .    
    private int readMaze(File mazeFile, char[][] maze, Point start, Point end) throws IOException
    {
        String contents;
        goalTable = new Hashtable<Integer, Point>();
        BufferedReader buffer = new BufferedReader(new FileReader(mazeFile));
        int rno = 0;
        int numGoals = 0;
        while((contents = buffer.readLine()) != null)
        {
            for(int i = 0; i < contents.length(); i++){
                maze[rno][i] = contents.charAt(i);
                if(maze[rno][i] == 'P'){
                    start.x = rno;
                    start.y = i;
                    goalTable.put(0, new Point(rno, i));
                }
                else if(maze[rno][i] == '.'){
                	numGoals++;
                	goalTable.put(numGoals, new Point(rno,i));
                }
            }
            rno++;
        }
        System.out.println("Start point is "+start.x+" "+start.y);        
        buffer.close();
        return numGoals;
    }
    
    private int aStarNew(char maze[][], Point[][] points, Point curStart, Point curEnd)
    {
        PriorityQueue<Point> queue = new PriorityQueue<Point>();
        queue.add(curStart);
        int M = maze.length;
        int N = maze[0].length;
        int count = 0;
        while(!queue.isEmpty())
        {
            Point current = queue.remove();

            //check for goal
           	if(current.x == curEnd.x && current.y == curEnd.y)
            {
           		// goal reached
            	while(current.x != curStart.x || current.y != curStart.y)
                {
            		count++;
                    current = current.parent;
                }
                return count;
            }

            //Mark
            points[current.x][current.y].visited = true;
            nodesExpanded++;
            //maze[current.x][current.y] = '*';

            //Move Right
            if (current.y + 1 < N && current.x < M && !points[current.x][current.y+1].visited && maze[current.x][current.y+1] != '%') {
                points[current.x][current.y + 1].parent = current;
                points[current.x][current.y + 1].path = current.path + 1;
                queue.add(points[current.x][current.y + 1]);
            }

            //Move down
            if (current.x + 1 < M && current.y < N && !points[current.x+1][current.y].visited && maze[current.x+1][current.y] != '%') {
                points[current.x+1][current.y].parent = current;
                points[current.x+1][current.y].path = current.path + 1;
                queue.add(points[current.x+1][current.y]);
            }

            //Move Up
            if (current.x - 1 >= 0 && current.y < N && !points[current.x-1][current.y].visited && maze[current.x-1][current.y] != '%') {
                points[current.x-1][current.y].parent = current;
                points[current.x-1][current.y].path = current.path + 1;
                queue.add(points[current.x-1][current.y]);
            }

            //Move Left
            if (current.y - 1 >= 0 && current.x < M && !points[current.x][current.y-1].visited && maze[current.x][current.y-1] != '%') {
                points[current.x][current.y-1].parent = current;
                points[current.x][current.y-1].path = current.path + 1;
                queue.add(points[current.x][current.y-1]);
            }

        }
        System.out.println("Diddnt find A star path");
        return 0;
    }

    public int manhattanMatrix(Point start1, Point end1){
    	return Math.abs(start1.x - end1.x) + Math.abs(start1.y - end1.y);
    }
    
    public int getMST(int[][] adjMatrix, int size){
    	ArrayList<Integer> visitedNodes = new ArrayList<Integer>();
    	int[][] newAdj = new int[size][size];
    	int start = 0;
    	visitedNodes.add(start);
    	while(visitedNodes.size() != size){
    		PriorityQueue<AdjMST> minPQ = new PriorityQueue<AdjMST>();
    		for(int i = 0; i < visitedNodes.size(); i++){
    			int curStart = visitedNodes.get(i);
    			for(int j = 0; j < size; j++){
    				//if(visitedNodes.size() == 2 && curStart == 3)
    					//System.out.println("here");
    				//if(curStart == 3 && j == 2)
    					//System.out.println("Found");
    				if(!visitedNodes.contains(j) && adjMatrix[curStart][j] != 0){
    					minPQ.add(new AdjMST(adjMatrix[curStart][j], j, curStart));
    				}
    			}
    		}
    		int newNodeColumn = minPQ.peek().column;
    		int newNodeValue = minPQ.peek().value;
    		int newFrom = minPQ.peek().from;
    		visitedNodes.add(newNodeColumn);
    		newAdj[newFrom][newNodeColumn] = newNodeValue;
    		newAdj[newNodeColumn][newFrom] = newNodeValue;
    	}
    	System.out.println("New MST is ");
    	for(int i = 0; i < size; i++){
    		for(int j = 0; j <size; j++){
    			System.out.print(newAdj[i][j]+" ");
    		}
    		System.out.println();
    	}
    	
    	int hval = 0;
    	for(int i = 0; i < size; i++){
    		for(int j = 0; j < i; j++){
    			hval += newAdj[i][j];
    		}
    	}
    	System.out.println("final sum is "+hval);
    	return hval;
    	
    }
    
	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to MazeTSP");
        int selection = Integer.parseInt(args[0]);
        MazeTSP obj = new MazeTSP();
        String[] files = {                
                "mediumSearch.txt",
                "bigSearch.txt"
        };
        
        
        
        File mazeFile = new File(files[selection]);
        BufferedReader br = new BufferedReader(new FileReader(mazeFile));
        String line;
        int M = 0; // number of rows
        int N = 0; // number of columns
        
        while((line = br.readLine()) != null){
            M++;
            String[] items = line.split("");
            N = items.length;
        }
        br.close();
        
        System.out.println("Number of rows is "+M);
        System.out.println("Number of columns is "+N);
        System.out.println();
        
        char[][] maze = new char[M][N];
        Point[][] points = new Point[M][N];

        // Initialize the point coordinates
        obj.initializePoints(points);

        Point start = new Point(0,0);
        Point end = new Point(0,0);

        // Read maze file into char array and get start and end point.
        int numGoals = obj.readMaze(mazeFile, maze, start, end);
        System.out.println("Number of goals are "+numGoals);
        
        int tc = 0;
        int[][] adjMatrix = new int[goalTable.size()][goalTable.size()];
        // for each goal state calculate A*Path to every goal state
        for(int i : goalTable.keySet()){
        	Point newStart = goalTable.get(i);
        	for(int j : goalTable.keySet()){
	        		Point newEnd = goalTable.get(j);
	        		int pathCost = obj.aStarNew(maze, points, newStart, newEnd);
	        		//int pathCost = obj.manhattanMatrix( newStart, newEnd);
	        		adjMatrix[i][j] = pathCost;
	        		adjMatrix[j][i] = pathCost;
	        		obj.initializePoints(points);

        	}       	
        	
        }
        
        for( int i: goalTable.keySet())
        	System.out.println((i+1)+"-> ("+goalTable.get(i).x+", "+goalTable.get(i).y+")");
        System.out.println("Final adjacency matrix");
        for(int i = 0; i < goalTable.size(); i++){
        	for(int j = 0; j < goalTable.size(); j++){
        		System.out.print(adjMatrix[i][j]+" ");
        	}
        	System.out.println();
        }
        
        int numRows = goalTable.size();
        int stepCount = 1;
        Hashtable<Integer, Boolean> visitedGoals = new Hashtable<>();
        int startIndex = 0;
        
        visitedGoals.put(startIndex, true);
        int totalPathCost = 0;
        nodesExpanded = 0;
        while(numGoals > 0){
        
        	PriorityQueue<AdjMST> rowPQ = new PriorityQueue<>();
        	
        	// find the least path cost goal state
        	for(int j = 0; j < numRows; j++){
        		if(adjMatrix[startIndex][j] != 0 && visitedGoals.get(j) == null)
        			rowPQ.add(new AdjMST(adjMatrix[startIndex][j], j));
        	}
        	
        	int endIndex = rowPQ.peek().column;
        	visitedGoals.put(endIndex, true);
        	Point newStart = goalTable.get(startIndex);
        	Point newEnd = goalTable.get(endIndex);
        	
        	int pathCost = obj.aStarNew(maze, points, newStart, newEnd);
        	totalPathCost += pathCost;
        	System.out.println((stepCount++)+": From ("+newStart.x+", "+newStart.y+") - To ("+newEnd.x+", "+newEnd.y+") : Pathcost is "+pathCost);
        	if(pathCost != 0)
        		numGoals--;
        	
        	startIndex = endIndex;
        	obj.initializePoints(points);
        	
        }
        		
        System.out.println("Done , Total Path cost is "+totalPathCost);
        System.out.println("Total nodes expanded is "+nodesExpanded);
        
        
	}
}
