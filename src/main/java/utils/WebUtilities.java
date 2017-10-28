package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;
//import org.apache.commons.codec.binary.Base64;

public class WebUtilities {
	private static final Logger logger = Logger.getLogger(WebUtilities.class);

	private static String cachedURLString;
	private static String cachedPage;

	public static String getPageSubstring(String surl, String startTag, String endTag, String username, String password)throws IOException{
		return getPageSubstring(surl, startTag, endTag, null, username, password);
	}		

	public static String getPageSubstring(String surl, String startTag, String endTag, String cookie) throws IOException{

		return getPageSubstring(surl, startTag, endTag, cookie);

	}

	public static String getPageSubstring(String surl, String startTag, String endTag) throws IOException{

		return getPageSubstring(surl, startTag, endTag, null, null, null);

	}

	public static String getPageNestedSubstring(String surl, String outerStartTag, String outerEndTag, String innerStartTag, String innerEndTag) throws IOException{
		return getPageNestedSubstring(surl, outerStartTag, outerEndTag, innerStartTag, innerEndTag, null, null, null);
	}

	public static String getPageNestedSubstrings(String surl, String outerStartTag, String outerEndTag, String innerStartTag, String innerEndTag) throws IOException{
		return getPageNestedSubstrings(surl, outerStartTag, outerEndTag, innerStartTag, innerEndTag, null, null, null);
	}

	public static String getPageNestedSubstrings(String surl, String outerStartTag, String outerEndTag, String innerStartTag, String innerEndTag, String cookie, String username, String password) throws IOException{

		String page;
		if(cachedURLString == null || !surl.equalsIgnoreCase(cachedURLString)){
			page = readPageIntoString(surl,cookie,username, password);
			cachedPage = page;
			cachedURLString = surl;	
		}
		else{
			page = cachedPage;
		}


		String outerSubstring = StringUtils.substringBetween(page, outerStartTag, outerEndTag);
		String[] innerSubstrings = StringUtils.substringsBetween(outerSubstring, innerStartTag, innerEndTag);
		String allStrings = "";
		String DELIMITER = ",";
		if(innerSubstrings != null){
			for(String substring: innerSubstrings){
				allStrings = allStrings + StringUtils.trim(substring) +  DELIMITER;
			}

			// remove last delimiter
			allStrings = StringUtils.removeEnd(allStrings, DELIMITER);

		}


		return allStrings;

	}

	private static String removeBlanksAndNbsp(String theString) {
		theString = StringUtils.remove(theString, "&nbsp;");
		theString = StringUtils.trim(theString);
		return theString;
	}


	public static String getPageNestedSubstring(String surl, String outerStartTag, String outerEndTag, String innerStartTag, String innerEndTag, String cookie, String username, String password) throws IOException{

		String page;
		if(cachedURLString == null || !surl.equalsIgnoreCase(cachedURLString)){
			page = readPageIntoString(surl,cookie,username, password);
			cachedPage = page;
			cachedURLString = surl;	
		}
		else{
			page = cachedPage;
		}


		String outerSubstring = StringUtils.substringBetween(page, outerStartTag, outerEndTag);
		String innerSubstring = StringUtils.substringBetween(outerSubstring, innerStartTag, innerEndTag);

		return innerSubstring;

	}

	public static String getPageSubstring(String surl, String startTag, String endTag, String cookie, String username, String password) throws IOException{

		String page;
		if(cachedURLString == null || !surl.equalsIgnoreCase(cachedURLString)){
			page = readPageIntoString(surl,cookie,username, password);
			cachedPage = page;
			cachedURLString = surl;	
		}
		else{
			page = cachedPage;
		}

		String pageSubstring = StringUtils.substringBetween(page, startTag, endTag);

		return pageSubstring;

	}

	public static String readPageIntoString(String surl, String cookie, String username, String password) throws IOException{

		Authenticator.setDefault(new MyAuthenticator(username,password));	

		URL url = new URL(surl);
		URLConnection conn = url.openConnection();

		// Set cookie if provided
		if(cookie != null){
			conn.setRequestProperty("Cookie", cookie);
		}

		InputStream is = conn.getInputStream();

		// Read page into a string
		//String pageAsString = IOUtils.toString(is, "ISO-8859-1");
		String pageAsString = IOUtils.toString(is, "UTF-8");


		return pageAsString;
	}

	public static void readURLIntoFile(URL url, File file, String cookie, String username, String password) throws IOException{

		Authenticator.setDefault(new MyAuthenticator(username,password));

		URLConnection conn = url.openConnection();

		// Set cookie if provided
		if(cookie != null){
			conn.setRequestProperty("Cookie", cookie);
		}

		InputStream is = conn.getInputStream();

		logger.info(url.toString());

		FileUtils.copyURLToFile(url, file);

		//String pageAsString = IOUtils.toString(is, "ISO-8859-1");
		String pageAsString = IOUtils.toString(is, "UTF-8");

	}


	static class MyAuthenticator extends Authenticator {

		private static String user;
		private static String passw;

		public MyAuthenticator(String username, String password) {
			user = username;
			passw = password;
		}

		public PasswordAuthentication getPasswordAuthentication () {
			return new PasswordAuthentication (user, passw.toCharArray());
		}

	}

}
