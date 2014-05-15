package utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

public class Utilities{
    
	private static final String LF = System.getProperty("line.separator");
    
    private static Properties environmentVars;
    
    /**
    *   Metoden anropar metoden setEnabled i en container och alla dess delkomponenter
    *   
    *   Metoden tar en container som inargument och loopar därefter igenom
    *   alla komponenter som den innehåller och om dessa underkomponenter också
    *   är containers så genomförs samma sak på dessa o.s.v.
    *
    */
    public static void enableComponents(Container cont, boolean enable){
        Component comps[] = cont.getComponents();
        for(int i = 0; i < comps.length; i ++){
            comps[i].setEnabled(enable);
            if(comps[i] instanceof Container){
                enableComponents((Container)comps[i], enable);
            }
        }
    }
    
    /**
    *   
    *   Metoden set till att det finns suffix på filen
    *   om aktuellt suffix redan finns läggs inget till
    *
    */
    public static File assureFileSuffix(File file, String suffix){
        if(file == null){
            return file;
        }
        if(! file.getName().toUpperCase().endsWith(suffix.toUpperCase())){
            file = new File(file.toString() + suffix);
        }
        return file;
    }
    
    /**
    *   
    *   Metoden tar bort suffix från filnamn
    *
    */
    public static File removeFileSuffix(File file){
        if(file == null){
            return file;
        }
        
        int endPos = file.toString().indexOf(".");
        if(endPos > -1){
            String newName = file.toString().substring(0, endPos);
            file = new File(newName);
        }
        
        return file;
    }
    
    
    /**
    *   Metoden formaterar en Sträng till en byte-array som avslytas med siffran 0,
    *   (detta är ett lämpligt format att överföra textsträngar till c-program)
    *   
    */
    public static byte[] stringToTerminatedByteArray(String inString){
        if(inString == null){
            return null;
        }
        
        byte[] temp = inString.getBytes();
        byte[] chrStr = new byte[temp.length + 1];
        System.arraycopy(temp, 0, chrStr, 0, temp.length);
        return chrStr;
    }
    
    
 
    
    /**
    *
    *   Metoden sparar en fil och låter användaren välja var med hjälp av en 
    *   JFileChooser, metoden frågar vid eventuell ersättning och meddelar fel
    *
    *   Metoden returnerar den fil som sparats, null om ingen sparats
    *
    */
    public static File saveFileViaChooser(File theFile, String suffix, InputStream fileStream, JFrame frame){
        // Flagga som håller koll på om sparning lyckats
        boolean fileSaved = false;
        
        System.err.println("Inne i saveFileViaChooser");
        
        // Skapa den panel som visas för användaren
        JFileChooser fileChooser = new JFileChooser();
        //fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
		
		// Om inte suffix är specifierat använd eventuellt suffix som finns i filnamn
		if(suffix == null){		
		    suffix = Utilities.getFileSuffix(theFile);		    
		}
		
		System.err.println("Hit");
		
		fileChooser.setFileSystemView(FileSystemView.getFileSystemView());
		
		fileChooser.setSelectedFile(theFile);
		       		       
		while(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION){
		    boolean shouldSaveFile = true;
		    theFile = fileChooser.getSelectedFile();
		    
		    // Lägg eventuellt till suffix
		    theFile = Utilities.assureFileSuffix(theFile, suffix);
		    

		    // Kolla om filen redan finns
		    if(theFile.exists()){
		        String question = theFile.toString() + "," + LF +
		                          "finns redan, vill du ersätta den?";
		        int result = JOptionPane.showOptionDialog(frame,
		                                                   question,
		                                                   "Ersätt?",
		                                                   JOptionPane.YES_NO_OPTION,
                                                           JOptionPane.QUESTION_MESSAGE,
                                                           null,
                                                           new Object[]{"Ja","Nej"},
                                                           "Nej"
                                                           );
                // Användaren ville ej skriva över
                if(result == JOptionPane.NO_OPTION){
                    shouldSaveFile = false;
                }
                else{
                    // Kolla om det går att skriva över filen
                	if(! theFile.canWrite()){
                	    System.err.println("Kan inte skriva");
                		JOptionPane.showOptionDialog(frame,
		                                                   "Det går inte att ersätta filen," + LF +
		                                                   "den kanske används eller är skrivskyddad.",
		                                                   "Går ej att ersätta",
		                                                   JOptionPane.OK_OPTION,
                                                           	JOptionPane.WARNING_MESSAGE,
                                                           	null,
                                                           	new Object[]{"Ok"},
                                                           	"Ok"
                                                           	);
                                                           	
                		shouldSaveFile = false;
                	}
                	
               }
               
                
            }
            
		    // Spara filen   
            if(shouldSaveFile){
		        
	// This is where file is saved
  //               fileSaved = Utilities.saveStreamAsFile(fileStream, theFile);
    
            	
                if(fileSaved){
	     	        break;
	     	    }
	     	    else{
	     	        JOptionPane.showOptionDialog(frame,
		                                                   "Det gick inte att spara filen," + LF +
		                                                   "kanske det saknas rättigheter" + LF + 
		                                                   "eller den kanske används.",
		                                                   "Går ej att spara",
		                                                   JOptionPane.OK_OPTION,
                                                           	JOptionPane.WARNING_MESSAGE,
                                                           	null,
                                                           	new Object[]{"Ok"},
                                                           	"Ok"
                                                           	);  
	     	        
	     	    }
	        }
	                
 
	   }
		
		if(fileSaved){
		    return theFile;
		}
		else{
		    return null;
		}
	}
    
  

	

