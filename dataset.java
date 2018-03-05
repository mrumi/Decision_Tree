package dTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.Hashtable;;

public class dataset {
	
	private ArrayList<Data> allData;
	private ArrayList<Data> trainingData;
    private ArrayList<Data> testingData;
    private ArrayList<Integer> feature;
    private Tree tr; 
    private int TP, TN, FP, FN;
    
	public dataset(){
		allData=new ArrayList<Data>();
		trainingData=new ArrayList<Data>();
	    testingData=new ArrayList<Data>();
	    feature = new ArrayList<Integer>();
	    TP = TN = FP = FN = 0;
	}
		
	public void readData(String filename) {
    	int count_attr = 0;
		try {
			BufferedReader br=new BufferedReader(new FileReader(new File(filename)));
			while(true) {
				String s=br.readLine();
				if(s==null)
					break;
				StringTokenizer tokens=new StringTokenizer(s,",");
				count_attr=tokens.countTokens();
				Data row=new Data(count_attr);
				for (int i=0;i<count_attr;i++) {
					String str=tokens.nextToken();
					row.setAttribute(i, Integer.parseInt(str));
				}
				allData.add(row);
			}
			br.close();			
		}
		catch(Exception e) {
			System.out.println("Exception : " + e);
		}		
    }	
	
	public void divideData() {
	  	Collections.shuffle(allData);
    	int train_count=(int)Math.ceil(allData.size()*0.8);     	
        for(int i=0;i<train_count;i++) {        	
        	trainingData.add(allData.get(i));        	
        }
        for(int i=train_count;i<allData.size();i++) {        	
            testingData.add(allData.get(i));
        }        
    }
	
	public ArrayList<Integer> countFeatures(){		
		int numFeatures = allData.get(0).size();		
		for(int i = 0; i < numFeatures - 1; i++){
			Hashtable<Integer, Integer> uniq = new Hashtable<Integer,Integer>();			
			for(int j = 0; j < allData.size(); j++) {
				Data d = (Data)allData.get(j);				
				int feat = d.getAttribute(i);							
				if(uniq.containsKey(feat)){
					uniq.put(feat, uniq.get(feat)+1);					
				}
				else
					uniq.put(feat, 1);									
			}			
			feature.add(uniq.size());
			uniq.clear();
		}												
		return feature;
	}
	
	public Result BuildandTestTree(int type){			
		tr = new Tree(trainingData.get(0).size(), feature);			
		for(int i = 0; i < trainingData.size(); i++){
			tr.addData(trainingData.get(i));
		}				
		tr.generateTree(type);		
		for(int i = 0; i < testingData.size(); i++) {
			Data d = (Data)testingData.get(i);
			int res = tr.testData(d);	
			countResult(res, d.getLabel());
		}		
		tr.memClear();		
		Result result = new Result();
		result.performance_evaluate(TP, FP, TN, FN);
		return result;
	}
	
	public void countResult(int modelRes, int original){
		if(original == 1){
			if(modelRes == 1)
				TP++;
			else
				FN++;
		}
		else{
			if(modelRes == 0)
				TN++;
			else
				FP++;
		}
	}
		        
    public void clearData() {
        trainingData.clear();
        testingData.clear();        
    } 
    
    public void allClear(){
    	this.allData.clear();
    	this.feature.clear();
    }
}
