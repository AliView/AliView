package utils;

import java.awt.Component;

public class DialogUtils {
	
	private static Component dialogParent;
	
	public static void init(Component dialogP){
		dialogParent = dialogP;
	}
	
	public static final Component getDialogParent(){
		return dialogParent;
	}
	
	

}
