package aliview.settings;

import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;

import utils.OSNativeUtils;
import aliview.AliViewWindow;
import aliview.aligner.AlignemtItem;
import aliview.aligner.MuscleWrapper;
import aliview.alignment.AlignmentEvent;
import aliview.alignment.AlignmentListener;
import aliview.color.ColorScheme;
import aliview.color.ColorSchemeFactory;
import aliview.externalcommands.CommandItem;
import aliview.hacks.FilePreferencesFactory;
import aliview.old.ExternalCmdFrame;
import aliview.pane.CharPixels;

public class Settings {
	private static final Logger logger = Logger.getLogger(Settings.class);
	private static final String LF = System.getProperty("line.separator");
	
	//private static final String ALIVIEW_HELP_URL = "http://www.ormbunkar.se/aliview/help/help.html";
	private static final String ALIVIEW_HELP_URL = "http://www.ormbunkar.se/aliview/index.html";
	
	private static final String SAVE_ALIGNMENT_DIRECTORY = "SAVE_ALIGNMENT_DIRECTORY";
	private static final String LOAD_ALIGNMENT_DIRECTORY = "LOAD_ALIGNMENT_DIRECTORY";
	private static final String SAVE_SELECTION_DIRECTORY = "SAVE_SELECTION_DIRECTORY";
	private static final String EXTERNAL_COMMAND_FILE_DIRECTORY = "EXTERNAL_COMMAND_FILE_DIRECTORY";
	private static final String TOGGLE_SHORT_NAME_ONLY = "TOGGLE_SHORT_NAME_ONLY";
	private static final String COLOR_SCHEME = "COLOR_SCHEME";
	private static final String COLOR_SCHEME_AMINOACID = "COLOR_SCHEME_AMINOACID";
	private static final String COLOR_SCHEME_NUCLEOTIDE = "COLOR_SCHEME_NUCLEOTIDE";
	private static final String  RECENT_FILE = "RECENT_FILE";
	
	private static final String CMD_NAME_ = "CMD_NAME_";
	private static final String CMD_PROGRAM_PATH_ = "CMD_PROGRAM_PATH_";
	private static final String CMD_COMMAND_ = "CMD_COMMAND_";
	private static final String CMD_IS_ACTIVATED_ = "CMD_IS_ACTIVATED_";
	private static final String CMD_SHOW_COMMAND_WIN_ = "CMD_SHOW_COMMAND_WIN_";
	
	private static SettingValue saveAlignmentDir = new SettingValue("SAVE_ALIGNMENT_DIRECTORY", System.getProperty("user.home"));
	private static SettingValue minPrimerLength = new SettingValue("MIN_PRIMER_LEN", 20, 15, 30);
	private static SettingValue maxPrimerLength = new SettingValue("MAX_PRIMER_LEN", 24, 15, 30);
	private static SettingValue dimerReportThreashold = new SettingValue("DIMER_REPORT_THREASHOLD", 5, 1, 30);
	private static SettingValue minPrimerTM =  new SettingValue("MIN_PRIMER_TM", 40, 0, 100);
	private static SettingValue maxPrimerTM = new SettingValue("MAX_PRIMER_TM", 80, 0, 100);
	private static SettingValue useCustomFontSize = new SettingValue("USE_CUSTOM_FONT_SIZE", false);
	private static SettingValue customFontSize = new SettingValue("CUSTOM_FONT_SIZE", 12, 1, 24);
		
	private static SettingValue reverseHorizontalMouseWheel = new SettingValue("REVERSE_HORIZONTAL_MOUSE_WHEEL", false);
	private static SettingValue reverseVerticalMouseWheel = new SettingValue("REVERSE_VERTICAL_MOUSE_WHEEL", false);
	
	
	
	private static SettingValue horizontalMouseWheelScrollModifier = new SettingValue("HORIZONTALMOUSEWHEELSCROLLMODIFIER", 20,1,100);
	private static SettingValue verticalMouseWheelScrollModifier = new SettingValue("VERTICALMOUSEWHEELSCROLLMODIFIER", 20,1,100);
	private static SettingValue largeFileIndexing = new SettingValue("LARGE_FILE_INDEXING", 100000,10,100000000);
	private static SettingValue maxFileHistogramSequences = new SettingValue("MAX_FILE_HISTOGRAM_SEQUENCES", 1000,10,1000000);
	
