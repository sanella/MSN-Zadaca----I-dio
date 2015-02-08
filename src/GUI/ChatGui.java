package GUI;

import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;

import javax.jws.Oneway;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatGui implements Runnable {

	// Atributi
	private JTextArea display;
	private TextField inputMsg;
	private Socket connection;
	private InputStream is;
	private OutputStream os;
	private JTextArea displayOnlinePeople;

	/**
	 * KONSTRUKTOR
	 * @param connection - prima konekciju
	 * @throws IOException
	 */
	public ChatGui(final Socket connection) throws IOException {

		this.connection = connection;
		this.is = connection.getInputStream();
		this.os = connection.getOutputStream();

		//GUI za MSN
		JFrame window = new JFrame("MSN");
		JPanel content = new JPanel();
		JButton buttonSend = new JButton("SEND");
		JFileChooser file = new JFileChooser();
		display = new JTextArea();
		displayOnlinePeople = new JTextArea();
		display.setEditable(false);
		inputMsg = new TextField();
		inputMsg.setPreferredSize(new Dimension(200, 20));

		
		file.addActionListener(new FileHandler());
		
		
		
		
		
		
		
		
		
		
		
		buttonSend.addActionListener(new MessageHandler());
		inputMsg.addKeyListener(new MessageHandler());
		display.setLineWrap(true);

		// Kada pravimo JScrollePane dodoajemo cijeli TextArea na njega!!!
		JScrollPane areaScrollPane = new JScrollPane(display);
		areaScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		areaScrollPane.setPreferredSize(new Dimension(390, 220));

		
		// prozor u kojem ce se ispisivati online klijenti 
		JScrollPane scrollePaneOnlinePeople = new JScrollPane(displayOnlinePeople);
		scrollePaneOnlinePeople
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollePaneOnlinePeople.setPreferredSize(new Dimension(100, 220));
		
		
		content.add(areaScrollPane);
		content.add(scrollePaneOnlinePeople);
		content.add(inputMsg);
		content.add(buttonSend);
		content.add(file);
		
		window.add(content);

		window.setResizable(false);
		
		//PITAJ ZASTo
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
			try {
				connection.shutdownOutput();
				connection.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			System.exit(0);
			}
		});
		window.setSize(600, 500);
		window.setVisible(true);

	}

	public void listenForNetwork() throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(is));

		String line = null;
		//petlja koja cita BufferdReader sve dok nije prazan == null
		
		while ((line = input.readLine()) != null) {
			// ako nije prazan string onda ga razdvoji sa ':' i dodaj u niz
			if (!line.equals("")) {
				System.out.println(line);
				String[] array = line.split(":");
				if(array[0].equals("%server%")){
					String[] arrayServer = array[1].split("%");
					if(arrayServer[0].equals(" join")){
						
						display.append(arrayServer[1]+" se pridruzio chatu\n");
						
					} else if(arrayServer[0].equals(" left")){
						
						display.append(arrayServer[1] + " napustio chat\n");
					
					}
					
				} else {
				display.append( line + "\n");
				
				}
				line = null;
			}
		}
	}

	
	
	
	//PRIVATNA CLASA ZA SLANJE FILEA
	
	private class FileHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			e.getSource();
			
		}
	}
	
	
	
	// privatna classa za button SEND
	private class MessageHandler extends KeyAdapter implements ActionListener {

		private void sendMessage() {
			// String str kupi poruku koju je korisnik unio
			String str = inputMsg.getText();
			// ako string nije prazan
			if (!str.equals("")) {
				// dodaj mu novi red jer OS cita po redovima
				str += "\n";
				// ispisi na display u ovom formatu
				display.append("Me: " + str);
				try {
					os.write(str.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				// ocisti text koji je korisnik unio
				inputMsg.setText(null);

			}

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			sendMessage();
		}

		// ako je korinik pritisnuo 10 posalji text (pozovi metodu sendMessage)
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 10)
				sendMessage();
		}
	}

	// Override  THREAD
	@Override
	public void run() {
		try {
			//pozivam metodu 
			listenForNetwork();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
}
