/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Monjura
 */
import java.util.*;

public class Node {    
    ArrayList data;
	private double entropy;
    private int split_attr;	//attribute used to split data set
    private int split_val; // the attribute-value that is used to divide the parent node
    private int leaf;
    Node []children;
    private Node parent;

    public Node()
    {
    	data = new ArrayList();
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
		this.leaf = l;
	}
	
	public void setParent(Node p){
		this.parent = p;
	}
	
	public double getEntropy(){
		return this.entropy;
	}
	
	public int getAttr(){
		return this.split_attr;
	}
	
	public int getVal(){
		return this.split_val;
	}
	
	public int getLeaf(){
		return this.leaf;
	}
	
	public Node getParent(){
		return this.parent;
	}
}
