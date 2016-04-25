import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class IO{
	
	
	public IO(){
		
	}
	
	
	public void saveSyncClusters(ArrayList<SyncObject> clusters, String fn){
		
		int len = clusters.size();
		int dim = clusters.get(0).data.length;
	    try{
			FileOutputStream fout = new FileOutputStream(new File(fn));     
			for(int i=0;i<len;i++){   
				for(int j=0; j<dim;j++){   
					fout.write(((Double)(clusters.get(i).data[j])+"\t").getBytes());   
				}   
				
				fout.write(((Integer)(clusters.get(i).label)+"\t").getBytes());
				
			fout.write(("\r\n").getBytes());   
			}   
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}
	
	
	public void saveArrayDouble(ArrayList<Double> acc, String fn){
		
		int len = acc.size();

	    try{
			FileOutputStream fout = new FileOutputStream(new File(fn));     
			for(int i=0;i<len;i++){  
				fout.write((acc.get(i)+"\t").getBytes());				
			fout.write(("\r\n").getBytes());   
			}   
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}
	
	public void saveArrays(ArrayList<double[]> acc, String fn){
		
		int len = acc.size();

	    try{
			FileOutputStream fout = new FileOutputStream(new File(fn));     
			for(int i=0;i<len;i++){  
				for(int j=0;j<acc.get(0).length;j++)
				  fout.write((acc.get(i)[j]+"\t").getBytes());				
			fout.write(("\r\n").getBytes());   
			}   
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}	
}