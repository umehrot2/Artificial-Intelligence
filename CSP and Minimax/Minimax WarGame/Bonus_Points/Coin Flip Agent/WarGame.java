import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.text.DecimalFormat;
/**
 * JAI MATA DI
 */

class Board
{
    int[][] board;

    Board()
    {
        board = new int[6][6];
    }
}

class assignment
{
    char[][] assignment;
    assignment next = null;
    MoveType moveType;

    assignment()
    {
        assignment = new char[6][6];
        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 6; j++)
            {
                assignment[i][j] = ' ';
            }
        }
    }
}

enum MoveType
{
    DROP, BLITZ, SABOTAGE, SABOTAGE_FAILED
}

class Move implements Comparable
{
    MoveType type;
    int row;
    int col;

    Move(MoveType type, int row, int col)
    {
        this.type = type;
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        if (!(o instanceof Move)) {
            return false;
        }

        Move c = (Move) o;

        // Compare the data members and return accordingly
        return (this.type == c.type) && (this.row == c.row) && (this.col == c.col);
    }

    @Override
    public int compareTo(Object o) {
        Move m = (Move) o;
        return this.type.ordinal() - m.type.ordinal();
        //return m.type.ordinal() - this.type.ordinal();
    }
}

public class WarGame
{
    Board b;
    int nodes_expanded = 0;
    static int depth_max = 3;
    int max_depth = 3;
    static int depth_max_alpha_beta = 6;
    int max_depth_alpha_beta = 6;
    boolean applySort = false;
    static String fileName = "Keren.txt";
    DecimalFormat df=new DecimalFormat("0.00");

    public WarGame()
    {
        b = new Board();
    }

    public int getUtility(assignment a, char player, char opponent)
    {
        int blue_utility = 0;
        int green_utility = 0;
        for(int row = 0; row < a.assignment.length; row++)
        {
            for(int col = 0; col < a.assignment[0].length; col++)
            {
                if(a.assignment[row][col] == player)
                    blue_utility += b.board[row][col];//blue_utility += 1;
                else if(a.assignment[row][col] == opponent)
                    green_utility += b.board[row][col];//green_utility += 1;
            }
        }

        return blue_utility - green_utility;
    }

    public int getUtility(assignment a)
    {
        int blue_utility = 0;
        int green_utility = 0;
        for(int row = 0; row < a.assignment.length; row++)
        {
            for(int col = 0; col < a.assignment[0].length; col++)
            {
                if(a.assignment[row][col] == 'B')
                    blue_utility += b.board[row][col];//blue_utility += 1;
                else if(a.assignment[row][col] == 'G')
                    green_utility += b.board[row][col];//green_utility += 1;
            }
        }

        return blue_utility - green_utility;
    }

    public void printUtility(assignment a)
    {
        int blue_utility = 0;
        int green_utility = 0;
        for(int row = 0; row < a.assignment.length; row++)
        {
            for(int col = 0; col < a.assignment[0].length; col++)
            {
                if(a.assignment[row][col] == 'B')
                    blue_utility += b.board[row][col];//blue_utility += 1;
                else if(a.assignment[row][col] == 'G')
                    green_utility += b.board[row][col];//green_utility += 1;
            }
        }

        System.out.println("Blue: " + blue_utility);
        System.out.println("Green: " + green_utility);
        if(blue_utility > green_utility)
            System.out.println("Blue Wins!!");
        else if(green_utility > blue_utility)
            System.out.println("Green Wins!!");
        else
            System.out.println("Game Drawn!!");
    }

