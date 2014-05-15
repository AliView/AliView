package aliview;

import java.awt.Component;
import java.awt.Dialog;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import utils.DialogUtils;
import utils.OSNativeUtils;

import aliview.messenges.Messenger;
import aliview.settings.Settings;


public class HelpUtils {
	private static final Logger logger = Logger.getLogger(HelpUtils.class);
	public static final String EXTERNAL_COMMANDS = "EXTERNAL_COMMANDS";
	public static final String ALIGNER_SETTINGS_ALL = "ALIGNER_SETTINS_ALL";
	public static final String ALIGNER_SETTINGS_ADD = "ALIGNER_SETTINS_ADD";
	public static final String General_settings = "General_settings";
	public static final String Find_Primer_settings = "Find_Primer_settings";
	public static final String TOP_HELP = "TOP_HELP";
	public static final String DOWNLOAD = "DOWNLOAD";
	
	protected static String baseURL = Settings.getAliViewHelpWebPage();
	protected static String localBaseURL = "file:///home/anders/maven/AliView/web/help/help.html";
	
	/*
	public static void display(String bookmark) {
		display(bookmark,DialogUtils.getDialogParent());
	}
	*/

	public static void display(String bookmark, JFrame parentFrame) {
		URL location = getURL(bookmark);
		
		try {
			openWebpage(location);
		} catch (Exception e) {
			// Nothing to do
			Messenger.showOKOnlyMessage(Messenger.COULD_NOT_OPEN_HELP_IN_BROWSER, parentFrame);
			
			e.printStackTrace();
		}
	}
	
	private static URL getURL(String bookmark) {
		
		String baseLocation = baseURL;
		String username = System.getenv("USERNAME");
		if(username != null && username.equals("anders-not-being-used")){
			baseLocation = localBaseURL;
		}
		
		URL location = null;
		try {
			location = new URL(baseLocation + "#" + bookmark);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return location;
	}

	public static void openWebpage(URL url) throws Exception {
		
		ArrayList<String> cmds = new ArrayList<String>();
		
		  if (OSNativeUtils.isMac())
		  {
		     cmds.add("open");
		     cmds.add(url.toString());
		  }
		  else if (OSNativeUtils.isWindows())
		  {
		     cmds.add("cmd.exe");
		     cmds.add("/c");
		     cmds.add("start");
		     cmds.add(url.toString());
		  }
		  else{
			  cmds.add("xdg-open");
			  cmds.add(url.toString());
		  }
		
		  ProcessBuilder builder = new ProcessBuilder(cmds);
		  
		  builder.start();

// 		I skipped Desktop.class since it is Java 1.6
// 		
//	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
//	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
//	        try {
//	            desktop.browse(uri);
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	    }
	    
	}

}
