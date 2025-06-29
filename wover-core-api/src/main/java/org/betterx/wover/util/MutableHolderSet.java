package org.betterx.wover.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MutableHolderSet<T> extends HolderSet.ListBacked<T> {
    private final List<Holder<T>> contents;

    protected MutableHolderSet(List<Holder<T>> contents) {
        this.contents = contents;
    }

    public static <T> MutableHolderSet<T> of(List<Holder<T>> contents) {
        return new MutableHolderSet<>(contents);
    }

    public static <T> @Nullable MutableHolderSet<T> of(HolderSet<T> contents) {
        final List<Holder<T>> content = contents.unwrap().right().orElse(null);
        if (content == null) return null;
        return new MutableHolderSet<>(new LinkedList<>(content));
    }

    public HolderSet<T> asDirectHolderSet() {
        return HolderSet.direct(this.contents);
    }

    @Override
    public @NotNull List<Holder<T>> contents() {
        return contents;
    }

    @Override
    public boolean isBound() {
        return true;
    }

    @Override
    public @NotNull Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.right(this.contents);
    }

    @Override
    public boolean contains(Holder<T> holder) {
        return false;
    }

    @Override
    public @NotNull Optional<TagKey<T>> unwrapKey() {
        return Optional.empty();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            if (object instanceof MutableHolderSet<?> holder) {
                if (this.contents.equals(holder.contents)) {
                    return true;
                }
            }
            return false;
        }
    }

    public int hashCode() {
        return this.contents.hashCode();
    }
}
