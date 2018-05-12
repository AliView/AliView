package utils;

import java.awt.Desktop;
import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import aliview.AliView;

/*
 * Heavily inspired by org.openstreetmap.josm.tools.PlatformHookOsx.java from JOSM project
 * License, GPL 
 * 
 * Creating handlers are done with reflection so that Java 1.5 still is working as minimal version
 * 
 */
public class OSXHandlerJava9 implements InvocationHandler {

	private static final Logger logger = Logger.getLogger(OSXHandlerJava9.class.getName());

	public OSXHandlerJava9(){
		try {
			Class<?> quitHandler = Class.forName("java.awt.desktop.QuitHandler");
			Class<?> aboutHandler = Class.forName("java.awt.desktop.AboutHandler");
			Class<?> openFilesHandler = Class.forName("java.awt.desktop.OpenFilesHandler");
			Class<?> preferencesHandler = Class.forName("java.awt.desktop.PreferencesHandler");
			Object proxy = Proxy.newProxyInstance(OSXHandlerJava9.class.getClassLoader(), new Class<?>[] {
				quitHandler, aboutHandler, openFilesHandler, preferencesHandler}, this);
			setHandlers(Desktop.class, quitHandler, aboutHandler, openFilesHandler, preferencesHandler, proxy, Desktop.getDesktop());

		} catch (Exception ex) {
			logger.error("Failed to register with OSX: " + ex);
		}
	}

	/**
	 * Registers Apple handlers.
	 * @param appClass application class
	 * @param quitHandler quit handler class
	 * @param aboutHandler about handler class
	 * @param openFilesHandler open file handler class
	 * @param preferencesHandler preferences handler class
	 * @param proxy proxy
	 * @param appInstance application instance (instance of {@code appClass})
	 * @throws IllegalAccessException in case of reflection error
	 * @throws InvocationTargetException in case of reflection error
	 * @throws NoSuchMethodException if any {@code set*Handler} method cannot be found
	 */
	protected void setHandlers(Class<?> appClass, Class<?> quitHandler, Class<?> aboutHandler,
			Class<?> openFilesHandler, Class<?> preferencesHandler, Object proxy, Object appInstance)
					throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		appClass.getDeclaredMethod("setQuitHandler", quitHandler).invoke(appInstance, proxy);
		appClass.getDeclaredMethod("setAboutHandler", aboutHandler).invoke(appInstance, proxy);
		appClass.getDeclaredMethod("setOpenFileHandler", openFilesHandler).invoke(appInstance, proxy);
		appClass.getDeclaredMethod("setPreferencesHandler", preferencesHandler).invoke(appInstance, proxy);
	}

	@SuppressWarnings("unchecked")
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		logger.debug("OSX handler: {0} - {1}" +  method.getName() + " arr to string " + Arrays.toString(args));

		String methodName = method.getName();

		if("openFiles".equalsIgnoreCase(methodName)){
			logger.info("Handle openFiles");
			if (args[0] != null) {
				try {
					Object oFiles = args[0].getClass().getMethod("getFiles").invoke(args[0]);
					if (oFiles instanceof List) {
						AliView.doMacOpenFiles((List<File>) oFiles);

					}
				} catch (Exception ex) {
					logger.warn("Failed to access open files event: " + ex);
				}
			}
		}
		else if("handleQuitRequestWith".equalsIgnoreCase(methodName)){
			logger.info("Handle handleQuitRequestWith");
			boolean isNotInterruptedByUser = AliView.doMacQuit();

			// I don't know if I really need this code section since AliView.doMacQuit() anyway will do an exit.
			if (args[1] != null) {
				try {
					args[1].getClass().getDeclaredMethod(isNotInterruptedByUser ? "performQuit" : "cancelQuit").invoke(args[1]);
				} catch (IllegalAccessException e) {
					logger.debug(e);
					// with Java 9, module java.desktop does not export com.apple.eawt, use new Desktop API instead
					Class.forName("java.awt.desktop.QuitResponse").getMethod(isNotInterruptedByUser ? "performQuit" : "cancelQuit").invoke(args[1]);
				}
			}
		}
		else if("handleAbout".equalsIgnoreCase(methodName)){
			logger.info("Handle handleAbout");
			AliView.doMacAbout();
		}

		else if("handlePreferences".equalsIgnoreCase(methodName)){
			logger.info("Handle handlePreferences");
			AliView.doMacPreferences();
		}

		else{
			logger.warn("OSX unsupported method: " + method.getName());
		}
		return null;
	}

}
