import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;


public class TerneryBonus {
	
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
	
	// add value to model
	public void addValueToModel(String pixelVal, int rowColCount, ArrayList<Integer> trainLabels, int exampleNum, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model){
		
		// if model do not contain key with the rowColCount
		if(model.get(rowColCount) == null){
			int attrVal;
			if(pixelVal.equals(" ")){
				attrVal = 0;
			}else if(pixelVal.equals("#")){
				attrVal = 1;
			}else{
				attrVal = 2;
			}
			
			int classVal = trainLabels.get(exampleNum);
			
			// hashtable with classval and count
			Hashtable<Integer, Double> classCountHT = new Hashtable<Integer, Double>();
			classCountHT.put(classVal, 1.0);
			
			// hashtable with attrVal and above HT
			Hashtable<Integer, Hashtable<Integer, Double>> attrHT = new Hashtable<Integer, Hashtable<Integer, Double>>();
			attrHT.put(attrVal, classCountHT);
			
			model.put(rowColCount, attrHT);
		}else{
			
			int attrVal;
			if(pixelVal.equals(" ")){
				attrVal = 0;
			}else if(pixelVal.equals("#")){
				attrVal = 1;
			}else{
				attrVal = 2;
			}
			
			int classVal = trainLabels.get(exampleNum);
			
			Hashtable<Integer, Hashtable<Integer, Double>> rowColHT = new Hashtable<Integer, Hashtable<Integer, Double>>();
			rowColHT = model.get(rowColCount);
			
			// if its a newly found attr value
			if(rowColHT.get(attrVal) == null){
				Hashtable<Integer, Double> tempHT = new Hashtable<Integer, Double>();
				tempHT.put(classVal, 1.0);
				rowColHT.put(attrVal, tempHT);
			}else{
				Hashtable<Integer, Double> tempHT = new Hashtable<Integer, Double>();
				tempHT = rowColHT.get(attrVal);

				// if the class val is not found
				if(tempHT.get(classVal) == null){
					tempHT.put(classVal, 1.0);
					
				}else{
					double tVal = tempHT.get(classVal);
					tempHT.put(classVal, tVal+1.0);
				}
				rowColHT.put(attrVal, tempHT);
			}
		
			model.put(rowColCount, rowColHT);
		}
	}
	
