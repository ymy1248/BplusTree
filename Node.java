/**
* @author R04921094 Ye, Meng-Yuan
**/

import java.util.LinkedList;
import java.util.Collections;

abstract class Node{
	public int d;
	public LinkedList<KeyType> keys;

	public Node(int d){
		this.d = d;
		keys = new LinkedList<>();
	}

	public int findChild(KeyType key){
		int i = Collections.binarySearch(keys, key);
		if(i<0){
			return -( i + 1);	// between two key, get the child
		} else {
			return i+1;			// equals to key or greater
		}
	}

	public int findKey(KeyType key){
		int i = Collections.binarySearch(keys, key);
		if(i<0){
			return -( i + 1);	// between two key, get the child
		} else {
			return i;			// equals to key or greater
		}
	}

	public boolean addFull(){
		if(keys.size() + 1 > 2*d){
			return true;
		} else{
			return false;
		}
	}

	public boolean rebuildable(){
		if(keys.size() > d){
			return true;
		} else{
			return false;
		}
	}
}