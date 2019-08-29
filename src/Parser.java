import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Parser {
	
	public static void main(String argv[]) throws InterruptedException {
		long startTime = System.currentTimeMillis();
		String indexPath = "index" + "/";
		
		int task = Integer.parseInt(argv[0]);
		String path = argv[1];
		if(task == 2) {
			WikiParser parser = new WikiParser();
			parser.parse("../data.xml", indexPath);
			long endTime = System.currentTimeMillis();

			System.out.println("That took " + (endTime - startTime) / 1000 + " seconds");
		}
		else {
			Search search = new Search(indexPath);
			try {
				File f = new File(path );

	            BufferedReader b = new BufferedReader(new FileReader(f));

	            String readLine = "";

	            while ((readLine = b.readLine()) != null) {
	                search.query(readLine);
	            }
	            b.close();	            
	            		
			}
			catch(Exception ex) {
				System.out.println(ex);
			}
			search.query("Gandhi");
			search.query("War");
			search.query("new york mayor");

			search.query("napier");

			
			search.query("title:gandhi body:arjun infobox:gandhi category:gandhi ref:gandhi");
		}
		
				
		

	}
}
