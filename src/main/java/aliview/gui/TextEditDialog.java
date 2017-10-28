package aliview.gui;

import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import aliview.AliViewWindow;

public class TextEditDialog {
	private static final String LF = System.getProperty("line.separator");
	public static final String TITLE_EDIT_SEQUENCE_NAME = "Rename sequence";
	public static final String TITLE_GO_TO_POS = "Go to position (x) or (,y) or (x,y)";
	private static JTextField textEdit;
	protected Integer selectedValue = -1;
	private Point preferredPos;
	
	public TextEditDialog() {
	}
	
	public TextEditDialog(Point pos) {
		this.preferredPos = pos;
	}

	public void showOKCancelTextEditor(String editString, String title, AliViewWindow aliViewWindow) {
		
		final JDialog dialog = new JDialog(aliViewWindow);
		dialog.setTitle(title);
		dialog.setModal(true);
		dialog.setAlwaysOnTop(true);
		dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
		
		textEdit = new JTextField(editString);
		textEdit.setEditable(true);		
		textEdit.setMaximumSize(new Dimension(800,30));
		textEdit.setPreferredSize(new Dimension(350,30));

		JPanel messagePanel = new JPanel();
//		testPanel.setPreferredSize(new Dimension(350,10));
//		testPanel.setMaximumSize(new Dimension(350,10));
//		testPanel.add(textEdit);
		
		Object[] messageAndEditor = new Object[]{messagePanel,textEdit};
		//Object[] messageAndEditor = new Object[]{textEdit};
		
		JOptionPane optPane = new JOptionPane(messageAndEditor,JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		optPane.addPropertyChangeListener(new PropertyChangeListener()
		   {
		      public void propertyChange(PropertyChangeEvent e)
		      {
		         if (e.getPropertyName().equals("value"))
		         {
		        	 
		        	selectedValue = (Integer) e.getNewValue();
		            switch (selectedValue)
		            {
		               case JOptionPane.OK_OPTION:
		                  break;
		               case JOptionPane.CANCEL_OPTION:
		                  //user clicks CANCEL
		                  break;                       
		            }
		            dialog.dispose();
		         }
		      }
		   });
		
		dialog.setContentPane(optPane);
		dialog.pack();
		if(preferredPos != null){
			dialog.setLocation(preferredPos);
			makeSureWithinBounds(dialog, aliViewWindow);
		}
		else{
			dialog.setLocationRelativeTo(aliViewWindow);
		}
		textEdit.selectAll();
		dialog.setVisible(true);
	}
	
	private void makeSureWithinBounds(JDialog dialog, AliViewWindow aliViewWindow) {
		Rectangle smaller = dialog.getBounds();
		Rectangle larger = aliViewWindow.getBounds();
		
		int topDiff = larger.x - smaller.x;
		int bottomDiff = larger.y + larger.height - (smaller.y + smaller.height);
		
		if(topDiff > 0){
			dialog.setLocation(smaller.x, larger.y);
		}
		if(bottomDiff < 0){
			dialog.setLocation(smaller.x, smaller.y + bottomDiff);
		}
	}

	public Integer getSelectedValue() {
		return selectedValue;
	}

	public String getEditText() {
		return textEdit.getText();
	}
}