	private static final String LOGFILE_NAME = "AliView.log";
	private static final String ALIVIEW_USERDATA_SUBDIR = ".AliView";
	
	private static Preferences prefs = Preferences.userNodeForPackage(Settings.class);
	private static ArrayList<SettingsListener> settingListeners = new ArrayList<SettingsListener>();
	
	// This is a good place for putting all hide checkbox values
	private static SettingValue hideFileSeqLimitedEditCapabilities = new SettingValue("hideFileSeqLimitedEditCapabilities", false);
	private static SettingValue hideEditModeMessage = new SettingValue("hideEditModeMessage", false);
	private static SettingValue hideMuscleProfileAlignInfoMessage = new SettingValue("hideMuscleProfileAlignInfoMessage", false);
	private static SettingValue hideRealignEverythingMessage = new SettingValue("hideRealignEverythingMessage", false);
	private static SettingValue hideAlignmentProgressWindowWhenDone = new SettingValue("hideAlignmentProgressWindowWhenDone", false);
	private static SettingValue hideDuplicateSeqNamesMessage = new SettingValue("hideDuplicateSeqNamesMessage", false);
	private static SettingValue hideAskBeforeEditMode  = new SettingValue("hideAskBeforeEditMode", false);
	private static SettingValue fontCase = new SettingValue("fontCase", CharPixels.CASE_UNTOUCHED, 0, 10);
	

	public static SettingValue getMinPrimerLength(){
		return minPrimerLength;
	}
	
	public static SettingValue getMaxPrimerLength(){	
		return maxPrimerLength;
	}
	
	public static SettingValue getDimerReportThreashold() {
		return dimerReportThreashold;
	}
	
