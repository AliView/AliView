package aliview.gui;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import aliview.AliViewWindow;

public class AppIcons {
	private static final Logger logger = Logger.getLogger(AppIcons.class);

	//public static ImageIcon clearIcon = new ImageIcon("http://openiconlibrary.sourceforge.net/gallery2/?./Icons/actions/edit-clear-2.png");

	public static ImageIcon getClearIcon(){
		return createIcon("/img/appicons/16x16/edit-clear-2.png");
	}

	public static ImageIcon getNewIcon(){
		return createIcon("/img/appicons/16x16/new.png");
	}

	public static ImageIcon getQuitIcon(){
		return createIcon("/img/appicons/16x16/application-exit.png");
	}

	public static ImageIcon getUndoIcon(){
		return createIcon("/img/appicons/16x16/edit-undo.png");
	}

	public static ImageIcon getGoTopIcon(){
		return createIcon("/img/appicons/16x16/go-top.png");
	}

	public static ImageIcon getGoBottomIcon(){
		return createIcon("/img/appicons/16x16/go-bottom.png");
	}

	public static ImageIcon getGoUpIcon(){
		return createIcon("/img/appicons/16x16/go-up.png");
	}

	public static ImageIcon getGoDownIcon(){
		return createIcon("/img/appicons/16x16/go-down.png");
	}

	public static ImageIcon getColorsIcon() {
		return createIcon("/img/appicons/16x16/stock_color.png");
	}


	public static ImageIcon getHighlightConsIcon() {
		return createIcon("/img/appicons/highlightCons22x22_transp.png");
	}

	public static ImageIcon getHighlightNonConsIcon() {
		return createIcon("/img/appicons/highlightNonCons22x22_transp.png");
		//return createIcon("/img/appicons/highlight_nonCons22x22_v2.png");
	}

	public static ImageIcon getTranslateIcon() {
		return createIcon("/img/appicons/translate22x22.png");
	}

	public static ImageIcon getDiffIcon() {
		return createIcon("/img/appicons/diff22x22.png");
	}


	public static ImageIcon getHighlightConsIconLG() {
		return createIcon("/img/appicons/hlight_cons_28.png");
	}

	public static ImageIcon getHighlightNonConsIconLG() {
		return createIcon("/img/appicons/hlight_diff_28.png");
		//return createIcon("/img/appicons/highlight_nonCons22x22_v2.png");
	}

	public static ImageIcon getTranslateIconLG() {
		return createIcon("/img/appicons/translate_28.png");
	}

	public static ImageIcon getDiffIconLG() {
		return createIcon("/img/appicons/trace_28.png");
	}

	public static ImageIcon getTransOnePosIcon() {
		return createIcon("/img/appicons/transOnePos22x22.png");
		//return createIcon("/img/appicons/highlight_nonCons22x22_v2.png");
	}

	public static ImageIcon getCoding1Icon() {
		return createIcon("/img/appicons/select1Codon22x22.png");
	}

	public static ImageIcon getCoding2Icon() {
		return createIcon("/img/appicons/select2Codon22x22.png");
	}

	public static ImageIcon getCoding3Icon() {
		return createIcon("/img/appicons/select3Codon22x22.png");
	}

	public static ImageIcon getCodingNoneIcon() {
		return createIcon("/img/appicons/setNonCoding22x22.png");
	}

	public static ImageIcon getShowCodonIcon() {
		return createIcon("/img/appicons/showCodon22x22.png");
	}

	public static ImageIcon getIncFontSize() {
		return createIcon("/img/appicons/incFont22x22.png");
	}

	public static ImageIcon getDecFontSize() {
		return createIcon("/img/appicons/decFont22x22.png");
	}

	public static ImageIcon getShowAACodeIcon() {
		return createIcon("/img/appicons/aaCode_22x22.png");
	}

	public static ImageIcon getCountCodonIcon() {
		return createIcon("/img/appicons/16x16/countCodon.png");
	}

	public static ImageIcon getRedoIcon(){
		return createIcon("/img/appicons/16x16/edit-redo.png");
	}

	public static Image getProgramIconImage() {
		ImageIcon icon = createIcon("/img/alignment_ico_128x128.png");
		logger.debug("icon" + icon);
		return icon.getImage();
	}


	public static ImageIcon getAlignIcon(){
		return createIcon("/img/appicons/16x16/align_format-indent-less.png");
	}

	public static ImageIcon getMoveRightIcon() {
		return createIcon("/img/appicons/16x16/go-next.png");	
	}

	public static ImageIcon getMoveLeftIcon() {
		return createIcon("/img/appicons/16x16/go-previous.png");
	}

	private static ImageIcon createIcon(String fileName) {

		URL resource = AliViewWindow.class.getResource(fileName);
		if(resource != null){
			Image image = Toolkit.getDefaultToolkit().getImage(resource);
			return new ImageIcon(image);
		}else{
			return new ImageIcon();
		}
	}









}
