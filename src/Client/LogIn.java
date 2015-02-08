package Client;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.xml.transform.TransformerFactoryConfigurationError;

import GUI.ChatGui;

public class LogIn {
	// Atributi
	private JTextField textField;
	private JPasswordField pass;
	// host i port preko kojeg ce se klijent spojiti
	private String host;
	private int port;
	private JFrame window;

	// Konstruktor
	public LogIn(String host, int port) {
		this.host = host;
		this.port = port;

		// Pravimo GUI za PRIJAVU (unos usera i pass)

		window = new JFrame("Login");
		JPanel content = new JPanel();
		textField = new JTextField(20);
		JButton loginButton = new JButton("Login");
		JButton quitButton = new JButton("Quit");
		JLabel labelUser = new JLabel("Username");
		JLabel labelPass = new JLabel("Password");
		pass = new JPasswordField(20);

		window.add(content);
		content.add(labelUser);
		content.add(textField);
		content.add(labelPass);
		content.add(pass);
		content.add(loginButton);

		// privatna class-a za ovaj button
		loginButton.addActionListener(new ButtonHandler());

		// anonimna klasa za ovaj button
		quitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// ako klikne na quit onda ce ga iskljuciti
				System.exit(0);
			}
		});

		content.add(quitButton);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.setSize(300, 200);
		window.setVisible(true);

	}

	// Metoda koja ispisuje upozorenje ako Client unese pogresan pass ili user
	private void showError(String message) {
		JOptionPane.showMessageDialog(null, message, "ERROR",
				JOptionPane.WARNING_MESSAGE);
	}

	// Privantna classa za nas button - LOGIN button
	private class ButtonHandler extends KeyAdapter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// uzmi user i pass
			String username = textField.getText();
			String password = new String(pass.getPassword());

			// Ukoliko neko unese space mi cemo to zamijeniti sa praznim str.
			
			username = username.replaceAll(" ", "");
			password = password.replaceAll(" ", "");
			
			// provjera
			System.out.println(username);
			System.out.println(password);

			// ako klijent nije unio pass ili user izbacit ce error
			if (username.equals("") || password.equals("")) {
				showError("Unesite password i username!");
				return;
			}

			
			
			// PASSWORD OSIGURANJE
			
			String passwordToHash = password;
	        String hashedPassword = null;
	        try {
	            // Create MessageDigest instance for MD5
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            //Add password bytes to digest
	            md.update(passwordToHash.getBytes());
	            //Get the hash's bytes
	            byte[] bytes = md.digest();
	            //This bytes[] has bytes in decimal format;
	            //Convert it to hexadecimal format
	            StringBuilder sb = new StringBuilder();
	            for(int i=0; i< bytes.length ;i++)
	            {
	                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	            }
	            //Get complete hashed password in hex format
	            hashedPassword = sb.toString();
	        }
	        catch (NoSuchAlgorithmException e1)
	        {
	            e1.printStackTrace();
	        }
	   
			// KRAJ OSIGURAVANJA PASSWORDA
			
			
			
			// ako je pass i user ok, ona pravimo konekciju
			Socket client;
			try {
				client = new Socket(host, port);
				OutputStream os = client.getOutputStream();
				InputStream is = client.getInputStream();
				// saljemo nas user i pass. mora uvijek biti \n kada saljemo
				// OustputStream!!! OutputStream salje Byete
				os.write((username + "\n").getBytes());
				os.write((password + "\n").getBytes());

				// TODO poruke da se mijenjanju

				int result = is.read();

				if (result == 0) {

					// ako su unijeti podaci u LOGin ok, onda iskljuci log in
					// prozor
					window.dispose();

					ChatGui gui = new ChatGui(client);
					
					new Thread(gui).start();
					 } else   {
					 showError("Username ili password nije tacan");
					 }

				

				
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}
}
