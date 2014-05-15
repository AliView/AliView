package aliview.externalcommands;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import aliview.AliViewWindow;
import aliview.FileFormat;
import aliview.aligner.MuscleWrapper;

public class CommandItem{
	private static final String LF = System.getProperty("line.separator");

	public static final String CURRENT_ALIGNMENT = "CURRENT_ALIGNMENT";
	public static final String CURRENT_ALIGNMENT_TEMP = "CURRENT_ALIGNMENT_TEMP";
	public static final String CURRENT_ALIGNMENT_FASTA = "CURRENT_ALIGNMENT_FASTA";
	public static final String CURRENT_ALIGNMENT_PHYLIP = "CURRENT_ALIGNMENT_PHYLIP";
	public static final String NEW_ALIGNMENT_TEMP_FILE = "NEW_ALIGNMENT_TEMP_FILE";
	public static final String OUTPUT_FILE = "TEMP_OUT_FILE";
	public static final String CURRENT_ALIGNMENT_FASTA_TRANSLATED_AA = "CURRENT_ALIGNMENT_FASTA_TRANSLATED_AA";
	public static final String SECOND_FILE = "SECOND_FILE";
	public static final String SECOND_SEQUENCES = "SECOND_SEQUENCES";

	private String name;
	private String programPath;
	private String command;
	private String commandAlternative;
	private boolean isActivated;
	private boolean showCommandWindow;

	private ArrayList<String[]> parsedCommand =  new ArrayList<String[]>();
	

	public CommandItem(String name, String programPath, String command, boolean isActivated, boolean showCommandWindow) {
		setName(name);
		setProgramPath(programPath);
		setCommand(command);
		this.isActivated = isActivated;
		this.showCommandWindow = showCommandWindow;
	}

	public void setProgramPath(String programPath) {
		this.programPath = programPath;
	}

	public String getProgramPath() {
		return programPath;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
		parseCommand();
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public boolean isShowCommandWindow() {
		return showCommandWindow;
	}

	public void setShowCommandWindow(boolean showCommandWindow) {
		this.showCommandWindow = showCommandWindow;
	}

	public FileFormat getCurrentAlignmentFileFormat() {
		
		if(getCommand().contains(CURRENT_ALIGNMENT_TEMP)){
			return FileFormat.FASTA;
		}
		else if(getCommand().contains(CURRENT_ALIGNMENT_FASTA)){
			return FileFormat.FASTA;
		}
		else if(getCommand().contains(CURRENT_ALIGNMENT_PHYLIP)){
			return FileFormat.PHYLIP;
		}
		else if(getCommand().contains(CURRENT_ALIGNMENT)){
			return FileFormat.FASTA;
		}
		else{
			return FileFormat.FASTA;
		}
	}
	
	public void reParseCommand(){
		parseCommand();
	}
	
	public void parseCommand(){
		parseCommandIncludingProgramAsFirstParameter();
	}
	
	private void parseCommandIncludingProgramAsFirstParameter(){
		if(getCommand() == null){
			return;
		}
		
		parsedCommand = new ArrayList<String[]>();
		
		
		String commandInclProgram = "";
		if(getProgramPath() != null && getProgramPath().length() > 0){
			// strip remaining " or ' from argument
			String stripped = StringUtils.strip(getProgramPath(), "\"'");
			commandInclProgram = "\"" + stripped + "\"" + " " + getCommand();
		}else{
			commandInclProgram = getCommand();
		}
 		
		// split lines
		String[] splitted = commandInclProgram.split("[\r\n]+");
		
		// create a command per line
		for(int n = 0; n < splitted.length; n++){

			List<String> matchList = new ArrayList<String>();
			Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
			Matcher regexMatcher = regex.matcher(splitted[n]);
			while (regexMatcher.find()) {
				String arg = regexMatcher.group();
				// strip remaining " or ' from argument
				arg = StringUtils.strip(arg, "\"'");
				matchList.add(arg);
			} 
			String[] splitline = matchList.toArray(new String[matchList.size()]);
			parsedCommand.add(splitline);
		}
	}
		
	
	public void replaceInCommand(String find, String replace){
		for(String[] line: parsedCommand){
			for(int i = 0; i < line.length; i++){
				line[i] = StringUtils.replace(line[i], find, replace);
			}
		}
	}
	
	public void replaceParametersInCommand(){
		replaceInCommand("LOCAL_ALIVIEW_DIR",MuscleWrapper.getAliViewUserDataDirectory());
	}
	
	public void setParameterSecondFile(File secondFile) {
		replaceInCommand(CommandItem.SECOND_FILE, secondFile.getAbsolutePath());
		replaceInCommand(CommandItem.SECOND_SEQUENCES, secondFile.getAbsolutePath());
	}
	
	public void setParameterCurrentFile(File currentFile) {
		replaceInCommand(CommandItem.CURRENT_ALIGNMENT_TEMP, currentFile.getAbsolutePath());
		replaceInCommand(CommandItem.CURRENT_ALIGNMENT_FASTA, currentFile.getAbsolutePath());
		replaceInCommand(CommandItem.CURRENT_ALIGNMENT_PHYLIP, currentFile.getAbsolutePath());
		replaceInCommand(CommandItem.CURRENT_ALIGNMENT_FASTA_TRANSLATED_AA, currentFile.getAbsolutePath());
		replaceInCommand(CommandItem.CURRENT_ALIGNMENT, currentFile.getAbsolutePath());
	}
	
	public void setParameterOutputFile(File outputFile) {
		replaceInCommand(CommandItem.OUTPUT_FILE, outputFile.getAbsolutePath());
	}

	public boolean commandContainsIgnoreCase(String target) {
		return getCommand().toLowerCase().contains(target.toLowerCase());
	}

	public ArrayList<String[]> getParsedCommands() {
		return parsedCommand;
	}
	
	
}
