package Server;

import java.util.LinkedList;
import java.util.Queue;

public class Message {
	// Atributi
	private String content;
	private String sender;
	private static volatile Queue<Message> msgQueue = new LinkedList<Message>();
	
	//KONSTRUKTOR
	public Message(String content, String sender){
		this.content = content + "\n";
		this.sender = sender;
		msgQueue.add(this);
	}
	
	//metoda koja provjerava da li je queue prazan
	public static boolean hasNext(){
		return !msgQueue.isEmpty();
	}
	//metoda koja vraca i uklanja
	public static Message next(){
		return msgQueue.poll();
	}

	// Seteri i geteri
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
}