	public static void putBooleanValue(SettingValue settingValue,boolean booleanValue) {
		prefs.putBoolean(settingValue.getPrefsKey(), booleanValue);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static boolean getBooleanValue(SettingValue settingValue) {
		boolean value = prefs.getBoolean(settingValue.getPrefsKey(), settingValue.getDefaultBooleanValue());
		return value;
	}
	
	
	public static int getIntValue(SettingValue settingValue) {
		int value = prefs.getInt(settingValue.getPrefsKey(), settingValue.getDefaultIntValue());
		return value;
	}
	
	public static void putIntValue(SettingValue settingValue, int intValue){
		prefs.putInt(settingValue.getPrefsKey(), intValue);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void putIntValue(SettingValue settingValue){
		logger.info("put" + settingValue.getIntValue());
		prefs.putInt(settingValue.getPrefsKey(), settingValue.getIntValue());
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getStringValue(SettingValue settingValue) {
		String value = prefs.get(settingValue.getPrefsKey(), settingValue.getDefaultStringValue());
		return value;
	}

	public static void putStringValue(SettingValue settingValue, String value) {
		prefs.put(settingValue.getPrefsKey(), value);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	/*
	public static void putIntValue(String prefsKey, int value) {
		prefs.putInt(maxPrimerLength.getPrefsKey(), length);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	
	public static final String getSaveAlignmentDirectory(){	
		return prefs.get(SAVE_ALIGNMENT_DIRECTORY, System.getProperty("user.home"));
	}
	
	public static final void putSaveAlignmentDirectory(String dirName){	
		prefs.put(SAVE_ALIGNMENT_DIRECTORY, dirName);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static final String getLoadAlignmentDirectory(){	
		return prefs.get(LOAD_ALIGNMENT_DIRECTORY, System.getProperty("user.home"));
	}
	
	
	public static final void putLoadAlignmentDirectory(String dirName){	
		prefs.put(LOAD_ALIGNMENT_DIRECTORY, dirName);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final boolean getToggleShortNameOnly() {
		return prefs.getBoolean(TOGGLE_SHORT_NAME_ONLY, false);
	}

	public static void putToggleShortNameOnly(boolean shortNameOnly) {
		prefs.putBoolean(TOGGLE_SHORT_NAME_ONLY, shortNameOnly);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String getExternalCommandFileDirectory() {
		return prefs.get(EXTERNAL_COMMAND_FILE_DIRECTORY, System.getProperty("user.home"));
	}
	
	public static void putExternalCommandFileDirectory(String selectedFile) {
		prefs.put(EXTERNAL_COMMAND_FILE_DIRECTORY, selectedFile);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static final String getSaveSelectionDirectory() {
		return prefs.get(SAVE_SELECTION_DIRECTORY, System.getProperty("user.home"));
	}

	public static void putSaveSelectionDirectory(String dirName) {
		prefs.put(SAVE_SELECTION_DIRECTORY, dirName);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getAliViewUserDataSubdir() {
		return ALIVIEW_USERDATA_SUBDIR;
	}

	public static String getLogfileName() {
		return LOGFILE_NAME;
	}

	
	public static ColorScheme getColorSchemeNucleotide() {
		String colorSchemeName = prefs.get(COLOR_SCHEME_NUCLEOTIDE, null);
		return ColorSchemeFactory.getColorScheme(colorSchemeName);
	}
	
	public static ColorScheme getColorSchemeAminoAcid() {
		String colorSchemeName = prefs.get(COLOR_SCHEME_AMINOACID, null);
		return ColorSchemeFactory.getColorScheme(colorSchemeName);
	}
	
	public static void setColorSchemeNucleotide(ColorScheme colorScheme) {
		prefs.put(COLOR_SCHEME_NUCLEOTIDE, colorScheme.getName());
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setColorSchemeAminoAcid(ColorScheme colorScheme) {
		prefs.put(COLOR_SCHEME_AMINOACID, colorScheme.getName());
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static ArrayList<CommandItem> getCommandItems(String prefix){
		ArrayList<CommandItem> commands = new ArrayList<CommandItem>();
		for(int n = 0; n < 10; n++){
			// always try 10
			CommandItem nextCmd = getCommandItem(n,prefix);
			if(nextCmd.getName() == null){
				// skip commands.add(new CommandItem("", "", "", false,false));
			}else{
				commands.add(nextCmd);
			}
		}
		return commands;
	}
	
	public static void putCommandItems(ArrayList<CommandItem> commands, String prefix){
		for(int n = 0; n < commands.size(); n++){
			putCommandItem(n, prefix, commands.get(n));
		}
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fireCommandItemsChanged();
	}

	public static void clearCommandItems(String prefix){
		for(int n = 0; n < 10; n++){
			logger.info("clear" + prefix);
			clearCommandItem(n, prefix);
		}
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void clearCommandItem(int i, String prefix){
		prefs.remove(prefix + CMD_NAME_ + i);
		prefs.remove(prefix + CMD_PROGRAM_PATH_ + i);
		prefs.remove(prefix + CMD_COMMAND_ + i);
		prefs.remove(prefix + CMD_IS_ACTIVATED_ + i);
		prefs.remove(prefix + CMD_SHOW_COMMAND_WIN_ + i);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static CommandItem getCommandItem(int i, String prefix){
		String name = prefs.get(prefix + CMD_NAME_ + i, null);
		String programPath = prefs.get(prefix + CMD_PROGRAM_PATH_ + i, null);
		String command = prefs.get(prefix + CMD_COMMAND_ + i, null);
		boolean isActivated = prefs.getBoolean(prefix + CMD_IS_ACTIVATED_ + i, false);
		boolean showCommandWindow =  prefs.getBoolean(prefix + CMD_SHOW_COMMAND_WIN_ + i, false);	
		return new CommandItem(name, programPath, command, isActivated, showCommandWindow);
	}
	
	private static void putCommandItem(int i, String prefix, CommandItem cmd){
		logger.info("put"+cmd);
		prefs.put(prefix + CMD_NAME_ + i, cmd.getName());
		prefs.put(prefix + CMD_PROGRAM_PATH_ + i, cmd.getProgramPath());
		prefs.put(prefix + CMD_COMMAND_ + i, cmd.getCommand());
		prefs.putBoolean(prefix + CMD_IS_ACTIVATED_ + i, cmd.isActivated());
		prefs.putBoolean(prefix + CMD_SHOW_COMMAND_WIN_ + i, cmd.isShowCommandWindow());
	}

	public static SettingValue getPrimerMinTM() {
		return minPrimerTM;
	}
	
	public static SettingValue getPrimerMaxTM() {
		return maxPrimerTM;
	}

	/*
	private static CommandItem getExternalCommand(int i){
		return getCommandItem(i, "EXTERNAL_");
	}

	
	private static void putExternalCommand(int i, CommandItem cmd){
		putCommandItem(i, "EXTERNAL_", cmd);
	}
	*/
	
	public static void putExternalCommands(ArrayList<CommandItem> items){
		putCommandItems(items, "EXTERNAL_");
	}
	
	public static ArrayList<CommandItem> getExternalCommands(){
		ArrayList<CommandItem> items = getCommandItems("EXTERNAL_");
		if(items.size() == 0){
			// DEFAULT
			for(CommandItem item: getDefaultCommandItems()){
				items.add(item);
			}
		}
		return items;		
	}
	
	
/*	
	private static CommandItem getAlignADDCommand(int i){
		return getCommandItem(i, "ALIGN_ADD_");
	}
	
	private static void putAlignADDCommand(int i, CommandItem cmd){
		putCommandItem(i, "ALIGN_ADD_", cmd);
	}
*/
	
	private static CommandItem[] getDefaultCommandItems() {
		
		// raxmlHPC -f a -x 1465421654 -# 1 -m PROTGAMMAGTR -n RAXML_ALIGN -s /tmp/aliview-tmp-current-alignment3189140589076301400phy 
		
		if(OSNativeUtils.isMac()){			
			CommandItem MAC_DEFAULT_1 = new CommandItem("FastTree + FigTree", "", "/usr/local/bin/FastTree -nt -gtr -out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA" + LF + "open -a \"FigTree v1.4.2.app\" TEMP_OUT_FILE", false, true);
			CommandItem MAC_DEFAULT_2 = new CommandItem("Textedit", "", "open -a TextEdit CURRENT_ALIGNMENT_FASTA", false,false);
			CommandItem MAC_DEFAULT_3 = new CommandItem("", "", "", false,false);
			CommandItem MAC_DEFAULT_4 = new CommandItem("", "", "", false,false);
			CommandItem MAC_DEFAULT_5 = new CommandItem("", "", "", false,false);
			CommandItem[] MAC_COMMAND_ITEM_DEFAULTS = new CommandItem[]{MAC_DEFAULT_1, MAC_DEFAULT_2, MAC_DEFAULT_3, MAC_DEFAULT_4, MAC_DEFAULT_5};
			return MAC_COMMAND_ITEM_DEFAULTS;
		}else if(OSNativeUtils.isWindows()){
			CommandItem WIN_DEFAULT_1 = new CommandItem("FastTree + FigTree", "", "\"C:\\Program Files\\FastTree\\FastTree.exe\" -nt -gtr -out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA" + LF + "\"C:\\Program Files\\FigTree\\FigTree v1.4.2.exe\" TEMP_OUT_FILE", true, true);
			CommandItem WIN_DEFAULT_2 = new CommandItem("notepad++(Texteditor)", "", "\"C:\\Program Files (x86)\\Notepad++\\Notepad++.exe\" CURRENT_ALIGNMENT_FASTA", false,false);
			CommandItem WIN_DEFAULT_3 = new CommandItem("", "", "", false,false);
			CommandItem WIN_DEFAULT_4 = new CommandItem("", "", "", false,false);
			CommandItem WIN_DEFAULT_5 = new CommandItem("", "", "", false,false);
			CommandItem[] WIN_COMMAND_ITEM_DEFAULTS = new CommandItem[]{WIN_DEFAULT_1, WIN_DEFAULT_2, WIN_DEFAULT_3, WIN_DEFAULT_4, WIN_DEFAULT_5};
			return WIN_COMMAND_ITEM_DEFAULTS;
		}else{
			CommandItem DEFAULT_1 = new CommandItem("FastTree + FigTree", "", "FastTree -nt -gtr -out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA" + LF + "figtree TEMP_OUT_FILE", false, true);
			CommandItem DEFAULT_2 = new CommandItem("Geany(Texteditor)", "", "geany CURRENT_ALIGNMENT_FASTA", false,false);
			CommandItem DEFAULT_3 = new CommandItem("", "", "", false,false);
			CommandItem DEFAULT_4 = new CommandItem("", "", "", false,false);
			CommandItem DEFAULT_5 = new CommandItem("", "", "", false,false);
			CommandItem[] COMMAND_ITEM_DEFAULTS = new CommandItem[]{DEFAULT_1, DEFAULT_2, DEFAULT_3, DEFAULT_4, DEFAULT_5};
			return COMMAND_ITEM_DEFAULTS;
		}
	}
	
	private static CommandItem[] getAlignerAddDefaultItems() {
		if(OSNativeUtils.isMac()){
			CommandItem MAC_DEFAULT_ADD_ALIGNER_ITEM_1 = new CommandItem("Muscle-profile", MuscleWrapper.getMusclePath().getAbsolutePath(), "-profile -in1 CURRENT_ALIGNMENT_FASTA -in2 SECOND_SEQUENCES -out TEMP_OUT_FILE", true,true);
			CommandItem MAC_DEFAULT_ADD_ALIGNER_ITEM_2 = new CommandItem("Mafft --add", "/usr/bin/mafft", "--add SECOND_SEQUENCES --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem MAC_DEFAULT_ADD_ALIGNER_ITEM_3 = new CommandItem("Mafft --addfragments", "/usr/local/bin/mafft", "--addfragments SECOND_SEQUENCES --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem MAC_DEFAULT_ADD_ALIGNER_ITEM_4 = new CommandItem("Mafft --addfull", "/usr/local/bin/mafft", "--addfull SECOND_SEQUENCES --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem MAC_DEFAULT_ADD_ALIGNER_ITEM_5 = new CommandItem("", "", "", false,false);
			CommandItem[] MAC_ALIGNER_ADD_ITEM_DEFAULTS = new CommandItem[]{MAC_DEFAULT_ADD_ALIGNER_ITEM_1, MAC_DEFAULT_ADD_ALIGNER_ITEM_2, MAC_DEFAULT_ADD_ALIGNER_ITEM_3, MAC_DEFAULT_ADD_ALIGNER_ITEM_4, MAC_DEFAULT_ADD_ALIGNER_ITEM_5};
			return MAC_ALIGNER_ADD_ITEM_DEFAULTS;
		}else if(OSNativeUtils.isWindows()){
			CommandItem WIN_DEFAULT_ADD_ALIGNER_ITEM_1 = new CommandItem("Muscle-profile", MuscleWrapper.getMusclePath().getAbsolutePath(), "-profile -in1 CURRENT_ALIGNMENT_FASTA -in2 SECOND_SEQUENCES -out TEMP_OUT_FILE", true,true);
			CommandItem WIN_DEFAULT_ADD_ALIGNER_ITEM_2 = new CommandItem("Mafft --add", "cmd.exe", "/C \"C:\\Program Files\\mafft-win\\mafft.bat\" --add SECOND_SEQUENCES --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem WIN_DEFAULT_ADD_ALIGNER_ITEM_3 = new CommandItem("Mafft --addfragments", "cmd.exe", "/C \"C:\\Program Files\\mafft-win\\mafft.bat\" --addfragments SECOND_SEQUENCES --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem WIN_DEFAULT_ADD_ALIGNER_ITEM_4 = new CommandItem("Mafft --addfull","cmd.exe", "/C \"C:\\Program Files\\mafft-win\\mafft.bat\" --addfull SECOND_SEQUENCES --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem WIN_DEFAULT_ADD_ALIGNER_ITEM_5 = new CommandItem("", "", "", false,false);
			CommandItem[] WIN_ALIGNER_ADD_ITEM_DEFAULTS = new CommandItem[]{WIN_DEFAULT_ADD_ALIGNER_ITEM_1, WIN_DEFAULT_ADD_ALIGNER_ITEM_2, WIN_DEFAULT_ADD_ALIGNER_ITEM_3, WIN_DEFAULT_ADD_ALIGNER_ITEM_4, WIN_DEFAULT_ADD_ALIGNER_ITEM_5};
			return WIN_ALIGNER_ADD_ITEM_DEFAULTS;
		}else{
			CommandItem DEFAULT_ADD_ALIGNER_ITEM_1 = new CommandItem("Muscle-profile", MuscleWrapper.getMusclePath().getAbsolutePath(), "-profile -in1 CURRENT_ALIGNMENT_FASTA -in2 SECOND_SEQUENCES -out TEMP_OUT_FILE", true,true);
			CommandItem DEFAULT_ADD_ALIGNER_ITEM_2 = new CommandItem("Mafft --add", "mafft", "--add SECOND_SEQUENCES --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem DEFAULT_ADD_ALIGNER_ITEM_3 = new CommandItem("Mafft --addfragments", "mafft", "--addfragments SECOND_SEQUENCES --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem DEFAULT_ADD_ALIGNER_ITEM_4 = new CommandItem("Mafft --addfull", "mafft", "--addfull SECOND_SEQUENCES --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem DEFAULT_ADD_ALIGNER_ITEM_5 = new CommandItem("", "", "", false,false);
			CommandItem[] ALIGNER_ADD_ITEM_DEFAULTS = new CommandItem[]{DEFAULT_ADD_ALIGNER_ITEM_1, DEFAULT_ADD_ALIGNER_ITEM_2, DEFAULT_ADD_ALIGNER_ITEM_3, DEFAULT_ADD_ALIGNER_ITEM_4, DEFAULT_ADD_ALIGNER_ITEM_5};
			return ALIGNER_ADD_ITEM_DEFAULTS;

		}
	}
	
	private static CommandItem[] getAlignerALLDefaultItems() {
		if(OSNativeUtils.isMac()){
			CommandItem MAC_DEFAULT_ALL_ALIGNER_ITEM_1 = new CommandItem("Muscle", MuscleWrapper.getMusclePath().getAbsolutePath(), "-in CURRENT_ALIGNMENT_FASTA -out TEMP_OUT_FILE", true, true);
			CommandItem MAC_DEFAULT_ALL_ALIGNER_ITEM_2 = new CommandItem("Mafft-localpair", "/usr/local/bin/mafft", "--localpair --reorder --maxiterate 1000 --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem MAC_DEFAULT_ALL_ALIGNER_ITEM_3 = new CommandItem("Mafft-globalpair", "/usr/local/bin/mafft", "--globalpair --thread 2 --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem MAC_DEFAULT_ALL_ALIGNER_ITEM_4 = new CommandItem("", "", "", false,true);
			CommandItem MAC_DEFAULT_ALL_ALIGNER_ITEM_5 = new CommandItem("", "", "", false,true);
			CommandItem[] MAC_ALIGNER_ALL_ITEM_DEFAULTS = new CommandItem[]{MAC_DEFAULT_ALL_ALIGNER_ITEM_1, MAC_DEFAULT_ALL_ALIGNER_ITEM_2, MAC_DEFAULT_ALL_ALIGNER_ITEM_3, MAC_DEFAULT_ALL_ALIGNER_ITEM_4, MAC_DEFAULT_ALL_ALIGNER_ITEM_5};
			return MAC_ALIGNER_ALL_ITEM_DEFAULTS;
		}else if(OSNativeUtils.isWindows()){
			CommandItem WIN_DEFAULT_ALL_ALIGNER_ITEM_1 = new CommandItem("Muscle", MuscleWrapper.getMusclePath().getAbsolutePath(), "-in CURRENT_ALIGNMENT_FASTA -out TEMP_OUT_FILE", true, true);
			CommandItem WIN_DEFAULT_ALL_ALIGNER_ITEM_2 = new CommandItem("Mafft", "cmd.exe", "/C \"C:\\Program Files\\mafft-win\\mafft.bat\" --localpair --reorder --maxiterate 1000 --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem WIN_DEFAULT_ALL_ALIGNER_ITEM_3 = new CommandItem("Mafft--globalpair", "cmd.exe", "/C \"C:\\Program Files\\mafft-win\\mafft.bat\" --globalpair --thread 2 --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem WIN_DEFAULT_ALL_ALIGNER_ITEM_4 = new CommandItem("", "", "", false,true);
			CommandItem WIN_DEFAULT_ALL_ALIGNER_ITEM_5 = new CommandItem("", "", "", false,true);
			CommandItem[] WIN_ALIGNER_ALL_ITEM_DEFAULTS = new CommandItem[]{WIN_DEFAULT_ALL_ALIGNER_ITEM_1, WIN_DEFAULT_ALL_ALIGNER_ITEM_2, WIN_DEFAULT_ALL_ALIGNER_ITEM_3, WIN_DEFAULT_ALL_ALIGNER_ITEM_4, WIN_DEFAULT_ALL_ALIGNER_ITEM_5};
			return WIN_ALIGNER_ALL_ITEM_DEFAULTS;
		}else{
			CommandItem DEFAULT_ALL_ALIGNER_ITEM_1 = new CommandItem("Muscle", MuscleWrapper.getMusclePath().getAbsolutePath(), "-in CURRENT_ALIGNMENT_FASTA -out TEMP_OUT_FILE", true, true);
			CommandItem DEFAULT_ALL_ALIGNER_ITEM_2 = new CommandItem("Mafft", "mafft", "--localpair --reorder --maxiterate 1000 --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem DEFAULT_ALL_ALIGNER_ITEM_3 = new CommandItem("Mafft", "mafft", "--globalpair --thread 2 --out TEMP_OUT_FILE CURRENT_ALIGNMENT_FASTA", false,true);
			CommandItem DEFAULT_ALL_ALIGNER_ITEM_4 = new CommandItem("", "", "", false,true);
			CommandItem DEFAULT_ALL_ALIGNER_ITEM_5 = new CommandItem("", "", "", false,true);
			CommandItem[] ALIGNER_ALL_ITEM_DEFAULTS = new CommandItem[]{DEFAULT_ALL_ALIGNER_ITEM_1, DEFAULT_ALL_ALIGNER_ITEM_2, DEFAULT_ALL_ALIGNER_ITEM_3, DEFAULT_ALL_ALIGNER_ITEM_4, DEFAULT_ALL_ALIGNER_ITEM_5};
			return ALIGNER_ALL_ITEM_DEFAULTS;
		}
	}

	public static ArrayList<CommandItem> getAlignADDCommands(){
		ArrayList<CommandItem> items = getCommandItems("ALIGN_ADD_");
		if(items.size() == 0){
			// DEFAULT
			for(CommandItem item: getAlignerAddDefaultItems()){
				logger.info(item.getName());
				items.add(item);
			}
		}
		return items;		
	}
	
	public static void putAlignADDCommands(ArrayList<CommandItem> items){
		putCommandItems(items, "ALIGN_ADD_");
		fireAlignADDCmdsChanged();
	}
	
	public static void clearAlignADDCommands() {
		clearCommandItems("ALIGN_ADD_");
		fireAlignADDCmdsChanged();
	}

	public static void clearAlignALLCommands() {
		clearCommandItems("ALIGN_ALL_");
		fireAlignAllCmdsChanged();
	}
	
	public static void putAlignALLCommands(ArrayList<CommandItem> items){
		putCommandItems(items, "ALIGN_ALL_");
		fireAlignAllCmdsChanged();
	}

	public static ArrayList<CommandItem> getAlignALLCommands(){
		ArrayList<CommandItem> items = getCommandItems("ALIGN_ALL_");
		if(items.size() == 0){
			logger.info("items.size() " + items.size() );
			// DEFAULT
			for(CommandItem item: getAlignerALLDefaultItems()){
				logger.info(item);
				items.add(item);
			}
		}
		return items;		
	}

	
	
	/*
	private static CommandItem getAlignALLCommand(int i){
		return getCommandItem(i, "ALIGN_ALL_");
	}
	
	private static void putAlignALLCommand(int i, CommandItem cmd){
		putCommandItem(i, "ALIGN_ALL_", cmd);
	}
	*/
	

	public static SettingValue getReverseHorizontalMouseWheel() {
		return reverseHorizontalMouseWheel;
	}
	
	public static SettingValue getReverseVerticalMouseWheel() {
		return reverseVerticalMouseWheel;
	}

	public static SettingValue getHorizontalScrollModifier() {
		return horizontalMouseWheelScrollModifier;
	}

	public static SettingValue getVerticalScrollModifier() {
		return verticalMouseWheelScrollModifier;
	}
	
	public static SettingValue getHideFileSeqLimitedEditCapabilities() {
		return hideFileSeqLimitedEditCapabilities;
	}
	
	public static SettingValue getHideEditModeMessage() {
		return hideEditModeMessage;
	}
	
	public static SettingValue getHideMuscleProfileAlignInfoMessage() {
		return hideMuscleProfileAlignInfoMessage;
	}
	
	public static SettingValue getHideRealignEverythingMessage() {
		return hideRealignEverythingMessage;
	}
	
	public static SettingValue getHideAlignmentProgressWindowWhenDone() {
		return hideAlignmentProgressWindowWhenDone;
	}
	
	public static SettingValue getHideDuplicateSeqNamesMessage() {
		return hideDuplicateSeqNamesMessage;
	}
	
	public static SettingValue getHideAskBeforeEditMode() {
		return hideAskBeforeEditMode;
	}
	
	public static void clearAllHideThisDialogCheckboxes() {
		getHideFileSeqLimitedEditCapabilities().putBooleanValue(false);
		getHideEditModeMessage().putBooleanValue(false);
		getHideMuscleProfileAlignInfoMessage().putBooleanValue(false);
		getHideRealignEverythingMessage().putBooleanValue(false);
		getHideAlignmentProgressWindowWhenDone().putBooleanValue(false);
		getHideDuplicateSeqNamesMessage().putBooleanValue(false);
		getHideAskBeforeEditMode().putBooleanValue(false);
	}
	

	public static void addRecentFile(File alignmentFile){
		Vector<File> files = getRecentFiles();
		
		// skip if file is same as last
		if(files.size() > 0 && files.get(0).getAbsolutePath().equals(alignmentFile.getAbsolutePath()) == true){
			
			// skip
			
		}else{
			
			// remove older previous files with same name
			Vector<File> toRemove = new Vector<File>();
			for(File aRecentFile: files){
				if(alignmentFile.equals(aRecentFile)){
					toRemove.add(aRecentFile);
				}
			}
			files.removeAll(toRemove);
			files.insertElementAt(alignmentFile, 0);
			
			putRecentFiles(files);
		}
	}
	
	private static void putRecentFiles(Vector<File> files){
		// save the last 20 files
		logger.info(files.size());
		
		// first remove old ones
		for(int n = 0; n < 20; n++){
			logger.info("n" + n);
			prefs.remove(RECENT_FILE + n);
		}
		
		// then add new ones
		for(int n = 0; n < files.size() && n < 20; n++){
			logger.info("n" + n);
			prefs.put(RECENT_FILE + n, files.get(n).getAbsolutePath());
		}
		
		// and flush changes
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fireRecentFilesChanged();
	}

	public static Vector<File> getRecentFiles(){
		Vector<File> recent = new Vector<File>();
		for(int n = 0; n < 20; n++){
			String nextFile = prefs.get(RECENT_FILE + n, null);
			if(nextFile != null && nextFile.length() > 0){
				recent.add(new File(nextFile));
			}
		}
		return recent;
	}

	public static SettingValue getLargeFileIndexing() {
		return largeFileIndexing;
	}
	
	public static void addSettingsListener(SettingsListener listener){
		if(listener != null || !settingListeners.contains(listener)){
			settingListeners.add(listener);
		}
	}
	
	private static void fireRecentFilesChanged() {
		//logger.info("fire fireRecentFilesChanged, settingListeners.size()" + settingListeners.size());
		for(SettingsListener listener: settingListeners){
			listener.recentFilesChanged();
		}
	}
	private static void fireAlignAllCmdsChanged() {
		for(SettingsListener listener: settingListeners){
			listener.alignAllCmdsChanged();
		}
	}
	private static void fireAlignADDCmdsChanged() {
		for(SettingsListener listener: settingListeners){
			listener.alignAddCmdsChanged();	
		}
	}
	private static void fireCommandItemsChanged() {
		for(SettingsListener listener: settingListeners){
			listener.externalCmdsChanged();
		}
	}
	
	public static SettingValue getUseCustomFontSize() {
		return useCustomFontSize;
	}
	
	public static SettingValue getCustomFontSize() {
		return customFontSize;
	}

	public static SettingValue getMaxFileHistogramSequences() {
		return maxFileHistogramSequences;
	}

	public static String getAliViewHelpWebPage() {
		return ALIVIEW_HELP_URL;
	}

	public static SettingValue getFontCase() {
		return fontCase;
	}

}
