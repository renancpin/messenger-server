package MOM;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.QueueBrowser;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class MOMService {
	private BrokerService broker;
	private Connection connection;
	private Session session;

	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

	public void initialize() throws JMSException {
		broker = new BrokerService();

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

	public void stop() throws JMSException {
		try {
			session.close();
			connection.close();
			broker.stop();
		} catch (Exception e) {
			e.printStackTrace();
			throw new JMSException(e.getMessage());
		}
	}

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

	public List<String> getQueues() throws JMSException {
		List<String> queues = new ArrayList<>();

		try {
			for (ActiveMQDestination destination : broker.getBroker().getDestinations()) {
				if (destination.isQueue()) {
					queues.add(destination.getPhysicalName());
				}
			}

			return queues;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<MessageObject> getQueueMessages(String queueName) throws JMSException {
		if (queueName == null || queueName.equals("")) {
			return null;
		}

		Queue queue = session.createQueue(queueName);
		QueueBrowser browser = session.createBrowser(queue);
		List<MessageObject> messages = new ArrayList<>();

		try {
			Enumeration<?> enumeration = browser.getEnumeration();

			while (enumeration.hasMoreElements()) {
				TextMessage message = (TextMessage) enumeration.nextElement();

				String sender = message.getStringProperty("sender");
				String receiver = queueName;
				String text = message.getText();

				MessageObject messageObj = new MessageObject(sender, receiver, text);
				messages.add(messageObj);
			}

			return messages;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void sendMessageToQueue(String sender, String queueName, String message) throws JMSException {
		if (queueName == null || queueName.equals("")) {
			return;
		}

		Message messageObj = session.createTextMessage(message);
		messageObj.setStringProperty("sender", sender);

		Destination destination = session.createQueue(queueName);
		MessageProducer producer = session.createProducer(destination);

		producer.send(messageObj);
		producer.close();
	}

	public List<MessageObject> receiveMessagesFromQueue(String queueName) throws JMSException {
		if (queueName == null || queueName.equals("")) {
			return null;
		}

		Destination destination = session.createQueue(queueName);
		MessageConsumer consumer = session.createConsumer(destination);
		List<MessageObject> messages = new ArrayList<>();
		TextMessage message;

		do {
			message = (TextMessage) consumer.receive(10);

			if (message != null) {
				String sender = message.getStringProperty("sender");
				String text = message.getText();

				MessageObject messageObj = new MessageObject(sender, queueName, text);
				messages.add(messageObj);
			}
		} while (message != null);

		consumer.close();

		return messages;
	}
}