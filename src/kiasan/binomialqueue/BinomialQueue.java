package kiasan.binomialqueue;

import java.util.LinkedList;

import kiasan.common.ObjectArray;
import kiasan.common.Underflow;

/**
 * Implements a binomial queue. Note that all "matching" is based on the
 * compareTo method.
 * 
 * @author Mark Allen Weiss
 */
public class BinomialQueue {
  static class BinomialNode {
    int element; // The data in the node

    BinomialNode leftChild; // Left child

    BinomialNode nextSibling; // Right child

    // Constructors
    BinomialNode(final int theElement) {
      this(theElement, null, null);
    }

    BinomialNode(final int theElement, final BinomialNode lt,
        final BinomialNode nt) {
      this.element = theElement;
      this.leftChild = lt;
      this.nextSibling = nt;
    }
  }

  private static final int DEFAULT_TREES = 1;

  public static void main(final String[] args) {
    final int numItems = 10000;
    final BinomialQueue h = new BinomialQueue();
    final BinomialQueue h1 = new BinomialQueue();
    int i = 37;

    System.out.println("Starting check.");

    for (i = 37; i != 0; i = (i + 37) % numItems) {
      if (i % 2 == 0) {
        h1.insert(i);
      } else {
        h.insert(i);
      }
    }
    assert h.wellFormed();
    h.merge(h1);
    assert h.wellFormed();
    for (i = 1; i < numItems; i++) {
      // assert h.wellFormed();
      if (h.deleteMin() != i) {
        System.out.println("Oops! " + i);
      }
    }

    System.out.println("Check done.");
  }

  private int currentSize; // # items in priority queue
  private ObjectArray<BinomialNode> theTrees; // An array of tree roots

  /**
   * Construct the binomial queue.
   */
  private BinomialQueue() {
    this.theTrees = new ObjectArray<BinomialNode>(BinomialQueue.DEFAULT_TREES);
    makeEmpty();
  }
  
  public static BinomialQueue __new__() {
	  return new BinomialQueue();
  }

  /**
   * Construct with a single item.
   */
  private BinomialQueue(final int item) {
    this.currentSize = 1;
    this.theTrees = new ObjectArray<BinomialNode>(1);
    this.theTrees.set(0, new BinomialNode(item, null, null));
  }
  
  public static BinomialQueue __new__(final int item) {
	  return new BinomialQueue(item);
  }

  /**
   * Return the capacity.
   */
  private int capacity() {
    return (2 * this.theTrees.length) - 1;
  }

  /**
   * Return the result of merging equal-sized t1 and t2.
   */
  private BinomialNode combineTrees(final BinomialNode t1, final BinomialNode t2) {
    if (t1.element > t2.element) {
      return combineTrees(t2, t1);
    }
    t2.nextSibling = t1.leftChild;
    t1.leftChild = t2;
    return t1;
  }

  /**
   * Remove the smallest item from the priority queue.
   * 
   * @return the smallest item, or null, if empty.
   */
  public int deleteMin() {
    if (isEmpty()) {
      throw new Underflow();
    }

    final int minIndex = findMinIndex();
    final int minItem = this.theTrees.get(minIndex).element;

    BinomialNode deletedTree = this.theTrees.get(minIndex).leftChild;

    // Construct H''
    final BinomialQueue deletedQueue = new BinomialQueue();
    deletedQueue.expandTheTrees(minIndex + 1);

    deletedQueue.currentSize = (1 << minIndex) - 1;
    for (int j = minIndex - 1; j >= 0; j--) {
      deletedQueue.theTrees.set(j, deletedTree);
      deletedTree = deletedTree.nextSibling;
      deletedQueue.theTrees.get(j).nextSibling = null;
    }

    // Construct H'
    this.theTrees.set(minIndex, null);
    this.currentSize -= deletedQueue.currentSize + 1;

    merge(deletedQueue);

    return minItem;
  }

  private void expandTheTrees(final int newNumTrees) {
    final ObjectArray<BinomialNode> old = this.theTrees;
    final int oldNumTrees = this.theTrees.length;

    this.theTrees = new ObjectArray<BinomialNode>(newNumTrees);
    for (int i = 0; i < oldNumTrees; i++) {
      this.theTrees.set(i, old.get(i));
    }
    for (int i = oldNumTrees; i < newNumTrees; i++) {
      this.theTrees.set(i, null);
    }
  }

  /**
   * Find the smallest item in the priority queue.
   * 
   * @return the smallest item, or null, if empty.
   */
  //@ requires this.wellFormed();
  //@ ensures this.wellFormed();
  public int findMin() {
    if (isEmpty()) {
      throw new Underflow();
    }

    return this.theTrees.get(findMinIndex()).element;
  }

  /**
   * Find index of tree containing the smallest item in the priority queue. The
   * priority queue must not be empty.
   * 
   * @return the index of tree containing the smallest item.
   */
  private int findMinIndex() {
    int i;
    int minIndex;

    for (i = 0; this.theTrees.get(i) == null; i++) {
      ;
    }

    for (minIndex = i; i < this.theTrees.length; i++) {
      if ((this.theTrees.get(i) != null)
          && (this.theTrees.get(i).element < this.theTrees.get(minIndex).element)) {
        minIndex = i;
      }
    }

    return minIndex;
  }

  /**
   * Insert into the priority queue, maintaining heap order. This implementation
   * is not optimized for O(1) performance.
   * 
   * @param x
   *          the item to insert.
   */
  //@ requires this.wellFormed();
  //@ ensures this.wellFormed();
  public void insert(final int x) {
    merge(new BinomialQueue(x));
  }

