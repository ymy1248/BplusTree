/**
* @author R04921094 Ye, Meng-Yuan
**/

import java.util.LinkedList;
import java.util.Collections;

public class Page{
	private static final int SIZE = 512;		// default 512 bytes size
	private static final int POINTER_SIZE = 2;

	private LinkedList<Integer> slotDir;
	private int size = SIZE;
	private int freeSpace;
	private int slotCount;
	private LinkedList<Record> recContent;
	private LinkedList<Integer> garbage;

	public int pageNum;

	public Page(int pageNum){
		this.freeSpace = 0;
		this.slotCount = 0;
		this.slotDir = new LinkedList<>();
		this.pageNum = pageNum;
		this.recContent = new LinkedList<>();
		this.garbage = new LinkedList<>();
	}

	public Page(int size, int pageNum){
		this(pageNum);
		this.size = size;
	}

	public int remainSpace(){
		return size - freeSpace -(4 + slotDir.size());
	}

	// can this record add into this page
	public boolean addable(Record record){
		return record.content.length() <= remainSpace();
	}

	public int[] addRecord(Record record){
		int[] rid = new int[2];
		int index = slotDir.indexOf(-1);
		int slotNum = 0;
		rid[0] = pageNum;
		if(index == -1){
			slotDir.addLast(freeSpace);
			slotDir.addLast(record.content.length());
			freeSpace += record.content.length();
			slotNum = ++slotCount - 1;
			recContent.addLast(record);
		} else {
			slotDir.remove(index);
			slotDir.remove(index+1);
			slotDir.add(index, freeSpace);
			slotDir.add(index+1, record.content.length());
			slotNum = index/2;
			recContent.addLast(record);
		}
		rid[0] = pageNum;
		rid[1] = slotNum;
		return rid;
	}

	public void deleteRecord(Record record){
		int slotNum = record.rid[1];
		garbage.add(slotNum);
		slotDir.remove(slotNum*2);
		slotDir.add(slotNum*2, -1);
	}

	public void garbageColl(){
		Collections.sort(garbage);
		for(int i = garbage.size()-1; i>=0; i--){
			int index = garbage.get(i);
			recContent.remove(index);
		}
		garbage.clear();
		slotDir.clear();							// reconstruct slotDir
		slotCount = recContent.size();
		int space = 0;
		for(Record r: recContent){					// for each record add to slotDir
			slotDir.add(space);
			slotDir.add(r.content.length());
			space += r.content.length();
		}
		freeSpace = space;
	}

	public String toString(){
		StringBuffer content = new StringBuffer();
		for(Record r: recContent){
			content.append(r.content+",");
		}
		for(int i = 0; i < remainSpace(); i++){
			content.append("*");
		}
		for(int i = slotDir.size() - 1; i>=0; i--){
			content.append(","+slotDir.get(i));
		}
		content.append("," + slotCount);
		content.append("," + freeSpace);

		return content.toString();
	}

	public static void main(String[] argv){
		Page page = new Page(12);
		KeyType key1 = new KeyType(Type.INTEGER, 1);
		KeyType key2 = new KeyType(Type.INTEGER, 2);
		KeyType key3 = new KeyType(Type.INTEGER, 3);
		Record record1 = new Record(key1, "1234567890");
		Record record2 = new Record(key2, "12345");
		Record record3 = new Record(key3, "12345678");
		record1.setRid(page.addRecord(record1));
		System.out.println(page);
		record2.setRid(page.addRecord(record2));
		System.out.println(page);
		record3.setRid(page.addRecord(record3));
		System.out.println(page);

		page.deleteRecord(record2);
		System.out.println(page);
		page.garbageColl();
		System.out.println(page);
		Record record4 = new Record(key1, "1234567");
		page.addRecord(record4);
		System.out.println(page);
	}
}