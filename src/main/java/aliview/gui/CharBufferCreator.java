package aliview.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;

import org.apache.log4j.Logger;

public class CharBufferCreator {	
	private static final Logger logger = Logger.getLogger(CharBufferCreator.class);
	private DrawCharBuffer template;
	private ArrayList<DrawCharBuffer> buffers = new ArrayList<DrawCharBuffer>(10);
	BitSet donePos = new BitSet();
	private static final byte SPACE = 32;
	

	public CharBufferCreator(DrawCharBuffer template){
		this.template = template;
	}
	
	public ArrayList<DrawCharBuffer> createFGBuffers(){
		
		while(donePos.cardinality() < template.length()){	
			int nextPos = donePos.nextClearBit(0);
			if(nextPos == -1){
				break;
			}
			buffers.add(createThisColorBuffer(template.fgColorBuffer[nextPos]));		
		}
		return buffers;
	}
	
	public HashSet<Color> getUniqueBGColors(){
		HashSet<Color> unique = new HashSet<Color>();
		for(int n = 0; n < template.length(); n++){
			unique.add(template.getBgColor(n));
		}	
		return unique;
	}
	
	public ArrayList<Color> getListOfUniqueBGColors(){
		ArrayList<Color> unique = new ArrayList<Color>();
		while(donePos.cardinality() < template.length()){	
			int nextPos = donePos.nextClearBit(0);
			if(nextPos == -1){
				break;
			}
			unique.add(template.bgColorBuffer[nextPos]);
			markThisColor(template.bgColorBuffer[nextPos]);		
		}
		return unique;
	}
	
	private void markThisColor(Color color){
		for(int n = 0; n < template.length(); n++){
			if(template.bgColorBuffer[n] == color){
				donePos.set(n);
			}
		}	
	}
	
	private DrawCharBuffer createThisColorBuffer(Color color){
		DrawCharBuffer buff = new DrawCharBuffer(template.length());
		for(int n = 0; n < template.length(); n++){
			if(template.fgColorBuffer[n] == color){
				buff.append(template.byteBuffer[n], color, null);
				donePos.set(n);
			}
			else{
				buff.append(SPACE, color, null);
			}
		}	
		return buff;
	}
	
	

}
