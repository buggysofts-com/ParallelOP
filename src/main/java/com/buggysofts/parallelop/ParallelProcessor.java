package com.buggysofts.parallelop;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ParallelProcessor {
    private final List<TaskRunner<?, ?>> taskRunnerList;

    private final TasksCompletionCallback tasksCompletionCallback;

    public ParallelProcessor(@NotNull List<TaskRunner<?, ?>> taskRunnerList,
                             @NotNull TasksCompletionCallback tasksCompletionCallback) {
        this.taskRunnerList = taskRunnerList;
        this.tasksCompletionCallback = tasksCompletionCallback;
    }

    /**
     * Start executing all the passed tasks simultaneously. When they are finished, the results will be available through the TasksCompletionCallback.onComplete(...) callback.
     * Note, this method creates separate threads for each of the given tasks. So, try limiting the number of tasks per single object.
     * */
    public void start() throws InterruptedException {
        // these two components are shared across multiple threads - make sure you access synchronously within any of the threads
        // thread locking latch
        CountDownLatch countDownLatch = new CountDownLatch(taskRunnerList.size());
        // data to pass after completing all tasks
        List<Object> results = new ArrayList<>(0);
        for (int i = 0; i < taskRunnerList.size(); i++) {
            results.add(i, null);
        }

        // assign tasks to new threads
        List<Thread> threads = new ArrayList<>(0);
        for (int i = 0; i < taskRunnerList.size(); ++i) {
            int currentIndex = i;
            TaskRunner<?, ?> currentRunner = taskRunnerList.get(i);
            threads.add(
                new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            // run the given task
                            currentRunner.run();

                            // perform operation synchronously on components that are shared on multiple threads
                            synchronized (results){
                                // the given task is now complete and the result is stored in the internal result variable.
                                // we add the result to our result list, which will propagate all the way to the caller through the TasksCompletionCallback.onComplete() method.
                                // here, we are putting the result at the same index as the task in the taskRunnerList - so that i-th result contains the result of i-th task.
                                results.set(currentIndex, currentRunner.getResult());
                            }
                            synchronized (countDownLatch){
                                // countdown the latch to free corresponding thread lock
                                countDownLatch.countDown();
                            }
                        }
                    }
                )
            );
        }
        // start the threads
        for (int i = 0; i < taskRunnerList.size(); ++i) {
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
