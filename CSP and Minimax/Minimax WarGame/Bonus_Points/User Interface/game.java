import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import static java.awt.Color.*;

/**
 * Created by uditmehrotra on 21/03/15.
 */
public class game extends JFrame {

    private JPanel rootPanel;

    private assignment applyBlitz(assignment a, int i, int j, char player, Container pane)
    {
        char opponent = 'B';
        if(player == 'B')
            opponent = 'G';

        if(i-1 >= 0 && a.assignment[i-1][j] == opponent)
        {
            a.assignment[i-1][j] = player;
            pane.getComponent((i-1)*6 + j).setBackground(Color.BLUE);
            pane.getComponent((i-1)*6 + j).setForeground(Color.BLUE);
            //pane.getComponent(i*6 + j).setEnabled(false);
        }

        //down
        if(i+1 < 6 && a.assignment[i+1][j] == opponent)
        {
            a.assignment[i+1][j] = player;
            pane.getComponent((i+1)*6 + j).setBackground(Color.BLUE);
            pane.getComponent((i+1)*6 + j).setForeground(Color.BLUE);
        }

        //left
        if(j-1 >= 0 && a.assignment[i][j-1] == opponent)
        {
            a.assignment[i][j-1] = player;
            pane.getComponent(i*6 + (j-1)).setBackground(Color.BLUE);
            pane.getComponent(i*6 + (j-1)).setForeground(Color.BLUE);
        }

        //right
        if(j+1 < 6 && a.assignment[i][j+1] == opponent)
        {
            a.assignment[i][j+1] = player;
            pane.getComponent(i*6 + (j+1)).setBackground(Color.BLUE);
            pane.getComponent(i*6 + (j+1)).setForeground(Color.BLUE);
        }

        return a;
    }

    private boolean isBlitz(assignment a, int i, int j, char player)
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

        return isBlitz;
    }
    private int playerUtility(assignment a, char player, WarGame w)
    {
        int score = 0;
        for(int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 6; j++)
            {
                if(a.assignment[i][j] == player)
                {
                    score += w.b.board[i][j];
                }
            }
        }

        return score;
    }

    public game(int depth, String fileName, final int game_play, String game_played)
    {
        super("War Game Application - Vs " + game_played + " Player @ Depth " + depth);

        final WarGame warGame = new WarGame();
        warGame.depth_max = depth;
        warGame.max_depth = depth;
        setLayout(new GridLayout());
        //setContentPane(rootPanel);
        this.setPreferredSize(new Dimension(800,800));
        pack();

        /*JButton button1 = new JButton("1");
        JButton button2 = new JButton("2");
        JButton button3 = new JButton("3");
        Container c = getContentPane();
        c.add(button1, BorderLayout.NORTH);
        c.add(button2, BorderLayout.CENTER);
        c.add(button3, BorderLayout.EAST);*/
        //c.add(button)
        //pack();
        //Read File

        warGame.readFile(fileName);
        warGame.displayBoard();
        final assignment a = new assignment();
        final JLabel blueScore;
        blueScore = new JLabel("Blue : ");

        final JLabel greenScore;
        greenScore = new JLabel("Green : ");

        final Container pane = getContentPane();
        pane.setLayout(new GridLayout(7, 6));
        for (int i = 0; i < 6; i++)
        {
            for(int j = 0; j < 6; j++) {
                final JButton button;
                button = new JButton(Integer.toString(warGame.b.board[i][j]));
                button.setName(i + " " + j);
                button.setOpaque(true);
                //button.setBackground(blue);
                button.addActionListener(
                        new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                button.setBackground(Color.BLUE);
                                button.setForeground(Color.BLUE);
                                //pane.getComponent(35).setVisible(false);

                                String[] coordinates = button.getName().split(" ");
                                int x = Integer.parseInt(coordinates[0]);
                                int y = Integer.parseInt(coordinates[1]);
                                a.assignment[x][y] = 'B';
                                if(isBlitz(a,x,y,'B'))
                                {
                                    assignment b = applyBlitz(a,x,y,'B',pane);
                                    for(int i = 0; i < 6; i++)
                                    {
                                        for(int j = 0; j < 6; j++)
                                        {
                                            a.assignment[i][j] = b.assignment[i][j];
                                        }
                                    }
                                }

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }

                                //Opponent moves
                                if(game_play == 0) {
                                    warGame.miniMax(a, 0, 'G', 'B');
                                }
                                else
                                {
                                    warGame.alpha_beta_search(a,'G','B');
                                }

                                for(int i = 0; i < 6; i++)
                                {
                                    for(int j = 0; j < 6; j++)
                                    {
                                        a.assignment[i][j] = a.next.assignment[i][j];
                                    }
                                }

                                for(int i = 0; i < 6; i++)
                                {
                                    for(int j = 0; j < 6; j++)
                                    {
                                        if(a.assignment[i][j] == 'G')
                                        {
                                                pane.getComponent(i*6 + j).setBackground(Color.GREEN);
                                                pane.getComponent(i*6 + j).setForeground(Color.GREEN);
                                                pane.getComponent(i*6 + j).setEnabled(false);
                                        }
                                    }
                                }

                                button.setEnabled(false);
                                int blue_score = playerUtility(a, 'B', warGame);
                                int green_score = playerUtility(a, 'G', warGame);
                                blueScore.setText("Blue : " + Integer.toString(blue_score));
                                greenScore.setText("Green : " + Integer.toString(green_score));

                                boolean isGameOver = true;
                                for(int i = 0; i < 6; i++)
                                {
                                    for (int j = 0; j < 6; j++)
                                    {
                                            if(pane.getComponent(i*6 + j).isEnabled() == true)
                                            {
                                                isGameOver = false;
                                                break;
                                            }
                                    }
                                }

                                if(isGameOver) {
                                    if(blue_score > green_score) {
                                        JOptionPane.showMessageDialog(pane,
                                                "You Won !!!",
                                                "War Game Result",
                                                JOptionPane.PLAIN_MESSAGE);
                                    }
                                    else if(blue_score < green_score)
                                    {
                                        JOptionPane.showMessageDialog(pane,
                                                "You Lost",
                                                "War Game Result",
                                                JOptionPane.PLAIN_MESSAGE);
                                    }
                                    else
                                    {
                                        JOptionPane.showMessageDialog(pane,
                                                "Match Drawn !!",
                                                "War Game Result",
                                                JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                            }
                        }
                );

                pane.add(button);
            }
        }


        pane.add(blueScore);
        pane.add(greenScore);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);


    }

    public static void main(String args[])
    {
        int d = 6;
        String f = "Keren.txt";
        int game = 1;
        if(args.length != 0)
        {
            d = Integer.parseInt(args[0]);
            f = args[1];
            game = Integer.parseInt(args[2]);
        }

        final int depth = d;
        final String fileName = f;
        final int game_play = game;

        if(game_play == 0) {
            SwingUtilities.invokeLater(new Runnable() {

                                           public void run() {
                                               game obj = new game(depth, fileName, game_play, "Minimax");
                                           }
                                       }
            );
        }
        else if(game_play == 1)
        {
            SwingUtilities.invokeLater(new Runnable() {

                                           public void run() {
                                               game obj = new game(depth, fileName, game_play, "Alpa Beta");
                                           }
                                       }
            );
        }
    }

}
