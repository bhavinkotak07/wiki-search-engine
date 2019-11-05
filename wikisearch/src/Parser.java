import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Parser {
	
	public static void main(String argv[]) throws InterruptedException, IOException {
		long startTime = System.currentTimeMillis();
		String indexPath = "../../index1" + "/";
		//String indexPath = "";
		String queryFilePath = "";
		String outputFilePath = "";
		String dataDumpPath = "../../data.xml";
		//int task = Integer.parseInt(argv[0]);
		
		int task = 4;
		if(task == 5) {
			try {
				RandomAccessFile file = new RandomAccessFile("../../index1/titleMapper.txt", "r");
//				int count = 0;
//				while(true) {
//					String t = file.readLine();
//					count += t.getBytes().length + 1;
//					System.out.println(count);
//					file.seek(count);
//					if(t == null)
//						break;
//				}
				file.seek(219928);
				System.out.println(file.readLine() );
				file.close();
			}
			catch(Exception ex) {
				
			}
		}
		if(task == 4) {
			Search search = new Search("");
			Scanner sc = new Scanner(System.in);
			while(true) {
				System.out.println();
				System.out.print("Search:");

				String query = sc.nextLine();
				System.out.println("Searching ...");
				startTime = System.currentTimeMillis();

				search.query(query);
				long endTime = System.currentTimeMillis();

				System.out.println("That took " + (endTime - startTime)  + " milli seconds");	

			}
			
					
		}
		if(task == 3) {
			Merger merger = new Merger();
			//merger.createSecondaryIndex();
			merger.secondaryIndexForTitle();
			//merger.merge(indexPath);
		}
		if(task == 2) {
//			dataDumpPath = argv[1];
//			indexPath = argv[2];
			if(indexPath.charAt(indexPath.length() - 1) != '/') {
				indexPath += "/";
			}
//			File directory = new File(indexPath);
//		    if (! directory.exists()){
//		        directory.mkdir();
//		        // If you require it to make the entire directory path including parents,
//		        // use directory.mkdirs(); here instead.
//		    }
			WikiParser parser = new WikiParser();
			parser.parse(dataDumpPath, indexPath);
			long endTime = System.currentTimeMillis();

			System.out.println("That took " + (endTime - startTime) / 1000 + " seconds");
		}
		if(task == 1) {
			indexPath = argv[1];
			if(indexPath.charAt(indexPath.length() - 1) != '/') {
				indexPath += "/";
			}
			queryFilePath = argv[2];
			outputFilePath = argv[3];
			Search search = new Search(indexPath);
			String outputData = "";
			try {
				System.out.println("Searching...");
				File f = new File(queryFilePath );
				
	            BufferedReader b = new BufferedReader(new FileReader(f));

	            String readLine = "";
	            while ((readLine = b.readLine()) != null) {
	            	outputData += search.query(readLine);
	            }
	            System.out.println(outputData);
	            
	            FileWriter fw = null;
	    		try {
	    			fw = new FileWriter(outputFilePath);
	    			
	    			fw.write(outputData);			

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
	    		
	    		
	            b.close();	            
	            		
			}
			catch(Exception ex) {
				System.out.println(ex);
			}
		}
		
				
		

	}
}
