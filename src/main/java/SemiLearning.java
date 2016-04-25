import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import Jama.Matrix;

/**
 * 
 * @author Junming Shao
 * @param None
 * Created on 2. Jan. 2013, 
 */


public class SemiLearning{
	
    public ArrayList<SyncObject> src; 
    public ArrayList<SyncObject> iData; 
    public ArrayList<SyncObject> trainTree;
    public int numInit;
    public int dim;
    public int numClass;
    public double[] max;
    public double[] min;
    public int proN;
    
 
    public SemiLearning() {

    }
    

    /**
     * Load the inital data from a given filename and/or normalize it
     * @param Filename, normFlag, InitSize
     * @return none
     */
    
    public ArrayList<SyncObject> loadInitData(String fn, boolean normFlag, int InitSize, String sp){            
    	
    	numInit = 0;
        try {
        	File mFile = new File(fn);
        	FileReader fr = new FileReader(mFile);
        	BufferedReader br = new BufferedReader(fr);
        	String line;
        	iData = new ArrayList<SyncObject>();
        	while((line=br.readLine())!=null && numInit<InitSize){

        		line = line.trim();        		
        		String[] strs = line.split(sp);
        		int dimm = strs.length;
        		double[] temp = new double[dimm-1];
        		int d= 0;
        		for(int i=0;i<dimm-1;i++){      			
    				temp[d] = Double.parseDouble(strs[i]);
    				d++;
        		}
        		dim = d;
    		
    			iData.add(new SyncObject(temp,Integer.parseInt(strs[dimm-1])));
    			numInit = numInit + 1;
        	}      
        	br.close();
        	fr.close();
        	
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
       
        if(normFlag){
        	iData = norm(iData, fn, sp);
        }
        System.out.println(iData.size()+","+iData.get(0).data.length);
        return iData;

        
    }
    public ArrayList<SyncObject> loadInitData(String fn, boolean normFlag, int InitSize, String dataname, String sp){            
    	
    	numInit = 0;
        try {
        	File mFile = new File(fn);
        	FileReader fr = new FileReader(mFile);
        	BufferedReader br = new BufferedReader(fr);
        	String line;
        	iData = new ArrayList<SyncObject>();
        	while((line=br.readLine())!=null && numInit<InitSize){
        		line = line.trim();        		
        		String[] str = line.split(sp);
        		int dimm = str.length;
        		double[] temp = new double[dimm-1];
        		dim = 0;
        		for(int i=0;i<dimm-1;i++){
        			if(i!=1 && i!=2 && i!=3 && i!=6 && i!=11 && i!=20 &&i!=21){
		        		temp[dim] = Double.parseDouble(str[i]);
		        		dim++;
        			}	        		
        		}

        		if(str[dimm-1]=="normal.")
        			iData.add(new SyncObject(temp,1));
        		else
        			iData.add(new SyncObject(temp,2));	
        		
    			numInit = numInit + 1;
        	}      
        	br.close();
        	fr.close();
        	
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
       
        System.out.println("Number of initial objects = " + numInit + "; dimensionality = " + dim);
       
        if(normFlag){
        	iData = norm(iData, fn,sp);
        }
        
        return iData;

        
    }
    
    /**
     * Normalize the data set into [0 pi/2]
     * @param ArrayList data (result)
     * @return ArrayList R
     */
    public ArrayList<SyncObject> norm(ArrayList<SyncObject> data, String fn, String sp){
       int n = data.size();
       int d = ((SyncObject)data.get(0)).data.length;
        max = new double[d];
        min = new double[d];
       
       for(int i=0;i<d;i++){
    	   max[i]= -Double.MAX_VALUE;
    	   min[i]= Double.MAX_VALUE;
       }

       
       try {
       	File mFile = new File(fn);
       	FileReader fr = new FileReader(mFile);
       	BufferedReader br = new BufferedReader(fr);
       	String line;
       	while((line=br.readLine())!=null){
       		line = line.trim();        		
       		String[] str = line.split(sp);
       		int dimm = str.length;
       		int dd = 0;
       		for(int i=0;i<dimm-1;i++){         			
	   			  double temp = Double.parseDouble(str[i]);
	   				
	      		  if(temp > max[dd]){
	    			  max[dd]= temp;
	    		  }    		  
	    		  if(temp < min[dd]){
	    			  min[dd]= temp;
	    		  }
	    		  dd++;
       		}      		
       	}      
       	br.close();
       	fr.close();
       	
       } catch (IOException ex) {
           ex.printStackTrace();
       } 
       
             
       //Normalize the data set to [0, pi/2] for each dimension
       for(int i=0;i<n;i++){
     	  for(int j=0;j<d;j++){
     		  double temp = (Double)((SyncObject)data.get(i)).data[j];
     		  if(max[j]!=min[j])
     			  temp = 0.5*Math.PI*(temp-min[j])/(max[j]-min[j]);     
     		  else
     			  temp = max[j];
     		 ((SyncObject)data.get(i)).data[j] = temp;
     	  }
        }
       
       return data; 
    }   
    
    public double EuclideanDist(double[] dis){
    	
    	double val = 0.0;
    	for(int i=0;i<dis.length;i++){
    		val = val + dis[i]*dis[i];
    	}   
    	double dist = Math.sqrt(val);
    	return dist;
    }
    
    
    public double dist(double[] al1, double[] al2){
    	double res = 0.0;
    	
    	if(al1.length!=al2.length) return 0.0;
    	else{
    		double[] diss = new double[al1.length];
			for(int d=0;d<al1.length;d++){
				diss[d] = al1[d]-al2[d];		
			}
			res = EuclideanDist(diss);
    	}
    	
    	return res;
    	
    }     
    
    public double kNN(ArrayList<SyncObject>data, int k){
    	
    	  double res = 0.0;
    	 
    	  ArrayList<SyncObject> src = data;

    	  // check inputs for validity
    	  if(src.size() == 0 || k<=0) return 0.0; // bail on bad input
    	 
    	  for(int s=0;s<src.size();s++){  
    		  
        	  double[] d = new double[k];
        	  int n = 0; // number of element in the res
    		  
  	    	  double dd = dist(((SyncObject)src.get(s)).data,((SyncObject)src.get(0)).data); // load first one into list
  	    	  d[n++] = dd;
  	    	 
  	    	  // go through all other data points
  	    	  for(int i = 1; i<src.size(); i++){
  	    		dd = dist(((SyncObject)src.get(s)).data,((SyncObject)src.get(i)).data);  
  	    	    if( n<k || d[k-1] > dd){ //add one
  	    	      if(n<k){
  	    	    	  d[n++] = dd;
  	    	      }
  	    	      int j = n-1;
  	    	      while(j>0 && dd < d[j-1]){ // slide big data up
  	    	        d[j] = d[j-1]; 
  	    	        j--;
  	    	      }
  	    	      d[j] = dd;
  	    	    }
  	    	  }
  	    	  
  	    	  res = res + d[k-1];
    	  }
    	  return res/src.size();
    	}
    
    
    
    
   public int kNNClassifier(StreamHierTree sht, SyncObject test, int k){
	   
	   int prelabel = 0;
	   
	   
	  // check inputs for validity
	  if(sht.leafLevel.size() == 0 || k<=0) return -1000; //  bad input
	 	  
	   ArrayList<SyncObject> data = new ArrayList<SyncObject>();
	   
	   //root objects	   
	   for(int i=0;i<sht.rootLevel.size();i++){
		   ArrayList<SyncObject> temp = sht.rootLevel.get(i);
		   for(int j=0;j<temp.size();j++){
			   data.add(temp.get(j));
		   }
	   }
	   
	   // node objects
	   for(int i=0;i<sht.nodeLevel.size();i++){
		   ArrayList<SyncObject> temp = sht.nodeLevel.get(i);
		   for(int j=0;j<temp.size();j++){
			   data.add(temp.get(j));
		   }
	   }
	   
	   // leaf objects	   
	   for(int i=0;i<sht.leafLevel.size();i++){
		   ArrayList<SyncObject> temp = sht.leafLevel.get(i);
		   for(int j=0;j<temp.size();j++){
			   data.add(temp.get(j));
		   }
	   }
	   
	   
 	  // check inputs for validity
 	  if(data.size() == 0 || data.size()<k) return -1000; //  bad input
 	  
 	  double[] dis = new double[data.size()];
 	  int[] f = new int[data.size()];
 	  int[] idx = new int[k];
 	  for(int s=0;s<data.size();s++){  		  
 		 dis[s] = dist(data.get(s).data,test.data);
 		 f[s] = 0;
 	  }
 	  
 	  for(int i=0;i<k;i++){
 		  double minV = Double.MAX_VALUE;
 		  for(int j=0;j<data.size();j++){
 			  if(dis[j]<minV && f[j]!=1){
 				  minV = dis[j];
 				  idx[i] = j;
 			  }
 		  }
 		  f[idx[i]] = 1;
 	  }
 		  
 	  
 	  int[] count = new int[numClass+1]; // e.g. 0 - unlabeled data, 1- class 1(positive);  2 - class 2 (negative)
 	  for(int i=0;i<k;i++){
 		  SyncObject o = data.get(idx[i]);
 		  count[o.label]++;
 	  }

 	  int maxN = 0;
 	  int idd = -1;
 	  for(int i=1;i<=numClass;i++){
 		  if(count[i]>maxN){
 			  idd = i;
 			  maxN = count[i];
 		  }
 	  }
 	  
 	  // check whether several classes have the same counts
 	  boolean flag = false;
 	  
 	  if(maxN>0){
	 	  for(int i=1;i<=numClass;i++){
	 		  if(i!=idd && maxN == count[i]){
	 			  flag = true;
	 		  }
	 	  }
 	  }else{
 		  flag = true;
 	  }
 	  
 	  if(flag){ 		  
 		  	prelabel = kNNClassifier(sht, test, k*2);
 	  }else{
		    prelabel = idd;
		  
	   }
	   
	   return prelabel;
    	
    }
    
public int kNNWeightedClassifier(StreamHierTree sht, SyncObject test, int k, boolean noise){
	   
	   int prelabel = 0;
	   
	   
	  // check inputs for validity
	  if(sht.leafLevel.size() == 0 || k<=0) return -1000; //  bad input
	 	  
	   ArrayList<SyncObject> data = new ArrayList<SyncObject>();
	   
	   // leaf objects	   
	   for(int i=0;i<sht.leafLevel.size();i++){
		   ArrayList<SyncObject> temp = sht.leafLevel.get(i);
		   for(int j=0;j<temp.size();j++){
			   data.add(temp.get(j));
		   }
	   }
	   
	   
 	  // check inputs for validity
 	  if(data.size() == 0 || data.size()<k) return -1000; //  bad input
 	  
 	  double[] dis = new double[data.size()];
 	  int[] f = new int[data.size()];
 	  int[] idx = new int[k];
 	  for(int s=0;s<data.size();s++){  		  
 		 dis[s] = dist(data.get(s).data,test.data);
 		 f[s] = 0;
 	  }
 	  
 	  for(int i=0;i<k;i++){
 		  double minV = Double.MAX_VALUE;
 		  for(int j=0;j<data.size();j++){
 			  if(dis[j]<minV && f[j]!=1){
 				  minV = dis[j];
 				  idx[i] = j;
 			  }
 		  }
 		  f[idx[i]] = 1;
 	  }
 		  
 	  
 	  double[] wdist = new double[numClass+1]; // e.g. 0 - unlabeled data, 1- class 1(positive);  2 - class 2 (negative)
 	  for(int i=0;i<k;i++){
 		  SyncObject o = data.get(idx[i]);
 		  wdist[o.label]+= 1.0/dis[idx[i]];
 	  }

 	  double maxN = 0;
 	  int idd = -1;
 	  for(int i=1;i<=numClass;i++){
 		  if(wdist[i]>maxN){
 			  idd = i;
 			  maxN = wdist[i];
 		  }
 	  }
 	  
 	  // check whether several classes have the same counts
 	  boolean flag = false;
 	  
 	  if(maxN>0){
	 	  for(int i=1;i<=numClass;i++){
	 		  if(i!=idd && maxN == wdist[i]){
	 			  flag = true;
	 		  }
	 	  }
 	  }else{
 		  flag = true;
 	  }
 	  
 	  if(flag){ 		  
 		  	prelabel = kNNClassifier(sht, test, k*2+1);
 	  }else{
		    prelabel = idd;
		  
	   }
 	  
 	  
 	  ArrayList al = new ArrayList();
 	  for(int i=0;i<k;i++){
 		  SyncObject o = data.get(idx[i]);
 		  if(o.label==idd){
 			  al.add(o);
 		  }
 	  }
 	  
	  if(prelabel==test.label){
		  for(int x=0;x<al.size();x++){
			  ((SyncObject)al.get(x)).addQC();
		  }
	  }else{
		  for(int x=0;x<al.size();x++){
			  ((SyncObject)al.get(x)).minusQC();
		  }
		  if(noise){
			 test.minusQC();
			  for(int x=0;x<al.size();x++){
				  data.remove((SyncObject)al.get(x));
			  }
		 } 				  
	  }	  
	   
	   return prelabel;
    	
    }  
      

public void checkHier(String fn, StreamHierTree sht){
	
		ArrayList<SyncObject> data = new ArrayList<SyncObject>();
	
	   // leaf objects	   
	   for(int i=0;i<sht.leafLevel.size();i++){
		   ArrayList<SyncObject> temp = sht.leafLevel.get(i);
		   for(int j=0;j<temp.size();j++){
			   data.add(temp.get(j));
		   }
	   }
	   
	   IO ios = new IO();
	   ios.saveSyncClusters(data, fn);
}


public int kNNWHierClassifier(StreamHierTree sht, SyncObject test, String type, boolean noise){
	   
	   int prelabel = 0;	   
	   
	  // check inputs for validity
	  if(sht.leafLevel.size() == 0) return 0; //  bad input
	 	  
	   ArrayList<SyncObject> data = new ArrayList<SyncObject>();
	   	   
	   // leaf objects	   
	   for(int i=0;i<sht.leafLevel.size();i++){
		   ArrayList<SyncObject> temp = sht.leafLevel.get(i);
		   for(int j=0;j<temp.size();j++){
			   data.add(temp.get(j));
		   }
	   }
	   
	   // node objects (concepts)
	   if(type.equalsIgnoreCase("s"))
		   for(int i=0;i<sht.nodeLevel.size();i++){
			   ArrayList<SyncObject> temp = sht.nodeLevel.get(i);
			   for(int j=0;j<temp.size();j++){
				   data.add(temp.get(j));
			   }
		   }
	   
	  // check inputs for validity
	  if(data.size() == 0) return 0; //  bad input
	  
	  double[] dis = new double[data.size()];
	  for(int s=0;s<data.size();s++){  		  
		 dis[s] = dist(data.get(s).data,test.data);
	  }
	  

	  double minV = Double.MAX_VALUE;
	  int idd = -1;
	  for(int j=0;j<data.size();j++){
		  if(dis[j]<minV){
			  minV = dis[j];
			  idd = j;
		  }
	  }

	  if(idd==-1){
		  System.out.println("Error:" + dis[0]);
	  }
	  prelabel = data.get(idd).label;	
	  
	  
	  if(prelabel==test.label){
		  data.get(idd).addQC();
	  }else{
		 data.get(idd).minusQC();
		 if(noise){
			 test.minusQC();
			 data.remove(idd);
		 }
	  }
	  return prelabel;
 }  


public int kNNHierClassifier(StreamHierTree sht, SyncObject test, int k, double r){
	   
	   int prelabel = 0;
	   
	   
	  // check inputs for validity
	  if(sht.leafLevel.size() == 0 || k<=0) return 0; //  bad input
	 	  
	   ArrayList<SyncObject> data = new ArrayList<SyncObject>();
	   	   
	   // leaf objects	   
	   for(int i=0;i<sht.leafLevel.size();i++){
		   ArrayList<SyncObject> temp = sht.leafLevel.get(i);
		   for(int j=0;j<temp.size();j++){
			   data.add(temp.get(j));
		   }
	   }
	   
	  // check inputs for validity
	  if(data.size() == 0 || data.size()<k) return 0; //  bad input
	  
	  double[] dis = new double[data.size()];
	  int[] f = new int[data.size()];
	  ArrayList<int[]> idx = new ArrayList<int[]>();
	  for(int s=0;s<data.size();s++){  		  
		 dis[s] = dist(data.get(s).data,test.data);
		 f[s] = 0;
	  }
	  

	  for(int i=0;i<k;i++){
		  double minV = Double.MAX_VALUE;
		  int[] temp = new int[2];
		  for(int j=0;j<data.size();j++){
			  if(dis[j]<minV && f[j]!=1){
				  minV = dis[j];
				  temp[0] = j;
				  temp[1] = 1;
			  }
		  }
		  idx.add(temp);
		  f[temp[0]] = 1;
	  }
		  
	  
	  double[] wdist = new double[numClass+1]; // e.g. 0 - unlabeled data, 1- class 1(positive);  2 - class 2 (negative)
	  double sum = 0;
	  for(int i=0;i<k;i++){
		  SyncObject o = data.get(idx.get(i)[0]);
		  wdist[o.label]+= o.nl/(dis[idx.get(i)[0]]*o.num);
		  sum = sum + o.nl/(dis[idx.get(i)[0]]*o.num);
	  }

	  double maxN = 0;
	  int idd = -1;
	  for(int i=1;i<=numClass;i++){
		  if(wdist[i]>maxN){
			  idd = i;
			  maxN = wdist[i];
		  }
	  }
	  
	  
	  if(maxN>=r*sum || sht.nodeLevel.size()==0){
	  
		  // check whether several classes have the same counts
		  boolean flag = false;
		  
		  if(maxN>0){
		 	  for(int i=1;i<=numClass;i++){
		 		  if(i!=idd && maxN == wdist[i]){
		 			  flag = true;
		 		  }
		 	  }
		  }else{
			  flag = true;
		  }
		  
		  if(flag){ 		  
			  	prelabel = kNNClassifier(sht, test, k*2);
		  }else{
			    prelabel = idd;
		   }
		  
	  }else{
		  // node level
		   ArrayList<SyncObject> data2 = new ArrayList<SyncObject>();
	   	      
		   // node objects
		   for(int i=0;i<sht.nodeLevel.size();i++){
			   ArrayList<SyncObject> temp = sht.nodeLevel.get(i);
			   for(int j=0;j<temp.size();j++){
				   data2.add(temp.get(j));
			   }
		   }
		   
		   if(data2.size() == 0 || data2.size()<k) return idd; 
		   
		  
		  double[] dis2 = new double[data2.size()];
		  for(int s=0;s<data2.size();s++){  		  
			 dis2[s] = dist(data2.get(s).data,test.data);

		  }

		  for(int j=0;j<data2.size();j++){
			  for(int i=0;i<k;i++){			  
				  if((idx.get(i)[0]==1 && dis2[j]<dis[idx.get(i)[0]]) || (idx.get(i)[0]==2 && dis2[j]<dis2[idx.get(i)[0]]) ){

					  int [] temp = new int[2];
					  temp[0] = j;
					  temp[1] = 2;
					  
					  idx.add(i,temp);
                   idx.remove(k);
                   break;
				  }
			  }
		  }
			  
		  
		  double[] wdist2 = new double[numClass+1]; // e.g. 0 - unlabeled data, 1- class 1(positive);  2 - class 2 (negative)
		  double sum2 = 0;
		  for(int i=0;i<k;i++){

			  if(idx.get(i)[1]==1){
				  SyncObject o = data.get(idx.get(i)[0]);
				  wdist2[o.label]+= o.nl/(dis[idx.get(i)[0]]*o.num);; 
				  sum2 = sum2 + o.nl/(dis[idx.get(i)[0]]*o.num);;
			  }else{
				  SyncObject o = data2.get(idx.get(i)[0]);
				  wdist2[o.label]+= o.nl/(dis2[idx.get(i)[0]]*o.num);;
				  sum2 = sum2 + o.nl/(dis2[idx.get(i)[0]]*o.num);;
			  }
		  }

		  double maxN2 = 0;
		  int idd2 = -1;
		  for(int i=1;i<=numClass;i++){
			  if(wdist2[i]>maxN2){
				  idd2 = i;
				  maxN2 = wdist2[i];
			  }
		  }
		  		  
		  
		  if(maxN2>=r*sum2 || sht.rootLevel.size()==0){
			  
			  // check whether several classes have the same counts
			  boolean flag = false;
			  
			  if(maxN2>0){
			 	  for(int i=1;i<=numClass;i++){
			 		  if(i!=idd2 && maxN2 == wdist2[i]){
			 			  flag = true;
			 		  }
			 	  }
			  }else{
				  flag = true;
			  }
			  
			  if(flag){ 		  
				  	prelabel = kNNClassifier(sht, test, k*2);
			  }else{
				    prelabel = idd2;
			   }
		  }else{
			  
			  // root level
			   ArrayList<SyncObject> data3 = new ArrayList<SyncObject>();
		   	      
			   // node objects
			   for(int i=0;i<sht.rootLevel.size();i++){
				   ArrayList<SyncObject> temp = sht.rootLevel.get(i);
				   for(int j=0;j<temp.size();j++){
					   data3.add(temp.get(j));
				   }
			   }
			   
			   if(data3.size() == 0 || data3.size()<k) return idd2; //  bad input
			  
			  double[] dis3 = new double[data3.size()];
			  for(int s=0;s<data3.size();s++){  		  
				 dis3[s] = dist(data3.get(s).data,test.data);

			  }
			  
			  
			  for(int j=0;j<data3.size();j++){
				  for(int i=0;i<k;i++){
					  if((idx.get(i)[1]==1 && dis3[j]<dis[idx.get(i)[0]]) || (idx.get(i)[1]==2 && dis3[j]<dis2[idx.get(i)[0]]) ||  (idx.get(i)[1]==3 && dis3[j]<dis3[idx.get(i)[0]])){
						  int [] temp = new int[2];
						  temp[0] = j;
						  temp[1] = 3;
						  
						  idx.add(i,temp);
	                      idx.remove(k);
	                      break;
					  }
				  }
			  }
				  
			  
			  double[] wdist3 = new double[numClass+1]; // e.g. 0 - unlabeled data, 1- class 1(positive);  2 - class 2 (negative)
			  double sum3 = 0;
			  for(int i=0;i<k;i++){

				  if(idx.get(i)[1]==1){
					  SyncObject o = data.get(idx.get(i)[0]);
					  wdist3[o.label]+= o.nl/(dis[idx.get(i)[0]]*o.num);; 
					  sum3 = sum3 + o.nl/(dis[idx.get(i)[0]]*o.num);;
				  }else if(idx.get(i)[1]==2){
					  SyncObject o = data2.get(idx.get(i)[0]);
					  wdist3[o.label]+= o.nl/(dis2[idx.get(i)[0]]*o.num);
					  sum3 = sum3 + o.nl/(dis2[idx.get(i)[0]]*o.num);;
				  }else{
					  SyncObject o = data3.get(idx.get(i)[0]);
					  wdist3[o.label]+= o.nl/(dis3[idx.get(i)[0]]*o.num);;
					  sum3 = sum3 + o.nl/(dis3[idx.get(i)[0]]*o.num);;					  
				 }
			}
			  
			  double maxN3 = 0;
			  int idd3 = -1;
			  for(int i=1;i<=numClass;i++){
				  if(wdist3[i]>maxN3){
					  idd3 = i;
					  maxN3 = wdist3[i];
				  }
			  }	  
					  
			  // check whether several classes have the same counts
			  boolean flag = false;
			  
			  if(maxN3>0){
			 	  for(int i=1;i<=numClass;i++){
			 		  if(i!=idd3 && maxN3 == wdist3[i]){
			 			  flag = true;
			 		  }
			 	  }
			  }else{
				  flag = true;
			  }
			  
			  if(flag){ 		  
				  	prelabel = kNNClassifier(sht, test, k*2);
			  }else{
				    prelabel = idd3;				  
			   }			  
		  
		  }
	  }
	   
	   return prelabel;
	
}  

 public void checkData(String fn){
	 
  	ArrayList al = new ArrayList();
  	double[][] test = {{1, 2, 3},{2, 3, 4},{1, 2, 3},{2, 3, 4}};
  	int n = 0;
  	
  	
     try {
     	File mFile = new File(fn);
     	FileReader fr = new FileReader(mFile);
     	BufferedReader br = new BufferedReader(fr);
     	String line;
     	


     	while((line=br.readLine())!=null){
     		line = line.trim();        		
     		String[] str = line.split(",");
     		dim = str.length;
     		double[] temp = new double[dim-1];
     		double s = 0;

     		for(int i=0;i<dim-1;i++){
     			temp[i] = Double.parseDouble(str[i]);	
     			s += temp[i];
     		}
 			al.add(s);
 			n = n + 1;
     	}      
     	br.close();
     	fr.close();
     	
     } catch (IOException ex) {
         ex.printStackTrace();
     } 
     
  	 HashSet set = new HashSet(al);
  	 System.out.println(n+"," +set.size() + ","+(n-(set.size())));

  	
  	
 }
 
  public double angle(double[][] a, double[][] b){
	  double r = 0.0, s = 0.0, t = 0.0, theta = 0.0;
	  double maxT = 0.0;
	  double avgT = 0.0;
	  int n = 0;
	  for(int k=0;k<a.length;k++){
		  for(int i=0;i<a[0].length;i++){
			  r = r+a[k][i]*a[k][i];
		  }
		  for(int i=0;i<a[0].length;i++){
			  s = s+b[k][i]*b[k][i];
		  }
		  
		  for(int i=0;i<a[0].length;i++){
			  t = t+a[k][i]*b[k][i];
		  }
		  
		  if(s!=0 && r!=0 && t!=0){
			  theta = Math.acos(t/(Math.sqrt(r)*Math.sqrt(s)));
			  avgT += theta;
			  n++;
			  if(theta>maxT){
				  maxT = theta;
			  }
		  }
	  }
	  
	  return maxT;	  
  }
  
  public double PCA_Angle(ArrayList<SyncObject> a, ArrayList<SyncObject> b, int C){

	  double maxT = 0.0;
	  double avgT = 0.0;
	  int n = 0;
	  
	  int[] fa = new int[C];
	  int[] fb = new int[C];
	  
	  
	  for(int k=0;k<C;k++){
		  int n1 = 0, n2=0;
		  double dp = 0.0, theta = 0.0;
		  for(int i=0;i<a.size();i++){
			  if(a.get(i).label==k){
				  fa[k] = 1;
				  n1++;
		      }
		  }
			  
		  for(int i=0;i<b.size();i++){
			  if(b.get(i).label==k){
				  fb[k] = 1;
				  n2++;
		      }
		  }	
		  
		  if(fa[k]==1 && fb[k]==1){
			  double[][] temp1 = new double[n1][]; 
			  double[][] temp2 = new double[n2][]; 
			  int t1 = 0, t2 = 0;
			  for(int i=0;i<a.size();i++){
				  if(a.get(i).label==k){
					  temp1[t1] = a.get(i).data;
					  t1++;
			      }
			  }
			  for(int i=0;i<b.size();i++){
				  if(b.get(i).label==k){
					  temp2[t2] = b.get(i).data;
					  t2++;
			      }
			  }		
			  
			  Matrix m1 = new Matrix(temp1);
			  Matrix m2 = new Matrix(temp2);
			  m1 = m1.transpose();
			  m2 = m2.transpose();
			  PCA pca1 = new PCA();			  
			  boolean f1 = pca1.eigenPCA(m1, false, false);;
			  PCA pca2 = new PCA();
			  boolean f2 = pca2.eigenPCA(m2, false, false);	
			  if(f1 && f2){
				  
				  double[] fC1 = pca1.getFirstPC();
				  double[] fC2 = pca2.getFirstPC();			  
			  		  
				  for(int i=0;i<fC1.length;i++){
					  dp = dp+fC1[i]*fC2[i];
				  }
				  
				  if(dp!=0){
					  theta = Math.acos(Math.abs(dp));
					  if(theta>Math.PI/2)
						  theta= Math.PI - theta;
					  avgT += theta;
					  n++;
					  if(theta>maxT){
						  maxT = theta;
					  }
				  }
				  
			  }
		  }
	}
	  
	  return maxT;	  
  }  
  
  
  public void formatArff(String fin, String fout) throws FileNotFoundException, IOException{
	  

		String sp = ",";		
		String header ="@relation covtype\n\n";
		
		for(int i=0;i<34;i++){
			header = header + "@attribute Att" +(i+1)+" numeric\n"; 
		}
		header = header + "@attribute class {back,buffer_overflow,ftp_write,guess_passwd,imap,ipsweep,land,loadmodule,multihop,neptune,nmap,normal,perl,phf,pod,portsweep,rootkit,satan,smurf,spy,teardrop,warezclient,warezmaster}\n";
		header = header + "@data\n";
		
		FileWriter fileWriter = new FileWriter(new File(fout),true);
		BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
		bufferFileWriter.write(header);
		
		
    	FileReader fr = new FileReader(new File(fin));
    	BufferedReader br = new BufferedReader(fr);
    	String line;  
    	
    	while((line=br.readLine())!=null){
    		line = line.trim();        		
    		String[] strs = line.split(sp);
    		int dimm = strs.length;
    		String temp = "";
    		for(int i=0;i<dimm-1;i++){
    			if(i!=1 && i!=2 && i!=3 && i!=6 && i!=11 && i!=20 &&i!=21){
	        		temp = temp + strs[i] + ",";
    			}
    		}
    		temp = temp + strs[dimm-1].substring(0, strs[dimm-1].length()-1)+"\n";
    		bufferFileWriter.write(temp);

    	}
    	br.close();
    	bufferFileWriter.close();
		
  }

}
