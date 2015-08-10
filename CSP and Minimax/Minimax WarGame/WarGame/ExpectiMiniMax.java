import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Jai Mata Di
 */
public class ExpectiMiniMax
{
    Board b;
    int nodes_expanded = 0;
    int depth_max = 3;
    int max_depth = 3;
    double gamma = 1.0;

    public ExpectiMiniMax()
    {
        b = new Board();
    }

    //Returns the utility/score of player given the current state
    private int getUtility(assignment a, char player)
    {
        int player_utility = 0;
        for(int row = 0; row < a.assignment.length; row++)
        {
            for(int col = 0; col < a.assignment[0].length; col++)
            {
                if(a.assignment[row][col] == player)
                    player_utility += b.board[row][col];
            }
        }

        return player_utility;
    }

    //Returns the utility (evaluation function) as difference of scores of main player and opponent
    private int getUtility(assignment a, char player, char opponent)
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

    //Prints the state of the final state of board, and the result
    private void printUtility(assignment a)
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

        System.out.println("Blue : " + blue_utility);
        System.out.println("Green : " + green_utility);
    }

    //Finds all possible moves for a player from the current state of board
    private ArrayList<Move> findMoves(assignment a, char player)
    {
        char opponent = 'G';
        if(player == 'G')
        {
            opponent = 'B';
        }

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
                        Move m = new Move(MoveType.BLITZ,i,j);
                        if(!moves.contains(m))
                        {
                            moves.add(m);
                        }
                    }
                    else
                    {
                        boolean isSabotage = false;
                        //Checks if move is a Sabotage
                        //up
                        if(i-1 >= 0 && a.assignment[i-1][j] == opponent)
                        {
                            isSabotage = true;
                        }

                        //down
                        else if(i+1 < 6 && a.assignment[i+1][j] == opponent)
                        {
                            isSabotage = true;
                        }

                        //left
                        else if(j-1 >= 0 && a.assignment[i][j-1] == opponent)
                        {
                            isSabotage = true;
                        }

                        //right
                        else if(j+1 < 6 && a.assignment[i][j+1] == opponent)
                        {
                            isSabotage = true;
                        }

                        Move m;

                        if(isSabotage) {
                            m = new Move(MoveType.SABOTAGE, i, j);
                            Move m1 = new Move(MoveType.DROP, i, j);
                            if(!moves.contains(m1))
                            {
                                moves.add(m1);
                            }
                        }
                        else
                        {
                            m = new Move(MoveType.DROP, i, j);
                        }

                        if(!moves.contains(m))
                        {
                            moves.add(m);
                        }
                    }
                }
            }
        }

        /*System.out.println("Moves : ");
        for(int i = 0; i < moves.size(); i++)
        {
            System.out.println(moves.get(i).type + " , " + moves.get(i).row + " , " + moves.get(i).col);
        }*/

        return moves;
    }

    //Applies a Sabotage move on the current state of the board for a given player
    private assignment applySabotage(assignment a, Move m, char player, boolean isPass)
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

        if(isPass)
        {
            as.assignment[m.row][m.col] = player;
            int i = m.row, j = m.col;

            if (i - 1 >= 0 && as.assignment[i - 1][j] == opponent) {
                as.assignment[i - 1][j] = player;
            }

            //down
            if (i + 1 < 6 && as.assignment[i + 1][j] == opponent) {
                as.assignment[i + 1][j] = player;
            }

            //left
            if (j - 1 >= 0 && as.assignment[i][j - 1] == opponent) {
                as.assignment[i][j - 1] = player;
            }

            //right
            if (j + 1 < 6 && as.assignment[i][j + 1] == opponent) {
                as.assignment[i][j + 1] = player;
            }
        }
        else
        {
            as.assignment[m.row][m.col] = opponent;
        }

        return as;
    }

    //Applies a move on the current state of the board for a given player
    private assignment applyMove(assignment a, Move m, char player)
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
        else if(m.type == MoveType.BLITZ)
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
    
    //Main mini max logic
    private double miniMax(assignment a, int depth, char player, char opponent)
    {
        if(depth == max_depth)
        {
            //Cut off at max depth
            return getUtility(a, player, opponent);
        }

        if(depth % 2 == 0)
        {
            //MAX player
            ArrayList<Move> moves = findMoves(a, player);
            double utility = Integer.MIN_VALUE;
            for(int loop = 0; loop < moves.size(); loop++)
            {
                assignment new_assignment;
                double result = 0;
                if (moves.get(loop).type != MoveType.SABOTAGE)
                {
                    //If not a sabotage move
                    new_assignment = applyMove(a, moves.get(loop), player);
                    result = miniMax(new_assignment, depth + 1, player, opponent);

                    if (utility < result)
                    {
                        a.next = new_assignment;
                        a.next.moveType = moves.get(loop).type;
                    }
                }
                else
                {
                    //Sabotage move - Run minimax for both sabotage success and fail state
                    assignment new_assignment1 = applySabotage(a, moves.get(loop), player, true);
                    double result1 = miniMax(new_assignment1, depth + 1, player, opponent);
                    new_assignment = null;
                    assignment new_assignment2 = applySabotage(a, moves.get(loop), player, false);
                    double result2 = miniMax(new_assignment2, depth + 1, player, opponent);
                    result = gamma * result1 + (1 - gamma) * result2;

                    if(utility < result)
                    {
                        //Choose success with gamma probability and fail state with 1-gamma
                        RandomCollection<Integer> rc = new RandomCollection<Integer>();
                        rc.add(gamma, 1);
                        rc.add(1-gamma, 2);

                        int val = rc.next();
                        if(val == 1) {
                            a.next = new_assignment1;
                            a.next.moveType = MoveType.SABOTAGE;
                        }
                        else if(val == 2) {
                            a.next = new_assignment2;
                            a.next.moveType = MoveType.SABOTAGE_FAILED;
                        }


                    }
                }

                utility = Math.max(utility, result);
                new_assignment = null;
                nodes_expanded++;
            }
            return utility;
        }
        else
        {
            //MIN player
            ArrayList<Move> moves = findMoves(a, opponent);
            double utility = Integer.MAX_VALUE;

            for(int loop = 0; loop < moves.size(); loop++)
            {
                assignment new_assignment;

                double result = 0;
                if (moves.get(loop).type != MoveType.SABOTAGE)
                {
                    //If not a sabotage move
                    new_assignment = applyMove(a, moves.get(loop), opponent);
                    result = miniMax(new_assignment, depth + 1, player, opponent);

                    if(utility > result)
                    {
                        a.next = new_assignment;
                        a.next.moveType = moves.get(loop).type;
                    }
                }
                else
                {
                    //Sabotage move - Run minimax for both sabotage success and fail state
                    assignment new_assignment1 = applySabotage(a, moves.get(loop), opponent, true);
                    double result1 = miniMax(new_assignment1, depth + 1, player, opponent);
                    new_assignment = null;
                    assignment new_assignment2 = applySabotage(a, moves.get(loop), opponent, false);
                    double result2 = miniMax(new_assignment2, depth + 1, player, opponent);
                    result = gamma * result1 + (1 - gamma) * result2;

                    if(utility > result)
                    {
                        //Choose success with gamma probability and fail state with 1-gamma
                        RandomCollection<Integer> rc = new RandomCollection<Integer>();
                        rc.add(gamma, 1);
                        rc.add(1-gamma, 2);

                        int val = rc.next();
                        if(val == 1) {
                            a.next = new_assignment1;
                            a.next.moveType = MoveType.SABOTAGE;
                        }
                        else if(val == 2) {
                            a.next = new_assignment2;
                            a.next.moveType = MoveType.SABOTAGE_FAILED;
                        }


                    }
                }

                utility = Math.min(utility, result);
                new_assignment = null;
                nodes_expanded++;
            }

            return utility;
        }
    }

    //Read and store the board from the file
    private void readFile(String file_name)
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

    //Print the board
    private void displayBoard()
    {
        System.out.println("Board Game : ");
        for(int row = 0; row < b.board.length; row++)
        {
            for(int col = 0; col < b.board[0].length; col++)
            {
                System.out.print(b.board[row][col] + "\t");
            }

            System.out.println();
        }
    }

    private void printSolution(assignment a)
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

    //Code for expectiminimax vs expectiminimax
    private ArrayList<Integer> expectiminimax_vs_expectiminimax(assignment a)
    {
        System.out.println("Output : ");
        System.out.println();
        int sabotage_success = 0;
        int sabotage_fail = 0;
        for(int loop = 0; loop < 36; loop++)
        {
            assignment new_as = new assignment();
            max_depth = depth_max;
            if(36 - loop < max_depth)
            {
                max_depth = 36 - loop;
            }

            if(loop % 2 == 0)
            {
                miniMax(a, 0, 'B', 'G');
            }
            else
            {
                miniMax(a, 0, 'G', 'B');
            }

            a = a.next;
            System.out.println("Next Move: ");
            for(int i = 0; i < 6; i++)
            {
                for(int j = 0; j < 6; j++)
                {
                    System.out.print(a.assignment[i][j] + "\t");
                }
                System.out.println();
            }
            printUtility(a);
            System.out.println("Final Utility : " + getUtility(a,'B','G'));

            if(a.moveType == MoveType.SABOTAGE)
            {
                System.out.println("SABOTAGE SUCCESSFUL!!!");
                sabotage_success++;
            }
            else if(a.moveType == MoveType.SABOTAGE_FAILED)
            {
                System.out.println("SABOTAGE UNSUCCESSFUL!!!");
                sabotage_fail++;
            }
            System.out.println();
            System.out.println();
        }

        System.out.println("Successful Sabotages : " + sabotage_success);
        System.out.println("Unsuccessful Sabotages : " + sabotage_fail);
        ArrayList<Integer> scores = new ArrayList<Integer>();
        scores.add(getUtility(a, 'B'));
        scores.add(getUtility(a, 'G'));
        scores.add(sabotage_fail + sabotage_success);
        return scores;
    }

    public static void main(String[] args)
    {
        ExpectiMiniMax game = new ExpectiMiniMax();
        String file_name = "Keren.txt";
        if(args.length != 0)
        {
            game.max_depth = Integer.parseInt(args[0]);
            game.depth_max = game.max_depth;
            game.gamma = Double.parseDouble(args[1]);
            if(game.gamma > 1.0 || game.gamma < 0.0)
            {
                System.out.println("Incorrect Gamma Value !!");
                System.exit(0);
            }
            file_name = args[2];
        }

        System.out.println("File Name : " + file_name);
        System.out.println("Gamma Value : " + game.gamma);
        System.out.println("Depth : " + game.depth_max);
        game.readFile(file_name);
        game.displayBoard();

        /*double blue_average_score = 0.0;
        double green_average_score = 0.0;
        double average_sabotages = 0.0;
        double avg_time = 0;
        for(int i = 0; i < 10; i++)
        {
            assignment a = new assignment();
            long startTime = System.currentTimeMillis();
            ArrayList<Integer> scores = game.expectiminimax_vs_expectiminimax(a);
            blue_average_score += scores.get(0);
            green_average_score += scores.get(1);
            average_sabotages += scores.get(2);
            long stopTime = System.currentTimeMillis();
            System.out.println("Time taken in Milliseconds : " + (int) (stopTime - startTime));
            avg_time += (stopTime - startTime);
            System.out.println();
            System.out.println();
        }
        blue_average_score = blue_average_score / 10.0;
        green_average_score = green_average_score / 10.0;
        average_sabotages = average_sabotages / 10.0;
        avg_time = avg_time / 10.0;
        System.out.println("Blue Average Score : " + blue_average_score);
        System.out.println("Green Average Score : " + green_average_score);
        System.out.println("Average Number of Sabotage Moves tried : " + average_sabotages);
        System.out.println("Average Time taken : " + avg_time);*/
        assignment a = new assignment();
        game.expectiminimax_vs_expectiminimax(a);

    }
}
