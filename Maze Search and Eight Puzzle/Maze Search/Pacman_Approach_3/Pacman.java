import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Stack;
import java.util.PriorityQueue;
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

    private int computeHeuristic(point p, ArrayList<point> goals)
    {
        int distance = 0;

        for(int i = 0; i < goals.size(); i++)
        {
            int d = Math.abs(goals.get(i).x - p.x) + Math.abs(goals.get(i).y - p.y);
            distance = distance + d;
        }

        return distance;
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
        int path_cost = 0;
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
            path_cost++;
        }

        path_cost--;
        System.out.println("Path Cost : " + path_cost);
        System.out.println("Order of visited nodes : ");
        while(!st.isEmpty())
        {
            point temp = st.pop();
            System.out.println(temp.x + "," + temp.y + "  :  " + ch);
            maze[temp.x][temp.y] = ch;
            ch++;
            if(ch == 58)
            {
                ch = 97;
            }
        }

        System.out.println("\nSolution maze : ");

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
        return p.pathCost + p.heuristic;
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
        HashMap<PacPoint, Integer> map = new HashMap<PacPoint, Integer>();
        PriorityQueue<PacPoint> frontier = new PriorityQueue<PacPoint>();
        frontier.add(start);
        map.put(start, getFunctionValue(start));

        int M = maze.length;
        int N = maze[0].length;
        int nodes_expanded = 0;

        while(!frontier.isEmpty())
        {
            PacPoint current = frontier.remove();
            map.remove(start);

            //check if it is a .
            if(maze[current.p.x][current.p.y] == '.')
            {
                //remove from goals list
                int index = -1;
                for(int i = 0; i < current.goals.size(); i++)
                {
                    int x = current.goals.get(i).x;
                    int y = current.goals.get(i).y;

                    if(current.p.x == x && current.p.y == y)
                    {
                        index = i;
                        break;
                    }
                }

                if(index != -1)
                {
                    current.goals.remove(index);
                }
            }

            //check if you have reached a goal
            if(checkGoal(current))
            {
                System.out.println("Reached The Goal !!");
                System.out.println("Number of nodes expanded : " + nodes_expanded);
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

                next.parent = current;
                next.pathCost = current.pathCost + 1;
                next.heuristic = computeHeuristic(next.p, current.goals);

                boolean shouldAdd = true;
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

                next.parent = current;
                next.pathCost = current.pathCost + 1;
                next.heuristic = computeHeuristic(next.p, current.goals);


                boolean shouldAdd = true;
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

                next.parent = current;
                next.pathCost = current.pathCost + 1;
                next.heuristic = computeHeuristic(next.p, current.goals);

                boolean shouldAdd = true;
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

                next.parent = current;
                next.pathCost = current.pathCost + 1;
                next.heuristic = computeHeuristic(next.p, current.goals);

                boolean shouldAdd = true;
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
                "mediumSearch.txt",
                "bigSearch.txt"
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
