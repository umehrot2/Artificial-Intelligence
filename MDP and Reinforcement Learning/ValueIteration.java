import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import org.math.plot.*;
import javax.swing.JFrame;
class State
{
    int x;
    int y;
    double utility;
    ArrayList<String> actions = new ArrayList<String>();
    String action;
    double reward;
    boolean isMarked = false;
}

public class ValueIteration {

    private static String[][] grid = new String[6][6];
    private static State[][] states = new State[6][6];
    private static double gamma = 0.99;
    private static int rows = 6;
    private static int cols = 6;
    DecimalFormat df = new DecimalFormat("#.######");
    HashMap<String,HashSet<String>> directions = new HashMap<String, HashSet<String>>();
    public static ArrayList<ArrayList<Double>> utilityVals = new ArrayList<ArrayList<Double>>();
    private static double e = 1.0;

    public ValueIteration()
    {
        //Initialize grid
        for(int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++)
            {
                grid[i][j] = " ";
            }
        }
        grid[0][0] = grid[0][2] = grid[0][5] = grid[1][3] = grid[2][4] = grid[3][5] = "1";
        grid[1][1] = grid[1][5] = grid[2][2] = grid[3][3] = grid[4][4] = "-1";
        grid[0][1] = grid[1][4] = grid[4][1] = grid[4][2] = grid[4][3] = "w";
        grid[3][2] = "s";