	// build model for the training images
	public void buildModel(String fileName, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model, ArrayList<Integer> trainLabels, Hashtable<Integer, Integer> trainClassCount) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		String line;
		for(int k = 0; k < trainLabels.size(); k++){
			int rowColCount = 0;
			for(int i = 0; i < 28; i++){
				line = br.readLine();
				String[] values = line.split("");
				for(int j = 0; j < 28; j++){
					String pixelVal = values[j];
					
					// add value to model
					addValueToModel(pixelVal, rowColCount, trainLabels, k, model);
					rowColCount++;
				}
			}
		}
		 br.close();
	}
	
	// calculate probability for each of class labels
	public void calculateModelProbability( Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model, Hashtable<Integer, Integer> trainClassCount){
		
		for(int fkey : model.keySet()){
			Hashtable<Integer, Hashtable<Integer, Double>> fHT = model.get(fkey);
			for(int skey : fHT.keySet()){
				Hashtable<Integer, Double> sHT = fHT.get(skey);
				for(int tkey: sHT.keySet()){
					double curVal = sHT.get(tkey);
					int denom = trainClassCount.get(tkey);
					double probabVal = (curVal + smoothingConstant)/(denom + (smoothingConstant * 3) * 1.0);
					sHT.put(tkey, probabVal);
				}
			}
		}
	}
	
	// load test data in memory which will help to predict the labels
	public void loadTestToMemory(String testFile, ArrayList<Integer> testLabels, ArrayList<ArrayList<String>> testDataInMemory) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File(testFile)));
		String line;
		for(int k = 0; k < testLabels.size(); k++){
			ArrayList<String> tempStr = new ArrayList<String>();
			for(int i = 0; i < 28; i++){
				line = br.readLine();
				tempStr.add(line);
			}
			testDataInMemory.add(tempStr);
		}
		 br.close();
	}
	
	// get probability value for each pixel of test data set
	public double checkConditionalProbab(int rowColCount, int attrVal, int classNum, Hashtable<Integer, Integer> trainClassCount, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model){
		
		// check if attribute value i.e. 0 or 1 is present for that pixel coordinate
		if(model.get(rowColCount).get(attrVal) != null){
			
			// check if class value is present for that pixel coordinate
			if(model.get(rowColCount).get(attrVal).get(classNum) != null){
				return (model.get(rowColCount).get(attrVal).get(classNum));
			}else{
				return (smoothingConstant/ ((trainClassCount.get(classNum) +( smoothingConstant * 3)) * 1.0));
			}
			
		}else{
			return (smoothingConstant/ ((trainClassCount.get(classNum) +( smoothingConstant * 3)) * 1.0));
		}
		
	}
	
	// predict labels for test data
	public void predictLabels(String testFile, ArrayList<Integer> trainLabels, ArrayList<Integer> testLabels, Hashtable<Integer, Integer> trainClassCount, Hashtable<Integer, Integer> testClassCount, ArrayList<ArrayList<String>> testDataInMemory, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model, Hashtable<Integer, Hashtable<Integer, Double>> cfm) throws IOException{
		int numCorrectPredictions = 0;
		String line;
		for(int k = 0; k < testLabels.size(); k++){
			
			// arraylist to keep track of conditional probability for each test label for all 10 classes
			ArrayList<Result> solution = new ArrayList<Result>();
			// loop for all 10 class labels
			for(int classNum = 0; classNum < 10; classNum++){
				ArrayList<String> digitPattern = testDataInMemory.get(k);
				int rowColCount = 0;
				double curProbab = 0.0;
				for(int i = 0; i < 28; i++){
					line = digitPattern.get(i);
					String[] values = line.split("");
					for(int j = 0; j < 28; j++){
						String pixelVal = values[j];
						int attrVal;
						if(pixelVal.equals(" ")){
							attrVal = 0;
						}else if(pixelVal.equals("#")){
							attrVal = 1;
						}else{
							attrVal = 2;
						}
						curProbab += Math.log(checkConditionalProbab(rowColCount, attrVal, classNum, trainClassCount, model));
						rowColCount++;
					}					
				}
				// for MAP
				double probabForClass = curProbab + Math.log((trainClassCount.get(classNum)/(trainLabels.size() * 1.0)));
				
				// for ML estimate
//				double probabForClass = curProbab;
				
				solution.add(new Result(classNum, probabForClass));
			}
			Collections.sort(solution);
			
			if(solution.get(0).classLabel == testLabels.get(k))
				numCorrectPredictions++;
			
			// put data in confusion matrix
			
			int predictedLabel = solution.get(0).classLabel;
			int actualLabel = testLabels.get(k);
			Hashtable<Integer, Double> cfmCount = cfm.get(actualLabel);
			double curCount = cfmCount.get(predictedLabel);
			++curCount;
			cfmCount.put(predictedLabel, curCount);
			
		}
		System.out.println("Accuracy is "+(numCorrectPredictions/(testLabels.size() * 1.0)));
				
	}
	
	// calculate and print confusion matrix
	public void printConfusionMatrix(Hashtable<Integer, Integer> testClassCount, Hashtable<Integer, Hashtable<Integer, Double>> cfm){
		for(int i = 0; i < 10; i++){
			Hashtable<Integer, Double> tempHT = cfm.get(i);
			for(int freq: tempHT.keySet()){
				double curVal = tempHT.get(freq);
				curVal/= (testClassCount.get(i) * 1.0);
				curVal = Math.round(curVal * 100.0) / 100.0;
				tempHT.put(freq, curVal);
			}
		}
		System.out.println("Final Confustion matrix is");
		System.out.print(" \t");
		for(int i =0; i < 10; i++)
			System.out.print(i+" \t");
		System.out.println();
		boolean firstTime = true;
		for(int i = 0; i < 10; i++){
			firstTime = true;
			for(int j = 0; j < 10; j++){
				if(firstTime){
					System.out.print(i+"\t");
					firstTime = !firstTime;
				}
				System.out.print(cfm.get(i).get(j)+" \t");
			}
			System.out.println();
		}
	}
	
	// calculate odds ratio for the training model
	public void oddsRatio(int c1, int c2, ArrayList<Double> oddsResult, Hashtable<Integer, Integer> trainClassCount, Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model){
//		System.out.println(model.size());
		for(int i = 0; i < model.size(); i++){
			if(model.get(i).get(1) != null){
				double probabResultClass1 = 0.0;
				double probabResultClass2 = 0.0;
				// get conditional probability value for c1 and c2
				if(model.get(i).get(1).get(c1) != null){
					probabResultClass1 = model.get(i).get(1).get(c1);
				}else{
					probabResultClass1 = (smoothingConstant/ ((trainClassCount.get(c1) +( smoothingConstant * 3)) * 1.0));
				}
				
				// for c2
				if(model.get(i).get(1).get(c2) != null){
					probabResultClass2 = model.get(i).get(1).get(c2);
				}else{
					probabResultClass2 = (smoothingConstant/ ((trainClassCount.get(c2) +( smoothingConstant * 3)) * 1.0));
				}
				double res = Math.log(probabResultClass1) - Math.log(probabResultClass2);
				oddsResult.add(res);
				
			}else{
				double probabResultClass1 = (smoothingConstant/ ((trainClassCount.get(c1) +( smoothingConstant * 3)) * 1.0));
				double probabResultClass2 = (smoothingConstant/ ((trainClassCount.get(c2) +( smoothingConstant * 3)) * 1.0));
				double res = Math.log(probabResultClass1) - Math.log(probabResultClass2);
				oddsResult.add(res);
			}
		}
		System.out.println("Odds Ratio for classes "+c1+" and "+c2+" are: ");
		int rowColCount = 0;
		for(int i = 0; i < 28; i++){
			for(int j = 0; j < 28; j++){
				if(oddsResult.get(rowColCount) < 0.0)
					System.out.print("-");
				else if(oddsResult.get(rowColCount) > 0.5 && oddsResult.get(rowColCount) <= 1.0)
					System.out.print(" ");
				else
					System.out.print("+");
				rowColCount++;
			}
			System.out.println();
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		
		TerneryBonus dr = new TerneryBonus();
		System.out.println("Digit Recognition, Ternary features");
		
		String[] trainingFiles = {"traininglabels", "trainingimages", "testlabels", "testimages"};
		
		// read all class labels of training and test data in order
		ArrayList<Integer> trainLabels = new ArrayList<Integer>();
		ArrayList<Integer> testLabels = new ArrayList<Integer>();
		
		// Hashtable to get the class label and its count
		Hashtable<Integer, Integer> trainClassCount = new Hashtable<Integer, Integer>();
		Hashtable<Integer, Integer> testClassCount = new Hashtable<Integer, Integer>();
		
		
		dr.readTrainTestLabels(trainingFiles[0], trainLabels, trainClassCount);
		dr.readTrainTestLabels(trainingFiles[2], testLabels, testClassCount);
		
		// read test data into memory for prediction
		ArrayList<ArrayList<String>> testDataInMemory = new ArrayList<ArrayList<String>>();
		
		dr.loadTestToMemory(trainingFiles[3], testLabels, testDataInMemory);
		
		// Model for NaiveBayes Classifier, 
		Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>> model = new Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Double>>>();
		
		dr.buildModel(trainingFiles[1], model, trainLabels, trainClassCount);
		
		dr.calculateModelProbability(model, trainClassCount);
		
		// calculate odds ratio
		ArrayList<Double> oddsResult = new ArrayList<Double>();
		int c1 = 5;
		int c2 = 0;
//		dr.oddsRatio(c1, c2, oddsResult, trainClassCount, model);

		
		// datastructure for confusion matrix
		Hashtable<Integer, Hashtable<Integer, Double>> cfm = new Hashtable<Integer, Hashtable<Integer, Double>>();
		
		for(int i = 0; i < 10; i++){
			Hashtable<Integer, Double> tHT = new Hashtable<Integer, Double>();
			for(int j = 0; j < 10; j++){				
				tHT.put(j, 0.0);				
			}
			cfm.put(i, tHT);
		}
		
		// predict on Test data set
		dr.predictLabels(trainingFiles[3], trainLabels, testLabels, trainClassCount, testClassCount, testDataInMemory, model, cfm);
		
		// calculate and print confusion matrix
		dr.printConfusionMatrix(testClassCount, cfm);
	}
}
