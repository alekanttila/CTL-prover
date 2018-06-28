package Prover;

/**
 * Implementation of ExerciseCode1.Stack ADT.
 *
 * This class is an implementation of the ExerciseCode1.Stack using an Array as the
 * underlying data structure. The capacity is limited and therefore
 * must be specified in the constructor. Overflow can occur when the
 * array becomes full.
 */
public class StackArray implements Stack {
    // Array containing the stack.
    private Object[] objectArray;

    // Variable keeping track of the number of items in the stack.
    private int stackSize;

    /**
     * Constructs a ExerciseCode1.Stack with maximum capacity of 50 items.
     */
    public StackArray() {
        objectArray = new Object[50];
        stackSize = 0;
    }

    /**
     * Constructs a ExerciseCode1.Stack with specified maximum capacity.
     */
    public StackArray(int size) {
        objectArray = new Object[size];
        stackSize = 0;
    }

    /**
     * Adds a new item to the top of the stack.
     */
    public void push(Object o)
    throws StackException {
        if (stackSize < objectArray.length) {
            objectArray[stackSize] = o;
            stackSize += 1;
        } else {
            throw new StackException("ExerciseCode1.Stack Overflow");
        }
    }

    /**
     * Removes and returns the item at the top of the stack.
     */
    public Object pop()
    throws StackException {
        if (stackSize != 0) {
            stackSize -= 1;
            return objectArray[stackSize];
        } else {
            throw new StackException("ExerciseCode1.Stack Underflow");
        }
    }

    /**
     * Returns the item at the top of the stack without removal.
     */
    public Object top()
    throws StackException {
        if (stackSize != 0) {
            return objectArray[stackSize - 1];
        } else {
            throw new StackException("ExerciseCode1.Stack Underflow");
        }
    }

    /**
     * Tests to see if the stack is empty.
     */
    public boolean empty() {
        return stackSize == 0;
    }

    /**
     * Removes all items from the stack.
     */
    public void clear() {
        stackSize = 0;
    }

    @Override
    public int size() {
        return stackSize;
    }
}

