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
import VisualNumerics.math.*;



/**
 * 
 * @author Junming Shao
 * @param None
 * Created on 2. Jan. 2013, 
 */


public class SyncStream{
	
    public ArrayList<SyncObject> src; 
    public ArrayList<SyncObject> iData; 
    public ArrayList<SyncObject> trainTree;
    public int numInit;
    public int dim;
    public int numClass;
    public double[] max;
    public double[] min;
    public int proN;
    
 
    public SyncStream() {

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

       int num = 0;
       
       try {
       	File mFile = new File(fn);
       	FileReader fr = new FileReader(mFile);
       	BufferedReader br = new BufferedReader(fr);
       	String line;
       	while((line=br.readLine())!=null){
       		num++;
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
       
       System.out.println(num);
       
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


public int kNNPTreeClassifier(PTree sht, SyncObject test, boolean type, boolean noise){
	   
	   int prelabel = 0;	   
	   
	  // check inputs for validity
	  if(sht.prototypeLevel.size() == 0) return 0; //  bad input
	 	  
	   ArrayList<SyncObject> data = new ArrayList<SyncObject>();
	   	   
	   // prototype objects	   
	   for(int i=0;i<sht.prototypeLevel.size();i++){
			   data.add(sht.prototypeLevel.get(i));
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
			    //System.out.print("-*-");
			  
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
 
  public double angle(double[][] a, double[][] b, String angleC){
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
	  if(angleC=="max")
		  return maxT;	
	  else
		  return avgT/n;
  }
  
  public double PCA_AngleNew(ArrayList<SyncObject> a, ArrayList<SyncObject> b, int C){

	  double maxT = 0.0;
	  double avgT = 0.0;
	  int n = 0;
	  int dim = a.get(0).data.length;
	  
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
			  double[][] temp1 = new double[dim][n1]; 
			  double[][] temp2 = new double[dim][n2]; 
			  int t1 = 0, t2 = 0;
			  for(int i=0;i<a.size();i++){
				  if(a.get(i).label==k){					 
					  for(int d=0;d<dim;d++){
						  temp1[d][t1] = a.get(i).data[d];
					  }
					  t1++;
			      }
			  }
			  for(int i=0;i<b.size();i++){
				  if(b.get(i).label==k){
					  for(int d=0;d<dim;d++){
						  temp2[d][t2] = b.get(i).data[d];
					  }
					  t2++;
			      }
			  }	
			  try{
				  
				  javastat.multivariate.PCA  pca1 = new javastat.multivariate.PCA(0.99, "covariance", temp1);
				  double[] fC1 = pca1.principalComponents[0]; 				  
				  javastat.multivariate.PCA  pca2 = new javastat.multivariate.PCA(0.99, "covariance", temp2);
				  double[] fC2 = pca2.principalComponents[0];

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
			  }catch(Exception e){
				  return 0;
			  }
		  }
	  }
			   
	  return maxT;	  
  }    
  
  public double PCA_Angle(ArrayList<SyncObject> a, ArrayList<SyncObject> b, int C, String angleC){

	  double maxT = 0.0;
	  double avgT = 0.0;
	  int n = 0;	  
	  
	  for(int k=0;k<C;k++){
		  int n1 = 0, n2=0;
		  double dp = 0.0, theta = 0.0;
		  for(int i=0;i<a.size();i++){
			  if(a.get(i).label==k){
				  n1++;
		      }
		  }
			  
		  for(int i=0;i<b.size();i++){
			  if(b.get(i).label==k){
				  n2++;
		      }
		  }	
		  if(n1>20 && n2>20){ // at least some objects are existed
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
	  
	  if(angleC=="avg")
		  return maxT;	
	  else
		  if(n>0)
			  return avgT/n;
		  else
			  return 0.0;
  
  }  
  
  
	static void swap (double[][] array, int idx1, int idx2) {
		double tmp1 = array[idx1][0];
		double tmp2 = array[idx1][1];
		array[idx1][0] = array[idx2][0];
		array[idx1][1] = array[idx2][1];
		array[idx2][0] = tmp1;
		array[idx2][1] = tmp2;
	}
	
	static void qsort (double[][] array, int le, int ri) {
		int lo = le, hi = ri;

		if (hi > lo) {
			// Pivotelement bestimmen
			double mid = array[(lo + hi) / 2][0];
			while (lo <= hi) {
				// Erstes Element suchen, das gr��er oder gleich dem
				// Pivotelement ist, beginnend vom linken Index
				while (lo < ri && array[lo][0] < mid)
					++lo;

				// Element suchen, das kleiner oder gleich dem
				// Pivotelement ist, beginnend vom rechten Index
				while (hi > le && array[hi][0] > mid)
					--hi;

				// Wenn Indexe nicht gekreuzt --> Inhalte vertauschen
				if (lo <= hi) {
					swap(array, lo, hi);
					++lo;
					--hi;
				}
			}
			// Linke Partition sortieren
			if (le < hi) {
				qsort (array, le, hi);
			}

			// Rechte Partition sortieren
			if (lo < ri) {
				qsort( array, lo, ri);
			}
		}
	}

	public void quickSort (double[][] array) {
		qsort (array, 0, array.length - 1);
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
  
  
  public double computePvalue(ArrayList<SyncObject> predata, ArrayList<SyncObject>nextdata, int numC){
	  
	  double pvalue = 1.0;
	  int nsize = predata.size();
	  int dim = predata.get(0).data.length;
	  
	  for(int i=0; i<numC;i++){
		  ArrayList<SyncObject> dx = new ArrayList<SyncObject>();
		  ArrayList<SyncObject> dy = new ArrayList<SyncObject>();
		  for(int j=0; j<nsize;j++){
			  if(predata.get(j).label==(i+1)){
				  dx.add(predata.get(j));
			  }
			  if(nextdata.get(j).label==(i+1)){
				  dy.add(nextdata.get(j));
			  }			  
		  }	  
	
		  if(dx.size()>20 && dy.size()>20){
			  
			  double S1 = 0.0, S2 = 0.0;
			  double R1 = 0.0, R2 = 0.0;
			  int lenx = dx.size();
			  int leny = dy.size();
			  
			  for(int d=0;d<dim;d++){	
				  double[][] X = new double[lenx][2];
				  double[][] Y = new double[leny][2];
				  double[][] Z = new double[lenx+leny][2];
				  
				  double[][] XX = new double[lenx][2];
				  double[][] YY = new double[leny][2];
				  double[][] ZZ = new double[lenx+leny][2];
				  
				  for(int k=0; k<lenx;k++){
					  X[k][0] = ((SyncObject)dx.get(k)).data[d];
					  X[k][1] = k+1;
					  Z[k][0] = X[k][0];
					  Z[k][1] = k+1;					  
				  }
					 
				  for(int k=0; k<leny;k++){
					  Y[k][0] = ((SyncObject)dy.get(k)).data[d];
					  Y[k][1] = k+1;
					  Z[k+lenx][0] = Y[k][0];
					  Z[k+lenx][1] = k+1+lenx;					  
				  }				  
				  
				  quickSort(X);
				  quickSort(Y);
				  quickSort(Z);
				  
				  for(int k=0; k<lenx;k++){
					  XX[k][0] = X[k][1];
					  XX[k][1] = k+1;
					  ZZ[k][0] = Z[k][1];
					  ZZ[k][1] = k+1;					  
				  }
				  
				  for(int k=0; k<leny;k++){
					  YY[k][0] = Y[k][1];
					  YY[k][1] = k+1;	
					  ZZ[k+lenx][0] = Z[k+lenx][1];
					  ZZ[k+lenx][1] = k+1+lenx;				  
				  }	
				  
				  quickSort(XX);
				  quickSort(YY);
				  quickSort(ZZ);
					  
				  double r1 = 0, r2 = 0;
				  
				  for(int k=0; k<lenx;k++){
					  r1 = r1 + ZZ[k][1];
				  }
				  for(int k=0; k<leny;k++){
					  r2 = r2 + ZZ[k+lenx][1];
				  }	
				  
				  R1 = R1 + r1;
				  R2 = R2 + r2;
				  
				  r1 = r1/lenx;
				  r2 = r2/leny;
				  
				  double s1 = 0, s2 = 0;
				  
				  for(int k=0; k<lenx;k++){
					  s1 = s1 + (ZZ[k][1]-XX[k][1]-r1+(lenx+1.0)/2.0)*(ZZ[k][1]-XX[k][1]-r1+(lenx+1.0)/2.0);
				  }		  
				  
				  for(int k=0; k<leny;k++){
					  s2 = s2 + (ZZ[k+lenx][1]-YY[k][1]-r2+(leny+1.0)/2.0)*(ZZ[k+lenx][1]-YY[k][1]-r2+(leny+1.0)/2.0);
				  }
				  
				  S1 = S1 + s1;
				  S2 = S2 + s2; 
			  }	
			  
			  S1 = S1/((lenx-1)*dim);
			  S2 = S2/((leny-1)*dim);
			  
			  R1 = R1/((lenx-1)*dim);
			  R2 = R2/((leny-1)*dim);
			  
			  double theta = Math.sqrt((lenx+leny)*S1/leny + (lenx+leny)*S2/lenx);
			  
			  if(theta<=1e-6){
				  theta = Math.sqrt((lenx+leny)/(2.0*lenx*leny));
			  }
			  
			  double W_BF = ((R1-R2)/theta)*Math.sqrt((lenx*leny)/((lenx+leny)*dim)); // ??
			  
			  
			  // sort the two data
			  Statistics stat = new Statistics();
			  double p = 2.0*stat.tCdf(-Math.abs(W_BF), lenx+leny-1);
			  if(p<pvalue)
				  pvalue = p;
		}

	  }

	  return pvalue;

  }
 
    /**
     * main function 
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
    	
    	String dir = args[0]; //output directory
    	String dataPath = args[1]; //input data path
    	String strategy = args[2]; //pca or statistic
    	String angleC = args[3]; //avg or max
     	int initSize = 	new Integer(args[4]); // the initial number of object in each partition for the original clustering, default 500
    	int T3 = new Integer(args[5]); //number of instances (Level 0), default 1000
    	int T2 = new Integer(args[6]); //number of objects (Level 1), default 250
    	double theta = new Double(args[7]); //60 degree   
    	int numC = new Integer(args[8]); //num classes 	

    	File f = new File(dir);
    	f.mkdirs();
    	String fn = dataPath.substring(dataPath.lastIndexOf("/")+1, dataPath.length()); 
    	boolean type = true;
    	boolean normFlag = true; // normalize the data or not? default = true
    	boolean noise = false; //containing obvious noise or not? default = false
    	boolean outputFlag = false;
    	String sp = ","; //"\\s+";
    	
    	//load the inital data set.
    	SyncStream sl = new SyncStream();
    	sl.numClass = numC;
    	ArrayList<Double> acc = new ArrayList<Double>(); 
    	ArrayList<Double> angles = new ArrayList<Double>(); 
    	ArrayList<double[]> change = new ArrayList<double[]>();
    	ArrayList<double[]> R = new ArrayList<double[]>();
    	
    	
    	
    	ArrayList<SyncObject> idata = sl.loadInitData(dataPath, normFlag, initSize,sp);  
    	   	
    	long   start   =   System.currentTimeMillis(); 
    	int numInstances = 0;
    	int numObj = 0;
    	int ne = 0;
    	int nee = 0;
    	int num = 0;
    	int unit = 0;

    	int cc = 0;
    	int ncount = 0;
    	int na = 0;
    	int conceptID = 0;
    	int numConcept = 0;
    	
    	PTree sht = new PTree(idata,numC);

        try {
        	BufferedReader br = new BufferedReader(new FileReader(dataPath));
        	String line;        	
        	

        	ArrayList<SyncObject> predata = new ArrayList<SyncObject>();
        	ArrayList<SyncObject> nextdata = new ArrayList<SyncObject>();
        	
        	while((line=br.readLine())!=null){
        		num++;
        		if(num>initSize ){
	        		line = line.trim();        		
	        		String[] strs = line.split(sp);
	        		int dimm = strs.length;
	        		double[] tt = new double[dimm-1];
	        		int d = 0;

	        		for(int i=0;i<dimm-1;i++){
			        		double temp = Double.parseDouble(strs[i]);
			        		if(normFlag)			        			
				       		  if(sl.max[d]!=sl.min[d])
				       			 tt[d] = 0.5*Math.PI*(temp-sl.min[d])/(sl.max[d]-sl.min[d]);     
				     		  else
				     			 tt[d] = sl.max[d];
			        		else
			        			tt[d] = temp;
			        		d++;

	        		}       		
	        		int l = 0;
	        		
	        		l=Integer.parseInt(strs[dimm-1]);
	        		numInstances++;
	        		numObj++;
	        		
	        		SyncObject test = new SyncObject(tt,l);
	        		
	        		//a lazy approach for classification *NN*
	        		int predict = sl.kNNPTreeClassifier(sht, test, type, noise);
	        		double[] tempR = new double[2];
	        		tempR[0] = predict;
	        		tempR[1] = test.label;
	        		R.add(tempR);
	        		
	        		if(test.label!=0){
	        			ncount++;
	        			na++;
	        			if(test.label!=predict){
	        				ne++;
	        				nee++;
	        			}
	        		}
	        		
	        		if(noise){
	        			if(test.label==predict || sht.numLeafObjects<10)
	        				sht.addInstanceToTree(test);
	        			
	        		}else{
	        			sht.addInstanceToTree(test);
	        		}
	        		test.conceptType = conceptID;
	        		nextdata.add(test);	        		
	        		
	        		if(numInstances%T3 ==0){
	        			System.out.println(cc+":"+(1-(nee+0.0)/ncount)*100+"%. Overall :" 
	        					+(na-ne+0.0)*100/na+"%"+"; size ="+(sht.numLeafObjects)+","+sht.numNodeObjects);
	        			acc.add(1-(nee+0.0)/ncount);
	        			acc.add((na-ne+0.0)/na);
	        			ncount = 0;
	        			nee = 0;	        			
	        			cc++;
	        		}
	        		

	        		if(numObj==T2){	
	        			unit++;
	        			if(outputFlag){
		        			ArrayList<SyncObject> output = new ArrayList<SyncObject>();
	        			    for(int ii=0;ii<sht.prototypeLevel.size();ii++){
	        					output.add(sht.prototypeLevel.get(ii));
	        			   }
	        			   
	        			    if(output.size()>0){
		        			   IO iso = new IO();
		        			   iso.saveSyncClusters(output, dir+"/traindata_"+unit+"_"+fn);
	        			    }
	        			}
	        			
	        			/*Strategy one: Mean difference*/
	        			
	        			if(strategy=="pca"){
	        				
	        				/*Second Strategy: PCA Analysis*/	        				
		        			double orient = -1.0;
		        			if(predata.size()>0 && nextdata.size()>0){
		        				orient = 180*sl.PCA_Angle(predata,nextdata, numC+1, angleC)/Math.PI;
		        				angles.add(orient);
		        			}		        					      
		        				        			
		        			if(predata.size()>0 && orient>theta){
		        				sht.addConcept(conceptID,type);
		        				conceptID++;
		        				numConcept++;
		        				System.out.print(".");
		        			}	
		        			predata = new ArrayList<SyncObject>();
		        			for(int m=0;m<nextdata.size();m++){
		        				predata.add(nextdata.get(m));
		        			}		        			
		        				        			
		        			numObj = 0;
			        		nextdata = new ArrayList<SyncObject>();


	        			}else if(strategy=="statistic"){
	        				
	        				/*Third Strategy: PCA Analysis*/
		        			double pvalue = 1.0;
		        			if(predata.size()>0  && nextdata.size()>0){
		        				pvalue = sl.computePvalue(predata,nextdata,numC);
		        				angles.add(pvalue);
		        			}
		        			
		        			if(predata.size()>0 && pvalue<0.01){
		        				sht.addConcept(conceptID, type);
		        				conceptID++;
		        				numConcept++;
		        			}	
		        			predata = new ArrayList<SyncObject>();
		        			for(int m=0;m<nextdata.size();m++){
		        				predata.add(nextdata.get(m));
		        			}		        			
		        				        			
		        			numObj = 0;
			        		nextdata = new ArrayList<SyncObject>();

	        			}	
	        		}

        		}

        	}      
        	br.close();
        	
        	
        } catch (IOException ex) {
            ex.printStackTrace();
        } 
        IO ios = new IO();
        ios.saveArrayDouble(acc, dir+"/acc_"+fn);
        ios.saveArrayDouble(angles, dir+"/anglesAvg_"+strategy+"_"+T2+"_"+fn);
        ios.saveArrays(change, dir+"/change_"+fn);
        ios.saveArrays(R, dir+"/ID_"+strategy+"_"+fn);
        
		if(outputFlag){
			
		    for(int ii=0;ii<sht.conceptsLevel.size();ii++){
		       ArrayList<SyncObject> output = new ArrayList<SyncObject>();
			   ArrayList<SyncObject> temp = sht.conceptsLevel.get(ii);
			   for(int jj=0;jj<temp.size();jj++){
				   output.add(temp.get(jj));
			   }
			    if(output.size()>0){
				   IO iso = new IO();
				   iso.saveSyncClusters(output, dir+"/Concept_"+ii+"_"+fn);
				}
		   }
		}
        
    	long   end   =   System.currentTimeMillis();
    	System.out.println("\nAccuracy = "+ (na-ne+0.0)*100/na +", number of concepts:" + numConcept);
  	  	System.out.println("All running time:"+Long.toString((end-start))+" ms.");      
  	  	System.out.println("Ending");	
   }
}