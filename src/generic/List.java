package generic;
/*
 * just a normal list structure (with a length parameter)
 */
public class List<ContentType>{
	private class Entry{
		protected ContentType content;
		public Entry next;
		
		private Entry(ContentType content){
			this.content = content;
		}
		private Entry(ContentType content, Entry next){
			this.content = content;
			this.next = next;
		}
	}
	
	Entry first,last,current;
	public int length;
	
	public List(){
		length = 0;
	}
	
	public void insert(ContentType content){
		if(isEmpty()){
			first = new Entry(content);
			last = first;
			length++;
			return;
		}
		if(hasAccess()){
			Entry previous = getPrevious(current);
			previous.next = new Entry(content, current);
			length++;
		}
	}
	public void append(ContentType content){
		if(isEmpty()){
			first = new Entry(content);
			last = first;
			length++;
			return;
		}
		last.next = new Entry(content);
		last = last.next;
		length++;
	}
	public List<ContentType> concat(List<ContentType> list){
		if(list == null || list == this || list.isEmpty()) return this;
		if(isEmpty()){
			first = list.first;
			last = list.last;
			length = list.length;
			return this;
		}
		last.next = list.first;
		last = list.last;
		length += list.length;
		return this;
	}
	public void remove(){
		if(!hasAccess()) return;
		if(current == first){
			current = first = first.next;
		}
		else{
			Entry previous = getPrevious(current);
			if (current == last) last = previous;
			current = previous.next = current.next;
		}
		if(isEmpty()) last = null;
		length--;
	}
	
	public void set(ContentType content){
		if(hasAccess()) current.content = content;
	}
	public ContentType get(){
		return current == null ? null : current.content;
	}
	
	public void next() {
		if (hasAccess()) current = current.next;
	}
	public void toFirst() {
		if (!isEmpty())	current = first;
	}
	public void toLast() {
		if (!isEmpty())	current = last;
	}
	
	private Entry getPrevious(Entry entry) {
		if(entry == null || entry == first || isEmpty()) return null;
		Entry tmp;
		for(tmp = first; (tmp.next != entry && tmp != null); tmp = tmp.next){}
		return tmp;
	}
	
	public boolean isEmpty() {
		return first == null;
	}
	public boolean hasAccess() {
		return current != null; 
	}
}