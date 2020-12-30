import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ReadTopic {
    MqttClient mqttClient;
    String subscribeTopic = "DK/sensor";
    String writeTopic = "DK/sensor/data";
    String clientId = "ReadTopic";

    ReadTopic() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient("tcp://broker.hivemq.com:1883", clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + mqttClient.getServerURI());
            mqttClient.connect(connOpts);
            System.out.println("Connected and listening to topic: " + subscribeTopic);
            System.out.println("Writes to topic: " + writeTopic);
            mqttClient.subscribe(subscribeTopic, new MqttPostPropertyMessageListener());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    class MqttPostPropertyMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String topic, MqttMessage content) throws MqttException, IOException {
            int x = Integer.parseInt(content.toString().substring(13,15));
            System.out.println(x);
            System.out.println("Received - " + topic + ": " + content.toString());
            String s = "ctrl, ";
            if (x >= 22) {
                s = s + "-";
            } else {
                s = s + "+";
            }
            MqttMessage msg = new MqttMessage(s.getBytes(StandardCharsets.UTF_8));
            msg.setQos(2);
            mqttClient.publish(writeTopic, msg);
            System.out.println("Sent - " + writeTopic + ": " + s);
        }
    }

    public static void main(String[] args) {
        new ReadTopic ();
    }
}