import java.util.ArrayList;


public class SyncObject{
    public double[] data; 
    public ArrayList<Integer> id;
    public int num; // number of objects
    public int nl; // number of labeled objects
    public int label; // label of the SyncObject
    public long time;
    public int queryCount;
    public int conceptType;


    

    
    public SyncObject() {
    	
    }
        
    
    public SyncObject(double[] centers, int l) {
    	this.data = centers;
    	this.label = l;  
    	this.num = 1;
    	this.queryCount = 0;
    	this.conceptType = -1;
    	
    	if(l!=0)
    		this.nl = 1;
    	else
    		this.nl = 0;
    }    
    
    public SyncObject(double[] centers, int l, long time) {
    	this.data = centers;
    	this.label = l;  
    	this.num = 1;
    	this.queryCount = 0;
    	this.time = time;
    	this.conceptType = -1;
    	
    	if(l!=0)
    		this.nl = 1;
    	else
    		this.nl = 0;
    }  
        
    public SyncObject(double[] centers, int num, int l, int nl) {
    	this.data = centers;
    	this.num = num;
    	
    	this.label = l;
    	this.nl = nl;
    	this.conceptType = -1;

    } 
    
    public void setQC(int c){
    	this.queryCount = c;
    }
    
    public int getQC(){
    	return this.queryCount;
    }
    
    public void addQC(){
    	this.queryCount = this.queryCount + 1;
    }
    
    public void minusQC(){
    	this.queryCount = this.queryCount - 1;
    }
}