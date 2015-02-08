package Server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ConnectionWriter extends Thread {

	// Atributi
	
	public static HashMap<String, OutputStream> connections = new HashMap<String, OutputStream>();
	private Set<String> set = connections.keySet();

	
	@Override
	public void run() {
		// beskonacna petlja
		while (true) {
			// Pozivam Message i provjeravam da li je Linked lista prazna
			if (Message.hasNext()) {
				// Metoda next()  uzma clan iz Linked liste i brise ga
				Message msg = Message.next();
				byte[] msgByte = (msg.getSender() + ": " + msg.getContent())
						.getBytes();
				// petlja koja prolazi kroz Set koji je nosi Key za HashMapu
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					String current = it.next();
					if (!current.equals(msg.getSender())) {
						OutputStream os = connections.get(current);

						try {
							os.write(msgByte);
							os.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
		}
	}
}
