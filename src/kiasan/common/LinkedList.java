package kiasan.common;

import java.util.Comparator;
import java.util.Iterator;

public class LinkedList<E> {

  public class LinkedListIterator
      implements Iterator<E> {
    private ListNode<E> current;

    public LinkedListIterator() {
      this.current = LinkedList.this.head.next;
      //return this;
    }

    public boolean hasNext() {
      return this.current != null;
    }

    public E next() {
      //assert current!=null;
      final ListNode<E> last = this.current;
      this.current = this.current.next;
      return last.element;
    }

    public void remove() {
      assert false;
    }
  }

  public static void main(final String[] args) {
    final LinkedList<Object> l = new LinkedList<Object>();
    //l.head = new ListNode();
    //l.head.next = l.head;
    assert l.isAcyclic();
    System.out.println(l.isAcyclic());
  }

  public/*@NonNull*/ListNode<E> head;

  public LinkedList() {
    this.head = new ListNode<E>();
  }

  public void add(final E e) {
    final ListNode<E> tmp = new ListNode<E>();
    tmp.element = e;
    tmp.next = this.head.next;
    this.head.next = tmp;
  }

  public void addToEnd(final E e) {
    ListNode<E> tmp = this.head;
    while (tmp.next != null) {
      tmp = tmp.next;
    }
    final ListNode<E> nn = new ListNode<E>(e, null);
    tmp.next = nn;
  }

  public boolean consistent() {
    if (this.head == null) {
      return false;
    }
    return isAcyclic();
  }

  public boolean contains(final E e) {
    ListNode<E> temp = this.head.next;
    while (temp != null) {
      if (e == temp.element)
      //if (e.equals(temp.element))
      {
        return true;
      }
      temp = temp.next;
    }
    return false;
  }

  public boolean containsAll(final LinkedList<E> list) {
    ListNode<E> temp = list.head.next;
    while (temp != null) {
      if (!contains(temp.element)) {
        return false;
      }
      temp = temp.next;
    }
    return true;
  }

  public boolean isAcyclic() {
    final LinkedList<ListNode<E>> ll = new LinkedList<ListNode<E>>();
    ListNode<E> temp = this.head;
    while (temp != null) {
      if (ll.contains(temp)) {
        return false;
      }
      ll.add(temp);
      temp = temp.next;
    }
    //        for(temp = head;temp!=null;temp=temp.next) {
    //        	System.out.print("-> ");
    //        }
    //        System.out.print("\n");

    return true;
  }

  public boolean isEmpty() {
    return this.head.next == null;
  }

  public boolean isSorted(final Comparator<E> c) {
    ListNode<E> temp = this.head.next;
    while (temp != null) {
      if (temp.next != null) {
        if (c.compare(temp.element, temp.next.element) > 0) {
          return false;
        }
      }
      temp = temp.next;
    }
    return true;
  }

  public LinkedListIterator iterator() {
    return new LinkedListIterator();
  }

  public boolean nonNullData() {
    assert this.head != null;
    ListNode<E> tmp = this.head.next;
    while (tmp != null) {
      if (tmp.element == null) {
        return false;
      }
      tmp = tmp.next;
    }
    return true;
  }

  public boolean nonNullElements() {
    ListNode<E> temp = this.head.next;
    while (temp != null) {
      if (temp.element == null) {
        return false;
      }
      temp = temp.next;
    }
    return true;

  }

  public E remove() {
    assert !isEmpty();
    final E ret = this.head.next.element;
    this.head = this.head.next;
    return ret;
  }

  //          public void partition(@NonNull E v,@NonNull Comparator<E> c){
  //              ListNode<E> l = head.next; 
  //              ListNode<E> curr,prev, newl, nextCurr;
  //            prev = newl = null; 
  //            curr = l;
  //            while(curr != null){
  //              nextCurr = curr.next;
  //              if( c.compare(curr.element,v)>0 ){
  //               //  if(prev != null &&
  //                 // nextCurr != null) //bug can be found by k=5
  //                 if(prev != null)      //fix
  //                   prev.next = nextCurr;
  //                 if(curr == l) l = nextCurr;
  //                 curr.next = newl;
  //                 newl = curr;
  //              }
  //              else prev = curr;
  //              curr = nextCurr;
  //            } 
  //            head.next=l;
  //            //check();
  //            }

  boolean smallerEq(final E v, final Comparator<E> c) {
    ListNode<E> tmp = this.head.next;
    while (tmp != null) {
      if (c.compare(tmp.element, v) > 0) {
        return false;
      }
      tmp = tmp.next;
    }
    return true;
  }

}
