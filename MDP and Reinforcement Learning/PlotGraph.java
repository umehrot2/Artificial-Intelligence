import org.math.plot.Plot2DPanel;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

/**
 * Created by uditmehrotra on 02/05/15.
 */
public class PlotGraph
{

    public void plotGiftFrenzyGraph(ArrayList<HashMap<GFState, Double>> utilityVals, String[][] grid)
    {
        Plot2DPanel plot = new Plot2DPanel();
        double[] numIter = new double[utilityVals.size()];
        double temp = 0.0;
        for(int i = 0; i < utilityVals.size(); i++){
            numIter[i] = temp;
            temp += 1;
        }

        for(int loop = 0; loop < 135; loop++)
        {
            double[] xval = new double[utilityVals.size()];
            //int column = loop % cols;
            //int row = (loop - column) / cols;
            GFState s = (GFState) utilityVals.get(0).keySet().toArray()[loop];
            if(grid[s.x][s.y].equals("%"))
                continue;

            for(int i = 0; i < utilityVals.size(); i++)
            {
                xval[i] = (Double) utilityVals.get(i).values().toArray()[loop];
            }

            plot.addLinePlot("Gift Frenzy ", numIter, xval);
        }

        //plot.setFixedBounds(0, 0, utilityVals.size() + 500);
        //plot.setFixedBounds(1, 0, 120);
        plot.setAxisLabels("Number of Iterations", "Utility Values");
        JFrame f = new JFrame("Gift Frenzy Q Iteration");

        f.setSize(1000, 1000);

        f.setContentPane(plot);
        f.setVisible(true);
    }

    public void plotQLGraph(ArrayList<ArrayList<Double>> utilityVals, String[][] grid, int cols)
    {
        Plot2DPanel plot = new Plot2DPanel();
        double[] numIter = new double[utilityVals.size()];
        double temp = 0.0;
        for(int i = 0; i < utilityVals.size(); i++){
            numIter[i] = temp;
            temp += 1;
        }

        for(int loop = 0; loop < 36; loop++)
        {
            double[] xval = new double[utilityVals.size()];
            int column = loop % cols;
            int row = (loop - column) / cols;
            if(grid[row][column].equals("w"))
                continue;
            for(int i = 0; i < utilityVals.size(); i++)
            {
                xval[i] = utilityVals.get(i).get(loop);
            }
            plot.addLinePlot("Q Learning ", numIter, xval);

        }

        plot.setFixedBounds(0, 0, utilityVals.size() + 500);
        plot.setFixedBounds(1, 0, 120);
        plot.setAxisLabels("Number of Iterations", "Utility Values");
        JFrame f = new JFrame("Q Learning Iteration");

        f.setSize(1000, 1000);

        f.setContentPane(plot);
        f.setVisible(true);
    }

    public void plotQLRMSE(ArrayList<Double> rmseToPlot)
    {
        double[] numIter = new double[rmseToPlot.size()];
        double[] rmseVals = new double[rmseToPlot.size()];
        double temp = 1.0;
        Plot2DPanel plot = new Plot2DPanel();

        for(int ij = 0; ij < rmseToPlot.size(); ij++){
            numIter[ij] = temp;
            temp += 1;
            rmseVals[ij] = rmseToPlot.get(ij);
        }


        plot.addLinePlot("RMSE Graph ", numIter, rmseVals);
        plot.setFixedBounds(0, 0, rmseToPlot.size() + 500);
        plot.setFixedBounds(1, 0, 120);
        plot.setAxisLabels("Number of Iterations", "RMSE Values");

        JFrame frame = new JFrame("RMSE Graph");
        frame.setContentPane(plot);
        frame.setSize(1000, 1000);
        frame.setVisible(true);

    }

    public void plotValueIterationGraph(ArrayList<ArrayList<Double>> utilityVals, String[][] grid, int cols){

        // create your PlotPanel (you can use it as a JPanel)
        Plot2DPanel plot = new Plot2DPanel();

        // add a line plot to the PlotPanel

        double[] numIter = new double[utilityVals.size()];
        double temp = 0.0;
        for(int i = 0; i < utilityVals.size(); i++){
            numIter[i] = temp;
            temp += 1;
        }

        for(int loop = 0; loop < 36; loop++)
        {
            double[] xval = new double[utilityVals.size()];
            int column = loop % cols;
            int row = (loop - column) / cols;
            if(grid[row][column].equals("w"))
                continue;
            for(int i = 0; i < utilityVals.size(); i++)
            {
                xval[i] = utilityVals.get(i).get(loop);
            }
            plot.addLinePlot("Value Iteration", numIter, xval);


        }

        plot.setFixedBounds(0, 0, utilityVals.size() + 10);
        plot.setFixedBounds(1, 0, 110);
        plot.setAxisLabels("Number of Iterations", "Utility Values");

        // put the PlotPanel in a JFrame, as a JPanel
        JFrame frame = new JFrame("Value Iteration");
        frame.setContentPane(plot);
        frame.setSize(1000,1000);
        frame.setVisible(true);
    }

    public static void main(String args[])
    {

    }
}
