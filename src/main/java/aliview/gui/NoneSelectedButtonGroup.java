package aliview.gui;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;

public class NoneSelectedButtonGroup extends ButtonGroup {
	private AbstractButton hack;

	public NoneSelectedButtonGroup() {
		super();
		hack = new JButton();
		add(hack);
	}

	@Override
	public void setSelected(ButtonModel model, boolean selected) {
		super.setSelected(selected ? model : hack.getModel(), true);
	}
}