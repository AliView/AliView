package aliview.gui;

import java.awt.Color;

public class DrawCharBuffer{
	byte[] byteBuffer;
	Color[] fgColorBuffer;
	Color[] bgColorBuffer;
	int endPointer = 0;
	int iterPointer = 0;
	
	public DrawCharBuffer(int size) {
		byteBuffer = new byte[size];
		fgColorBuffer = new Color[size];
		bgColorBuffer = new Color[size];	
	}
	
	public void clear(){
		endPointer = 0;
	}
	
	public void append(int i, Color colVal, Color bgColor){
		byteBuffer[endPointer] = (byte)i;
		fgColorBuffer[endPointer] = colVal;
		bgColorBuffer[endPointer] = bgColor;
		endPointer ++;
	}
	
	public int length(){
		return endPointer;
	}
	
	public Color getFgColor(int pos){
		return fgColorBuffer[pos];
	}
	
	public Color getBgColor(int pos){
		return bgColorBuffer[pos];
	}
	
	public int getNextSameFGColorCount(int startPos, int maxLen){
		int start = startPos;
		Color lastColor = fgColorBuffer[startPos];
		int counter = 0;
		for(int n = start; n < endPointer; n++){
			
			if(fgColorBuffer[n] != lastColor){
				break;
			}
			if(counter == maxLen){
				break;
			}
			counter ++;
			lastColor = fgColorBuffer[n];
		}
		return counter;
	}
	
	public int getNextSameBGColorCount(int startPos, int maxLen){
		int start = startPos;
		Color lastColor = bgColorBuffer[startPos];
		int counter = 0;
		for(int n = start; n < endPointer; n++){
			
			if(bgColorBuffer[n] != lastColor){
				break;
			}
			if(counter == maxLen){
				break;
			}
			counter ++;
			lastColor = bgColorBuffer[n];
		}
		return counter;
	}
	
	public byte[] getByteBuffer(){
		return byteBuffer;
	}

	public byte getByte(int pos) {
		return byteBuffer[pos];
	}
}
