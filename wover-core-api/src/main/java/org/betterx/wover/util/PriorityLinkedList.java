package org.betterx.wover.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;


/**
 * A linked list that is sorted by priority when adding elements
 * <p>
 * Elements with <b>lower</b> priority are placed at the <b>end</b> of the list
 *
 * @param <T> The type of the elements in the list
 */
public class PriorityLinkedList<T> implements Iterable<T>, Collection<T> {
    /**
     * The default priority for an entry if none is specified
     */
    public static int DEFAULT_PRIORITY = 1000;
    private final SortedLinkedList<PriorityLinkedList.Entry<T>> list;

    /**
     * Constructor
     */
    public PriorityLinkedList() {
        list = new SortedLinkedList<>((a, b) -> b.priority - a.priority);
    }

    /**
     * Adds an entry to the list
     *
     * @param value    The value to add
     * @param priority The priority of the value
     * @return Whether the value was added
     */
    public boolean add(T value, int priority) {
        return list.add(new Entry<>(value, priority));
    }

    /**
     * Adds an entry to the list using the {@link #DEFAULT_PRIORITY}
     *
     * @param t element to be added to this collection
     * @return true if this collection changed as a result of the call
     */
    @Override
    public boolean add(T t) {
        return list.add(new Entry<>(t, DEFAULT_PRIORITY));
    }

    /**
     * Removes the specified element
     *
     * @param o element to be removed from this collection, if present
     * @return true if an element was removed as a result of this call
     */
    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    /**
     * Removes all elements of the collection from this List
     *
     * @param c collection containing elements to be removed from this collection
     * @return true if this collection changed as a result of the call
     */
    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return list.removeAll(c);
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
        return list.retainAll(c);
    }

    /**
     * Adds all of the elements in the specified collection with the {@link #DEFAULT_PRIORITY} to
     * this collection
     *
     * @param c collection containing elements to be added to this collection
     * @return true if this collection changed as a result of the call
     */
    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return addAll(c, DEFAULT_PRIORITY);
    }

    /**
     * Adds all of the elements in the specified collection with the specified priority to this
     * collection.
     *
     * @param c        collection containing elements to be added to this collection
     * @param priority The priority of the elements
     * @return true if this collection changed as a result of the call
     */
    public boolean addAll(@NotNull Collection<? extends T> c, int priority) {
        boolean changed = false;
        for (T e : c) {
            changed |= add(e, priority);
        }
        return changed;
    }


    /**
     * Clears the list
     */
    public void clear() {
        list.clear();
    }

    /**
     * Gets the size of the list
     *
     * @return The size of the list
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * Checks if the list is empty
     *
     * @return true if the list is empty
     */
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Checks if the list contains the specified element
     *
     * @param o element whose presence in this collection is to be tested
     * @return true if this collection contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    /**
     * Test is all elements form the collection are part of this List
     *
     * @param c collection to be checked for containment in this collection
     * @return true if all elements of the collection are part of this List
     */
    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return list.containsAll(c);
    }

    /**
     * Returns an iterator for the list
     *
     * @return The iterator
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new PriorityLinkedListIterator<>(list.iterator());
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
        Object[] result = new Object[size()];
        int i = 0;
        for (Entry<T> entry : list)
            result[i++] = entry.value;
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
    public <T1> T1[] toArray(@NotNull T1[] a) {
        if (a.length < size())
            a = (T1[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size());

        int i = 0;
        Object[] result = a;
        for (Entry<T> entry : list)
            result[i++] = entry.value;

        if (a.length > size())
            a[size()] = null;

        return a;
    }

    /**
     * Gets the element at the specified index
     *
     * @param i The index
     * @return The element
     */
    public T get(int i) {
        return list.get(i).value;
    }


    //Iterator class for PriorityLinkedList
    private static class PriorityLinkedListIterator<T> implements Iterator<T> {
        private final Iterator<Entry<T>> iterator;

        public PriorityLinkedListIterator(Iterator<Entry<T>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next().value;
        }
    }

    private static class Entry<T> {
        public final T value;
        private final int priority;

        public Entry(T value, int priority) {
            this.value = value;
            this.priority = priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Entry<?> that) {
                return Objects.equals(value, that.value);
            } else if (o != null) {
                return o.equals(value);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return value.toString() + " - " + priority;
        }
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
