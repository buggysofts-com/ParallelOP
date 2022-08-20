package com.buggysofts.parallelop;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        new ParallelProcessor(
            Arrays.asList(
                new TaskRunner<Integer, Integer>(
                    5,
                    new Task<Integer, Integer>() {
                        @Override
                        public Integer run(@NotNull Integer dataItem) {
                            int sum = 0;
                            for (int i = 1; i <= dataItem; ++i) {
                                try {
                                    System.out.println("Working on task 1");
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                } finally {
                                    sum += i;
                                }
                            }
                            return sum;
                        }
                    }
                ),
                new TaskRunner<Integer, Long>(
                    5,
                    new Task<Integer, Long>() {
                        @Override
                        public Long run(@NotNull Integer dataItem) {
                            long fact = 1L;
                            for (int i = 1; i <= dataItem; ++i) {
                                try {
                                    System.out.println("Working on task 2");
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                } finally {
                                    fact *= i;
                                }
                            }
                            return fact;
                        }
                    }
                )
            ),
            new ParallelProcessor.TasksCompletionCallback() {
                @Override
                public void onComplete(@NotNull List<?> resultList) {
                    for (int i = 0; i < resultList.size(); ++i) {
                        if(resultList.get(i) != null){
                            System.out.printf(
                                "Result of operation %d: %d   -  Result type: %s\n",
                                (i+1),
                                resultList.get(i),
                                resultList.get(i).getClass().getSimpleName()
                            );
                        } else {
                            System.out.printf(
                                "Result of operation %d: null\n",
                                (i+1)
                            );
                        }
                    }
                }
            }
        ).start();
    }
}
