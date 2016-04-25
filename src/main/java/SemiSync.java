import java.util.ArrayList;

/**
 * Semi-Supervised clustering based on local synchronization
 * 
 * @author Junming Shao
 * @param None
 * Created on 16. July. 2013, 
 */


public class SemiSync{
	
    public double K;
    public int num;
    public int dim;
    public int proN = 0;

    
    public SemiSync() {

    }
    
    public double EuclideanDist(double[] dis){
    	
    	double val = 0.0;
    	for(int i=0;i<dis.length;i++){
    		val = val + dis[i]*dis[i];
    	}   
    	double dist = Math.sqrt(val);
    	return dist;
    }
    
    public ArrayList<SyncObject> findSynCluster(ArrayList<SyncObject> data){
    	

    	int len = data.size();
    	ArrayList<SyncObject> SyncCluster = new ArrayList<SyncObject>();

    	int[] id = new int[len];
    	int[] f = new int[len];
    	for(int i=0;i<len;i++){
    		id[i] = -1;
    		f[i] = 0;
    	}


    	for(int i=0;i<len;i++){
    		if(f[i]==0){ //check whether this object is clustered  
    			double[] diss = new double[dim];
    			int num  =0;
    			ArrayList<Integer> al = new ArrayList<Integer>();
	    		for(int j=0;j<len;j++){
	    			if(i!=j && f[j]==0){
			    		for(int d=0;d<dim;d++){
			    			diss[d] = data.get(j).data[d] - data.get(i).data[d];		    			
			    		}
		    			double dis = EuclideanDist(diss);
		    			if(Math.abs(dis)<(Math.sqrt(dim)*1e-4)){ // check which objects are synchronized together   				    				
		    				num = num + 1;
		    				al.add(j);	
		    			}	    			
	    			}
	    		}
	    		if(num>1){	    			
	    			f[i] = 1;
	    			int count = 0;
	    			int l = 0;
	    			int nn = al.size();
	    			int c =0;
	    			for(int k=0;k<nn;k++){
	    				f[(Integer)al.get(k)] = 1;
	    				if(data.get(al.get(k)).label!= 0){
	    					if(l!=0 && l!=data.get(al.get(k)).label){
	    						proN++;
	    		    			SyncObject outlier = new SyncObject(data.get(al.get(k)).data, 1, data.get(al.get(k)).label, 1);
	    		    			SyncCluster.add(outlier);
	    		    			c++;
	    						count--;
	    					}

	    					l = data.get(al.get(k)).label;
	    					count = count + 1;
	    				}
	    			}
	    			al.add(i);
    			
	    			SyncObject PA = new SyncObject(data.get(i).data, al.size()-c, l, count);
	    			SyncCluster.add(PA);

	    		}else{
	    			f[i] = 1;
	    			int nl = 0;
    				if(data.get(i).label!= 0){
    					nl = 1;
    				}	    			
	    			SyncObject outlier = new SyncObject(data.get(i).data, 1, data.get(i).label, nl);
	    			SyncCluster.add(outlier);
	    		}
    		}
    	}

		return SyncCluster;
    	
    }  
    public ArrayList<SyncObject> findSynCluster(ArrayList<SyncObject> data, double order){
    	
    	int len = data.size();
    	ArrayList<SyncObject> SyncCluster = new ArrayList<SyncObject>();

    	int[] id = new int[len];
    	int[] f = new int[len];
    	for(int i=0;i<len;i++){
    		id[i] = -1;
    		f[i] = 0;
    	}


    	for(int i=0;i<len;i++){
    		if(f[i]==0){ //check whether this object is clustered  
    			double[] diss = new double[dim];
    			int num  =0;
    			ArrayList<Integer> al = new ArrayList<Integer>();
	    		for(int j=0;j<len;j++){
	    			if(i!=j && f[j]==0){
			    		for(int d=0;d<dim;d++){
			    			diss[d] = data.get(j).data[d] - data.get(i).data[d];		    			
			    		}
		    			double dis = EuclideanDist(diss);
		    			if(Math.abs(dis)<1e-4 || Math.abs(dis)<order/10 ){ // check which objects are synchronized together   				    				
		    				num = num + 1;
		    				al.add(j);	
		    			}	    			
	    			}
	    		}
	    		if(num>1){	    			
	    			f[i] = 1;
	    			int count = 0;
	    			int l = 0;
	    			for(int k=0;k<al.size();k++){
	    				f[(Integer)al.get(k)] = 1;
	    				if(data.get(al.get(k)).label!= 0){
	    					l = data.get(al.get(k)).label;
	    					count = count + 1;
	    				}
	    			}
	    			al.add(i);
    			
	    			SyncObject PA = new SyncObject(data.get(i).data, al.size(), l, count);
	    			SyncCluster.add(PA);

	    		}else{
	    			f[i] = 1;
	    			int nl = 0;
    				if(data.get(i).label!= 0){
    					nl = 1;
    				}	    			
	    			SyncObject outlier = new SyncObject(data.get(i).data, 1, data.get(i).label, nl);
	    			SyncCluster.add(outlier);
	    		}
    		}
    	}

		return SyncCluster;
    	
    }      

