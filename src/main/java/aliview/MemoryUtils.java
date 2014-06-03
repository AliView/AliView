package aliview;

import org.apache.log4j.Logger;

public class MemoryUtils {
	private static final Logger logger = Logger.getLogger(MemoryUtils.class);
	static final int MB = 1024*1024;
	
	public static long getMaxMem() {
		Runtime runtime = Runtime.getRuntime();
		return runtime.maxMemory();
	}
	
	public static long getMaxMemMB(){
		return getMaxMem() / MB;
	}
	
	public static long getTotalMemMB(){
		Runtime runtime = Runtime.getRuntime();
		return runtime.totalMemory() / MB;
	}
	
	public static long getFreeMemMB(){
		Runtime runtime = Runtime.getRuntime();
		return runtime.freeMemory() / MB;
	}
	
	public static long getUsedMemMB(){
		Runtime runtime = Runtime.getRuntime();
		return (runtime.totalMemory() - runtime.freeMemory()) /MB;
	}

	public static void logMem(){
		logger.info("getMaxMemMB()" + getMaxMemMB());
		logger.info("getTotalMemMB()" + getTotalMemMB());
		logger.info("getFreeMemMB()" + getFreeMemMB());
		logger.info("getUsedMemMB()" + getUsedMemMB());
	}	
	
	public static double getPresumableFreeMemoryMB(){
		double presumableFreeMemory= Runtime.getRuntime().maxMemory() - getAllocatedMemory();
		return presumableFreeMemory / MB;

	}
	
	public static double getAllocatedMemory(){
		double allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
		return allocatedMemory;
	}
}
