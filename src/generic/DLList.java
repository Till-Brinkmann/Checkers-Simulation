package generic;

public class DLList<ContentType> {
	
	private class DLListNode {
		ContentType content;
		
		DLListNode prev;
		DLListNode next;
		
		public DLListNode(ContentType content, DLListNode prev, DLListNode next){
			this.content = content;
			this.next = next;
			this.prev = prev;
		}
	}
	
	private DLListNode current;
	
	public DLList() {
		
	}
	
	public ContentType get(){
		return current.content;
	}
	
	/**
	 * adds the new entry after current
	 * @param newContent
	 */
	public void add(ContentType newContent){
		current.next = new DLListNode(newContent, current, current.next);
	}
	
	/**
	 * deletes current
	 */
	public void delete(){
		if(current.prev == null){
			current = current.next;
			current.prev = null;
		}
		else {
			DLListNode tmp = current;
			current = current.prev;
			current.next = tmp.next;
			current.next.prev = current;
		}
	}
	
	public boolean hasNext(){
		return current.next != null;
	}
	public ContentType next(){
		if(current.next == null){
			return null;
		}
		current = current.next;
		return current.content;
	}
	
	public boolean hasPrevious(){
		return current.prev != null;
	}
	public ContentType previous(){
		if(current.prev == null){
			return null;
		}
		current = current.prev;
		return current.content;
	}
	
	public boolean isEmpty(){
		return current == null;
	}

}
