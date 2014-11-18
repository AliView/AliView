package utils;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import aliview.AliView;

import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.PrintFilesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.PrintFilesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

/*
 * This class are keeping the com.apple.eawt isolated from rest of program and is instantiated and called
 * through reflection so other platforms does not get runtime error by classes only residing in Mac JRE
 * the com.apple.eawt are only stubs in this project (in orange-extensions.jar) and scope is "provided" in maven
 * This way it compiles on all platforms but only used on Mac where the jar is provided by JRE
 */
public class MacAdapter implements OpenFilesHandler, PreferencesHandler, QuitHandler, PrintFilesHandler{
	private static final Logger logger = Logger.getLogger(MacAdapter.class);

	private MacAdapter(AliView aliview) {
	    Application.getApplication().setOpenFileHandler(this);
	    Application.getApplication().setPrintFileHandler(this);
	    Application.getApplication().setQuitHandler(this);
	    Application.getApplication().setPreferencesHandler(this);
	}
	
	public static void registerApplication(AliView aliView){
		new MacAdapter(aliView);
	}

	/*
	 * (non-Javadoc)
	 * @see com.apple.eawt.OpenFilesHandler#openFiles(com.apple.eawt.AppEvent.OpenFilesEvent)
	 */
	public void openFiles(OpenFilesEvent event) {
	   logger.info("openFiles");
	   List<File> files = event.getFiles();
	   logger.info("files" + files);
	   if(files != null && files.size() > 0){
		   logger.info("files.size()" + files.size());
		   logger.info("files.get(0).getAbsolutePath()" + files.get(0).getAbsolutePath());
		   File firstFile=new File(files.get(0).getAbsolutePath());
		   AliView.openAlignmentFile(firstFile);
	   }
	}

	/*
	 * (non-Javadoc)
	 * @see com.apple.eawt.PrintFilesHandler#printFiles(com.apple.eawt.AppEvent.PrintFilesEvent)
	 */
	public void printFiles(PrintFilesEvent e) {
		AliView.doMacPrintFile();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.apple.eawt.QuitHandler#handleQuitRequestWith(com.apple.eawt.AppEvent.QuitEvent, com.apple.eawt.QuitResponse)
	 */
	public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
		AliView.doMacQuit();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.apple.eawt.PreferencesHandler#handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent)
	 */
	public void handlePreferences(PreferencesEvent e) {
		AliView.doMacPreferences();
	}
	
	
}