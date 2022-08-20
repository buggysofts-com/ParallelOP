# ParallelOP [![](https://jitpack.io/v/buggysofts-com/ParallelOP.svg)](https://jitpack.io/#buggysofts-com/ParallelOP)

A simple, yet very powerful library to perform parallel operations with any kind of data. It can operate on any kind of data, to be specific, it can work with multiple types of data simultaneously.

<br />

## Import

### Maven

Add JitPack repository to your <b>pom.xml</b> file
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Finally, add this dependency.
```
<dependency>
    <groupId>com.github.buggysofts-com</groupId>
    <artifactId>ParallelOP</artifactId>
    <version>v1.0.8</version>
</dependency>
```
And you are done importing the library in your maven project.

<br />

### Gradle

Add JitPack repository to your project-level build.gradle file
```
...

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Or, in newer gradle projects, specially in android, if you need to the add repository in settings.gradle file...
```
...

dependencyResolutionManagement {
    ...
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Finally, add this dependency to your app/module level build.gradle file
```
...

dependencies {
    ...
    implementation 'com.github.buggysofts-com:ParallelOP:v1.0.8'
}
```
And you are done importing the library in your gradle project.

<br />

## Sample codes
Don't just get scared seeing the length of the code bellow, most of it is for demo.
<br />
<br />
Here we are running 2 parallel tasks. In the first one, we are giving an <b>Integer array</b> as input and specifying <b>Integer</b> as the desired output. We are specifying this with the ```new TaskRunner<Integer[], Integer>```. Each <b>TaskRunner</b> object takes in two parameters, a data item of the specified input type, and a <b>Task</b> object that defines the operation to be performed on that data item. This <b>Task</b> object also requires the same type parameters as with <b>TaskRunner</b>. The <b>run()</b> method within the <b>Task</b> class is where you define the operation to be performed. The same data item we passed as the first parameter of <b>TaskRunner</b> is passed to this method so that you can use it to perform any kind of operation you want. This method returns the result of the operation. This result is of the type we specified as our output type. Similarly, we have a second Task for which input type is <b>Integer</b> and output type is <b>Long</b>.
<br />
<br />
Lastly, we have a tasks completion callback defined as the last parameter of <b>ParallelProcessor</b>. The <b>onComplete(@NotNull List<?> resultList)</b> method is called (on the calling thread) when all the parallel operations are complete, with a list of results. This list can contain multiple type of data depending on the output type you specified for each task. For instance, for this sample code bellow, the result list will contain two results, the first one is of type <b>Integer</b> and the second one is of type <b>Long</b>. The number of items in the result list will be exactly the same as the number of tasks, and order of the result items will be the order in which tasks were added. That means, i-th result item contains the result of i-th task.
```
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
```