import java.util.ArrayList;


public class PTree{
    public int numLevels;   
    public int numLeafObjects = 0;
    public int numNodeObjects = 0;
    public int numConcepts = 0;

    public ArrayList<ArrayList<SyncObject>> conceptsLevel;
    public ArrayList<SyncObject> prototypeLevel;
    public static int leafSize = 1000;
    public static int nodeSize = 10;
       
    
    public PTree() {
    	this.numLevels = 2; 
    	this.conceptsLevel = new ArrayList<ArrayList<SyncObject>>(); 
    	this.prototypeLevel = new ArrayList<SyncObject>(); 
    	this.numLeafObjects = 0;
    	this.numNodeObjects = 0; 
    	
    }
    
    public PTree(ArrayList<SyncObject> streamClu, int nC) {
    	this.numLevels = 2;
    	this.conceptsLevel = new ArrayList<ArrayList<SyncObject>>(); 
    	this.prototypeLevel = new ArrayList<SyncObject>(); 
    	
    	for(int i=0;i<streamClu.size();i++){
    		this.prototypeLevel.add(streamClu.get(i)); 
    	}
    	this.numLeafObjects = streamClu.size();
    	this.numNodeObjects = 0;
    }
    
    public boolean isFullLeaf(int nsize){
    	if((numLeafObjects + nsize)>leafSize)
    		return true;
    	else
    		return false;
    }
    
    public boolean isFullLeaf(){
    	if((numLeafObjects + 1)>leafSize)
    		return true;
    	else
    		return false;
    }
    
    public boolean isFullNode(){
    	if(this.numConcepts+1 > nodeSize)
    		return true;
    	else
    		return false;
    }
    
   	
    	
        public void addInstanceToTree(SyncObject obj){
        	if(!isFullLeaf()){
        		prototypeLevel.add(obj);
        		numLeafObjects++;
        	}else{
        		//System.out.println("Updating");
        		SemiSync ss = new SemiSync();       		
        		ArrayList<SyncObject> data = new ArrayList<SyncObject>();
        		int ln = prototypeLevel.size();       		
        		int r = 0;
        		
        		for(int i=0; i<ln;i++){ 	    			
    				if(prototypeLevel.get(i-r).queryCount<=0){
    					if(prototypeLevel.get(i-r).queryCount==0){
    						data.add(prototypeLevel.get(i-r));
    						if(data.size()>=0.3*leafSize)
    							break;
    					}
    					prototypeLevel.remove(i-r);
    					r++;
    					numLeafObjects--;	    					
    				}else{
    					prototypeLevel.get(i-r).setQC(0); // restore the status of all objects
    				}	    				
	    		}	    	
        		
        		
        		int c = 0;
        		SemiLearning s = new SemiLearning();      		
        		if(data.size()>20){
        			while(c<data.size()){
	        			ArrayList<SyncObject> pdata = new ArrayList<SyncObject>();
	        			for(int j=0;j<20&&(c+j)<data.size();j++){
	        				pdata.add(data.get(c+j));
	        				c++;
	        			}
	            		double K = s.kNN(pdata,pdata.size()/3);  
	            		ArrayList<SyncObject> leafClu = ss.ClusteringConstraints(pdata, K);
	            		
	            		if(leafClu.size()>data.size()/2){
	            			K = K*2;
	            			leafClu = ss.ClusteringConstraints(data, K);
	            		}
	            		for(int i=0;i<leafClu.size();i++){
	            			prototypeLevel.add(leafClu.get(i));
	            		}
	            		numLeafObjects += leafClu.size(); 
        			}
        		}else{
            		double K = s.kNN(data,data.size()/5);  
            		ArrayList<SyncObject> leafClu = ss.ClusteringConstraints(data, K);
            		
            		for(int i=0;i<leafClu.size();i++){
            			prototypeLevel.add(leafClu.get(i));
            		}
            		numLeafObjects += leafClu.size();         			
        		}
        		
        		prototypeLevel.add(obj);
        		numLeafObjects++;
        		
        		if(numLeafObjects>2*leafSize){
        			while(prototypeLevel.size()>leafSize){
        				prototypeLevel.remove(0);
        				numLeafObjects--;
        			}
        		}
        			
        	}
    		
    }   
        
         
    public void addConcept(int ConceptID, boolean Grandual){

    		SemiSync ss = new SemiSync();

    		ArrayList<SyncObject> data = new ArrayList<SyncObject>();
    		int ln = prototypeLevel.size();
    		
    		for(int i=(ln-1); i>=0;i--){ //update   			
				if(prototypeLevel.get(i).conceptType==ConceptID){
					data.add(prototypeLevel.get(i));
					if(data.size()>=0.5*leafSize){
						break;        						
					}
						
				}
    		}
    		
    		if(data.size()==0) return;
    		
    		SemiLearning s = new SemiLearning();
    		int R = 6;
    		if(data.size()>0.4*leafSize)
    			R = data.size()/50;
   		
    		double K = s.kNN(data,R);
    		ArrayList<SyncObject> leafClu = ss.ClusteringConstraints(data, K);
    		
    		
    		if(!isFullNode()){
    			conceptsLevel.add(leafClu);
    			numConcepts++;
    			numNodeObjects = numNodeObjects + leafClu.size();
    			if(!Grandual){
    				prototypeLevel.clear();
	    			numLeafObjects = 0;
    			}

    		}else{
    			//delete the oldest concepts  			
    			numNodeObjects = numNodeObjects- conceptsLevel.get(0).size();
    			conceptsLevel.remove(0);
    			conceptsLevel.add(leafClu);
    			numNodeObjects = numNodeObjects + leafClu.size();
    	}
    }
}
