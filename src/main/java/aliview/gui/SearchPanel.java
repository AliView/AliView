package aliview.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;


public class SearchPanel extends JPanel{
	private static final Logger logger = Logger.getLogger(SearchPanel.class);
	JTextField searchField;
	JLabel searchMessageLabel;
	Color NOT_FOUND_COLOR = new Color(255,182,182);
	Color defaultBG_color;
	
	public SearchPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
//		setMinimumSize(new Dimension(230, 10));
//		setMaximumSize(new Dimension(230, 30));
		setPreferredSize(new Dimension(210, 30));

		searchField = new JTextField();
		defaultBG_color = searchField.getBackground();
		searchField.setText("Search");
//		searchField.getDocument().addDocumentListener(new DocumentListener(){
//		     public void changedUpdate(    DocumentEvent e){
//		       logger.info("changed");
//		      }
//		     public void insertUpdate(    DocumentEvent e){
//		    	 logger.info("insert");
//		    	 String text = searchField.getText();
//		    	 if(text.contains("\n")){
//		    		   logger.info("contains \n");
//				       text = text.replaceAll("\n", "");
//				       searchField.setText(text);
//			       }
//		    	 if(text.contains("\r")){
//		    		   logger.info("contains \r");
//				       text = text.replaceAll("\r", "");
//				       searchField.setText(text);
//			       }
//		      }
//		     public void removeUpdate(    DocumentEvent e){
//		       
//		      }
//		    });
		
		
		if(searchField.getDocument() instanceof PlainDocument){
			PlainDocument doc = (PlainDocument) searchField.getDocument();
			doc.setDocumentFilter(new TrimPastedTextFilter());
		}
		
		searchField.setColumns(10);
		searchField.setEnabled(false);
		searchField.setHorizontalAlignment(SwingConstants.LEFT);
		searchField.setMaximumSize(new Dimension(130,30));
//		searchField.setMinimumSize(new Dimension(130,30));
		searchField.setPreferredSize(new Dimension(130, 30));
		searchField.addFocusListener(new FocusListener(){		
			public void focusLost(FocusEvent e) {
				searchMessageLabel.setText("");	
				//searchField.setBackground(defaultBG_color);
				searchField.setEnabled(false);		
			}
			public void focusGained(FocusEvent e) {
				searchField.setEnabled(true);
				searchField.selectAll();
			}
		});
		searchField.addMouseListener(new MouseListener() {		
			public void mouseReleased(MouseEvent e) {
			}		
			public void mousePressed(MouseEvent e) {
			}			
			public void mouseExited(MouseEvent e) {				
			}			
			public void mouseEntered(MouseEvent e) {
				searchField.setEnabled(true);				
			}			
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		
		
		searchMessageLabel = new JLabel();
		searchMessageLabel.setHorizontalAlignment(JLabel.TRAILING);
		searchMessageLabel.setMaximumSize(new Dimension(80,30));
//		searchMessageLabel.setMinimumSize(new Dimension(95,20));
		searchMessageLabel.setPreferredSize(new Dimension(80, 30));
		
		this.add(searchMessageLabel);
		this.add(Box.createHorizontalStrut(5));
		this.add(searchField);
				
	}
	
	public JTextField getSearchField() {
		return searchField;
	}
	
	public JLabel getSearchMessageLabel() {
		return searchMessageLabel;
	}

	public void setText(String searchText) {
		searchField.setText(searchText);
	}

	public String getText() {
		return searchField.getText();
	}

	public void requestFocusAndSelectAll() {
		searchField.requestFocus();	
	}

	public void setFoundMessage() {
		searchMessageLabel.setText("");
		searchField.setBackground(defaultBG_color);
	}
	public void setNoFoundMessage() {
		searchMessageLabel.setText("not found");
		searchField.setBackground(NOT_FOUND_COLOR);
	}
	
	class TrimPastedTextFilter extends DocumentFilter {
		   @Override
		   public void insertString(FilterBypass fb, int offset, String inText, AttributeSet attrs) throws BadLocationException {

			  logger.info("instr:" + inText);	  
			  // trim pasted data from spaces (new line or CR are converted to this when pasted=
			  // length larger than > 1 makes sure we are checking pasted data an still allowing space if typed
			  if(inText != null && inText.length() > 1){
				  inText = inText.trim();
			  }		      
		      super.insertString(fb, offset, inText, attrs);
		   }	  

		   @Override
		   public void replace(FilterBypass fb, int offset, int length, String inText, AttributeSet attrs) throws BadLocationException {		   
			   logger.info("replace");		   
			   // trim pasted data from spaces (new line or CR are converted to this when pasted=
			   // length larger than > 1 makes sure we are checking pasted data an still allowing space if typed
			   if(inText != null && inText.length() > 1){
					  inText = inText.trim();
			   }			   
			   super.replace(fb, offset, length, inText, attrs);
		   }

		   @Override
		   public void remove(FilterBypass fb, int offset, int length) throws BadLocationException { 
			   logger.info("remove");
			   super.remove(fb, offset, length);
		   }
	}
	
}


