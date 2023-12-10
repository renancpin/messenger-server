package Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
	public void handleMessage(String sender, String message) throws RemoteException;
}
