package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
	public void connect(String userName) throws RemoteException;

	public void disconnect(String userName) throws RemoteException;

	public boolean getIsOnline(String userName) throws RemoteException;

	public void sendMessage(String sender, String receiver, String message) throws RemoteException;

	public void addContact(String userName, String contact) throws RemoteException;

	public void removeContact(String userName, String contact) throws RemoteException;

	public List<String> getContacts(String userName) throws RemoteException;
}