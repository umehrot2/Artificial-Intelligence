

// class to store class label and corresponding probability
public class Result implements Comparable<Result> {
	int classLabel;
	double probability;
	
	Result(int classLabel, double probability){
		this.classLabel = classLabel;
		this.probability = probability;
	}

	@Override
	public int compareTo(Result o) {
		return (int) (o.probability - this.probability);
	}
	
}
