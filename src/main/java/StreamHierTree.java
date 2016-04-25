import java.util.ArrayList;

public class StreamHierTree{
    public int numLevels;   
    public int numLeafObjects = 0;
    public int numNodeObjects = 0;
    public int nodeRound;
    public int rootRound;
    public ArrayList<ArrayList<SyncObject>> rootLevel;
    public ArrayList<ArrayList<SyncObject>> nodeLevel;
    public ArrayList<ArrayList<SyncObject>> leafLevel;
    public static int leafSize = 1000;
    public static int nodeSize = 10;

 
    public StreamHierTree() {
    	this.numLevels = 3; 
    	this.nodeRound = 0;
    	this.rootRound = 0;
    	this.rootLevel = new ArrayList<ArrayList<SyncObject>>(); 
    	this.nodeLevel = new ArrayList<ArrayList<SyncObject>>(); 
    	this.leafLevel = new ArrayList<ArrayList<SyncObject>>(); 
    	this.numLeafObjects = 0;
    	this.numNodeObjects = 0; 
    	
    }
    
    public StreamHierTree(ArrayList<SyncObject> streamClu, int nC) {
    	this.numLevels = 3;
    	this.rootLevel = new ArrayList<ArrayList<SyncObject>>(); 
    	this.nodeLevel = new ArrayList<ArrayList<SyncObject>>(); 
    	this.leafLevel = new ArrayList<ArrayList<SyncObject>>(); 
    	
    	this.leafLevel.add(streamClu); 
    	this.numLeafObjects = streamClu.size();
    	this.numNodeObjects = 0;
    }
    
    public boolean isFullLeaf(int nsize){
    	if((numLeafObjects + nsize)>leafSize)
    		return true;
    	else
    		return false;
    }
    public boolean isFullNode(int nsize){
    	if(this.nodeLevel.size()+1 > nodeSize)
    		return true;
    	else
    		return false;
    }
    
    
    public void addCluster(ArrayList<SyncObject> streamClu, double K){
    	if(!isFullLeaf(streamClu.size())){
    		leafLevel.add(streamClu);
    		numLeafObjects += streamClu.size();
    		System.out.println("\nAdd leaf node... Size = " + leafLevel.size());
    	}else{
    		SemiSync ss = new SemiSync();
    		
    		ArrayList<SyncObject> data = new ArrayList<SyncObject>();
    		for(int i=0; i<leafLevel.size();i++){
    			ArrayList<SyncObject> temp = leafLevel.get(i);
    			for(int j=0;j<temp.size();j++){
    				data.add(temp.get(j));
    			}
    		}
    		
    		ArrayList<SyncObject> leafClu = ss.ClusteringConstraints(data, K);
    		this.leafLevel.clear();
    		numLeafObjects = 0;
    		if(!isFullNode(streamClu.size())){
    			nodeLevel.add(leafClu);
    			
    			if(true){
            		ArrayList<SyncObject> data1 = new ArrayList<SyncObject>();
            		for(int i=0; i<nodeLevel.size();i++){
            			ArrayList<SyncObject> temp = nodeLevel.get(i);
            			for(int j=0;j<temp.size();j++){
            				data1.add(temp.get(j));
            			}
            		}  				
	        		IO ios = new IO();
	        		ios.saveSyncClusters(data1, "cluster"+nodeRound);
    			}
        		
    			numNodeObjects++;
    			nodeRound = nodeRound + 1;
    			System.out.println("\nRound:"+nodeRound+"; Add inner node...Size = " + nodeLevel.size());
    			leafLevel.add(streamClu);
    			numLeafObjects += streamClu.size();
    		}else{
        		ArrayList<SyncObject> data1 = new ArrayList<SyncObject>();
        		for(int i=0; i<nodeLevel.size();i++){
        			ArrayList<SyncObject> temp = nodeLevel.get(i);
        			for(int j=0;j<temp.size();j++){
        				data1.add(temp.get(j));
        			}
        		}
        		
        		ArrayList<SyncObject> nodeClu = ss.ClusteringConstraints(data, K);
    			rootRound = rootRound + 1;
    			nodeLevel.clear();
    			numNodeObjects = 0;
    			rootLevel.add(nodeClu);
    			leafLevel.add(streamClu);
    		}
    			
    	}
    		
    }
    
