
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Queue;

//import com.thinkjs.io.Point;

//import com.thinkjs.io.Point;

public class mazeNew
{

    public void markSolutionMaze(char[][] maze, Point[][] points, Point end,int m,int n)
    {
    	int nodes_expanded = 0;
    	int count = 0;
        Point current = points[end.x][end.y];
        for (int i=0;i<m;i++){
        	for(int j=0;j<n;j++){
        		if (points[i][j].visited==true){
        			nodes_expanded++;
        		}
        	}
        }
        System.out.println("Nodes Expanded "+nodes_expanded);
        while(maze[current.x][current.y] != 'P')
        {
            current = current.parent;
            if(maze[current.x][current.y] != 'P')
            {
                maze[current.x][current.y] = '.';
                count++;
            }
        }
        maze[current.x][current.y] = '.';
        System.out.println("The path cost is "+count);
        printMaze(maze, m, n);
        return ;
    }
    private void bfs(char maze[][],Point[][] points, Point start, Point end, int M, int N){
    	Queue<Point> frontier = new LinkedList<Point>();
    	frontier.add(start);
    	
    	
    	while(!frontier.isEmpty()){
    		Point current = frontier.poll();
    		if(maze[current.x][current.y] == '.')
            {
                System.out.println("Reached the end !!");
                
                // mark the solution path with '.' and get the path cost
                markSolutionMaze(maze, points, end,M,N);
                return;
            }

            //Mark
            points[current.x][current.y].visited = true;
            //maze[current.x][current.y] = '*';

            //Move Right
            if (current.y + 1 < N && current.x < M && !points[current.x][current.y+1].visited && maze[current.x][current.y+1] != '%') {
                points[current.x][current.y + 1].parent = current;
                frontier.add(points[current.x][current.y + 1]);
            }

            //Move down
            if (current.x + 1 < M && current.y < N && !points[current.x+1][current.y].visited && maze[current.x+1][current.y] != '%') {
                points[current.x+1][current.y].parent = current;
                frontier.add(points[current.x+1][current.y]);
            }

            //Move Up
            if (current.x - 1 >= 0 && current.y < N && !points[current.x-1][current.y].visited && maze[current.x-1][current.y] != '%') {
                points[current.x-1][current.y].parent = current;
                frontier.add(points[current.x-1][current.y]);
            }

            //Move Left
            if (current.y - 1 >= 0 && current.x < M && !points[current.x][current.y-1].visited && maze[current.x][current.y-1] != '%') {
                points[current.x][current.y-1].parent = current;
                frontier.add(points[current.x][current.y-1]);
            }

        }

    	
    }

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
    private void readMaze(File mazeFile, char[][] maze, Point start, Point end) throws IOException
    {
        String contents;

        BufferedReader buffer = new BufferedReader(new FileReader(mazeFile));
        int rno = 0;

        while((contents = buffer.readLine()) != null)
        {
            for(int i = 0; i < contents.length(); i++){
                maze[rno][i] = contents.charAt(i);
                if(maze[rno][i] == 'P'){
                    start.x = rno;
                    start.y = i;
                }
                else if(maze[rno][i] == '.'){
                    end.x = rno;
                    end.y = i;
                }
            }
            rno++;
        }
        
        buffer.close();
    }
    
    private void dfs(char maze[][], Point[][] points, Point start, Point end, int M, int N)
    {
        Stack<Point> frontier = new Stack<Point>();

        frontier.push(start);

        while(!frontier.isEmpty())
        {
            Point current = frontier.pop();

            //check for goal
            if(maze[current.x][current.y] == '.')
            {
                System.out.println("Reached the end !!");
                
                // mark the solution path with '.' and get the path cost
                markSolutionMaze(maze, points, end,M,N);
                return;
            }

            //Mark
            points[current.x][current.y].visited = true;
            //maze[current.x][current.y] = '*';

            //Move Right
            if (current.y + 1 < N && current.x < M && !points[current.x][current.y+1].visited && maze[current.x][current.y+1] != '%') {
                points[current.x][current.y + 1].parent = current;
                frontier.push(points[current.x][current.y + 1]);
            }

            //Move down
            if (current.x + 1 < M && current.y < N && !points[current.x+1][current.y].visited && maze[current.x+1][current.y] != '%') {
                points[current.x+1][current.y].parent = current;
                frontier.push(points[current.x+1][current.y]);
            }

            //Move Up
            if (current.x - 1 >= 0 && current.y < N && !points[current.x-1][current.y].visited && maze[current.x-1][current.y] != '%') {
                points[current.x-1][current.y].parent = current;
                frontier.push(points[current.x-1][current.y]);
            }

            //Move Left
            if (current.y - 1 >= 0 && current.x < M && !points[current.x][current.y-1].visited && maze[current.x][current.y-1] != '%') {
                points[current.x][current.y-1].parent = current;
                frontier.push(points[current.x][current.y-1]);
            }

        }



    }

    public void printMaze(char[][] maze, int M, int N){
    	for(int i = 0; i < M; i++){
            for(int j = 0; j < N; j++){
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }
    public static void main(String[] args) throws IOException
    {
    	//int pathCost =0;
        System.out.println("Welcome to maze search");
        int selection = Integer.parseInt(args[0]);
        mazeNew obj = new mazeNew();
        String[] files = {
                "smallMaze.txt",
                "mediumMaze.txt",
                "bigMaze.txt",
                "AAGR.txt",
                "fromRP2.txt"
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
        obj.readMaze(mazeFile, maze, start, end);
        System.out.println("Start point coordinates are row: "+start.x+", col: "+start.y+", value is "+maze[start.x][start.y]);
        System.out.println("End point coordinates are row: "+end.x+", col: "+end.y+", value is "+maze[end.x][end.y]);

        System.out.println("Maze array is ");
        obj.printMaze(maze, M, N);

        // DFS
        System.out.println("=====DEPTH FIRST SEARCH====");
        obj.dfs(maze, points, start, end, M, N);        
        System.out.println("===========================");
        
        // Re-initialize maze and points array
        obj.initializePoints(points);
        
        // Read maze file into char array and get start and end point.
        obj.readMaze(mazeFile, maze, start, end);
        // Re-initialize maze and points array
        
        
        // BFS
        System.out.println("=====BREADTH FIRST SEARCH====");
        obj.bfs(maze, points, start, end, M, N);
        System.out.println("===========================");

        // Re-initialize maze and points array
        obj.initializePoints(points);
        
        // Read maze file into char array and get start and end point.
        obj.readMaze(mazeFile, maze, start, end);
        // Re-initialize maze and points array
        
        // Greedy Search
        System.out.println("====Greedy Search====");
        GreedySearch gs = new GreedySearch();
        gs.greedySearch(maze, points, start, end, M, N);
        System.out.println("===========================");
        
        System.out.println("Maze Search Completed");
    }
}