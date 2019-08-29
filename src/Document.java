import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigInteger; 

public class Document implements Runnable {
	public String text;
	public String title;
	public long id;
	public String category = "";
	public String infobox = "";
	public String references = "";
	public static long counter = 1;
	public ArrayList<String> bodyWordsList;
	public ArrayList<String> categoriesList;
	public ArrayList<String> refList;
	public ArrayList<String> titleList;
	public ArrayList<String> infoboxList;
	public Document() {
		id = counter;
		counter += 1;
	}
	
	public String getText() {
		return text;
	}

	public void run() {
		this.extractFields();
		this.tokenize();
		//System.out.println("Tokenization done" + id);
		//indexer.index(this);
		
	}
	public String processExternalLinks(String str) {
		Pattern externalLinksPattern = Pattern.compile("== ?external links ?==(.*?)\n\n", Pattern.DOTALL);
		Pattern UrlFtpFile = Pattern
				.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", Pattern.DOTALL);

		Matcher externalLinksMatcher = externalLinksPattern.matcher(str);
		if (externalLinksMatcher.find()) {
			// System.out.println(externalLinksMatcher.group());
			String externalLinks = externalLinksMatcher.group(1);
			Pattern http = Pattern.compile("(\\[https?:)(.*?) (.*?)(\\])", Pattern.DOTALL);
			Matcher match = http.matcher(externalLinks);
			String res = "";
			while (match.find()) {
				res = UrlFtpFile.matcher(match.group(3).toString()).replaceAll(" ");
				// tokenize(res.replaceAll("[^a-z]", " ").trim().replaceAll("\\s+"," "), 'L');
				//System.out.println("Link:" + res);
			}
		}
		str = externalLinksMatcher.replaceAll(" ");

		return str;
	}

	public ArrayList<String> findAll(String regex, String text) {
		ArrayList<String> results = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		Matcher match = pattern.matcher(text);

		while (match.find()) {
			// System.out.println(match.start() + " " + match.end() );
			// System.out.println(match.group());
			results.add(match.group());
		}
		match.replaceAll("");
		return results;
	}

	public String replaceAll(String regex, String text) {
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		Matcher match = pattern.matcher(text);

		return match.replaceAll(" ");
	}

	public void printList(ArrayList<String> res) {
		for (String s : res) {
			System.out.println(s);
		}
	}

	public void findReferences() {
		String regex = "(;notes)|(\\{\\{reflist.*?\\}\\})";
		ArrayList<String> res = findAll(regex, text);
		//printList(res);
	}

	public void findCategories() {
		String regex = "\\[\\[category.*?\\]\\]";
		// System.out.println(replaceAll(regex, "[[category:this is dog]] blah blah"));
		ArrayList<String> res = findAll(regex, text);
		categoriesList = new ArrayList<String>();
		//printList(res);
		for(int i = 0; i < res.size(); i++) {
			try {
				if(res.get(i).startsWith("[[category:") ) {
					String temp = res.get(i).substring(11, res.get(i).length() - 2).replaceAll("[^a-z0-9]", " ").trim().replaceAll("\\s+", " ");
					for(String s: temp.split(" ")) {
						categoriesList.add(s);
					}
				}
					
			}
			catch(Exception ex) {
				System.out.println("Exception:" + ex.getMessage());
			}
		}
		//categoriesList = res;
		//printList(res);

	}

	public void findURLS() {
		String regex = "https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s|^\\|]{2,}";
		ArrayList<String> res = findAll(regex, text);
		//printList(res);

	}

	public String deleteCitation(final String content) {
		// Deletes the citation from the content.
		final String CITE_PATTERN = "{{cite";

		// Find the start pos and end pos of citation.
		int startPos = content.indexOf(CITE_PATTERN);
		if (startPos < 0)
			return content;
		int bracketCount = 2;
		int endPos = startPos + CITE_PATTERN.length();
		for (; endPos < content.length(); endPos++) {
			switch (content.charAt(endPos)) {
			case '}':
				bracketCount--;
				break;
			case '{':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0)
				break;
		}

		// Discard the citation and search for remaining citations.
		final String text = content.substring(0, startPos - 1) + content.substring(endPos);
		return deleteCitation(text);
	}

