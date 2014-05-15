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
	
}

