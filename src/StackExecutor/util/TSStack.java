package util;
public class TSStack<ContentType>{
	private class Item{
		private ContentType content;
		private Item next;
		private Item(ContentType content, Item next){
			this.content = content;
			this.next = next;
		}
	}
	private Item top;
	private int length;
	public TSStack(){
		length = 0;
	}
	public synchronized void push(ContentType content){
		top = new Item(content, top);
		length++;
	}
	public synchronized ContentType pop(){
		if(!isEmpty()){
			ContentType content = top.content;
			top = top.next;
			length--;
			return content;
		}
		return null;
	}
	public synchronized ContentType peek(){
		return isEmpty() ? null : top.content;
	}
	public synchronized boolean isEmpty(){
		return top == null;
	}
	public synchronized int length(){
		return length;
	}
}

