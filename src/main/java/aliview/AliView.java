package aliview;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.JarURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.ApplicationEvent;
import org.simplericity.macify.eawt.ApplicationListener;
import org.simplericity.macify.eawt.DefaultApplication;

import utils.DialogUtils;
import utils.FileUtilities;
import utils.OSNativeUtils;
import aliview.gui.AliViewJMenuBarFactory;
import aliview.gui.RepeatingKeyEventsFixer;
import aliview.messenges.Messenger;
import aliview.settings.Settings;
import aliview.test.Test;

public class AliView implements ApplicationListener{

	private static final String LF = System.getProperty("line.separator");
	private static final AliViewJMenuBarFactory menuBarFactory = new AliViewJMenuBarFactory();
	private static AliView aliView;
	private static ArrayList<AliViewWindow> aliViewWindows = new ArrayList<AliViewWindow>();
	private static AliViewWindow activeWindow = null;
	private static final Logger logger = Logger.getLogger(AliView.class);
	private static File savedInitialArgumentAlignmentFileForMac = null;
	private static boolean debugMode = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args){
		
		// First set max logging for startup, then at end of initialization turn off
//		// it can then be turned on manually from menu
//		System.setErr( new PrintStream( new LoggingOutputStream( logger, Level.ERROR ), true));
//		System.setOut( new PrintStream( new LoggingOutputStream( logger, Level.INFO ), true));
		
		long startTime = System.currentTimeMillis();
		
		Logger.getRootLogger().setLevel(Level.ALL);
		logAllLogs();
			
		logger.info("version " + AliView.getVersion());
		long time = AliView.getTime(AliView.class);
		logger.info("version time " + new Date(time));
		
		System.out.println("Time to here in ms = " + ( System.currentTimeMillis() - startTime));

		Properties props = System.getProperties();
		props.list(System.out);

		try{
			
			// check for debug args
			boolean hasDebugArg = false;
			if(args != null && args.length >= 1){	
				for(String arg: args){
					if("debug".equalsIgnoreCase(arg)){
						hasDebugArg = true;
					}
				}
			}
			// check if debug in user environ
			String debugEnv = System.getenv("ALIVIEW_DEBUG");
			logger.info("debugEnv" + debugEnv);

			debugEnv = null;

			
			
			
			// Set exception handler that takes care of error that are uncaught in the GUI-thread (Event-dispatching-queue)
			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			      public void uncaughtException(Thread t, Throwable e) {
			    	  e.printStackTrace();
			    	  if(e instanceof OutOfMemoryError){
			    		  Messenger.showOKOnlyMessage(Messenger.OUT_OF_MEMORY_ERROR, activeWindow);
			    	  }
			      }
			    });

			RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
			List<String> aList = bean.getInputArguments();

			for (int i = 0; i < aList.size(); i++) {
				 logger.info("" +  aList.get( i ));
			}
			// print the non-JVM command line arguments using args
			// name of the main class
			logger.info(" " + System.getProperty("sun.java.command"));
			
			logger.info("java.vendor" + System.getProperty("java.vendor"));
			logger.info("java.version" + System.getProperty("java.version"));

			// Log program args
			if(args != null){
				logger.info("args.length=" + args.length);
				for(String arg: args){	
					logger.info("arg=" + arg);
				}
			}
			else{
				logger.info("args(null)=" + args);
			}
			
			// set debug mode
						if(hasDebugArg || (debugEnv != null)){
							AliView.setDebug(true);
							}
						else{
							AliView.setDebug(false);
						}
						

			// I think this issue is solved now when creating actions and adding keybinding to the root pane
//			if(OSNativeUtils.isLinuxOrUnix()){
//				RepeatingKeyEventsFixer rf = new RepeatingKeyEventsFixer();
//				rf.install();
//			}
			
			// Quick tooltips and for a long time
			ToolTipManager.sharedInstance().setInitialDelay(100);
			ToolTipManager.sharedInstance().setDismissDelay(3000);

			// -Dsun.java2d.opengl=true
			//System.setProperty("sun.java2d.opengl","True");
	 
			
			if(OSNativeUtils.isMac()){
				
//				System.setProperty("awt.useSystemAAFontSettings","on");
//				System.setProperty("swing.aatext", "true");

				// Default look and feel mac is already Aqua

				// This might actually be to late for setting mac-application menu
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				// Mac application menu
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "AliView");

				logger.info("apple.awt.antialiasing" + System.getProperty("apple.awt.antialiasing"));
				logger.info("apple.awt.graphics.UseQuartz" + System.getProperty("apple.awt.graphics.UseQuartz"));
				logger.info("apple.awt.graphics.EnableQ2DX" + System.getProperty("apple.awt.graphics.EnableQ2DX"));
				logger.info("apple.awt.rendering" + System.getProperty("apple.awt.rendering"));

				// ToDo turn on anti-aliasing on Mac
