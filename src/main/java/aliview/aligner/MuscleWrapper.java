package aliview.aligner;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import utils.OSNativeUtils;


public class MuscleWrapper {

	public static void main(String[] args) {
		getMusclePath();
	}

	private static final Logger logger = Logger.getLogger(MuscleWrapper.class);

	public static File getMusclePath(){

		// get predefined path

		// if no predefined path create one for distributed program
		File localMuscleBinFile = new File(getAliViewUserDataDirectory(), "/binaries" + File.separator + getMuscleBinDependingOnOS());

		//logger.info("localMuscleBinFile.lastModified()" + localMuscleBinFile.lastModified());
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		//System.out.println("After Format : " + sdf.format(localMuscleBinFile.lastModified()));

		// check if file exist if not extract it from jar
		if(! localMuscleBinFile.exists() || localMuscleBinFile.length() == 0 || localMuscleBinFile.lastModified() < 1395868152000L){ // modified date = 20140326 22:09:12 (this is the date when saving file problem finally was resolved)
			ClassLoader cl = MuscleWrapper.class.getClassLoader();	
			try {
				boolean fileISOK = false;
				int nTries = 0;
				while(! fileISOK && nTries <=3){
					nTries ++;
					InputStream muscleStreamFromJar = cl.getResourceAsStream(localMuscleBinFile.getName());
					logger.info(muscleStreamFromJar);
					copyMuscleFileToLocalDir(muscleStreamFromJar, localMuscleBinFile);
					// reopen stream
					muscleStreamFromJar = cl.getResourceAsStream(localMuscleBinFile.getName());
					fileISOK = verifyMD5(muscleStreamFromJar, localMuscleBinFile);
					if(! fileISOK){
						FileUtils.deleteQuietly(localMuscleBinFile);
					}else{
						localMuscleBinFile.setExecutable(true);
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// TODO Maybe notify something went wrong with installing muscle
			}

		}

		logger.info(localMuscleBinFile);		
		return localMuscleBinFile;

	}


	private static boolean verifyMD5(InputStream streamFromJar,File localMuscleBinFile) throws FileNotFoundException {
		boolean checksumOK = false;
		String checksum1 = checkSum(streamFromJar);
		String checksum2 = checkSum(new FileInputStream(localMuscleBinFile));
		logger.info(checksum1);
		logger.info(checksum2);
		if(checksum1 != null && checksum2 != null && checksum1.equals(checksum2)){
			checksumOK = true;
		}
		return checksumOK;
	}


	private static void copyMuscleFileToLocalDir(InputStream muscleStreamFromJar, File localMuscleBinFile) throws IOException {
		FileUtils.forceMkdir(localMuscleBinFile.getParentFile());
		FileUtils.copyInputStreamToFile(muscleStreamFromJar, localMuscleBinFile);		
	}


	public static final String getAliViewUserDataDirectory() {
		return System.getProperty("user.home") + File.separator + ".AliView";
	}



	public static final String getMuscleBinDependingOnOS() {

		String binName = "";
		// 64-bit
		if(OSNativeUtils.is64BitOS()){
			if(OSNativeUtils.isMac()){
				binName = "muscle3.8.31_i86darwin64";
			}
			else if(OSNativeUtils.isLinuxOrUnix()){
				binName = "muscle3.8.425_i86linux64";
			}
			// default
			else{
				binName = "muscle3.8.425_win32.exe";
			}
			// 32-bit
		}else{
			if(OSNativeUtils.isMac()){
				if(OSNativeUtils.isPowerPC()){
					binName = "muscle3.8.31_macppc";
				}
				else{
					binName = "muscle3.8.31_i86darwin32";
				}
			}
			else if(OSNativeUtils.isLinuxOrUnix()){
				binName = "muscle3.8.425_i86linux32";
			}
			// default
			else{
				binName = "muscle3.8.425_win32.exe";
			}
		}

		return binName;

	}

	/*
	 * Calculate checksum of a File using MD5 algorithm
	 */
	public static String checkSum(InputStream instream){
		String checksum = null;

		try {
			BufferedInputStream bis = new BufferedInputStream(instream);
			MessageDigest md = MessageDigest.getInstance("MD5");

			//Using MessageDigest update() method to provide input
			byte[] buffer = new byte[8192];
			int numOfBytesRead;
			while( (numOfBytesRead = bis.read(buffer)) > 0){
				md.update(buffer, 0, numOfBytesRead);
			}
			byte[] hash = md.digest();
			checksum = new BigInteger(1, hash).toString(16); //don't use this, truncates leading zero
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return checksum;
	}


}


