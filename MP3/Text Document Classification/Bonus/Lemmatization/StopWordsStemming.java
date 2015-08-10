import org.tartarus.snowball.ext.PorterStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by uditmehrotra on 08/04/15.
 */
public class StopWordsStemming
{
    private static ArrayList<Integer> training_labels;
    private static ArrayList<HashMap<String,Integer>> training_data;
    private static ArrayList<Integer> test_labels;
    private static ArrayList<HashMap<String,Integer>> test_data;
    private static HashMap<String, HashMap<Integer,Double>> classifier;
    private static HashMap<Integer,Integer> training_labels_frequency;
    private static HashMap<Integer,Integer> training_labels_words_frequency;
    private static ArrayList<Integer> predicted_labels;
    DecimalFormat df = new DecimalFormat("0.00");
    DecimalFormat df2 = new DecimalFormat("0.0000");
    private static HashSet<String> stopWords;
    private static String training_file = "spam_detection/train_email.txt";
    private static String test_file = "spam_detection/test_email.txt";

    private void readStopWords()
    {
        stopWords = new HashSet<String>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("stop-word-list.txt"));
            String line;
            while((line = br.readLine()) != null)
            {
                stopWords.add(line.trim());
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }

    private String stem_word(String word)
    {
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }

    private boolean isStopWord(String word)
    {
        /*Set<String> stopWords = new HashSet<String>();
        stopWords.add("a");
        stopWords.add("an");
        stopWords.add("I");
        stopWords.add("the");*/
        if(stopWords.contains(word) || word.length() == 0)
        {
            return true;
        }

        return false;
    }

    private ArrayList<String> findAttributes(String training_file)
    {
        ArrayList<String> unique_words = new ArrayList<String>();

        try
        {
            //Read Training Data
            int count = 0;
            BufferedReader br = new BufferedReader(new FileReader(training_file));
            String line;
            while((line = br.readLine()) != null)
            {
                String[] temp = line.split(" ");
                for(int loop = 1; loop < temp.length; loop++)
                {
                    String[] attr_val = temp[loop].split(":");
                    String attr = attr_val[0];
                    count = count + Integer.parseInt(attr_val[1]);
                    if(!isStopWord(attr)) {
                        attr = stem_word(attr);
                        if (!unique_words.contains(attr)) {
                            unique_words.add(attr);
                        }
                    }
                }
            }

            System.out.println("Count : " + count);
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        return unique_words;
    }

    private void load_data(String training_file, String test_file)
    {
        training_data = new ArrayList<HashMap<String, Integer>>();
        test_data = new ArrayList<HashMap<String, Integer>>();
        training_labels = new ArrayList<Integer>();
        test_labels = new ArrayList<Integer>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(training_file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(" ");
                training_labels.add(Integer.parseInt(temp[0]));

                HashMap<String, Integer> pairs = new HashMap<String, Integer>();
                for (int loop = 0; loop < temp.length - 1; loop++) {
                    String[] attr_val = temp[loop + 1].split(":");
                    String attr = attr_val[0];
                    if(!isStopWord(attr))
                    {
                        attr = stem_word(attr);
                        int val = Integer.parseInt(attr_val[1]);
                        pairs.put(attr, val);
                    }
                }

                training_data.add(pairs);
            }

            System.out.println(training_labels.size());
            System.out.println(training_data.size());

            br = new BufferedReader(new FileReader(test_file));
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(" ");
                test_labels.add(Integer.parseInt(temp[0]));

                HashMap<String, Integer> pairs = new HashMap<String, Integer>();
                for (int loop = 0; loop < temp.length - 1; loop++) {
                    String[] attr_val = temp[loop + 1].split(":");
                    String attr = attr_val[0];
                    if(!isStopWord(attr))
                    {
                        attr = stem_word(attr);
                        int val = Integer.parseInt(attr_val[1]);
                        pairs.put(attr, val);
                    }
                }

                test_data.add(pairs);
            }

            System.out.println(test_labels.size());
            System.out.println(test_data.size());

        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }

    private void classifier()
    {
        classifier = new HashMap<String,HashMap<Integer, Double>>();
        training_labels_frequency = new HashMap<Integer, Integer>();
        training_labels_words_frequency = new HashMap<Integer, Integer>();

        for(int loop = 0; loop < training_data.size(); loop++)
        {
            int label = training_labels.get(loop);
            if(!training_labels_frequency.containsKey(label))
            {
                training_labels_frequency.put(label, 1);
                training_labels_words_frequency.put(label,0);
            }
            else
            {
                int current_count = training_labels_frequency.get(label);
                training_labels_frequency.put(label, current_count+1);
            }

            for(String key : training_data.get(loop).keySet())
            {
                int value = training_data.get(loop).get(key);
                int cur_val = training_labels_words_frequency.get(label);
                training_labels_words_frequency.put(label, cur_val + value);

                if(!classifier.containsKey(key))
                {
                    HashMap<Integer, Double> class_freq_pair = new HashMap<Integer, Double>();
                    class_freq_pair.put(label, value * 1.0);
                    classifier.put(key, class_freq_pair);
                }
                else
                {
                    if(!classifier.get(key).containsKey(label))
                    {
                        classifier.get(key).put(label, value * 1.0);
                    }
                    else
                    {
                        double current_count = classifier.get(key).get(label);
                        classifier.get(key).put(label, current_count + value);
                    }
                }
            }
        }

        //adding labels which might not have occurred for a particular attribute-value pair
        for(String key : classifier.keySet())
        {
            for(Integer class_label : training_labels_frequency.keySet())
            {
                if (!classifier.get(key).containsKey(class_label)) {
                    classifier.get(key).put(class_label, 0.0);
                }
            }
        }

        System.out.println("Classifier size " + classifier.size());
    }

