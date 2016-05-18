
public class Demo {

	public static void main(String[] args) {
		
		// Create new paho instance
		Paho paho = new Paho();
		
		// Create gui and pass the paho instance
		new SwingGUI(paho);
	}

}
