package aliview.primer;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

public class PrimerPanelCellRenderer implements TableCellRenderer, TableCellEditor {
	private static final Logger logger = Logger.getLogger(PrimerPanelCellRenderer.class);

	public Component getTableCellRendererComponent(JTable table, Object obj,
													boolean isSelected, boolean hasFocus,
													int row, int column) {
		
		PrimerPanel panel = (PrimerPanel) obj;
		panel.isSelected(isSelected);
		
		return panel;
		
	}

	public Component getTableCellEditorComponent(JTable table, Object obj,
			boolean isSelected, int row, int column) {
		logger.info("gotEditTableCellComp");
		PrimerPanel panel = (PrimerPanel) obj;
		panel.isSelected(isSelected);
		
		return panel;
	}

	public void addCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub
		
	}

	public void cancelCellEditing() {
		// TODO Auto-generated method stub
		
	}

	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCellEditable(EventObject anEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub
		
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean stopCellEditing() {
		// TODO Auto-generated method stub
		return false;
	}

}