	/**
	 * main function - clustering
	 */
    public ArrayList<SyncObject> ClusteringConstraints(ArrayList<SyncObject> data, double K) {   	
        	
    	num = data.size();
    	dim =data.get(0).data.length;
    	
    	boolean loop = true;
    	int loopNum = 0;
    	double localOrder = 0.0;
    	double allorder = 0.0;

    	ArrayList<SyncObject> prex = new ArrayList<SyncObject>();

    	//Copy data set    	
    	for(int i=0;i<num;i++){
    		double[] temp1 = new double[dim];    		
    		for(int j=0;j<dim;j++){
    			temp1[j] = data.get(i).data[j];
    		}
			prex.add(new SyncObject(temp1,data.get(i).label));
			
    	} 

    	//Dynamic clustering
    	while(loop){

    		double[] order = new double[num];
    		localOrder = 0.0;
    		allorder = 0.0;
    		
    		loopNum = loopNum + 1;
    		
	    	for(int i=0;i<num;i++){	    		
	    		double[] sinValue = new double[dim]; 
    			double[] diss = new double[dim]; 
    			double[] temp = new double[dim];
    			
    			double dis = 0.0;   			
	    		int n = 0; double sita = 0.0; 
	    		int ilabel = data.get(i).label;
			
	    		for(int j=0;j<num;j++){	    			
	    			dis = 0.0; 
	    			for(int d=0;d<dim;d++){
	    				diss[d] = prex.get(j).data[d]-prex.get(i).data[d];	    				
	    			}
	    			
	    			dis = EuclideanDist(diss);  

	    			if(dis < K && (data.get(j).label ==0 || data.get(j).label==ilabel)){
	    				n = n + 1;
	    				//Calculating the coupling strength
		    			for(int d=0;d<dim;d++){
		    				temp[d] = (diss[d]+ 1e-10)/(prex.get(j).data[d]+ 1e-10);
		    			}	    	
	    				
		    			for(int d=0;d<dim;d++){ 
		    				sinValue[d] = sinValue[d] + Math.sin(diss[d]);
		    			}	 
	    				sita = sita + Math.exp(-dis);
	    			}
	    		}
	    		if(n > 1){
	    			for(int d=0;d<dim;d++){
	    				data.get(i).data[d] = prex.get(i).data[d]+ ((1.0/n)*sinValue[d]); 			
	    			}
	    			order[i] = sita/n;
	    		}
	    	}
	    	for(int k = 0; k < num; k++){
	    		allorder = allorder + order[k];	    		
	    	}
	    	
	    	//Local order parameter
	    	localOrder = allorder/num;

	    	if( localOrder > 1 - (1e-3)|| loopNum >= 10){ //user's specification
	    		loop = false;
	    	}
	    	
	    	
	    	for(int i=0;i<num;i++){
	    		double[] temp1 = new double[dim];    		
	    		for(int j=0;j<dim;j++){
	    			temp1[j] = data.get(i).data[j];
	    		}
				prex.set(i, new SyncObject(temp1,data.get(i).label));
	    	}	   	
    	}
    	
    	//find the clusters
    	ArrayList<SyncObject> mc = findSynCluster(data);   

    	return mc;

    }    	
 
