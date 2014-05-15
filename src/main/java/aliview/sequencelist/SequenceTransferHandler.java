package aliview.sequencelist;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

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
    private final DataFlavor localObjectFlavor; // = DataFlavor.stringFlavor;
    private Object[] transferedObjects = null;
    private JComponent source = null;
    
    // If you want you could export as FastaString, and skip importing if drag-drop is within
    // Same list
    public SequenceTransferHandler() {
        localObjectFlavor = new ActivationDataFlavor(
          Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
      }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        source = c;
        JList list = (JList) c;
        SequenceListModel model = (SequenceListModel)list.getModel();
        ArrayList<Sequence> transObjs = new ArrayList<Sequence>();
        for(Object obj: list.getSelectedValues()){
        	logger.info(obj);
          	transObjs.add((Sequence)obj);
        }
        transferedObjects = transObjs.toArray();
        return new DataHandler(transferedObjects,localObjectFlavor.getMimeType());
      }
   
    @Override
    public boolean canImport(TransferSupport info) {
        boolean b = info.isDataFlavorSupported(localObjectFlavor);
        //logger.info("b" + b);
        return b;
    }
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }
    
    
    @Override
    public boolean importData(TransferSupport info) {
        SequenceJList targetList = (SequenceJList)info.getComponent();
        JList.DropLocation dl  = (JList.DropLocation)info.getDropLocation();
        SequenceListModel model = (SequenceListModel)targetList.getModel();
        int index = dl.getIndex();
        logger.info(index);

        logger.info("import");
        
        try{
        	Object[] values = (Object[])info.getTransferable().getTransferData(localObjectFlavor);
        	logger.info("" + source + targetList + values);
        	
            if(source==targetList){
            	
            	// TODO this is not undoable.....
            	targetList.moveSelectedSequencesTo(index);
            	// model.moveSequencesTo(index, selection);
            	
            }
            else{
            	// This is where fasta sequences could be imported
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
    /*
    private void cleanup(JComponent src, boolean remove) {
        if(remove && rows != null) {
        	JList table = (JList)src;
   //         src.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            DefaultTableModel model = (DefaultTableModel)table.getModel();
            if(addCount > 0) {
                for(int i=0;i<rows.length;i++) {
                    if(rows[i]>=addIndex) {
                        rows[i] += addCount;
                    }
                }
            }
            for(int i=rows.length-1;i>=0;i--) model.removeRow(rows[i]);
        }
        rows     = null;
        addCount = 0;
        addIndex = -1;
    }
    */
}