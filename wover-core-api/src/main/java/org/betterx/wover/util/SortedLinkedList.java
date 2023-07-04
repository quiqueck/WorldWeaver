package org.betterx.wover.util;

import java.util.Collection;
import java.util.Comparator;
import org.jetbrains.annotations.NotNull;

/**
 * SortedLinkedList that implements a linked list that adds new elements
 * using a Comparator and the insertionSort algorithm
 */
public class SortedLinkedList<E> implements Iterable<E>, Collection<E> {
    private Node<E> head;
    private int size;

    private final Comparator<E> comparator;

    /**
     * Constructor that takes in a comparator
     *
     * @param comparator The comparator to use for the stored elements
     */
    public SortedLinkedList(Comparator<E> comparator) {
        head = null;
        size = 0;
        this.comparator = comparator;
    }

    /**
     * Adds a new element to the list using the insertionSort algorithm
     *
     * @param data The element to add
     */
    @Override
    public boolean add(E data) {
        //If the list is empty, add the element to the head
        if (head == null) {
            head = new Node<E>(data);
        }
        //If the list is not empty, add the element to the correct position
        else {
            //If the element is less than the head, add it as new head
            if (comparator.compare(data, head.data) < 0) {
                head = new Node<E>(data, head);
            } else { //If the element is greater or equal than the head, add it to the correct position
                Node<E> current = head;
                Node<E> previous = null;

                //Loop through the list until the correct position is found
                while (current != null && comparator.compare(data, current.data) >= 0) {
                    previous = current;
                    current = current.next;
                }

                //Add the element to the correct position
                previous.next = new Node<E>(data, current);
            }
        }

        //Increment the size
        size++;

        return true;
    }

    /**
     * Removes the element at the specified index
     *
     * @param index The index of the element to remove
     * @return The element that was removed
     */
    public E remove(int index) {
        //If the index is out of bounds, throw an exception
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        //If the index is 0, remove the head
        if (index == 0) {
            Node<E> temp = head;
            head = head.next;
            size--;
            return temp.data;
        } else { //If the index is not 0, remove the element at the specified index
            Node<E> current = head;
            Node<E> previous = null;

            //Loop through the list until the correct position is found
            for (int i = 0; i < index; i++) {
                previous = current;
                current = current.next;
            }

            //Remove the element at the specified index
            previous.next = current.next;
            size--;
            return current.data;
        }
    }

