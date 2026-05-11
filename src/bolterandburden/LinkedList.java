/**
 * Author:      Shane Potts
 * Project:     Bolter & Burden: The Traitor's Throne
 * Class:       CSC 1061 Computer Science II - Java, FRCC Spring 2026
 * Description: Custom generic singly-linked list. Provides all roster
 *              management operations needed by Warband. The inner Node<T>
 *              class holds data and a next pointer. insertSorted() requires
 *              T to implement Comparable<T>, which Unit satisfies via its
 *              speed-based compareTo() implementation.
 * Inputs:      Generic type T; data inserted via insertFront() or insertSorted();
 *              targets for remove() and search() passed as method arguments
 * Outputs:     Inserted/removed data reflected in list state; search returns T
 *              or null; display() prints each node's toString() to console
 *
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; generic singly-linked
 *                             list with inner Node<T>; insertFront(), insertSorted()
 *                             (requires Comparable<T>), remove(), search(), display(),
 *                             getSize(), isEmpty()
 *   2026-03-23  Shane Potts  Phase 2 - added toArrayList() so CombatEngine can load
 *                             roster into PriorityQueue each round
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; used by Warband
 *                             for all roster operations throughout the system
 */

package bolterandburden;

import java.util.ArrayList;

public class LinkedList<T> {

    // -------------------------------------------------------------------------
    // Inner Node class
    // -------------------------------------------------------------------------

    /**
     * A single node in the linked list. Holds a data reference and a pointer
     * to the next node in the chain.
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

    private Node<T> head;
    private int size;

    /**
     * Constructs an empty linked list.
     */
    public LinkedList() {
        head = null;
        size = 0;
    }

    // -------------------------------------------------------------------------
    // Insert Operations
    // -------------------------------------------------------------------------

    /**
     * Inserts a new element at the front of the list in O(1) time.
     *
     * @param data The element to prepend to the list
     */
    public void insertFront(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = head;
        head = newNode;
        size++;
    }

    /**
     * Inserts a new element in sorted order. Requires T to implement
     * Comparable<T>. Elements that compare as "less" (i.e., higher initiative
     * for Unit) are placed nearer the front of the list.
     *
     * @param data The element to insert in sorted position
     */
    @SuppressWarnings("unchecked")
    public void insertSorted(T data) {
        Node<T> newNode = new Node<>(data);

        // Insert at front if list is empty or new node belongs before head
        if (head == null || ((Comparable<T>) data).compareTo(head.data) <= 0) {
            newNode.next = head;
            head = newNode;
            size++;
            return;
        }

        // Walk until we find the insertion point
        Node<T> current = head;
        while (current.next != null
                && ((Comparable<T>) data).compareTo(current.next.data) > 0) {
            current = current.next;
        }
        newNode.next = current.next;
        current.next = newNode;
        size++;
    }

    // -------------------------------------------------------------------------
    // Remove Operation
    // -------------------------------------------------------------------------

    /**
     * Removes the first occurrence of the specified element using .equals().
     * Does nothing if the element is not found.
     *
     * @param target The element to remove
     * @return true if the element was found and removed, false otherwise
     */
    public boolean remove(T target) {
        if (head == null) {
            return false;
        }

        // Check if head is the target
        if (head.data.equals(target)) {
            head = head.next;
            size--;
            return true;
        }

        // Walk the list looking for target
        Node<T> current = head;
        while (current.next != null) {
            if (current.next.data.equals(target)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Search Operation
    // -------------------------------------------------------------------------

    /**
     * Searches the list for a node whose toString() contains the given name
     * string (case-insensitive). Returns the first match found.
     *
     * @param name The name string to search for
     * @return The matching element, or null if no match is found
     */
    public T search(String name) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.toString().toLowerCase().contains(name.toLowerCase())) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Display Operation
    // -------------------------------------------------------------------------

    /**
     * Prints every element's toString() to the console, one per line,
     * preceded by its position number in the list.
     */
    public void display() {
        if (head == null) {
            System.out.println("  (empty)");
            return;
        }
        Node<T> current = head;
        int position = 1;
        while (current != null) {
            System.out.println("  [" + position + "] " + current.data.toString());
            current = current.next;
            position++;
        }
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /**
     * Returns the number of elements currently in the list.
     *
     * @return Current size of the list
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns true if the list contains no elements.
     *
     * @return true if size == 0
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Copies all elements into a new ArrayList in list order (head first).
     * Used by CombatEngine to load units into the initiative queue.
     *
     * @return ArrayList containing every element currently in the list
     */
    public ArrayList<T> toArrayList() {
        ArrayList<T> result = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            result.add(current.data);
            current = current.next;
        }
        return result;
    }
}

/*
 * Change Log:
 *   2026-03-23  Shane Potts  Initial creation - Phase 1; generic singly-linked
 *                             list with inner Node<T>; insertFront(), insertSorted()
 *                             (requires Comparable<T>), remove(), search(), display(),
 *                             getSize(), isEmpty()
 *   2026-03-23  Shane Potts  Phase 2 - added toArrayList() so CombatEngine can load
 *                             roster into PriorityQueue each round
 *   2026-04-08  Shane Potts  Added change log to top-level Javadoc per project instructions
 *   2026-04-26  Shane Potts  Phase 3 complete - no further changes; used by Warband
 *                             for all roster operations throughout the system
 */
