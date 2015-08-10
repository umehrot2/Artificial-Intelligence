/**
 * Created by udit mehrotra on 08/02/15.
 */
import java.util.ArrayList;

class point
{
    int x;
    int y;

    public point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof point)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        point c = (point) o;

        // Compare the data members and return accordingly
        return x == c.x && y == c.y;
    }
}

public class PacPoint implements Comparable<PacPoint>
{
    point p;
    PacPoint parent;
    int pathCost = 0;
    int heuristic = 0;
    ArrayList<point> goals;

    public PacPoint(int x, int y)
    {
        p = new point(x,y);
        parent = null;
        pathCost = 0;
        heuristic = 0;
        goals = new ArrayList<point>();
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof PacPoint)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        PacPoint c = (PacPoint) o;

        // Compare the data members and return accordingly
        return p.x == c.p.x && p.y == c.p.y && goals.size() == c.goals.size();
    }

    @Override
    public int compareTo(PacPoint o) {
        Integer val = (this.pathCost + this.heuristic) - (o.pathCost + o.heuristic);
        return val.compareTo(0);
    }

    @Override
    public int hashCode() {

        int result = 0;
        result = 31*result + this.p.x;
        result = 31*result + this.p.y;
        result = 31*result + this.goals.size();
        return result;
        //return this.p.x + this.p.y + this.p.x * this.p.y * this.goals.size();
    }
}
