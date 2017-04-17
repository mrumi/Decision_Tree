package adaboost;

import java.io.*;
import java.util.*;

/**
 *
 * @author Monjura
 */
public class ensamble {
    int count_attr;
    int train_count;
    int numvalues=10;
    Node root=new Node();
    double TP=0,TN=0,FP=0,FN=0;
    ArrayList<Data> allData=new ArrayList();
    ArrayList<Data> trainingData=new ArrayList();
    ArrayList<Data> testingData=new ArrayList();
    ArrayList<Boolean> prediction=new ArrayList<Boolean>();
    ArrayList<Double> weights=new ArrayList<Double>();
    ArrayList<Double> prob=new ArrayList<Double>();
    ArrayList<Integer> result=new ArrayList<Integer>();
    ArrayList<Tree> classifier=new ArrayList();
    ArrayList<Double> beta=new ArrayList<Double>();

    double accuracy1=0,precision1=0,recall1=0,f_measure1=0,g_mean1=0;
    double accuracy2=0,precision2=0,recall2=0,f_measure2=0,g_mean2=0;
    double accuracy3=0,precision3=0,recall3=0,f_measure3=0,g_mean3=0;
    double accuracy4=0,precision4=0,recall4=0,f_measure4=0,g_mean4=0;
    double accuracy5=0,precision5=0,recall5=0,f_measure5=0,g_mean5=0;
    double accuracy6=0,precision6=0,recall6=0,f_measure6=0,g_mean6=0;
    int max1=0,max0=0;

    public static void main(String[] args) {
        ensamble es=new ensamble();
        es.readData();
        for(int k=0;k<100;k++)
        {
            es.divideData();
            es.decisionStump();
		 es.test_Stump();
            es.ID3();
            es.AdaBoost(5);
            es.AdaBoost(10);
            es.AdaBoost(20);
            es.AdaBoost(30);
	        es.clearData();
        }
        es.showResult();
        es.allClear();
    }

    public void test_boost(int r)
    {
        TP=TN=FP=FN=0;
	    for(int i=0;i<testingData.size();i++)
	    {
	        Data d=(Data)testingData.get(i);
            int actual=d.attributes[count_attr-1];
            int predicted=predict(d);
            if(actual==1 && predicted==1)
                TP++;
            else if(actual==1 && predicted==0)
                FN++;
            else if(actual==0 && predicted==1)
                FP++;
            else if(actual==0 && predicted==0)
                TN++;
        }
        //System.out.println("TP= " + TP+" FP="+FP+" TN="+TN+" FN="+FN);
        performance_evaluate("a"+r);
    }

    public int predict(Data row)
    {
        double pos=0,neg=0;
        Tree t=new Tree();
        for(int i=0;i<classifier.size();i++)
        {
            t=classifier.get(i);
            int attr=t.resultboost.get(0);
            int val=row.attributes[attr];
            int pr=(int)t.resultboost.get(val);
            double b=(double)beta.get(i);
            double vote=Math.log(1.0/b);
            if(pr==1)
                pos+=vote;
            else
                neg+=vote;
        }
        if(pos>neg)
            return 1;
        else
            return 0;
    }

    public double check_error()
    {
        int count=0;
        double error_sum=0;
        int attr=(int)result.get(0);
        for(int i=0;i<trainingData.size();i++)
	    {
	        Data d=(Data)trainingData.get(i);
            int actual=d.attributes[count_attr-1];
            int val=d.attributes[attr];
            int predicted=(int)result.get(val);
            if(actual==predicted)
                prediction.add(true);
            else
            {
                error_sum+=(double)prob.get(i);
                prediction.add(false);
                count++;
            }
        }
        //System.out.println("misclassified "+count);
        return error_sum;
    }

    public void update_weight(double beta)
    {
        for(int i=0;i<trainingData.size();i++)
        {
            boolean check=prediction.get(i);
            if(check)
            {
                weights.set(i,weights.get(i)*beta);
            }
        }
        prediction.clear();
    }

