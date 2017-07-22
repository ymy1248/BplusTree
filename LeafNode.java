/**
* @author R04921094 Ye, Meng-Yuan
**/

import java.util.LinkedList;

public class LeafNode extends Node{
	private static final String TAG = "LeafNode: ";

	public LeafNode left;
	public LeafNode right;
	public LinkedList<Record> records;

	public LeafNode(int d, LeafNode left, LeafNode right){
		super(d);
		this.left = left;
		this.right = right;
		// rids = new ArrayList<>();
		records = new LinkedList<>();
	}

	/**
	* Add a new record into the leafnode
	* @return null if LeafNode has space, else return split new node
	**/
	public LeafNode addRecord(Record record){
		int insert = findChild(record.key);
		//System.out.println(insert);
		if(keys.size() < 2*d){
			// rids.add(insert, record.rid);
			records.add(insert, record);
			keys.add(insert, record.key);
			return null;
		} else{
			// split
			 LeafNode newNode = new LeafNode(d, this, right);
			 if(right != null){
			 	right.left = newNode;
			 }
			 // copy record into new node
			 while(records.size() > d){
			 	newNode.pushRecord(records.removeLast());
			 	keys.removeLast();
			 }
			 if(insert > d){
			 	// add record into new leafnode
			 	int newIndex = insert - d;
			 	newNode.records.add(newIndex, record);
			 	newNode.keys.add(newIndex, record.key);
			 }else{
			 	// add into original leafnode
			 	records.add(insert, record);
			 	keys.add(insert, record.key);
			 }
			 // System.out.println(this);
			 return newNode;
		}
	}

	// public void setRid(int index, int rid){
	// 	rids.add(index, rid);
	// }

	// public int getRid(int index){
	// 	return rids.get(index);
	// }

	// public int delete(int index){
	// 	return rids.remove(index);
	// }

	public KeyType rebuild(LeafNode cousin, int position){
		int thisSize = records.size();
		int cousinSize = cousin.records.size();
		// +1 come from text book fig. 10.19 -> 10.20
		// syntactic sugar
		int newSize = (thisSize + cousinSize + 1)/2;
		if(position == BplusTree.LEFT){
			while(records.size() < newSize){
				records.push(cousin.records.removeLast());
				keys.push(cousin.keys.removeLast());
			}
			return keys.getFirst();
		} else{					// right
			while(records.size() < newSize){
				records.add(cousin.records.pop());
				keys.add(cousin.keys.pop());
			}
			return cousin.keys.getFirst();
		}
	}

	public void pushRecord(Record record){
		records.push(record);
		keys.push(record.key);
	}

	public Record getRecord(int index){
		return records.get(index);
	}

	public String toString(){
		String s = "LeafNode: ";
		for(int i = 0; i < keys.size(); i++){
			// s += "Key: " + keys.get(i) + ", Record: " + records.get(i).content + "; ";
			s += keys.get(i) + ",";
		}
		return s;
	}

	public static void main(String[] argv){
		// LeafNode leafNode = new LeafNode(10, null, null);
		// KeyType keyType1 = new KeyType(Type.INTEGER, 5);
		// KeyType keyType2 = new KeyType(Type.INTEGER, 10);
		// KeyType keyType3 = new KeyType(Type.INTEGER, 7);
		// Record record1 = new Record(keyType1, Integer.parseUnsignedInt("20"), "this is record1", 100);
		// Record record2 = new Record(keyType2, Integer.parseUnsignedInt("40"), "this is record2", 100);
		// Record record3 = new Record(keyType3, Integer.parseUnsignedInt("59"), "this is record3", 100);

		// leafNode.addRecord(record1);
		// leafNode.addRecord(record2);
		// leafNode.addRecord(record3);

		// System.out.println(leafNode.keys.size());
		// KeyType[] keys = leafNode.keys.toArray(new KeyType[leafNode.keys.size()]);

		// for(int i = 0; i<keys.length; i++){
		// 	System.out.println((Integer)keys[i].value);
		// }
		LeafNode node = new LeafNode(3, null, null);
		LeafNode left = new LeafNode(3, null, null);

		Record[] records = new Record[4];
		KeyType[] keys = new KeyType[4];

		keys[0] = new KeyType(Type.INTEGER, 5);
		keys[1] = new KeyType(Type.INTEGER, 10);
		keys[2] = new KeyType(Type.INTEGER, 7);
		keys[3] = new KeyType(Type.INTEGER, 19);
		records[0] = new Record(keys[0], "this is record1");
		records[1] = new Record(keys[1], "this is record2");
		records[2] = new Record(keys[2], "this is record3");
		records[3] = new Record(keys[3], "this is record4");

		for(int i = 0; i<2; i++){
			node.addRecord(records[i]);
		}

		for(int i = 0; i<4; i++){
			left.addRecord(records[i]);
		}

		System.out.println(node);
		System.out.println(left);

		node.rebuild(left, BplusTree.LEFT);

		System.out.println(node);
		System.out.println(left);
	}
}