	public String extractInfoBox(String content) {
		final String infoBoxPatterm = "{{infobox";
		int startPos = content.indexOf(infoBoxPatterm);
		if (startPos < 0)
			return "";
		int bracketCount = 2;
		int endPos = startPos + infoBoxPatterm.length();
		for (; endPos < content.length(); endPos++) {
			switch (content.charAt(endPos)) {
			case '}':
				bracketCount--;
				break;
			case '{':
				bracketCount++;
				break;
			default:
			}
			if (bracketCount == 0)
				break;
		}

		if (endPos + 1 >= content.length())
			return "";

		String infoBoxText = content.substring(startPos, endPos + 1);
		infoBoxText = deleteCitation(infoBoxText);
		infoBoxText = infoBoxText.replaceAll("&gt;", ">");
		infoBoxText = infoBoxText.replaceAll("&lt;", "<");
		infoBoxText = infoBoxText.replaceAll("<ref.*?>.*?</ref>", " ");
		infoBoxText = infoBoxText.replaceAll("</?.*?>", " ");
		
		infobox = infoBoxText;
		if (startPos - 1 < 0)
			content = content.substring(endPos);
		else
			content = content.substring(0, startPos - 1) + content.substring(endPos);
		
		return content;

	}
	public void findCategories(String text) {
		boolean isCategory = false;
		String[] lines = text.split("\n");

		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("[[category:")) {
				isCategory = true;
			} 
			if (isCategory) {
				category += new String(lines[i].substring(11, lines[i].length() - 2));
				category += ",";
				isCategory = false;

			} 
		}
		
	}
	public void extractFields() {

		this.findURLS();
		this.extractInfoBox(text);
		text = this.processExternalLinks(text);
		this.findCategories();
		this.findReferences();
		
	}
	

	public void setText(String text) {
		this.text = new String(text.toLowerCase());
		

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = new String(title);
	}

	public long getId() {
		return id;
	}

	

	public String getCategory() {
		return category;
	}

	public void tokenize() {
		text = text.replaceAll("[^a-z0-9]", " ").trim().replaceAll("\\s+", " ");
		text = text.replaceAll("&amp;", "");
		text = text.replaceAll("&quot;", "");
		
		infobox = infobox.replaceAll("[^a-z0-9]", " ").trim().replaceAll("\\s+", " ");
		infobox = infobox.replaceAll("&amp;", "");
		infobox = infobox.replaceAll("&quot;", "");
		
		String tt = title.toLowerCase().replaceAll("[^a-z0-9]", " ").trim().replaceAll("\\s+", " ");
		
		tt = tt.replaceAll("&amp;", "");
		tt = tt.replaceAll("&quot;", "");
		//text = text.replaceAll("", "");
		this.bodyWordsList = Stopwords.removeStopwords(text.split(" "));
		//printList(words);
		
		this.infoboxList = Stopwords.removeStopwords(infobox.split(" "));
		this.titleList = Stopwords.removeStopwords(tt.split(" "));
		this.categoriesList = Stopwords.removeStopwords(categoriesList);


		
	}

	public void showDocument() {
		System.out.println("Doc:" + this.getId());
		//System.out.println("Title:" + this.getTitle());
		// System.out.println("Category: " + category);
		// System.out.println("References:" + references);
		//System.out.println("Infobox:" + infobox);
		// System.out.println("Text:" + this.getText());
	}

	public String toString() {
		return "Title:" + title + "\n" + "Text:" + text;
	}

	public void finalize() {
		// System.out.println("Finalize");
	}
}
