package dTree;


/**
 *
 * @author Monjura
 */

public class Data {
    private int []attributes;    
 	public Data(int attr_mum)
 	{
  	   attributes= new int [attr_mum];
 	}
 	
 	public void setAttribute(int pos, int attr){ 		
 		attributes[pos]=attr;
 	}
 	
 	public int getAttribute(int num){
 		return attributes[num];
 	}
 	
 	public int getLabel(){
 		return attributes[attributes.length-1];
 	}
 	
 	public int size(){
 		return attributes.length;
 	}
}
