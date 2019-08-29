import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WikiParser extends DefaultHandler {
	SAXParserFactory factory = SAXParserFactory.newInstance();
	SAXParser saxParser;
	Indexer indexer = new Indexer();
	ArrayList<Thread> threadList;;
	ExecutorService executor;

	public WikiParser() {
		try {
			saxParser = factory.newSAXParser();
			//threadList = new ArrayList<Thread>();
			executor = Executors.newFixedThreadPool(1000);
		} catch (Exception ex) {

		}

	}

	StringBuffer buff;

	boolean page = false;
	boolean text = false;
	boolean revision = false;
	boolean title = false;
	int count = 0;
	int end = Integer.MAX_VALUE;
	Document doc;

	public void parse(String docPath, String indexPath) {
		try {
			saxParser.parse(docPath, this);
			try {
//				for(Thread t: threadList)
//					t.join();
//				
				executor.shutdown();
				while(!executor.isTerminated());
			}
			catch(Exception ex) {
				
			}
			indexer.index();
			
			
			indexer.writeToFile(indexPath);
			System.out.println("Exiting Parse method");
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

			//doc = new Document();
			doc = new Document();
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
			//doc.showDocument();
			if(count % 1000 == 0)
				System.out.println(count + " docs processed");
			page = false;
			// doc = null;
			count += 1;
		}
		if (qName.equalsIgnoreCase("text") && count <= end) {
			text = false;
			// count += 1;
			res = new String(buff.toString());
			doc.setText(res);
			//System.out.println("Set text");
//			Thread thread = new Thread(doc);
//			thread.start();
			executor.execute(doc);
			//threadList.add(thread);
			indexer.add(doc);
			//doc.run();
			//indexer.index(doc);
			//System.out.println("Indexing");

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
