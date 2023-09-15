package org.betterx.wover.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A list that allows to get a random value based on the weight of the entries.
 *
 * @param <T> the type of the values
 */
public class RandomizedWeightedList<T> {
    public static <T> Codec<RandomizedWeightedList<T>> buildCodec(Codec<T> elementCodec) {
        return RecordCodecBuilder.create((instance) -> instance
                .group(
                        ExtraCodecs.nonEmptyList(WeightedEntry.buildElementCodec(elementCodec).listOf())
                                   .fieldOf("items").forGetter((RandomizedWeightedList<T> cfg) -> cfg.entries)
                )
                .apply(instance, RandomizedWeightedList::new));
    }

    private final List<WeightedEntry<T>> entries;

    private double totalWeight;

    private RandomizedWeightedList(List<WeightedEntry<T>> entries) {
        this.entries = entries;
        totalWeight = entries.stream().map(w -> w.weight).reduce(0.0, Double::sum);
    }

    /**
     * Creates a new instance.
     */
    public RandomizedWeightedList() {
        entries = new LinkedList<>();
        totalWeight = 0.0;
    }

    /**
     * Returns the total weight of all entries.
     *
     * @return the total weight
     */
    public double getTotalWeight() {
        return totalWeight;
    }

    /**
     * Adds a new entry to the list.
     *
     * @param value  the value
     * @param weight the weight
     */
    public void add(T value, double weight) {
        if (weight <= 0.0) {
            throw new IllegalArgumentException("Weight must be greater than zero.");
        }

        entries.add(new WeightedEntry<>(value, weight));
        totalWeight += weight;
    }

    /**
     * Removes an entry from the list.
     *
     * @param value the value
     */
    public void remove(T value) {
        for (WeightedEntry<T> entry : entries) {
            if (entry.getValue().equals(value)) {
                totalWeight -= entry.getWeight();
                entries.remove(entry);
                return;
            }
        }
    }

    /**
     * Returns a random value from the list.
     *
     * @param random the random generator to use
     * @return the randomly chosen value
     */
    public T getRandomValue(Random random) {
        return getRandomValue(random::nextDouble);
    }

    /**
     * Returns a random value from the list.
     *
     * @param random the random generator to use
     * @return the randomly chosen value
     */
    public T getRandomValue(RandomSource random) {
        return getRandomValue(random::nextDouble);
    }

    /**
     * Maps the each vaue for this list to a new type and creates a new list with the mapped values and the
     * original weights.
     *
     * @param mapper the mapping function
     * @param <R>    the new type
     * @return the new list
     */
    public <R> RandomizedWeightedList<R> map(Function<T, R> mapper) {
        RandomizedWeightedList<R> res = new RandomizedWeightedList<>();
        for (WeightedEntry<T> e : entries) {
            res.add(mapper.apply(e.getValue()), e.getWeight());
        }
        return res;
    }

    /**
     * Iterates over all entries in the list.
     *
     * @param consumer the consumer which will accept the value and assigned weight
     */
    public void forEach(BiConsumer<T, Double> consumer) {
        for (WeightedEntry<T> e : entries) {
            consumer.accept(e.getValue(), e.getWeight());
        }
    }

    @FunctionalInterface
    private interface RandomValueSupplier {
        double nextDouble();
    }

    private T getRandomValue(RandomValueSupplier rnd) {
        if (entries.isEmpty()) {
            throw new IllegalStateException("The list is empty.");
        }

        double randomValue = rnd.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;

        for (WeightedEntry<T> entry : entries) {
            cumulativeWeight += entry.getWeight();
            if (randomValue < cumulativeWeight) {
                return entry.getValue();
            }
        }

        // Return the last element if no match is found
        return entries.get(entries.size() - 1).getValue();
    }

    /**
     * Creates a new, empty instance.
     *
     * @param <E> the type of the values
     * @return a new, empty instance
     */
    public static <E> RandomizedWeightedList<E> of() {
        return new RandomizedWeightedList<E>();
    }

