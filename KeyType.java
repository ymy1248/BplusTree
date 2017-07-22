/**
* @author R04921094 Ye, Meng-Yuan
**/

public class KeyType implements Comparable<KeyType>{
	private static final String TAG = "KeyType: ";
	public Type type;
	public Object value;

	public KeyType (Type type, Object value){
		this.type = type;

		switch(type){
			case INTEGER:
				if(value instanceof Integer){
					this.value = value;
				} else {
					// TODO different instance exception
					System.out.println(TAG + "type Integer, value is not Integer.");
				}
				break;
			case STRING:
				if(value instanceof String){
					this.value = value;
				} else {
					// TODO same as last todo
					System.out.println(TAG + "type String, value is not String.");
				}
		}
		this.value = value;
	}

	public KeyType (Type type, String value) throws NumberFormatException{
		this.type = type;

		switch(type){
			case INTEGER:
				try{
					this.value = (Integer)Integer.parseInt(value);
				} catch(NumberFormatException e){
					throw e;
				}
					break;
			case STRING:
				this.value = value;
				break;
		}
	}

	public int compareTo(KeyType that){
		if(this.type == that.type){
			switch(type){
				case INTEGER:
					Integer i = (Integer)this.value;
					return i.compareTo((Integer)that.value);

				case STRING:
					String s = (String)this.value;
					return s.compareTo((String)that.value);
			}
		} else {
			System.out.println(TAG + "compareTo() different type");
			return 0;
		}
		return 0;
	}

	public String toString(){
		switch(type){
			case INTEGER:
				Integer i = (Integer)value;
				return i.toString();
			case STRING:
				String s = (String)value;
				return s.toString();
		}
		return null;
	}

	public static void main(String[] argv){
		KeyType intKey1 = new KeyType(Type.INTEGER, "4");
		KeyType intKey2 = new KeyType(Type.INTEGER, 5);

		System.out.println(intKey1);

		KeyType strKey1 = new KeyType(Type.STRING, "aba");
		KeyType strKey2 = new KeyType(Type.STRING, "abb");

		System.out.println(strKey1.compareTo(strKey2));
		System.out.println(strKey1.toString());
	}
}