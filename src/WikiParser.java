import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WikiParser extends DefaultHandler {
	SAXParserFactory factory = SAXParserFactory.newInstance();
	SAXParser saxParser;

	public WikiParser() {
		try {
			saxParser = factory.newSAXParser();
		} catch (Exception ex) {

		}

	}

	StringBuffer buff;

	boolean page = false;
	boolean text = false;
	boolean revision = false;
	boolean title = false;
	int count = 0;
	int end = 20;
	Document doc;

	public void parse(String path) {
		try {
			saxParser.parse(path, this);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		/*
		 * if(count <= end && (qName.equalsIgnoreCase("page") ||
		 * qName.equalsIgnoreCase("text") || qName.equalsIgnoreCase("title") ))
		 * System.out.println("Start Element :" + qName);
		 */
		if (qName.equalsIgnoreCase("page") && count <= end) {
			page = true;

			doc = new Document(count);
		}
		if (qName.equalsIgnoreCase("text")) {
			text = true;
		}
		if (qName.equalsIgnoreCase("title")) {
			title = true;
		}
		if (page || text || title) {
			buff = new StringBuffer();
		}

	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		/*
		 * if(count <= end && (qName.equalsIgnoreCase("page") ||
		 * qName.equalsIgnoreCase("text") || qName.equalsIgnoreCase("title") ))
		 * System.out.println("End Element :" + qName);
		 */

		String res = "";

		if (qName.equalsIgnoreCase("page") && count <= end) {
			doc.showDocument();

			page = false;
			// doc = null;
			count += 1;
		}
		if (qName.equalsIgnoreCase("text") && count <= end) {
			text = false;
			// count += 1;
			res = new String(buff.toString());
			doc.setText(res);

		}
		if (qName.equalsIgnoreCase("title") && count <= end) {

			res = new String(buff.toString());
			doc.setTitle(res);
			title = false;
			// count += 1;
		}

	}

	public void characters(char ch[], int start, int length) throws SAXException {
		if (text || title) {
			buff.append(new String(ch, start, length));
			// System.out.println("Text");
		}

	}

}
