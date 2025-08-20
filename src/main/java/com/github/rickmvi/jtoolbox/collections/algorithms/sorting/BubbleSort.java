package com.github.rickmvi.jtoolbox.collections.algorithms.sorting;

import com.github.rickmvi.jtoolbox.control.Iteration;
import com.github.rickmvi.jtoolbox.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@lombok.experimental.UtilityClass
public class BubbleSort {

    public void sort(int @NotNull [] arr) {
        int length = arr.length;
        Iteration.bubble(0, length, (i, j) -> {
            if (arr[i] > arr[j]) {
                int assistant = arr[i];
                arr[i] = arr[j];
                arr[j] = assistant;
            }
        });
    }

    public List<Integer> sort(String input) {
        if (input == null || input.isEmpty()) return new ArrayList<>();
        List<Integer> result = new ArrayList<>();

        int[] arrInt = ArrayUtils.convert(input);

        Iteration.bubble(0, arrInt.length, (i, j) -> {
            if (arrInt[i] > arrInt[j]) {
                int assistant = arrInt[i];
                arrInt[i] = arrInt[j];
                arrInt[j] = assistant;
            }
        });

        for (int i : arrInt) {
            result.add(i);
        }
        return result;
    }

    public void sort(@NotNull String @NotNull [] arr) {
        int length = arr.length;

        Iteration.bubble(0, length, (i, j) -> {
            if (arr[i].compareTo(arr[j]) > 0) {
                String assistant = arr[i];
                arr[i] = arr[j];
                arr[j] = assistant;
            }
        });
    }
    
}
