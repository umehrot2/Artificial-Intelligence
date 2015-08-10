import org.math.plot.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

/**
 * Created by uditmehrotra on 28/04/15.
 */
enum Action
{
    up, down, right, left, stay
}

class RLState
{
    int x;
    int y;
    HashMap<Action, Double> q;
    HashMap<Action, Double> previous_q;
    HashMap<Action, Integer> n;
    double reward;
    ArrayList<Action> actions = new ArrayList<Action>();
    Action policy;

    public RLState(int x, int y)
    {
        this.x = x;
        this.y = y;

        q = new HashMap<Action, Double>();
        q.put(Action.up, 0.0);
        q.put(Action.down, 0.0);
        q.put(Action.right, 0.0);
        q.put(Action.left, 0.0);
        
        previous_q = new HashMap<Action, Double>();
        previous_q.put(Action.up, 0.0);
        previous_q.put(Action.down, 0.0);
        previous_q.put(Action.right, 0.0);
        previous_q.put(Action.left, 0.0);

        n = new HashMap<Action, Integer>();
        n.put(Action.up, 0);
        n.put(Action.down, 0);
        n.put(Action.right, 0);
        n.put(Action.left, 0);

        actions = new ArrayList<Action>();
        policy = Action.up;

        reward = 0.0;
    }
}

public class QLearning
{
    private static RLState states[][] = new RLState[6][6];
    private static String[][] grid = new String[6][6];
    private static final double gamma = 0.99;
    private static final double Ne = 100;
    private static final int rows = 6;
    private static final int cols = 6;
    public static State mds[][] = new State[6][6];
    DecimalFormat df = new DecimalFormat("#.######");
    HashMap<Action,HashSet<Action>> directions = new HashMap<Action, HashSet<Action>>();
    private static final double e = 0.1;
    public static ArrayList<ArrayList<Double>> utilityVals = new ArrayList<ArrayList<Double>>();
    public static ArrayList<Double> rmseToPlot = new ArrayList<Double>();

    public QLearning()
    {
        //Initialize grid
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
            {
                grid[i][j] = " ";
            }
        }
        grid[0][0] = grid[0][2] = grid[0][5] = grid[1][3] = grid[2][4] = grid[3][5] = "1";
        grid[1][1] = grid[1][5] = grid[2][2] = grid[3][3] = grid[4][4] = "-1";
        grid[0][1] = grid[1][4] = grid[4][1] = grid[4][2] = grid[4][3] = "w";
        grid[3][2] = "s";


