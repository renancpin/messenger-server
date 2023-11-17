import Broker.BrokerModel;
import Broker.BrokerView;

public class App {
    public static void main(String args[]) {
        BrokerModel broker = new BrokerModel();

        try {
            broker.initialize();
            BrokerView brokerview = new BrokerView(broker);
            brokerview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
