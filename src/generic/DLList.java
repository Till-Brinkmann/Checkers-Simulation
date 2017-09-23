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
		if(isEmpty()){
			current = new DLListNode(newContent, null, null);
			return;
		} 
		current.next = new DLListNode(newContent, current, current.next);
		if(current.next.next != null){
			current.next.next.prev = current.next;
		}
	}
	/**
	 * adds the new entry before current
	 * @param newContent
	 */
	public void addBefore(ContentType newContent){
		if(isEmpty()){
			current = new DLListNode(newContent, null, null);
			return;
		}
		current.prev = new DLListNode(newContent, current.prev, current);
		if(current.prev.prev != null){
			current.prev.prev.next = current.prev;
		}
	}
	
	/**
	 * deletes current and makes previous the current object.
	 * if current has no previous, next is chosen.
	 */
	public void delete(){
		if(current.prev == null){
			if(current.next == null){
				current = null;
			}
			else {
				current = current.next;
				current.prev = null;
			}
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
