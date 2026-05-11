/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Custom generic stack backed by a singly-linked list. Used by
 *              CombatEngine to record combat events within each round. Follows
 *              LIFO (last-in, first-out) ordering, so the most recent event
 *              surfaces first when the log is popped for the round summary.
 *              The inner Node<T> class mirrors the structure used in LinkedList<T>
 *              but push/pop target the head rather than inserting in sorted order.
 * Inputs:      Generic type T pushed via push(T item); items removed via pop()
 * Outputs:     Top element via peek(); full stack contents via display();
 *              size via getSize(); popped entries consumed via pop() in a loop
 *
 * Change Log:
 *   2026-04-08  Shane Potts  Initial creation - Phase 2; generic LIFO stack
 *                             with inner Node<T> class; push(), pop(), peek(),
 *                             clear(), isEmpty(), getSize(), display(); used by
 *                             CombatEngine to record and replay per-round events
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */

package bolterandburden;

public class CombatLog<T> {

    // -------------------------------------------------------------------------
    // Inner Node class
    // -------------------------------------------------------------------------

    /**
     * A single node in the stack. Holds a data reference and a pointer to
     * the node below it in the stack.
     *
     * @param <T> The type of data stored in this node
     */
    private class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private Node<T> top;
    private int size;

    /**
     * Constructs an empty CombatLog stack.
     */
    public CombatLog() {
        top  = null;
        size = 0;
    }

    // -------------------------------------------------------------------------
    // Stack Operations
    // -------------------------------------------------------------------------

    /**
     * Pushes a new item onto the top of the stack in O(1) time.
     *
     * @param item The item to push
     */
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top  = newNode;
        size++;
    }

    /**
     * Removes and returns the top item in O(1) time.
     * Returns null if the stack is empty.
     *
     * @return The item that was on top, or null if empty
     */
    public T pop() {
        if (top == null) {
            return null;
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    /**
     * Returns the top item without removing it. Returns null if empty.
     *
     * @return The top item, or null if the stack is empty
     */
    public T peek() {
        return (top == null) ? null : top.data;
    }

    /**
     * Removes all items from the stack, resetting it to empty state.
     */
    public void clear() {
        top  = null;
        size = 0;
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /**
     * Returns the number of items currently in the stack.
     *
     * @return Current stack size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns true if the stack contains no items.
     *
     * @return true if size == 0
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Prints every item's toString() to the console from top to bottom
     * without consuming the stack. Useful for inspection without draining
     * the log before the round summary.
     */
    public void display() {
        if (top == null) {
            System.out.println("  (log empty)");
            return;
        }
        Node<T> current = top;
        int position = 1;
        while (current != null) {
            System.out.println("    [" + position + "] " + current.data.toString());
            current = current.next;
            position++;
        }
    }
}

/*
 * Change Log:
 *   2026-04-08  Shane Potts  Initial creation - Phase 2; generic LIFO stack
 *                             with inner Node<T> class; push(), pop(), peek(),
 *                             clear(), isEmpty(), getSize(), display(); used by
 *                             CombatEngine to record and replay per-round events
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes
 */
