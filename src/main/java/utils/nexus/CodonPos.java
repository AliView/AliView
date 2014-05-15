package utils.nexus;

import org.apache.log4j.Logger;

public class CodonPos {
	private static final Logger logger = Logger.getLogger(CodonPos.class);
	
		public int startPos;
		public int endPos;
		
		public CodonPos(int startPos, int endPos){
			this.startPos = startPos;
			this.endPos = endPos;
		}
		
		public void addEndPos(int x) {
			this.endPos = x;
		}

		public boolean isOrfan(){
			if(endPos - startPos == 2){
				return false;
			}else{
				return true;
			}
		}
		
	}


