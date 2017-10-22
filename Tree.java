
import java.io.*;
import java.util.*;

public class Tree {

    /**
     * @param args the command line arguments
     */
    Node root=new Node();
    ArrayList<Data> allData=new ArrayList();
    ArrayList<Data> trainingData=new ArrayList();
    ArrayList<Data> testingData=new ArrayList();

    int count_attr;

    double TP=0,TN=0,FP=0,FN=0;
    double accuracy1=0,precision1=0,recall1=0,f_measure1=0,g_mean1=0;
    double accuracy2=0,precision2=0,recall2=0,f_measure2=0,g_mean2=0;
    int max1=0,max0=0;

    public static void main(String[] args) {
        // TODO code application logic here
        Tree tr = new Tree();
        tr.readData();
        for(int k=0;k<100;k++)
        {
        	tr.divideData();
	        tr.createTreeImpurity();
	        tr.testData(1);
	        tr.createTreeGain();
	        tr.testData(2);
	        tr.clearData();
        }
        tr.showResult();
    }

    private void showResult()
    {        
        double ac=accuracy1;
        double pc=precision1;
        double rc=recall1;
        double fm=f_measure1;
        double gm=g_mean1;
        System.out.println("Misclassification Impurity");
        System.out.println("accuracy :"+ac+"\nprecision: "+pc+"\nrecall: "+rc+"\nf_measure: "+fm+"\ng_mean :"+gm);        
        ac=accuracy2;
        pc=precision2;
        rc=recall2;
        fm=f_measure2;
        gm=g_mean2;
        System.out.println("Information Gain");
        System.out.println("accuracy :"+ac+"\nprecision: "+pc+"\nrecall: "+rc+"\nf_measure: "+fm+"\ng_mean :"+gm);
    }

    public void performance_evaluate(int type)
	{
		double total=TP+FP+TN+FN;
		double accuracy_temp=0,precision_temp=0,recall_temp=0,f_measure_temp=0,g_mean_temp=0;
	 	accuracy_temp=(TP+TN)/total;
	 	if(type==1)
	 		accuracy1+=accuracy_temp;
	 	else
	 		accuracy2+=accuracy_temp;
		//System.out.println("Accuracy= " + accuracy_temp);
		if((TP+FP)>0)
		{
			precision_temp=TP/(TP+FP);
			if(type==1)
				precision1+=precision_temp;
			else
				precision2+=precision_temp;
		}
		//System.out.println("Precision= " + precision_temp);
		if((TP+FN)>0)
		{
			recall_temp=TP/(TP+FN);
			if(type==1)
				recall1+=recall_temp;
			else
				recall2+=recall_temp;
		}
		//System.out.println("Recall= " + recall_temp);
		if((precision_temp+recall_temp)>0)
		{
			f_measure_temp=(2*precision_temp*recall_temp)/(precision_temp+recall_temp);
			if(type==1)
				f_measure1+=f_measure_temp;
			else
				f_measure2+=f_measure_temp;
		}
		//System.out.println("F-measure= " + f_measure_temp);
		if((TN+FP)>0)
		{
			g_mean_temp=Math.sqrt(recall_temp*TN/(TN+FP));
			if(type==1)
				g_mean1+=g_mean_temp;
			else
				g_mean2+=g_mean_temp;
		}
		//System.out.println("G-mean= " + g_mean_temp);
	}

    public void testData(int type)//type denote impurity or gain
    {
    	String s="";
    	TP=TN=FP=FN=0;
	    for(int i=0;i<testingData.size();i++)
	    {
	        Data d=(Data)testingData.get(i);
	        int r=testSingleData(root,d);
        }
        s+="type "+type+" TP "+TP+" TN "+TN+" FP "+FP+" FN "+FN+"\r\n";
        writeResult(s);
        performance_evaluate(type);
    }

    public int testSingleData(Node node, Data row)
    {
    	int result=-1;
        if (node.children == null || node.getEntropy()==0) //Leaf node
        {
        	int output=row.attributes[count_attr-1];
           	result=node.getLeaf();
            if((node.getLeaf()==output) && (output==1))
            	TP++;
            else if((node.getLeaf()==output) && (output==0))
            	TN++;
            else if((node.getLeaf()!=output) && (output==1))
            	FN++;
            else if((node.getLeaf()!=output) && (output==0))
            	FP++;
            return node.getLeaf();
        }
        for (int i=0;i<node.children.length;i++)
        {
            if(row.attributes[node.getAttr()]==node.children[i].getVal()) //val
            {
                result=testSingleData(node.children[i],row);
            }
        }
        return result;
    }

    public double calEntropy(Node node) //tppe=2
    {
        if(node.data.size()==0)
    		return 0;
        int output=count_attr-1;
        int count1=0,count0=0;
       	for(int j=0;j<node.data.size();j++)
       	{
       		Data d=(Data)node.data.get(j);
       		if(d.attributes[output]==1)
       			count1++;
       		else
       			count0++;
       	}
       	double prob1=(double)count1/node.data.size();
       	double prob0=(double)count0/node.data.size();
       	double pos=0;
       	double neg=0;
       	if(count1>0)
       		pos=-prob1*(Math.log10(prob1)/Math.log10(2));
       	if(count0>0)
       		neg=-prob0*(Math.log10(prob0)/Math.log10(2));
       	double entropy=pos+neg;
        return entropy;
    }

