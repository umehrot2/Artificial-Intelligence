import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;

public class WordCloud {

    private static ArrayList<HashMap<String,Integer>> data;
    private static ArrayList<String> words;
    private static String file = "spam_detection/train_email.txt";
    private static int label = 1;

    private void load_data()
    {
        data = new ArrayList<HashMap<String, Integer>>();
        words = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(" ");
                int current_label = Integer.parseInt(temp[0]);

                if(current_label == label)
                {
                    HashMap<String, Integer> pairs = new HashMap<String, Integer>();
                    for (int loop = 0; loop < temp.length - 1; loop++) {
                        String[] attr_val = temp[loop + 1].split(":");
                        String attr = attr_val[0];
                        int val = Integer.parseInt(attr_val[1]);
                        pairs.put(attr, val);

                        for (int i = 0; i < val; i++) {
                            words.add(attr);
                        }
                    }

                    data.add(pairs);
                }
            }

        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }

    protected void initUI() {
        JFrame frame = new JFrame(WordCloud.class.getSimpleName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        Cloud cloud = new Cloud();

        for (String s : words) {
                cloud.addTag(s);
        }

        cloud.setMaxTagsToDisplay(200);
        cloud.setMaxWeight(65);
        cloud.setMinWeight(5);

        for (Tag tag : cloud.tags()) {
            final JLabel label = new JLabel(tag.getName());
            label.setOpaque(false);
            label.setFont(label.getFont().deriveFont((float) tag.getWeight()));
            panel.add(label);
        }
        frame.add(panel);
        frame.setTitle("Word Cloud - File : " + file );
        frame.setSize(1000, 1000);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

       if(args.length != 0)
       {
           file = args[0];
           label = Integer.parseInt(args[1]);
       }

       final WordCloud obj = new WordCloud();
       obj.load_data();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                obj.initUI();
            }
        });
    }

}