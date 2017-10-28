package aliview.old;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import aliview.AliViewWindow;
import aliview.externalcommands.CommandItem;

public class AlignerItemPanel extends JPanel{
	private static final Logger logger = Logger.getLogger(AlignerItemPanel.class);
	private static final String LF = System.getProperty("line.separator");
	private JRadioButton radioIsActivated;

	public AlignerItemPanel(final CommandItem alignItem, final AliViewWindow aliWin){



		JTextField txtCommandName = new JTextField();
		txtCommandName.setMaximumSize(new Dimension(130, 25));
		txtCommandName.setMinimumSize(new Dimension(130, 25));
		txtCommandName.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e) {
				JTextField txt = (JTextField) e.getSource();
				alignItem.setName(txt.getText());
			}
			public void focusGained(FocusEvent e) {
			}
		});

		JTextArea txtCommand = new JTextArea();
		txtCommand.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED), new EmptyBorder(2,2,2,2)));
		txtCommand.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e){
				JTextArea txt = (JTextArea) e.getSource();
				alignItem.setCommand(txt.getText());
			}
			public void focusGained(FocusEvent e) {
			}
		});



		radioIsActivated = new JRadioButton();

		logger.info("isactivated" + alignItem.getName());
		radioIsActivated.addChangeListener(new ChangeListener() {	
			public void stateChanged(ChangeEvent e) {
				JRadioButton rbtn = (JRadioButton) e.getSource();
				alignItem.setActivated(rbtn.isSelected());
			}
		});



		//		JCheckBox cbxOutputWin = new JCheckBox();
		//		cbxOutputWin.setSelected(alignItem.isShowCommandWindow());
		//		cbxOutputWin.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				JCheckBox cbx = (JCheckBox) e.getSource();
		//				alignItem.setShowCommandWindow(cbx.isSelected());
		//			}
		//		});
		//		


		txtCommandName.setText(alignItem.getName());
		txtCommand.setText(alignItem.getCommand());
		radioIsActivated.setSelected(alignItem.isActivated());

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		//GridLayout layout = new GridLayout(1,4,3,3);
		//layout. setColumnExpandRatio(0, 0.33f);
		//this.setLayout(gridLayout);
		this.add(radioIsActivated);
		//		this.add(cbxOutputWin);
		this.add(txtCommandName);
		this.add(txtCommand);
		//JButton removeButton = new JButton("Remove");
		//this.add(removeButton);
		JButton testButton = new JButton("Test");			
		//		this.add(testButton);
		testButton.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				//aliWin.reAlignEverythingWithAlignCommand(alignIt
			}
		});
		this.setMaximumSize(new Dimension(2000,30));
	}

	public JRadioButton getRadioIsActivated() {
		return radioIsActivated;
	}


}
