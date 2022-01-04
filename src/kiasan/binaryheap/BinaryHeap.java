package kiasan.binaryheap;

import kiasan.common.Overflow;

// BinaryHeap class
//
// CONSTRUCTION: with optional capacity (that defaults to 100)
//
// ******************PUBLIC OPERATIONS*********************
// void insert( x )       --> Insert x
// int deleteMin( )--> Return and remove smallest item
// int findMin( )  --> Return smallest item
// boolean isEmpty( )     --> Return true if empty; else false
// boolean isFull( )      --> Return true if full; else false
// void makeEmpty( )      --> Remove all items
// ******************ERRORS********************************
// Throws Overflow if capacity exceeded

/**
 * Implements a binary heap. Note that all "matching" is based on the compareTo
 * method.
 * 
 * @author Mark Allen Weiss
 */
public class BinaryHeap {
  private static final int DEFAULT_CAPACITY = 100;

  // Test program
  public static void main(final String[] args) {
    final int numItems = 10000;
    final BinaryHeap h = new BinaryHeap(numItems);
    int i = 37;

    try {
      for (i = 37; i != 0; i = (i + 37) % numItems) {
        h.insert(i);
      }
      for (i = 1; i < numItems; i++) {
        if ((h.deleteMin()) != i) {
          System.out.println("Oops! " + i);
        }
      }

      for (i = 37; i != 0; i = (i + 37) % numItems) {
        h.insert(i);
      }
      h.insert(0);
      i = 9999999;
      h.insert(i);
      for (i = 1; i <= numItems; i++) {
        if (h.deleteMin() != i) {
          System.out.println("Oops! " + i + " ");
        }
      }
    } catch (final Overflow e) {
      System.out.println("Overflow (expected)! " + i);
    }
  }

  private int currentSize; // Number of elements in heap

  private final int[] array; // The heap array

  /**
   * Construct the binary heap.
   */
  public BinaryHeap() {
    this(BinaryHeap.DEFAULT_CAPACITY);
  }

  /**
   * Construct the binary heap.
   * 
   * @param capacity
   *          the capacity of the binary heap.
   */
  public BinaryHeap(final int capacity) {
    this.currentSize = 0;
    this.array = new int[capacity + 1];
  }

  /**
   * Establish heap order property from an arbitrary arrangement of items. Runs
   * in linear time.
   */
  void buildHeap() {
    for (int i = this.currentSize / 2; i > 0; i--) {
      percolateDown(i);
    }
  }

  /**
   * Remove the smallest item from the priority queue.
   * 
   * @return the smallest item, or null, if empty.
   */
  //@ requires wellFormed();
  //@ ensures wellFormed();
  public int deleteMin() {
    if (isEmpty()) {
      return -1;
    }

    final int minItem = findMin();
    this.array[1] = this.array[this.currentSize--];
    percolateDown(1);

    return minItem;
  }

  /**
   * Find the smallest item in the priority queue.
   * 
   * @return the smallest item, or null, if empty.
   */
  //@ requires wellFormed();
  //@ ensures wellFormed();
  public int findMin() {
    if (isEmpty()) {
      return -1;
    }
    return this.array[1];
  }

  /**
   * Insert into the priority queue, maintaining heap order. Duplicates are
   * allowed.
   * 
   * @param x
   *          the item to insert.
   * @exception Overflow
   *              if container is full.
   */
  //@ requires wellFormed();
  //@ ensures wellFormed();
  public void insert(final int x) throws Overflow {
    if (isFull()) {
      throw new Overflow();
    }

    // Percolate up
    int hole = ++this.currentSize;
    for (; (hole > 1) && (x < this.array[hole / 2]); hole /= 2) {
      this.array[hole] = this.array[hole / 2];
    }
    this.array[hole] = x;
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
   * Test if the priority queue is logically full.
   * 
   * @return true if full, false otherwise.
   */
  public boolean isFull() {
    return this.currentSize == this.array.length - 1;
  }

  /**
   * Make the priority queue logically empty.
   */
  public void makeEmpty() {
    this.currentSize = 0;
  }

  /**
   * Internal method to percolate down in the heap.
   * 
   * @param hole
   *          the index at which the percolate begins.
   */
  private void percolateDown(int hole) {
    /* 1 */int child;
    /* 2 */final int tmp = this.array[hole];

    /* 3 */for (; hole * 2 <= this.currentSize; hole = child) {
      /* 4 */child = hole * 2;
      /* 5 */if ((child != this.currentSize)
          && (/* 6 */this.array[child + 1] < this.array[child])) {
        /* 7 */child++;
      }
      /* 8 */if (this.array[child] < tmp) {
        /* 9 */this.array[hole] = this.array[child];
      } else {
        /* 10 */break;
      }
    }
    /* 11 */this.array[hole] = tmp;
  }

  boolean wellFormed() {
    if (this.array == null) {// array!=null
      return false;
    }
    if ((this.currentSize < 0) || (this.currentSize >= this.array.length)) {// currentSize
      // >=
      // 0
      // ;
      // currentSize<array.length;
      return false;
    }
    for (int i = 1; i < this.currentSize; i++) {
      if ((i * 2 <= this.currentSize) && (this.array[i] > this.array[2 * i])) {
        return false;
      }
      if ((i * 2 + 1 <= this.currentSize)
          && (this.array[i] > this.array[2 * i + 1])) {
        return false;
      }
    }
    return true;
  }
}
