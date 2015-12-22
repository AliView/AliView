package aliview.alignment;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import aliview.AliView;

public class AlignmentFile extends File{

	private static final Logger logger = Logger.getLogger(AlignmentFile.class);
	private static final String TMP_FILE_PREFIX = "aliview-tmp-";
	
	public AlignmentFile(File file) {
		super(file.getAbsolutePath());
	}
	
	public static AlignmentFile createAliViewTempFile(String name, String suffix) throws IOException {
		String nameWithTmpPrefix = TMP_FILE_PREFIX + name + "_";
		return new AlignmentFile(File.createTempFile(nameWithTmpPrefix, suffix));
	}
	
	public static AlignmentFile createUserHomeFile() {
		String pathString = System.getenv("USERPROFILE");
		if(pathString == null){
			pathString = System.getProperty("user.home");
		}
		return new AlignmentFile(new File(pathString));
	}
	
	public boolean isAliViewTempFile() {
		String tempFilePath = FilenameUtils.normalizeNoEndSeparator(FileUtils.getTempDirectoryPath());	
		return tempFilePath.equalsIgnoreCase(FilenameUtils.normalizeNoEndSeparator(this.getParent()));
	}

	public String getNameWithoutTempPrefix(){
		String name = this.getName();
		if(name == null){
			return null;
		}	
		if(name.startsWith(TMP_FILE_PREFIX)){
			name = StringUtils.substringAfter(name, TMP_FILE_PREFIX);
		}
		return name;
	}

	public String getName() {
		return super.getName();
	}

	public String getAbsolutePath() {
		return super.getAbsolutePath();
	}

	public File getAbsoluteFile() {
		return super.getAbsoluteFile();
	}

	public String getParent() {
		return super.getParent();
	}
}