    public double calImp(Node node) //type=1
    {
    	if(node.data.size()==0)
    		return 0;
        int output=count_attr-1;
        int count1=0,count0=0;
       	for(int j=0;j<node.data.size();j++)
       	{
       		Data d=(Data)node.data.get(j);
       		if(d.attributes[output]==1)
       			count1++;
       		else
       			count0++;
       	}
       	double prob1=(double)count1/node.data.size();
       	double prob0=(double)count0/node.data.size();
		double imp=1-Math.pow(prob1,2)-Math.pow(prob0,2);
        return imp;
    }

    public int setResult(Node node)
    {
    	int result;
    	if(max1>max0)
    		result=1;
    	else
    		result=0;
    	int sz=node.data.size();
    	if(sz>0)
    	{
    		Data r=(Data)node.data.get(0);
    		result=r.attributes[count_attr-1];
    	}
    	return result;
    }

    public boolean isUsed(Node node,int attr)
    {
    	if(node.children!=null)
    	{
    		if(node.getAttr()==attr)
    			return true;
    	}
    	if(node.getParent()==null)
    		return false;
    	return isUsed(node.getParent(),attr);
    }

	public ArrayList<Data> getSubset(Node node,int attr,int value)
    {
        ArrayList<Data> subset=new ArrayList();

        for(int i=0;i<node.data.size();i++)
        {
        	Data d=(Data)node.data.get(i);
        	if(d.attributes[attr]==value)
        		subset.add(d);
        }
        return subset;
    }

    public void createTree(Node node,int type)
    {
        boolean selected=false;
        int select_attr=-1;
        double bestGain=0;
        int numAttr=count_attr-1;
		if(type==1)
			node.setEntropy(calImp(node));
		else
			node.setEntropy(calEntropy(node));
        if(node.getEntropy()==0)
        {
        	node.setLeaf(setResult(node));;
       		return;
        }
        for(int i=0;i<numAttr;i++)
        {
            int numvalues=10; //num of diff types of values for an attribute
            if(isUsed(node,i))
                continue;
            double avgEntropy=0;
            //ArrayList<Double> entropies = new ArrayList<Double>();
            for(int j=1;j<=numvalues;j++)
            {
                ArrayList<Data> subset=getSubset(node,i,j);
                if(subset.isEmpty())
                	continue;
                Node temp=new Node();
                temp.data=subset;                
				if(type==1)
				{
					temp.setEntropy(calImp(temp));;
				}
				else
				{
					temp.setEntropy(calEntropy(temp));;
				}
                avgEntropy+=temp.getEntropy()*subset.size();
            }
            avgEntropy=avgEntropy/node.data.size();
            double gain=node.getEntropy()-avgEntropy;
            if (selected==false)
            {
                selected=true;
                bestGain = gain;
                select_attr=i;
            }
            else
            {
                if (gain>bestGain)
                {
                    selected=true;
                    bestGain = gain;
                    select_attr=i;
                }
            }
        }
        //if(selected==false )
        if(select_attr==-1)
            return;

        int numvalues=10;
        node.setAttr(select_attr);

        //System.out.println("selected attr "+select_attr);
        node.children=new Node [numvalues];
        for (int j=0;j<numvalues;j++)
        {
            node.children[j]=new Node();
            node.children[j].setParent(node);
            node.children[j].data=getSubset(node,select_attr,j+1);
            node.children[j].setVal(j+1);
        }
        for (int j=0;j<numvalues;j++)
        {
            createTree(node.children[j],type);
        }
    }

    public void createTreeImpurity()
    {
	    createTree(root,1);
    }

    public void createTreeGain()
    {
    	createTree(root,2);
    }

    public void divideData()
    {
	  	Collections.shuffle(allData);
    	int train_count=(int)Math.ceil(allData.size()*0.8);
        //int test_count=allData.size()-train_count;

        for(int i=0;i<train_count;i++)
        {
        	Data d=(Data)allData.get(i);
        	trainingData.add(d);
        	root.data.add(d);
        	if(d.attributes[count_attr-1]==1)
        		max1++;
        	else
        		max0++;
        }
        for(int i=train_count;i<allData.size();i++)
        {
        	Data d=(Data)allData.get(i);
            testingData.add(allData.get(i));
        }
        root.setParent(null);
    }

    public void readData()
    {
    	accuracy1=0;
    	precision1=0;
    	recall1=0;
    	f_measure1=0;
    	g_mean1=0;

    	accuracy2=0;
    	precision2=0;
    	recall2=0;
    	f_measure2=0;
    	g_mean2=0;

		try
		{
			BufferedReader br=new BufferedReader(new FileReader(new File("data.csv")));
			while(true)
			{
				String s=br.readLine();
				if(s==null)
					break;
				StringTokenizer tokens=new StringTokenizer(s,",");
				count_attr=tokens.countTokens();
				Data row=new Data(count_attr);
				for (int i=0;i<count_attr;i++)
				{
					String str=tokens.nextToken();
					row.attributes[i]=Integer.parseInt(str);
				}
				allData.add(row);
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception : " + e);
		}
    }

    public void writeResult(String s)
    {
        char buffer[]=new char[s.length()];
        s.getChars(0, s.length(), buffer, 0);
        FileWriter fw = null;
        try
        {
            fw = new FileWriter("debug.data",true);
        }
        catch (IOException ex)
        {
            System.out.println("Exception in write file: "+ex);
        }
        try
        {
            fw.write(buffer);
        }
        catch (IOException ex)
        {
            System.out.println("Exception in write file: "+ex);
        }
        try
        {
            fw.close();
        }
        catch (IOException ex)
        {
            System.out.println("Exception: "+ex);
        }
    }

    public void clearData()
    {
        trainingData.clear();
        testingData.clear();
        root.data.clear();
    }
}
