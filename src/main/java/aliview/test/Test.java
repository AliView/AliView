package aliview.test;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;


import aliview.NucleotideUtilities;

public class Test implements Cloneable{
	private static final Logger logger = Logger.getLogger(Test.class);

	// Preference keys for this package
	private static final String NUM_ROWS = "num_rows";
	private static final String NUM_COLS = "num_cols";
	public int[] array = new int[]{12,13,19};
	
	private static byte[] testByte ="GTCACCGGCA".getBytes();
	private static String testString = "GTCACCGGCA";
	

	public static void main(String[] args) throws CloneNotSupportedException {
		
		
		//getAATextInfo(true);
		
		logger.info(8 % 3);
		
		testAlphaColor();
		
		//showMemStats();
		//testBitSet();
		Test t1 = new Test();
		Test tClone = (Test) t1.clone();
		t1.array[1] = 15;
		logger.info(tClone.array[1]);
		
		String[] splitted = "kjs   \t sdkj".split("\\s+");
		logger.info(splitted.length);
//		logger.info(System.getProperty("java.util.prefs.PreferencesFactory"));
//		
//		try {
//			buildTestFasta();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		logger.info("r="+getRed(-687486));
//		logger.info("g="+getGreen(-687486));
//		logger.info("b="+getBlue(-687486));
//		logger.info("a="+getAlpha(-687486));
//		
//		logger.info("r="+getRed(-9553093));
//		logger.info("g="+getGreen(-9553093));
//		logger.info("b="+getBlue(-9553093));
//		logger.info("a="+getAlpha(-9553093));
		
	}

	/*
	private static AATextInfo getAATextInfoFromMap(Map hints) {
	    
        Object aaHint   = hints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
        Object contHint = hints.get(RenderingHints.KEY_TEXT_LCD_CONTRAST);

        if (aaHint == null ||
            aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF ||
            aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
            return null;
        } else {
            return new AATextInfo(aaHint, (Integer)contHint);
        }
    }

    public static AATextInfo getAATextInfo(boolean lafCondition) {
        SunToolkit.setAAFontSettingsCondition(lafCondition);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Object map = tk.getDesktopProperty(SunToolkit.DESKTOPFONTHINTS);
        if (map instanceof Map) {
        	Map mapObj = (Map) map;
        	 logger.info(mapObj.size());
        	for (Map.Entry<Object, Object> entry : ((RenderingHints) map).entrySet()) {
        	    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        	}
        	
            return getAATextInfoFromMap((Map)map);
        } else {
        	logger.info("is null");
            return null;
        }
    }
    
    */
    
	
	
	private static void testAlphaColor() {
		Color test = new Color(1,1,10,0);
		logger.info("test.getAlpha()" + test.getAlpha());
	//	logger.info("getRGB" + getRGB(test.getRGB()));
		logger.info(test.getRGB());
		
		logger.info(addTranspGrey(-687486, 0.45));
		
	}
	public static int getGolorVal(int r, int g, int b, int a) {
        int rgba = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
        return rgba;
    }
	
	public static int addTranspGrey(int inVal, double transp){
		
		int a = 255;//getAlpha(inVal);
		int r = (int)(getRed(inVal) * transp);
		int g = (int)(getGreen(inVal) * transp);
		int b = (int)(getBlue(inVal) * transp);
		
        int rgba = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
        return rgba;
    }
	
	
	public static int getRed(int colVal) {
		return (colVal >> 16) & 0xFF;
	    }

	    /**
	     * Returns the green component in the range 0-255 in the default sRGB
	     * space.
	     * @return the green component.
	     * @see #getRGB
	     */
	    public static int getGreen(int colVal) {
		return (colVal >> 8) & 0xFF;
	    }

	    /**
	     * Returns the blue component in the range 0-255 in the default sRGB
	     * space.
	     * @return the blue component.
	     * @see #getRGB
	     */
	    public static int getBlue(int colVal) {
		return (colVal >> 0) & 0xFF;
	    }

	    /**
	     * Returns the alpha component in the range 0-255.
	     * @return the alpha component.
	     * @see #getRGB
	     */
	    public static int getAlpha(int colVal) {
	        return (colVal >> 24) & 0xff;
	    }
	
	public static void buildTestFasta() throws IOException{
		File fasta = new File("/vol2/big_data/test-wide.fasta");
		BufferedWriter out = new BufferedWriter(new FileWriter(fasta));
		
		for(int i = 0; i < 500; i++){
			out.write(">Testseq-" + i + "\n");
			for(int n = 0; n < 1000000; n++){
				out.write(testString);
			}
			out.write('\n');
			logger.info("done with seq" + i);
		}
		
	}
	


	public Test() {
		// TODO Auto-generated constructor stub
	}
	
	private static void testBitSet() {
		BitSet bitSelection = new BitSet();
		
		bitSelection.set(8);
		
		logger.info(bitSelection.length());
		
		logger.info(bitSelection.get(4));
		
		logger.info(bitSelection.length());
		
		logger.info(bitSelection.get(100));
		
		logger.info(bitSelection.length());
		
		bitSelection.set(100);
		
		logger.info(bitSelection.length());
		
		bitSelection.set(98, true);
		
		bitSelection.set(90, true);
		
		logger.info(bitSelection.length());
		
		bitSelection.set(100, false);
		
		logger.info(bitSelection.length());
	}

	private void testFileDialog(){

		Frame f = new Frame();
		FileDialog fd = new FileDialog(f, "Hej", FileDialog.LOAD);
		fd.setDirectory("/home/anders/_ormbunkar");
		fd.setLocation(100,100);
		fd.setSize(600, 600);
		fd.setFile("");
		fd.setVisible(true);
		File selectedFile = new File(fd.getFile());

	}

