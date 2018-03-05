package dTree;
/**
 *
 * @author Monjura
 */

import java.util.ArrayList;

public class Tree {
                          
    private Node root; 
    private int numAttr;
    private ArrayList<Integer>numValues;
    
    public Tree(int val, ArrayList<Integer>numVal){    	
	    root=new Node();
	    root.setParent(null);
	    numAttr = val;
	    numValues = numVal;	    	    
    }         
    
    public void addData(Data d){
    	root.insert(d);
    }        
    
    public void generateTree(int type) {     		    
    	createTree(root, type);	    
    } 
    
    public int setResult(Node node){  
    	int countPos = 0, countNeg=0;
    	for(int i = 0; i < node.getSize();i++){
    		if(root.getInformation(i)==1)
    			countPos++;
    		else
    			countNeg++;
    	}
    	if(countPos>countNeg)
    		return 1;
    	else return 0;
    }
    
    public void createTree(Node node, int type) {                 
		node.setEntropy(calEntropy(node, type));		
		if(node.getEntropy()==0) { 
			int res = setResult(node.getParent());
			node.setLeaf(res);        	
       		return;
        }		
		boolean selected=false;
        int select_attr=-1;
        double bestGain=0;
		
        for(int i = 0; i < numAttr - 1; i++) {                   
            if(isUsed(node, i))
                continue;
            double avgEntropy = 0;            
            for(int j = 0; j < this.numValues.get(i); j++) {
            	int [] sub = node.passSubset(i,j+1);            	
            	int subSize = sub[0]+sub[1];
            	avgEntropy+= this.myEntropy(sub, type)+subSize;                
            }
            
            avgEntropy=avgEntropy/node.getSize();
            double gain=node.getEntropy()-avgEntropy;
            if (selected==false) {            
                selected=true;
                bestGain = gain;
                select_attr=i;
            }
            else if (gain>bestGain) {
            	selected=true;
            	bestGain = gain;
            	select_attr=i;               
            }
        }
        //if(selected==false )
        if(select_attr==-1)
            return;        
        node.setAttr(select_attr);                        
        int numChild = this.numValues.get(select_attr-1);        
        node.createChild(numChild, node, select_attr);         
        for (int j = 0; j < numChild; j++) {
        	//System.out.println(node.getChild(j).getSize());        	
            createTree(node.getChild(j), type);
        }        
    }     
        
    public double calEntropy(Node node, int type) {
    	int size = node.getSize();
        if(size == 0)
    		return 0;        
        int count1 = 0, count0 = 0;
       	for(int j = 0; j < size; j++) {       	      		
       		if(node.getInformation(j) == 1)
       			count1++;
       		else
       			count0++;
       	}
       	return myEntropy(new int[]{count1, count0}, type);
    }
    public double myEntropy(int[]labels, int type){
    	int count1 = labels[0], count0 = labels[1];
    	int size = count1 + count0;
       	double prob1 = (double)count1/size;
       	double prob0 = (double)count0/size;
       	if(type == 1)
        	return (1-Math.pow(prob1,2)-Math.pow(prob0,2));
       	else{       		
       		double pos = 0;
           	double neg = 0;
           	if(count1 > 0)
           		pos = -prob1*(Math.log10(prob1)/Math.log10(2));
           	if(count0>0)
           		neg = -prob0*(Math.log10(prob0)/Math.log10(2));           	
            return (pos+neg);   		
       	}                      
    }        

    public boolean isUsed(Node node, int attr) {    
    	if(node.getParent() == null)
    		return false;    	
    	if(node.getSAttr() == attr)
    			return true;    	    	
    	return isUsed(node.getParent(), attr);
    }	
    
    public int testSingleData(Node node, Data row) {
    	int result = -1;
        if (node.isLeaf()) { //Leaf node     	        			           	           
            return node.getLeaf();
        }        
        int sattr = node.getSAttr();
        int dataval = row.getAttribute(sattr);
        // go to associate children
        for (int i = 0; i < node.numOfChild(); i++) {        	
            if(dataval==node.getChild(i).getSVal()) {
                result=testSingleData(node.getChild(i),row);
            }
        }
        return result;
    }        

    public int testData(Data d){
    	return this.testSingleData(root, d);
    }
    
    public void memClear(){
    	recursiveClear(root);
    }  
    
    private void recursiveClear(Node node){
    	node.clearMem();
    	if(!node.isLeaf()){
    		for(int i = 0; i < node.numOfChild() ; i++)    			
    				recursiveClear(node.getChild(i));    		    		
    	}    		    	
    }
        
}
