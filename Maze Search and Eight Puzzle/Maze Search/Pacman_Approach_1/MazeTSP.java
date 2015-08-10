import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.PriorityQueue;

class AdjMST implements Comparable<AdjMST>{
	Integer value;
	Integer column;
	Integer from;
	
	AdjMST(int value, int column){
		this.value = value;
		this.column = column;
	}
	
	AdjMST(int value, int column, int from){
		this.value = value;
		this.column = column;
		this.from = from;
	}

	@Override
	public int compareTo(AdjMST o) {
		// TODO Auto-generated method stub
		return this.value.compareTo(o.value);
	}
}

public class MazeTSP {
    
    public int getMST(int[][] adjMatrix, int size){
    	ArrayList<Integer> visitedNodes = new ArrayList<Integer>();
    	int[][] newAdj = new int[size][size];
    	int start = 0;
    	visitedNodes.add(start);
    	while(visitedNodes.size() != size){
    		PriorityQueue<AdjMST> minPQ = new PriorityQueue<AdjMST>();
    		for(int i = 0; i < visitedNodes.size(); i++){
    			int curStart = visitedNodes.get(i);
    			for(int j = 0; j < size; j++){
    				if(!visitedNodes.contains(j) && adjMatrix[curStart][j] != 0){
    					minPQ.add(new AdjMST(adjMatrix[curStart][j], j, curStart));
    				}
    			}
    		}
    		int newNodeColumn = minPQ.peek().column;
    		int newNodeValue = minPQ.peek().value;
    		int newFrom = minPQ.peek().from;
    		visitedNodes.add(newNodeColumn);
    		newAdj[newFrom][newNodeColumn] = newNodeValue;
    		newAdj[newNodeColumn][newFrom] = newNodeValue;
    	}

    	
    	int hval = 0;
    	for(int i = 0; i < size; i++){
    		for(int j = 0; j < i; j++){
    			hval += newAdj[i][j];
    		}
    	}

    	return hval;
    	
    }
}
