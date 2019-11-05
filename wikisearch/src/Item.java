
public class Item implements Comparable<Item>{
	int fp;
	String data;
	long pointer;
	boolean isNumber;
	boolean isScore;
	Long docId;
	Double score;
	@Override
    public boolean equals(Object o) { 
		Item i = (Item)o;
		return this.data.equals(i.data);
	}

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return this.data.hashCode();
    }
    
    public int compareTo(Item c) {
    	if(c.isScore) {
    		return c.score.compareTo(this.score);
    	}
    	if(c.isNumber) {
    		Long a = Long.parseLong(this.data);
    		Long b = Long.parseLong(c.data);
    		
    		return  a.compareTo(b);
    	}
        return this.data.compareTo(c.data);
    }
	
}