    /**
     * Creates a new instance with a single element.
     *
     * @param e1  the element
     * @param <E> the type of the values
     * @return the new instance
     */
    public static <E> RandomizedWeightedList<E> of(E e1) {
        var l = new RandomizedWeightedList<E>();
        l.add(e1, 1);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(E e0, Double w0, E e1, Double w1) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(E e0, Double w0, E e1, Double w1, E e2, Double w2) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(E e0, Double w0, E e1, Double w1, E e2, Double w2, E e3, Double w3) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5,
            E e6,
            Double w6
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        l.add(e6, w6);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5,
            E e6,
            Double w6,
            E e7,
            Double w7
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        l.add(e6, w6);
        l.add(e7, w7);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5,
            E e6,
            Double w6,
            E e7,
            Double w7,
            E e8,
            Double w8
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        l.add(e6, w6);
        l.add(e7, w7);
        l.add(e8, w8);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5,
            E e6,
            Double w6,
            E e7,
            Double w7,
            E e8,
            Double w8,
            E e9,
            Double w9
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        l.add(e6, w6);
        l.add(e7, w7);
        l.add(e8, w8);
        l.add(e9, w9);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5,
            E e6,
            Double w6,
            E e7,
            Double w7,
            E e8,
            Double w8,
            E e9,
            Double w9,
            E e10,
            Double w10
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        l.add(e6, w6);
        l.add(e7, w7);
        l.add(e8, w8);
        l.add(e9, w9);
        l.add(e10, w10);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5,
            E e6,
            Double w6,
            E e7,
            Double w7,
            E e8,
            Double w8,
            E e9,
            Double w9,
            E e10,
            Double w10,
            E e11,
            Double w11
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        l.add(e6, w6);
        l.add(e7, w7);
        l.add(e8, w8);
        l.add(e9, w9);
        l.add(e10, w10);
        l.add(e11, w11);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5,
            E e6,
            Double w6,
            E e7,
            Double w7,
            E e8,
            Double w8,
            E e9,
            Double w9,
            E e10,
            Double w10,
            E e11,
            Double w11,
            E e12,
            Double w12
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        l.add(e6, w6);
        l.add(e7, w7);
        l.add(e8, w8);
        l.add(e9, w9);
        l.add(e10, w10);
        l.add(e11, w11);
        l.add(e12, w12);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5,
            E e6,
            Double w6,
            E e7,
            Double w7,
            E e8,
            Double w8,
            E e9,
            Double w9,
            E e10,
            Double w10,
            E e11,
            Double w11,
            E e12,
            Double w12,
            E e13,
            Double w13
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        l.add(e6, w6);
        l.add(e7, w7);
        l.add(e8, w8);
        l.add(e9, w9);
        l.add(e10, w10);
        l.add(e11, w11);
        l.add(e12, w12);
        l.add(e13, w13);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5,
            E e6,
            Double w6,
            E e7,
            Double w7,
            E e8,
            Double w8,
            E e9,
            Double w9,
            E e10,
            Double w10,
            E e11,
            Double w11,
            E e12,
            Double w12,
            E e13,
            Double w13,
            E e14,
            Double w14
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        l.add(e6, w6);
        l.add(e7, w7);
        l.add(e8, w8);
        l.add(e9, w9);
        l.add(e10, w10);
        l.add(e11, w11);
        l.add(e12, w12);
        l.add(e13, w13);
        l.add(e14, w14);
        return l;
    }

    public static <E> RandomizedWeightedList<E> of(
            E e0,
            Double w0,
            E e1,
            Double w1,
            E e2,
            Double w2,
            E e3,
            Double w3,
            E e4,
            Double w4,
            E e5,
            Double w5,
            E e6,
            Double w6,
            E e7,
            Double w7,
            E e8,
            Double w8,
            E e9,
            Double w9,
            E e10,
            Double w10,
            E e11,
            Double w11,
            E e12,
            Double w12,
            E e13,
            Double w13,
            E e14,
            Double w14,
            E e15,
            Double w15
    ) {
        var l = new RandomizedWeightedList<E>();
        l.add(e0, w0);
        l.add(e1, w1);
        l.add(e2, w2);
        l.add(e3, w3);
        l.add(e4, w4);
        l.add(e5, w5);
        l.add(e6, w6);
        l.add(e7, w7);
        l.add(e8, w8);
        l.add(e9, w9);
        l.add(e10, w10);
        l.add(e11, w11);
        l.add(e12, w12);
        l.add(e13, w13);
        l.add(e14, w14);
        l.add(e15, w15);
        return l;
    }

    /**
     * Returns the number of elements stored in the list
     *
     * @return the number of elements
     */
    public int size() {
        return entries.size();
    }

    /**
     * Returns whether the list is empty.
     *
     * @return {@code true} if the list is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Returns a sublist of this list.
     *
     * @param fromIndex – low endpoint (inclusive) of the subList
     * @param toIndex   – high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     */
    public RandomizedWeightedList<T> subList(int fromIndex, int toIndex) {
        return new RandomizedWeightedList<>(entries.subList(fromIndex, toIndex));
    }

