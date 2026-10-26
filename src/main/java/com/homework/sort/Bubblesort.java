package com.homework.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Bubblesort<T> implements SortAlgorithm<T> {

    @Override
    public List<T> sort(List<T> list, Comparator<T> comparator) {
        List<T> result = new ArrayList<>(list);

        for (int i = 0; i < result.size() - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < result.size() - i - 1; j++) {
                if (comparator.compare(result.get(j), result.get(j + 1)) > 0) {
                    T tmp = result.get(j);
                    result.add(j, result.get(j + 1));
                    result.add(j + 1, tmp);
                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }
        return result;
    }

}