    public void AdaBoost(int rounds)
    {
        classifier.clear();
        weights.clear();
        prob.clear();
        beta.clear();
    	int i,k=rounds;
    	int size=train_count;
        for(i=0;i<size;i++)
        {
        	weights.add(1.0/size);
        	prob.add(0.0);
        }
        for(int r=0;r<rounds;r++)
        {
        	double sum=0;
        	for(i=0;i<size;i++)
        		sum+=weights.get(i);
        	for(i=0;i<size;i++)
        	{
        		double probability=weights.get(i)/sum;
                prob.set(i,probability);
        	}
            ////////////////////random data selection
            double running_prob[]=new double [size];
            double p=0;
            for(i=0;i<size;i++)
            {
                p+=(double)prob.get(i);
                running_prob[i]=p;
            }
            root.data.clear();
            for(int j=0;j<train_count;j++)
            {
                Random generator = new Random();
                double random=generator.nextDouble();
                int index=find(random,running_prob);
                if(index>=size)
                {
                    System.out.println("error");
                    j=j-1;
                    continue;
                }
                root.data.add(trainingData.get(index));
            }
            result.clear();
            decisionStump();
            Tree tr=new Tree();
            tr.resultboost=result;
            double error=check_error();
            if(error>.5)
            {
                k=r;
                break;
            }
            double beta_val=error/(1-error);
            classifier.add(tr);
            beta.add(beta_val);
            update_weight(beta_val);
        }
        //System.out.println(k+" "+rounds);
        test_boost(rounds);
    }

    public int find(double query, double[] data)
    {
        int index=-1;
        int max = data.length;
        int min = 0;
        while (min<=max && index==-1)
        {
        	int mid = (max + min) / 2;
            if (mid == 0 && query < data[mid])
                index=mid;
            else if (mid == (data.length - 1) && query <= data[mid])
                index=mid;
            else if (data[mid] <= query && data[mid + 1]>query)
            	index=mid;
            else if (data[mid] > query)
                max = mid;
            else
                min = mid;
        }
        return index;
    }

    public void test_ID3()
    {
    	TP=TN=FP=FN=0;
	    for(int i=0;i<testingData.size();i++)
	        testSingleData(root,testingData.get(i));
        performance_evaluate("i");
    }

    public void testSingleData(Node node, Data row)
    {
        if (node.children == null || node.entropy==0) //Leaf node
        {
        	int actual=row.attributes[count_attr-1];
            int predicted=node.leaf;
            if(actual==1 && predicted==1)
                TP++;
            else if(actual==1 && predicted==0)
                FN++;
            else if(actual==0 && predicted==1)
                FP++;
            else if(actual==0 && predicted==0)
                TN++;
            return;
        }
        for (int i=0;i<node.children.length;i++)
        {
            if(row.attributes[node.split_attr]==node.children[i].split_val)
                testSingleData(node.children[i],row);
        }
    }

    public boolean isUsed(Node node,int attr)
    {
    	if(node.children!=null)
    	{
    		if(node.split_attr==attr)
    			return true;
    	}
    	if(node.parent==null)
    		return false;
    	return isUsed(node.parent,attr);
    }

    public int setResult_ID3(Node node)
    {
    	int output;
    	if(max1>max0)
    		output=1;
    	else
    		output=0;
    	int sz=node.data.size();
    	if(sz>0)
    	{
    		Data r=(Data)node.data.get(0);
    		output=r.attributes[count_attr-1];
    	}
    	return output;
    }

    public void ID3()
    {
    	createTree(root);
        test_ID3();
    }

    public void createTree(Node node)
    {
        boolean selected=false;
        int selected_attr=-1;
        double bestGain=0;
		node.entropy=calEntropy(node);
        if(node.entropy==0)
        {
        	node.leaf=setResult_ID3(node);
       		return;
        }
        for(int i=0;i<count_attr-1;i++)
        {
            if(isUsed(node,i))
                continue;
            double avgEntropy=0;
            for(int j=1;j<=numvalues;j++)
            {
                ArrayList<Data> subset=getSubset(node,i,j);
                if(subset.isEmpty())
                	continue;
                Node temp=new Node();
                temp.data=subset;
				temp.entropy=calEntropy(temp);
                avgEntropy+=temp.entropy*subset.size();
            }
            avgEntropy=avgEntropy/node.data.size();
            double gain=node.entropy-avgEntropy;
            if (selected==false)
            {
                selected=true;
                bestGain = gain;
                selected_attr=i;
            }
            else
            {
                if (gain>bestGain)
                {
                    selected=true;
                    bestGain = gain;
                    selected_attr=i;
                }
            }
        }
        if(selected_attr==-1)
            return;
        node.split_attr=selected_attr;
        //System.out.println("selected attr "+select_attr);
        node.children=new Node[numvalues];
        for (int j=0;j<numvalues;j++)
        {
            node.children[j]=new Node();
            node.children[j].parent=node;
            node.children[j].data=getSubset(node,selected_attr,j+1);
            node.children[j].split_val=j+1;
        }
        for (int j=0;j<numvalues;j++)
            createTree(node.children[j]);
    }