	private static void showMemStats(){

		int mb = 1024*1024;

		//Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();

		System.out.println("##### Heap utilization statistics [MB] #####");

		//Print used memory
		System.out.println("Used Memory:"
				+ (runtime.totalMemory() - runtime.freeMemory()) / mb);

		//Print free memory
		System.out.println("Free Memory:"
				+ runtime.freeMemory() / mb);

		//Print total available memory
		System.out.println("Total Memory:" + runtime.totalMemory() / mb);

		//Print Maximum available memory
		System.out.println("Max Memory:" + runtime.maxMemory() / mb);
	}



	private void testprefs(){
		Preferences prefs = Preferences.userNodeForPackage(Test.class);

		int numRows = prefs.getInt(NUM_ROWS, 40);
		int numCols = prefs.getInt(NUM_COLS, 80);
		logger.info(numRows);
		logger.info(numCols);
		prefs.putInt(NUM_ROWS, 30);
		prefs.put("Another", "hejHopp");
		prefs.put("Third", "tree");
		try {
			prefs.flush();
		} catch (BackingStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Thread.sleep(1000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void testBitwise(){

		int x = 3 | 1 | 4 | 1 | 6;
		System.out.println("" + x);

		System.out.println(5 | 2);


	}

	public static void runJavascript(){
		// create a script engine manager
		ScriptEngineManager factory = new ScriptEngineManager();
		// create a JavaScript engine
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		// evaluate JavaScript code from String
		try {
			engine.eval("print('Welocme to java world')");
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * @param args
	 */
	public static void testArray() {



		long startTime = System.nanoTime();

		// Kan läsa det allra mesta

		int size = 10000000;
		byte[] array = new byte[size];
		byte[] toarray = new byte[size];

		for(int n = 0; n < array.length; n++){
			toarray[n] = array[n];
		}

		long endTime = System.nanoTime();
		System.out.println("That took " + (endTime - startTime) + " nanoseconds");

		startTime = System.nanoTime();
		System.arraycopy(array, 0, toarray, 0, size);

		endTime = System.nanoTime();
		System.out.println("That took " + (endTime - startTime) + " nanoseconds");




		logger.info(4 % 3);

		logger.info(System.getProperty("java.io.tmpdir"));

		logger.info(Math.abs(2 % 3) + 1);

		int readingFrame = 3;
		int wantedFrame = 2;
		int startPos = 146;

		int offset = readingFrame - wantedFrame;

		int invertedOffset = 3 - offset;
		offset = invertedOffset % 3; // convert 3 to 0;

		startPos = startPos + offset;

		logger.info(startPos);


		//new Test().runJavascript();

		//new Test().testBitwise();

		//new Test().testFileDialog();

		//new Test().testprefs();

		// TODO Auto-generated method stub

		// 159 MB file into Stringbuffer = 2.1 sek
		// Only reading = 0.6 sek
		//INFO  2012-05-15 20:37:26,325 Test                  20 - Start
		//INFO  2012-05-15 20:37:26,327 Test                  24 - File length = 159095555
		//INFO  2012-05-15 20:37:28,419 Test                  38 - Finished


		//logger.info(Math.floor(2.99));




		//		
		//		long startTime = System.currentTimeMillis();
		//		
		//		// Kan läsa det allra mesta
		//		
		//		byte[][] x = new byte[10000][1000];
		//		
		//		File testFile = new File("/opt/Silva_108/core_aligned/Silva_108_core_aligned_seqs.fasta");
		//		//File testFile = new File("/opt/Silva_108/rep_set/Silva_108_rep_set.fna");
		//		//File testFile = new File("/opt/Transcriptomefiles/Asplenium_nidus_PSKY/PSKY-assembly.fa");
		//		logger.info("File length = " + testFile.length());
		//		try {
		//		StringBuffer buff = new StringBuffer(100000);
		//		BufferedReader r = new BufferedReader(new FileReader(testFile));
		//		String line;
		//		int nLine = 0;
		//			while ((line = r.readLine()) != null) {
		//			
		//			   char[] nextLine = line.toCharArray();
		//			   
		//			   /*
		//			   if(nLine < 5){
		//				   logger.info(nextLine[0]);
		//			   }
		//			   */
		//
		//			   nLine ++;
		//				
		//			}
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		long endTime = System.currentTimeMillis();
		//		System.out.println("That took " + (endTime - startTime) + " milliseconds");


	}

	/*
	 * 
	 * Buffered drawing
	 * 
	 * Use a BufferedImage and the setRGB(...) method. Then you draw the entire image in your paint routine.
	 * 
	 */



	// File reading
	/*
	 * 
	 * public class Buffer
{
    public static void main(String args[]) throws Exception
    {
        String inputFile = "charData.xml";
        FileInputStream in = new FileInputStream(inputFile);
        FileChannel ch = in.getChannel();
        ByteBuffer buf = ByteBuffer.allocateDirect(BUFSIZE);  // BUFSIZE = 256

        Charset cs = Charset.forName("ASCII"); // Or whatever encoding you want

        // read the file into a buffer, 256 bytes at a time 
        int rd;
        while ( (rd = ch.read( buf )) != -1 ) {
            buf.rewind();
            System.out.println("String read: ");
            CharBuffer chbuf = cs.decode(buf);
            for ( int i = 0; i < chbuf.length(); i++ ) {
                // print each character 
                System.out.print(chbuf.get());
            }
            buf.clear();
        }
    }
}

	 */

}