	/**
	 * main function - clustering
	 */
    public ArrayList<SyncObject> ClusteringConstraint(ArrayList<SyncObject> data, double K) {   	
        	
    	int num = data.size();
    	int dim =data.get(0).data.length;
    	
    	boolean loop = true;
    	int loopNum = 0;
    	double localOrder = 0.0;
    	double allorder = 0.0;

    	ArrayList<SyncObject> prex = new ArrayList<SyncObject>();

    	//Copy data set    	
    	for(int i=0;i<num;i++){
    		double[] temp1 = new double[dim];    		
    		for(int j=0;j<dim;j++){
    			temp1[j] = data.get(i).data[j];
    		}
			prex.add(new SyncObject(temp1,data.get(i).label));
			
    	} 

    	//Dynamic clustering
    	while(loop){

    		double[] order = new double[num];
    		localOrder = 0.0;
    		allorder = 0.0;
    		
    		loopNum = loopNum + 1;
    		
	    	for(int i=0;i<num;i++){	    		
	    		double[] sinValue = new double[dim]; 
    			double[] diss = new double[dim]; 
    			double[] temp = new double[dim];
    			
    			double dis = 0.0;   			
	    		int n = 0; double sita = 0.0; 
	    		int ilabel = data.get(i).label;
			
	    		for(int j=0;j<num;j++){	    			
	    			dis = 0.0; 
	    			for(int d=0;d<dim;d++){
	    				diss[d] = prex.get(j).data[d]-prex.get(i).data[d];	    				
	    			}
	    			
	    			dis = EuclideanDist(diss);  

	    			if(dis < K){
	    				n = n + 1;
	    				//Calculating the coupling strength
		    			for(int d=0;d<dim;d++){
		    				temp[d] = (diss[d]+ 1e-10)/(prex.get(j).data[d]+ 1e-10);
		    			}	    	
	    				
		    			for(int d=0;d<dim;d++){
		    				 if((data.get(j).label ==0 || data.get(j).label==ilabel))
		    					 sinValue[d] = sinValue[d] + Math.sin(diss[d]); //attract
		    				 else
		    					 sinValue[d] = sinValue[d] - Math.sin(diss[d]); //repell
		    			}	 
		    				    					    			 
		    			sita = sita + Math.exp(-dis); 

	    			}
	    		}
	    		if(n > 1){
	    			for(int d=0;d<dim;d++){
	    				data.get(i).data[d] = prex.get(i).data[d]+ ((1.0/n)*sinValue[d]); 			
	    			}
	    			order[i] = sita/n;
	    		}
	    	}
	    	for(int k = 0; k < num; k++){
	    		allorder = allorder + order[k];	    		
	    	}
	    	
	    	//Local order parameter
	    	localOrder = allorder/num;

	    	if( localOrder > 1 - (1e-3)|| loopNum >= 20){ //user's specification
	    		loop = false;	    	
	    	}
	    	
	    	for(int i=0;i<num;i++){
	    		double[] temp1 = new double[dim];    		
	    		for(int j=0;j<dim;j++){
	    			temp1[j] = data.get(i).data[j];
	    		}
				prex.set(i, new SyncObject(temp1,data.get(i).label));
	    	}	   	
    	}
    	
    	//find the clusters
    	ArrayList<SyncObject> mc = findSynCluster(data);   

    	return mc;

    }

}
