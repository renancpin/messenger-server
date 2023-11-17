package Broker;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;

public class BrokerModel {
    private BrokerService broker;
    private Connection connection;
    private Session session;

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    public void initialize() throws JMSException {
        broker = new BrokerService();
//        broker.setPersistent(false);

        try {
            broker.addConnector("tcp://localhost:61616");
            broker.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new JMSException(e.getMessage());
        }

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connection = connectionFactory.createConnection();
        connection.start();

        session = connection.createSession(Session.AUTO_ACKNOWLEDGE);
    }

    public void stop() {
        try {
            session.close();
            connection.close();
            broker.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------- QUEUES -----------------
    public Destination createQueue(String queueName) throws JMSException {
    	if (queueName == null || queueName.equals("")) {
    		return null;
    	}
    	
        Destination destination = session.createQueue(queueName);
        MessageProducer producer = session.createProducer(destination);
		producer.close();
        return destination;
    }

    public void deleteQueue(String queueName) throws JMSException {
    	if (queueName == null || queueName.equals("")) {
    		return;
    	}
    	
        Destination destination = session.createQueue(queueName);
        ((ActiveMQConnection) connection).destroyDestination((ActiveMQDestination) destination);
    }

    public Map<String, Integer> getQueues() throws JMSException {
        Map<String, Integer> queues = new TreeMap<>();

        try {
            for (ActiveMQDestination destination : broker.getBroker().getDestinations()) {
                if (destination.isQueue()) {
                    List<String> messages = getQueueMessages(destination.getPhysicalName());
                    queues.put(destination.getPhysicalName(), messages.size());
                }
            }

            return queues;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getQueueMessages(String queueName) throws JMSException {
    	if (queueName == null || queueName.equals("")) {
    		return null;
    	}
    	
        Queue queue = session.createQueue(queueName);
        QueueBrowser browser = session.createBrowser(queue);
        List<String> messages = new ArrayList<>();

        try {
            Enumeration<?> enumeration = browser.getEnumeration();

            while (enumeration.hasMoreElements()) {
                TextMessage message = (TextMessage) enumeration.nextElement();
                messages.add(message.getText());
            }

            return messages;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ----------- TOPICS ------------
    public Destination createTopic(String topicName) throws JMSException {
    	if (topicName == null || topicName.equals("")){
    		return null;
    	}
    	
        Destination destination = session.createTopic(topicName);
        MessageProducer producer = session.createProducer(destination);
        producer.close();
        return destination;
    }

    public void deleteTopic(String topicName) throws JMSException {
    	if (topicName == null || topicName.equals("")){
    		return;
    	}
    	
        Destination destination = session.createTopic(topicName);
        ((ActiveMQConnection) connection).destroyDestination((ActiveMQDestination) destination);
    }

    public Set<String> getTopics() throws JMSException {
    	Set<String> topics = new TreeSet<>();

        try {
            for (ActiveMQDestination destination : broker.getBroker().getDestinations()) {
                if (destination.isTopic() && destination.getPhysicalName().indexOf("ActiveMQ") < 0) {
                    topics.add(destination.getPhysicalName());
                }
            }

            return topics;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}