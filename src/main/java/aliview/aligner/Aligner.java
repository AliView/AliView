package aliview.aligner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.log4j.Logger;

import aliview.subprocesses.SubProcessWindow;


public class Aligner {
	private static final Logger logger = Logger.getLogger(Aligner.class);
	private static final String LF = System.getProperty("line.separator");


	public static void muscleProfileAlign(File in1, File in2, File out){
		muscleProfileAlign(in1, in2, out, null);
	}

	public static void muscleProfileAlign(File in1, File in2, File out, SubProcessWindow subProcessWin){

		boolean wasProcessInterrupted = false;

		try {

			String[] commandArray = new String[]{
					MuscleWrapper.getMusclePath().toString(),
					"-profile",
					"-in1",in1.toString(),
					"-in2",in2.toString(),
					"-out",out.toString()
			};


			for(String token: commandArray){
				logger.info(token);
			}

			String totalCommand = "";
			for(String token: commandArray){
				totalCommand += token + " ";
			}
			logger.info(totalCommand);

			//			String command = "muscle -in " + in.getAbsolutePath() + " -out " + out.getAbsolutePath();		
			//			ProcessBuilder builder = new ProcessBuilder(command);
			//			builder.redirectErrorStream(true);
			//			Process p = builder.start();


			Process subprocess = Runtime.getRuntime().exec(commandArray);

			// so that process gets killed when window destroys
			subProcessWin.setActiveProcess(subprocess);


			Scanner sc = new Scanner(subprocess.getInputStream());
			Scanner errorSc = new Scanner(subprocess.getErrorStream());

			// First read errors (but everything is written in error stream so use ordinary info-logger)
			//			// with the redirect above we dont need to check error - everything is sent to standard

			while (errorSc.hasNext()){
				//logger.info(errorSc.nextLine());
				String nextLine = errorSc.nextLine();
				subProcessWin.appendOutput(nextLine + LF);
				System.out.println(nextLine);
			}

			while (sc.hasNext()){
				//logger.info(sc.nextLine());
				String nextLine = sc.nextLine();
				subProcessWin.appendOutput(nextLine + LF);
				System.out.println(nextLine);
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


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void mafftAlign(File in, File out, final SubProcessWindow subProcessWin) throws IOException {

		boolean wasProcessInterrupted = false;


		String[] commandArray = new String[]{
				"mafft",
				"--localpair",
				"--maxiterate","1000",
				in.toString()
		};

		ProcessBuilder probuilder = new ProcessBuilder( commandArray );


		for(String token: commandArray){
			logger.info(token);
		}

		String totalCommand = "";
		for(String token: commandArray){
			totalCommand += token + " ";
		}
		logger.info(totalCommand);

		//		String command = "muscle -in " + in.getAbsolutePath() + " -out " + out.getAbsolutePath();		
		//		ProcessBuilder builder = new ProcessBuilder(command);
		//		builder.redirectErrorStream(true);
		//		Process p = builder.start();

		//probuilder.redirectErrorStream(true);

		Process subprocess = probuilder.start();

		// so that process gets killed when window destroys
		subProcessWin.setActiveProcess(subprocess);

		//		
		//		BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
		//		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));
		//		
		//		
		//		
		//		while (scan.hasNext()) {
		//		    String input = scan.nextLine();
		//		    if (input.trim().equals("exit")) {
		//		        // Putting 'exit' amongst the echo --EOF--s below doesn't work.
		//		        writer.write("exit\n");
		//		    } else {
		//		        writer.write("((" + input + ") && echo --EOF--) || echo --EOF--\n");
		//		    }
		//		    writer.flush();
		//
		//		    String line = reader.readLine();
		//		    while (line != null && ! line.trim().equals("--EOF--")) {
		//		        System.out.println ("Stdout: " + line);
		//		        line = reader.readLine();
		//		    }
		//		    if (line == null) {
		//		        break;
		//		    }
		//		}



		Scanner sc = new Scanner(subprocess.getInputStream());
		final Scanner errorSc = new Scanner(subprocess.getErrorStream());

		// First read errors (but everything is written in error stream so use ordinary info-logger)
		// with the redirect above we dont need to check error - everything is sent to standard


		Thread errorReaderThread = new Thread(new Runnable(){
			public void run(){
				while (errorSc.hasNext()){
					//logger.info(errorSc.nextLine());
					String nextLine = errorSc.nextLine();
					subProcessWin.appendOutput(nextLine + LF);
					System.out.println(nextLine);
				}
				logger.info("errorReaderThread-finished");

			}
		});
		errorReaderThread.start();

		BufferedWriter buffOut = new BufferedWriter(new FileWriter(out));

		while (sc.hasNext()){
			//logger.info(sc.nextLine());
			String nextLine = sc.nextLine();
			buffOut.append(nextLine + LF);
			//subProcessWin.appendOutput(nextLine + LF);
			//System.out.println(nextLine);
		}
		sc.close();
		buffOut.close();

		// clean up external process
		try {
			subprocess.waitFor();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		subprocess.destroy();

		logger.info("done");


	}


	public static void muscleAlign(File in, File out, SubProcessWindow subProcessWin){

		try {
			// 
			String[] commandArray = new String[]{
					MuscleWrapper.getMusclePath().toString(), // "muscle"
					"-in",in.toString(),
					"-out",out.toString()
			};


			for(String token: commandArray){
				logger.info(token);
			}

			String totalCommand = "";
			for(String token: commandArray){
				totalCommand += token + " ";
			}
			logger.info(totalCommand);

			//			String command = "muscle -in " + in.getAbsolutePath() + " -out " + out.getAbsolutePath();		
			//			ProcessBuilder builder = new ProcessBuilder(command);
			//			builder.redirectErrorStream(true);
			//			Process p = builder.start();


			Process subprocess = Runtime.getRuntime().exec(commandArray);

			// so that process gets killed when window destroys
			subProcessWin.setActiveProcess(subprocess);


			Scanner sc = new Scanner(subprocess.getInputStream());
			Scanner errorSc = new Scanner(subprocess.getErrorStream());

			// First read errors (but everything is written in error stream so use ordinary info-logger)
			//			// with the redirect above we dont need to check error - everything is sent to standard

			while (errorSc.hasNext()){
				//logger.info(errorSc.nextLine());
				String nextLine = errorSc.nextLine();
				subProcessWin.appendOutput(nextLine + LF);
				System.err.println(nextLine);
			}

			while (sc.hasNext()){
				//logger.info(sc.nextLine());
				String nextLine = sc.nextLine();
				subProcessWin.appendOutput(nextLine + LF);
				System.out.println(nextLine);
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


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*

	public static void mafftAlign(File in, File out){
		try {

			// 
			String[] commandArray = new String[]{
					"mafft",
					"--maxiterate", "1000",
					"--localpair", in.toString(),
					">",out.toString()
			};


			for(String token: commandArray){
				logger.info(token);
			}

			String totalCommand = "";
			for(String token: commandArray){
				totalCommand += token + " ";
			}
				logger.info(totalCommand);

//			String command = "muscle -in " + in.getAbsolutePath() + " -out " + out.getAbsolutePath();		
//			ProcessBuilder builder = new ProcessBuilder(command);
//			builder.redirectErrorStream(true);
//			Process p = builder.start();

			Process p = Runtime.getRuntime().exec(totalCommand);



			Scanner sc = new Scanner(p.getInputStream());
			Scanner errorSc = new Scanner(p.getErrorStream());

			// First read errors (but everything is written in error stream so use ordinary info-logger)
//			// with the redirect above we dont need to check error - everything is sent to standard
			while (errorSc.hasNext()){
				//logger.info(errorSc.nextLine());
				System.out.println(errorSc.nextLine());
			}

			while (sc.hasNext()){
				//logger.info(sc.nextLine());
				System.out.println(sc.nextLine());
			}

			// clean up external process
			try {
				p.waitFor();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			p.destroy();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 */

	class AlignResult{
		private boolean wasDestroyedByUser;

		protected AlignResult() {
		}

		protected AlignResult(boolean wasDestroyedByUser) {
			this.wasDestroyedByUser = wasDestroyedByUser;
		}

		public boolean wasDestroyedByUser() {
			return wasDestroyedByUser;
		}

		public boolean isWasDestroyedByUser() {
			return wasDestroyedByUser;
		}

		public void setWasDestroyedByUser(boolean wasDestroyedByUser) {
			this.wasDestroyedByUser = wasDestroyedByUser;
		}

	}

}
