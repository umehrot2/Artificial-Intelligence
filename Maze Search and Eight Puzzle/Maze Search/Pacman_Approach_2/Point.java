
public class Point implements Comparable<Point>
{

    int x;
    int y;
    Point parent;
    boolean visited = false;
    int path = 0;
    int distance = 0;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
        parent = null;
        distance = 0;
        path = 0;
    }

    public Point(int x, int y, Point parent)
    {
        this.x = x;
        this.y = y;
        this.parent = new Point(parent.x, parent.y);
        this.path = 0;
        this.distance = 0;
    }

    public Point(int x, int y, Point parent, int distance)
    {
        this.x = x;
        this.y = y;
        this.parent = new Point(parent.x, parent.y);
        this.distance = distance;
    }

    public Point(int x, int y, Point parent, int path, int distance)
    {
        this.x = x;
        this.y = y;
        this.parent = new Point(parent.x, parent.y);
        this.path = path;
        this.distance = distance;
    }

    @Override
    public int compareTo(Point o) {
        //Double d = Math.sqrt(Math.pow(this.x - o.x, 2) + Math.pow(this.y - o.y, 2));
        //return this.distance - o.distance;
        //return d.compareTo(0.0);

        Integer val = (this.path + this.distance) - (o.path + o.distance);
        return val.compareTo(0);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
    
    
}
