package dTree;

/**
*
* @author Monjura
*/
import java.util.*;

public class Node {
	private double entropy;
	private ArrayList<Data> node_data;
	private int split_attr;	//attribute used to split data set
	private int split_val; // the attribute-value that is used to divide the parent node
	private int leaf;
	private Node []children;
	private Node parent;
	
	public Node(){
		node_data = new ArrayList<Data>();
   	}
	
	public void insert(Data d){
		node_data.add(d);
	}
	
	public boolean isLeaf(){
		if(this.children==null || this.entropy==0)
			return true;
		return false;
	}
	
	public void createChild(int numChild, Node n, int attr){
		children = new Node[numChild];
		for(int i = 0; i < numChild; i++){
			children[i] = new Node();	
			children[i].setParent(n);
			children[i].getSubset(n, attr, i+1);
			children[i].setVal(i+1);
		}						
	}
	
	public void getSubset(Node pNode, int attr,int value) {        
        for(int i=0;i<pNode.node_data.size();i++) {
        	Data d=(Data)pNode.node_data.get(i);
        	if(d.getAttribute(attr)==value)
        		this.insert(d);        		
        }      
    }
	
	public int numOfChild(){
		if(this.children == null)
			return 0;
		else
			return this.children.length;
	}
	
	public Node getChild(int pos){
		return children[pos];
	}
	
	public int[] passSubset(int attr, int val){
		int pos = 0, neg = 0;		
		for(int i = 0; i < this.node_data.size(); i++){
			Data d = this.node_data.get(i);
			if(d.getAttribute(attr) == val){
				int l = d.getLabel();
				if(l==1)
					pos++;
				else
					neg++;
			}							
		}		
		return new int[]{pos, neg};
	}
	
	public int getSize(){
		return this.node_data.size();
	}
	
	public int getInformation(int pos){
		return this.node_data.get(pos).getLabel();
	}
		
	public void setEntropy(double e){
		this.entropy = e;
	}
	
	public void setAttr(int attr){
		this.split_attr = attr;
	}
	
	public void setVal(int val){
		this.split_val = val;
	}
	
	public void setLeaf(int l){			
		if(this.node_data.size()>0)
			this.leaf = this.node_data.get(0).getLabel();
		else
			this.leaf=l;
	}
	
	public void setParent(Node p){
		this.parent = p;
	}
	
	public double getEntropy(){
		return this.entropy;
	}
	
	public int getSAttr(){
		return this.split_attr;
	}
	
	public int getSVal(){
		return this.split_val;
	}
	
	public int getLeaf(){
		return this.leaf;
	}
	
	public Node getParent(){
		return this.parent;
	}
	
	public void clearMem(){
		node_data.clear();
	}

}
