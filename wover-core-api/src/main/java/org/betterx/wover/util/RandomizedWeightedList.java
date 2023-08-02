package org.betterx.wover.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

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

    public static <E> RandomizedWeightedList<E> of() {
        return new RandomizedWeightedList<E>();
    }

    public static <E> RandomizedWeightedList<E> of(E e1) {
        var l = new RandomizedWeightedList<E>();
        l.add(e1, 1);
        return l;
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
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
}
