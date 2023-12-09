package Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import Client.ClientInterface;
import MOM.MOMService;
import MOM.MessageObject;

public class ServerModel implements ServerInterface {
	private static String SERVER_NAME = "MessengerServer";

	private Registry registry = null;
	private Map<String, Boolean> users = new TreeMap<>();
	private MOMService momService = new MOMService();

	public void initialize() {
		try {
			registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			Remote object = UnicastRemoteObject.exportObject(this, 0);
			registry.bind(SERVER_NAME, object);

			momService.initialize();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			registry.unbind(SERVER_NAME);
			momService.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, Boolean> getUsers() {
		try {
			List<String> userNames = momService.getQueues();

			for (String user : userNames) {
				if (!users.containsKey(user)) {
					users.put(user, false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new TreeMap<>(users);
	}

	public List<MessageObject> getMessages(String userName) {
		try {
			return momService.getQueueMessages(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void deleteUser(String userName) {
		try {
			momService.deleteQueue(userName);
			users.remove(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendDirectMessage(String sender, String receiver, String message) throws Exception {
		ClientInterface client = (ClientInterface) registry.lookup(receiver);

		client.handleMessage(sender, message);
	}

	@Override
	public void connect(String userName) throws RemoteException {
		users.put(userName, true);

		try {
			for (MessageObject message : momService.getQueueMessages(userName)) {
				sendDirectMessage(message.getSender(), userName, message.getText());
			}

			momService.receiveMessagesFromQueue(userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect(String userName) throws RemoteException {
		users.put(userName, false);
	}

	@Override
	public boolean getIsOnline(String userName) throws RemoteException {
		return users.getOrDefault(userName, false);
	}

	@Override
	public void sendMessage(String sender, String receiver, String message) throws RemoteException {
		if (users.getOrDefault(receiver, false)) {
			try {
				sendDirectMessage(sender, receiver, message);

				return;
			} catch (Exception e) {
				users.put(receiver, false);
			}
		}

		try {
			momService.sendMessageToQueue(sender, receiver, message);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("Could not send message to offline client");
		}
	}

	private Set<String> readContacts(String userName) {
		Set<String> contacts = new TreeSet<>();

		try {
			FileReader reader = new FileReader("./contacts/" + userName + ".txt");
			BufferedReader bufferedReader = new BufferedReader(reader);
			String contact;

			while ((contact = bufferedReader.readLine()) != null) {
				contacts.add(contact);
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return contacts;
	}

	private void writeContacts(String userName, Set<String> contacts) {
		try {
			Files.createDirectories(Paths.get("contacts"));
			FileWriter writer = new FileWriter("./contacts/" + userName + ".txt");

			for (String contact : contacts) {
				writer.write(contact + '\n');
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addContact(String userName, String contact) throws RemoteException {
		Set<String> contacts = readContacts(userName);
		contacts.add(contact);
		writeContacts(userName, contacts);
	}

	@Override
	public void removeContact(String userName, String contact) throws RemoteException {
		Set<String> contacts = readContacts(userName);
		contacts.remove(contact);
		writeContacts(userName, contacts);
	}

	@Override
	public List<String> getContacts(String userName) throws RemoteException {
		Set<String> contacts = readContacts(userName);
		return new ArrayList<String>(contacts);
	}
}