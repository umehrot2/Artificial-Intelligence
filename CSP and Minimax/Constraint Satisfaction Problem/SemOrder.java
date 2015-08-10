
public class SemOrder implements Comparable<SemOrder>{
	int courseID;
	int price;
	boolean taken;
	
	

	SemOrder(int courseID){
		this.courseID = courseID;
	}
	
	SemOrder(int courseID, int price){
		this.courseID = courseID;
		this.price = price;
	}

	SemOrder(int courseID, int price, boolean taken){
		this.courseID = courseID;
		this.price = price;
		this.taken = taken;
	}

	@Override
	public int compareTo(SemOrder o) {
		// TODO Auto-generated method stub
//		return o.price-this.price;
		return this.price-o.price;
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
		SemOrder other = (SemOrder) obj;
		if (courseID != other.courseID)
			return false;
		return true;
	}
}