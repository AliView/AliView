package aliview.utils;

import java.awt.Rectangle;

public class Utils {
	
	public static Rectangle addRects(Rectangle rect1, Rectangle rect2){
		if(rect1 != null && rect2 != null){
			Rectangle newRect = new Rectangle(rect1);
			newRect.add(rect2);
			return newRect;	
		}else if(rect1 != null){
			return new Rectangle(rect1);
		}
		else if(rect2 != null){
			return new Rectangle(rect2);
		}
		else{
			return null;
		}
	}
	
	public static boolean hasSameBounds(Rectangle rect1, Rectangle rect2){
		if(rect1 != null && rect2 != null){
			if(rect1.x != rect2.x){
				return false;
			}
			if(rect1.y != rect2.y){
				return false;
			}
			if(rect1.width != rect2.width){
				return false;
			}
			if(rect1.height != rect2.height){
				return false;
			}
			return true;
		}
		else{
			return false;
		}
	}

}
