/**
* @author R04921094 Ye, Meng-Yuan
**/
import java.util.LinkedList;

public class BplusTree{
	private static final String TAG = "BplusTree: ";
	// delete message
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int NO_DELETE = 2;

	public int d;
	public int recordLength;
	public Type type;
	public String name;
	public Node root;

	private PageManager pm;

	public BplusTree(String name, Type type, int d, int recordLength){
		this.d = d;
		this.type = type;
		this.name = name;
		this.recordLength = recordLength;
		this.pm = new PageManager();
		this.root = new LeafNode(d, null, null);
	}

	public Record query(String strKey){
		KeyType key;

		if(type == Type.INTEGER){
			key = new KeyType(type, Integer.parseInt(strKey));
		} else{
			key = new KeyType(type, strKey);
		}
		return search(root, key);
	}

	public Record[] query(String strKey1, String strKey2){
		KeyType key1;
		KeyType key2;

		if(type == Type.INTEGER){
			key1 = new KeyType(type, Integer.parseInt(strKey1));
			key2 = new KeyType(type, Integer.parseInt(strKey2));
		} else{
			key1 = new KeyType(type, strKey1);
			key2 = new KeyType(type, strKey2);
		}

		return rangeQuery(root, key1, key2);
	}

	public int[] insert(Record record){
		record.setRid(pm.addRecord(record));
		if(root instanceof TreeNode){
			TreeNode newNode = (TreeNode)insert(root, record);
			if(newNode != null){
				TreeNode newRoot = new TreeNode(d);
				// System.out.println("newRoot " + newNode);
				newRoot.createRoot(root, newNode);
				root = newRoot;
			}
			return record.rid;
		} else{
			// the initial state
			LeafNode leafRoot = (LeafNode) root;
			LeafNode newNode = leafRoot.addRecord(record);
			if(newNode == null){
				return record.rid;
			} else{
				// System.out.println("newRoot" + newNode);
				TreeNode newRoot = new TreeNode(d);
				newRoot.createRoot(leafRoot ,newNode);
				root = newRoot;
				return record.rid;
			}
		}
	}

	public void delete(KeyType key){
		if(root instanceof TreeNode){
			TreeNode treeRoot = (TreeNode) root;
			int pPointer = treeRoot.findChild(key);
			int left = pPointer - 1;
			int right = pPointer + 1;
			Node next = treeRoot.children.get(pPointer);
			// System.out.println("Root findChild:" + next);
			int message = delete(next, treeRoot, pPointer, key);
			if(message == LEFT){
				treeRoot.children.remove(left);
				treeRoot.keys.remove(left);
			} else if(message == RIGHT){
				treeRoot.children.remove(right);
				treeRoot.keys.remove(pPointer);
			} else if(message == NO_DELETE){
				return;
			} else {
				// System.out.println(TAG + "delete(): something wrong!");
			}
			// System.out.println("root: " + treeRoot);
			if(treeRoot.keys.size() == 0){
				root = next;
			}
		} else {
			LeafNode leafRoot = (LeafNode) root;
			int i = leafRoot.findChild(key);
			// System.out.println("size:" + leafRoot.keys.size());
			leafRoot.keys.remove(i);
			leafRoot.records.remove(i);
		}
	}

	public int scanIndexPage(){
		LinkedList<Node> queue = new LinkedList<>();
		int num = 0;

		if(root instanceof LeafNode){
			// initial state
			return 0;
		}

		queue.add(root);
		while(!queue.isEmpty()){
			TreeNode n = (TreeNode)queue.remove();
			if(n.children.get(0) instanceof TreeNode){
				queue.addAll(n.children);
			}
			num++;
		}
		return num;
	}

	public int[] scan(){
		// bfs for scan
		LinkedList<Node> queue = new LinkedList<>();
		int[] out = new int[2];
		out[0] = 0;
		out[1] = 0;

		queue.add(root);
		while(!queue.isEmpty()){
			Node n = queue.remove();

			if(n instanceof TreeNode){
				TreeNode tn = (TreeNode) n;
				out[1]++;
				queue.addAll(tn.children);
			} else {
				out[0]++;
			}
		}

		return out;
	}

	public String getPageString(int i){
		// private access !
		return pm.getPageString(i);
	}

	public int getPageNum(){
		return pm.getPageListSize();
	}

	private Record search(Node node, KeyType key){
		if(node instanceof LeafNode){
			LeafNode leaf = (LeafNode)node;
			int i = leaf.findKey(key);
			// System.out.println("Keys size: " + leaf.keys.size() + ", Records size: " + leaf.records.size());
			if(i<0 || i>=leaf.keys.size()){
				return null;
			}
			return leaf.getRecord(i);
		} else{
			TreeNode tree = (TreeNode)node;
			int i = tree.findChild(key);
			Node next;
			next = tree.children.get(i);
			return search(next, key);
		}
	}

