/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Custom generic priority queue backed by a singly-linked list
 *              with insertion sort. The type parameter is bounded by
 *              Comparable<T> so only orderable types can be stored. Elements
 *              are maintained in ascending natural order (lowest compareTo
 *              value at the head), so the element that sorts first in natural
 *              ordering is always dequeued first. Because Unit.compareTo()
 *              orders by speed descending (highest speed = lowest compareTo
 *              value), the fastest unit in any PriorityQueue<Unit> is always
 *              at the head - ready to act first in initiative order.
 * Inputs:      Generic type T extends Comparable<T>; elements enqueued via
 *              enqueue(T item)
 * Outputs:     Elements dequeued in initiative order via dequeue(); head
 *              element inspected without removal via peek()
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; generic priority queue
 *                             backed by insertion-sorted singly-linked list; T
 *                             bounded by Comparable<T>; enqueue() maintains sorted
 *                             order; dequeue() removes head; Unit.compareTo() orders
 *                             by speed descending so fastest unit acts first
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; two PriorityQueue
 *                             instances per round in CombatEngine (Fights First tier
 *                             and standard tier)
 */

package bolterandburden;

public class PriorityQueue<T extends Comparable<T>> {

    // -------------------------------------------------------------------------
    // Inner Node class
    // -------------------------------------------------------------------------

    private class Node {
        T data;
        Node next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private Node head;
    private int size;

    /**
     * Constructs an empty priority queue.
     */
    public PriorityQueue() {
        head = null;
        size = 0;
    }

    // -------------------------------------------------------------------------
    // Enqueue - insertion sort to maintain priority order
    // -------------------------------------------------------------------------

    /**
     * Inserts an element into the queue in sorted position using insertion sort.
     * Elements with a lower compareTo value are placed nearer the front.
     * For Unit objects, this means the highest-speed unit is always at the head.
     *
     * @param item The element to insert
     */
    public void enqueue(T item) {
        Node newNode = new Node(item);

        // Insert at head if queue is empty or item has higher priority (lower compareTo)
        if (head == null || item.compareTo(head.data) <= 0) {
            newNode.next = head;
            head = newNode;
            size++;
            return;
        }

        // Walk to the correct sorted position
        Node current = head;
        while (current.next != null && item.compareTo(current.next.data) > 0) {
            current = current.next;
        }
        newNode.next = current.next;
        current.next = newNode;
        size++;
    }

    // -------------------------------------------------------------------------
    // Dequeue - remove and return the highest-priority element
    // -------------------------------------------------------------------------

    /**
     * Removes and returns the highest-priority element (the head of the queue).
     *
     * @return The element with the highest priority, or null if the queue is empty
     */
    public T dequeue() {
        if (head == null) {
            return null;
        }
        T data = head.data;
        head = head.next;
        size--;
        return data;
    }

    // -------------------------------------------------------------------------
    // Peek - inspect head without removing
    // -------------------------------------------------------------------------

    /**
     * Returns the highest-priority element without removing it.
     *
     * @return The head element, or null if the queue is empty
     */
    public T peek() {
        return head == null ? null : head.data;
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /**
     * Returns true if the queue contains no elements.
     *
     * @return true if size == 0
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements currently in the queue.
     *
     * @return Current size of the queue
     */
    public int getSize() {
        return size;
    }

    /**
     * Prints all elements in priority order without removing them.
     * Used for debugging and test verification.
     */
    public void display() {
        if (head == null) {
            System.out.println("  (empty queue)");
            return;
        }
        Node current = head;
        int position = 1;
        while (current != null) {
            System.out.println("  [" + position + "] " + current.data.toString());
            current = current.next;
            position++;
        }
    }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 2; generic priority queue
 *                             backed by insertion-sorted singly-linked list; T
 *                             bounded by Comparable<T>; enqueue() maintains sorted
 *                             order; dequeue() removes head; Unit.compareTo() orders
 *                             by speed descending so fastest unit acts first
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; two PriorityQueue
 *                             instances per round in CombatEngine (Fights First tier
 *                             and standard tier)
 */