    public void addNewCluster(ArrayList<SyncObject> streamClu, double K){
    	if(!isFullLeaf(streamClu.size())){
    		leafLevel.add(streamClu);
    		numLeafObjects += streamClu.size();
    	}else{
    		SemiSync ss = new SemiSync();
    		
    		ArrayList<SyncObject> data = new ArrayList<SyncObject>();
    		for(int i=0; i<leafLevel.size();i++){ //only half of the leafNodes are updated
    			ArrayList<SyncObject> temp = leafLevel.get(i);
    			for(int j=0;j<temp.size();j++){
    				data.add(temp.get(j));
    			}
    		}
    		   
    		System.out.println("data size =" + data.size());
			// Perform clustering on leaf nodes 
    		ArrayList<SyncObject> leafClu = ss.ClusteringConstraints(data, 1.5*K);
    		
    		int nsize = leafLevel.size();
    		for(int i=0; i<nsize;i++){ //only half of the leafNodes are updated
    			numLeafObjects = numLeafObjects - leafLevel.get(i).size();
    			leafLevel.remove(i);
    			
    		}   
    		
			leafLevel.add(streamClu);
			numLeafObjects += streamClu.size();
			
			
    		if(!isFullNode(leafClu.size())){
    			nodeLevel.add(leafClu);
    			numNodeObjects = numNodeObjects + leafClu.size();
    			nodeRound = nodeRound + 1;
    			
    			boolean flag = false;
    			if(flag){
            		ArrayList<SyncObject> data1 = new ArrayList<SyncObject>();
            		for(int i=0; i<nodeLevel.size();i++){
            			ArrayList<SyncObject> temp = nodeLevel.get(i);
            			for(int j=0;j<temp.size();j++){
            				data1.add(temp.get(j));
            			}
            		}  				
	        		IO ios = new IO();
	        		ios.saveSyncClusters(data1, "cluster"+nodeRound);
    			}
        		

    		}else{
        		ArrayList<SyncObject> data1 = new ArrayList<SyncObject>();
        		for(int i=0; i<nodeLevel.size();i++){
        			ArrayList<SyncObject> temp = nodeLevel.get(i);
        			for(int j=0;j<temp.size();j++){
        				data1.add(temp.get(j));
        			}
        		}  
        		   
    			int nnsize = nodeLevel.size();
        		for(int i=0; i<nnsize;i++){ //only half of the leafNodes are updated
        			numNodeObjects = numNodeObjects- nodeLevel.get(i).size();
        			nodeLevel.remove(i);
        			
        		}  
        		
        		ArrayList<SyncObject> nodeClu = ss.ClusteringConstraints(data1, 2*K);
    			rootRound = rootRound + 1;

    			
    			rootLevel.add(nodeClu);
    			nodeLevel.add(leafClu);
    			numNodeObjects = numNodeObjects + leafClu.size();
    		}
    			
    	}
    }
    	
    	
        public void addClusterToTree(ArrayList<SyncObject> streamClu, int bsize){
        	if(!isFullLeaf(streamClu.size())){
        		leafLevel.add(streamClu);
        		numLeafObjects += streamClu.size();
        	}else{
        		SemiSync ss = new SemiSync();
        		
        		ArrayList<SyncObject> data = new ArrayList<SyncObject>();
        		int ln = leafLevel.size();       		
        		int n = 0;
        		boolean f = false;
        		for(int i=0; i<ln;i++){ 
	    			ArrayList<SyncObject> temp = leafLevel.get(i);
	    			int len = temp.size();
	    			int r = 0;
	    			for(int j=0;j<len;j++){
	    				if(!f && temp.get(j-r).queryCount<=0){    						
	    					data.add(temp.get(j-r));
	    					temp.remove(j-r);
	    					r++;
	    					numLeafObjects--;
	    					n++;	
	    				}else{
	    					temp.get(j-r).setQC(0); // restore the status of all objects
	    				}	    				
	    			}
	    		}
        		
        		int c = 0;
        		SemiLearning s = new SemiLearning();      		
        		if(data.size()>bsize){
        			while(c<data.size()){
	        			ArrayList<SyncObject> pdata = new ArrayList<SyncObject>();
	        			for(int j=0;j<bsize&&(c+j)<data.size();j++){
	        				pdata.add(data.get(c+j));
	        				c++;
	        			}
	            		double K = s.kNN(pdata,pdata.size()/2);  
	            		ArrayList<SyncObject> leafClu = ss.ClusteringConstraints(pdata, K);
	        			leafLevel.add(leafClu);
	        			numLeafObjects += leafClu.size(); 
	        		}
        		}

//    			// Perform clustering on leaf nodes       		
    			leafLevel.add(streamClu);
    			numLeafObjects += streamClu.size();
        			
        	}
    }   
        
