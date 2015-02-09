package aliview.sequencelist;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import aliview.AliView;
import aliview.AliViewWindow;
import aliview.sequences.Sequence;


/*
 * 
 *  TODO if you want to drag and drop seqs between windows then you have to serializable-them
 *  // or actually as Fasta strings..... ind import them where dropped
 * 
 * 
 */
public class SequenceTransferHandler extends TransferHandler {
	private static final Logger logger = Logger.getLogger(SequenceTransferHandler.class);
    private int[] rows    = null;
    private int addIndex  = -1;
    private int addCount  = 0;
    private final DataFlavor LOCAL_OBJECT_FLAVOR = new ActivationDataFlavor(Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
    private Object[] transferedObjects = null;
    private JComponent source = null;
	private AliViewWindow aliWindow;
   
    public SequenceTransferHandler(AliViewWindow aliWindow) {
		this.aliWindow = aliWindow;
	}
    	
    @Override
    protected Transferable createTransferable(JComponent c) {
    	logger.info("create Transferable");
        source = c;
        JList list = (JList) c;
        AlignmentListModel model = (AlignmentListModel)list.getModel();
        ArrayList<Sequence> transObjs = new ArrayList<Sequence>();
        for(Object obj: list.getSelectedValues()){
        	logger.info(obj);
          	transObjs.add((Sequence)obj);
        }
        transferedObjects = transObjs.toArray();
        return new DataHandler(transferedObjects,LOCAL_OBJECT_FLAVOR.getMimeType());
      }
   
    @Override
    public boolean canImport(TransferSupport info) {
    	
    	// Depending on the datatype also change DropMode which makes drop location line
    	// or drop location nothing painted properly
    	boolean isSupported = false;
    	if(info.isDataFlavorSupported(LOCAL_OBJECT_FLAVOR)){
    		isSupported = true;
    		((JList) info.getComponent()).setDropMode(DropMode.INSERT);
    	}
    	if(info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
    		isSupported = true;
    		((JList) info.getComponent()).setDropMode(DropMode.ON);
    	}
        
        return true;
    }
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }
    
    
    @Override
    public boolean importData(TransferSupport info) {
    	
    	 try{
    	
    		Transferable tr = info.getTransferable();
    		
	    	// This if is a small "hack" so that files also can be dropped in the list
	    	if(tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
	    		List<File> fileList = (List<File>) info.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
	    		aliWindow.fileDropped(fileList, info.getDropAction());
	    	}    	
	    	else if(tr.isDataFlavorSupported(LOCAL_OBJECT_FLAVOR)){
		        SequenceJList targetList = (SequenceJList)info.getComponent();
		        JList.DropLocation dl  = (JList.DropLocation)info.getDropLocation();
		        AlignmentListModel model = (AlignmentListModel)targetList.getModel();
		        int index = dl.getIndex();
		        logger.info("drop location index" + index);
		
		        logger.info("import");
	        
	       
	        	Object[] values = (Object[])info.getTransferable().getTransferData(LOCAL_OBJECT_FLAVOR);
	        	logger.info("" + source + targetList + values);
	        	logger.info("target index" + targetList.getSelectedIndex());
	        	
	            if(source==targetList){
    	
	            	logger.info("to index" + index);
	            	aliWindow.moveSelectedTo(index);
	            	
	            }
	            else{
	            	// This is where fasta sequences could be imported
	            }
	    	}
            
            return true;
            
        }catch(UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        }catch(java.io.IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }
    @Override
    protected void exportDone(JComponent c, Transferable t, int act) {
    	logger.info("exportDone");
    	// here I could call something when done
        //cleanup(c, act == MOVE);
    }
}