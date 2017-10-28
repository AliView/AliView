package aliview.primer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import aliview.AliViewWindow;
import aliview.gui.AppIcons;

public class PrimerResultsFrame extends JFrame {
	private static final Logger logger = Logger.getLogger(PrimerResultsFrame.class);
	private static final int MAX_NUMBER_OF_PRIMERS_REPORTED = 1000;
	JPanel mainPanel = new JPanel();
	private final AliViewWindow aliViewWindow;
	JTable mainTable;
	PrimerDetailFrame primerDetailFrame;
	private ArrayList<Primer> primerResult;

	public PrimerResultsFrame(ArrayList<Primer> primRes,AliViewWindow aliViewWin){
		this.aliViewWindow = aliViewWin;
		this.primerResult = primRes;

		DefaultTableModel tm = new DefaultTableModel(PrimerResultTableRow.getColumnHeaders().toArray(),0);

		mainTable = new JTable(tm);
		mainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mainTable.setRowHeight(44);
		//mainTable.setDefaultRenderer(PrimerPanel.class, new PrimerPanelCellRenderer());
		mainTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				// If cell selection is enabled, both row and column change events are fired
				if (e.getValueIsAdjusting()) {
					// The mouse button has not yet been released
				}
				else{
					displaySelectedPrimerDetailWindow();
				}
			}

		});	

		mainTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == e.BUTTON3){
					logger.info("copy Primer To clipboard");

					int colIndex = 0;
					if(mainTable.getSelectedColumn() == colIndex){
						PrimerPanel selected = (PrimerPanel) mainTable.getModel().getValueAt(mainTable.getSelectedRow(),0);   	
						String data = selected.getPrimer().getPrimerDetailsAsText();

						System.out.println(data);
					}
				}
				// always display window
				displaySelectedPrimerDetailWindow();

			}

		});

		Enumeration<TableColumn> enu = mainTable.getColumnModel().getColumns();
		while(enu.hasMoreElements()) {
			TableColumn col = enu.nextElement();
			col.setCellRenderer(new ComponentCellRenderer());
			col.setCellEditor(new ComponentCellRenderer());
		}

		for(int n = 0; n < mainTable.getColumnModel().getColumnCount(); n++){
			mainTable.getColumnModel().getColumn(n).setPreferredWidth(PrimerResultTableRow.getColumnSizes().get(n));
		}

		JScrollPane scrollPane = new JScrollPane(mainTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		int nCount = 0;
		for(Primer primer: primRes){
			if(primer.getScore() <= 1000 && nCount < MAX_NUMBER_OF_PRIMERS_REPORTED){
				//logger.info("AddingPanel");
				//tm.addRow(new Object[]{new PrimerPanel(primer)});
				//logger.info("AddingRow");
				tm.addRow( new PrimerResultTableRow(primer).getRow().toArray() );
				nCount ++;

			}
		}


		this.setTitle("Primer finds");
		this.setIconImage(AppIcons.getProgramIconImage());
		this.setPreferredSize(new Dimension(700,400));
		this.placeFrameupperLeftLocationOfThis(aliViewWin);
		this.pack();
		this.setVisible(true);

	}

	public void placeFrameupperLeftLocationOfThis(Component parent){
		if(parent != null){
			int newX = parent.getX() + 100;
			int newY = parent.getY() + 100;
			this.setLocation(newX, newY);
		}
	}	

	protected void displaySelectedPrimerDetailWindow() {
		int colIndex = 0;	  
		int rowIndex = mainTable.getSelectedRow();    	
		Primer selectedPrimer = primerResult.get(rowIndex);     	

		//PrimerPanel selected = (PrimerPanel) mainTable.getModel().getValueAt(mainTable.getSelectedRow(),0);

		// find primer in alignment
		aliViewWindow.performFind(selectedPrimer.getSequence());

		// show detail in new frame 	
		String data = selectedPrimer.getPrimerDetailsAsText();

		if(primerDetailFrame == null){
			primerDetailFrame = new PrimerDetailFrame(aliViewWindow);
		}
		primerDetailFrame.setText(data);
		primerDetailFrame.setVisible(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		if(primerDetailFrame != null){
			primerDetailFrame.dispose();
		}
	}




}
