package aliview.messenges;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;


/*Provides a confirm dialog with a checkbox*/
public class JOptionPaneWithCheckbox extends JOptionPane{


	/*In your own code, create a JCheckbox with the required text, eg
JCheckBox dontAskBox = new JCheckBox("Don't ask this again");
Then call this method like the ones in JOptionPane.
Parameter optionType is
for example YES_NO_CANCEL_OPTION as in JOptionPane.
Because the checkbox was created in your own code, you can check anytime
whether it is selected or not:
if (dontAskBox.isSelected()) {xxxxx}
or: boolean dontAsk = dontAskBox.isSelected();
	 */



	public JOptionPaneWithCheckbox(JCheckBox cbx, String text, int messageType, int optionType) {
		super(new Object[]{text," ",cbx}, messageType, optionType);
	}

	public JOptionPaneWithCheckbox(JCheckBox cbx, String text, int messageType) {
		// TODO change into JTextArea without border and editable - to make text selectable
		//super(new Object[]{new JTextArea(text)," ", cbx}, messageType);
		super(new Object[]{text," ", cbx}, messageType);
	}

	/*
	public static int showConfirmDialogWithCheckBox(JCheckBox dontAskBox, Object message, String title, int optionType) {


		Object[] params = ;

		int reply = showConfirmDialog (new JFrame(),
				params,
				title,
				optionType);

		return reply;
	 */
} 