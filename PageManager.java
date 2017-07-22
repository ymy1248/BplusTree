/**
* @author R04921094 Ye, Meng-Yuan
**/
import java.util.LinkedList;

public class PageManager{
	private static final String TAG = "PageManager";
	private static final int MAX_PAGE = 65536;

	private Page page;
	private int pageIndex=0;
	private LinkedList<Page> pageList;

	public PageManager(){
		this.pageIndex = 0;
		this.pageList = new LinkedList<>();
		this.pageList.add(new Page(0));
	}

	public int[] addRecord(Record record){
		if(pageIndex < 65535){						// can have one more page
			page = pageList.get(pageIndex);

			if(page.addable(record)){
				return page.addRecord(record);
			} else{
				page = new Page(++pageIndex);
				pageList.add(page);
				return page.addRecord(record);
			}
		} else{										// used out all the page
			garbageColl();
			page = pageList.get(pageIndex);
			while(!page.addable(record)){
				page = pageList.get(++pageIndex);
				return page.addRecord(record);
			}
			for(pageIndex = 0; pageIndex < pageList.size(); pageIndex++){
				if(page.addable(record)){
					return page.addRecord(record);
				}
			}
			System.out.println(TAG + "ERROR: No more memory!");
			return null;
		}
	}

	public void deleteRecord(Record record){
		int[] rid = record.rid;
		pageList.get(rid[0]).deleteRecord(record);
	}

	public String getPageString(int i){
		if(0>i || i >= pageList.size()){
			return "No such page: pageId = " + i;
		}
		return pageList.get(i).toString();
	}

	public int getPageListSize(){
		return pageList.size();
	}

	private void garbageColl(){
		// round robin garbage collection: used out all the page, we run garbage
		// collection, point index to 0
		for(Page p: pageList){
			p.garbageColl();
		}
		pageIndex = 0;
	}

	public static void main(String[] argv){		
		PageManager pm = new PageManager();
	}
}