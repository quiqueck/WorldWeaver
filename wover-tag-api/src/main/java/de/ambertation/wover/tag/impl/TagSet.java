package de.ambertation.wover.tag.impl;

import de.ambertation.wover.tag.api.event.context.TagElementWrapper;

import java.util.HashSet;

public class TagSet<T> extends HashSet<TagElementWrapper<T>> {
    @Override
    public boolean add(TagElementWrapper<T> element) {
        if (element.required()) {
            remove(element);
        }
        return super.add(element);
    }
}
