package Broker;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.Set;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import Client.ClientModel;
import Client.ClientView;

public class BrokerView {
	private BrokerModel broker;
	private JFrame frame;
	private JTextField newQueueField;
	private JTextField newTopicField;

	/**
	 * Create the application.
	 */
	public BrokerView(BrokerModel broker) {
		this.broker = broker;
		initialize();
	}
	
	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							broker.stop();
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 543, 458);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Filas");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblNewLabel.setBounds(234, 24, 56, 42);
		panel.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(54, 124, 433, 252);
		panel.add(scrollPane);
		
		DefaultTableModel queues = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		queues.setColumnIdentifiers(new String[]{"Fila", "Mensagens"});
		
		JTable queueList = new JTable(queues);
		queueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		queueList.setFillsViewportHeight(true);
		scrollPane.setViewportView(queueList);
		
		JButton btnAtualizarFilas = new JButton("Atualizar");
		btnAtualizarFilas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					Map<String, Integer> queueInfo = broker.getQueues();
					queues.setRowCount(0);
					for (String queue : queueInfo.keySet()) {
						queues.addRow(new Object[] {queue, queueInfo.get(queue)});
					}
//					queues.fireTableDataChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnAtualizarFilas.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAtualizarFilas.setBounds(54, 386, 85, 31);
		panel.add(btnAtualizarFilas);
		
		newQueueField = new JTextField();
		newQueueField.setBounds(54, 76, 321, 25);
		panel.add(newQueueField);
		
		JButton btnAddQueue = new JButton("Adicionar");
		btnAddQueue.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAddQueue.setBounds(402, 72, 85, 31);
		panel.add(btnAddQueue);
		
		ActionListener adicionarFila = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					String queueName = newQueueField.getText();
					if (queueName == null || queueName.equals("")) {
						return;
					}
					newQueueField.setText("");
					broker.createQueue(queueName);
					btnAtualizarFilas.doClick();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		newQueueField.addActionListener(adicionarFila);
		btnAddQueue.addActionListener(adicionarFila);
		
		JButton btnRemoveQueue = new JButton("Remover");
		btnRemoveQueue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					int selectedQueue = queueList.getSelectedRow();
					if (selectedQueue < 0) {
						return;
					}
					String queueName = (String) queueList.getValueAt(selectedQueue, 0);
					broker.deleteQueue(queueName);
					btnAtualizarFilas.doClick();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnRemoveQueue.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnRemoveQueue.setBounds(402, 386, 85, 31);
		panel.add(btnRemoveQueue);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(543, 0, 543, 458);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblTpicos = new JLabel("Tópicos");
		lblTpicos.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblTpicos.setBounds(225, 24, 101, 42);
		panel_1.add(lblTpicos);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(63, 124, 433, 252);
		panel_1.add(scrollPane_1);
		
		DefaultListModel<String> topics = new DefaultListModel<>();
		JList<String> topicList = new JList<>(topics);
		scrollPane_1.setViewportView(topicList);
		
		JButton btnAtualizarTopicos = new JButton("Atualizar");
		btnAtualizarTopicos.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					Set<String> topicInfo = broker.getTopics();
					topics.clear();
					topics.addAll(topicInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnAtualizarTopicos.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAtualizarTopicos.setBounds(63, 386, 85, 31);
		panel_1.add(btnAtualizarTopicos);
		
		newTopicField = new JTextField();
		newTopicField.setBounds(63, 76, 321, 25);
		panel_1.add(newTopicField);
		
		JButton btnAddTopic = new JButton("Adicionar");
		btnAddTopic.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAddTopic.setBounds(411, 72, 85, 31);
		panel_1.add(btnAddTopic);
		
		ActionListener adicionarTopico = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					String topicName = newTopicField.getText();
					if (topicName == null || topicName.equals("")) {
						return;
					}
					newTopicField.setText("");
					broker.createTopic(topicName);
					btnAtualizarTopicos.doClick();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		newTopicField.addActionListener(adicionarTopico);
		btnAddTopic.addActionListener(adicionarTopico);
		
		JButton btnRemoveTopic = new JButton("Remover");
		btnRemoveTopic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					String topicName = topicList.getSelectedValue();
					if (topicName == null || topicName.equals("")) {
						return;
					}
					broker.deleteTopic(topicName);
					btnAtualizarTopicos.doClick();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnRemoveTopic.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnRemoveTopic.setBounds(411, 386, 85, 31);
		panel_1.add(btnRemoveTopic);
		
		JButton btnCriarCliente = new JButton("Iniciar Cliente");
		btnCriarCliente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ClientModel client = new ClientModel();
					client.initialize();
					ClientView clientView = new ClientView(client);
					clientView.start();
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		});
		btnCriarCliente.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnCriarCliente.setBounds(475, 484, 157, 35);
		frame.getContentPane().add(btnCriarCliente);
	}
}
