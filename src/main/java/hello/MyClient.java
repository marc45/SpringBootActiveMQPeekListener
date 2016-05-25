package hello;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.jms.*;

public class MyClient {

    //default broker url: failover://tcp://localhost:61616
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    //name of the queue we will receive messages from
    private static final String queue = "IN.RD.POC";

    public static void main(String[] args) throws JMSException, InterruptedException {

        //getting jms connection from server
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        //create session for sending messages
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Queue destination = session.createQueue(queue);

        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
                System.out.println("message received: " + activeMQTextMessage.getMessageId());

                boolean succesfull = false;

                if(succesfull) {
                    try {
                        message.acknowledge();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        MessageProducer producer = session.createProducer(destination);
        ActiveMQTextMessage message = new ActiveMQTextMessage();

        for (int i = 0; i < 10; i++) {
            message.setText("TestMessage (" + i + ")");
            producer.send(destination, message);
        }
    }
}
