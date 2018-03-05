package dTree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			    
		dataset ds = new dataset();
		ds.readData("data.csv");
		ds.countFeatures();
				   
        for(int k = 1; k <= 100; k++)
        {        	
        	ds.divideData();        	         	        
        	Result imp = ds.BuildandTestTree(1); //impurity	        	        	        
	        Result gain = ds.BuildandTestTree(2); //gain	        	        
	        ds.clearData();
	        //showResult(imp, gain);
	        writeResult(k, imp, gain);
	        //break;
        }
        ds.allClear();               
	}
	
	public static void writeResult(int counter, Result imp, Result gain){
		String s = "";
		s+= "Step " + counter + "\n";
        s+= "Misclassification Impurity\n";
        s+= "Accuracy: " + imp.getAccuracy() + "\n";
        s+= "Precision:" + imp.getPrecision() + "\n";
        s+= "Recall: " + imp.getRecall() + "\n";
        s+= "F_measure: " + imp.getF_Measure() + "\n";        
        s+= "G_mean :" + imp.getG_Mean() + "\n";
        s+= "\nInformation Gain\n";
        s+= "Accuracy: " + gain.getAccuracy() + "\n";
        s+= "Precision:" + gain.getPrecision() + "\n";
        s+= "Recall: " + gain.getRecall() + "\n";
        s+= "F_measure: " + gain.getF_Measure() + "\n";        
        s+= "G_mean :" + gain.getG_Mean() + "\n\n\n";        
        writeFile(s);
	}
	public static void showResult(Result imp, Result gain)
    {                
        System.out.println("Misclassification Impurity");
        System.out.println("Accuracy :"+imp.getAccuracy());
        System.out.println("Precision: "+imp.getPrecision()); 
        System.out.println("Recall: "+imp.getRecall());
        System.out.println("F_measure: "+imp.getF_Measure());
        System.out.println("G_mean :"+imp.getG_Mean());        
        System.out.println("Information Gain");
        System.out.println("Accuracy :"+gain.getAccuracy());
        System.out.println("Precision: "+gain.getPrecision()); 
        System.out.println("Recall: "+gain.getRecall());
        System.out.println("F_measure: "+gain.getF_Measure());
        System.out.println("G_mean :"+gain.getG_Mean());                
    }        

	
	public static void writeFile(String s) {
		String fileName = "result.txt";
        FileWriter fw = null;
        try {
        	File file = new File(fileName);
        	if (!file.exists()) {
				file.createNewFile();
			}
        	fw = new FileWriter(file, true);                   
            fw.write(s);        
            fw.close();
        }
        catch (IOException ex) {
            System.out.println("Exception: " + ex);
        }
    }	    
}
