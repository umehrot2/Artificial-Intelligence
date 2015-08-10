
//import java.util.Hashtable;
import java.util.PriorityQueue;

public class GreedySearch {

	public void greedySearch(char[][] maze, Point[][] points, Point start, Point end, int M, int N ){
		//System.out.println("Inside greedy function");
		mazeNew mainMaze = new mazeNew();
		//mainMaze.printMaze(maze, M, N);
		
		// get source distance for all the points
		for(int i = 0; i < M; i++){
			for(int j = 0; j < N; j++){
				Point p = points[i][j];
				p.distance = Math.abs(p.x - end.x) + Math.abs(p.y - end.y);
			}
		}
		
//		System.out.println("Distance check");
//		System.out.println("Dist of (3,1) is "+points[3][1].sourceDistance);
		
		PriorityQueue<Point> frontierQueue = new PriorityQueue<Point>();
		frontierQueue.add(start);
		
		while(!frontierQueue.isEmpty()){
			Point current = frontierQueue.remove();
			
			 //check for goal
            if(maze[current.x][current.y] == '.')
            {
                System.out.println("Reached the end Greedy Search!!");
                mainMaze.markSolutionMaze(maze, points, end,M,N);
                return;
            }

            //Mark
            points[current.x][current.y].visited = true;
            //maze[current.x][current.y] = '*';

            //Move Right
            if (current.y + 1 < N && current.x < M && !points[current.x][current.y+1].visited && maze[current.x][current.y+1] != '%') {
                points[current.x][current.y + 1].parent = current;
                frontierQueue.add(points[current.x][current.y + 1]);
            }

            //Move down
            if (current.x + 1 < M && current.y < N && !points[current.x+1][current.y].visited && maze[current.x+1][current.y] != '%') {
                points[current.x+1][current.y].parent = current;
                frontierQueue.add(points[current.x+1][current.y]);
            }

            //Move Up
            if (current.x - 1 >= 0 && current.y < N && !points[current.x-1][current.y].visited && maze[current.x-1][current.y] != '%') {
                points[current.x-1][current.y].parent = current;
                frontierQueue.add(points[current.x-1][current.y]);
            }

            //Move Left
            if (current.y - 1 >= 0 && current.x < M && !points[current.x][current.y-1].visited && maze[current.x][current.y-1] != '%') {
                points[current.x][current.y-1].parent = current;
                frontierQueue.add(points[current.x][current.y-1]);
            }
		}
		
		
	}
}
