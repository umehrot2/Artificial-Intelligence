import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.io.PrintWriter;

/**
 * Created by uditmehrotra on 11/04/15.
 */
public class NewsData
{

    private static ArrayList<String> vocabulary;
    private HashSet<String> unique_words = new HashSet<String>();

    private void readVocabulary(String file)
    {
        vocabulary = new ArrayList<String>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null)
            {
                vocabulary.add(line);
            }

            System.out.println("Vocabulary Size : " + vocabulary.size());
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }

    private void readTrainingData(String file, String label_file)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedReader br1 = new BufferedReader(new FileReader(label_file));
            PrintWriter output = new PrintWriter("20news-train.txt");
            String line;

            HashMap<Integer,HashMap<String, Integer>> map = new HashMap<Integer, HashMap<String, Integer>>();
            ArrayList<Integer> labels = new ArrayList<Integer>();
            while ((line = br.readLine()) != null)
            {
                String[] temp = line.split(" ");
                int current_doc_id = Integer.parseInt(temp[0]);
                int word_index = Integer.parseInt(temp[1]);
                String word = vocabulary.get(word_index-1);
                int word_count = Integer.parseInt(temp[2]);
                if(!map.containsKey(current_doc_id))
                {
                    HashMap<String, Integer> hm = new HashMap<String, Integer>();
                    hm.put(word, word_count);
                    map.put(current_doc_id, hm);
                }
                else
                {
                    map.get(current_doc_id).put(word, word_count);
                }
            }

            while ((line = br1.readLine()) != null)
            {
                int class_label = Integer.parseInt(line);
                labels.add(class_label);
            }

            for(Integer doc_id : map.keySet())
            {
                output.print(labels.get(doc_id - 1) + " ");
                for(int loop = 0; loop < map.get(doc_id).size(); loop++)
                {
                    String word = map.get(doc_id).keySet().toArray()[loop].toString();
                    int count = Integer.parseInt(map.get(doc_id).values().toArray()[loop].toString());
                    if(loop == map.get(doc_id).size()-1)
                    {
                        output.print(word + ":" + count);
                    }
                    else
                    {
                        output.print(word + ":" + count + " ");
                    }

                }
                output.println();
            }

            output.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }

    private void readTestData(String file, String label_file)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedReader br1 = new BufferedReader(new FileReader(label_file));
            PrintWriter output = new PrintWriter("20news-test.txt");
            String line;

            HashMap<Integer,HashMap<String, Integer>> map = new HashMap<Integer, HashMap<String, Integer>>();
            ArrayList<Integer> labels = new ArrayList<Integer>();
            while ((line = br.readLine()) != null)
            {
                String[] temp = line.split(" ");
                int current_doc_id = Integer.parseInt(temp[0]);
                int word_index = Integer.parseInt(temp[1]);
                String word = vocabulary.get(word_index-1);
                int word_count = Integer.parseInt(temp[2]);
                if(!map.containsKey(current_doc_id))
                {
                    HashMap<String, Integer> hm = new HashMap<String, Integer>();
                    hm.put(word, word_count);
                    map.put(current_doc_id, hm);
                }
                else
                {
                    map.get(current_doc_id).put(word, word_count);
                }
            }

            while ((line = br1.readLine()) != null)
            {
                int class_label = Integer.parseInt(line);
                labels.add(class_label);
            }

            for(Integer doc_id : map.keySet())
            {
                output.print(labels.get(doc_id - 1) + " ");
                for(int loop = 0; loop < map.get(doc_id).size(); loop++)
                {
                    String word = map.get(doc_id).keySet().toArray()[loop].toString();
                    int count = Integer.parseInt(map.get(doc_id).values().toArray()[loop].toString());
                    if(loop == map.get(doc_id).size()-1)
                    {
                        output.print(word + ":" + count);
                    }
                    else
                    {
                        output.print(word + ":" + count + " ");
                    }

                }
                output.println();
            }

            output.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }

    private void findUniqueAttributes(String file)
    {
        unique_words = new HashSet<String>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] temp = line.split(" ");
                int word_index = Integer.parseInt(temp[1]) - 1;
                String word = vocabulary.get(word_index);
                if (!unique_words.contains(word))
                {
                    unique_words.add(word);
                }
            }

            System.out.println("Unique words in training data : " + unique_words.size());
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }

    public static void main(String[] args)
    {
        //String vocab_file = "20news-bydate/matlab/vocabulary.txt";
        //String training_file = "20news-bydate/matlab/test.data";
        //String training_label_file = "20news-bydate/matlab/test.label";
        String dataset_path = "20news-bydate/matlab/";
        if(args.length != 0)
        {
            dataset_path = args[0];
        }

        String vocab_file = dataset_path + "vocabulary.txt";
        String training_file = dataset_path + "train.data";
        String training_label_file = dataset_path + "train.label";
        String test_file = dataset_path + "test.data";
        String test_label_file = dataset_path + "test.label";

        NewsData obj = new NewsData();
        obj.readVocabulary(vocab_file);

        obj.findUniqueAttributes(training_file);
        obj.readTrainingData(training_file, training_label_file);

        obj.findUniqueAttributes(test_file);
        obj.readTestData(test_file, test_label_file);
    }
}
