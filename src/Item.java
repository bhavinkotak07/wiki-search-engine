
public class Item implements Comparable<Item>{
	public Long id;
	public Long count;
	@Override
    public boolean equals(Object o) { 
		Item i = (Item)o;
		if(i.id == this.id)
			return true;
		return false;
	}

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return ((Long)id).hashCode();
    }
    
    public int compareTo(Item c) {
        return this.count.compareTo(c.count);
    }
	
}
