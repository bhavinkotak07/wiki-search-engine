import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;



public class Search {
	
	HashMap<String, HashMap<Long, Long>> bodyText;
	HashMap<String, HashMap<Long, Long>> categories;
	HashMap<String, HashMap<Long, Long>> infobox;
	HashMap<String, HashMap<Long, Long>> title;

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
		loadIndex(path + "body.txt", bodyText);
		loadIndex(path + "categories.txt", categories);
		loadIndex(path + "infobox.txt", infobox);
		loadIndex(path + "title.txt", title);

		loadTitleMapper(path + "titleMapper.txt");
		
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
	private static Map<Long, Long> sortByValue(Map<Long, Long> unsortMap, final boolean order)
    {
        List<Entry<Long, Long>> list = new LinkedList<>(unsortMap.entrySet());

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
	public void query(String text) {
		
		text = text.toLowerCase();
		text = text.trim().replaceAll("\\s+", " ");
		String title = "";
		String category = "";
		String body = "";
		String ref = "";
		String infobox = "";

		boolean isFieldQuery = false;
		Map<Long, Long> results = new HashMap<Long, Long>();

		if(text.contains(":")) {
			isFieldQuery = true;
			for(String s: text.split(" ")) {
				if(s.split(":")[0].equals("title")) {
					title = s.split(":")[1];
				}
				if(s.split(":")[0].equals("body")) {
					body = s.split(":")[1];
				}
				if(s.split(":")[0].equals("category")) {
					category = s.split(":")[1];
				}
				if(s.split(":")[0].equals("ref")) {
					ref = s.split(":")[1];
				}
				if(s.split(":")[0].equals("infobox")) {
					infobox = s.split(":")[1];
				}
			}
			searchWord(category, this.categories, results, isFieldQuery, false);
//			System.out.println(results.size());
			searchWord(title, this.title, results, isFieldQuery, true);
//			System.out.println(results.size());			

			

			searchWord(infobox, this.infobox, results, isFieldQuery, false);
//			System.out.println(results.size());
			
			searchWord(body, this.bodyText, results, isFieldQuery, false);
			//System.out.println(results.size());

		}
		else {
			searchWord(text, this.title, results, isFieldQuery, true);
			//System.out.println(results.size());			

			searchWord(text, this.categories, results, isFieldQuery, false);
			//System.out.println(results.size());

			searchWord(text, this.infobox, results, isFieldQuery, false);
			//System.out.println(results.size());
			
			searchWord(text, this.bodyText, results, isFieldQuery, false);
			//System.out.println(results.size());




		}
		

		//System.out.println("Results:" + results.size());
		results = sortByValue(results, false);
		//System.out.println("Results:" + results.size());
		int count = 0;
        for(Long id: results.keySet()) {
        	//System.out.println(id);
        	count += 1;
        	//System.out.println(titleMapper.get("10"));
        	
        	String mappedTitle = titleMapper.get( id.toString());
//        	System.out.println(mappedTitle + " : " +id.toString() + " : " +  results.get(id) );
        	System.out.println(mappedTitle);
        	if(count == 10)
        		break;
        }
//		
        System.out.println();
		
	}
	
	
	
	

}
