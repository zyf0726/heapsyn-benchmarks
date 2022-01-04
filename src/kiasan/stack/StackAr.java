package kiasan.stack;

import kiasan.common.Overflow;
import kiasan.common.Underflow;

// StackAr class
//
// CONSTRUCTION: with or without a capacity; default is 10
//
// ******************PUBLIC OPERATIONS*********************
// void push( x )         --> Insert x
// void pop( )            --> Remove most recently inserted item
// Object top( )          --> Return most recently inserted item
// Object topAndPop( )    --> Return and remove most recently inserted item
// boolean isEmpty( )     --> Return true if empty; else false
// boolean isFull( )      --> Return true if full; else false
// void makeEmpty( )      --> Remove all items
// ******************ERRORS********************************
// Overflow and Underflow thrown as needed

/**
 * Array-based implementation of the stack.
 * 
 * @author Mark Allen Weiss
 */
public class StackAr {
  private final Object[] theArray;

  private int topOfStack;

  static final int DEFAULT_CAPACITY = 10;

  public static void main(final String[] args) {
    final StackAr s = new StackAr(12);

    try {
      for (int i = 0; i < 10; i++) {
        s.push(new Integer(i));
      }
    } catch (final Overflow e) {
      System.out.println("Unexpected overflow");
    }

    while (!s.isEmpty()) {
      System.out.println(s.topAndPop());
    }
  }

  /**
   * Construct the stack.
   */
  public StackAr() {
    this(StackAr.DEFAULT_CAPACITY);
  }

  /**
   * Construct the stack.
   * 
   * @param capacity
   *          the capacity.
   */
  public StackAr(final int capacity) {
    this.theArray = new Object[capacity];
    this.topOfStack = -1;
  }

  /**
   * Test if the stack is logically empty.
   * 
   * @return true if empty, false otherwise.
   */
  public boolean isEmpty() {
    return this.topOfStack == -1;
  }

  /**
   * Test if the stack is logically full.
   * 
   * @return true if full, false otherwise.
   */
  public boolean isFull() {
    return this.topOfStack == this.theArray.length - 1;
  }

  /**
   * Make the stack logically empty.
   */
  public void makeEmpty() {
    this.topOfStack = -1;
  }

  /**
   * Remove the most recently inserted item from the stack.
   * 
   * @exception Underflow
   *              if stack is already empty.
   */
  //@ requires (this.theArray != null) && (this.theArray.length >= 0) && (this.topOfStack >= -1) && (this.topOfStack < this.theArray.length);
  //@ ensures true;
  public void pop() throws Underflow {
    if (isEmpty()) {
      throw new Underflow();
    }
    this.theArray[this.topOfStack--] = null;
  }

  /**
   * Insert a new item into the stack, if not already full.
   * 
   * @param x
   *          the item to insert.
   * @exception Overflow
   *              if stack is already full.
   */
  //@ requires (this.theArray != null) && (this.theArray.length > 0) && (this.topOfStack >= -1) && (this.topOfStack < this.theArray.length);
  //@ ensures this.theArray[this.topOfStack] == x;
  public void push(final Object x) throws Overflow {
    if (isFull()) {
      throw new Overflow();
    }
    this.theArray[++this.topOfStack] = x;
  }

  //@ requires (this.theArray != null) && (this.theArray.length > 0) && !isFull() && (this.topOfStack >= -1) && (this.topOfStack < this.theArray.length);
  //@ ensures true;
  public void pushPop(final Object x) throws Overflow {
    push(x);
    final Object y = topAndPop();
    assert x == y;
  }

  /**
   * Get the most recently inserted item in the stack. Does not alter the stack.
   * 
   * @return the most recently inserted item in the stack, or null, if empty.
   */
  public Object top() {
    if (isEmpty()) {
      return null;
    }
    return this.theArray[this.topOfStack];
  }

  /**
   * Return and remove most recently inserted item from the stack.
   * 
   * @return most recently inserted item, or null, if stack is empty.
   */
  public Object topAndPop() {
    if (isEmpty()) {
      return null;
    }
    final Object topItem = top();
    this.theArray[this.topOfStack--] = null;
    return topItem;
  }

}
