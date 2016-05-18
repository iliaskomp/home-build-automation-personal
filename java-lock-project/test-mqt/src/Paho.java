import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

// SocketException if no network
// NoRoute if wrong ip

public class Paho {

  MqttClient client;
  
  public Paho() {}

  public void doDemo(String serverURI, String msg) {
	//String serverURI="tcp://192.168.3.10"; //default port 1883 for tcp, 8883 for ssl
	//String msg = "0"; // 0 for lock, 1 for unlock
	
	String clientId = "pahomqttpublish1";
	
    try {
    	
      client = new MqttClient(serverURI, clientId);
      client.connect();
      
      MqttMessage message = new MqttMessage();
      message.setPayload(msg.getBytes());
      
      client.publish("lockTest", message);
      client.disconnect();    
      
    } catch (MqttException e) {
      
    	e.printStackTrace();
    
    }
  }
}