    //This function predicts using the classifier on test data
    private void predict(ArrayList<String> unique_words, boolean applyLaplace)
    {
        predicted_labels = new ArrayList<Integer>();

        for(int loop = 0; loop < test_data.size(); loop++)
        {
            double max_probability = Integer.MIN_VALUE;
            int predicted_label = 0;

            for (Integer class_label : training_labels_frequency.keySet())
            {
                double conditional_probability = 1.0;
                int class_frequency = training_labels_words_frequency.get(class_label);

                for (String key : test_data.get(loop).keySet())
                {
                    //If test data contains a new word, skip it
                    if(!classifier.containsKey(key))
                    {
                        continue;
                    }

                    int value = test_data.get(loop).get(key);

                    double attr_value_class_frequency;
                    attr_value_class_frequency = classifier.get(key).get(class_label);
                    if(applyLaplace)
                    {
                        //Incrementing numerator by 1 for Laplacian
                        attr_value_class_frequency++;
                    }

                    if(!applyLaplace)
                    {
                        conditional_probability = conditional_probability * (attr_value_class_frequency / (class_frequency * 1.0));
                    }
                    else {
                        //Adding the count of attributes unique values to denominator for Laplacian correction
                        int freq = class_frequency + unique_words.size();
                        //int freq = class_frequency + 1;
                        //conditional_probability = conditional_probability * (attr_value_class_frequency / (freq * 1.0));
                        conditional_probability = conditional_probability + value * Math.log(attr_value_class_frequency / (freq * 1.0));
                    }
                }

                //double current_probability = conditional_probability * (training_labels_frequency.get(class_label) / (training_data.size() * 1.0));
                double current_probability = conditional_probability + Math.log((training_labels_frequency.get(class_label) / (training_data.size() * 1.0)));
                if (max_probability < current_probability) {
                    max_probability = current_probability;
                    predicted_label = class_label;
                }
            }

            predicted_labels.add(predicted_label);
        }

        System.out.println("Predicted Labels : " + predicted_labels.size());
    }

    private void accuracy()
    {
        int correct = 0;
        for(int loop = 0; loop < test_labels.size(); loop++)
        {
            if(test_labels.get(loop) == predicted_labels.get(loop))
            {
                correct++;
            }
        }

        double accuracy = correct / (test_labels.size() * 1.0);
        System.out.println("Accuracy : " + df2.format(accuracy));
    }

