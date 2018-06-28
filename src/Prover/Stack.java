package Prover;

public interface Stack {
    /**
     * Adds a new item to the top of the stack.
     */
    void push(Object o)
    throws StackException;

    /**
     * Removes and returns the item at the top of the stack.
     */
    Object pop()
    throws StackException;

    /**
     * Returns the item at the top of the stack without removal.
     */
    Object top()
    throws StackException;

    /**
     * Tests to see if the stack is empty.
     */
    boolean empty();

    /**
     * Removes all items from the stack.
     */
    void clear();

    int size();
}

