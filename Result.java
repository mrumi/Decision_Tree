package dTree;

public class Result {
	
	private double accuracy;
	private double precision;
	private double recall;
	private double f_measure;
	private double g_mean;		
    
	public Result(){		
		accuracy = 0;
		precision = 0;
		recall = 0;
		f_measure = 0;
		g_mean = 0;		
	}
	
	public void performance_evaluate(int TP, int FP, int TN, int FN) {
		double total = TP + FP + TN + FN;		
	 	this.accuracy = (TP + TN) / total;
	 		 				
		if((TP + FP) > 0) {
			this.precision = (double)TP / (TP + FP);			
		}		
		if((TP + FN) > 0) {
			this.recall = (double)TP / (TP + FN);
			
		}		
		if((this.precision + this.recall) > 0) {
			this.f_measure = (2 * this.precision * this.recall) / (this.precision + this.recall);			
		}		
		if((TN + FP) > 0) {
			this.g_mean = Math.sqrt(this.recall * TN / (TN + FP));			
		}		
	}
	
	public double getAccuracy(){
		return this.accuracy;
	}		
	
	public double getPrecision(){
		return this.precision;
	}		
	
	public double getRecall(){
		return this.recall;
	}			
	
	public double getF_Measure(){
		return this.f_measure;
	}
	
	public double getG_Mean(){
		return this.g_mean;
	}	

}