    /**
     * Removes the specified element
     *
     * @param data The element to remove
     * @return True if the element was removed, false otherwise
     */
    @Override
    public boolean remove(Object data) {
        //If the list is empty, return false
        if (head == null) {
            return false;
        }

        //If the element is the head, remove the head
        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return true;
        } else { //If the element is not the head, remove the element
            Node<E> current = head;
            Node<E> previous = null;

            //Loop through the list until the correct position is found
            while (current != null && !current.data.equals(data)) {
                previous = current;
                current = current.next;
            }

            //If the element was found, remove it
            if (current != null) {
                previous.next = current.next;
                size--;
                return true;
            } else { //If the element was not found, return false
                return false;
            }
        }
    }

    /**
     * Test is all elements form the collection are part of this List
     *
     * @param c collection to be checked for containment in this collection
     * @return true if all elements of the collection are part of this List
     */
    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Adds all elements of the collection to this List
     *
     * @param c collection containing elements to be added to this collection
     * @return true if this collection changed as a result of the call
     */
    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) {
            changed |= add(e);
        }
        return changed;
    }

    /**
     * Removes all elements of the collection from this List
     *
     * @param c collection containing elements to be removed from this collection
     * @return true if this collection changed as a result of the call
     */
    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            changed |= remove(o);
        }
        return changed;
    }

    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation). In other words, removes from this
     * collection all of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this collection
     * @return true if this collection changed as a result of the call
     */
    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean changed = false;
        Node<E> current = head;
        Node<E> previous = null;

        while (current != null) {
            Node<E> cNext = current.next;
            if (!c.contains(current.data)) {
                if (previous == null) {
                    head = cNext;
                } else {
                    previous.next = cNext;
                }
                changed = true;
            }
            previous = current;
            current = cNext;
        }
        return changed;
    }

    /**
     * Returns the number of elements stored in the List
     *
     * @return The number of elements
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Removes all elements from the list
     */
    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Test if the list is empty
     *
     * @return True if the list is empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Test if the list contains the specified element
     */
    @Override
    public boolean contains(Object data) {
        Node<E> current = head;

        //Loop through the list until the correct position is found
        while (current != null && !current.data.equals(data)) {
            current = current.next;
        }

        //If the element was found, return true
        if (current != null) {
            return true;
        } else { //If the element was not found, return false
            return false;
        }
    }


    /**
     * Returns the element at the specified index
     *
     * @param index The index of the element to return
     * @return The element at the specified index
     */

    public E get(int index) {
        //If the index is out of bounds, throw an exception
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        //If the index is 0, return the head
        if (index == 0) {
            return head.data;
        } else { //If the index is not 0, return the element at the specified index
            Node<E> current = head;

            //Loop through the list until the correct position is found
            for (int i = 0; i < index; i++) {
                current = current.next;
            }

            //Return the element at the specified index
            return current.data;
        }
    }

    /**
     * Returns the first Element
     *
     * @return The first element or null if the list is empty
     */
    public E peek() {
        if (head == null) return null;
        return head.data;
    }

    /**
     * Returns the first element and removes it
     *
     * @return The first element or null if the list is empty
     */
    public E dequeue() {
        if (head == null) return null;
        return remove(0);
    }

    /**
     * Returns an iterator for the list
     *
     * @return An iterator for the list
     */
    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<>();
    }

    /**
     * Returns an array containing all of the elements in this collection. If this collection makes any guarantees as to what order its elements are returned by its iterator, this method must return the elements in the same order. The returned array's runtime component type is Object.
     * The returned array will be "safe" in that no references to it are maintained by this collection. (In other words, this method must allocate a new array even if this collection is backed by an array). The caller is thus free to modify the returned array.
     *
     * @return an array, whose runtime component type is Object, containing all of the elements in this collection
     */
    @NotNull
    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = head; x != null; x = x.next)
            result[i++] = x.data;
        return result;
    }

    /**
     * Returns an array containing all of the elements in this collection; the runtime type of the returned array is that of the specified array. If the collection fits in the specified array, it is returned therein. Otherwise, a new array is allocated with the runtime type of the specified array and the size of this collection.
     * If this collection fits in the specified array with room to spare (i.e., the array has more elements than this collection), the element in the array immediately following the end of the collection is set to null. (This is useful in determining the length of this collection only if the caller knows that this collection does not contain any null elements.)
     * If this collection makes any guarantees as to what order its elements are returned by its iterator, this method must return the elements in the same order.
     *
     * @param a the array into which the elements of this collection are to be stored, if it is big enough; otherwise, a new array of the same runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this collection
     * @throws ArrayStoreException  – if the runtime type of any element in this collection is not assignable to the runtime component type of the specified array
     * @throws NullPointerException – if the specified array is null
     */
    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        if (a.length < size)
            a = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);

        int i = 0;
        Object[] result = a;
        for (Node<E> x = head; x != null; x = x.next)
            result[i++] = x.data;

        if (a.length > size)
            a[size] = null;

        return a;
    }


    /**
     * Iterator class that iterates through the list
     */
    public class Iterator<E> implements java.util.Iterator<E> {
        private Node<E> current;


        /**
         * Constructor that sets the current node to the head
         */
        public Iterator() {
            current = (Node<E>) head;
        }


        @Override
        /**
         * Returns true if there is another element in the list
         *
         * @return True if there is another element in the list
         */
        public boolean hasNext() {
            return current != null;
        }

        @Override
        /**
         * Returns the next element in the list
         *
         * @return The next element in the list
         */
        public E next() {
            E data = current.data;
            current = current.next;
            return data;
        }
    }


    /**
     * Node class that holds the data and the next node
     *
     * @param <E> The type of data
     */
    private static class Node<E> {
        public final E data;
        private Node<E> next;

        /**
         * Constructor that takes in the data and the next node
         *
         * @param data The data
         * @param next The next node
         */
        public Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }

        /**
         * Constructor that takes in the data and sets the next node to null
         *
         * @param data The data
         */
        public Node(E data) {
            this(data, null);
        }
    }

    /**
     * Returns a string representation of the list
     *
     * @return A string representation of the list
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<E> current = head;
        while (current != null) {
            sb.append(current.data.toString());
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}

