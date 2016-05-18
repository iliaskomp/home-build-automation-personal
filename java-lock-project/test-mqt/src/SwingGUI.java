import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;


public class SwingGUI implements ActionListener{

	JButton jbLock;
	JButton jbUnlock;
	JLabel jLab;
	JTextArea textIP;

	Paho paho;
	
	public SwingGUI(Paho paho){
		
		this.paho = paho; 
		
		// Create frame and set some options.
		JFrame f = new JFrame("MQTT");
		f.setLayout(new FlowLayout());
		f.setSize(150, 150);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Text area for IP
		textIP = new JTextArea("192.168.3.10");
		f.add(textIP);
		
		// Button for locking the door
		jbLock = new JButton("Lock");
		jbUnlock = new JButton("Unlock");
		f.add(jbLock);
		
		// Button for unlocking the door
		jbLock.addActionListener(this);
		jbUnlock.addActionListener(this);
		f.add(jbUnlock);
		
		// Text Label for the current status of the door
		jLab = new JLabel("Door is locked.");
		f.add(jLab);
		
		f.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e){

		if (e.getSource().equals(jbLock)){
			
			paho.doDemo("tcp://" + textIP.getText() ,"0");
			jLab.setText("Door is locked");	
						
		} else if (e.getSource().equals(jbUnlock)){

			paho.doDemo("tcp://" + textIP.getText() ,"1");
			jLab.setText("Door is unlocked");	

		}		
	}
}
