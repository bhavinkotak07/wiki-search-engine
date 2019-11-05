import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

public class Merger {
	public void closeAll(ArrayList<BufferedReader> files) {
		for(var x: files)
			try {
				x.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public ArrayList<BufferedReader> getAllFiles(String path) {
		File f = null;
		int count = 0;

		String p = path + count + ".txt";
		System.out.println(path);
		ArrayList<BufferedReader> files = new ArrayList<BufferedReader>();
		try {
			while( Files.exists(Paths.get(p) )  ){
				//System.out.println(f.getAbsolutePath());
				System.out.println(p);
				f = new File(p);
				BufferedReader b = new BufferedReader(new FileReader(f));
				files.add(b);

				count += 1;
				p = path  + count + ".txt";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return files;
	}
	public void mergeHelper(ArrayList<BufferedReader> files, String name) {
		PriorityQueue<Item> queue = new PriorityQueue<Item>();
		
		String previous = "";
		int mod = 5000;
		String filename = "main/primary_index_" + name + ".txt";
		String secondaryIndexName = "main/secondary_index_" + name + ".txt";
		int filesOpen = files.size();
		HashSet<Integer> set = new HashSet<Integer>();
		try {
			String last = "";
			//FileWriter writer = new FileWriter(filename);
			RandomAccessFile primaryIndexFile = new RandomAccessFile(filename, "rw");
			FileWriter secondaryWriter = new FileWriter(secondaryIndexName);
			int count = 0;
			while(filesOpen != 0) {
				if(queue.isEmpty()) {
					for(int i = 0; i < files.size(); i++) {
						Item item = new Item();
						try {
							item.data = files.get(i).readLine();
							item.fp = i;
							
							if(item.data != null)
								queue.add(item);
							else{
								filesOpen -= 1;
								set.add(i);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
				else {
					Item temp = queue.remove();
					if(count % mod == 0) {
						secondaryWriter.write(temp.data.split(" ")[0] + " " + primaryIndexFile.getFilePointer() + "\n");
					}
					count += 1;
					if(temp.data.split(" ")[0].equals(previous) ) {
						String data = "," + temp.data.split(" ")[1];
						//writer.write(data);
						primaryIndexFile.writeBytes(data);
					}
					else {
						String data = "\n" + temp.data;

						//writer.write(data);
						primaryIndexFile.writeBytes(data);

						previous = temp.data.split(" ")[0];
					}
					last = temp.data.split(" ")[0];
					

					if(set.contains(temp.fp)) {
						continue;
					}
					String str = files.get(temp.fp).readLine();
					if(str != null) {
						Item e = new Item();
						e.data = str;
						e.fp = temp.fp;
						queue.add(e);
						
					}
					else {
						set.add(temp.fp);
						filesOpen -= 1;
					}
				}
				
				
				
			}
			secondaryWriter.write(last + " " + primaryIndexFile.getFilePointer() + "\n");

			System.out.println(queue.size());
			while(!queue.isEmpty()) {
				Item temp = queue.remove();
				if(temp.data.split(" ")[0].equals(previous) ) {
					//writer.write("," + temp.data.split(" ")[1]);
					primaryIndexFile.writeBytes("," + temp.data.split(" ")[1]);

				}
				else {
					count += 1;
					previous = temp.data.split(" ")[0];

					if(count % mod == 0) {
						secondaryWriter.write(previous + " " + primaryIndexFile.getFilePointer() + "\n");

					}
					primaryIndexFile.writeBytes("\n" + temp.data);
					//writer.write("\n" + temp.data);
					
					
				}
			}
			secondaryWriter.write(previous + " " + primaryIndexFile.getFilePointer() + "\n");


			//writer.close();
			primaryIndexFile.close();
			secondaryWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	public void createSecondaryIndex() {
		createSecondaryIndex("body");
		createSecondaryIndex("categories");
		createSecondaryIndex("title");
		createSecondaryIndex("infobox");


	}
	public void createSecondaryIndex(String filename) {
		RandomAccessFile primaryIndexFile = null;
		FileWriter secondaryWriter = null;
		FileWriter primaryWriter = null;
		BufferedReader br = null;
		try {
			//primaryIndexFile = new RandomAccessFile("main/primary_index_" + filename + ".txt", "r");
			br = new BufferedReader(new FileReader("main/primary_index_" + filename + ".txt"));
			secondaryWriter = new FileWriter("secondary_index_" + filename + ".txt");
			String line = "";
			System.out.println(filename.toUpperCase());
			long count = 0;
			int fileNumber = 0;
			long offset = 0;
			primaryWriter = new FileWriter("primary/primary_index_" + filename + fileNumber + ".txt");
			while((line = br.readLine() ) != null) {
				if(count == 0) {
					count += 1;
					continue;

				}
				if((count ) % 10000 == 0) {
					offset = 0;
					fileNumber += 1;

					primaryWriter = new FileWriter("primary/primary_index_" + filename + fileNumber + ".txt");

				}
				primaryWriter.write(line + "\n");
				if( (count ) % 5000 == 0) {
					String temp = "";
					for(int i = 0; i < line.length(); i++) {
						
						if(line.charAt(i) == ' ') {
							break;
						}
						temp += line.charAt(i);
					}
					secondaryWriter.write(temp + " " + fileNumber + " " + offset + "\n");
				}
				offset += line.getBytes().length + 1;

				
				count += 1;


			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				br.close();
				secondaryWriter.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void secondaryIndexForTitle() {
		//RandomAccessFile primaryIndexFile = null;
		FileWriter secondaryWriter = null;
		BufferedReader br = null;
		try {
			//primaryIndexFile = new RandomAccessFile("../../index1/titleMapper.txt", "r");
			br = new BufferedReader(new FileReader("../../index1/titleMapper.txt"));
			secondaryWriter = new FileWriter("main/secondary_index_titleMapper.txt");
			String line = "";
			long count = 0;
			long offset = 0;
			while((line = br.readLine() ) != null) {
				if( (count) % 5000 == 0) {
					String temp = "";
					for(int i = 0; i < line.length(); i++) {
						
						if(line.charAt(i) == ':') {
							break;
						}
						temp += line.charAt(i);
					}
					secondaryWriter.write(temp + " " + offset + "\n");
				}
				
				offset += line.getBytes().length + 1;
				count += 1;

			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				br.close();
				secondaryWriter.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void merge(String path) {
		
		var files = getAllFiles(path + "body");
//		mergeHelper(files, "body");
//		closeAll(files);
//
//		
//		files = getAllFiles(path + "categories");
//		mergeHelper(files, "categories");
//		closeAll(files);
//
//		
//		files = getAllFiles(path + "infobox");
//		mergeHelper(files, "infobox");
//		closeAll(files);

		
		files = getAllFiles(path + "title");
		mergeHelper(files, "title");		
		closeAll(files);
		
		
	}

}
