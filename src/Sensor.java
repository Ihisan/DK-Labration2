import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class Sensor {
    String topic;
    String mqttBroker;
    String clientId;
    MqttClient mqttClient;
    Timer timer;
    int secondsForTimer = 10;
    long delay = secondsForTimer * 1000L;

    Sensor() {
        connectToBroker ();
        timer = new Timer ();
        timer.schedule (new TimerToDo (), delay);
    }

    public static void main(String[] args) {
        new Sensor ();
    }

    void connectToBroker() {
        try {
            topic = "DK/sensor";
            mqttBroker = "tcp://broker.hivemq.com:1883";
            clientId = "TempSensor";
            MemoryPersistence persistence = new MemoryPersistence ();
            mqttClient = new MqttClient (mqttBroker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions ();
            connOpts.setCleanSession (true);
            System.out.println ("Connecting to broker: " + mqttBroker);
            mqttClient.connect (connOpts);
            System.out.println ("Connected and writing to topic: " + topic);
        } catch (MqttException e) {
            e.printStackTrace ();
        }
    }

    String getStringDegree() {
        int degree = (int) (Math.random () * 10) + 15;
        return String.valueOf (degree);
    }

    class TimerToDo extends TimerTask {
        @Override
        public void run() {
            try {
                String temp = "temperature, " + getStringDegree () + "°C";
                MqttMessage message = new MqttMessage (temp.getBytes (StandardCharsets.UTF_8));
                message.setQos (2);
                mqttClient.publish (topic, message);
                System.out.println ("Sent temperature: " + temp);
            } catch (MqttException e) {
                e.printStackTrace ();
            }
            timer.cancel ();
            timer = new Timer ();
            timer.schedule (new TimerToDo (), delay);
            System.out.println ("Created new timer");
        }
    }
}