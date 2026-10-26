package com.homework.sort;

import java.util.Comparator;
import java.util.List;

public interface SortAlgorithm<T> {
    List<T> sort(List<T> list, Comparator<T> comparator);
}
