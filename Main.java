/**
* @author R04921094 Ye, Meng-Yuan
**/

import java.util.*;
import java.io.*;

public class Main{
    private static final String TAG = "Main: ";
    private static final String INT = "integer,";
    private static final String STR = "String,";

    /*
    D order is based on the leaf page, integer 2d = (512-8)/8 = 63, d = 31
    string 2d = (512-8)/14 = 36, d = 18. 8 is the neighbor page pointer size
     */
    // private static final int INT_D = 63;
    // // private static final int INT_D = 2;
    // private static final int STR_D = 36;

    private int INT_D;
    private int STR_D;

    private HashMap<String, BplusTree> treeMap;

    public Main(int int_d, int str_d){
    	treeMap = new HashMap<> ();
    	INT_D = int_d;
    	STR_D = str_d;
    }


	public String input(String command){
		if(command.charAt(0) == 'R'){
			String[] commands = command.split(" ");
			if(commands.length != 4){
				return "Invalid input. Create B+ tree must be" + 
					"R, Relation-name, key-type, record-length" + "\n";
			}
			String relName = commands[1].substring(0, commands[1].length()-1);
            int recordLen = Integer.parseInt(
            	commands[3].substring(0, commands[3].length()-1));
            Type type;
            int d;
            if(commands[2].equals(INT)){
                type = Type.INTEGER;
                d = INT_D;
            } else{
                type = Type.STRING;
                d = STR_D;
            }
             
            BplusTree tree = new BplusTree(relName, type, d, recordLen);
            treeMap.put(relName, tree);
            return null;
        }

        else if(command.charAt(0) == 'I'){
			String[] commands = command.split(";");
			String[] rootCommands = commands[0].split(",");
			if(rootCommands.length != 4){
				return "Invalid input. Insert should be:"
					+ "I, Relation-name, key-value, \"the rest of the record\"" + "\n";
			}
			String relName = rootCommands[1].substring(1);
			String strKey;
			BplusTree tree = treeMap.get(relName);
			Type type = tree.type;
			int len = tree.recordLength;
			int recNum = commands.length;
			String ans = new String();
			strKey = rootCommands[2].substring(1);
			if(type == Type.STRING){
				strKey = strKey.substring(1,strKey.length()-1);
			}
			KeyType key = new KeyType(type, strKey);
			// System.out.println(rootCommands[2].substring(1) + "");
			String content = rootCommands[3].substring(2, 
				rootCommands[3].length()-1);
			int[] rid = tree.insert(new Record(key, content));
			// System.out.println("Insert: " + key);
			// System.out.println(tree);
			// System.out.println(rids[0] + "," + rid[0]);
			ans += rid[0] + "," + rid[1] + "\n";

			for(int i = 1; i<recNum; i++){
				String[] sub = commands[i].split(",");
				strKey = sub[0].substring(1);
				if(type == Type.STRING){
					strKey = strKey.substring(1,strKey.length()-1);
				}
				KeyType newKey = new KeyType(type, strKey);
				String c = sub[1].substring(2,sub[1].length()-1);
				rid = tree.insert(new Record(newKey, c));
				// System.out.println("Insert: " + newKey);
				// System.out.println(tree);
				// System.out.println(rid[0] + "," + rid[1]);
				ans += rid[0] + "," + rid[1] + "\n";
				// System.out.println(ans[i]);
			}

			return ans;
		}

		else if(command.charAt(0) == 'D'){
			String[] sub = command.split(",");
			if(sub.length != 3){
				System.out.println();
				return "Invalid input."
					+ "Delete should be: D, Relation-name, key-value" + "\n";
			}
			String relName = sub[1].substring(1);
			BplusTree tree = treeMap.get(relName);
			if(tree == null){
				return "Didn't create B+ tree " + relName + "\n";
			}
			Type type = tree.type;
			KeyType key;
			try{
				// error input
				key = new KeyType(type, sub[2].substring(1));
			} catch(NumberFormatException e){
				return "Key is not a integer!" + "\n";
			}
			// System.out.println("Delete:" + key);
			tree.delete(key);
			// System.out.println(tree);
			return null;
		}
		
		else if(command.charAt(0) == 'S'){
			String[] commands = command.split(" ");
			if(commands.length != 2){
				return "Invalid input."
					+ "Scan should be: Scan Relation-name " + "\n";
			}
			String ans = new String();
			String relName = commands[1];
			// System.out.println("RelName: " + relName);
			BplusTree tree = treeMap.get(relName);
			int[] out = tree.scan();
			// System.out.println(out[0] + ", " + out[1]);
			ans = out[0] + "," + out[1] + "\n";
			return ans;
		}

		else if(command.charAt(0) == 'q'){
			String ans = "";
			String strKeys = "";
			String[] commands = command.split(" ");
			if(commands.length != 3 && commands.length != 4){
				return "Invalid input."
					+ " Quert should be: q Relation-name key-value"
					+ ", or q Relation-name key-value1 key-value2 " + "\n";
			}
			String relName = commands[1];
			BplusTree tree = treeMap.get(relName);
			Type type = tree.type;

			if(commands.length == 3){
				Record record;
				if(type == Type.INTEGER){
					try{
						record = tree.query(commands[2]);
					} catch(NumberFormatException e){
						return "Key is not a integer!" + "\n";
					}
				} else{
					record = tree.query(commands[2].substring(1, commands[2].length() -1));
				}
				if(record == null){
					return ans += "No such data: key = " + commands[2] + "\n";
				}
				ans += record.key + ", " +
					 record.content.length() + 
					 ", Rid:" + record.rid[0] + 
					 "," + record.rid[1] + "\n";
			} else if(commands.length == 4){
				Record[] records;
				String strKey1 = "", strKey2 = "";
				if(type == Type.INTEGER){
					strKey1 = commands[2];
					strKey2 = commands[3];
					try{
						records = tree.query(strKey1, strKey2);
					} catch(NumberFormatException e){
						return "Key is not a integer!" + "\n";
					}
				} else{
					strKey1 = commands[2].substring(1, commands[2].length() -1);
					strKey2 = commands[3].substring(1, commands[3].length() -1);
					records = tree.query(commands[2], commands[3]);
				}
				if(records == null){
					return "No such element between " + 
						strKey1 + " and " + strKey2;
				}

				for(Record r: records){
					ans += r.rid[0] + "," + r.rid[1] + "\n";
					strKeys += r.key + ",";
				}
				// for range query print keys correctness
				// System.out.println(strKeys);
			}
			return ans;
		}

		else if(command.charAt(0) == 'p'){
			String[] commands = command.split(" ");
			if(commands.length != 3){
				return "Invalid input." 
					+ "page query should be: " 
					+ "p relation-name page-id" + "\n";
			}
			int pageId = Integer.parseInt(commands[2]);
			// System.out.println("pageid: " + pageId);
			String relName = commands[1];
			BplusTree tree = treeMap.get(relName);
			return tree.getPageString(pageId) + "\n";
		}
		else if(command.charAt(0) == 'c'){
			String[] commands = command.split(" ");
			if(commands.length != 2){
				return "Invalid input." 
					+ "c should be: "
					+ "c relation-name " + "\n";
			}
			String relName = commands[1];
			BplusTree tree = treeMap.get(relName);
			int indexNum = tree.scanIndexPage();
			int pageNum = tree.getPageNum();
			return  indexNum + "," + pageNum + "\n";
		} else{
			return "Invalid command.\n" ;
		}
	}

	public static void main(String[] argv){
		BufferedReader br;
		BufferedWriter bw;
		String buffer;
		String OUT_FILE;
		Main project;

		if(argv[0].equals("test_data.txt")){
			project = new Main(2,2);	// for TA test data
			OUT_FILE = "test_data_result.txt";
		} else if(argv[0].equals("ProjectB_data.txt")){
			project = new Main(31, 18);
			OUT_FILE = "ProjectB_data_result.txt";
		} else{
			project = new Main(31, 18);
			OUT_FILE = argv[0] + "_result.txt";
		}


		try{
			bw = new BufferedWriter(new FileWriter(OUT_FILE));
			br = new BufferedReader(new FileReader(argv[0]));
			while((buffer = br.readLine()) != null){
				if(buffer.length() == 0){
					bw.write("Empty line!!!" + "\n");
				} else{
					bw.write(buffer + "\n");
					// bw.newLine();
					String ans = project.input(buffer);
					if(ans != null){
						bw.write(ans + "\n");
					}
				}
			}
			bw.close();
		} catch (IOException e){
			System.out.println(e);
		}
	}
}