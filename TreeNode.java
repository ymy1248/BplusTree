/**
* @author R04921094 Ye, Meng-Yuan
**/
import java.util.LinkedList;

public class TreeNode extends Node{
	private static final String TAG = "TreeNode: ";

	public LinkedList<Node> children;

	public TreeNode(int d){
		super(d);
		children = new LinkedList<> ();
	}

	public void setChild(int index, Node child){
		children.add(index, child);
		keys.add(index, child.keys.get(0));
	}

	public Node findChildNode(KeyType key){
		int i = super.findChild(key);
		return children.get(i);
	}

	public void createRoot(Node left, Node right){
		if(left instanceof LeafNode){
			keys.add(right.keys.get(0));
			children.add(left);
			children.add(right);
		} else{
			keys.add(right.keys.remove());
			children.add(left);
			children.add(right);
		}
		
	}

	public Node addChild(Node child){
		int insert = findChild(child.keys.get(0));

		if(child instanceof LeafNode){
			if(!addFull()){
				// insert + 1 is the right children
				children.add(insert + 1, child);
				// System.out.println("Insert index" + insert);
				keys.add(insert, child.keys.get(0));
				// System.out.println("No split");
				return null;
			} else{
				TreeNode newtn = new TreeNode(d);
				children.add(insert + 1, child);
				keys.add(insert, child.keys.get(0));
				while(children.size() > d+1){
					// children to new node, there is one more child remain
					// the original node
					newtn.children.push(children.removeLast());
				}
				while(keys.size() > d){
					newtn.keys.push(keys.removeLast());
				}
				return newtn;
			}
		} else if(child instanceof TreeNode){
			TreeNode tree = (TreeNode)child;
			if(!addFull()){
				children.add(insert + 1, child);
				keys.add(insert, child.keys.remove(0));
				tree.children.remove(0);
				return null;
			} else{
				TreeNode newTree = new TreeNode(d);
				children.add(insert, child);
				keys.add(insert, child.keys.get(0));
				while(children.size() > d+1){
					// children to new node, there is one more child remain
					// the original node
					newTree.children.
						add(children.remove(children.size()-1));
				}
				while(keys.size() > d){
					newTree.keys.add(keys.remove(keys.size()-1));
				}
				return newTree;
			}
		} else{
			return null;
		}
	}

	public String toString(){
		String s = "TreeNode: ";
		for(KeyType k: keys){
			s += k + ",";
		}
		return s;
	}

	public KeyType rebuild(TreeNode cousin, int position, KeyType parentKey){
		int thisSize = keys.size();
		int cousinSize = cousin.keys.size();
		// +1 come from text book fig. 10.19 -> 10.20
		int newSize = (thisSize + cousinSize)/2 + 1;
		if(position == BplusTree.LEFT){
			keys.push(parentKey);
			children.push(cousin.children.removeLast());
			while(keys.size() < newSize){
				keys.push(cousin.keys.removeLast());
				children.push(cousin.children.removeLast());
			}
			return cousin.keys.removeLast();
		} else{
			keys.add(parentKey);
			children.add(cousin.children.remove());
			while(keys.size() < newSize){
				keys.add(cousin.keys.remove());
				children.add(cousin.children.remove());
			}
			return cousin.keys.remove();
		}
	}
}