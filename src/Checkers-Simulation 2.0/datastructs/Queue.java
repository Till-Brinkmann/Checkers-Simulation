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
	/**
	 * The constructor initializes the length of the Queue with 0.
	 */
	public Queue() {
		length = 0;
	}
	/**
	 * The new content is added at the end of the queue. 
	 * <p>
	 * @param content The content which will be added.
	 */
	public void enqueue(ContentType content) {
		length++;
		if(isEmpty()) {
			head = tail = new Node(content, null);
			return;
		}
		tail.next = new Node(content, null);
		tail = tail.next;
	}
	/**
	 * Returns the first object in the Queue and removes it from this spot.
	 * <p>
	 * @return The first object in the Queue.
	 */
	public ContentType dequeue() {
		if(isEmpty()) return null;
		ContentType dequeued = head.content;
		head = head.next;
		if(isEmpty()) tail = null;
		length--;
		return dequeued;
	}
	/**
	 * Returns only the first object form the Queue without removing it.
	 * <p>
	 * @return The first object in the Queue
	 */
	public ContentType front() {
		if(isEmpty()) return null;
		return head.content;
	}
	/**
	 * Tests if the Queue is empty
	 * <p>
	 * @return True, if the list is empty. False, if there is at least one object in the queue. 
	 */
	public boolean isEmpty() {
		return head == null;
	}
}