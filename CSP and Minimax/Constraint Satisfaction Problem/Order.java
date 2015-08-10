
public class Order{
	int courseID;
	boolean taken;
	
	Order(int courseID, boolean taken){
		this.courseID = courseID;
		this.taken = taken;
	}
	
	Order(int courseID){
		this.courseID = courseID;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + courseID;
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
		Order other = (Order) obj;
		if (courseID != other.courseID)
			return false;
		return true;
	}
	
}
