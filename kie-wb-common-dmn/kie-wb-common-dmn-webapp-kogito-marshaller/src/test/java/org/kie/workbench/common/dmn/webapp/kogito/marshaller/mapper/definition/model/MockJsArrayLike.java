package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jsinterop.base.JsArrayLike;

class MockJsArrayLike<T> implements JsArrayLike<T> {

    private List<T> elements = new ArrayList<>();

    MockJsArrayLike(final T... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    @Override
    public int getLength() {
        return elements.size();
    }

    @Override
    public void setLength(final int length) {
        this.elements = new ArrayList<>(length);
    }

    @Override
    public T getAt(int index) {
        return elements.get(index);
    }

    @Override
    public void setAt(final int index,
                      final T value) {
        this.elements.add(index, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] asArray() {
        return (T[]) elements.toArray();
    }
}
