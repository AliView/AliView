package aliview;

import java.net.URL;

import org.apache.log4j.Logger;

import utils.OSNativeUtils;

public class BrowserUtils {
	private static final Logger logger = Logger.getLogger(BrowserUtils.class);
	/*************************************
	 * There is no way for java to cleanly
	 * open a broswer from swing or anything
	 * else. The convention today seems to be
	 * to do the following
	 *
	 * @author gedden
	 *************************************/
		/*************************************
		 * Attempts to open a browser. Currently
		 * this method ONLY supports http.
		 *
		 * @param url
		 * @throws Exception 
		 *************************************/
		public static final void open(URL url) throws Exception
		{
			open(url.toString());
		}


		/*************************************
		 * Attempts to open a browser. Currently
		 * this method ONLY supports http.
		 *
		 * @param url
		 * @throws Exception 
		 *************************************/
		public static final void open(String URL) throws Exception
		{
			
			logger.info(OSNativeUtils.isWindows());
			
				if(OSNativeUtils.isWindows()){
					openBroswerWindows(URL);
				}
				else if(OSNativeUtils.isMac()){
					openBroswerOSX(URL);
				}
				else{
					openBroswerLINUX(URL);
				}
		}

		/*************************************
		 * Opens a browser on a windows
		 * computer
		 *
		 * @param url
		 *************************************/
		private static void openBroswerWindows(String url) throws Exception
		{
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
		}

		/*************************************
		 * Opens a browser on a mac
		 *
		 * @param url
		 *************************************/
		private static void openBroswerOSX(String url) throws Exception
		{
			Runtime.getRuntime().exec("open " + url);
		}

		/*************************************
		 * Opens a browser on linux
		 *
		 * @param url
		 *************************************/
		private static void openBroswerLINUX(String url) throws Exception
		{
			Process p = Runtime.getRuntime().exec("which firefox");
			if (p.waitFor() == 0)
			{
				Runtime.getRuntime().exec("firefox " + url);
				return;
			}

			p = Runtime.getRuntime().exec("which netscape");
			if (p.waitFor() == 0)
			{
				Runtime.getRuntime().exec("netscape " + url);
				return;
			}

			p = Runtime.getRuntime().exec("which opera");
			if (p.waitFor() == 0)
			{
				Runtime.getRuntime().exec("opera " + url);
				return;
			}
		}
	}
