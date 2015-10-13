package utils

import javax.imageio.ImageIO
import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowEvent

/**
 *
 * java图像显示当时 设置
 */

public class ImageLoader extends JFrame {

	static final int DEFAULT_WIDTH = 200;
	static final int DEFAULT_HEIGHT = 180;

	JTextField usernameInput
	JButton button
	static String code

	ImageLoader(String imagePath) {
		super()
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setLayout(null)
		centered(this)
        def imageFile=ImageIO.read(new File(imagePath));
		ImageIcon image = new ImageIcon(imageFile)
		JLabel label = new JLabel(image)
//		label.setIcon(image)
		label.setBounds(new Rectangle(30, 0, 180, 60))
		add(label)
		usernameInput = new JTextField();
		usernameInput.setBounds(new Rectangle(10, 80, 160, 20))
		add(usernameInput)
		button = new JButton("确定")
		button.addActionListener(new ButtonListen())
		button.setBounds(new Rectangle(60, 115, 60, 25))
		add(button)
		setVisible(true)
		enableEvents(AWTEvent.WINDOW_EVENT_MASK)
	}

	public void centered(Container container) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		int w = container.getWidth();
		int h = container.getHeight();
		container.setBounds((int) ((screenSize.width - w) / 2),
				(int) ((screenSize.height - h) / 2), w, h)
	}

	public String getValue() {
		code = usernameInput.getText()
	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			getValue()
			if (code?.empty)
				code = "-1"
			dispose()
		}
		super.processWindowEvent(e);
	}


	class ButtonListen implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			getValue()
			if (code?.empty)
				code = "-1"
			dispose()
		}
	}
}