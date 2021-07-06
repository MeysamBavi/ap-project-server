package models;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SearchQuery<T> {
    final private List<T> list;
    private T[] value;
    final private Predicate<T> predicate;
    private boolean isFinished;

    public SearchQuery(Predicate<T> predicate) {
        this.predicate = predicate;
        this.list = new ArrayList<>();
        isFinished = false;
    }

    public void feed(T object) {
        if (!isFinished && predicate.test(object)) {
            list.add(object);
        }
    }

    public void finish() {
        if (!isFinished) {
            isFinished = true;
            Integer[] a = new Integer[3];
            value = (T[]) list.toArray();
        }
    }

    public List<T> getValue() {
        return list;
    }
}
