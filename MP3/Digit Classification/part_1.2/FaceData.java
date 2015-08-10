import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;


public class FaceData {
	public static double smoothingConstant = 1.0;
	
	// read training and test data labels
		public void readTrainTestLabels(String fileName, ArrayList<Integer> trainLabels, Hashtable<Integer, Integer> trainClassCount) throws IOException{
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			while((line = br.readLine()) != null){
				int classVal = Integer.parseInt(line);
				trainLabels.add(classVal);
				
				if(trainClassCount.get(classVal) != null){
					int count = trainClassCount.get(classVal);
					trainClassCount.put(classVal, count+1);
				}else{
					trainClassCount.put(classVal, 1);
				}
				
			}	
			br.close();
			
		}
		
	// read training images into memory
	public void readTrainData(String fileName, ArrayList<String[][]> trainImages, ArrayList<Integer> trainLabels) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		for(int k = 0; k < trainLabels.size(); k++){
			String[][] board = new String[70][60];
			for(int i = 0; i < 70; i++){
				String line = br.readLine();
				String[] vals = line.split("");
				for(int j = 0; j < 60; j++){
					board[i][j] = vals[j];
				}
			}
			trainImages.add(board);
		}
		br.close();
	}
	
	// add given featureNum, decimalVal and class combination to the model
	public void addValueToModel(int featureNum, int decimalVal, int classLabel, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model){
		if(model.get(featureNum) != null){
			
			Hashtable<Integer, Hashtable<Integer, Double>> rowColHT = new Hashtable<Integer, Hashtable<Integer, Double>>();
			rowColHT = model.get(featureNum);
			
			// if its a newly found attr value
			if(rowColHT.get(decimalVal) == null){
				Hashtable<Integer, Double> tempHT = new Hashtable<Integer, Double>();
				tempHT.put(classLabel, 1.0);
				rowColHT.put(decimalVal, tempHT);
			}else{
				Hashtable<Integer, Double> tempHT = new Hashtable<Integer, Double>();
				tempHT = rowColHT.get(decimalVal);

				// if the class val is not found
				if(tempHT.get(classLabel) == null){
					tempHT.put(classLabel, 1.0);
					
				}else{
					double tVal = tempHT.get(classLabel);
					tempHT.put(classLabel, tVal+1.0);
				}
				rowColHT.put(decimalVal, tempHT);
			}
		
			model.put(featureNum, rowColHT);
			
		}else{
			Hashtable<Integer, Double> innerHT = new Hashtable<Integer, Double>();
			innerHT.put(classLabel, 1.0);
			Hashtable<Integer, Hashtable<Integer, Double>> outerHT = new Hashtable<Integer, Hashtable<Integer, Double>>();
			outerHT.put(decimalVal, innerHT);
			model.put(featureNum, outerHT);
			
		}
	}
	
	// calculate model for sliding window of size m*n for disjoint squares
	public void slidingWindowDisjoint(int m, int n, ArrayList<Integer> trainLabels, ArrayList<String[][]> trainImages, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model){
		for(int z = 0; z < trainImages.size(); z++){
			String[][] board = trainImages.get(z);
			int featureNum = 0;
			for(int i = 0; i < 70; i+=m){
				for(int j = 0; j < 60; j+=n){
					StringBuilder sb = new StringBuilder();
					for(int k = 0; k < m; k++){
						for(int l = 0; l < n; l++){
//							System.out.println(i+" "+j+" "+k+" "+l);
							if(i+k >= 70)
								break;
							if(board[i+k][j+l].equals(" ")){
								sb.append(String.valueOf(0));								
							}else{
								sb.append(String.valueOf(1));
							}
						}
					}
					// convert binary to decimal value
					int decimalVal = Integer.parseInt(sb.toString(), 2);
					addValueToModel(featureNum, decimalVal, trainLabels.get(z), model);
					featureNum++;
				}
			}
		}
	}
	
	// calculate model for sliding window of size m*n for disjoint squares
	public void slidingWindowOverlapping(int p, int q, int m, int n, ArrayList<Integer> trainLabels, ArrayList<String[][]> trainImages, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model){
		for(int z = 0; z < trainImages.size(); z++){
			String[][] board = trainImages.get(z);
			int featureNum = 0;
			for(int i = 0; i < 70; i++){
				if(i+m > p)
					break;
				for(int j = 0; j < 60; j++){
					if(j+n > q)
						break;
					StringBuilder sb = new StringBuilder();
					for(int k = 0; k < m; k++){
						for(int l = 0; l < n; l++){
							if(board[i+k][j+l].equals(" ")){
								sb.append(String.valueOf(0));								
							}else{
								sb.append(String.valueOf(1));
							}
						}
					}
					// convert binary to decimal value
					int decimalVal = Integer.parseInt(sb.toString(), 2);
					addValueToModel(featureNum, decimalVal, trainLabels.get(z), model);
					featureNum++;
				}
			}
		}
	}
	
	// calculate probability for each of class labels
	public void calculateModelProbability(int m, int n, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model, Hashtable<Integer, Integer> trainClassCount){
		
		for(int fkey : model.keySet()){
			Hashtable<Integer, Hashtable<Integer, Double>> fHT = model.get(fkey);
			for(int skey : fHT.keySet()){
				Hashtable<Integer, Double> sHT = fHT.get(skey);
				for(int tkey: sHT.keySet()){
					double curVal = sHT.get(tkey);
					int denom = trainClassCount.get(tkey);
					double uniqVals = Math.pow(2, (m*n));
					double probabVal = (curVal + smoothingConstant)/(denom + (smoothingConstant * uniqVals) * 1.0);
					sHT.put(tkey, probabVal);
				}
			}
		}
	}
	
	// get probability value for each pixel of test data set
	public double checkConditionalProbab(int m, int n, int rowColCount, int attrVal, int classNum, Hashtable<Integer, Integer> trainClassCount, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model){
		
		// check if attribute value i.e. 0 or 1 is present for that pixel coordinate
		if(model.get(rowColCount).get(attrVal) != null){
			
			// check if class value is present for that pixel coordinate
			if(model.get(rowColCount).get(attrVal).get(classNum) != null){
				return (model.get(rowColCount).get(attrVal).get(classNum));
			}else{
				double uniqVals = Math.pow(2, (m*n));
				return (smoothingConstant/ ((trainClassCount.get(classNum) +( smoothingConstant * uniqVals)) * 1.0));
			}
			
		}else{
			double uniqVals = Math.pow(2, (m*n));
			return (smoothingConstant/ ((trainClassCount.get(classNum) +( smoothingConstant * uniqVals)) * 1.0));
		}
	}
	
	// predict labels for overalapping test data
		public void predictLabelsOverlap(int p, int q, int m, int n, ArrayList<String[][]> testImages, ArrayList<Integer> trainLabels, ArrayList<Integer> testLabels, Hashtable<Integer, Integer> trainClassCount, Hashtable<Integer, Integer> testClassCount, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model){
			int numCorrectPredictions = 0;
			for(int z = 0; z < testImages.size(); z++){
				ArrayList<Result> solution = new ArrayList<Result>();
				for(int classNum = 0; classNum < 2; classNum++){
					double curProbab = 0.0;
					String[][] board = testImages.get(z);
					int featureNum = 0;
					for(int i = 0; i < 70; i++){
						if(i+m > p)
							break;
						for(int j = 0; j < 60; j++){
							if(j+n > q)
								break;
							StringBuilder sb = new StringBuilder();
							for(int k = 0; k < m; k++){
								for(int l = 0; l < n; l++){
									if(board[i+k][j+l].equals(" ")){
										sb.append(String.valueOf(0));								
									}else{
										sb.append(String.valueOf(1));
									}
								}
							}
							// convert binary to decimal value
							int decimalVal = Integer.parseInt(sb.toString(), 2);
							curProbab += Math.log(checkConditionalProbab(m, n, featureNum, decimalVal, classNum, trainClassCount, model));
							featureNum++;
						}
					}
					double probabForClass = curProbab + Math.log((trainClassCount.get(classNum)/(trainLabels.size() * 1.0)));
					
					
					solution.add(new Result(classNum, probabForClass));
				}
				Collections.sort(solution);
				
				if(solution.get(0).classLabel == testLabels.get(z))
					numCorrectPredictions++;
			}
			System.out.println("Accuracy is "+(numCorrectPredictions/(testLabels.size() * 1.0)));
		}
	
	// predict labels for test data
	public void predictLabels(int m, int n, ArrayList<String[][]> testImages, ArrayList<Integer> trainLabels, ArrayList<Integer> testLabels, Hashtable<Integer, Integer> trainClassCount, Hashtable<Integer, Integer> testClassCount, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model){
		int numCorrectPredictions = 0;
		for(int z = 0; z < testImages.size(); z++){
			ArrayList<Result> solution = new ArrayList<Result>();
			for(int classNum = 0; classNum < 2; classNum++){
				double curProbab = 0.0;
				String[][] board = testImages.get(z);
				int featureNum = 0;
				for(int i = 0; i < 70; i+=m){
					for(int j = 0; j < 60; j+=n){
						StringBuilder sb = new StringBuilder();
						for(int k = 0; k < m; k++){
							for(int l = 0; l < n; l++){
								if(i+k >= 70)
									break;
								if(board[i+k][j+l].equals(" ")){
									sb.append(String.valueOf(0));								
								}else{
									sb.append(String.valueOf(1));
								}
							}
						}
						// convert binary to decimal value
						int decimalVal = Integer.parseInt(sb.toString(), 2);
						curProbab += Math.log(checkConditionalProbab(m, n, featureNum, decimalVal, classNum, trainClassCount, model));
						featureNum++;
					}
				}
				double probabForClass = curProbab + Math.log((trainClassCount.get(classNum)/(trainLabels.size() * 1.0)));
				
				
				solution.add(new Result(classNum, probabForClass));
			}
			Collections.sort(solution);
			
			if(solution.get(0).classLabel == testLabels.get(z))
				numCorrectPredictions++;
		}
		System.out.println("Accuracy is "+(numCorrectPredictions/(testLabels.size() * 1.0)));
	}
	
	public static void main(String[] args) throws IOException {
		long startTime = 0, endTime = 0, totalTime = 0, programRunTime = 0;
		long progStartTime = System.currentTimeMillis();
		FaceData dr = new FaceData();
		
		if(args.length < 3){
			System.out.println("Usage: <Type> <window row size> <window col size>");
			System.out.println("Invalid Usage: Pass 0 (Disjoint) or 1 (Overlap) as first argument");
			System.exit(0);
		}
		
		// type = 0 for disjoint and 1 for overlapping pixel grouping
		int type = Integer.parseInt(args[0]);
//		int type = 1;
		
		System.out.println("Sliding Window: Face data classification");
		
		String[] trainingFiles = {"facedatatrainlabels", "facedatatrain", "facedatatestlabels", "facedatatest"};
		
		// read all class labels of training and test data in order
		ArrayList<Integer> trainLabels = new ArrayList<Integer>();
		ArrayList<Integer> testLabels = new ArrayList<Integer>();
		
		// Hashtable to get the class label and its count
		Hashtable<Integer, Integer> trainClassCount = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> testClassCount = new Hashtable<Integer, Integer>();
		
		
		dr.readTrainTestLabels(trainingFiles[0], trainLabels, trainClassCount);
		dr.readTrainTestLabels(trainingFiles[2], testLabels, testClassCount);
		
		// read training data into memory
		ArrayList<String[][]> trainImages = new ArrayList<String[][]>();
		dr.readTrainData(trainingFiles[1], trainImages, trainLabels);
		
		// read test data into memory
		ArrayList<String[][]> testImages = new ArrayList<String[][]>();
		dr.readTrainData(trainingFiles[3], testImages, testLabels);
		
		// implement sliding window
		// enter board size
		int p = 70, q = 60;

		// enter window size
		startTime = System.currentTimeMillis();
		int m = Integer.parseInt(args[1]), n = Integer.parseInt(args[2]);
//		int m = 4, n = 2;
		Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model = new Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>>();
		if(type == 0)
			dr.slidingWindowDisjoint(m, n, trainLabels, trainImages, model);
		else
			dr.slidingWindowOverlapping(p, q, m, n, trainLabels, trainImages, model);
		
		// calculate model probability
		dr.calculateModelProbability(m, n, model, trainClassCount);
		endTime   = System.currentTimeMillis();
		totalTime = endTime - startTime;
		
		System.out.println("Total time taken for training in milliseconds "+totalTime);
		
		// predict labels for test data
		startTime   = System.currentTimeMillis();
		if(type == 0)
			dr.predictLabels(m, n, testImages, trainLabels, testLabels, trainClassCount, testClassCount, model);
		else
			dr.predictLabelsOverlap(p, q, m, n, testImages, trainLabels, testLabels, trainClassCount, testClassCount, model);
		endTime   = System.currentTimeMillis();
		totalTime = endTime - startTime;
		
		System.out.println("Total time taken for test in milliseconds "+totalTime);
		System.out.println();
		long progEndTime = System.currentTimeMillis();
		programRunTime = progEndTime - progStartTime;
		System.out.println("Total time taken to run entire program "+programRunTime);
	}
}
