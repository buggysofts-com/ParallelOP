package com.buggysofts.parallelop;

import org.jetbrains.annotations.NotNull;

public interface Task<T, V> {
    public V run(@NotNull T dataItem);
}
