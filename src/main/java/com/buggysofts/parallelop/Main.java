package com.buggysofts.parallelop;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

class Main {
    public static void main(String[] args) throws InterruptedException {
        new ParallelProcessor(
            Arrays.asList(
                new TaskRunner<Integer[], Integer>(
                    new Integer[]{1, 2, 3, 4, 5},
                    new Task<Integer[], Integer>() {
                        @Override
                        public Integer run(@NotNull Integer[] dataItem) {
                            int sum = 0;
                            for (int i = 0; i < dataItem.length; ++i) {
                                try {
                                    System.out.println("Working on task 1");
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                } finally {
                                    sum += dataItem[i];
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
                                "Result of operation %d(%s): %d   -  Result type: %s\n",
                                (i+1),
                                (i == 0) ? "Sum" : "Factorial",
                                resultList.get(i),
                                resultList.get(i).getClass().getSimpleName()
                            );
                        } else {
                            System.out.printf(
                                "Result of operation %d(%s): null\n",
                                (i+1),
                                (i == 0) ? "Sum" : "Factorial"
                            );
                        }
                    }
                }
            }
        ).start();
    }
}
