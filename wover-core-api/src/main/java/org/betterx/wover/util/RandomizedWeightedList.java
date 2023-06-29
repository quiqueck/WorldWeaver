package org.betterx.wover.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A list that allows to get a random value based on the weight of the entries.
 *
 * @param <T> the type of the values
 */
public class RandomizedWeightedList<T> {
    private final List<WeightedEntry<T>> entries;

    private double totalWeight;

    /**
     * Creates a new instance.
     */
    public RandomizedWeightedList() {
        entries = new LinkedList<>();
        totalWeight = 0.0;
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
        if (entries.isEmpty()) {
            throw new IllegalStateException("The list is empty.");
        }

        double randomValue = random.nextDouble() * totalWeight;
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

    private static class WeightedEntry<T> {
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
}
