package Client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JList;

public class ClientView {
	private ClientModel client;
	private JFrame frame;
	private JTextField userNameField;
	private JTextField destinationQueueField;
	private JTextField queueMessageField;
	private JTextField topicField;
	private JTextField destinationTopicField;
	private JTextField topicMessageField;

	/**
	 * Create the application.
	 */
	public ClientView(ClientModel client) {
		this.client = client;
		initialize();
	}
	
	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							client.stop();
							super.windowClosing(e);
						}
					});
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1101, 595);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		
		// ------------ Queues ------------
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Filas");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblNewLabel.setBounds(234, 24, 56, 42);
		panel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Nome de usuario");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(75, 85, 115, 19);
		panel.add(lblNewLabel_1);
		
		userNameField = new JTextField();
		userNameField.setBounds(75, 108, 150, 19);
		panel.add(userNameField);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(75, 148, 400, 246);
		panel.add(scrollPane);
		
		JTextArea queueMessagesArea = new JTextArea();
		scrollPane.setViewportView(queueMessagesArea);
		
		JButton btnAtualizarFila = new JButton("Receber");
		btnAtualizarFila.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAtualizarFila.setBounds(390, 101, 85, 31);
		panel.add(btnAtualizarFila);
		
		ActionListener atualizarFila = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String userName = userNameField.getText();
					if (userName == null || userName.equals("")) {
						return;
					}
					List<String> messages = client.receiveMessagesFromQueue(userName);
					for (String message : messages) {
						queueMessagesArea.append(message + '\n');
					}
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		};
		userNameField.addActionListener(atualizarFila);
		btnAtualizarFila.addActionListener(atualizarFila);
		
		JLabel lblNewLabel_2 = new JLabel("Destinatario");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(75, 413, 85, 13);
		panel.add(lblNewLabel_2);
		
		destinationQueueField = new JTextField();
		destinationQueueField.setBounds(75, 436, 150, 19);
		panel.add(destinationQueueField);
		
		JLabel lblNewLabel_3 = new JLabel("Mensagem");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_3.setBounds(75, 465, 85, 19);
		panel.add(lblNewLabel_3);
		
		queueMessageField = new JTextField();
		queueMessageField.setBounds(75, 492, 275, 19);
		panel.add(queueMessageField);
		
		JButton btnSendToQueue = new JButton("Enviar");
		btnSendToQueue.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnSendToQueue.setBounds(390, 465, 85, 46);
		panel.add(btnSendToQueue);
		
		ActionListener sendToQueue = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String message = queueMessageField.getText();
					if (message == null || message.equals("")) {
						return;
					}
					
					String destination = destinationQueueField.getText();
					if (destination == null || destination.equals("")) {
						return;
					}
					
					client.sendMessageToQueue(destination, message);
					queueMessageField.setText("");
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		};
		
		queueMessageField.addActionListener(sendToQueue);
		btnSendToQueue.addActionListener(sendToQueue);
		
		// ------------ Topics --------------
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel_4 = new JLabel("Tópicos");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblNewLabel_4.setBounds(240, 24, 85, 42);
		panel_1.add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("Tópico");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_5.setBounds(52, 94, 115, 24);
		panel_1.add(lblNewLabel_5);		
		
		topicField = new JTextField();
		topicField.setBounds(52, 126, 118, 19);
		panel_1.add(topicField);

		JButton btnSubscribe = new JButton("Assinar");
		btnSubscribe.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnSubscribe.setBounds(52, 155, 85, 23);
		panel_1.add(btnSubscribe);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(52, 188, 118, 152);
		panel_1.add(scrollPane_1);
		
		DefaultListModel<String> topics = new DefaultListModel<>();
		JList<String> topicList = new JList<>(topics);
		scrollPane_1.setViewportView(topicList);		
		
		JButton btnUnsubscribe = new JButton("Remover");
		btnUnsubscribe.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnUnsubscribe.setBounds(52, 350, 85, 23);
		panel_1.add(btnUnsubscribe);
		
		JLabel lblNewLabel_6 = new JLabel("Mensagens");
		lblNewLabel_6.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_6.setBounds(197, 94, 115, 24);
		panel_1.add(lblNewLabel_6);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(197, 126, 300, 270);
		panel_1.add(scrollPane_2);
		
		JTextArea topicMessagesArea = new JTextArea();
		scrollPane_2.setViewportView(topicMessagesArea);
		
		JLabel lblNewLabel_7 = new JLabel("Publicar em");
		lblNewLabel_7.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_7.setBounds(52, 414, 85, 19);
		panel_1.add(lblNewLabel_7);
		
		destinationTopicField = new JTextField();
		destinationTopicField.setBounds(52, 437, 150, 19);
		panel_1.add(destinationTopicField);
		
		JLabel lblNewLabel_8 = new JLabel("Mensagem");
		lblNewLabel_8.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_8.setBounds(52, 466, 85, 19);
		panel_1.add(lblNewLabel_8);
		
		topicMessageField = new JTextField();
		topicMessageField.setBounds(52, 493, 336, 19);
		panel_1.add(topicMessageField);
		
		ActionListener sendToTopic = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String message = topicMessageField.getText();
					if (message == null || message.equals("")) {
						return;
					}
					
					String destination = destinationTopicField.getText();
					if (destination == null || destination.equals("")) {
						return;
					}
					
					client.sendMessageToTopic(destination, message);
					topicMessageField.setText("");
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		};
		
		topicMessageField.addActionListener(sendToTopic);
		
		JButton btnSendToTopic = new JButton("Enviar");
		btnSendToTopic.addActionListener(sendToTopic);
		btnSendToTopic.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnSendToTopic.setBounds(412, 466, 85, 46);
		panel_1.add(btnSendToTopic);
		
		ActionListener subscribeToTopic = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String topicName = topicField.getText();
					if (topics.contains(topicName)) {
						return;
					}
					client.subscribeToTopic(topicName, (String message, String source) -> {
						topicMessagesArea.append("[" + source + "]: " + message + '\n');
					});
					topics.addElement(topicName);
					topicField.setText("");
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		};
		topicField.addActionListener(subscribeToTopic);
		btnSubscribe.addActionListener(subscribeToTopic);
		
		ActionListener unsubscribeFromTopic = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String topicName = topicList.getSelectedValue();
					if (topicName == null || topicName.equals("")) {
						return;
					}
					client.unsubscribeFromTopic(topicName);
					topics.removeElement(topicName);
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		};
		btnUnsubscribe.addActionListener(unsubscribeFromTopic);
	}
}
