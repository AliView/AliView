package aliview.externalcommands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.aligner.Aligner;
import aliview.aligner.MuscleWrapper;
import aliview.subprocesses.SubProcessWindow;

public class ExternalCommandExecutor {

	private static final Logger logger = Logger.getLogger(ExternalCommandExecutor.class);
	private static final String LF = System.getProperty("line.separator");


	public static boolean executeMultiple(CommandItem cmdItem, SubProcessWindow subProcessWin) throws IOException {
		// TODO Auto-generated method stub

		boolean wasProcessInterrupted = false;
		for(String[] commandLine: cmdItem.getParsedCommands()){
			wasProcessInterrupted = executeCommand(commandLine, subProcessWin);
			if(subProcessWin.wasSubProcessDestrouedByUser()){
				wasProcessInterrupted = true;
				break;
			}
		}
		return wasProcessInterrupted;
	}

	private static boolean executeCommand(String[] commandArray, final SubProcessWindow subProcessWin) throws IOException{

		boolean wasProcessInterrupted = false;

		// check for piped output in command
		File pipedOut = null;
		for(int i = 0; i < commandArray.length; i++){
			if(commandArray[i].equals(">")){
				logger.info("found pipe");
				if(commandArray.length >= i+1){
					pipedOut = new File(commandArray[i+1]);
					commandArray = (String[]) ArrayUtils.subarray(commandArray, 0, i);
					break;
				}
			}
		}

		String cmdAsString ="";
		for(String part: commandArray){
			cmdAsString += " " + part;
		}
		logger.info(cmdAsString);
		
		ProcessBuilder probuilder = new ProcessBuilder( commandArray );
		
		probuilder.redirectErrorStream(true);
		
		Process subprocess = probuilder.start();
			
		// so that process gets killed when window destroys
		subProcessWin.setActiveProcess(subprocess);
		
		subProcessWin.appendOutput("command:" + LF + cmdAsString + LF);

		final Scanner sc = new Scanner(subprocess.getInputStream());
	//	final Scanner errorSc = new Scanner(subprocess.getErrorStream());

//		
//		final File finalPipedOut = pipedOut;
//		
//		// read output in separate thread so nothing gets blocked
//		Thread pipedOutThread = new Thread(new Runnable(){
//			public void run(){
//			
//			// if pipped out then read output into file
//			if(finalPipedOut != null){
//				try {
//					logger.info("piped out");
//					BufferedWriter buffOut = new BufferedWriter(new FileWriter(finalPipedOut));	
//					while (sc.hasNext()){
//						logger.info("beforeNextLine");
//						String nextLine = sc.nextLine();
//						logger.info("afterNextLine");
//						buffOut.append(nextLine + LF);
//						//logger.info((nextLine));
//					}
//					sc.close();
//					buffOut.close();
//					logger.info(("done with piped out"));
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			// else just dump output to screen
//			else{
//				try {
//					logger.info(("going into scanner loop"));
//					while (sc.hasNext()){
//						logger.info("wait for");
//						String nextLine = sc.nextLine();
//						subProcessWin.appendOutput(nextLine + LF);
//						logger.info((nextLine));
//					}
//					sc.close();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			logger.info("outthreaddone");
//			}
//		});
//		pipedOutThread.start();
//		
		while (sc.hasNext()){
			//logger.info(errorSc.nextLine());
			String nextLine = sc.nextLine();
			subProcessWin.appendOutput(nextLine + LF);
			System.out.println(nextLine);
		}
		sc.close();
		logger.info("errorReaderThread-finished");
		logger.info("before wait for subprocess");
		
		// kill output thread if this was interrupted
		
		
		// Dont close streams - it might block on windows		
		//		if(subProcessWin.wasSubProcessDestrouedByUser()){
		//			subprocess.getInputStream().close();
		//			subprocess.getOutputStream().close();
		//		}
		
		// close streams
		
		
		// clean up external process
		/*
		try {
			subprocess.waitFor();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		subprocess.destroy();

		logger.info("done");

		return wasProcessInterrupted;

	}
	
	/*
	 private static boolean executeCommand(String[] commandArray, final SubProcessWindow subProcessWin) throws IOException{

		boolean wasProcessInterrupted = false;

		// check for piped output in command
		File pipedOut = null;
		for(int i = 0; i < commandArray.length; i++){
			if(commandArray[i].equals(">")){
				logger.info("found pipe");
				if(commandArray.length >= i+1){
					pipedOut = new File(commandArray[i+1]);
					commandArray = (String[]) ArrayUtils.subarray(commandArray, 0, i);
					break;
				}
			}
		}

		String cmdAsString ="";
		for(String part: commandArray){
			cmdAsString += " " + part;
		}
		logger.info(cmdAsString);
		
		ProcessBuilder probuilder = new ProcessBuilder( commandArray );
		Process subprocess = probuilder.start();

				
		// so that process gets killed when window destroys
		subProcessWin.setActiveProcess(subprocess);


		final Scanner sc = new Scanner(subprocess.getInputStream());
		final Scanner errorSc = new Scanner(subprocess.getErrorStream());

		
		final File finalPipedOut = pipedOut;
		
		// read output in separate thread so nothing gets blocked
		Thread pipedOutThread = new Thread(new Runnable(){
			public void run(){
			
			// if pipped out then read output into file
			if(finalPipedOut != null){
				try {
					logger.info("piped out");
					BufferedWriter buffOut = new BufferedWriter(new FileWriter(finalPipedOut));	
					while (sc.hasNext()){
						logger.info("beforeNextLine");
						String nextLine = sc.nextLine();
						logger.info("afterNextLine");
						buffOut.append(nextLine + LF);
						//logger.info((nextLine));
					}
					sc.close();
					buffOut.close();
					logger.info(("done with piped out"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// else just dump output to screen
			else{
				try {
					logger.info(("going into scanner loop"));
					while (sc.hasNext()){
						logger.info("wait for");
						String nextLine = sc.nextLine();
						subProcessWin.appendOutput(nextLine + LF);
						logger.info((nextLine));
					}
					sc.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.info("outthreaddone");
			}
		});
		pipedOutThread.start();
		
		while (errorSc.hasNext()){
			//logger.info(errorSc.nextLine());
			String nextLine = errorSc.nextLine();
			subProcessWin.appendOutput(nextLine + LF);
			System.out.println(nextLine);
		}
		errorSc.close();
		logger.info("errorReaderThread-finished");
		logger.info("before wait for subprocess");
		
		// kill output thread if this was interrupted
		if(subProcessWin.wasSubProcessDestrouedByUser()){
			pipedOutThread.interrupt();
			subprocess.getInputStream().close();
			subprocess.getOutputStream().close();
		}
		
		// clean up external process
		try {
			subprocess.waitFor();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		subprocess.destroy();

		logger.info("done");


		return wasProcessInterrupted;

	}
	*/

}
