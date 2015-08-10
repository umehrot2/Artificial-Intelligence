import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by uditmehrotra on 18/03/15.
 */
//Used for generating random number with a given probability
public class RandomCollection<E> {

    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {

        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public void add(double weight, E result) {
        //Finds the cumulative weight each time and adds an entry
        if(weight <= 0) return;
        total += weight;
        map.put(total, result);
    }

    public E next() {
        //Generates a random number and returns the value where key is ceiling of that number
        double value = random.nextDouble() * total;
        return map.ceilingEntry(value).getValue();
    }
}
