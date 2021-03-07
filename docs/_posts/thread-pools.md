Thread Pools are useful when you need to **limit the number of threads running in your application at the same time**.
There is a performance overhead associated with starting a new thread, and each thread is also allocated some memory for
its stack etc.

Instead of starting a new thread for every task to execute concurrently, the task can be passed to a thread pool. As
soon as the pool has any idle threads the task is assigned to one of them and executed. Internally the tasks are
inserted into a Blocking Queue which the threads in the pool are dequeuing from. When a new task is inserted into the
queue one of the idle threads will dequeue it successfully and execute it. The rest of the idle threads in the pool will
be blocked waiting to dequeue tasks.

Thread pools are often used in multi threaded servers. Each connection arriving at the server via the network is wrapped
as a task and passed on to a thread pool. The threads in the thread pool will process the requests on the connections
concurrently.

Java 5 comes with built in thread pools in the `java.util.concurrent` package, so you don't have to implement your own
thread pool.