        //Initialize utility values
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                states[i][j] = new RLState(i,j);
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
        directions.put(Action.right, new HashSet<Action>());
        directions.get(Action.right).add(Action.up);
        directions.get(Action.right).add(Action.down);
        directions.put(Action.up, new HashSet<Action>());
        directions.get(Action.up).add(Action.left);
        directions.get(Action.up).add(Action.right);
        directions.put(Action.down, new HashSet<Action>());
        directions.get(Action.down).add(Action.left);
        directions.get(Action.down).add(Action.right);
        directions.put(Action.left, new HashSet<Action>());
        directions.get(Action.left).add(Action.up);
        directions.get(Action.left).add(Action.down);
    }

    private void getActions() {
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[0].length; j++) {
                if (grid[i][j].equals("w"))
                    continue;

                if (states[i][j].x - 1 >= 0 && !grid[states[i][j].x - 1][states[i][j].y].equals("w")) {
                    states[i][j].actions.add(Action.up);
                }

                //Go Down
                if (states[i][j].x + 1 < rows && !grid[states[i][j].x + 1][states[i][j].y].equals("w")) {
                    states[i][j].actions.add(Action.down);
                }

                //Go Left
                if (states[i][j].y - 1 >= 0 && !grid[states[i][j].x][states[i][j].y - 1].equals("w")) {
                    states[i][j].actions.add(Action.left);
                }

                //Go Right
                if (states[i][j].y + 1 < cols && !grid[states[i][j].x][states[i][j].y + 1].equals("w")) {
                    states[i][j].actions.add(Action.right);
                }

            }
        }
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
    
    private double f(double Q, double N)
    {
        if(N < Ne)
        {
            //Continue to Explore
            return 1.0;
        }
        else
        {
            return Q;
        }
    }
    
    private Action getMaxAction(RLState state)
    {
        double max_val = Integer.MIN_VALUE;
        Action max_action = Action.up;

        for(Action a : directions.keySet())
        {
            double Q = state.q.get(a);
            double N = state.n.get(a);

            double f_val = f(Q, N);
            if(f_val > max_val)
            {
                max_val = f_val;
                max_action = a;
            }
        }

        return max_action;
    }

    private RLState getState(RLState current, Action a)
    {
        if(a.equals(Action.up))
        {
            return states[current.x - 1][current.y];
        }
        else if(a.equals(Action.down))
        {
            return states[current.x + 1][current.y];
        }
        else if(a.equals(Action.left))
        {
            return states[current.x][current.y - 1];
        }
        else if(a.equals(Action.right))
        {
            return states[current.x][current.y + 1];
        }
        else
        {
            return current;
        }
    }

    private RLState getNextState(RLState state, Action a)
    {
        double stayProbability = 0.0;
        RandomCollection<Action> randomCollection = new RandomCollection<Action>();

        //Check if we can move in that direction
        if(state.actions.contains(a))
        {
            //We can move
            randomCollection.add(0.8, a);
        }
        else
        {
            //Stay with probability
            stayProbability += 0.8;
        }

        //Check the same for right angle directions
        for(Action d : directions.get(a))
        {
            //Check if we can move in that direction
            if(state.actions.contains(d))
            {
                //We can move
                randomCollection.add(0.1, d);
            }
            else
            {
                //Stay with probability
                stayProbability += 0.1;
            }
        }

        if(stayProbability > 0.0)
        {
            randomCollection.add(stayProbability, Action.stay);
        }

        return getState(state, randomCollection.next());
    }

    private double getMaxQ(RLState state)
    {
        double max_val = Integer.MIN_VALUE;

        for(Action ac : directions.keySet())
        {
            double q_val = state.q.get(ac);

            if(q_val > max_val)
                 max_val = q_val;
        }

        return max_val;
    }

    private double getMaxPrevQ(RLState state)
    {
        double max_val = Integer.MIN_VALUE;

        for(Action ac : directions.keySet())
        {
            double q_val = state.previous_q.get(ac);

            if(q_val > max_val)
                max_val = q_val;
        }

        return max_val;
    }

    private boolean checkConvergence(double[][] previous_utilities)
    {

        double delta = Integer.MIN_VALUE;
        for(int i = 0; i < states.length; i++)
        {
            for (int j = 0; j < states[0].length; j++)
            {
                for(Action a : states[i][j].q.keySet())
                {
                    double diff = states[i][j].q.get(a) - previous_utilities[i][j];
                    if (diff > delta) {
                        delta = diff;
                    }
                }
            }
        }

        double val = e * (1-gamma) / gamma;

        if(delta < val)
            return true;
        else
            return false;
    }

    private void compute()
    {
//        for(int i =0; i < 100; i++)
        int i = 0;
//        int previous_n = 1;
        while (true)
        {
            RLState current;

            if(i == 0) {
                current = states[3][2];
            }
            else
            {
                Random rand = new Random();
                int action_num = rand.nextInt(36);
                int r = action_num / 6;
                int c = action_num % 6;
                current = states[r][c];
                if(grid[r][c].equals("w"))
                    continue;
            }


            for (int loop = 1; loop <= 2000; loop++)
            {
                double alpha = 60.0 / (59 + loop) * 1.0;
//                double alpha = 60.0 / (59 + previous_n) * 1.0;

                Action a = Action.up;

                if (loop == 1) {
                    //Randomly generate action for start state
                    Random rand = new Random();
                    int action_num = rand.nextInt(4);
                    a = Action.values()[action_num];
                } else {
                    //Get action considering exploration-exploitation trade-off
                    a = getMaxAction(current);
                }

                //Get successor state based on randomness
                RLState newState = getNextState(current, a);

                //Get maximum utility value possible from new state
                double maxQ_newState = getMaxQ(newState);

                //Update the N(current state, action) value
                int n_val = current.n.get(a);
                current.n.put(a, n_val + 1);

//                previous_n = n_val + 1;

                //Update the Q(current state, action)
                double q_val = current.q.get(a);
                q_val += alpha * (current.reward + (gamma * maxQ_newState) - q_val);
                current.q.put(a, q_val);

                //Change current state to new state
                current = newState;
            }

            i++;

            //RMSE values to plot
            rmseToPlot.add(getRMSE());

            // add to array list for plotting
            ArrayList<Double> interimUtilities = new ArrayList<Double>();

            for(int ij = 0; ij < states.length; ij++)
            {
                for (int ji = 0; ji < states[0].length; ji++)
                {
                    interimUtilities.add(getMaxQ(states[ij][ji]));
                }
            }

            utilityVals.add(interimUtilities);

            if(hasConverged())
                break;

            //Copy current q values to previous q values
            for(int x = 0; x < states.length; x++) {
                for (int y = 0; y < states[0].length; y++) {
                    for(Action a : states[x][y].q.keySet())
                    {
                        states[x][y].previous_q.put(a, states[x][y].q.get(a));
                    }
                }
            }

            printRMS();
        }
        System.out.println("Total number of iterations is "+i);
    }

    private void printPolicy()
    {
        int count = 0;
        for(int i = 0; i < states.length; i++)
        {
            for (int j = 0; j < states[0].length; j++)
            {
                double max_val = Integer.MIN_VALUE;
                for (Action a : states[i][j].q.keySet()) {
                    if (states[i][j].q.get(a) > max_val) {
                        max_val = states[i][j].q.get(a);
                        states[i][j].policy = a;
                    }
                }

                System.out.println("State : " + i + " , " + j + "   Max Utility :  " + max_val + "   Policy  :  " + states[i][j].policy + "  MDP Policy : " + mds[i][j].action);

                if(mds[i][j].action != null) {
                    if (states[i][j].policy.toString().charAt(0) != mds[i][j].action.charAt(0))
                        count++;
                }
            }
        }

        System.out.println("Wrong policies : " + count);
    }

    private boolean hasConverged()
    {
        double delta = Integer.MIN_VALUE;
        double rmse = 0.0;
        for(int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                for(Action a : states[i][j].q.keySet())
                {
                    double diff = Math.abs(states[i][j].q.get(a) - states[i][j].previous_q.get(a));

                    //double diff = Math.abs(getMaxQ(states[i][j]) - getMaxPrevQ(states[i][j]));
                    //rmse += Math.pow(Math.abs(getMaxQ(states[i][j]) - getMaxPrevQ(states[i][j])),2);
                    if(diff > delta)
                    {
                        delta = diff;
                    }
                }
            }
        }

        double val = e * (1-gamma) / gamma;
        //rmse = Math.sqrt(rmse/36.0);

        //System.out.println("RMSE Error : " + rmse);
        if(delta < val)
            return true;
        else
            return false;
    }

    private double getRMSE()
    {
        double mdpUtility = 0.0;

        for(int i = 0; i < states.length; i++)
        {
            for (int j = 0; j < states[0].length; j++)
            {
                double max_val = Integer.MIN_VALUE;
                for (Action a : states[i][j].q.keySet()) {
                    if (states[i][j].q.get(a) > max_val) {
                        max_val = states[i][j].q.get(a);
                        states[i][j].policy = a;
                    }
                }

                //calculate RMS error
                double sqauredDiff  = Math.pow(max_val - mds[i][j].utility,2);
                mdpUtility += sqauredDiff;
            }
        }

        double rmsError = Math.sqrt(mdpUtility/(36.0));
        return rmsError;
    }

    private void printRMS()
    {
        double mdpUtility = 0.0;

        for(int i = 0; i < states.length; i++)
        {
            for (int j = 0; j < states[0].length; j++)
            {
                double max_val = Integer.MIN_VALUE;
                for (Action a : states[i][j].q.keySet()) {
                    if (states[i][j].q.get(a) > max_val) {
                        max_val = states[i][j].q.get(a);
                        states[i][j].policy = a;
                    }
                }
                //calculate RMS error
                double sqauredDiff  = Math.pow(max_val - mds[i][j].utility,2);
                mdpUtility += sqauredDiff;

//                System.out.println("State : " + i + " , " + j + "   Max Utility :  " + max_val + "   Policy  :  " + states[i][j].policy);
            }
        }
        double rmsError = Math.sqrt(mdpUtility/(36.0));
        System.out.println("Rms error is " + rmsError);

        /*double val = e * (1-gamma) / gamma;

        if(rmsError < 1.0)
            return true;
        else
            return false;*/
    }

    private void printPolicyMaps()
    {
        System.out.println();
        System.out.println("Policy Map  : \n");

        for(int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                double max_val = Integer.MIN_VALUE;
                Action max_action = Action.up;

                for (Action a : states[i][j].q.keySet())
                {
                    double val = states[i][j].q.get(a);
                    if (max_val < val) {
                        max_val = val;
                        max_action = a;
                    }
                }

                if (grid[i][j].equals("w"))
                {
                    System.out.print("#" + "\t");
                }
                else if (max_action.equals(Action.up)) {
                    System.out.print("\u2191" + "\t");
                } else if (max_action.equals(Action.down)) {
                    System.out.print("\u2193" + "\t");
                } else if (max_action.equals(Action.right)) {
                    System.out.print("\u2192" + "\t");
                } else if (max_action.equals(Action.left)) {
                    System.out.print("\u2190" + "\t");
                }
            }

            System.out.println();
        }

        System.out.println();
        System.out.println();

    }

    public static void main(String[] args)
    {
        QLearning obj = new QLearning();
        ValueIteration mdp = new ValueIteration();

        mds = mdp.returnUtility();
        obj.setDirections();
        obj.printGrid();
        obj.getActions();
        obj.compute();
        obj.printPolicy();
        obj.printPolicyMaps();
        PlotGraph graph = new PlotGraph();
        graph.plotQLGraph(utilityVals, grid, cols);
        graph.plotQLRMSE(rmseToPlot);
        //obj.plotGraph();
        // obj.plotRMSE();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive

    }

}
