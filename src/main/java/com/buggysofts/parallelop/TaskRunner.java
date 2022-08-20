package com.buggysofts.parallelop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TaskRunner<T, V> implements Runnable {
    private final T dataItem;
    private final Task<T, V> task;

    // this item is populated after the run() method is executed
    private V result;

    public TaskRunner(@NotNull T dataItem, @NotNull Task<T, V> task) {
        this.dataItem = dataItem;
        this.task = task;
    }

    /**
     * Result of the task. If the task is not yet completed, it will return null.
     * */
    @Nullable
    public V getResult() {
        return result;
    }

    @Override
    public void run() {
        result = task.run(dataItem);
    }
}
