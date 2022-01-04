package kiasan.common;

public class ListNode<E> {
  // Friendly data; accessible by other package routines
  public E element;

  ListNode<E> next;

  public ListNode() {
  }

  // Constructors
  ListNode(final E theElement) {
    this(theElement, null);
  }

  ListNode(final E theElement, final ListNode<E> n) {
    this.element = theElement;
    this.next = n;
  }
}