        //Initialize utility values
        for(int i = 0; i < states.length; i++)
        {
            for(int j = 0; j < states[0].length; j++)
            {
                states[i][j] = new State();
                states[i][j].x = i;
                states[i][j].y = j;
                states[i][j].utility = 0.0;
                if(grid[i][j].equals("1"))
                {
                    states[i][j].reward = 1.0;
                }
                else if(grid[i][j].equals("-1"))
                {
                    states[i][j].reward = -1.0;
                }
                else if(grid[i][j].equals(" ") || grid[i][j].equals("s"))
                {
                    states[i][j].reward = -0.04;
                }
            }
        }
    }

    private void setDirections()
    {
        directions.put("r", new HashSet<String>());
        directions.get("r").add("u");
        directions.get("r").add("d");
        directions.put("u", new HashSet<String>());
        directions.get("u").add("l");
        directions.get("u").add("r");
        directions.put("d", new HashSet<String>());
        directions.get("d").add("l");
        directions.get("d").add("r");
        directions.put("l", new HashSet<String>());
        directions.get("l").add("u");
        directions.get("l").add("d");
    }

    private void getActions()
    {
        for(int i = 0; i < states.length; i++)
        {
            for (int j = 0; j < states[0].length; j++)
            {
                if(grid[i][j].equals("w"))
                    continue;

                //states[i][j].actions = new HashMap<String, HashMap<String, Double>>();

                //Go Up
                /*states[i][j].actions.put("u", new HashMap<String, Double>());
                if (states[i][j].x - 1 >= 0)
                {
                    if(!grid[states[i][j].x - 1][states[i][j].y].equals("w")) {
                        states[i][j].actions.get("u").put("u", 0.8);
                    }
                    else
                    {
                        states[i][j].actions.get("u").put("s", 0.8);
                    }
                }
                else
                {
                    if(states[i][j].y - 1 < 0 || states[i][j].y + 1 >= cols) {
                        //Top left and Top Right corners
                        states[i][j].actions.get("u").put("s", 0.9);

                    }
                    else
                    {

                    }
                }*/


                if (states[i][j].x - 1 >= 0 && !grid[states[i][j].x - 1][states[i][j].y].equals("w"))
                {
                    states[i][j].actions.add("u");
                }

                //Go Down
                if (states[i][j].x + 1 < rows && !grid[states[i][j].x + 1][states[i][j].y].equals("w"))
                {
                    states[i][j].actions.add("d");
                }

                //Go Left
                if (states[i][j].y - 1 >= 0 && !grid[states[i][j].x][states[i][j].y - 1].equals("w"))
                {
                    states[i][j].actions.add("l");
                }

                //Go Right
                if (states[i][j].y + 1 < cols && !grid[states[i][j].x][states[i][j].y + 1].equals("w"))
                {
                    states[i][j].actions.add("r");
                }

            }
        }


        for(int i = 0; i < states.length; i++) {

            for (int j = 0; j < states[0].length; j++) {
                System.out.print("Current State : " +  i + "," + j +  "  :  ");
                for(int loop = 0; loop < states[i][j].actions.size(); loop++)
                {
                    System.out.print(states[i][j].actions.get(loop) + "  ");
                }
                System.out.println();
            }


        }
    }

    private boolean checkConvergence(double[][] previous_utilities)
    {

        /*for(int i = 0; i < states.length; i++) {

            for (int j = 0; j < states[0].length; j++) {
                if (states[i][j].utility != previous_utilities[i][j])
                    return false;
            }
        }

        return true;*/

        double delta = Integer.MIN_VALUE;
        for(int i = 0; i < states.length; i++)
        {

            for (int j = 0; j < states[0].length; j++)
            {
                double diff = Math.abs(states[i][j].utility - previous_utilities[i][j]);
                if(diff > delta)
                {
                    delta = diff;
                }
            }
        }

        double val = e * (1-gamma) / gamma;

        if(delta < val)
            return true;
        else
            return false;
    }

    private State getState(State current, String direction)
    {
        if(direction.equals("u"))
        {
            return states[current.x - 1][current.y];
        }
        else if(direction.equals("d"))
        {
            return states[current.x + 1][current.y];
        }
        else if(direction.equals("l"))
        {
            return states[current.x][current.y - 1];
        }
        else
        {
            return states[current.x][current.y + 1];
        }
    }

    private void valueIteration()
    {
        int count = 0;
        while(true)
        {
            count++;
            System.out.println("Iteration : " + count);
            Queue<State> neighbors = new LinkedList<State>();
            neighbors.add(states[3][2]);

            double[][] previous_utilities = new double[6][6];
            double[][] new_utilities = new double[6][6];

            while (!neighbors.isEmpty()) {
                State current = neighbors.remove();
                double max_val = Integer.MIN_VALUE;
                current.isMarked = true;

                for(String direction : directions.keySet())
                {
                    double expectedUtility = 0.0;
                    double stayProbability = 0.0;

                    //Check if we can move in that direction
                    if(current.actions.contains(direction))
                    {
                        //We can move
                        expectedUtility += 0.8 * getState(current, direction).utility;
                    }
                    else
                    {
                        //Stay with probability
                        stayProbability += 0.8;
                    }

                    //Check the same for right angle directions
                    for(String d : directions.get(direction))
                    {
                        //Check if we can move in that direction
                        if(current.actions.contains(d))
                        {
                            //We can move
                            expectedUtility += 0.1 * getState(current, d).utility;
                        }
                        else
                        {
                            //Stay with probability
                            stayProbability += 0.1;
                        }
                    }

                    expectedUtility += stayProbability * current.utility;

                    if (expectedUtility > max_val) {
                        max_val = expectedUtility;
                        current.action = direction;
                    }

                    if(current.actions.contains(direction) && getState(current,direction).isMarked == false)
                    {
                        neighbors.add(getState(current,direction));
                    }
                }

                previous_utilities[current.x][current.y] = Double.parseDouble(df.format(current.utility));
                new_utilities[current.x][current.y] = current.reward + gamma * max_val;
                new_utilities[current.x][current.y] = Double.parseDouble(df.format(new_utilities[current.x][current.y]));
            }

            //Un mark all states
            for (int i = 0; i < states.length; i++) {

                for (int j = 0; j < states[0].length; j++) {
                    states[i][j].isMarked = false;
                }
            }

            //Update new utilities
            ArrayList<Double> interimUtilities = new ArrayList<Double>();
            for (int i = 0; i < states.length; i++) {

                for (int j = 0; j < states[0].length; j++) {
                    states[i][j].utility = new_utilities[i][j];
                    interimUtilities.add(states[i][j].utility);
                }
            }

            utilityVals.add(interimUtilities);

            if(checkConvergence(previous_utilities))
            {
                break;
            }
        }

        System.out.println("Reached Convergence : " + count);

    }

    private void printGrid()
    {
        for(int i = 0; i < grid.length; i++)
        {
            for(int j = 0; j < grid[0].length; j++)
            {
                if(grid[i][j] == " ")
                    System.out.print("0" + "\t");
                else
                    System.out.print(grid[i][j] + "\t");
            }
            System.out.println();
        }
    }

    private void printPolicyMaps()
    {
        System.out.println();
        System.out.println("Policy Map  : \n");

        for(int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {

                if (grid[i][j].equals("w"))
                {
                    System.out.print("#" + "\t");
                }
                else if (states[i][j].action.equals("u")) {
                    System.out.print("\u2191" + "\t");
                } else if (states[i][j].action.equals("d")) {
                    System.out.print("\u2193" + "\t");
                } else if (states[i][j].action.equals("r")) {
                    System.out.print("\u2192" + "\t");
                } else if (states[i][j].action.equals("l")) {
                    System.out.print("\u2190" + "\t");
                }
            }

            System.out.println();
        }

        System.out.println();
        System.out.println();

    }

    private void printUtilities()
    {
        for (int i = 0; i < states.length; i++)
        {
            for (int j = 0; j < states[0].length; j++)
            {
                System.out.println("State (" + i + "," + j + ") : " + states[i][j].utility);// + "  Policy  :  " + states[i][j].action);
            }
        }
    }

    public State[][] returnUtility(){

        setDirections();
        getActions();
        valueIteration();

        return states;
    }

    public static void main(String[] args) {

        ValueIteration obj = new ValueIteration();

        obj.printGrid();
        obj.returnUtility();
        obj.printUtilities();
        obj.printPolicyMaps();
        PlotGraph graph = new PlotGraph();
        graph.plotValueIterationGraph(utilityVals, grid, cols);
    }
}