//				System.setProperty("apple.awt.antialiasing","on");
				//				System.setProperty("apple.awt.rendering", "VALUE_RENDER_SPEED");		
				//System.setProperty("apple.awt.graphics.UseQuartz", "false");
				//System.setProperty("apple.awt.graphics.EnableQ2DX", "true");

				logger.info("apple.awt.antialiasing" + System.getProperty("apple.awt.antialiasing"));
				logger.info("apple.awt.graphics.UseQuartz" + System.getProperty("apple.awt.graphics.UseQuartz"));
				logger.info("apple.awt.graphics.EnableQ2DX" + System.getProperty("apple.awt.graphics.EnableQ2DX"));
				logger.info("apple.awt.rendering" + System.getProperty("apple.awt.rendering"));
				
			}

			else if(OSNativeUtils.isLinuxOrUnix()){
				try {
					// First try Nimbus
					boolean uiFound = false;
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						// First try Nimbus
						if ("Nimbus".equals(info.getName())) {
							UIManager.setLookAndFeel(info.getClassName());
							uiFound = true;
							break;
						}
					}
					if(! uiFound){
						for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
							if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {   
								javax.swing.UIManager.setLookAndFeel(info.getClassName());				       
								OSNativeUtils.installGtkPopupBugWorkaround();
								uiFound = true;
								break;
							}
						}
					}
					if(! uiFound){
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						uiFound = true;
						
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// debugUIDefaults();
			
			

			if(Settings.getUseCustomFontSize().getBooleanValue()){
				logger.info("user font size");
				float userSize = Settings.getCustomFontSize().getIntValue();
				Object obj = UIManager.getLookAndFeelDefaults().get("defaultFont");
				if(obj != null && obj instanceof Font){
					Font defaultFont = (Font) obj;
					UIManager.getLookAndFeelDefaults().put("defaultFont", defaultFont.deriveFont(userSize));
					
				}
				// and some more keys
				setUIFontSize(userSize);
			}

			aliView = new AliView();

			// Create Application Adapter (only needed for OsX and register this AliView as listener of Application events (interface below)
			if(OSNativeUtils.isMac()){
				Application macApplication = new DefaultApplication();
				macApplication.addApplicationListener(aliView);
				macApplication.addPreferencesMenuItem();
				macApplication.setEnabledPreferencesMenu(true);
			}

			// get alignment fileName as first argument to program
			File alignmentFile = null;
			if(args != null && args.length >= 1){	
				alignmentFile = new File(args[0]);
			}

			// if alignmentfile is invalid set it to null
			if(alignmentFile != null && alignmentFile.exists() == false){
				alignmentFile = null;
			}

			
			if(AliView.isDebugMode() && alignmentFile == null){
		//		alignmentFile = new File("/home/anders/projekt/ormbunkar/analys/sekv_analysis/aligned-WoodsiatrnGR-mafft.fasta.nexus");
				//File alignmentFile = new File("/home/anders/projekt/ormbunkar/analys/test_seqconcat/test_seqconcat.nexus");
				//File alignmentFile = new File("/home/anders/projekt/ormbunkar/cytotree/sequences/rbcl_all_ferns.tiny.xml.fasta");
				//alignmentFile = new File("/home/anders/projekt/sequences/anjas_cpDNA_con09123cut.selection_high.fasta");
				//File alignmentFile = new File("/home/anders/projekt/ormbunkar/fernloci/carl_genbank_submit/tplate_r2_fixed.nex");
				//File alignmentFile = new File("/home/anders/projekt/alignments/carl_protein_test/loci/concat.nexus.reopened.");
				//File alignmentFile = new File("/home/anders/projekt/ormbunkar/analys/seqconcat_test/seqconcat_test.nexus");
				//File alignmentFile = new File("/home/anders/projekt/virusfylogeni/JennyHesson/20131010_ed_virus.nexus");
				//File alignmentFile = new File("/home/anders/projekt/virusfylogeni/JennyHesson/analys/mb/20130308_virus.nexus");

				//alignmentFile = new File("/home/anders/projekt/ormbunkar/sekvenser_output/forkade_alignments/Woodsia_chloroplast_min1_20131101_v2.nexus");
				//alignmentFile = new File("/home/anders/projekt/ormbunkar/analys/sekv_analysis/aligned-WoodsiapgiC-mafft.fasta");
				//		alignmentFile = new File("/home/anders/projekt/alignments/carl_protein_test/concat.nexus.phy.translated.phy");
				//alignmentFile = new File("/home/anders/projekt/alignments/sample_of_SSURef_108_full_align_tax_silva_trunc_larger.fasta");
	//			alignmentFile = new File("/home/anders/projekt/alignments/WoodsiapgiC-forked_2.nexus");
				//		alignmentFile = new File("/home/anders/projekt/alignments/SMALL-FLAVI-v7-dating.nuc.aed.ALL.protfnuc.mafft.glob.cod.seav.fasta");
				//alignmentFile = new File("/home/anders/projekt/ormbunkar/fernloci/alignments_work/refined/6928/6928_all_w_arabid.27.fasta");
				//alignmentFile = new File("/home/anders/projekt/ormbunkar/analys/forked_pgiC_indel/WoodsiapgiC-forked_1_indels.only_seqs.nexus");
				//			alignmentFile = new File("/home/anders/projekt/alignments/ssu_pr2-99.fasta");
				//		alignmentFile = new File("/home/anders/projekt/alignments/sample_of_SSURef_108_full_align_tax_silva_trunc_larger.fasta");
			//			alignmentFile = new File("/vol2/big_data/SSURef_108_filtered_bacteria_pos_5389-24317.fasta");
				
				//alignmentFile = new File("/vol2/big_data/test.fasta");
				
				
	//			alignmentFile = new File("/home/anders/projekt/alignments/sandies/euArc36C_F1_big_problematic.nex");
			//	alignmentFile = new File("/home/anders/projekt/alignments/sandies/euArc165F_F1.fasta");
			//	alignmentFile = new File("/home/anders/projekt/alignments/Woodsia_chloroplast_min4_20131109_v2.excluded.nexus");
				
	//			alignmentFile = new File("/home/anders/projekt/alignments/Woodsia_chloroplast_min1_20131029.nexus");
			//	alignmentFile = new File("/vol2/big_data/test.nexus");
				
			//			  alignmentFile = new File("/vol2/johns_454/SSURef_108_full_align_tax_silva_trunc.fasta");
			//		  alignmentFile = new File("/vol2/big_data/SSURef_108_full_align_tax_silva_trunc.selection.fasta");
				//	alignmentFile = new File("/home/anders/projekt/alignments/sample_of_SSURef_108_full_align_tax_silva_trunc.fasta");

				//	alignmentFile = new File("/home/anders/projekt/alignments/Silva_108_core_aligned_seqs.fasta");
				//			alignmentFile = new File("/home/anders/projekt/alignments/woodsia_chloropl_excl_hybrid.selection.fasta");
		//			alignmentFile = new File("/home/anders/projekt/alignments/harris_CT144.phy");
	//			alignmentFile = new File("/home/anders/projekt/alignments/harris_CT144_seq_v2.phy");
		//		alignmentFile = new File("/home/anders/projekt/alignments/harris_CT144_seq_v2_shortname_sequential.phy");
			//	alignmentFile = new File("/vol2/big_data/small_test_shortname.phy");
//			alignmentFile = new File("/vol2/big_data/test.phy");
	//			alignmentFile = new File("/vol2/big_data/test.fasta");
				
			//			alignmentFile = new File("/home/anders/projekt/alignments/smalphylipSeqShortName.phy");
			//			alignmentFile = new File("/home/anders/projekt/alignments/smalphylipInterlLongName.phy");
				//alignmentFile = new File("/home/anders/projekt/alignments/smalphylipInterlShortName.phy");
//				alignmentFile = new File("/home/anders/projekt/alignments/harris_CT144.phy");

				alignmentFile = new File("/home/anders/projekt/alignments/gold_strains_gg16S_aligned.fasta");
	//					 alignmentFile = new File("/home/anders/projekt/alignments/woodsia_chloropl_excl_hybrid.fasta");
				//			alignmentFile = new File("/home/anders/projekt/henriks_laboul/both.fasta");
				//		alignmentFile = new File("/home/anders/projekt/ormbunkar/sekvenser_output/forkade_alignments/WoodsiapgiC-forked_2.nexus");
				//			alignmentFile = new File("/home/anders/projekt/ormbunkar/sekvenser_output/forkade_alignments/Woodsia_chloroplast_min1_20131101_v2.nexus.excluded");
				//	alignmentFile = new File("/home/anders/projekt/ormbunkar/analys/test_protein_alignment.fasta");
				//alignmentFile = new File("/home/anders/projekt/ormbunkar/analys/phylip.example.phy");
				//alignmentFile = new File("/home/anders/projekt/ormbunkar/analys/pgiC_20121215/pgiC_20121215.phylip");
				//alignmentFile = new File("/home/anders/tmp/Purple_ITScut2.nexus");
				//alignmentFile = new File("/home/anders/projekt/ormbunkar/fernloci/carls_example/locus_alignments_transcriptome/4321_transcriptome_alignments_etc/4321_v2.2_allin.nex");
				//alignmentFile = new File("/opt/Silva_108/core_aligned/Silva_108_core_aligned_seqs.fasta");
				
				//alignmentFile = new File("/home/anders/projekt/alignments/MSF_format.example.msf");
				//alignmentFile = new File("/home/anders/projekt/alignments/clustal_wrong2.aln");
			//	alignmentFile = new File("/home/anders/projekt/alignments/Woodsia_chloroplast_min4_20131109_v2.excluded.msf");
		//		alignmentFile = new File("/home/anders/projekt/alignments/infile_V2.phy");
	//			alignmentFile = new File("/home/anders/projekt/alignments/smalphylipInterlLongName.phy");
				
				
				
				if(! alignmentFile.exists()){
					alignmentFile = null;
				}
			}

			// only open file in Mac if there is a file name argument
			// - otherwise open is handeled in handleOpenFile()
			// but if --args is passed on command line in mac - this is were to pick them up
			if(OSNativeUtils.isMac() && alignmentFile != null){
				savedInitialArgumentAlignmentFileForMac = alignmentFile;
				// application AliView.createNewAliViewWindow will be called automatic in MacOS
				// via method handle open application
			}
			// for all non mac systems start here
			else if(! OSNativeUtils.isMac()){
				AliView.createNewAliViewWindow(alignmentFile);
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}catch(Error err){
			logger.info("error was caught");
			err.printStackTrace();
		}


		// set debug mode
		if(isDebugMode()){
			Logger.getRootLogger().setLevel(Level.ALL);
		}
		else{
			Logger.getRootLogger().setLevel(Level.ERROR);
		}

		logger.info("done with main method");

	}
	
	private static void debugUIDefaults(){
		UIDefaults def = UIManager.getLookAndFeelDefaults();
		Enumeration enumer = def.keys();
		while (enumer.hasMoreElements()) {
			Object item = enumer.nextElement();
			System.out.println(item +" " + def.get(item));
		}
	}

	private static void setDebug(boolean b) {
		debugMode = b;
	}
	
	public static boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * Create the application.
	 */
	public AliView() {
	}


	public static void openAlignmentFileViaChooser(Component parent) {

		// As default get last used stored directory
		String suggestedDir = Settings.getLoadAlignmentDirectory();
		File suggestedFile = new File(suggestedDir);
		File selectedFile = FileUtilities.selectOpenFileViaChooser(suggestedFile,parent);

		if(selectedFile != null){	
			openAlignmentFile(selectedFile);		
			Settings.putLoadAlignmentDirectory(selectedFile.getParent());
		}
	}
	
	private static boolean souldBreakBecauseOfLowMemory(File alignmentFile){
		double fileSize = alignmentFile.length();
		double fileSizeMB = fileSize / (1000 * 1000);			
		double presumableFreeMemory = MemoryUtils.getPresumableFreeMemoryMB();
		
		MemoryUtils.logMem();
		logger.info("getPresumableFreeMemory()=" + MemoryUtils.getPresumableFreeMemoryMB());
		logger.info("fileSizeMB=" + fileSizeMB);
		
		boolean isBreakBecauseOfLowMemory = false;
		if(presumableFreeMemory < 1.5 * fileSizeMB){			
			// ask user whether to continue or not
			String message="Memory is running low, if you open this Alignment before closing some" + LF + 
					       "other Alignments the program might run out of Memory." + LF +
					       "" + LF +
					       "Do you want to continue and open the new Alignment?";
			int retVal = JOptionPane.showConfirmDialog(DialogUtils.getDialogParent(), message, "Continue?", JOptionPane.YES_NO_CANCEL_OPTION);
			// return if not OK
			if(retVal != JOptionPane.OK_OPTION){
				isBreakBecauseOfLowMemory = true;
			}
		}
		return isBreakBecauseOfLowMemory;
	}
	


	public static void openAlignmentFile(File alignmentFile){	
		try{
			// if it is empty load file in old window - otherwise create new window
			logger.info("activeWindow=" + activeWindow);
			logger.info("activeWindow.isEmpty()" + activeWindow.isEmpty());
			
			if(hasNonEmptyWindows()){
				if(souldBreakBecauseOfLowMemory(alignmentFile)){
					return;
				}
			}
			
			if(activeWindow != null && activeWindow.isEmpty()){

				activeWindow.loadNewAlignmentFile(alignmentFile);
			}else{
				createNewAliViewWindow(alignmentFile);
			}
			Settings.putLoadAlignmentDirectory(alignmentFile.getAbsoluteFile().getParent());
			Settings.addRecentFile(alignmentFile);
		
		} catch (Exception e) {
			e.printStackTrace();
		}catch(OutOfMemoryError memoryErr){
			logger.info("memory err");
			memoryErr.printStackTrace();
			Messenger.showOKOnlyMessage(Messenger.OUT_OF_MEMORY_ERROR, activeWindow);
		}catch(Error err){
			err.printStackTrace();
		}
	}

	private static boolean hasNonEmptyWindows() {
		boolean hasNonEmptyWin = false;
		for(AliViewWindow aliWin: aliViewWindows){
			if(aliWin != null && !aliWin.isEmpty()){
				hasNonEmptyWin = true;
			}
		}
		return hasNonEmptyWin;
	}

	public static void createNewWindow() {
		logger.info("new win");
		createNewAliViewWindow(null);
	}
	
	public static JFrame getActiveWindow(){
		return activeWindow;
	}

	private static void createNewAliViewWindow(final File alignmentFile){
		
		// If memory is low ask user first and break if wanted
//		if(aliViewWindows.size() > 0){
//			if(alignmentFile != null && !activeWindow.isEmpty()){			
//				if(souldBreakBecauseOfLowMemory(alignmentFile)){
//					return;
//				}		
//			}			
//		}
			
		try {

			AliViewWindow newWin = new AliViewWindow(alignmentFile,menuBarFactory);

			newWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			newWin.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

			// create and add windowlisteners
			newWin.addWindowListener(new WindowAdapter() {

				public void windowActivated(java.awt.event.WindowEvent e) {
					AliViewWindow thisWin = (AliViewWindow) e.getWindow();
					activeWindow = thisWin;
					logger.info("window activated");
				}

				public void windowClosing(WindowEvent e) {
					AliViewWindow thisWin = (AliViewWindow) e.getWindow();

					boolean isCloseOK = thisWin.requestWindowClose();

					if(isCloseOK){
						thisWin.dispose();
						aliViewWindows.remove(thisWin);
						// if this was last then quit
						if(aliViewWindows.size() == 0){
							aliView.quitProgram();
						}

					}
					else{
						// do nothing
					}
				}

			}); // end WindowAdapter

			// if there is another active window save that geom first
			if(activeWindow != null){
				activeWindow.saveWindowGeometry();
			}
			aliViewWindows.add(newWin);
			activeWindow = newWin;	
			newWin.restoreWindowGeometry();

			// open window a little bit offset if there is one already
			if(aliViewWindows.size()>1){
				newWin.setLocation(newWin.getX() + 20, newWin.getY() + 10);
			}

			// adjust window so it is not outside desktop
			placeWithinDesktop(newWin);

			newWin.setVisible(true);
			newWin.toFront();

		} catch (Exception e) {
			e.printStackTrace();
		}catch(OutOfMemoryError memoryErr){
			logger.info("memory err");
		//	memoryErr.printStackTrace();
			Messenger.showOKOnlyMessage(Messenger.OUT_OF_MEMORY_ERROR, activeWindow);
		}catch(Error err){
			err.printStackTrace();
		}
		//			}
		//		});	

	}
	
	public static void setUIFontSize (float newSize){
	    Enumeration<Object> keys = UIManager.getLookAndFeelDefaults().keys();
	    while (keys.hasMoreElements()) {
	      Object key = keys.nextElement();
	      Object value = UIManager.get (key);
	      if (value != null && value instanceof Font){
	    //	logger.info(key + " " + value);
	    	Font derivFont = ((Font)value).deriveFont(newSize);
	    	FontUIResource fontRes = new FontUIResource(derivFont);
	    //	logger.info(key + " " + fontRes);
	    	UIManager.getLookAndFeelDefaults().put (key, fontRes);
	      }
	    } 
	}
	
	private static void placeWithinDesktop(AliViewWindow newWin) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Rectangle screen = new Rectangle(screenSize);
		Rectangle intersection = screen.intersection(newWin.getBounds());

		// make sure size is something
		if(intersection.getWidth() < 100 || intersection.getHeight() < 100){
			newWin.setBounds(0,0,200,200);
		}else{
			newWin.setBounds(intersection);
		}

	}

	public static void quitProgram() {

		// close all windows (ask if it is OK)
		// reverse order since that is order you windows opened
		for(int n = aliViewWindows.size() - 1; n >= 0; n--){
			AliViewWindow window = aliViewWindows.get(n);		
			boolean isCloseOK = window.requestWindowClose();
			if(isCloseOK){
				window.dispose();
				aliViewWindows.remove(window);
			}
			else{
				// Not OK stop operation
				return;
			}
		}

		logger.info("System exit");
		System.exit(0);
	}

	public static void logAllLogs(){
		Enumeration enumer = Logger.getRootLogger().getAllAppenders();
		while ( enumer.hasMoreElements() ){
			Appender app = (Appender)enumer.nextElement();
			if ( app instanceof FileAppender ){
				System.out.println("File: " + ((FileAppender)app).getFile());
			}
		}
	}

	/*
	 * 
	 * Mac OSX Application Listener
	 * 
	 */

	public void handleAbout(ApplicationEvent event) {
		// this is handled with a standard window in Mac - version is specified in application-jar
		logger.info("inside handle About");
		// TODO Auto-generated method stub

	}

	public void handleOpenApplication(ApplicationEvent event) {
		event.setHandled(true);
		// Is called first time application start
		logger.info("inside handle open application");

		// check if file arguments this is if argument is passed to Mac on command line and not in Finder "open file with..." if
		// open from finder or dropped, file name is passed with a call to handleOpenFile (or in the file dropped handler)
		if(savedInitialArgumentAlignmentFileForMac != null){
			AliView.createNewAliViewWindow(savedInitialArgumentAlignmentFileForMac);
		}else{
			AliView.createNewAliViewWindow(null);	
		}
	}

	public void handleOpenFile(ApplicationEvent event) {
		event.setHandled(true);
		// TODO create new AliView if program
		logger.info("inside handle open file" + event.getSource());
		String fileName = event.getFilename();
		logger.info("fileName" + fileName);
		openAlignmentFile(new File(fileName));
	}

	public void handlePreferences(ApplicationEvent event) {
		activeWindow.openPreferencesGeneral();
	}

	public void handlePrintFile(ApplicationEvent event) {
		//		String message = "No print option - Instead choose Export as Image" + LF + "and then print from Image-program";
		//		JOptionPane.showConfirmDialog(activeWindow, message, "Print", JOptionPane.OK_OPTION);
		activeWindow.printAlignment();
	}

	public void handleQuit(ApplicationEvent event) {
		logger.info("inside handle quit");
		event.setHandled(true);
		quitProgram();
	}

	public void handleReOpenApplication(ApplicationEvent event) {
		// Is handled when files are dropped
		logger.info("inside handle RE-open application");
	}

	/*
	 * 
	 * End Mac OSX Application Listener
	 * 
	 */


	public static String getVersion() {
		Properties versionProp = new Properties(); 
		InputStream in = AliView.class.getResourceAsStream("/version.properties");
		String version = "";
		try {
			versionProp.load(in);
			version = (String)versionProp.get("version");

		} catch (Exception exc) {
			logger.error("error loading version config");
			exc.printStackTrace();
			version = "?";
		}	
		return version;
	}

	public static long getTime(Class<?> cl) {
		try {
			String rn = cl.getName().replace('.', '/') + ".class";
			JarURLConnection j = (JarURLConnection) ClassLoader.getSystemResource(rn).openConnection();
			return j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime();
		} catch (Exception e) {
			return 0;
		}
	}
	
}




