package datastructs;
/**
 * LIFO Stack with a length parameter.
 * <p>
 * @param <ContentType> The type of object to hold.
 */
public class Stack<ContentType>{
	private class Item {
		private ContentType content;
		private Item next;
		private Item(ContentType content, Item next){
			this.content = content;
			this.next = next;
		}
	}
	private Item top;
	private int length;
	public Stack(){
		length = 0;
	}
	public void push(ContentType content){
		top = new Item(content, top);
		length++;
	}
	public ContentType pop(){
		if(!isEmpty()){
			ContentType content = top.content;
			top = top.next;
			length--;
			return content;
		}
		return null;
	}
	public ContentType peek(){
		return isEmpty() ? null : top.content;
	}
	public boolean isEmpty(){
		return top == null;
	}
}