     /**
	*
	*   Metoden returnerar den inloggade användarens hemkatalog 
	*
	*/
	public static String getUserDir(){
	    String userdir;
	    
	    // Försök hitta en lämplig katalog att spara i 
		userdir = Utilities.getEnv("HOMEDRIVE") + Utilities.getEnv("HOMEPATH");
		
		if(userdir == null){
		    userdir = System.getProperty("user.dir");
		}
		if(userdir == null){
		    userdir = System.getProperty("user.home");
		}      
		if(userdir == null){
		    userdir = System.getProperty("java.io.tmpdir");
		}
		if(userdir == null){
		    userdir = "";
		}
		
        return userdir;
     }
     
     
     /**
     *
     *  Metoden returnerar innehållet i en specifierad miljövariabel
     *
     */
     public static String getEnv(String env){
		
		// Försök först att hämta med native-funktion
		String envVarValue = null;
		
		System.err.println("(env)" + env + "=" + envVarValue);
		
		// Om den returnerade null så fungerade ej native-funktionen
		if(envVarValue == null){
		    if(environmentVars == null){
		        environmentVars = new Properties();
    		    
		        try {
			        InputStream is = Runtime.getRuntime().exec("cmd /c set").getInputStream();
			        InputStreamReader isr = new InputStreamReader(is);
			        BufferedReader br = new BufferedReader(isr);
			        // read output from subprogram 
			        while(true){
    			    
		  	   	        String inLine = br.readLine();
	  	   		        if(inLine == null){
	 	   			        break;
	   	   		        }	   	   		
	   	   		        // dela upp vid =
	   	   		        String property = inLine.substring(0, inLine.indexOf('='));
	   	   		        String value = inLine.substring(inLine.indexOf('=') + 1);
    	   	   		    
	   	   		        environmentVars.setProperty(property, value);	   	   		    
	   	   	        }
    	   	   	
			        br.close();
                    
                    /*
                    environmentVars.load();
                    environmentVars.list(new PrintStream(System.out));
                    */
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
            
            envVarValue = environmentVars.getProperty(env);
        }
        
        return envVarValue;

	}
    
    
    /**
    *
    *  Metoden kör ett program
    *
    */
	public static void runProgram(String programName, String parameters) throws Exception{
		
		
		String command = "";
		
		// Kommandot som skall utföras är olika beroende på OS
		String os = System.getProperty("os.name");
		if("Windows 95".equalsIgnoreCase(os) ||
		   "Windows ME".equalsIgnoreCase(os) ||
		   "Windows 98".equalsIgnoreCase(os)){
			command = "start " + programName + " " + parameters;
		}
		else{
			command = "cmd /c start " + programName + " " + parameters;
		}
		
		// Kör kommandot
		Runtime.getRuntime().exec(command);
		
	}
	
	/**
    *
    *  Metoden byter font för programmets alla komponenter
    *
    *  Denna metod byter endast på komponenter som ej ännu skapats
    *
    */
	public static void adjustFonts(String fontName, int fontSize) {
      UIDefaults defaults = UIManager.getDefaults();
      Vector newDefaults = new Vector();
      for ( Enumeration e = defaults.keys(); e.hasMoreElements();  ) {
         Object key = e.nextElement();
         Object value = defaults.getFont( key );
         if ( value != null ) {
            Font oldFont = (Font) value;
            Font newFont = new Font( fontName,
             Font.PLAIN , fontSize ); //oldFont.getStyle()
            newDefaults.add( key );
            newDefaults.add( newFont );
         }
      }

      defaults.putDefaults( newDefaults.toArray() );
   }
   
   /**
    *
    *  Metoden byter ut en färg för programmets alla komponenter
    *
    *  Denna metod byter endast på komponenter som ej ännu skapats
    *
    */
	public static void adjustColors(Color oldColor, Color newColor) {
      UIDefaults defaults = UIManager.getDefaults();
      Vector newDefaults = new Vector();
      for ( Enumeration e = defaults.keys(); e.hasMoreElements();  ) {
         Object key = e.nextElement();
         Object value = defaults.getColor( key );
         if ( value != null ) {
            Color defaultColor = (Color) value;
            System.err.println(key + " R=" + defaultColor.getRed() + " G=" + defaultColor.getGreen() + " B=" + defaultColor.getBlue() + " A=" + defaultColor.getAlpha());
            if(oldColor.equals(defaultColor)){
                Color newDefaultColor = new Color(newColor.getRGB());
                newDefaults.add( key );
                newDefaults.add( newDefaultColor );
            }
         }
      }

      defaults.putDefaults( newDefaults.toArray() );
   }
   
   

    
  
    
     /**
    *
    *   Metoden returnerar filsuffix (bokstäver efter sista "." i en fil
    *   
    *
    */
    public static String getFileSuffix(File file){
        
        String suffix = "";
        
        String fileName = file.toString();
        
        int pos = fileName.lastIndexOf(".");
        
        // Om det fanns ett suffix
        if(pos > -1){
            suffix = fileName.substring(pos);
        }
        
        return suffix;
        
    }
    
    /**
    *
    *   Metoden returnerar huruvide det är möjligt att skriva en fil, denna behövs
    *   då filmetoden canWrite() endast fungerar för dos-filattribut.
    *   Metoden försöker skapa filen, om det går returneras sant och filen tas bort
    *
    */
    public static boolean isWritePossible(File testFile){
        if(testFile == null){
            return false;
        }
        
        try{
            FileOutputStream fileOutStream = new FileOutputStream(testFile);
            fileOutStream.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
            return false;
        }
 
        testFile.delete();
        
        return true;
    }
    
    /**
    *
    *   Metoden returnerar en nummerformaterare som är justerad så att det
    *   alltid används punkt . som decimalsymbol istället för ,
    *
    */
    public static NumberFormat getNumberInstance(){
        NumberFormat nf = NumberFormat.getNumberInstance();
        if(nf instanceof DecimalFormat){
            DecimalFormat df = (DecimalFormat) nf;
            DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(dfs);
        }
        return nf;
        
        
    }
    
    /**
    *
    *   Metoden byter ut komman mot punkter
    *
    */
    public static String adjustDecimalSymbol(String input){
        if(input == null){
            return null;
        }
        
        // Byt ut , till .    
        String output = input.replace(',','.');
        
        return output;
    }
    
        
    
    
    
    
    
    
    
    
    
    

}