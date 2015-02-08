package Server;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XmlConnection {

	// ATRIBUTI
	private static Document xmlDoc;
	private static DocumentBuilder docReader;
	private static XPath xPath;

	// konstruktor
	// pisanje u XML
	public XmlConnection() throws ParserConfigurationException, SAXException,
			IOException {
		
		docReader = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		xmlDoc = docReader.parse(new File("./XML/User.xml"));
		xPath = XPathFactory.newInstance().newXPath();

	}

	// XPATH 
	public static int userLogin(String username, String password) {
		// prvo provjeravamo da li u XML fileu imamo clienta sa ovim userom i pass
		//USER I PASS
		String expression = "//user[@name =\"" + username
				+ "\" and @password=\"" + password + "\"]";

		System.out.println(expression);

		try {
			Node user = (Node) xPath.compile(expression).evaluate(xmlDoc,
					XPathConstants.NODE);
			if (user == null) {
				// ako nemamo onda provjeravao da li imamo ovaj user
				// USER
				String expression2 = "//user[@name =\"" + username + "\"]";
				Node user2 = (Node) xPath.compile(expression2).evaluate(xmlDoc,
						XPathConstants.NODE);
				if (user2 == null) {
					
					// ako nemamo ovoh usera onda ha usnoimo tj registrujemo
					Element newUser = xmlDoc.createElement("user");
					newUser.setAttribute("name", username);
					newUser.setAttribute("password", password);
					xmlDoc.getElementsByTagName("users").item(0)
							.appendChild(newUser);

					
					// novi nacin pisanja u XML!!!! 
					StreamResult file = new StreamResult(new File(
							"./XML/user.xml"));
					Transformer transformer;
					try {
						transformer = TransformerFactory.newInstance()
								.newTransformer();
						DOMSource source = new DOMSource(xmlDoc);
						transformer.transform(source, file);
						return 0;
					} catch (TransformerFactoryConfigurationError
							| TransformerException e1) {

						e1.printStackTrace();
						return -4;
					}
				} else {
					return -1;
				}
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return -3;
		}

		return 0;

	}

	// pocetak main metode
	public static void main(String[] args) {
		try {
			//kreiramo xml
			XmlConnection test = new XmlConnection();
			// pozivamo  metodu provjere usera i kupimo rezultat
			int rezultat = XmlConnection.userLogin("Fata", "Semsa");
			System.out.println(rezultat);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

	}// kraj main metode
	
}
