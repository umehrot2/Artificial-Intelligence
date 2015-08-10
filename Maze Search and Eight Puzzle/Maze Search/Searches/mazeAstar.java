//package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.PriorityQueue;

public class mazeAstar
{
    private static Point start = new Point(0,0);
    private static Point end = new Point(0,0);

    private void printMaze(char[][] maze)
    {
        int rows = maze.length;
        int cols = maze[0].length;

        System.out.println("Maze array is ");
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }

    private int computeDistance(Point p)
    {
        //int startDistance = Math.abs(p.x - start.x) + Math.abs(p.y - start.y);
        //int endDistance = Math.abs(p.x - end.x) + Math.abs(p.y - end.y);

        //return startDistance + endDistance;
        return Math.abs(p.x - end.x) + Math.abs(p.y - end.y);
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
                int distance = computeDistance(points[i][j]);
                points[i][j].distance = distance;
            }
        }
    }

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

    private void aStar(char maze[][], Point[][] points)
    {
        PriorityQueue<Point> queue = new PriorityQueue<Point>();
        queue.add(start);
        int M = maze.length;
        int N = maze[0].length;
        Hashtable<Point, Integer> refHT = new Hashtable<>();
        refHT.put(new Point(start.x, start.y), 1);
        while(!queue.isEmpty())
        {
            Point current = queue.remove();

            //check for goal
            if(maze[current.x][current.y] == '.')
            {
                System.out.println("Reached the end !!");
                return;
            }

            //Mark
            points[current.x][current.y].visited = true;
            //maze[current.x][current.y] = '*';

            //Move Right
            if (current.y + 1 < N && current.x < M && !points[current.x][current.y+1].visited && maze[current.x][current.y+1] != '%') {
                points[current.x][current.y + 1].parent = current;
                points[current.x][current.y + 1].path = current.path + 1;
                if(refHT.get(new Point(current.x, current.y+1)) == null){
                	queue.add(points[current.x][current.y + 1]);
                	refHT.put(new Point(current.x, current.y+1), points[current.x][current.y + 1].path);
                }
                else{
                	int tpath = refHT.get(new Point(current.x, current.y+1));
                	if(tpath > points[current.x][current.y + 1].path){
                		queue.remove(points[current.x][current.y + 1]);
                		queue.add(points[current.x][current.y + 1]);
                		
                		refHT.put(new Point(current.x, current.y+1), points[current.x][current.y + 1].path);
                	}
                }
            }

            //Move down
            if (current.x + 1 < M && current.y < N && !points[current.x+1][current.y].visited && maze[current.x+1][current.y] != '%') {
                points[current.x+1][current.y].parent = current;
                points[current.x+1][current.y].path = current.path + 1;
                //queue.add(points[current.x+1][current.y]);
                if(refHT.get(new Point(current.x+1, current.y)) == null){
                	queue.add(points[current.x+1][current.y]);
                	refHT.put(new Point(current.x+1, current.y), points[current.x+1][current.y].path);
                }
                else{
                	int tpath = refHT.get(new Point(current.x+1, current.y));
                	if(tpath > points[current.x+1][current.y].path){
                		queue.remove(points[current.x+1][current.y]);
                		queue.add(points[current.x+1][current.y]);
                		
                		refHT.put(new Point(current.x+1, current.y), points[current.x+1][current.y].path);
                	}
                }
            }

            //Move Up
            if (current.x - 1 >= 0 && current.y < N && !points[current.x-1][current.y].visited && maze[current.x-1][current.y] != '%') {
                points[current.x-1][current.y].parent = current;
                points[current.x-1][current.y].path = current.path + 1;
                //queue.add(points[current.x-1][current.y]);
                if(refHT.get(new Point(current.x-1, current.y)) == null){
                	queue.add(points[current.x-1][current.y]);
                	refHT.put(new Point(current.x-1, current.y), points[current.x-1][current.y].path);
                }
                else{
                	int tpath = refHT.get(new Point(current.x-1, current.y));
                	if(tpath > points[current.x-1][current.y].path){
                		queue.remove(points[current.x-1][current.y]);
                		queue.add(points[current.x-1][current.y]);
                		
                		refHT.put(new Point(current.x-1, current.y), points[current.x-1][current.y].path);
                	}
                }
            }

            //Move Left
            if (current.y - 1 >= 0 && current.x < M && !points[current.x][current.y-1].visited && maze[current.x][current.y-1] != '%') {
                points[current.x][current.y-1].parent = current;
                points[current.x][current.y-1].path = current.path + 1;
                //queue.add(points[current.x][current.y-1]);
                if(refHT.get(new Point(current.x, current.y-1)) == null){
                	queue.add(points[current.x][current.y - 1]);
                	refHT.put(new Point(current.x, current.y-1), points[current.x][current.y - 1].path);
                }
                else{
                	int tpath = refHT.get(new Point(current.x, current.y-1));
                	if(tpath > points[current.x][current.y - 1].path){
                		queue.remove(points[current.x][current.y - 1]);
                		queue.add(points[current.x][current.y - 1]);
                		
                		refHT.put(new Point(current.x, current.y-1), points[current.x][current.y - 1].path);
                	}
                }
            }

        }

    }

    private int get_solution(char[][] maze, Point[][] points, int m, int n)
    {
    	int nodes_expanded = 0;
    	int count = 0;
    	for (int i=0;i<m;i++){
        	for(int j=0;j<n;j++){
        		if (points[i][j].visited==true){
        			nodes_expanded++;
        			//if(maze[i][j] != 'P')
        				//maze[i][j] = '*';
        		}
        	}
        }
        System.out.println("Nodes Expanded "+nodes_expanded);
    	
        Point current = points[end.x][end.y];
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
        return count;
    }

    public static void main(String[] args) throws IOException
    {
        System.out.println("Welcome to Maze Astar search");

        mazeAstar obj = new mazeAstar();
        int selection = Integer.parseInt(args[0]);
        String[] files = {
                "smallMaze.txt",//0
                "mediumMaze.txt",//1
                "bigMaze.txt",
                "AAGR.txt",
                "fromRP2.txt"
                
        };

        File mazeFile = new File(files[selection]);
        BufferedReader br = new BufferedReader(new FileReader(mazeFile));
        String line;
        int M = 0;
        int N = 0;
        while((line = br.readLine()) != null){
            M++;
            String[] items = line.split("");
            N = items.length;
        }        
        br.close();

        char[][] maze = new char[M][N];
        Point[][] points = new Point[M][N];

        //Initialize start / end points and read maze
        obj.readMaze(mazeFile, maze, start, end);

        //Initialize the point coordinates
        obj.initializePoints(points);

        //Print maze
        obj.printMaze(maze);

        //Apply A star search
        obj.aStar(maze, points);

        //Get the exact solution path
        obj.get_solution(maze, points, M, N);

        //Print the maze with solution
        obj.printMaze(maze);
    }
}
