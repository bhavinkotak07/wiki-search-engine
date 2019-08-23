import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Stopwords {
	public static String stopWordsFilename = "src/stopwords.txt";
	static HashSet<String> stopWords = null;
	static {
		readStopWords();
	}

	public static void readStopWords() {

		try {
			stopWords = new HashSet<String>();
			File f = new File(stopWordsFilename);
			BufferedReader b = new BufferedReader(new FileReader(f));

			String readLine = "";
			try {
				while ((readLine = b.readLine()) != null) {

					stopWords.add(readLine);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

	}

	public static boolean isStopWord(String text) {
		if (stopWords.contains(text))
			return true;
		return false;
	}

	public static ArrayList<String> removeStopwords(String[] strings) {
		ArrayList<String> res = new ArrayList<String>();

		for (String word : strings) {
			if (Stopwords.isStopWord(word) == false && word.length() > 2) {
				String stem = Stemmer.getStem(word);
				res.add(Stemmer.getStem(stem));
			}

		}
		return res;
	}

	public static ArrayList<String> removeStopwords(ArrayList<String> strings) {
		ArrayList<String> res = new ArrayList<String>();
		for (String word : strings) {
			if (Stopwords.isStopWord(word) == false && word.length() > 2) {
				res.add(Stemmer.getStem(word));
			}

		}
		return res;
	}

}