    public assignment applyMove(assignment a, Move m, char player)
    {
        char opponent = 'B';
        if(player == 'B')
            opponent = 'G';

        assignment as = new assignment();
        for(int row = 0; row < 6; row++)
        {
            for(int col = 0; col < 6; col++)
            {
                as.assignment[row][col] = a.assignment[row][col];
            }
        }

        if(m.type == MoveType.DROP)
        {
            as.assignment[m.row][m.col] = player;
        }
        else
        {
            as.assignment[m.row][m.col] = player;
            int i = m.row, j = m.col;

            if(i-1 >= 0 && as.assignment[i-1][j] == opponent)
            {
                as.assignment[i-1][j] = player;
            }

            //down
            if(i+1 < 6 && as.assignment[i+1][j] == opponent)
            {
                as.assignment[i+1][j] = player;
            }

            //left
            if(j-1 >= 0 && as.assignment[i][j-1] == opponent)
            {
                as.assignment[i][j-1] = player;
            }

            //right
            if(j+1 < 6 && as.assignment[i][j+1] == opponent)
            {
                as.assignment[i][j+1] = player;
            }
        }

        return as;
    }

    public int miniMax(assignment a, int depth, char player, char opponent)
    {
        if(depth == max_depth)
        {
            nodes_expanded++;
            return getUtility(a, player, opponent);
        }

        nodes_expanded++;
        if(depth % 2 == 0)
        {
            //Blue Moves (MAX player)
            ArrayList<Move> moves = findMoves(a, player);
            if(applySort)
                Collections.sort(moves);
            int utility = Integer.MIN_VALUE;
            for(int loop = 0; loop < moves.size(); loop++)
            {
                assignment new_assignment = applyMove(a,moves.get(loop),player);
                int result = miniMax(new_assignment, depth+1, player,opponent);
                if(utility < result)
                {
                    a.next = new_assignment;
                }
                utility = Math.max(utility, result);
                new_assignment = null;
            }

            return utility;
        }
        else
        {
            //Green Moves (MIN player)
            ArrayList<Move> moves = findMoves(a, opponent);
            if(applySort)
                Collections.sort(moves);
            int utility = Integer.MAX_VALUE;
            for(int loop = 0; loop < moves.size(); loop++)
            {
                assignment new_assignment = applyMove(a,moves.get(loop),opponent);

                int result = miniMax(new_assignment, depth+1, player, opponent);
                if(utility > result)
                {
                    a.next = new_assignment;
                }

                utility = Math.min(utility, result);
                new_assignment = null;
            }

            return utility;
        }

    }

    private ArrayList<Move> findMoves(assignment a, char player)
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        for(int i = 0; i < a.assignment.length; i++)
        {
            for(int j = 0; j < a.assignment[0].length; j++)
            {
                if(a.assignment[i][j] == ' ')
                {
                    boolean isBlitz = false;
                    //up
                    if(i-1 >= 0 && a.assignment[i-1][j] == player)
                    {
                        isBlitz = true;
                    }

                    //down
                    else if(i+1 < 6 && a.assignment[i+1][j] == player)
                    {
                        isBlitz = true;
                    }

                    //left
                    else if(j-1 >= 0 && a.assignment[i][j-1] == player)
                    {
                        isBlitz = true;
                    }

                    //right
                    else if(j+1 < 6 && a.assignment[i][j+1] == player)
                    {
                        isBlitz = true;
                    }

                    if(isBlitz)
                    {
                        //Flipping the Coin
                        RandomCollection<Integer> rc = new RandomCollection<Integer>();
                        rc.add(0.5, 1);
                        rc.add(0.5, 2);
                        int val = rc.next();
                        Move m;
                        if(val == 1)
                        {
                            //Go for the Blitz with probability of 0.5
                            m = new Move(MoveType.BLITZ,i,j);
                        }
                        else
                        {
                            //Blitz cannot happen, and hence you cannot convert. It will be just a para drop
                            m = new Move(MoveType.DROP, i, j);
                        }

                        if(!moves.contains(m))
                        {
                            moves.add(m);
                        }
                    }
                    else
                    {
                        Move m = new Move(MoveType.DROP,i,j);
                        if(!moves.contains(m))
                        {
                            moves.add(m);
                        }
                    }
                }
            }
        }

