package WriteREad;

import org.eclipse.paho.client.mqttv3.*;

import java.util.UUID;

public class MyMqtt implements MqttCallback {

    private MqttClient myClient;
    private MqttConnectOptions connectOptions;

    private static final String BROKER_URL = "";

    private static final String MY_MQTT_CLIENT_ID = UUID.randomUUID().toString();

    static final String DEFAULT_TOPIC = "training/Silviu/SolarEdge/test";

    private static final boolean PUBLISHER = true;
    private static final boolean SUBSCRIBER = false;

    private static final int RETRIES = 3;

    /**
     * @param args
     */
//    public static void main(String[] args) throws Exception {
//
//        MyMqttApp app = new MyMqttApp();
//        app.runClient();
//        app.sendMessage(DEFAULT_TOPIC, "Hello From My MQTT APP");
//        Thread.sleep(200);
//        app.stopClient();
//
//    }

    /**
     * @throws MqttException
     */
    public MyMqtt() throws MqttException {
        myClient = new MqttClient(BROKER_URL, MY_MQTT_CLIENT_ID);
        myClient.setCallback(this);
    }

    public void runClient() {

        connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setKeepAliveInterval(100);

        try {

            System.out.println("Attempting Connection to " + BROKER_URL);
            myClient.connect(connectOptions);
            System.out.println("Connected to " + BROKER_URL);


        } catch (MqttException me) {

            System.err.println(me.getMessage());
            System.err.println(me.getStackTrace());
            System.exit(-1);
        }

    }

    @Override
    public void connectionLost(Throwable arg0) {
        // TODO Auto-generated method stub
        System.out.println("Connection lost!");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        System.out.println("Devliery completed with token ::");
        System.out.println("Message Id :: " + arg0.getMessageId());
        System.out.println("Response :: " + arg0.getResponse().toString());

    }

    @Override
    public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
        System.out.println("Recieved Message :: -----------------------------");
        System.out.println("| Topic:" + arg0);
        System.out.println("| Message: " + new String(arg1.getPayload()));
        System.out.println("End ---------------------------------------------");
    }

    public void sendMessage(String topic, String message) throws InterruptedException {

        System.out.println("Building message with " + message.getBytes().length + "bytes of payload");
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(0);
        mqttMessage.setRetained(false);

        MqttTopic mqttTopic = myClient.getTopic(topic);

        MqttDeliveryToken token = null;

        try {
            token = mqttTopic.publish(mqttMessage);
            Thread.sleep(100);
            token.waitForCompletion();
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (null != token) {
            System.out.println("Published with Token ::");
            System.out.println(token.getMessageId());
        }
    }

    public void stopClient() throws MqttException {
        myClient.disconnect();
        System.out.println("Disonnected from " + BROKER_URL);
        System.exit(0);
    }
}
