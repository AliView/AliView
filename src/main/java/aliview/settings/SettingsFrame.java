package aliview.settings;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import aliview.aligner.AlignerADDItemsPanel;
import aliview.aligner.AlignerALLItemsPanel;
import aliview.alignment.Alignment;
import aliview.externalcommands.CmdItemsPanel;
import aliview.gui.AppIcons;

public class SettingsFrame extends JFrame {
	private static final Logger logger = Logger.getLogger(SettingsFrame.class);
	JTabbedPane tabbedPane;
	public static final String TAB_EXTERNAL_COMMANDS = "External commands";
	public static final String TAB_GENERAL = "General";
	public static final String TAB_PRIMER = "Primer";
	public static final String TAB_ALIGN_ALL = "Align ALL program";
	public static final String TAB_ALIGN_ADD =  "Align ADD program";
	public static final String TAB_MEMORY = "Memory";
	
	public SettingsFrame(Component parent){
		init();
//		this.setSize(new Dimension(650,500));
//		this.setMaximumSize(new Dimension(650,500));
		this.setAlwaysOnTop(true);
		this.pack();
		this.centerLocationToThisComponent(parent);
	}
	
	private void init(){
		tabbedPane = new JTabbedPane();
//		tabbedPane.setPreferredSize(new Dimension(650,500));
		tabbedPane.add(TAB_GENERAL, new GeneralSettingsPanel(this));
		tabbedPane.add(TAB_PRIMER, new PrimerSettingsPanel(this));
		tabbedPane.add(TAB_ALIGN_ALL, new AlignerALLItemsPanel(this));
		tabbedPane.add(TAB_ALIGN_ADD, new AlignerADDItemsPanel(this));
		tabbedPane.add(TAB_EXTERNAL_COMMANDS, new CmdItemsPanel(this));
		tabbedPane.add(TAB_MEMORY, new MemorySettingsPanel(this));
		this.getContentPane().add(tabbedPane);
		this.setIconImage(AppIcons.getProgramIconImage());
		this.setTitle("Settings");
		this.setPreferredSize(new Dimension(700,600));
	}
	
	public void reload(){
		int index = tabbedPane.getSelectedIndex();
		this.getContentPane().remove(tabbedPane);
		init();
		tabbedPane.setSelectedIndex(index);
		validate();		
	}
	
	public void selectTab(String name){
		int tabIndex = 0;
		for(int n = 0; n < tabbedPane.getTabCount(); n++){
			if(tabbedPane.getTitleAt(n).equals(name)){
				tabIndex = n;
			}
		}
		tabbedPane.setSelectedIndex(tabIndex);
	}
	
	public void centerLocationToThisComponent(Component parent){
		// align to middle of parent window
		if(parent != null){
			int newX = parent.getX() + parent.getWidth()/2 - this.getWidth()/2;
			int newY = parent.getY() + parent.getHeight()/2 - this.getHeight()/2;
			this.setLocation(newX, newY);
		}
	}
}
