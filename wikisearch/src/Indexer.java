import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.HashSet;

public class Indexer {
	Document doc;
	TreeMap<String, TreeMap<Long, Long> > textPostingList;
	TreeMap<String, TreeMap<Long, Long> > titlePostingList;
	TreeMap<String, TreeMap<Long, Long> > categoriesPostingList;
	TreeMap<String, TreeMap<Long, Long> > infoboxPostingList;
	TreeMap<String, TreeMap<Long, Long> > referencesPostingList;
	String titleMapper = "";
	ArrayList<Document> docList;
	public int count = 0;
	public int id = 0;
	public Indexer() {
		docList = new ArrayList<Document>();
		textPostingList = new TreeMap<String, TreeMap<Long, Long> > ();
		titlePostingList = new TreeMap<String, TreeMap<Long, Long> > ();

		categoriesPostingList = new TreeMap<String, TreeMap<Long, Long> > ();

		infoboxPostingList = new TreeMap<String, TreeMap<Long, Long> > ();

		referencesPostingList = new TreeMap<String, TreeMap<Long, Long> > ();

		
	}
	void process() {
		System.out.println("Count: " + count);
	}
	public void add(Document doc) {
		docList.add(doc);
		count += 1;
	}
	public void clear() {
		docList.clear();
	}
	
	
	public void index() {
		for(Document doc : docList) {
			this.doc = doc;
			titleMapper += doc.id + ":" + doc.getTitle() + "\n";
			//System.out.println("Doc:" + doc.id);
			addToList1(doc.bodyWordsList, textPostingList);
			addToList1(doc.titleList, titlePostingList);
			addToList1(doc.categoriesList, categoriesPostingList);
			addToList1(doc.infoboxList, infoboxPostingList);
		}
		
		
		
	}
	public void addToList1(ArrayList<String> list, TreeMap<String, TreeMap<Long, Long> > postingList) {
		for(String word : list) {
			if(postingList.containsKey(word)) {
				long value = 1;
				if(postingList.get(word).containsKey(doc.getId()) ) {
					value = postingList.get(word).get(doc.getId());
					value += 1;
					
				}
				postingList.get(word).put(doc.getId(), value );
			}
			else {
				TreeMap<Long, Long> map = new TreeMap<Long, Long>();
				map.put(doc.getId(), 1L);
				
				postingList.put(word, map);
			}
		}
	}
	public void addToList(ArrayList<String> list, TreeMap<String, HashSet<Long >> postingList) {
		for(String word : list) {
			if(postingList.containsKey(word)) {
				postingList.get(word).add(doc.getId());
			}
			else {
				HashSet<Long> set = new HashSet<Long>();
				
				set.add(doc.getId());
				postingList.put(word, set);
			}
		}
	}
	
	public void writeToIndexFile(String path, TreeMap<String, HashSet<Long> > postingList) {
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(path);
			//System.out.println(postingList.size());
			for(String key : postingList.keySet()) {
				fw.write(key + " ");
				
				String ids =  "";
				for(Long id : postingList.get(key) ) {
					ids += id + ",";

				}
				fw.write(ids.substring(0, ids.length() - 1));
				fw.write("\n");

			}

		}
		catch(Exception ex) {
			System.out.println(ex);
			
		}
		finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
public void writeToIndexFile1(String path, TreeMap<String, TreeMap<Long, Long> > postingList) {
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(path);
			//System.out.println(postingList.size());
			for(String key : postingList.keySet()) {
				fw.write(key + " ");
				
				String ids = "";
				for(Long id : postingList.get(key).keySet() ) {
					ids += id + ":" + postingList.get(key).get(id) + ",";

				}
				fw.write(ids.substring(0, ids.length() - 1));
				fw.write("\n");

			}

		}
		catch(Exception ex) {
			System.out.println(ex);
			
		}
		finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	public void write(String path, String text) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(path, true);
			
			fw.write(text);			

		}
		catch(Exception ex) {
			System.out.println(ex);
			
		}
		finally {
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	public void writeToFile(String path, int id) {
		this.id = id;
		System.out.println("Writing to file");
		
		String titleMapperPath = path + "titleMapper.txt";
		String titlePath = path + "title" + this.id + ".txt";
		String bodyPath = path + "body" + this.id + ".txt";
		String categoryPath = path + "categories" + this.id + ".txt";
		String infoboxPath = path + "infobox" + this.id + ".txt";
		
		
		write(titleMapperPath , titleMapper);
		System.out.println("TitleMapping done");
		
		
		writeToIndexFile1(titlePath, titlePostingList);
		System.out.println("Title postings created");
		
		writeToIndexFile1(bodyPath, textPostingList);
		System.out.println("Body postings created");

		writeToIndexFile1(infoboxPath, infoboxPostingList);
		System.out.println("Infobox postings created");

		writeToIndexFile1(categoryPath, categoriesPostingList);
		System.out.println("Indexing done");

		
	}
	
	

}