        public void addConcept(int ConceptID, int bsize, boolean Grandual){

        		SemiSync ss = new SemiSync();
        		
        		System.out.println("...Add a new conpect in the hierarchical tree("+nodeRound+")...");
        		
        		ArrayList<SyncObject> data = new ArrayList<SyncObject>();
        		int ln = leafLevel.size();

        		for(int i=(ln-1); i>=0;i--){ //only half of the leafNodes are updated
        			ArrayList<SyncObject> temp = leafLevel.get(i);
        			boolean f = false;
        			for(int j=0;j<temp.size();j++){
        				if(temp.get(j).conceptType==ConceptID){
        					data.add(temp.get(j));
        					if(data.size()>=0.5*leafSize){
        						f = true;
        						break;        						
        					}
        						
        				}
        			}
        			if(f) break;
        		}
        		
        		SemiLearning s = new SemiLearning();
        		int R = 3;
        		if(data.size()/50>5)
        			R = data.size()/50;
        		else
        			R = 3;
        		
        		double K = s.kNN(data,8);
    			// Perform clustering on leaf nodes 
        		if(data.size()==0) return;
        		ArrayList<SyncObject> leafClu = ss.ClusteringConstraints(data, K);
        		
        		
        		if(!isFullNode(leafClu.size())){
	    			nodeLevel.add(leafClu);
	    			numNodeObjects = numNodeObjects + leafClu.size();
	    			nodeRound = nodeRound + 1;
	    			if(!Grandual){
		    			leafLevel.clear();
		    			numLeafObjects = 0;
	    			}
	    		}else{
	    			//merg the oldest concepts
	    			System.out.println("...Merging...");
	        		SemiSync se = new SemiSync();
	        		
	        		ArrayList<SyncObject> concept = new ArrayList<SyncObject>();
	        		int nl = nodeLevel.size();       		
	        		int n = 0;
	        		int nc = 0;
	        		boolean[] f = new boolean[nl];	        		
	        		for(int i=0; i<nl;i++){ 
	        			f[i] = false;
		    			ArrayList<SyncObject> temp = nodeLevel.get(i);
		    			for(int j=0;j<temp.size();j++){
		    				if(temp.get(j).queryCount>1){
		    					f[i] = true;
		    				}
		    			}
		    			if(f[i]) nc++;
		    		} 
	        		
	        		System.out.println("========================"+nc);
	        		if(true){	//nc==nl       
	        			System.out.println("the oldest two concepts are merged");
		        		for(int i=0; i<2;i++){ 
			    			ArrayList<SyncObject> temp = nodeLevel.get(i);
			    			for(int j=0;j<temp.size();j++){
			    				concept.add(temp.get(j));
			    			}
			    		}
		        		numNodeObjects -= nodeLevel.get(0).size();
		        		nodeLevel.remove(0);
		        		numNodeObjects -= nodeLevel.get(0).size();
		        		
		        		
		        		SemiLearning clu = new SemiLearning();
		        		double KK = clu.kNN(concept,10);
		    			// Perform clustering on leaf nodes 
		        		ArrayList<SyncObject> nodeClu = se.ClusteringConstraints(concept, KK);		        		
	        			
		        		nodeLevel.set(0, nodeClu);
		        		numNodeObjects += nodeClu.size();

		        		nodeLevel.add(leafClu);
		        		numNodeObjects += leafClu.size();
		        		
		    			if(!Grandual){
			    			leafLevel.clear();
			    			numLeafObjects = 0;
		    			}		        		
	        			
	        		}
	        		
       			if(!Grandual){
	    			leafLevel.clear();
	    			numLeafObjects = 0;
    			}
        	}
        }
}

 