    public void test_Stump()
    {
    	TP=TN=FP=FN=0;
        int attr=(int)result.get(0);
	    for(int i=0;i<testingData.size();i++)
	    {
	        Data d=(Data)testingData.get(i);
            int val=d.attributes[attr];
            int predicted=(int)result.get(val);
            int actual=d.attributes[count_attr-1];
            if(actual==1 && predicted==1)
                TP++;
            else if(actual==1 && predicted==0)
                FN++;
            else if(actual==0 && predicted==1)
                FP++;
            else if(actual==0 && predicted==0)
                TN++;
        }
        //System.out.println("TP= " + TP+" FP="+FP+" TN="+TN+" FN="+FN);
        performance_evaluate("s");
    }

    public void set_result(int attr)
    {
        max1=0;
        max0=0;
        for(int i=0;i<root.data.size();i++)
        {
            Data d=(Data)root.data.get(i);
            if(d.attributes[count_attr-1]==1)
                max1++;
            else
                max0++;
        }
        int output;
    	if(max1>max0)
    		output=1;
    	else
    		output=0;
        result.add(attr);
        for(int j=1;j<=numvalues;j++)
            result.add(output);
        for(int j=1;j<=numvalues;j++)
        {
            int class_max1=0,class_max0=0;
            ArrayList<Data>subset=getSubset(root,attr,j);
            if(subset.isEmpty())
                continue;
            for(int i=0;i<subset.size();i++)
            {
                Data d=(Data)subset.get(i);
                if(d.attributes[count_attr-1]==1)
                    class_max1++;
                else
                    class_max0++;
            }
            if(class_max1>class_max0)
                result.set(j,1);
            else
                result.set(j,0);
        }
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

    public double calEntropy(Node node)
    {
        if(node.data.size()==0)
    		return 0;
        int count1=0,count0=0;
       	for(int j=0;j<node.data.size();j++)
       	{
       		Data d=(Data)node.data.get(j);
       		if(d.attributes[count_attr-1]==1)
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

    public void decisionStump()
    {
        boolean selected=false;
        double bestGain=0;
        root.entropy=calEntropy(root);
        int selected_attr=-1;
        for(int i=0;i<count_attr-1;i++)
        {
            double avgEntropy=0;
            for(int j=1;j<=numvalues;j++)
            {
                ArrayList<Data> subset=getSubset(root,i,j);
                if(subset.isEmpty())
                	continue;
                Node temp=new Node();
                temp.data=subset;
				temp.entropy=calEntropy(temp);
                avgEntropy+=temp.entropy*subset.size();
            }
            avgEntropy=avgEntropy/root.data.size();
            double gain=root.entropy-avgEntropy;
            if (selected==false)
            {
                selected=true;
                bestGain=gain;
                selected_attr=i;
            }
            else
            {
                if (gain>bestGain)
                {
                    selected=true;
                    bestGain=gain;
                    selected_attr=i;
                }
            }
        }
        set_result(selected_attr);
        //System.out.println(selected_attr);
    }

    public void showResult()
    {
        System.out.println("ID3 ");
        System.out.println("accuracy :"+accuracy1+"\nprecision: "
                +precision1+"\nrecall: "+recall1+"\nf_measure: "+f_measure1+"\ng_mean :"+g_mean1);
        System.out.println();

        System.out.println("Decision Stump ");
        System.out.println("accuracy :"+accuracy2+"\nprecision: "+precision2
                +"\nrecall: "+recall2+"\nf_measure: "+f_measure2+"\ng_mean :"+g_mean2);
        System.out.println();

        System.out.println("AdaBoost 5 rounds");
        System.out.println("accuracy :"+accuracy3+"\nprecision: "+precision3
                +"\nrecall: "+recall3+"\nf_measure: "+f_measure3+"\ng_mean :"+g_mean3);
        System.out.println();

        System.out.println("AdaBoost 10 rounds");
        System.out.println("accuracy :"+accuracy4+"\nprecision: "+precision4
                +"\nrecall: "+recall4+"\nf_measure: "+f_measure4+"\ng_mean :"+g_mean4);
        System.out.println();

        System.out.println("AdaBoost 20 rounds");
        System.out.println("accuracy :"+accuracy5+"\nprecision: "+precision5
                +"\nrecall: "+recall5+"\nf_measure: "+f_measure5+"\ng_mean :"+g_mean5);
        System.out.println();

        System.out.println("AdaBoost 30 rounds");
        System.out.println("accuracy :"+accuracy6+"\nprecision: "+precision6
                +"\nrecall: "+recall6+"\nf_measure: "+f_measure6+"\ng_mean :"+g_mean6);
    }

    public void performance_evaluate(String name)
	{
		double total=TP+FP+TN+FN;
		double accuracy_temp=0,precision_temp=0,recall_temp=0,f_measure_temp=0,g_mean_temp=0;
	 	accuracy_temp=(TP+TN)/total;
	 	if(name.matches("i"))
	 		accuracy1+=accuracy_temp;
	 	else if(name.matches("s"))
	 		accuracy2+=accuracy_temp;
        else if(name.matches("a5"))
            accuracy3+=accuracy_temp;
        else if(name.matches("a10"))
            accuracy4+=accuracy_temp;
        else if(name.matches("a20"))
            accuracy5+=accuracy_temp;
        else if(name.matches("a30"))
            accuracy6+=accuracy_temp;
		//System.out.println("Accuracy= " + accuracy_temp);
		if((TP+FP)>0)
		{
			precision_temp=TP/(TP+FP);
            if(name.matches("i"))
                precision1+=precision_temp;
            else if(name.matches("s"))
                precision2+=precision_temp;
            else if(name.matches("a5"))
                precision3+=precision_temp;
            else if(name.matches("a10"))
                precision4+=precision_temp;
            else if(name.matches("a20"))
                precision5+=precision_temp;
            else if(name.matches("a30"))
                precision6+=precision_temp;
		}
		//System.out.println("Precision= " + precision_temp);
		if((TP+FN)>0)
		{
			recall_temp=TP/(TP+FN);
            if(name.matches("i"))
                recall1+=recall_temp;
            else if(name.matches("s"))
                recall2+=recall_temp;
            else if(name.matches("a5"))
                recall3+=recall_temp;
            else if(name.matches("a10"))
                recall4+=recall_temp;
            else if(name.matches("a20"))
                recall5+=recall_temp;
            else if(name.matches("a30"))
                recall6+=recall_temp;
		}
		//System.out.println("Recall= " + recall_temp);
		if((precision_temp+recall_temp)>0)
		{
			f_measure_temp=(2*precision_temp*recall_temp)/(precision_temp+recall_temp);
            if(name.matches("i"))
                f_measure1+=f_measure_temp;
            else if(name.matches("s"))
                f_measure2+=f_measure_temp;
            else if(name.matches("a5"))
                f_measure3+=f_measure_temp;
            else if(name.matches("a10"))
                f_measure4+=f_measure_temp;
            else if(name.matches("a20"))
                f_measure5+=f_measure_temp;
            else if(name.matches("a30"))
                f_measure6+=f_measure_temp;
		}
		//System.out.println("F-measure= " + f_measure_temp);
		if((TN+FP)>0)
		{
			g_mean_temp=Math.sqrt(recall_temp*TN/(TN+FP));
            if(name.matches("i"))
                g_mean1+=g_mean_temp;
            else if(name.matches("s"))
                g_mean2+=g_mean_temp;
            else if(name.matches("a5"))
                g_mean3+=g_mean_temp;
            else if(name.matches("a10"))
                g_mean4+=g_mean_temp;
            else if(name.matches("a20"))
                g_mean5+=g_mean_temp;
            else if(name.matches("a30"))
                g_mean6+=g_mean_temp;
		}
		//System.out.println("G-mean= " + g_mean_temp);
	}

    public void divideData()
    {
	  	Collections.shuffle(allData);
    	train_count=(int)Math.ceil(allData.size()*0.8);
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
            testingData.add(allData.get(i));
        root.parent=null;
    }

    public void readData()
    {
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

    public void clearData()
    {
        trainingData.clear();
        testingData.clear();
        root.data.clear();
        classifier.clear();
        result.clear();
        prediction.clear();
        weights.clear();
        prob.clear();
        beta.clear();
    }

    public void allClear()
    {
        allData.clear();
        trainingData.clear();
        testingData.clear();
        root.data.clear();
        classifier.clear();
        result.clear();
        prediction.clear();
        weights.clear();
        prob.clear();
        beta.clear();
    }
}