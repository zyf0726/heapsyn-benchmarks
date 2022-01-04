package kiasan.common;

public class ObjectArray<E> {
	
	private class ObjectNode {
		private ObjectNode next = null;
		private E value = null;
	}
	
	private final ObjectNode header;
	public final int length;

	public ObjectArray(int length) {
		assert length > 0;
		ObjectNode curr = new ObjectNode();
		this.header = curr;
		this.length = length;
		for (int i = 1; i < length; ++i) {
			curr.next = new ObjectNode();
			curr = curr.next;
		}
	}
	
	private ObjectNode entry(int index) {
		assert index >= 0;
		ObjectNode curr = this.header;
		for (int i = 0; i < index; ++i) {
			curr = curr.next;
		}	
		return curr;
	}
	
	public E get(int index) {
		return entry(index).value;
	}
	
	public void set(int index, E value) {
		entry(index).value = value;
	}
	
	public static void main(String[] args) {
		ObjectArray<Integer> arr = new ObjectArray<>(10);
		assert arr.get(0) == null;
		assert arr.length == 10;
		arr.set(1, 3);
		arr.set(1, 2);
		assert arr.get(1) == 2;
		arr.set(3, 5);
		arr.set(4, 6);
		arr.set(9, 7);
		assert arr.get(9) == 7;
		assert arr.get(3) == 5;
		assert arr.get(1) == 2;
		assert arr.length == 10;
	}
	
}
