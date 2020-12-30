import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class DataTopic {
    MqttClient mqttClient; //Client
    String subscribeTopic = "DK/sensor";
    String mqttBroker = "tcp://broker.hivemq.com:1883";
    String clientId = "readerData";

    DataTopic() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(mqttBroker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + mqttBroker);
            mqttClient.connect(connOpts);
            System.out.println("Connected and listening to topic: " + subscribeTopic);
            mqttClient.subscribe(subscribeTopic, new MqttPostPropertyMessageListener());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    class MqttPostPropertyMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String topic, MqttMessage content) throws IOException {
            Date date = new Date();
            String receivedContent = topic + ", " + content.toString();
            System.out.println(date + ": " + receivedContent);
            FileWriter fw = new FileWriter("src/Temp.txt", true);
            fw.write(date + ", " + receivedContent + "\n");
            fw.close();
        }
    }

    public static void main(String[] args) {
        new DataTopic ();
    }
}