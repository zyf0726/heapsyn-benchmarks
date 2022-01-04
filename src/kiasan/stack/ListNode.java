package kiasan.stack;

public class ListNode {
  // Friendly data; accessible by other package routines
  Object element;

  ListNode next;

  public ListNode() {
  }

  // Constructors
  ListNode(final Object theElement) {
    this(theElement, null);
  }

  ListNode(final Object theElement, final ListNode n) {
    this.element = theElement;
    this.next = n;
  }
}
