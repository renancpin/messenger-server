package Server;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import MOM.MessageObject;

public class ServerView {
	private ServerModel server;
	private JFrame frame;

	/**
	 * Create the application.
	 */
	public ServerView(ServerModel server) {
		this.server = server;
		initialize();
	}

	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							server.stop();
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
		frame.setBounds(100, 100, 1010, 595);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblTitle = new JLabel("Servidor - Gerenciamento");
		lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblTitle.setBounds(317, 26, 374, 49);
		panel.add(lblTitle);

		JLabel lblUsuarios = new JLabel("Usuários");
		lblUsuarios.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblUsuarios.setBounds(54, 123, 104, 31);
		panel.add(lblUsuarios);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(54, 164, 424, 275);
		panel.add(scrollPane);

		DefaultTableModel users = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		users.setColumnIdentifiers(new String[] { "Nome", "Status", "Mensagens" });

		JTable userList = new JTable(users);
		userList.setFillsViewportHeight(true);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(userList);

		JButton btnAtualizarUsuarios = new JButton("Atualizar");
		btnAtualizarUsuarios.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnAtualizarUsuarios.setBounds(393, 123, 85, 31);
		btnAtualizarUsuarios.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					Map<String, Boolean> userInfo = server.getUsers();

					users.setRowCount(0);
					for (String user : userInfo.keySet()) {
						String status = userInfo.get(user) ? "Online" : "Offline";
						int messages = server.getMessages(user).size();

						users.addRow(new Object[] { user, status, messages });
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		panel.add(btnAtualizarUsuarios);

		JButton btnRemoverUsuario = new JButton("Remover");
		btnRemoverUsuario.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnRemoverUsuario.setBounds(54, 462, 85, 31);
		btnRemoverUsuario.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					int selectedUser = userList.getSelectedRow();
					if (selectedUser < 0) {
						return;
					}
					String userName = (String) userList.getValueAt(selectedUser, 0);
					server.deleteUser(userName);
					btnAtualizarUsuarios.doClick();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		panel.add(btnRemoverUsuario);

		JButton btnVerMensagens = new JButton("Ver Mensagens >>");
		btnVerMensagens.setEnabled(false);
		btnVerMensagens.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnVerMensagens.setBounds(327, 462, 150, 31);
		panel.add(btnVerMensagens);

		JLabel lblMensagens = new JLabel("Mensagens Pendentes");
		lblMensagens.setBounds(543, 123, 188, 31);
		lblMensagens.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel.add(lblMensagens);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(543, 164, 397, 329);
		panel.add(scrollPane_1);

		DefaultListModel<String> messages = new DefaultListModel<>();
		JList<String> messagesList = new JList<>(messages);
		scrollPane_1.setViewportView(messagesList);

		JButton btnAtualizarMensagens = new JButton("Atualizar");
		btnAtualizarMensagens.setEnabled(false);
		btnAtualizarMensagens.setBounds(855, 125, 85, 31);
		btnAtualizarMensagens.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel.add(btnAtualizarMensagens);

		ActionListener verMensagens = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					String userName = (String) userList.getValueAt(userList.getSelectedRow(), 0);

					if (userName == null || userName.equals("")) {
						return;
					}

					messages.clear();
					for (MessageObject message : server.getMessages(userName)) {
						String text = "[" + message.getSender() + "]: " + message.getText();

						messages.addElement(text);
					}
					;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		btnVerMensagens.addActionListener(verMensagens);
		btnAtualizarMensagens.addActionListener(verMensagens);

		userList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean isUserSelected = userList.getSelectedRow() != -1;

				btnRemoverUsuario.setEnabled(isUserSelected);
				btnVerMensagens.setEnabled(isUserSelected);

				messages.clear();
			}
		});

		btnAtualizarUsuarios.doClick();
	}
}
