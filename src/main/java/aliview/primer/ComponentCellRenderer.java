package aliview.primer;

import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

public class ComponentCellRenderer implements TableCellRenderer, TableCellEditor {
	private static final Logger logger = Logger.getLogger(ComponentCellRenderer.class);
	private static final LineBorder unselectedBorder = new LineBorder(Color.LIGHT_GRAY);
	private static final LineBorder selectedBorder = new LineBorder(Color.DARK_GRAY);

	public Component getTableCellRendererComponent(JTable table, Object obj,
			boolean isSelected, boolean hasFocus,
			int row, int column) {

		JComponent comp = (JComponent) obj;
		if(isSelected){
			comp.setBorder(selectedBorder);
		}else{
			comp.setBorder(unselectedBorder);
		}

		return (Component) obj;

	}

	public Component getTableCellEditorComponent(JTable table, Object obj,
			boolean isSelected, int row, int column) {

		return (Component) obj;
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
