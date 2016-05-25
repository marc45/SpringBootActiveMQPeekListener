package hello;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;
import javax.jms.*;
import javax.jms.Message;

@SpringBootApplication
public class MyClientSpringBoot {

    private static final String queue = "IN.RD.POC";

    @Autowired
    private ConnectionFactory connectionFactory;

    public static void main(String[] args) {
        SpringApplication.run(MyClientSpringBoot.class, args);
    }

    @Bean
    Connection connection() {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Set up a connection between spring.activemq.broker-url (see application.properties)
     * And adjust Session Acknowledge Mode to CLIENT_ACKNOWLEDGE
     * This will ensure a client/listener from a certain queue will have to acknowledge a message before it is removed from the queue
     * This will ensure a client/listener will 'peek' a message instead of 'read & delete'.
     * @throws JMSException
     */
    @PostConstruct
    public void init() throws JMSException {
        System.out.println("Init Client");
        Session session = connection().createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Queue destination = session.createQueue(queue);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(setMessageListener());
    }

    /**
     * Create a MessageListener for a MessageConsumer
     * adjust local variable boolean 'succesful' if you want to acknowledge a specific message or not
     * @return
     */
    private MessageListener setMessageListener() {
        return new MessageListener() {
            @Override
            public void onMessage(Message message) {
                ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
                System.out.println("message received: " + activeMQTextMessage.getMessageId());

                boolean succesful = false;

                if (!succesful) {
                    try {
                        System.out.println("message acknowledged");
                        message.acknowledge();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("message NOT acknowledged");
                }
            }
        };
    }
}
