package hello;


import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Test;

import javax.jms.*;

public class SenderTest {

    private static final String IN_RD_POC = "IN.RD.POC";
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL; //default broker url: failover://tcp://localhost:61616
    private Queue queue_IN_RD_POC;
    private MessageProducer producer;

    @Before
    public void init() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            queue_IN_RD_POC = session.createQueue(IN_RD_POC);
            producer = session.createProducer(queue_IN_RD_POC);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendToIN_RD_POCTest() {
        try {
            ActiveMQTextMessage message = new ActiveMQTextMessage();
            for (int i = 0; i < 10; i++) {
                message.setText("TestMessage (" + i + ")");
                producer.send(queue_IN_RD_POC, message);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
