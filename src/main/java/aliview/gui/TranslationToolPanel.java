package aliview.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ListDataEvent;

import aliview.AliViewWindow;
import aliview.GeneticCode;
import aliview.alignment.Alignment;
import aliview.alignment.AlignmentEvent;
import aliview.alignment.AlignmentListener;
import aliview.sequencelist.AlignmentDataEvent;
import aliview.sequencelist.AlignmentDataListener;
import aliview.sequencelist.AlignmentSelectionEvent;
import aliview.sequencelist.AlignmentSelectionListener;

public class TranslationToolPanel extends JPanel implements AlignmentListener, AlignmentDataListener, AlignmentSelectionListener {
	
	private JComboBox readingFrameBox;
	private JComboBox genCodeBox;

	public TranslationToolPanel(final AliViewWindow aliViewWindow) {
	
	this.setVisible(false);
	this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	
	JToggleButton toggleBtnAACode = new JToggleButton();//("");
	toggleBtnAACode.setPreferredSize(new Dimension(32,32));
	toggleBtnAACode.setMaximumSize(new Dimension(32,32));
	toggleBtnAACode.setToolTipText("Show Amino Acid code (when translated sequence)");
	toggleBtnAACode.setIcon(AppIcons.getShowAACodeIcon());
	toggleBtnAACode.setModel(aliViewWindow.getAliMenuBar().getShowAACodeButtonModel());
	this.add(toggleBtnAACode);
	
	JToggleButton toggleBtnTransOnePos = new JToggleButton();//("");
	toggleBtnTransOnePos.setPreferredSize(new Dimension(32,32));
	toggleBtnTransOnePos.setMaximumSize(new Dimension(32,32));
	toggleBtnTransOnePos.setToolTipText("Show Translation as only one character Amino Acid)");
	toggleBtnTransOnePos.setIcon(AppIcons.getTransOnePosIcon());
	toggleBtnTransOnePos.setModel(aliViewWindow.getAliMenuBar().getTransOnePosButtonModel());
	this.add(toggleBtnTransOnePos);
	
	JToggleButton toggleBtnShowCodon = new JToggleButton();//("");
	toggleBtnShowCodon.setPreferredSize(new Dimension(32,32));
	toggleBtnShowCodon.setMaximumSize(new Dimension(32,32));
	toggleBtnShowCodon.setToolTipText("Show/Hide codon positions on toolbar");
	toggleBtnShowCodon.setIcon(AppIcons.getShowCodonIcon());
	toggleBtnShowCodon.setModel(aliViewWindow.getAliMenuBar().getDrawCoonPosOnRulerButtonModel());
	this.add(toggleBtnShowCodon);
	
	JButton btnCodon1Select = new JButton();
	btnCodon1Select.setPreferredSize(new Dimension(32,32));
	btnCodon1Select.setMaximumSize(new Dimension(32,32));
	btnCodon1Select.setToolTipText("Set selection as coding 1-2-3");
	btnCodon1Select.setIcon(AppIcons.getCoding1Icon());
	btnCodon1Select.setModel(aliViewWindow.getAliMenuBar().getCoding0ButtonModel());
	this.add(btnCodon1Select);
	
	JButton btnCodon2Select = new JButton();
	btnCodon2Select.setPreferredSize(new Dimension(32,32));
	btnCodon2Select.setMaximumSize(new Dimension(32,32));
	btnCodon2Select.setToolTipText("Set selection as coding 2-3-1");
	btnCodon2Select.setIcon(AppIcons.getCoding2Icon());
	btnCodon2Select.setModel(aliViewWindow.getAliMenuBar().getCoding1ButtonModel());
	this.add(btnCodon2Select);
	
	JButton btnCodon3Select = new JButton();
	btnCodon3Select.setPreferredSize(new Dimension(32,32));
	btnCodon3Select.setMaximumSize(new Dimension(32,32));
	btnCodon3Select.setToolTipText("Set selection as coding 3-1-2");
	btnCodon3Select.setIcon(AppIcons.getCoding3Icon());
	btnCodon3Select.setModel(aliViewWindow.getAliMenuBar().getCoding2ButtonModel());
	this.add(btnCodon3Select);
	
	JButton btnCodonNoneSelect = new JButton();
	btnCodonNoneSelect.setPreferredSize(new Dimension(32,32));
	btnCodonNoneSelect.setMaximumSize(new Dimension(32,32));
	btnCodonNoneSelect.setToolTipText("Set selection as non-coding");
	btnCodonNoneSelect.setIcon(AppIcons.getCodingNoneIcon());
	btnCodonNoneSelect.setModel(aliViewWindow.getAliMenuBar().getCodingNoneButtonModel());
	this.add(btnCodonNoneSelect);
	
	JButton btnCountCodon = new JButton();
	btnCountCodon.setPreferredSize(new Dimension(32,32));
	btnCountCodon.setMaximumSize(new Dimension(32,32));
	btnCountCodon.setToolTipText("Count stop codons");
	btnCountCodon.setIcon(AppIcons.getCountCodonIcon());
	btnCountCodon.setModel(aliViewWindow.getAliMenuBar().getCountCodonButtonModel());
	this.add(btnCountCodon);
	
	Component horizontalStrut_1 = Box.createHorizontalStrut(10);
	this.add(horizontalStrut_1);
	
	JLabel lblNewLabel = new JLabel("Reading frame:");
	this.add(lblNewLabel);
	
	Integer[] readingFrames = new Integer[]{1,2,3};
	
	readingFrameBox = new JComboBox();
	readingFrameBox.setMaximumSize(new Dimension(60, 25));
	readingFrameBox.setPreferredSize(new Dimension(40, 25));
	for(Integer value: readingFrames){
			readingFrameBox.addItem(value);
	}
		
	readingFrameBox.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e){
				JComboBox box = (JComboBox) e.getSource();
				Integer selectedFrame = (Integer)box.getSelectedItem();
				aliViewWindow.setReadingFrame(selectedFrame.intValue());
			}
		});
	this.add(readingFrameBox);
	
	Component horizontalStrut_2 = Box.createHorizontalStrut(10);
	this.add(horizontalStrut_2);
	
	genCodeBox = new JComboBox();
	genCodeBox.setMaximumSize(new Dimension(170, 25));
	genCodeBox.setPreferredSize(new Dimension(170, 25));
	for(GeneticCode value: GeneticCode.allCodesArray){
		genCodeBox.addItem(value);
	}
		
	genCodeBox.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e){
				JComboBox box = (JComboBox) e.getSource();
				GeneticCode selected = (GeneticCode)box.getSelectedItem();
				aliViewWindow.setGeneticCode(selected);
			}
		});
	this.add(genCodeBox);
	}
	

	/*
	 * 
	 * 
	 * AlignmentListener
	 * 
	 * 
	 */
	public void newSequences(AlignmentEvent alignmentEvent) {
	}

	public void alignmentMetaChanged(AlignmentEvent alignmentEvent) {
		Alignment aliment = alignmentEvent.getSource();
		readingFrameBox.setSelectedItem(new Integer(aliment.getReadingFrame()));
		genCodeBox.setSelectedItem(aliment.getGeneticCode());
	}

	
	
	//
	// AlignmentDataListener
	//
	public void intervalAdded(ListDataEvent e) {
		if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}

	public void intervalRemoved(ListDataEvent e) {
        if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}  	
	
	public void contentsChanged(ListDataEvent e) {
		if(e instanceof AlignmentDataEvent){
			contentsChanged((AlignmentDataEvent)e);
		}
	}
	
	public void contentsChanged(AlignmentDataEvent e) {
	}
	
	//
	// AlignmentSelectionListener
	//
	public void selectionChanged(AlignmentSelectionEvent e) {
		
	}
	
}