    /**
     * Creates a search tree for this list. This is a useful speedup if you want to get multiple
     * random values from the same list (at least if the list is large).
     *
     * @return a new search tree
     */
    public SearchTree buildSearchTree() {
        return new SearchTree(this);
    }

    private static class WeightedEntry<T> {
        public static <T> Codec<WeightedEntry<T>> buildElementCodec(Codec<T> elementCodec) {
            return RecordCodecBuilder.create((instance) -> instance
                    .group(
                            elementCodec.fieldOf("value").forGetter(WeightedEntry::getValue),
                            Codec.DOUBLE.fieldOf("weight").orElse(1.0).forGetter(WeightedEntry::getWeight)
                    )
                    .apply(instance, WeightedEntry::new)
            );
        }

        private final T value;
        private final double weight;

        public WeightedEntry(T value, double weight) {
            this.value = value;
            this.weight = weight;
        }

        public T getValue() {
            return value;
        }

        public double getWeight() {
            return weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WeightedEntry<?> that)) return false;
            return Double.compare(that.weight, weight) == 0 && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, weight);
        }
    }

    /**
     * A search tree for a {@link RandomizedWeightedList}.
     */
    public class SearchTree {
        @FunctionalInterface
        private interface RandomFloatValueSupplier {
            float nextFloat();
        }

        private final Node root;

        private SearchTree(RandomizedWeightedList<T> list) {
            root = getNode(list);
        }

        private T getRandomValue(RandomFloatValueSupplier random) {
            return root.get(random.nextFloat() * (float) totalWeight);
        }

        /**
         * Returns a random value from the tree.
         *
         * @param random the random generator to use
         * @return the randomly chosen value
         */
        public T getRandomValue(Random random) {
            return getRandomValue(random::nextFloat);
        }

        /**
         * Returns a random value from the tree.
         *
         * @param random the random generator to use
         * @return the randomly chosen value
         */
        public T getRandomValue(RandomSource random) {
            return getRandomValue(random::nextFloat);
        }

        /**
         * Returns whether the tree is empty.
         *
         * @return {@code true} if the tree is empty, {@code false} otherwise
         */
        public boolean isEmpty() {
            return root == null || (root instanceof Leaf l && l.biome == null);
        }

        private Node getNode(RandomizedWeightedList<T> list) {
            List<Float> cumulativeWeights = new ArrayList<>(list.size());
            float sum = 0;
            for (int i = 0; i < list.size(); i++) {
                sum += (float) list.entries.get(i).weight;
                cumulativeWeights.add(sum);
            }

            return getNode(list, cumulativeWeights);
        }

        private Node getNode(RandomizedWeightedList<T> list, List<Float> cumulativeWeights) {
            int size = list.size();
            if (size == 0) {
                return new Leaf(null);
            } else if (size == 1) {
                return new Leaf(list.entries.get(0).value);
            } else if (size == 2) {
                return new Branch(
                        cumulativeWeights.get(0),
                        new SearchTree.Leaf(list.entries.get(0).value),
                        new SearchTree.Leaf(list.entries.get(1).value)
                );
            } else {
                final int midIndex = size >> 1;
                float separator = cumulativeWeights.get(midIndex - 1);
                Node lower = getNode(list.subList(0, midIndex), cumulativeWeights.subList(0, midIndex));
                Node upper = getNode(list.subList(midIndex, size), cumulativeWeights.subList(midIndex, size));
                return new Branch(separator, lower, upper);
            }
        }

        private abstract class Node {
            abstract T get(float value);
        }

        private class Branch extends Node {
            final float separator;
            final Node min;
            final Node max;

            public Branch(float separator, Node min, Node max) {
                this.separator = separator;
                this.min = min;
                this.max = max;
            }

            @Override
            T get(float value) {
                return value < separator ? min.get(value) : max.get(value);
            }

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "[%f, %s, %s]", separator, min.toString(), max.toString());
            }
        }

        private class Leaf extends Node {
            final T biome;

            Leaf(T value) {
                this.biome = value;
            }

            @Override
            T get(float value) {
                return biome;
            }

            @Override
            public String toString() {
                return String.format(Locale.ROOT, "[%s]", biome.toString());
            }
        }

        @Override
        public String toString() {
            return root.toString();
        }
    }
}