        return moves;
    }

    private int alpha_beta_search(assignment a, char player, char opponent)
    {
        int result = max_value(a, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, player, opponent);
        return result;
    }

    private int min_value(assignment a, int depth, int alpha, int beta, char player, char opponent)
    {
        if(depth == max_depth_alpha_beta)
        {
            nodes_expanded++;
            return getUtility(a, player, opponent);
        }

        int utility = Integer.MAX_VALUE;
        ArrayList<Move> moves = findMoves(a, opponent);
        if(applySort)
            Collections.sort(moves);
        nodes_expanded++;
        for(int loop = 0; loop < moves.size(); loop++)
        {
            //alpha_beta_expanded++;
            assignment new_assignment = applyMove(a,moves.get(loop),opponent);
            int result = max_value(new_assignment, depth+1, alpha, beta, player, opponent);
            if(utility > result)
            {
                a.next = new_assignment;
            }
            utility = Math.min(utility, result);
            if(utility <= alpha)
                return utility;

            beta = Math.min(utility, beta);
            new_assignment = null;
        }

        return utility;
    }

    private int max_value(assignment a, int depth, int alpha, int beta, char player, char opponent)
    {
        if(depth == max_depth_alpha_beta)
        {
            nodes_expanded++;
            return getUtility(a, player, opponent);
        }

        int utility = Integer.MIN_VALUE;
        ArrayList<Move> moves = findMoves(a, player);
        if(applySort)
            Collections.sort(moves);
        nodes_expanded++;
        for(int loop = 0; loop < moves.size(); loop++)
        {
            //alpha_beta_expanded++;
            assignment new_assignment = applyMove(a,moves.get(loop),player);
            int result = min_value(new_assignment, depth+1, alpha, beta, player, opponent);
            if(utility < result)
            {
                a.next = new_assignment;
            }
            utility = Math.max(utility, result);

            if(utility >= beta)
                return utility;

            alpha = Math.max(utility, alpha);
            new_assignment = null;
        }

        return utility;
    }

    public void readFile(String file_name)
    {
        try
        {
            FileReader reader = new FileReader(file_name);
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            int row = 0;
            while(line != null)
            {
                String values[] = line.split("\t");
                for(int loop = 0; loop < values.length; loop++)
                {
                    b.board[row][loop] = Integer.parseInt(values[loop]);
                }

                row++;
                line = br.readLine();
            }

            reader.close();
            br.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }

    public void displayBoard()
    {
        System.out.println("Board Game: ");
        for(int row = 0; row < b.board.length; row++)
        {
            for(int col = 0; col < b.board[0].length; col++)
            {
                System.out.print(b.board[row][col] + "\t");
            }

            System.out.println();
        }
    }

    public void printSolution(assignment a)
    {
        while(a != null)
        {
            for(int row = 0; row < 6; row++)
            {
                for(int col = 0; col < 6; col++)
                {
                    if(a.assignment[row][col] == ' ')
                        System.out.print("-" + "\t");
                    else
                        System.out.print(a.assignment[row][col] + "\t");
                }

                System.out.println();
            }

            a = a.next;
            System.out.println();
            System.out.println();
        }
    }

    public void minimax_vs_minimax(assignment a)
    {
        int nodes_expanded_player1 = 0;
        int nodes_expanded_player2 = 0;
        double average_time_taken_player1 = 0.0;
        double average_time_taken_player2 = 0.0;

        System.out.println("Output: ");
        for(int loop = 0; loop < 36; loop++)
        {

            if(loop % 2 == 0)
            {
                max_depth = depth_max;
                if(36 - loop < max_depth)
                {
                    max_depth = 36 - loop;
                }
                nodes_expanded = 0;
                long startTime = System.currentTimeMillis();
                miniMax(a, 0, 'B', 'G');
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                nodes_expanded_player1 += nodes_expanded;
                average_time_taken_player1 += elapsedTime;
            }
            else
            {
                max_depth = depth_max;
                if(36 - loop < max_depth)
                {
                    max_depth = 36 - loop;
                }
                nodes_expanded = 0;
                long startTime = System.currentTimeMillis();
                miniMax(a, 0, 'G', 'B');
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                nodes_expanded_player2 += nodes_expanded;
                average_time_taken_player2 += elapsedTime;
            }

            a = a.next;
        }


        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 6; j++)
            {
                System.out.print(a.assignment[i][j] + "\t");
            }
            System.out.println();
        }

        printUtility(a);
        System.out.println();


        int average_nodes_expanded = (nodes_expanded_player1 + nodes_expanded_player2) / 36;
        double average_time_taken = (average_time_taken_player1 + average_time_taken_player2) / 36;

        System.out.println("Nodes expanded by player 1 = " + nodes_expanded_player1);
        System.out.println("Nodes expanded by player 2 = " + nodes_expanded_player2);
        System.out.println("Average Nodes Expanded per move: " + average_nodes_expanded);
        System.out.println("Average Time taken per move in Milliseconds: " + df.format(average_time_taken));
    }

    private void alphabeta_vs_alphabeta(assignment a)
    {
        int nodes_expanded_player1 = 0;
        int nodes_expanded_player2 = 0;
        double average_time_taken_player1 = 0.0;
        double average_time_taken_player2 = 0.0;

        System.out.println("Output: ");
        for(int loop = 0; loop < 36; loop++)
        {

            if(loop % 2 == 0)
            {
                max_depth_alpha_beta = depth_max_alpha_beta;
                if(36 - loop < max_depth_alpha_beta)
                {
                    max_depth_alpha_beta = 36 - loop;
                }

                nodes_expanded = 0;
                long startTime = System.currentTimeMillis();
                alpha_beta_search(a, 'B', 'G');
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;

                nodes_expanded_player1 += nodes_expanded;
                average_time_taken_player1 += elapsedTime;
            }
            else
            {
                max_depth_alpha_beta = depth_max_alpha_beta;
                if(36 - loop < max_depth_alpha_beta)
                {
                    max_depth_alpha_beta = 36 - loop;
                }

                nodes_expanded = 0;
                long startTime = System.currentTimeMillis();
                alpha_beta_search(a, 'G', 'B');
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;

                nodes_expanded_player2 += nodes_expanded;
                average_time_taken_player2 += elapsedTime;
            }

            a = a.next;

        }

        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 6; j++)
            {
                System.out.print(a.assignment[i][j] + "\t");
            }
            System.out.println();
        }

        printUtility(a);
        System.out.println();

        int average_nodes_expanded = (nodes_expanded_player1 + nodes_expanded_player2) / 36;
        double average_time_taken = (average_time_taken_player1 + average_time_taken_player2) / 36;
        System.out.println("Nodes expanded by player 1 = " + nodes_expanded_player1);
        System.out.println("Nodes expanded by player 2 = " + nodes_expanded_player2);
        System.out.println("Average Nodes Expanded per move: " + average_nodes_expanded);
        System.out.println("Average Time taken per move in Milliseconds: " + df.format(average_time_taken));
    }

    private void minimax_vs_alphabeta(assignment a)
    {
        int nodes_expanded_player1 = 0;
        int nodes_expanded_player2 = 0;
        double average_time_taken_player1 = 0.0;
        double average_time_taken_player2 = 0.0;
        System.out.println("Output: ");

        for(int loop = 0; loop < 36; loop++)
        {
            if(loop % 2 == 0)
            {
                max_depth = depth_max;
                if(36 - loop < max_depth)
                {
                    max_depth = 36 - loop;
                }

                nodes_expanded = 0;
                long startTime = System.currentTimeMillis();
                miniMax(a, 0, 'B', 'G');
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                nodes_expanded_player1 += nodes_expanded;
                average_time_taken_player1 += elapsedTime;
            }
            else
            {
                max_depth_alpha_beta = depth_max_alpha_beta;
                if(36 - loop < max_depth_alpha_beta)
                {
                    max_depth_alpha_beta = 36 - loop;
                }

                nodes_expanded = 0;
                long startTime = System.currentTimeMillis();
                alpha_beta_search(a, 'G', 'B');
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;

                nodes_expanded_player2 += nodes_expanded;
                average_time_taken_player2 += elapsedTime;
            }

            a = a.next;

        }

        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 6; j++)
            {
                System.out.print(a.assignment[i][j] + "\t");
            }
            System.out.println();
        }

        printUtility(a);
        System.out.println();

        int average_nodes_expanded = (nodes_expanded_player1 + nodes_expanded_player2) / 36;
        double average_time_taken = (average_time_taken_player1 + average_time_taken_player2) / 36;

        System.out.println("Nodes expanded by player 1 = " + nodes_expanded_player1);
        System.out.println("Nodes expanded by player 2 = " + nodes_expanded_player2);
        System.out.println("Average Nodes Expanded per move: " + average_nodes_expanded);
        System.out.println("Average Time taken per move in Milliseconds: " + df.format(average_time_taken));
    }

    private void alphabeta_vs_minimax(assignment a)
    {
        int nodes_expanded_player1 = 0;
        int nodes_expanded_player2 = 0;
        double average_time_taken_player1 = 0.0;
        double average_time_taken_player2 = 0.0;

        System.out.println("Output: ");

        for(int loop = 0; loop < 36; loop++)
        {
            if(loop % 2 == 0)
            {
                max_depth_alpha_beta = depth_max_alpha_beta;
                if(36 - loop < max_depth_alpha_beta)
                {
                    max_depth_alpha_beta = 36 - loop;
                }

                nodes_expanded = 0;
                long startTime = System.currentTimeMillis();
                alpha_beta_search(a, 'B', 'G');
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;

                nodes_expanded_player1 += nodes_expanded;
                average_time_taken_player1 += elapsedTime;
            }
            else
            {
                max_depth = depth_max;
                if(36 - loop < max_depth)
                {
                    max_depth = 36 - loop;
                }

                nodes_expanded = 0;
                long startTime = System.currentTimeMillis();
                miniMax(a, 0, 'G', 'B');
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;

                nodes_expanded_player2 += nodes_expanded;
                average_time_taken_player2 += elapsedTime;
            }

            a = a.next;
        }

        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 6; j++)
            {
                System.out.print(a.assignment[i][j] + "\t");
            }
            System.out.println();
        }

        printUtility(a);
        System.out.println();

        int average_nodes_expanded = (nodes_expanded_player1 + nodes_expanded_player2) / 36;
        double average_time_taken = (average_time_taken_player1 + average_time_taken_player2) / 36;

        System.out.println("Nodes expanded by player 1 = " + nodes_expanded_player1);
        System.out.println("Nodes expanded by player 2 = " + nodes_expanded_player2);
        System.out.println("Average Nodes Expanded per move: " + average_nodes_expanded);
        System.out.println("Average Time taken per move in Milliseconds: " + df.format(average_time_taken));
    }


    public static void main(String[] args)
    {
        WarGame game = new WarGame();
        if(args.length != 0)
        {
            game.max_depth = Integer.parseInt(args[0]);
            depth_max = game.max_depth;
            game.max_depth_alpha_beta = Integer.parseInt(args[1]);
            depth_max_alpha_beta = game.max_depth_alpha_beta;
            fileName = args[2];
        }


        game.readFile(fileName);
        System.out.println("File: " + fileName);
        System.out.println("Minimax Depth: " + depth_max);
        System.out.println("Alpha Beta Minimax Depth: " + depth_max_alpha_beta);
        game.displayBoard();

        assignment a = new assignment();
        /*double avg_time = 0.0;
        for(int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis();
            System.out.println("\nMINIMAX vs. MINIMAX: ");
            game.minimax_vs_minimax(a);
            long stopTime = System.currentTimeMillis();
            System.out.println("Time taken in Milliseconds: " + (int) (stopTime - startTime));
            avg_time += (stopTime - startTime);
        }
        avg_time = avg_time / 10.0;
        System.out.println("Average Time : " + avg_time);*/
        System.out.println("\nMINIMAX vs. MINIMAX: ");
        game.minimax_vs_minimax(a);

        System.out.println("\nALPHA BETA vs. ALPHA BETA: ");
        game.alphabeta_vs_alphabeta(a);

        System.out.println("\nMINIMAX vs. ALPHA BETA: ");
        game.minimax_vs_alphabeta(a);

        System.out.println("\nALPHA BETA vs. MINIMAX: ");
        game.alphabeta_vs_minimax(a);
    }
}
