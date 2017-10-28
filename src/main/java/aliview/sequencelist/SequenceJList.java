package aliview.sequencelist;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.datatransfer.FlavorMap;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicListUI;

import org.apache.log4j.Logger;

import utils.OSNativeUtils;
import aliview.AliView;
import aliview.AliViewWindow;
import aliview.UndoControler;
import aliview.gui.pane.NotUsed_AlignmentPane_Orig;
import aliview.sequences.Sequence;
import aliview.sequences.SequenceSelectionModel;
import aliview.settings.Settings;
import aliview.undo.UndoSavedStateSequenceOrder;


public class SequenceJList extends javax.swing.JList implements Autoscroll{
	private static final Logger logger = Logger.getLogger(SequenceJList.class);
	// todo These two constants should be synchronized in one class (AlignmentPane & this)
	private static final int MIN_CHAR_SIZE = 2;
	private static final int MAX_CHAR_SIZE = 100;
	private double charHeight;
	private ListCellRenderer storedCellRenderer;
	private BasicListUI builist;
	//private DefaultListModel<String> notUsed;
	private JScrollPane alignmentScrollPane;
	private JScrollPane listScrollPane;


	public SequenceJList(AlignmentListModel model, double charHeight, AliViewWindow aliWindow) {
		super(model);
		//this.addMouseMotionListener(new SequenceListMouseListener());
		//this.getParent().addMouseMotionListener(new SequenceListMouseListener());

		/*
		DropTarget old = this.getDropTarget();
		DropTarget newDT = new MyOtherDropTarget();	
		newDT.setComponent(old.getComponent());
		newDT.setFlavorMap(old.getFlavorMap());
		newDT.setDefaultActions(old.getDefaultActions());
		this.setDropTarget(newDT);
		 */

		//jComponent.setTransferHandler(new MyTransferHandler());
		this.setDropMode(DropMode.INSERT);
		this.setTransferHandler(new SequenceTransferHandler(aliWindow));

		// Add modified drop target due to otherwise erratic drag-scrolling
		DropTarget original = this.getDropTarget();// the Swing DropTarget
		MyDropTarget myDropTarget = new MyDropTarget();
		try {
			myDropTarget.addDropTargetListener(original);
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// delegate for original behavior
		this.setDropTarget(myDropTarget);

		this.setDragEnabled(true);


		this.setSelectionModel(model.getAlignmentSelectionModel().getSequenceListSelectionModel());
		//this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


		this.setCharSize(charHeight);


		// int width = model.getLongestSequenceName();
		// int fixedWidth = 300;

		this.setCellRenderer(new FasterTextCellRenderer()); 	


		this.setBorder(new EmptyBorder(0,0,0,0));

		// Remove default ctrl-C action (because it only copys names in list and not sequence)
		InputMap map = this.getInputMap();

		// remove from input map is not working so I am replacing one with nothing
		map.put(OSNativeUtils.getPasteKeyAccelerator(),"null");
		map.put(OSNativeUtils.getCopyKeyAccelerator(),"null");
		map.put(OSNativeUtils.getCopySelectionAsFastaKeyAccelerator(),"null");
		map.put(OSNativeUtils.getMoveSelectionUpKeyAccelerator(),"null");
		map.put(OSNativeUtils.getMoveSelectionDownKeyAccelerator(),"null");
		map.put(OSNativeUtils.getIncreaseFontSizeKeyAccelerator(),"null");
		map.put(OSNativeUtils.getDecreaseFontSizeKeyAccelerator(),"null");

	}

	@Override
	public AlignmentListModel getModel() {
		// TODO Auto-generated method stub
		return (AlignmentListModel) super.getModel();
	}

	public void setModel(AlignmentListModel model) {
		super.setModel(model);
	}


	public void paintComponent(Graphics g){
		long startTime = System.currentTimeMillis();
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g);
		long endTime = System.currentTimeMillis();
		logger.info("Draw JList took " + (endTime - startTime) + " milliseconds");	
	}

	@Override
	public void validate() {
		long startTime = System.currentTimeMillis();
		super.validate();
		long endTime = System.currentTimeMillis();
		synchAlignmentScrollPane();
		logger.info("Validate JList took " + (endTime - startTime) + " milliseconds");	
	}