	private Record[] rangeQuery(Node node, KeyType key1, KeyType key2){
		// TODO rangequery
		LinkedList<Record> l = new LinkedList<>();
		if(node instanceof LeafNode){
			LeafNode leaf = (LeafNode)node;
			int i = leaf.findKey(key1);
			Record r;		// initial record
			if(i >= leaf.keys.size()){
				// shit! in the last element of that node
				if(leaf.right != null){
					i = 0;
					r = leaf.getRecord(i);
					leaf = leaf.right;
				} else{
					return null;
				}
			} else{
				r = leaf.getRecord(i);
			}
			while(r.key.compareTo(key2) != 1){	// less than or equal to
				l.add(r);
				if(i < leaf.keys.size() -1){
					r = leaf.getRecord(++i);	// leftCousin
				} else if(i == leaf.keys.size() - 1 && leaf.right != null){
					// get the last child go to right
					i = 0;
					leaf = leaf.right;
					r = leaf.getRecord(i);
				} else if(i == leaf.keys.size() - 1 && leaf.right == null){
					// no more right
					return l.toArray(new Record[l.size()]);
				} else{
					System.out.println(TAG + 
						"rangeQuery() something wrong, but i can't deal");
					return l.toArray(new Record[l.size()]);
				}
			}
			return l.toArray(new Record[l.size()]);
		} else{
			TreeNode tree = (TreeNode)node;
			int i = tree.findChild(key1);
			Node next;
			next = tree.children.get(i);
			return rangeQuery(next, key1, key2);
		}
	}

	private Node insert(Node node, Record record){
		if(node instanceof LeafNode){
			LeafNode leaf = (LeafNode)node;
			LeafNode newNode = leaf.addRecord(record);
			if(newNode == null){
				// no splite
				return null;
			} else{
				// splite
				leaf.right = newNode;
				newNode.left = leaf;
				return newNode;
			}
		} else if (node instanceof TreeNode){
			TreeNode tn = (TreeNode)node;
			// int i = tree.findChild(record.key);
			Node next = tn.findChildNode(record.key);
			Node newNode = insert(next, record);

			if(newNode == null){
				return null;
			} else{
				// System.out.println("NewNode: " + newNode);
				return tn.addChild(newNode);
			}
		} else{
			return null;
		}
	}

