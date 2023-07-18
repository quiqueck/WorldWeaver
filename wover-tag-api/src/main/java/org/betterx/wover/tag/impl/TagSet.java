package org.betterx.wover.tag.impl;

import org.betterx.wover.tag.api.event.context.TagElementWrapper;

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