	private void synchAlignmentScrollPane(){
		logger.info("synch ScrollPanes");
		JScrollPane source = listScrollPane;
		JScrollPane dest = alignmentScrollPane;
		Point viewPos = new Point(dest.getViewport().getViewPosition().x, source.getViewport().getViewPosition().y );
		dest.getViewport().setViewPosition(viewPos);	
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		//logger.info("super.getPreferredScrollableViewportSize();" + super.getPreferredScrollableViewportSize());
		return super.getPreferredScrollableViewportSize();
	}

	public void setCharSize(double charHeight) {
		this.charHeight = charHeight;

		// And now check font size
		float listFontSize = (int)(charHeight -1);
		if(listFontSize > 13 && !Settings.getUseCustomFontSize().getBooleanValue()){	
			listFontSize = 13;
		}	
		updateCharSize(listFontSize);
	}

	@Override
	public void revalidate(){
		super.revalidate();
	}

	@Override
	public Dimension getPreferredSize() {
		//logger.info(super.getPreferredSize());
		return super.getPreferredSize();
	}

	private void updateCharSize(float listFontSize) {


		// Fixed cell height is needed or otherwise all items are loaded
		this.setFixedCellHeight((int)charHeight);
		this.setFixedCellWidth(this.getModel().getLongestSequenceName()*(int)(charHeight));

		this.setFont(this.getFont().deriveFont(listFontSize));


		// Remove List cell renderer att small sizes (saves a lot of drawing speed)
		if(charHeight < 3 && this.getCellRenderer() != null){
			this.storedCellRenderer = this.getCellRenderer();
			this.setCellRenderer(null);
		}else if(charHeight >= 3 && this.getCellRenderer() == null){
			this.setCellRenderer(this.storedCellRenderer);
		}

	}


	/*
	 * This is for drop support from SequenceTransferHandler
	 */
	public void moveSelectedSequencesTo(int index) {
		getModel().moveSelectedSequencesTo(index);
	}

	public void addSynchPanes(JScrollPane listScrollPane, JScrollPane alignmentScrollPane) {
		this.listScrollPane = listScrollPane;
		this.alignmentScrollPane = alignmentScrollPane;	
	}

	/*
	 * 
	 * Drop target Autoscroll interface
	 * 
	 */

	public Insets getAutoscrollInsets(){
		//return autoscrollInsets;
		return new Insets(this.HEIGHT, 100, this.HEIGHT, 100);
		//return getInsets();
	}


	public void autoscroll(Point cursor) {
		//	logger.info("autoscroll loc=" + cursor);
		Rectangle visiRect = this.getVisibleRect();
		//	logger.info("visiRect=" + visiRect);


		// depending on how close pointer is to border the more indexex get visible at a time

		int topDist = cursor.y - visiRect.y;
		int bottomDist = (visiRect.y + visiRect.height) - cursor.y;

		int scrollSpeed = 0;
		if(topDist < 20){
			scrollSpeed = -1;
		}
		if(topDist < 15){
			scrollSpeed = -2;
		}
		if(topDist < 10){
			scrollSpeed = -3;
		}
		if(topDist < 5){
			scrollSpeed = -4;
		}
		if(topDist < 2){
			scrollSpeed = -5;
		}

		if(bottomDist < 20){
			scrollSpeed = 1;
		}
		if(bottomDist < 15){
			scrollSpeed = 2;
		}
		if(bottomDist < 10){
			scrollSpeed = 3;
		}
		if(bottomDist < 5){
			scrollSpeed = 4;
		}
		if(bottomDist < 2){
			scrollSpeed = 5;
		}

		if(scrollSpeed > 0){
			int lastVisible = this.getLastVisibleIndex();
			ensureIndexIsVisible(lastVisible + scrollSpeed);
		}

		if(scrollSpeed < 0){
			int firstVisible = this.getFirstVisibleIndex();
			ensureIndexIsVisible(firstVisible + scrollSpeed);
		}

	}

	public Point getFirstSelectedCellPos(){
		int index = getSelectedIndex();
		if(index < 0){
			return new Point(0,0);
		}
		Point pos = indexToLocation(index);
		SwingUtilities.convertPointToScreen(pos, this);
		return pos;
	}


}
