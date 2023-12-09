import Server.ServerModel;
import Server.ServerView;

public class App {
	public static void main(String args[]) {
		ServerModel server = new ServerModel();

		try {
			server.initialize();
			ServerView view = new ServerView(server);
			view.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