	private int delete(Node node, TreeNode parent, int pPointer, KeyType key){
		if(node instanceof LeafNode){
			LeafNode ln = (LeafNode) node;
			int i = node.findKey(key);
			int left = pPointer - 1;
			int right = pPointer + 1;
			// System.out.println("delete key: " + ln.keys.get(i));
			ln.keys.remove(i);
			ln.records.remove(i);
			if(ln.keys.size() >= d){		// normal case
				return NO_DELETE;
			} else{							// rebuild or merge
				int parentSize = parent.keys.size();
				if(left >= 0){						// check left neighbour
					LeafNode leftCousin = (LeafNode)parent.children.get(left);
					if(leftCousin.rebuildable()){
						KeyType newKey = ln.rebuild(leftCousin, LEFT);
						parent.keys.set(left, newKey);
						return NO_DELETE;
					}
				} 
				if(right <= parentSize + 1){		// check right neighbour
					LeafNode rightCousin = (LeafNode)parent.children.get(right);
					if(rightCousin.rebuildable()){
						KeyType newKey = ln.rebuild(rightCousin, RIGHT);
						parent.keys.set(pPointer, newKey);
						return NO_DELETE;
					}
				}

				// merge
				if(left >= 0){							// merge left
					// System.out.println("Left:" + left);
					LeafNode leftCousin = (LeafNode)parent.children.get(left);

					for(Record r: leftCousin.records){
						ln.addRecord(r);
					}
					// reassign neighbour pointer
					ln.left = leftCousin.left;
					if(leftCousin.left != null){
						leftCousin.left.right = ln;
					}
					return LEFT;
				} else if( right <= parentSize){			 // merge right
					LeafNode rightCousin = (LeafNode)parent.children.get(right);
					for(Record r: rightCousin.records){
						ln.addRecord(r);
					}
					// reassign neighbour pointer
					ln.right = rightCousin.right;
					if(rightCousin.right != null){
						rightCousin.right.left = ln;
					}
					return RIGHT;
				}
			}
		} else{		// tree node
			TreeNode tn = (TreeNode)node;
			int i = tn.findChild(key);
			int leftChild = i - 1;
			int rightChild = i + 1;
			int nextPointer;
			Node next;
			next = tn.children.get(i);				// between two key, get the child
			nextPointer = i;
			int message = delete(next, tn, nextPointer, key);
			if(message == NO_DELETE){
				return NO_DELETE;
			}
			// deletetion require
			if(message == LEFT){
				tn.children.remove(leftChild);
				tn.keys.remove(leftChild);
			} else{
				tn.children.remove(rightChild);
				// System.out.println("pP:" + pPointer);
				tn.keys.remove(nextPointer);
				
			}

			if(tn.keys.size() >= d){
				// usual case size >= d
				return NO_DELETE;
			}

			// rebuild
			int parentSize = parent.keys.size();
			if(leftChild >= 0){						// check left neighbour
				TreeNode leftCousin = (TreeNode)parent.children.get(leftChild);
				if(leftCousin.rebuildable()){
					KeyType parentKey = parent.keys.get(leftChild);
					KeyType newKey = tn.rebuild(leftCousin, 
						LEFT, parentKey);
					parent.keys.set(leftChild, newKey);
					return NO_DELETE;
				}
			} else if(rightChild <= parentSize){		// check right neighbour
				TreeNode rightCousin = (TreeNode)parent.children.get(rightChild);
				if(rightCousin.rebuildable()){
					KeyType parentKey = parent.keys.get(pPointer);
					KeyType newKey = tn.rebuild(rightCousin,
						RIGHT, parentKey);
					parent.keys.set(pPointer, newKey);
					return NO_DELETE;
				}
			}

			// merge
			int left = pPointer - 1;
			int right = pPointer + 1;
			if(left >= 0){							// merge left
				TreeNode leftCousin = (TreeNode)parent.children.get(left);
				KeyType parentKey = parent.keys.get(left);
				// pull parent key down
				tn.keys.push(parentKey);
				while(!leftCousin.keys.isEmpty()){
					KeyType leftKey = leftCousin.keys.removeLast();
					tn.keys.push(leftKey);
				}
				while(!leftCousin.children.isEmpty()){
					Node leftChildNode = leftCousin.children.removeLast();
					tn.children.push(leftChildNode);
				}
				// deliever message to parent to delete left
				return LEFT;
			} else if( right <= parentSize){
				TreeNode rightCousin = (TreeNode)parent.children.get(right);
				// System.out.println("pPointer: " + pPointer);
				// System.out.println("Right: " + rightCousin);
				// System.out.println("This:" + tn);
				KeyType parentKey = parent.keys.get(pPointer - 1);
				// pull parent key down
				tn.keys.add(parentKey);
				while(!rightCousin.keys.isEmpty()){
					KeyType rightKey = rightCousin.keys.remove();
					tn.keys.add(rightKey);
				}
				while(!rightCousin.children.isEmpty()){
					Node rightChildNode = rightCousin.children.remove();
					tn.children.add(rightChildNode);
				}
				return RIGHT;
			}
		}
		return 4;
	}

	public String toString(){
		LinkedList<Node> queue = new LinkedList<>();
		String s = "";

		queue.add(root);
		while(!queue.isEmpty()){
			Node n = queue.remove();
			if(n instanceof TreeNode){
				TreeNode tn = (TreeNode)n;
				s += tn + "\n";
				queue.addAll(tn.children);
			} else{
				LeafNode ln = (LeafNode)n;
				s += ln + "\n";
			}
		}
		return s + "\n";
	}

	public static void main(String[] argv){
		// BplusTree  bpTree = new BplusTree("TestTree", Type.INTEGER, 2, 100);

		// KeyType key1 = new KeyType(Type.INTEGER, 1);
		// KeyType key2 = new KeyType(Type.INTEGER, 2);
		// KeyType key5 = new KeyType(Type.INTEGER, 5);
		// KeyType key6 = new KeyType(Type.INTEGER, 6);
		// KeyType key8 = new KeyType(Type.INTEGER, 8);
		// KeyType key10 = new KeyType(Type.INTEGER, 10);
		// KeyType key18 = new KeyType(Type.INTEGER, 18);
		// KeyType key27 = new KeyType(Type.INTEGER, 27);
		// KeyType key32 = new KeyType(Type.INTEGER, 32);
		// KeyType key39 = new KeyType(Type.INTEGER, 39);
		// KeyType key41 = new KeyType(Type.INTEGER, 41);
		// KeyType key45 = new KeyType(Type.INTEGER, 45);
		// KeyType key52 = new KeyType(Type.INTEGER, 52);
		// KeyType key58 = new KeyType(Type.INTEGER, 58);
		// KeyType key73 = new KeyType(Type.INTEGER, 73);
		// KeyType key80 = new KeyType(Type.INTEGER, 80);
		// KeyType key91 = new KeyType(Type.INTEGER, 91);
		// KeyType key99 = new KeyType(Type.INTEGER, 99);

		// Record record1 = new Record(key1, Integer.parseUnsignedInt("1"), "this is record1", 100);
		// Record record2 = new Record(key2, Integer.parseUnsignedInt("2"), "this is record2", 100);
		// Record record3 = new Record(key3, Integer.parseUnsignedInt("5"), "this is record3", 100);
		// Record record4 = new Record();
	}
}