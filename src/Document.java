import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {
	public String text;
	public String title;
	public String id;
	public String category = "";
	public String infobox = "";
	public String references = "";

	public Document(int id) {
		this.id = String.valueOf(id);
		// System.out.println(id);
	}

	public String getText() {
		return text;
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
				System.out.println("Link:" + res);
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
		printList(res);
	}

	public void findCategories() {
		String regex = "\\[\\[category.*?\\]\\]";
		// System.out.println(replaceAll(regex, "[[category:this is dog]] blah blah"));
		ArrayList<String> res = findAll(regex, text);

	}

	public void findURLS() {
		String regex = "https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s|^\\|]{2,}";
		ArrayList<String> res = findAll(regex, text);
		printList(res);

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
		final String infoBoxPatterm = "{{Infobox";
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
		;
	}
	public void extractFields() {

		this.findURLS();
		infobox = this.extractInfoBox(text);
		text = this.processExternalLinks(text);
		this.findCategories();
		this.findReferences();
		
	}

	public void setText(String text) {
		this.text = new String(text.toLowerCase());
		this.extractFields();
		this.tokenize();

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = new String(title);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void tokenize() {
		text = text.replaceAll("[^a-z0-9]", " ").trim().replaceAll("\\s+", " ");
		text = text.replaceAll("&amp;", "");
		text = text.replaceAll("&quot;", "");


		//text = text.replaceAll("", "");
		ArrayList<String> words = Stopwords.removeStopwords(text.split(" "));
		//printList(words);

		
	}

	public void showDocument() {
		System.out.println("Doc");
		System.out.println("Title:" + this.getTitle());
		// System.out.println("Category: " + category);
		// System.out.println("References:" + references);
		// System.out.println("Infobox:" + infobox);
		// System.out.println("Text:" + this.getText());
	}

	public String toString() {
		return "Title:" + title + "\n" + "Text:" + text;
	}

	public void finalize() {
		// System.out.println("Finalize");
	}
}
