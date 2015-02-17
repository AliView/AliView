package utils.nexus;

import org.apache.log4j.Logger;

public class CodonPos {
	private static final Logger logger = Logger.getLogger(CodonPos.class);
		public int startPos;
		public int endPos;
		public boolean isCoding;
		
		public CodonPos(int startPos, int endPos, boolean isCoding){
			this.startPos = startPos;
			this.endPos = endPos;
			this.isCoding = isCoding;
		}
		
		public void addEndPos(int x) {
			this.endPos = x;
		}

		public boolean isOrfan(){
			if(endPos - startPos != 2){ // one less than three because end is inclusive
				return true;
			}
			return false;
		}
		
		public boolean isCoding(){
			return isCoding;
		}

		public void setIsCoding(boolean b) {
			isCoding = b;
		}
}


