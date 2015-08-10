import java.util.Arrays;
import java.util.Hashtable;

//import com.thinkjs.io.A;
class Coord{
	int x;
	int y;
	
	Coord(int x, int y){
		this.x = x;
		this.y = y;
	}
}

public class Puzzle implements Comparable<Puzzle> {
	Integer h;
	int[][] P = new int[3][3];
	Puzzle parent;
	int x;
	int y;
	int path = 0;
	boolean visited = false;

	
	public Puzzle(int[][] puz, int[][] solution, int path, int heuristic){
		for (int i = 0; i < puz.length; i++) {
	        P[i] = Arrays.copyOf(puz[i], puz[i].length);
	    }
		
		// get 0 tile's value i.e. the end state
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(P[i][j] == 0){
					x = i;
					y = j;
				}
				
			}
		}
		
		if(heuristic == 1){
		// compute hueristic value - h - Misplaced tiles
			int count = 0;
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					if(solution[i][j] != P[i][j])
						count++;
				}
			}
			h = count;
		// compute hueristic value - h - Misplaced tiles
		}else if(heuristic == 2){
		
		// compute hueristic value - h - sum of manhattan distance of misplaced tiles
		Hashtable<Integer, Coord> ht = new Hashtable<Integer, Coord>();
		for(int i =0; i < 3;i++){
			for(int j = 0; j < 3; j++){
				ht.put(solution[i][j], new Coord(i,j));
			}
		}
		int count1 = 0;
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(solution[i][j] != P[i][j]){
					Coord tempPos = ht.get(P[i][j]);
					count1 += (Math.abs(i-tempPos.x) + Math.abs(j-tempPos.y));
				}
			}
		}
		h = count1;
		// compute hueristic value - h - sum of manhattan distance of misplaced tiles
		}else{
			Gasching ga = new Gasching();
			int[][] tempPuz = new int[3][3];
			for(int kl = 0; kl < 3; kl++){
				for(int mn = 0; mn < 3; mn++){
					tempPuz[kl][mn] = puz[kl][mn];
				}
			}
			 
			 h = ga.getHeuristicVal(tempPuz, solution);
		}
		// path value for a puzzle instance
		this.path = path;
	}
	
	@Override
	public int compareTo(Puzzle o) {
		Integer val = (this.path + this.h) - (o.path + o.h);
        return val.compareTo(0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(P);
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
		Puzzle other = (Puzzle) obj;
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(P[i][j] != other.P[i][j])
					return false;
			}
		}
		return true;
	}
	
	
}

