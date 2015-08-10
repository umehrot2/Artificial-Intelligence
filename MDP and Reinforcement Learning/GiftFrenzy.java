/**
 * Created by uditmehrotra on 30/04/15.
 */
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.io.BufferedReader;
import java.util.Random;

enum Gift
{
    yes, no
}

enum Money
{
    yes, no
}

class GFState
{
    int x;
    int y;
    HashMap<Gift, HashMap<Money, HashMap<Action, Double>>> q;
    HashMap<Gift, HashMap<Money, HashMap<Action, Double>>> previous_q;
    HashMap<Gift, HashMap<Money, HashMap<Action, Integer>>> n;
    ArrayList<Action> actions;
    HashMap<Gift, HashMap<Money, Action>> policy;
    Gift hasGift;
    Money hasMoney;
    double reward;

    public GFState(int x, int y)
    {
        this.x = x;
        this.y = y;
        q = new HashMap<Gift, HashMap<Money, HashMap<Action, Double>>>();
        previous_q = new HashMap<Gift, HashMap<Money, HashMap<Action, Double>>>();
        n = new HashMap<Gift, HashMap<Money, HashMap<Action, Integer>>>();

        for(Gift g : Gift.values())
        {
            q.put(g, new HashMap<Money, HashMap<Action, Double>>());
            previous_q.put(g, new HashMap<Money, HashMap<Action, Double>>());
            n.put(g, new HashMap<Money, HashMap<Action, Integer>>());
            for (Money m : Money.values())
            {
                q.get(g).put(m, new HashMap<Action, Double>());
                previous_q.get(g).put(m, new HashMap<Action, Double>());
                n.get(g).put(m, new HashMap<Action, Integer>());
                for(Action a : Action.values())
                {
                    if(!a.equals(Action.stay)) {
                        q.get(g).get(m).put(a, 0.0);
                        previous_q.get(g).get(m).put(a, 0.0);
                        n.get(g).get(m).put(a, 0);
                    }
                }
            }
        }

        actions = new ArrayList<Action>();
        policy = new HashMap<Gift, HashMap<Money, Action>>();
        hasGift = Gift.no;
        hasMoney = Money.no;
        reward = -0.1;
    }
}

public class GiftFrenzy
{
    private static GFState states[][];
    private static String[][] grid;
    private static final double gamma = 0.99; //0.95
    private static final double Ne = 100;
    private static int rows;
    private static int cols;
    DecimalFormat df = new DecimalFormat("#.######");
    HashMap<Action,HashSet<Action>> directions = new HashMap<Action, HashSet<Action>>();
    private static ArrayList<HashMap<GFState, Double>> utility_values = new ArrayList<HashMap<GFState, Double>>();
    private static final double e = 0.001;
    private static final int iterations = 5000;
    private static final double optimisticReward = 5.0;

