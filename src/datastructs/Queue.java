package datastructs;
/**
 * FIFO Queue with length parameter.
 * <p>
 * @param <ContentType> The type of the object ot hold.
 */
public class Queue<ContentType> {
	protected class Node {
		private ContentType content;
		private Node next;
		
		public Node(ContentType content, Node next) {
			this.content = content;
			this.next = next;
		}
	}
	
	private Node head;
	private Node tail;
	
	public int length;
	
	public Queue() {
		length = 0;
	}
	
	public void enqueue(ContentType content) {
		length++;
		if(isEmpty()) {
			head = tail = new Node(content, null);
			return;
		}
		tail.next = new Node(content, null);
		tail = tail.next;
	}
	
	public ContentType dequeue() {
		if(isEmpty()) return null;
		ContentType dequeued = head.content;
		head = head.next;
		if(isEmpty()) tail = null;
		length--;
		return dequeued;
	}
	
	public ContentType front() {
		if(isEmpty()) return null;
		return head.content;
	}
	
	public boolean isEmpty() {
		return head == null;
	}
}