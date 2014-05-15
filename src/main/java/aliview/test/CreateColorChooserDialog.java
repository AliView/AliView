package aliview.test;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;

public class CreateColorChooserDialog {

	private static final long serialVersionUID = 1L;

	private static void createAndShowGUI() {

		// Create and set up the window.
		final JFrame frame = new JFrame("Centered");

		// Display the window.
		frame.setSize(200, 200);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// set flow layout for the frame
		frame.getContentPane().setLayout(new FlowLayout());

		JButton button = new JButton("Choose color");
		button.setSize(50, 50);

		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(frame, "Choose a color", Color.blue);
				System.out.println("The selected color was:" + color);
			}
		});

		frame.getContentPane().add(button);

	}

	public static void main(String[] args) {

  //Schedule a job for the event-dispatching thread:

  //creating and showing this application's GUI.

  javax.swing.SwingUtilities.invokeLater(new Runnable() {

public void run() {

    createAndShowGUI(); 

}

  });
    }

}