    public GiftFrenzy()
    {
        //Initialize grid
        try
        {
            BufferedReader bf = new BufferedReader(new FileReader("environment.txt"));
            String line = null;
            int x = 0;
            ArrayList<String> lines = new ArrayList<String>();
            while((line = bf.readLine()) != null)
            {
                lines.add(line);
            }

            rows = lines.size();
            cols = lines.get(0).length();
            grid = new String[rows][cols];
            int row = 0;
            for(String l : lines)
            {
                for(int loop = 0; loop < l.length(); loop++)
                {
                    grid[row][loop] = Character.toString(l.charAt(loop));
                }
                row++;
            }

        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }


        //Initialize states
        states = new GFState[rows][cols];
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                states[i][j] = new GFState(i,j);
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
                if (grid[i][j].equals("%"))
                    continue;

                if (states[i][j].x - 1 >= 0 && !grid[states[i][j].x - 1][states[i][j].y].equals("%")) {
                    states[i][j].actions.add(Action.up);
                }

                //Go Down
                if (states[i][j].x + 1 < rows && !grid[states[i][j].x + 1][states[i][j].y].equals("%")) {
                    states[i][j].actions.add(Action.down);
                }

                //Go Left
                if (states[i][j].y - 1 >= 0 && !grid[states[i][j].x][states[i][j].y - 1].equals("%")) {
                    states[i][j].actions.add(Action.left);
                }

                //Go Right
                if (states[i][j].y + 1 < cols && !grid[states[i][j].x][states[i][j].y + 1].equals("%")) {
                    states[i][j].actions.add(Action.right);
                }

            }
        }
    }

    private void printGrid()
    {
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                    System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }

    private double f(double Q, double N)
    {
        if(N < Ne)
        {
            //Continue to Explore
            return 5.0;
//            return optimisticReward;
        }
        else
        {
            return Q;
        }
    }

    private Action getMaxAction(GFState state, Gift gift, Money money)
    {
        double max_val = Integer.MIN_VALUE;
        Action max_action = Action.up;

        ArrayList<Action> ac = new ArrayList<Action>();

        for(Action a : directions.keySet())
        {
            double Q = state.q.get(gift).get(money).get(a);
            double N = state.n.get(gift).get(money).get(a);

            double f_val = f(Q, N);

            if(f_val == 5.0)
            {
                ac.add(a);
            }

            if(f_val > max_val)
            {
                max_val = f_val;
                max_action = a;
            }
        }

        if(max_val == 5.0 && ac.size() > 1)
        {
            Random rand = new Random();
            int next = rand.nextInt(ac.size());
            max_action = ac.get(next);
        }

        return max_action;
    }

    private GFState getState(GFState current, Action a)
    {
        GFState newState;
        if(a.equals(Action.up))
        {
            newState = states[current.x - 1][current.y];
        }
        else if(a.equals(Action.down))
        {
            newState = states[current.x + 1][current.y];
        }
        else if(a.equals(Action.left))
        {
            newState = states[current.x][current.y - 1];
        }
        else if(a.equals(Action.right))
        {
            newState = states[current.x][current.y + 1];
        }
        else
        {
            newState = current;
        }

        String pos = grid[newState.x][newState.y];

        if(pos.equals("B"))
        {
            //Reached Bank
            if(current.hasMoney.equals(Money.no))
            {
                newState.hasMoney = Money.yes;
                newState.hasGift = current.hasGift;
            }
            else
            {
                newState.hasMoney = current.hasMoney;
                newState.hasGift = current.hasGift;
            }
        }
        else if(pos.equals("S"))
        {
            //Reached Store
            if(current.hasGift.equals(Gift.no) && current.hasMoney.equals(Money.yes))
            {
                //Buy gift
                newState.hasMoney = Money.no;
                newState.hasGift = Gift.yes;
            }
            else
            {
                newState.hasMoney = current.hasMoney;
                newState.hasGift = current.hasGift;
            }
        }
        else if(pos.equals("F"))
        {
            if(current.hasGift.equals(Gift.yes))
            {
                newState.hasMoney = current.hasMoney;
                newState.hasGift = Gift.no;
            }
            else
            {
                newState.hasMoney = current.hasMoney;
                newState.hasGift = current.hasGift;
            }
        }
        else
        {
            newState.hasMoney = current.hasMoney;
            newState.hasGift = current.hasGift;
        }

        return newState;
    }

    private GFState getNextState(GFState state, Action a)
    {
        double stayProbability = 0.0;
        RandomCollection<Action> randomCollection = new RandomCollection<Action>();

        if(state.hasGift == Gift.no)
        {
            //Not Carrying a gift

            //Check if we can move in that direction
            if(state.actions.contains(a))
            {
                //We can move
                randomCollection.add(0.9, a);
            }
            else
            {
                //Stay with probability
                stayProbability += 0.9;
            }
        }
        else
        {
            //Carrying a gift

            if(grid[state.x][state.y].equals("S"))
            {
                //Visiting a Store with the gift
                stayProbability = 0.3;
                if(state.actions.contains(a))
                {
                    //We can move
                    randomCollection.add(0.6, a);
                }
                else
                {
                    //Stay with probability
                    stayProbability += 0.6;
                }
            }
            else
            {
                //Not visiting a Store with Gift
                stayProbability = 0.1;
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
            }
        }

        //Check the same for right angle directions
        for(Action d : directions.get(a))
        {
            //Check if we can move in that direction
            if(state.actions.contains(d))
            {
                //We can move
                randomCollection.add(0.05, d);
            }
            else
            {
                //Stay with probability
                stayProbability += 0.05;
            }
        }

        if(stayProbability > 0.0)
        {
            randomCollection.add(stayProbability, Action.stay);
        }

        return getState(state, randomCollection.next());
    }

    private double getMaxQ(GFState state)
    {
        double max_val = Integer.MIN_VALUE;

        for(Action ac : directions.keySet())
        {
            double q_val = state.q.get(state.hasGift).get(state.hasMoney).get(ac);

            if(q_val > max_val)
                max_val = q_val;
        }

        return max_val;
    }

    private double getReward(GFState state)
    {
        String pos = grid[state.x][state.y];

        if(pos.equals("F") && state.hasGift.equals(Gift.yes))
        {
            return 5.0;
        }

        return -0.1;
    }

    private void copyPreviousQ()
    {
        for(int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                for(Gift g : states[i][j].q.keySet())
                {
                    for(Money m : states[i][j].q.get(g).keySet())
                    {
                        for(Action a : states[i][j].q.get(g).get(m).keySet())
                        {
                            states[i][j].previous_q.get(g).get(m).put(a, states[i][j].q.get(g).get(m).get(a));
                        }
                    }
                }
            }
        }
    }

    private boolean hasConverged()
    {
        double delta = Integer.MIN_VALUE;

        for(int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                for(Gift g : states[i][j].q.keySet())
                {
                    for(Money m : states[i][j].q.get(g).keySet())
                    {
                        for(Action a : states[i][j].q.get(g).get(m).keySet())
                        {
                            double diff = Math.abs(states[i][j].q.get(g).get(m).get(a) - states[i][j].previous_q.get(g).get(m).get(a));

                            if(diff > delta)
                            {
                                delta = diff;
                            }
                        }
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

    private void printPolicy()
    {
        for(int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if(!grid[i][j].equals("%"))
                {
                    for (Gift g : states[i][j].q.keySet()) {
                        for (Money m : states[i][j].q.get(g).keySet()) {
                            double max_val = Integer.MIN_VALUE;
                            Action max_action = Action.up;
                            for (Action a : states[i][j].q.get(g).get(m).keySet()) {
                                double val = states[i][j].q.get(g).get(m).get(a);
                                if (max_val < val) {
                                    max_val = val;
                                    max_action = a;
                                }
                            }

                            System.out.println("( " + i + " , " + j + " , " + g + " , " + m + " , " + " , " + max_val + " , " + max_action + " )");
                        }
                    }
                }
            }
        }
    }


    private void compute()
    {

        int i = 0;
        int previous_n = 1;
        while(true)
        //while(i <= 15000)
        {
            GFState current;

            if(i == 0)
            {
                //Start at bank with no gift and money
                current = states[6][2];
                current.hasGift = Gift.no;
                current.hasMoney = Money.yes;
            }
            else
            {
                Random rand = new Random();
                int r = rand.nextInt(rows);
                int c = rand.nextInt(cols);

                if(grid[r][c].equals("%"))
                    continue;

                current = states[r][c];

                int gift = rand.nextInt(2);
                current.hasGift = Gift.values()[gift];

                int money = rand.nextInt(2);
                current.hasMoney = Money.values()[money];

            }

//            for (int loop = 1; loop <= 20000; loop++)
            for (int loop = 1; loop <= iterations; loop++)
            {
//                double alpha = 180.0 / (59 + loop) * 1.0;
//                int t = i * iterations + loop;
                double alpha = 60.0 / (59 + loop) * 1.0;
//                double alpha = 60.0 / (59 + previous_n) * 1.0;

                //Randomly generate action for start state
                Action a = Action.up;

                if (loop == 1)
                {
                    Random rand = new Random();
                    int action_num = rand.nextInt(4);
                    a = Action.values()[action_num];
                }
                else
                {
                    //Get max action
                    a = getMaxAction(current, current.hasGift, current.hasMoney);
                }

                //Get successor state
                GFState newState = getNextState(current, a);

                //Get Reward
                double reward = getReward(current);

                //Get max Q value of next state
                double maxQ = getMaxQ(newState);

                //Update the N(current state, action) value
                int n_val = current.n.get(current.hasGift).get(current.hasMoney).get(a);
                current.n.get(current.hasGift).get(current.hasMoney).put(a, n_val+1);

                //Update Q value
                double currentQ = current.q.get(current.hasGift).get(current.hasMoney).get(a);
                currentQ += alpha * (reward + (gamma * maxQ) - currentQ);
                current.q.get(current.hasGift).get(current.hasMoney).put(a, currentQ);

                //Change current state to new state
//                previous_n = current.n.get(current.hasGift).get(current.hasMoney).get(a);
                current = newState;

            }

            saveUtilityValues();

            if (hasConverged())
                break;

            //Copy Previous Q values
            copyPreviousQ();

            System.out.println("Iteration : " + i);

            i++;
        }

        System.out.println("No. of Iterations : " + i);

    }

    private void saveUtilityValues()
    {
        HashMap<GFState, Double> intermediate_values = new HashMap<GFState, Double>();
        for(int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++) {
                for (Gift g : states[i][j].q.keySet())
                {
                    for (Money m : states[i][j].q.get(g).keySet())
                    {
                        states[i][j].hasGift = g;
                        states[i][j].hasMoney = m;

                        intermediate_values.put(states[i][j], getMaxQ(states[i][j]));
                    }
                }
            }
        }

        utility_values.add(intermediate_values);
    }

    private void printPolicyMaps()
    {
        System.out.println();
        System.out.println();

        for(Gift g : Gift.values())
        {
            for(Money m : Money.values())
            {
                System.out.println("Policy for Gift -> " + g + " , Money -> " + m + " : ");
                System.out.println();
                for(int i = 0; i < rows; i++)
                {
                    for (int j = 0; j < cols; j++)
                    {
                        double max_val = Integer.MIN_VALUE;
                        Action max_action = Action.up;

                        for (Action a : states[i][j].q.get(g).get(m).keySet())
                        {
                            double val = states[i][j].q.get(g).get(m).get(a);
                            if (max_val < val) {
                                max_val = val;
                                max_action = a;
                            }
                        }

                        if(grid[i][j].equals("%"))
                        {
                            System.out.print("#" + "\t");
                        }
                        else if(max_action.equals(Action.up))
                        {
                            System.out.print("\u2191" + "\t");
                        }
                        else if(max_action.equals(Action.down))
                        {
                            System.out.print("\u2193" + "\t");
                        }
                        else if(max_action.equals(Action.right))
                        {
                            System.out.print("\u2192" + "\t");
                        }
                        else if(max_action.equals(Action.left))
                        {
                            System.out.print("\u2190" + "\t");
                        }
                    }

                    System.out.println();
                }

                System.out.println();
                System.out.println();
            }
        }
    }

    public static void main(String[] args)
    {
        GiftFrenzy obj = new GiftFrenzy();
        obj.printGrid();
        obj.setDirections();
        obj.getActions();
        obj.compute();
        obj.printPolicy();
        obj.printPolicyMaps();

        //PlotGraph graph = new PlotGraph();
        //graph.plotGiftFrenzyGraph(utility_values, grid);
    }
}
