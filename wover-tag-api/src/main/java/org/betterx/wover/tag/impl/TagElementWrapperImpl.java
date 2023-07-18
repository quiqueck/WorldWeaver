package org.betterx.wover.tag.impl;

import org.betterx.wover.tag.api.event.context.TagElementWrapper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;

import java.util.Objects;

public record TagElementWrapperImpl<T>(ResourceLocation id, boolean tag,
                                       boolean required) implements TagElementWrapper<T> {
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.tag) {
            stringBuilder.append('#');
        }

        stringBuilder.append(this.id);
        if (!this.required) {
            stringBuilder.append('?');
        }

        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagElementWrapperImpl<?> that)) return false;
        return tag == that.tag && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tag);
    }

    public TagEntry createTagEntry() {
        if (tag) {
            if (required) {
                return TagEntry.tag(id);
            } else {
                return TagEntry.optionalTag(id);
            }
        } else {
            if (required) {
                return TagEntry.element(id);
            } else {
                return TagEntry.optionalElement(id);
            }
        }
    }
}
