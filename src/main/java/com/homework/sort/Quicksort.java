package com.homework.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Quicksort<T> implements SortAlgorithm<T> {

    @Override
    public List<T> sort(List<T> list, Comparator<T> comparator) {
        if (list.size() <= 1) {
            return new ArrayList<>(list);
        }
        T pivot = list.get(list.size() / 2);

        List<T> left = list.stream().filter(u -> comparator.compare(u, pivot) < 0).toList();
        List<T> middle = list.stream().filter(u -> comparator.compare(u, pivot) == 0).toList();
        List<T> right = list.stream().filter(u -> comparator.compare(u, pivot) > 0).toList();

        List<T> result = new ArrayList<>(list.size());

        result.addAll(sort(left, comparator));
        result.addAll(middle);
        result.addAll(sort(right, comparator));

        return result;
    }

}
