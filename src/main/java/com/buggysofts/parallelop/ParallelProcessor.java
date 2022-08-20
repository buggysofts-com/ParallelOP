package com.buggysofts.parallelop;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ParallelProcessor {
    private final List<TaskRunner<?, ?>> runnableList;

    private final TasksCompletionCallback tasksCompletionCallback;

    public ParallelProcessor(@NotNull List<TaskRunner<?, ?>> runnableList, @NotNull TasksCompletionCallback tasksCompletionCallback) {
        this.runnableList = runnableList;
        this.tasksCompletionCallback = tasksCompletionCallback;
    }

    /**
     * Start executing all the passed tasks simultaneously. When they are finished, the results will be available through the TasksCompletionCallback.onComplete(...) callback.
     * Note, this method creates separate threads for each of the given tasks. So, try limiting the number of tasks per single object.
     * */
    public void start() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(runnableList.size());

        // data to pass after completing all tasks
        List<Object> results = new ArrayList<>(0);
        // assign tasks to new threads
        List<Thread> threads = new ArrayList<>(0);
        for (int i = 0; i < runnableList.size(); ++i) {
            TaskRunner<?, ?> currentRunnable = runnableList.get(i);
            threads.add(
                new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            // run the given task
                            currentRunnable.run();

                            // the given task is now complete and the result is stored in the internal result variable.
                            // we add the result to our result list, which will propagate all the way to the caller through the TasksFinalizer.onAllTasksFinished() method.
                            results.add(currentRunnable.getResult());

                            // countdown the latch to free corresponding thread lock
                            countDownLatch.countDown();
                        }
                    }
                )
            );
        }
        // start the threads
        for (int i = 0; i < runnableList.size(); ++i) {
            threads.get(i).start();
        }

        // wait from them to complete
        countDownLatch.await();

        // at this point all threads have finished their works and the combined thread lock is fully unlocked
        // now we can return user the results
        tasksCompletionCallback.onComplete(results);

        ////////////////////////
        // ALL THREADS FINISHED
        ////////////////////////
    }

    public interface TasksCompletionCallback{
        /**
         * When all the tasks are complete, TasksCompletionCallback.onComplete(...) is called with the results of the tasks.
         * @param resultList Results of the given tasks. The results are ordered in the same sequence as the tasks was given, i.e, i-th result contains the result of i-th task.
         *                   Please note, although the list itself will never be null, but its elements may be null. Please check for nullability before working with each result component.
         * */
        public void onComplete(@NotNull List<?> resultList);
    }
}
