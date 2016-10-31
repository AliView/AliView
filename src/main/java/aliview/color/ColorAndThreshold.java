package aliview.color;

import java.awt.Color;

import org.apache.log4j.Logger;

import aliview.AliView;

public class ColorAndThreshold {
	private static final Logger logger = Logger.getLogger(ColorAndThreshold.class);
	public Color color;
	public ClustalThreshold threshold;
	
	public ColorAndThreshold(Color color, ClustalThreshold threshold) {
		
		if(threshold == null){
			logger.debug("threshold" + threshold);
			logger.debug("exit here:");
			System.exit(1);
		}
		
		this.color = color;
		this.threshold = threshold;
	}
	
	@Override
	public String toString() {
		return "ClustalThreshold: " + threshold + " Color: " + color;
	}
	
}
