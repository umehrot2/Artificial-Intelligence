import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Stack;
import java.util.PriorityQueue;
import java.util.HashMap;

/**
 * Created by uditmehrotra on 08/02/15.
 */
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Pacman
{
    private static PacPoint start = new PacPoint(0,0);

    private ArrayList<point> goals(char[][] maze)
    {
        int rows = maze.length;
        int cols = maze[0].length;
        ArrayList<point> goals = new ArrayList<point>();

        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                if(maze[i][j] == '.')
                {
                    goals.add(new point(i,j));
                }
            }
        }

        return goals;
    }

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

    private void readMaze(File mazeFile, char[][] maze, PacPoint start) throws IOException
    {
        String contents;

        BufferedReader buffer = new BufferedReader(new FileReader(mazeFile));
        int rno = 0;

        while((contents = buffer.readLine()) != null)
        {
            for(int i = 0; i < contents.length(); i++){
                maze[rno][i] = contents.charAt(i);
                if(maze[rno][i] == 'P'){
                    start.p.x = rno;
                    start.p.y = i;
                }
            }
            rno++;
        }

        buffer.close();
    }

    private int[][] computeAdjacencyMatrix(HashMap<Integer, point> goals)
    {
        int[][] adjMatrix = new int[goals.size()][goals.size()];

        for(Integer i : goals.keySet())
        {
            for(Integer j : goals.keySet())
            {
                point start = goals.get(i);
                point end = goals.get(j);

                int distance = Math.abs(start.x - end.x) + Math.abs(start.y - end.y);
                adjMatrix[i][j] = distance;
                adjMatrix[j][i] = distance;
            }
        }

        return adjMatrix;
    }

    private int computeHeuristic(point p, ArrayList<point> goals)
    {
        int distance = 0;
        HashMap<Integer, point> map = new HashMap<Integer, point>();

        boolean present = false;
        if(goals.contains(p))
        {
             goals.remove(p);
             present = true;
        }

        map.put(0, p);
        int index = 1;
        for(int loop = 0; loop < goals.size(); loop++)
        {
            map.put(index++, goals.get(loop));
        }

        if(present)
        {
            goals.add(p);
        }

        int[][] adjMatrix = computeAdjacencyMatrix(map);

        MazeTSP obj = new MazeTSP();
        return obj.getMST(adjMatrix, adjMatrix.length);
    }

    private boolean checkGoal(PacPoint p)
    {
        return p.goals.size() == 0;
    }

    private void print_solution(PacPoint p, char[][] maze)
    {
        ArrayList<point> goals = goals(maze);

        char ch = (char) 48;
        Stack<point> st = new Stack<point>();

        int cost = 0;
        while(p != null)
        {
            for(int i = 0; i < p.goals.size(); i++)
            {
                if(!st.contains(p.goals.get(i)))
                {
                    st.push(p.goals.get(i));
                }
            }

            p = p.parent;
            cost++;
        }

        cost--;
        System.out.println("Path Cost : " + cost);

        while(!st.isEmpty())
        {
            point temp = st.pop();
            maze[temp.x][temp.y] = ch;
            ch++;
            if(ch == 58)
            {
                ch = 97;
            }
        }

        System.out.println("Solution : ");
        for(int i = 0; i < maze.length; i++)
        {
            for(int j = 0; j < maze[0].length; j++)
            {
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }

    private int getFunctionValue(PacPoint p)
    {
        return p.heuristic + p.pathCost;
    }

    private boolean checkIfExistsHigherCost(PacPoint p, HashMap<PacPoint,Integer> frontier)
    {
        if(frontier.containsKey(p))
        {
            int previousCost = frontier.get(p);
            if(previousCost > getFunctionValue(p)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkIfExistsLowerCost(PacPoint p, HashMap<PacPoint,Integer> frontier)
    {
        if(frontier.containsKey(p))
        {
            int previousCost = frontier.get(p);
            if(previousCost <= getFunctionValue(p)) {
                return true;
            }
        }

        return false;
    }

    //jai mata di
    private void astar(char[][] maze)
    {
        //HashMap for storing frontier for efficient search
        int nodes_expanded = 0;
        HashMap<PacPoint, Integer> map = new HashMap<PacPoint, Integer>();
        PriorityQueue<PacPoint> frontier = new PriorityQueue<PacPoint>();
        frontier.add(start);
        map.put(start, getFunctionValue(start));
        int M = maze.length;
        int N = maze[0].length;

        while(!frontier.isEmpty())
        {
            PacPoint current = frontier.remove();
            map.remove(current);
            //check if it is a .

            //check if you have reached a goal
            if(checkGoal(current))
            {
                System.out.println("Reached The Goal !!");
                System.out.println("Number of Nodes Expanded : " + nodes_expanded);
                print_solution(current, maze);
                return;
            }

            nodes_expanded++;



            //Move Right
            if (current.p.y + 1 < N && current.p.x < M && maze[current.p.x][current.p.y+1] != '%') {
                PacPoint next = new PacPoint(current.p.x, current.p.y + 1);


                //copy goals from current
                for(int i = 0; i < current.goals.size(); i++)
                {
                    next.goals.add(current.goals.get(i));
                }

                if(next.goals.contains(next.p))
                    next.goals.remove(next.p);

                boolean shouldAdd = true;
                next.parent = current;
                next.pathCost = current.pathCost + 1;
                next.heuristic = computeHeuristic(next.p, current.goals);

                if(checkIfExistsHigherCost(next, map))
                {
                    frontier.remove(next);
                    map.remove(next);
                }
                else if(checkIfExistsLowerCost(next, map))
                {
                    shouldAdd = false;
                }

                if(shouldAdd)
                {
                    frontier.add(next);
                    map.put(next, getFunctionValue(next));
                }
            }





            //Move Left
            if (current.p.y - 1 >= 0 && current.p.x < M && maze[current.p.x][current.p.y-1] != '%') {
                PacPoint next = new PacPoint(current.p.x, current.p.y - 1);


                //copy goals from current
                for(int i = 0; i < current.goals.size(); i++)
                {
                    next.goals.add(current.goals.get(i));
                }

                if(next.goals.contains(next.p))
                    next.goals.remove(next.p);

                boolean shouldAdd = true;
                next.parent = current;
                next.pathCost = current.pathCost + 1;
                next.heuristic = computeHeuristic(next.p, current.goals);

                if(checkIfExistsHigherCost(next, map))
                {
                    frontier.remove(next);
                    map.remove(next);
                }
                else if(checkIfExistsLowerCost(next, map))
                {
                    shouldAdd = false;
                }

                if(shouldAdd)
                {
                    frontier.add(next);
                    map.put(next, getFunctionValue(next));
                }
            }

            //Move Up
            if (current.p.x - 1 >= 0 && current.p.y < N && maze[current.p.x-1][current.p.y] != '%') {
                PacPoint next = new PacPoint(current.p.x - 1, current.p.y);


                //copy goals from current
                for(int i = 0; i < current.goals.size(); i++)
                {
                    next.goals.add(current.goals.get(i));
                }

                if(next.goals.contains(next.p))
                    next.goals.remove(next.p);

                boolean shouldAdd = true;
                next.parent = current;
                next.pathCost = current.pathCost + 1;
                next.heuristic = computeHeuristic(next.p, current.goals);

                if(checkIfExistsHigherCost(next, map))
                {
                    frontier.remove(next);
                    map.remove(next);
                }
                else if(checkIfExistsLowerCost(next, map))
                {
                    shouldAdd = false;
                }

                if(shouldAdd)
                {
                    frontier.add(next);
                    map.put(next, getFunctionValue(next));
                }
            }

            //Move down
            if (current.p.x + 1 < M && current.p.y < N && maze[current.p.x+1][current.p.y] != '%')
            {
                PacPoint next = new PacPoint(current.p.x + 1, current.p.y);

                //copy goals from current
                for(int i = 0; i < current.goals.size(); i++)
                {
                    next.goals.add(current.goals.get(i));
                }

                if(next.goals.contains(next.p))
                    next.goals.remove(next.p);

                boolean shouldAdd = true;
                next.parent = current;
                next.pathCost = current.pathCost + 1;
                next.heuristic = computeHeuristic(next.p, current.goals);

                if(checkIfExistsHigherCost(next, map))
                {
                    frontier.remove(next);
                    map.remove(next);
                }
                else if(checkIfExistsLowerCost(next, map))
                {
                    shouldAdd = false;
                }

                if(shouldAdd)
                {
                    frontier.add(next);
                    map.put(next, getFunctionValue(next));
                }
            }
        }

    }


    private void printGoals(ArrayList<point> goals)
    {
        for(int i = 0; i < goals.size(); i++)
        {
            System.out.println(goals.get(i).x + " , " + goals.get(i).y);
        }
    }

    public static void main(String[] args) throws IOException
    {
        System.out.println("Welcome to Pac Man problem !!");

        Pacman obj = new Pacman();
        int selection = Integer.parseInt(args[0]);
        String[] files = {
                "smallSearch.txt",
                "trickySearch.txt",
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

        //Initialize start point and read maze
        obj.readMaze(mazeFile, maze, start);

        //Print maze
        obj.printMaze(maze);

        //Get goals for the start state
        start.goals = obj.goals(maze);

        //Run A star search
        obj.astar(maze);
    }
}