  private boolean isAcyclic(final BinomialNode start,
      final LinkedList<BinomialNode> seen) {
    if (start.leftChild != null) {
      if (seen.contains(start.leftChild)) {
        return false;
      }
      seen.addLast(start.leftChild);
      if (!isAcyclic(start.leftChild, seen)) {
        return false;
      }
    }
    final BinomialNode ns = start.nextSibling;
    if (ns != null) {
      if (seen.contains(ns)) {
        return false;
      }
      seen.addLast(ns);
      if (!isAcyclic(ns, seen)) {
        return false;
        //ns = ns.nextSibling;
      }
    }
    return true;
  }

  /**
   * Test if the priority queue is logically empty.
   * 
   * @return true if empty, false otherwise.
   */
  public boolean isEmpty() {
    return this.currentSize == 0;
  }

  /**
   * Make the priority queue logically empty.
   */
  public void makeEmpty() {
    this.currentSize = 0;
    for (int i = 0; i < this.theTrees.length; i++) {
      this.theTrees.set(i, null);
    }
  }

  /**
   * Merge rhs into the priority queue. rhs becomes empty. rhs must be different
   * from this.
   * 
   * @param rhs
   *          the other binomial queue.
   */
  public void merge(final BinomialQueue rhs) {
    if (this == rhs) {
      return;
    }

    this.currentSize += rhs.currentSize;

    if (this.currentSize > capacity()) {
      final int newNumTrees = Math.max(this.theTrees.length,
          rhs.theTrees.length) + 1;
      expandTheTrees(newNumTrees);
    }

    BinomialNode carry = null;
    for (int i = 0, j = 1; j <= this.currentSize; i++, j *= 2) {
      final BinomialNode t1 = this.theTrees.get(i);
      final BinomialNode t2 = i < rhs.theTrees.length ? rhs.theTrees.get(i) : null;

      int whichCase = t1 == null ? 0 : 1;
      whichCase += t2 == null ? 0 : 2;
      whichCase += carry == null ? 0 : 4;

      switch (whichCase) {
        case 0: /* No trees */
        case 1: /* Only this */
          break;
        case 2: /* Only rhs */
          this.theTrees.set(i, t2);
          rhs.theTrees.set(i, null);
          break;
        case 4: /* Only carry */
          this.theTrees.set(i, carry);
          carry = null;
          break;
        case 3: /* this and rhs */
          carry = combineTrees(t1, t2);
          this.theTrees.set(i, null);
          rhs.theTrees.set(i, null);
          break;
        case 5: /* this and carry */
          carry = combineTrees(t1, carry);
          this.theTrees.set(i, null);
          break;
        case 6: /* rhs and carry */
          carry = combineTrees(t2, carry);
          rhs.theTrees.set(i, null);
          break;
        case 7: /* All three */
          this.theTrees.set(i, carry);
          carry = combineTrees(t1, t2);
          rhs.theTrees.set(i, null);
          break;
      }
    }

    for (int k = 0; k < rhs.theTrees.length; k++) {
      rhs.theTrees.set(k, null);
    }
    rhs.currentSize = 0;
  }

  private boolean ordered(final BinomialNode node) {
    if (node.leftChild != null) {
      if (node.leftChild.element < node.element) {
        return false;
      }
      if (!ordered(node.leftChild)) {
        return false;
      }
      for (BinomialNode ns = node.leftChild.nextSibling; ns != null; ns = ns.nextSibling) {
        if (ns.element < node.element) {
          return false;
        }
        if (!ordered(ns)) {
          return false;
        }
      }
      return true;
    } else {
      return node.nextSibling == null;
    }
  }

  private boolean wellFormed() {
    if (this.theTrees == null) {
      return false;
    }
    final LinkedList<BinomialNode> seen = new LinkedList<BinomialNode>();
    for (int i = 0; i < this.theTrees.length; i++) {
      if (this.theTrees.get(i) != null) {
        if (seen.contains(this.theTrees.get(i))) {
          return false;
        }
        seen.addLast(this.theTrees.get(i));
        if (!isAcyclic(this.theTrees.get(i), seen)) {
          return false;
        }
      }
    }
    return wellStructured();
  }

  private boolean wellStructured() {
    if (this.currentSize < 0) {
      return false;
    }
    if (this.theTrees == null) {
      return false;
    }
    int size = this.currentSize;
    for (int i = 0, j = 1; i < this.theTrees.length; i++, j *= 2) {
      if (size < 0) {
        return false;
      }
      if ((size == 0) && (this.theTrees.get(i) != null)) {
        return false;
      }
      if (this.theTrees.get(i) != null) {
        if (this.theTrees.get(i).nextSibling != null) {
          return false;
        }
        if (!wellStructuredBK(this.theTrees.get(i), i)) {
          return false;
        }
        if (!ordered(this.theTrees.get(i))) {
          return false;
        }
        size -= j;
      }
    }
    if (size != 0) {
      return false;
    }
    return true;
  }

  //specification method
  //B_k k has to be a power of 2
  private boolean wellStructuredBK(final BinomialNode node, int k) {
    assert k >= 0;
    if (node == null) {
      return false;
    }
    if (k == 0) {
      return (node.leftChild == null) && (node.nextSibling == null);
    }
    if (!wellStructuredBK(node.leftChild, --k)) {
      return false;
    }
    --k;
    for (BinomialNode nc = node.leftChild.nextSibling; k >= 0; nc = nc.nextSibling, --k) {
      if (!wellStructuredBK(nc, k)) {
        return false;
      }
    }
    return true;
  }
}
