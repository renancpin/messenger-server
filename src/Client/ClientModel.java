package Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ClientModel {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private Connection connection;
    private Session queueSession;
    private Session topicSession;

    private Map<String, MessageConsumer> topicConsumers = new HashMap<>();

    public void initialize() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createConnection();
        connection.start();

        queueSession = connection.createSession(Session.AUTO_ACKNOWLEDGE);
        topicSession = connection.createSession(Session.AUTO_ACKNOWLEDGE);
    }

    public void stop() {
        try {
            queueSession.close();
            topicSession.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------- QUEUES -----------
    public void sendMessageToQueue(String queueName, String message) throws JMSException {
    	if (queueName == null || queueName.equals("")) {
    		return;
    	}
    	
        Destination destination = queueSession.createQueue(queueName);
        MessageProducer producer = queueSession.createProducer(destination);
        producer.send(queueSession.createTextMessage(message));
        producer.close();
    }

    public List<String> receiveMessagesFromQueue(String queueName) throws JMSException {
    	if (queueName == null || queueName.equals("")) {
    		return null;
    	}
    	
        Destination destination = queueSession.createQueue(queueName);
        MessageConsumer consumer = queueSession.createConsumer(destination);
        List<String> messages = new ArrayList<>();
        TextMessage message;

        do {
            message = (TextMessage) consumer.receive(10);

            if (message != null) {
                messages.add(message.getText());
            }
        } while (message != null);

        consumer.close();

        return messages;
    }

    // -------- TOPICS ----------
    public void sendMessageToTopic(String topicName, String message) throws JMSException {
    	if (topicName == null || topicName.equals("")){
    		return;
    	}
    	
        Destination destination = topicSession.createTopic(topicName);
        MessageProducer producer = topicSession.createProducer(destination);
        producer.send(topicSession.createTextMessage(message));
        producer.close();
    }

    public void subscribeToTopic(String topicName, BiConsumer<String, String> listener) throws JMSException {
    	if (topicName == null || topicName.equals("")){
    		return;
    	}
    	
    	Destination destination = topicSession.createTopic(topicName);
        MessageConsumer consumer = topicSession.createConsumer(destination);

        topicConsumers.put(topicName, consumer);

        MessageListener onMessage = (message) -> {
            if (message instanceof TextMessage) {
                try {
                    String lastMessage = ((TextMessage) message).getText();

                    listener.accept(lastMessage, topicName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        consumer.setMessageListener(onMessage);
    }

    public void unsubscribeFromTopic(String topicName) throws JMSException {
    	if (topicName == null || topicName.equals("")){
    		return;
    	}
    	
        MessageConsumer consumer = topicConsumers.remove(topicName);
        
        if (consumer == null) {
        	return;
        }
        
        consumer.setMessageListener(null);
    	consumer.close();
    }
}