    private void classificationRate(ArrayList<String> unique_words)
    {
        HashMap<Integer, Integer> class_correct_classified = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> test_label_frequency = new HashMap<Integer, Integer>();
        HashMap<Integer, HashMap<Integer, Integer>> confusion_matrix = new HashMap<Integer, HashMap<Integer, Integer>>();

        for(Integer label : training_labels_frequency.keySet())
        {
            class_correct_classified.put(label, 0);
            test_label_frequency.put(label, 0);
            confusion_matrix.put(label, new HashMap<Integer, Integer>());
        }

        for(int loop = 0; loop < test_labels.size(); loop++)
        {
            if(test_labels.get(loop) == predicted_labels.get(loop))
            {
                int val = class_correct_classified.get(test_labels.get(loop));
                class_correct_classified.put(test_labels.get(loop), val + 1);
                val = test_label_frequency.get(test_labels.get(loop));
                test_label_frequency.put(test_labels.get(loop), val + 1);
            }
            else
            {
                int val = test_label_frequency.get(test_labels.get(loop));
                test_label_frequency.put(test_labels.get(loop), val + 1);
            }

            if(!confusion_matrix.get(test_labels.get(loop)).containsKey(predicted_labels.get(loop)))
            {
                confusion_matrix.get(test_labels.get(loop)).put(predicted_labels.get(loop), 1);
            }
            else
            {
                int val = confusion_matrix.get(test_labels.get(loop)).get(predicted_labels.get(loop));
                confusion_matrix.get(test_labels.get(loop)).put(predicted_labels.get(loop), val + 1);
            }
        }

        System.out.println("\nClassification Accuracy : ");
        System.out.println("--------------------------------------");
        for(Integer label : training_labels_frequency.keySet()) {
            System.out.println("Classification Accuracy for class " + label + " : " + (class_correct_classified.get(label) / (test_label_frequency.get(label) * 1.0)));
        }
        System.out.println("--------------------------------------\n");

        System.out.println("Confusion Matrix : ");
        System.out.println("--------------------------------------");
        System.out.print(" " + "\t");
        for(Integer label : confusion_matrix.keySet())
        {
            System.out.print(label + "\t\t\t");
        }
        System.out.println();
        System.out.println("----------------------------------------------------------------------------------------------");
        HashMap<String, Double> class_pair_confusion = new HashMap<String, Double>();
        for(Integer label1 : confusion_matrix.keySet())
        {
            System.out.print(label1 + "|" + "\t");
            for(Integer label2 : confusion_matrix.keySet())
            {
                double percentage = 0.00;
                if (confusion_matrix.get(label1).containsKey(label2)) {
                    percentage = confusion_matrix.get(label1).get(label2) / (test_label_frequency.get(label1) * 1.0);
                    System.out.print(df.format(percentage) + "\t\t");
                }
                else
                {
                    System.out.print("0.00" + "\t\t");
                }

                if(label1 != label2)
                {
                    class_pair_confusion.put(label1.toString() + "," + label2.toString(), percentage);
                }
            }
            System.out.println();
        }

        System.out.println("--------------------------------------\n");

        System.out.println("Highest Confusion Pairs : ");
        System.out.println("--------------------------------------");

        int count;
        if(training_labels_frequency.size() == 2)
        {
            count = 1;
        }
        else
        {
            count = 4;
        }

        int temp_count = count;
        Map<String, Double> map = sortByValues(class_pair_confusion);

        for(String key : map.keySet())
        {
            String[] labels = key.split(",");
            //System.out.println("Class " + key.charAt(0) + " & Class " + key.charAt(1) + " : " + df.format(map.get(key)));
            System.out.println("Class " + labels[0] + " & Class " + labels[1] + " : " + df.format(map.get(key)));
            count--;
            if(count == 0) break;
        }

        System.out.println("--------------------------------------");
        count = temp_count;
        for(String key : map.keySet())
        {
            String[] labels = key.split(",");
            System.out.println("\nHighest Log odd Ratio words : Class " + labels[0] + " & " + labels[1]);
            System.out.println("--------------------------------------\n");
            //int label1 = Integer.parseInt(key.substring(0, 1));
            //int label2 = Integer.parseInt(key.substring(1,2));
            int label1 = Integer.parseInt(labels[0]);
            int label2 = Integer.parseInt(labels[1]);
            HashMap<String, Double> words_log_odd = new HashMap<String, Double>();
            for(String attr : classifier.keySet())
            {
                double val1 = (classifier.get(attr).get(label1) + 1) / (training_labels_words_frequency.get(label1) + unique_words.size());
                double val2 = (classifier.get(attr).get(label2) + 1) / (training_labels_words_frequency.get(label2) + unique_words.size());

                //double val1 = classifier.get(attr).get(label1) / training_labels_words_frequency.get(label1);
                //double val2 = classifier.get(attr).get(label2) / training_labels_words_frequency.get(label2);

                if (val2 != 0) {
                    double log_odd_ratio = Math.log10(val1 / (val2 * 1.0));
                    words_log_odd.put(attr, log_odd_ratio);
                }
            }

            map = sortByValues(words_log_odd);
            int cutoff = 20;
            for(String k : map.keySet())
            {
                System.out.println(k + " : " + df2.format(map.get(k)));
                cutoff--;
                if(cutoff == 0)
                    break;
            }
            count--;
            if(count == 0) break;
        }
    }

    private void top20Words(ArrayList<String> unique_words)
    {
        for(Integer cls : training_labels_frequency.keySet())
        {
            HashMap<String, Double> mapClassWords = new HashMap<String, Double>();

            for (String attr : classifier.keySet())
            {
                //mapClassWords.put(attr,classifier.get(attr).get(cls) / (training_labels_words_frequency.get(cls) * 1.0));
                mapClassWords.put(attr,(classifier.get(attr).get(cls)+1) / ((training_labels_words_frequency.get(cls) + unique_words.size()) * 1.0));
            }

            Map<String, Double> map = sortByValues(mapClassWords);
            int count = 0;
            System.out.println();
            System.out.println("Top 20 words for Class " + cls);
            System.out.println("--------------------------------------");
            for(String key : map.keySet())
            {
                System.out.println(key + " : " + df2.format(map.get(key)));
                count++;
                if(count == 20)
                {
                    break;
                }
            }
            System.out.println("--------------------------------------");
        }

    }

    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    public static void main(String[] args)
    {
        if(args.length != 0)
        {
            training_file = args[0];
            test_file = args[1];
        }

        StopWordsStemming obj = new StopWordsStemming();
        obj.readStopWords();
        ArrayList<String> unique_words = obj.findAttributes(training_file);
        obj.load_data(training_file, test_file);
        obj.classifier();
        obj.predict(unique_words, true);
        obj.accuracy();
        obj.classificationRate(unique_words);
        obj.top20Words(unique_words);
    }
}
