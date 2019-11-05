import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;



public class Search {
	ArrayList<Item> secondaryIndexBody = new ArrayList<Item>();
	ArrayList<Item> secondaryIndexTitle = new ArrayList<Item>();
	ArrayList<Item> secondaryIndexCategory = new ArrayList<Item>();

	ArrayList<Item> secondaryIndexInfobox = new ArrayList<Item>();

	ArrayList<Item> secondaryIndexTitleMapper = new ArrayList<Item>();

	HashMap<String, HashMap<Long, Long>> bodyText;
	HashMap<String, HashMap<Long, Long>> categories;
	HashMap<String, HashMap<Long, Long>> infobox;
	HashMap<String, HashMap<Long, Long>> title;
	long idf = 19567269;
	HashMap<String, String> titleMapper;
	String path = "";
	Stemmer stemmer;
	Search(String path){
		stemmer = new Stemmer();
		bodyText = new HashMap<String, HashMap<Long, Long> >();
		categories = new HashMap<String, HashMap<Long, Long> >();
		title = new HashMap<String, HashMap<Long, Long> >();
		infobox = new HashMap<String, HashMap<Long, Long> >();

		titleMapper = new HashMap<String, String>();
		this.path = path;
//		loadIndex(path + "body.txt", bodyText);
//		loadIndex(path + "categories.txt", categories);
//		loadIndex(path + "infobox.txt", infobox);
//		loadIndex(path + "title.txt", title);

		//loadTitleMapper("../../index1/titleMapper.txt");
		loadSecondaryIndex(secondaryIndexBody, "body");
		loadSecondaryIndex(secondaryIndexTitle, "title");
		loadSecondaryIndex(secondaryIndexCategory, "categories");
		loadSecondaryIndex(secondaryIndexInfobox, "infobox");


		loadSecondaryIndex(secondaryIndexTitleMapper, "titleMapper");
		
		//lookUp(secondaryIndexBody, "body","gandhi");
		
	}
	public HashMap<Long, Double> computeTfIdf( String line) {
		long startTime = System.currentTimeMillis();

		HashMap<Long, Double> list= new HashMap<Long, Double>();
		//PriorityQueue<Item> queue = new PriorityQueue<Item>();
		String []postings = line.split(" ")[1].split(",");
		//System.out.println("Posting list length: " + postings.length);
		for(String posting : postings) {
			Item e = new Item();
			if(posting.split(":").length <= 1)
				continue;
			double tf = Long.parseLong(posting.split(":")[1] );
			tf = Math.log(1 + tf);
			double idf = Math.log(this.idf / (double)postings.length);
			list.put(Long.parseLong(posting.split(":")[0] ), tf * idf );
			e.docId = Long.parseLong( posting.split(":")[0] );
			e.score = tf * idf;
			e.isScore = true;
			//queue.add(e);
		}
		//System.out.println("TFIDF done" );
		long endTime = System.currentTimeMillis();

		//System.out.println("TFIDF took " + (endTime - startTime) / 1000 + " seconds");
		return list;
		//return queue;
	}
	public HashMap<Long, Double>  lookUp( ArrayList<Item> secondaryIndex, String type, String word) {
		int start = 0, end = secondaryIndex.size();
		int mid = 0;
		Item temp = new Item();
		temp.data = word;
		int idx = Collections.binarySearch(secondaryIndex, temp);
		//System.out.println(secondaryIndex.size());

		if(idx < 0) {
			//System.out.println("Negative");
			idx = Math.max(0, Math.abs(idx) - 2);
		}
		
		//System.out.println(secondaryIndex.get(523).data);
//		System.out.println( secondaryIndex.get(idx ).data);
//		System.out.println( secondaryIndex.get(idx + 1 ).data);

//		while(start < end) {
//			mid = start + (end - start)/2;
//			if(secondaryIndex.get(mid).data.compareTo(word) < 0) {
//				start = mid + 1;
//			}
//			else if(secondaryIndex.get(mid).data.compareTo(word) == 0) {
//				
//				break;
//			}
//			else {
//				end = mid - 1;
//			}
//		}
		mid = idx;
		RandomAccessFile file;
		try {
			file = new RandomAccessFile("primary/primary_index_" + type +secondaryIndex.get(mid).fp+".txt", "r");
//			System.out.println(secondaryIndex.get(mid).data);
//			System.out.println(secondaryIndex.get(mid).pointer);
			String fname = "primary/primary_index_" + type +secondaryIndex.get(mid).fp+".txt";
			BufferedReader br = new BufferedReader( new FileReader(fname) );
			//System.out.println(fname);
			long pos = secondaryIndex.get(mid).pointer;
			//file.seek(pos);
			int count = 0;
			while(count <= 10000) {
				String line = br.readLine() ;
				if(line == null)
					break;
//				if(line.split(" ").length > 1)
//					System.out.println(line.split(" ")[0]);
				if(line.split(" ").length > 1 && line.split(" ")[0].equals(word)) {
					br.close();
					//System.out.println(line);
					//System.out.println("Found");
					return computeTfIdf( line);
				}
				count += 1;
			}
			file.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HashMap<Long, Double>();
		//return new PriorityQueue<Item>();
//		System.out.println(secondaryIndex.get(mid).data);
//		System.out.println(secondaryIndex.get(mid).pointer);

	}
	public void loadSecondaryIndex(ArrayList<Item> secondaryIndex, String type) {
		
		File f = new File("secondary_index_" + type + ".txt" );

        try {
			BufferedReader b = new BufferedReader(new FileReader(f));
			String line = "";
			 while ((line = b.readLine()) != null) {
	                String []data = line.split(" ");
	                String id = data[0];
	                Item i = new Item();
	                i.data = id;
	                if(type.equals("titleMapper")){
		                i.pointer = Long.parseLong(data[1]);

	                }
	                else {
	                	i.fp = Integer.parseInt( data[1] );
		                i.pointer = Long.parseLong(data[2]);
	                }
	                
	                secondaryIndex.add(i);
			 }
			 b.close();
			 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        
	}
	public void loadTitleMapper(String path) {
		try {
			File f = new File(path );

            BufferedReader b = new BufferedReader(new FileReader(f));

            String readLine = "";

            while ((readLine = b.readLine()) != null) {
                String []data = readLine.split(":");
                String id = data[0];
                String temp = "";
                data[0] = "";
                for(String s: data) {
                	if(s.length() > 0)
                		temp += s + ":";
                }
                //System.out.println(temp);
                titleMapper.put(new String(id), new String(temp.substring(0, temp.length() - 1)) );
                
            }
            b.close();
            //System.out.println(titleMapper.get("1") );
            
            		
		}
		catch(Exception ex) {
			System.out.println(ex);
		}
	}
	public void loadIndex(String name, HashMap<String, HashMap<Long, Long>> map) {
		
		try {
			File f = new File(name);

            BufferedReader b = new BufferedReader(new FileReader(f));

            String readLine = "";

            while ((readLine = b.readLine()) != null) {
                String []data = readLine.split(" ");
                HashMap<Long, Long> list = new HashMap<Long, Long>();
                for(String doc : data[1].split(",")) {
                	//Item item = new Item();
                	Long id = Long.parseLong( doc.split(":")[0] );
                	Long count = Long.parseLong( doc.split(":")[1] );
                	list.put(id, count);
                }
                	
                
                map.put(data[0],  list);
                
            }
            //System.out.println(bodyText.get("gandhi"));
            b.close();
            
            		
		}
		catch(Exception ex) {
			System.out.println(ex);
		}
		
	}
	private static Map<Long, Double> sortByValue(Map<Long, Double> unsortMap, final boolean order)
    {
        List<Entry<Long, Double>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }
	public void searchWord(String text, HashMap<String, HashMap<Long, Long>> list, Map<Long, Long> results, boolean isFieldQuery, boolean isTitle) {
		ArrayList<String> words = Stopwords.removeStopwords(text.split(" "));
		for(String word: words) {
			if(list.containsKey(word)) {
				if(results.size() == 0) {
					
					for(Long key: list.get(word).keySet()) {
						
						if(isTitle) {
							results.put(key, list.get(word).get(key) + 10);
						}
						else
							results.put(key, list.get(word).get(key) );		
						}

				}
				else {
					
					for(Long key: list.get(word).keySet() ) {
						if(results.containsKey(key)) {
							if(isTitle) {
								results.put(key, results.get(key) + list.get(word).get(key) + 10);
								//System.out.println(word);
							}
							else {
								results.put(key, results.get(key) + list.get(word).get(key) );

							}
						}
						else {
//							if(isTitle) {
//								results.put(key, list.get(word).get(key) + 10);
//								System.out.println(word);
//							}
//							else
//								results.put(key, list.get(word).get(key) );
							if(isFieldQuery) {
								results.remove(key);
								

							}
							else {
								if(isTitle)
									results.put(key, list.get(word).get(key) + 10);
								else
									results.put(key, list.get(word).get(key) );
//
//
							}
						}
					}
				}
			}
		}
	}
	public String getTitle(Long docId, ArrayList<Item> secondaryIndex) {
		int start = 0, end = secondaryIndex.size() - 1;
		int mid = 0;
		boolean found = false;
		
		while(end - start > 1) {
			mid = start + (end - start)/2;
			Long value = Long.parseLong(secondaryIndex.get(mid).data);
			if(value < docId) {
				start = mid + 1;
			}
			else if(value == docId) {
				found = true;
				break;
			}
			else {
				end = mid - 1;
			}
		}
		Item temp = new Item();
		temp.isNumber = true;
		temp.data = docId.toString();
		int idx = Collections.binarySearch(secondaryIndex, temp);
		if(idx < 0) {
			//System.out.println("Negative");
			idx = Math.abs(idx) - 2;
		}
		mid = idx;
		if(mid < 0)
			mid = 0;
		RandomAccessFile file;
		String title = "";
		//System.out.println(mid);
		try {
			file = new RandomAccessFile("../../index1/titleMapper" +".txt", "r");
//			System.out.println("Doc ID:" + docId);
//
//			System.out.println(secondaryIndex.get(mid).data);
//			System.out.println(secondaryIndex.get(mid).pointer);

			long pos = secondaryIndex.get(mid).pointer;
			file.seek(pos);
			int count = 0;
			while(count <= 5000) {
				String line = file.readLine() ;
				if(line == null) {
					file.close();
					break;

				}
				//System.out.println(line);
				if(line.split(":")[0].equals(docId.toString())) {
					file.close();
					//System.out.println("Title:" +line);
					String []words = line.split(":");
					for(int i = 1; i < words.length; i++) {
						if(i > 1 ) {
							title += ":"+ words[i];

						}
						else {
							title += words[i];

						}
					}
					//title = line.split(":")[1];
					break;
				}
				count += 1;
			}
			file.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return title;
		
	}
	public String query(String text) {
		
		text = text.toLowerCase();
		text = text.trim().replaceAll("\\s+", " ").replaceAll("[^a-z:0-9]", " ");
		text = Stopwords.removeStopWords(text);
		//System.out.println(text);
		String title = "";
		String category = "";
		String body = "";
		String ref = "";
		String infobox = "";

		boolean isFieldQuery = false;
		Map<Long, Double> results = new HashMap<Long, Double>();
		//System.out.println(text);
		if(text.contains(":")) {
			
//			String []queryList = text.split(":");
//			for(int i = 0; i < queryList.length; i++) {
//				if(queryList[i].equals("title")) {
//					
//				}
//			}
			if(text.contains("title:") || text.contains("body:") || text.contains("category:") || text.contains("infobox:")) {
				int titleIndex = text.indexOf("title:");
				int bodyIndex = text.indexOf("body:");
				int catIndex = text.indexOf("category:");
				int infoIndex = text.indexOf("infobox:");
				int refIndex = text.indexOf("ref:");
				//System.out.println(text.substring(titleIndex + 6, text.length() ));
				for(int i = titleIndex + 6; i < text.length() && i != bodyIndex && i!= refIndex && i!= infoIndex && i != catIndex; i++) {
					title += text.charAt(i);
				}
				for(int i = bodyIndex + 5; i < text.length() && i != titleIndex && i!= refIndex && i!= infoIndex && i != catIndex; i++) {
					body += text.charAt(i);
				}
				for(int i = catIndex + 9; i < text.length() && i != titleIndex && i!= refIndex && i!= infoIndex && i != bodyIndex; i++) {
					category += text.charAt(i);
				}
				for(int i = infoIndex + 8; i < text.length() && i != titleIndex && i!= refIndex && i!= bodyIndex && i != bodyIndex; i++) {
					infobox += text.charAt(i);
				}
				for(int i = refIndex + 4; i < text.length() && i != titleIndex && i!= bodyIndex && i!= infoIndex && i != bodyIndex; i++) {
					ref += text.charAt(i);
				}
//				System.out.println("title:" + title);
//				System.out.println("body:" + body);
//				System.out.println("info:"+infobox);
//				System.out.println("cat:"+category);
//				System.out.println("ref:"+ref);
			}
			
			isFieldQuery = true;
			
			if(isFieldQuery) {
				
				body = stemmer.getStem(body);
				title = stemmer.getStem(title);
				category = stemmer.getStem(category);
				infobox = stemmer.getStem(infobox);
				
				
				for(String c: category.split(" ")) {
					var tempResCategory = lookUp(secondaryIndexCategory, "categories", c);

					for(Long id: tempResCategory.keySet()) {
						if(results.containsKey(id)) {
							results.put(id, 1.5 * (results.get(id) + tempResCategory.get(id) ) );
						}
						else {
							results.put(id, tempResCategory.get(id));
						}
					}
				}
				for(String i:infobox.split(" ")) {
					var tempResInfobox = lookUp(secondaryIndexInfobox, "infobox", i);

					for(Long id: tempResInfobox.keySet()) {
						if(results.containsKey(id)) {
							results.put(id, 1.3 * (results.get(id) +  tempResInfobox.get(id)) );
						}
						else {
							results.put(id, tempResInfobox.get(id));
						}
					}
				}
				for(String b:body.split(" ")) {	
					
					var tempRes = lookUp(secondaryIndexBody, "body", b);				
					
					
					for(Long id: tempRes.keySet()) {
						if(results.containsKey(id)) {
							results.put(id, 1.3 * (results.get(id) + tempRes.get(id)) );
						}
						else {
							results.put(id, tempRes.get(id));
						}
					}					
					
				}
				for(String t:title.split(" ")) {
					var tempResTitle = lookUp(secondaryIndexTitle, "title", t);
					for(Long id: tempResTitle.keySet()) {
						if(results.containsKey(id)) {
							results.put(id, 2 * (results.get(id) + tempResTitle.get(id)) );
						}
						else {
							results.put(id, 2* tempResTitle.get(id));
						}
					}
				}
				
				
				
			}
			
			
//			searchWord(category, this.categories, results, isFieldQuery, false);
////			System.out.println(results.size());
//			searchWord(title, this.title, results, isFieldQuery, true);
////			System.out.println(results.size());			
//
//			
//
//			searchWord(infobox, this.infobox, results, isFieldQuery, false);
////			System.out.println(results.size());
//			
//			searchWord(body, this.bodyText, results, isFieldQuery, false);
//			//System.out.println(results.size());

		}
		else {
			//text = Stopwords.removeStopWords(text);
			for(String word: text.split(" ")) {
				word = stemmer.getStem(word);
				var tempRes = lookUp(secondaryIndexBody, "body", word);
				for(Long id: tempRes.keySet()) {
					if(results.containsKey(id)) {
						results.put(id, 1.1 * ( results.get(id) + tempRes.get(id) ) );
					}
					else {
						results.put(id, tempRes.get(id));
					}
				}				
			}
			
			for(String word: text.split(" ")) {
				word = stemmer.getStem(word);
				var tempResTitle = lookUp(secondaryIndexTitle, "title", word);
				for(Long id: tempResTitle.keySet()) {
					if(results.containsKey(id)) {
						results.put(id, 1.3 * ( results.get(id) + tempResTitle.get(id) ) );
					}
					else {
						results.put(id, tempResTitle.get(id));
					}
				}				
			}
			for(String word: text.split(" ")) {
				word = stemmer.getStem(word);
				var tempResCategory = lookUp(secondaryIndexCategory, "categories", word);
				for(Long id: tempResCategory.keySet()) {
					if(results.containsKey(id)) {
						results.put(id, 1.2 * ( results.get(id) + tempResCategory.get(id) ) );
					}
					else {
						results.put(id, tempResCategory.get(id));
					}
				}				
			}
			
			for(String word: text.split(" ")) {
				word = stemmer.getStem(word);
				var tempResInfobox = lookUp(secondaryIndexInfobox, "infobox", word);
				for(Long id: tempResInfobox.keySet()) {
					if(results.containsKey(id)) {
						results.put(id, 1.2 * ( results.get(id) + tempResInfobox.get(id) ) );
					}
					else {
						results.put(id, tempResInfobox.get(id));
					}
				}				
			}
			
				
				
				
				
				
				
			
//			searchWord(text, this.title, results, isFieldQuery, true);
//			//System.out.println(results.size());			
//
//			searchWord(text, this.categories, results, isFieldQuery, false);
//			//System.out.println(results.size());
//
//			searchWord(text, this.infobox, results, isFieldQuery, false);
//			//System.out.println(results.size());
//			
//			searchWord(text, this.bodyText, results, isFieldQuery, false);
//			//System.out.println(results.size());




		}
		PriorityQueue<Item> res = new PriorityQueue<>();
		
		
//		for(String word: text.split(" ")) {
//			word = stemmer.getStem(word);
//			var tempRes = lookUp(secondaryIndexBody, "body", word);
//			var tempResTitle = lookUp(secondaryIndexTitle, "title", word);
//
//			for(Long id: tempRes.keySet()) {
//				if(results.containsKey(id)) {
//					results.put(id, results.get(id) + tempRes.get(id) );
//				}
//				else {
//					results.put(id, tempRes.get(id));
//				}
//			}
//			for(Long id: tempResTitle.keySet()) {
//				if(results.containsKey(id)) {
//					results.put(id, results.get(id) + ( 10 + tempResTitle.get(id) ) );
//				}
//				else {
//					results.put(id, (10 + tempResTitle.get(id) ) );
//				}
//			}
//			//results.putAll(	 );
//			//res.addAll(lookUp(secondaryIndexBody, "body", word));
//		}
		
		
		//System.out.println("Results:" + results.size());
		//results = sortByValue(results, false);
		//System.out.println("Results:" + results.size());
		int count = 0;
		String output = "-------------------------------------------------------------\n \n";
//		
        for(Long id: results.keySet()) {
        	Item e = new Item();
        	e.score = results.get(id);
        	e.docId = id;
        	e.isScore = true;
        	res.add(e);
        	continue;
        	//System.out.println(id);
        	//System.out.println(titleMapper.get("10"));
        	
        	//String mappedTitle = titleMapper.get( id.toString());
//        	System.out.println(mappedTitle + " : " +id.toString() + " : " +  results.get(id) );
        	//System.out.println(mappedTitle);
        	//output += mappedTitle + "\n";
        	//output += mappedTitle + "\n";
//        	String mappedTitle = this.getTitle(id, secondaryIndexTitle);
//        	output += mappedTitle + "\n";
//        	count += 1;
//
//        	if(count == 10)
//        		break;
        }
        
        while(!res.isEmpty()) {
			Item e = res.remove();
			String mappedTitle = this.getTitle(e.docId, secondaryIndexTitleMapper);
        	output += mappedTitle + "\n";
        	count += 1;

        	if(count == 10)
        		break;
			
		}
//
        //System.out.println();
        //output += "\n";
        System.out.println(output);
        return output;
		
	}
	
	
	
	

}
