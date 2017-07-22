/**
* @author R04921094 Ye, Meng-Yuan
**/

public class Record implements Comparable<Record>{
	public KeyType key;
	public int[] rid;
	public String content;

	public Record(KeyType key, String content){
		this.key = key;
		this.content = content;
	}

	public void setRid(int[] rid){
		this.rid = rid;
	}

	public int compareTo(Record that){
		return this.key.compareTo(that.key);
	}

	public String toString(){
		return "Key: " + key + ", val: " + content;
	}
}