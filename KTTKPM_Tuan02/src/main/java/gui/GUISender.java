package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class GUISender extends JFrame implements ActionListener {
	private JTextField textField;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUISender frame = new GUISender();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUISender() {
		setResizable(false);
		setAlwaysOnTop(true);
		getContentPane().setLayout(null);
		setSize(650,550);
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(20, 41, 589, 386);
		getContentPane().add(textArea);
		
		textField = new JTextField();
		textField.setBounds(20, 437, 500, 39);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(524, 437, 85, 39);
		getContentPane().add(btnNewButton);
		
		btnNewButton.addActionListener(this);
		textField.addActionListener(this);
	}

	public void seed() throws Exception {
		BasicConfigurator.configure();
		//config environment for JNDI
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		//create context
		Context ctx = new InitialContext(settings);
		//lookup JMS connection factory
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		//lookup destination. (If not exist-->ActiveMQ create once)
		Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
		//get connection using credential
		Connection con = factory.createConnection("admin", "admin");
		//connect to MOM
		con.start();
		//create session
		Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
		//create producer
		MessageProducer producer = session.createProducer(destination);
		//create text message
		Message msg = session.createTextMessage("hello mesage from ActiveMQ");
		producer.send(msg);
		try {
				String name = textField.getText();
				Person p = new Person(1001,name, new Date());
				String xml = new XMLConvert<Person>(p).object2XML(p);
				msg = session.createTextMessage(xml);
				producer.send(msg);
				textField.setText("");
				textArea.setText(textArea.getText() + "\n" + name);
				System.out.println(name);

		} finally {
			session.close();
			con.close();
			System.out.println("Finished...");
		}
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			seed();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}