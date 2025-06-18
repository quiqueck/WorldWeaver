package org.betterx.wover.util;

public final class GrowableArray<T> {
    private T[] elements;


    @SuppressWarnings("unchecked")
    public GrowableArray(T... elements) {
        this.elements = elements;
    }

    @SuppressWarnings("unchecked")
    public final void add(T element) {
        T[] newElements = (T[]) new Object[elements.length + 1];
        System.arraycopy(elements, 0, newElements, 0, elements.length);
        newElements[elements.length] = element;
        elements = newElements;
    }

    @SuppressWarnings("unchecked")
    public final void add(T... newElements) {
        T[] combined = (T[]) new Object[elements.length + newElements.length];
        System.arraycopy(elements, 0, combined, 0, elements.length);
        System.arraycopy(newElements, 0, combined, elements.length, newElements.length);
        elements = combined;
    }

    public T[] elements() {
        return elements;
    }

}