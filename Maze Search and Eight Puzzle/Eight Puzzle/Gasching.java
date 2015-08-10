import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javax.print.attribute.HashAttributeSet;


public class Gasching {
	public static Hashtable<Integer, Coord> referenceCoord;
	public boolean checkSolution(int[][] puzzle, int[][] solution){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(puzzle[i][j] != solution[i][j])
					return false;
			}
		}
		return true;
	}
	
	public int getActualTile(int zeroX, int zeroY){
		for(int tile : referenceCoord.keySet()){
			if(zeroX == referenceCoord.get(tile).x && zeroY == referenceCoord.get(tile).y){
				return tile;
			}
		}
		return 0;
	}
	
	public void printArrayC(int[][] a){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				System.out.print(a[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public int getHeuristicVal(int[][] input, int[][] solution){
		int count = 0;
		Coord zeroPosition = new Coord(0, 0);
		// hashtable to store current coordinate values for puzzle items
		Hashtable<Integer, Coord> currentCoord = new Hashtable<Integer, Coord>();
		referenceCoord = new Hashtable<Integer, Coord>();
		
		// get the value of all misplaced tiles sorted in increasing order
		PriorityQueue<Integer> misplacedTile = new PriorityQueue<Integer>();
		
		// find initial zero position and position of all the items in the current puzzle and solution puzzle
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(input[i][j] == 0){
					zeroPosition.x = i;
					zeroPosition.y = j;
				}
				currentCoord.put(input[i][j], new Coord(i,j));
				referenceCoord.put(solution[i][j], new Coord(i,j));
				
				// add misplaced tile
				if(input[i][j] != solution[i][j] && input[i][j] != 0)
					misplacedTile.add(input[i][j]);
			}
		}
		
		while(!checkSolution(input, solution)){
			// if zero is not in its position
			if(zeroPosition.x != 0 || zeroPosition.y != 0){
				int correctTile = getActualTile(zeroPosition.x, zeroPosition.y);
								
				int correctTileX = currentCoord.get(correctTile).x;
				int correctTileY = currentCoord.get(correctTile).y;
				
				// swap blank with the correct value				
				input[zeroPosition.x][zeroPosition.y] = input[correctTileX][correctTileY];
				input[correctTileX][correctTileY] = 0;
				
				// update blank tile position
				zeroPosition.x = correctTileX;
				zeroPosition.y = correctTileY;
				
				// remove corrected tile from misplaced queue
				misplacedTile.remove(correctTile);
				//printArrayC(input);
				
			}else{
				// if zero is in correct place, but puzzle is not solved yet, get first element from misplaced tile queue
				int tempMisplacedTile = misplacedTile.peek();
				
				// get misplaced tile's current coordinates
				int tempX = currentCoord.get(tempMisplacedTile).x;
				int tempY = currentCoord.get(tempMisplacedTile).y;
				
				// swap misplaced tile with blank
				input[zeroPosition.x][zeroPosition.y] = tempMisplacedTile;
				input[tempX][tempY] = 0;
				
				// update blank and misplaced tile position
				currentCoord.put(tempMisplacedTile, new Coord(zeroPosition.x, zeroPosition.y));
				zeroPosition.x = tempX;
				zeroPosition.y = tempY;			
			}
			count++;			
		}
		return count;
	}

}
