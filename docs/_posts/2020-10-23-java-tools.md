---
layout: post
title: Java Tools
tags: [Java]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/18-cover.png"
thumbnail: "assets/img/post-cover/18-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

* [VisualVM](https://visualvm.github.io/)
* [jConsole](https://openjdk.java.net/tools/svc/jconsole/)

## The `jps` Utility

`jps` provides a list of all active JVM processes on the local machine (or a remote machine, if a suitable instance of
`jstatd` is running on the remote side).

Examples below demonstrates the usage of the jps utility.

```bash
$ jps
16217 MyApplication
16342 jps
```

The utility lists the virtual machines for which the user has access rights. This is determined by access-control
mechanisms specific to the operating system. On Oracle Solaris operating system, for example, if a non-root user
executes the jps utility, then the output is a list of the virtual machines that were started with that user's uid.

In addition to listing the PID, the utility provides options to output the arguments passed to the application's `main`
method, the complete list of VM arguments, and the full package name of the application's main class. The `jps` utility
can also list processes on a remote system if the remote system is running the `jstatd` daemon.

If you are running several Java Web Start applications on a system, they tend to look the same, as shown below

```bash
    $ jps
    1271 jps
         1269 Main
         1190 Main
```

In this case, use `jps -m` to distinguish them, as shown in the example below

```bash
    $ jps -m
    1271 jps -m
         1269 Main http://bugster.central.sun.com/bugster.jnlp
         1190 Main http://webbugs.sfbay/IncidentManager/incident.jnlp
```

## The `jstat` Utility

The `jstat` utility uses the built-in instrumentation in the Java HotSpot VM to provide information about performance
and resource consumption of running applications. The tool can be used when **diagnosing performance issues**, and in
particular **issues related to heap sizing and garbage collection**. The `jstat` utility does not require the VM to be
started with any special options. The built-in instrumentation in the Java HotSpot VM is enabled by default. This
utility is included in the JDK download for all operating system platforms supported by Oracle.

The instrumentation is not accessible on a FAT32 file system.
{: .notice}

The `jstat` utility uses the virtual machine identifier (VMID) to identify the target process. The documentation
describes the syntax of the VMID, but its only required component is the local virtual machine identifier (LVMID). The
LVMID is typically (but not always) the operating system's PID for the target JVM process.

The `jstat` tool provides data similar to the data provided by the tools `vmstat` and `iostat` on Oracle Solaris
and Linux operating systems.

For a graphical representation of the data, you can use the `visualgc` tool. See
[The visualgc Tool](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr018.html#BABCDEBE)

## The `jinfo` Utility

The `jinfo` command-line utility gets configuration information from a running Java process or crash dump and prints
the system properties or the command-line flags that were used to start the JVM.

The release of JDK 8 introduced
* [Java Mission Control](https://www.oracle.com/java/technologies/jdk-mission-control.html)
* [Java Flight Recorder](https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/about.htm#JFRUH170>)
* `jcmd` utility for diagnosing problems with JVM and Java applications. It is suggested to use the latest utility,
  `jcmd` instead of the previous `jinfo` utility for enhanced diagnostics and reduced performance overhead.
{: .notice}

The utility can also use the `jsadebugd` daemon to query a process or core file on a remote machine. Note: The output
takes longer to print in this case.

The output for jinfo utility for a Java Process with PID number 29620 as shown in example below

```bash
$ jinfo 29620
Attaching to process ID 29620, please wait...
Debugger attached successfully.
Client compiler detected.
JVM version is 1.6.0-rc-b100
Java System Properties:

java.runtime.name = Java(TM) SE Runtime Environment
sun.boot.library.path = /usr/jdk/instances/jdk1.6.0/jre/lib/sparc
java.vm.version = 1.6.0-rc-b100
java.vm.vendor = Sun Microsystems Inc.
java.vendor.url = http://java.sun.com/
path.separator = :
java.vm.name = Java HotSpot(TM) Client VM
file.encoding.pkg = sun.io
sun.java.launcher = SUN_STANDARD
sun.os.patch.level = unknown
java.vm.specification.name = Java Virtual Machine Specification
user.dir = /home/js159705
java.runtime.version = 1.6.0-rc-b100
java.awt.graphicsenv = sun.awt.X11GraphicsEnvironment
java.endorsed.dirs = /usr/jdk/instances/jdk1.6.0/jre/lib/endorsed
os.arch = sparc
java.io.tmpdir = /var/tmp/
line.separator =

java.vm.specification.vendor = Sun Microsystems Inc.
os.name = SunOS
sun.jnu.encoding = ISO646-US
java.library.path = /usr/jdk/instances/jdk1.6.0/jre/lib/sparc/client:/usr/jdk/instances/jdk1.6.0/jre/lib/sparc:
/usr/jdk/instances/jdk1.6.0/jre/../lib/sparc:/net/gtee.sfbay/usr/sge/sge6/lib/sol-sparc64:
/usr/jdk/packages/lib/sparc:/lib:/usr/lib
java.specification.name = Java Platform API Specification
java.class.version = 50.0
sun.management.compiler = HotSpot Client Compiler
os.version = 5.10
user.home = /home/js159705
user.timezone = US/Pacific
java.awt.printerjob = sun.print.PSPrinterJob
file.encoding = ISO646-US
java.specification.version = 1.6
java.class.path = /usr/jdk/jdk1.6.0/demo/jfc/Java2D/Java2Demo.jar
user.name = js159705
java.vm.specification.version = 1.0
java.home = /usr/jdk/instances/jdk1.6.0/jre
sun.arch.data.model = 32
user.language = en
java.specification.vendor = Sun Microsystems Inc.
java.vm.info = mixed mode, sharing
java.version = 1.6.0-rc
java.ext.dirs = /usr/jdk/instances/jdk1.6.0/jre/lib/ext:/usr/jdk/packages/lib/ext
sun.boot.class.path = /usr/jdk/instances/jdk1.6.0/jre/lib/resources.jar:
/usr/jdk/instances/jdk1.6.0/jre/lib/rt.jar:/usr/jdk/instances/jdk1.6.0/jre/lib/sunrsasign.jar:
/usr/jdk/instances/jdk1.6.0/jre/lib/jsse.jar:
/usr/jdk/instances/jdk1.6.0/jre/lib/jce.jar:/usr/jdk/instances/jdk1.6.0/jre/lib/charsets.jar:
/usr/jdk/instances/jdk1.6.0/jre/classes
java.vendor = Sun Microsystems Inc.
file.separator = /
java.vendor.url.bug = http://java.sun.com/cgi-bin/bugreport.cgi
sun.io.unicode.encoding = UnicodeBig
sun.cpu.endian = big
sun.cpu.isalist =
```

## The `jmap` Utility

The `jmap` command-line utility prints memory-related statistics for a running VM or core file.

The utility can also use the `jsadebugd` daemon to query a process or core file on a remote machine. Note: The output
takes longer to print in this case.

The release of JDK 8 introduced
* [Java Mission Control](https://www.oracle.com/java/technologies/jdk-mission-control.html)
* [Java Flight Recorder](https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/about.htm#JFRUH170>)
* `jcmd` utility for diagnosing problems with JVM and Java applications. It is suggested to use the latest utility,
  `jcmd` instead of the previous `jmap` utility for enhanced diagnostics and reduced performance overhead.
{: .notice}

If `jmap` is used with a process or core file without any command-line options, then it prints the list of shared
objects loaded (the output is similar to the `pmap` utility on Oracle Solaris operating system). For more specific
information, you can use the options `-heap`, `-histo`, or `-permstat`. These options are described in the subsections
that follow.

In addition, the JDK 7 release introduced the `-dump:format=b,file=filename option`, which causes `jmap` to dump the
Java heap in binary `HPROF` format to a specified file. This file can then be analyzed with the `jhat` tool.

If the `jmap` pid command does not respond because of a hung process, then the `-F` option can be used (on Oracle
Solaris and Linux operating systems only) to force the use of the Serviceability Agent.

### Heap Configuration and Usage

The `-heap` option is used to obtain the following Java heap information:

* Information specific to the garbage collection (GC) algorithm, including the name of the GC algorithm (for example,
  parallel GC) and algorithm-specific details (such as number of threads for parallel GC).

* Heap configuration that might have been specified as command-line options or selected by the VM based on the machine
  configuration.

* Heap usage summary: For each generation (area of the heap), the tool prints the total heap capacity, in-use memory,
  and available free memory. If a generation is organized as a collection of spaces (for example, the new generation),
  then a space specific memory size summary is included.

An example below shows output from the `jmap -heap` command.

```bash
$ jmap -heap 29620
Attaching to process ID 29620, please wait...
Debugger attached successfully.
Client compiler detected.
JVM version is 1.6.0-rc-b100

using thread-local object allocation.
Mark Sweep Compact GC

Heap Configuration:
   MinHeapFreeRatio = 40
   MaxHeapFreeRatio = 70
   MaxHeapSize      = 67108864 (64.0MB)
   NewSize          = 2228224 (2.125MB)
   MaxNewSize       = 4294901760 (4095.9375MB)
   OldSize          = 4194304 (4.0MB)
   NewRatio         = 8
   SurvivorRatio    = 8
   PermSize         = 12582912 (12.0MB)
   MaxPermSize      = 67108864 (64.0MB)

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 2031616 (1.9375MB)
   used     = 70984 (0.06769561767578125MB)
   free     = 1960632 (1.8698043823242188MB)
   3.4939673639112905% used
Eden Space:
   capacity = 1835008 (1.75MB)
   used     = 36152 (0.03447723388671875MB)
   free     = 1798856 (1.7155227661132812MB)
   1.9701276506696428% used
From Space:
   capacity = 196608 (0.1875MB)
   used     = 34832 (0.0332183837890625MB)
   free     = 161776 (0.1542816162109375MB)
   17.716471354166668% used
To Space:
   capacity = 196608 (0.1875MB)
   used     = 0 (0.0MB)
   free     = 196608 (0.1875MB)
   0.0% used
tenured generation:
   capacity = 15966208 (15.2265625MB)
   used     = 9577760 (9.134063720703125MB)
   free     = 6388448 (6.092498779296875MB)
   59.98769400974859% used
Perm Generation:
   capacity = 12582912 (12.0MB)
   used     = 1469408 (1.401336669921875MB)
   free     = 11113504 (10.598663330078125MB)
   11.677805582682291% used
```

### Heap Histogram

The `jmap` command with the `-histo` option can be used to obtain a class specific histogram of the heap. Depending
on the parameter specified, the `jmap -histo` command can print out the heap histogram for a running process or a core
file.

When the command is executed on a running process, the tool prints the number of objects, memory size in bytes, and
fully qualified class name for each class. Internal classes in the Java HotSpot VM are enclosed in angle brackets. The
histogram is useful in understanding how the heap is used. To get the size of an object, you must divide the total size
by the count of that object type.

The example below shows output from the `jmap -histo` command when it is executed on a process with PID number 29620.

```bash
$ jmap -histo 29620
num   #instances    #bytes  class name
--------------------------------------
  1:      1414     6013016  [I
  2:       793      482888  [B
  3:      2502      334928  <constMethodKlass>
  4:       280      274976  <instanceKlassKlass>
  5:       324      227152  [D
  6:      2502      200896  <methodKlass>
  7:      2094      187496  [C
  8:       280      172248  <constantPoolKlass>
  9:      3767      139000  [Ljava.lang.Object;
 10:       260      122416  <constantPoolCacheKlass>
 11:      3304      112864  <symbolKlass>
 12:       160       72960  java2d.Tools$3
 13:       192       61440  <objArrayKlassKlass>
 14:       219       55640  [F
 15:      2114       50736  java.lang.String
 16:      2079       49896  java.util.HashMap$Entry
 17:       528       48344  [S
 18:      1940       46560  java.util.Hashtable$Entry
 19:       481       46176  java.lang.Class
 20:        92       43424  javax.swing.plaf.metal.MetalScrollButton
... more lines removed here to reduce output...
1118:         1           8  java.util.Hashtable$EmptyIterator
1119:         1           8  sun.java2d.pipe.SolidTextRenderer
Total    61297    10152040
```

When the `jmap -histo` command is executed on a core file, the tool prints the size, count, and class name for each
class. Internal classes in the Java HotSpot VM are prefixed with an asterisk (*).

Here is an example that shows output of the `jmap -histo` command when it is executed on a core file.

```bash
$ jmap -histo /usr/bin/java core
Attaching to core core from executable /usr/bin/java, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 1.6.0-rc-b100
Iterating over heap. This may take a while...
Heap traversal took 8.902 seconds.

Object Histogram:

Size    Count    Class description
-------------------------------------------------------
4151816    2941    int[]
2997816    26403    * ConstMethodKlass
2118728    26403    * MethodKlass
1613184    39750    * SymbolKlass
1268896    2011    * ConstantPoolKlass
1097040    2011    * InstanceKlassKlass
882048    1906    * ConstantPoolCacheKlass
758424    7572    char[]
733776    2518    byte[]
252240    3260    short[]
214944    2239    java.lang.Class
177448    3341    * System ObjArray
176832    7368    java.lang.String
137792    3756    java.lang.Object[]
121744    74    long[]
72960    160    java2d.Tools$3
63680    199    * ObjArrayKlassKlass
53264    158    float[]
... more lines removed here to reduce output...
```

### Class Loader Statistics

Use the `jmap` command with the `-clstats` option to print class loader statistics for the Java heap.

The `jmap` command connects to a running process using the process ID and prints detailed information about classes
loaded in the Metaspace:

* **class_loader**: The address of the class loader object at the snapshot when the utility was run
* **classes**: The number of classes loaded
* **bytes**: The approximate number of bytes consumed by metadata for all classes loaded by this class loader
* **parent_loader**: The address of the parent class loader (if any)
* **alive?**: A live or dead indication of whether the loader object will be garbage collected in the future
* **type**: The class name of this class loader

The following example shows the output from the `jmap -clstats` command when it is executed on a process with PID
number 16624.

```bash
$ jmap -clstats 16624
Attaching to process ID 16624, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.211-b12
finding class loader instances ..done.
computing per loader stat ..done.
please wait.. computing liveness.liveness analysis may be inaccurate ...
class_loader    classes bytes   parent_loader   alive?  type

<bootstrap>     982     1845411   null          live    <internal>
0x000000076f5081e0      1948    3133806   null          dead    sun/misc/Launcher$ExtClassLoader@0x00000007c000fc80
0x000000076e13d3d0      1       1481      null          dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x000000076ca9e100      0       0       0x000000076f510178      dead    java/util/ResourceBundle$RBClassLoader@0x00000007c01ade48
0x000000076f510178      38      94160   0x000000076f5081e0      dead    sun/misc/Launcher$AppClassLoader@0x00000007c000f8d8

total = 5       2969    5074858     N/A         alive=1, dead=4     N/A
```

## The `jhat` Utility

The jhat tool provides a convenient means to **browse the object topology** in a heap snapshot. This tool replaces the
Heap Analysis Tool (HAT).

The tool parses a heap dump in binary format (for example, a heap dump produced by `jmap -dump`).

This utility can help **debug unintentional object retention**. This term is used to describe an object that is no
longer needed but is kept alive due to references through some path from the rootset. This can happen, for example, if
an unintentional static reference to an object remains after the object is no longer needed, if an Observer or Listener
fails to unregister itself from its subject when it is no longer needed, or if a Thread that refers to an object does
not terminate when it should. Unintentional object retention is the Java language equivalent of a memory leak.

The tool provides a number of standard queries. For example, the Roots query displays all reference paths from the
rootset to a specified object and is particularly useful for finding unnecessary object retention.

In addition to the standard queries, you can develop your own custom queries with the Object Query Language (OQL)
interface.

When you issue the jhat command, the utility starts an HTTP server on a specified TCP port. You can then use any browser
to connect to the server and execute queries on the specified heap dump.

An example below shows how to execute `jhat` to analyze a heap dump file named `snapshot.hprof`:

```bash
$ jhat snapshot.hprof
Started HTTP server on port 7000
Reading from java_pid2278.hprof...
Dump file created Fri May 19 17:18:38 BST 2006
Snapshot read, resolving...
Resolving 6162194 objects...
Chasing references, expect 12324 dots................................
Eliminating duplicate references.....................................
Snapshot resolved.
Server is ready.
```

At this point, `jhat` has started an HTTP server on port 7000. Point your browser to http://localhost:7000 to
connect to the `jhat` server.

When you are connected to the server, you can execute a standard query

### Standard Queries

#### All Classes Query

The default page is the All Classes query, which displays all of the classes present in the heap, excluding platform
classes. This list is sorted by fully qualified class name, and broken out by package. Click the name of a class to go
to the Class query.

The second variant of this query includes the platform classes. Platform classes include classes whose fully qualified
names start with prefixes such as `java`, `sun.`, `javax.swing.`, or `char[.` The list of prefixes is in a system
resource file called `/resources/platform_names.txt`. You can override this list by replacing it in the JAR file, or by
arranging for your replacement to occur first on the classpath when `jhat` is invoked.

#### Class Query

The Class query displays information about a class. This includes its superclass, any subclasses, instance data members,
and static data members. From this page you can navigate to any of the classes that are referenced, or you can navigate
to an Instances query.

#### Object Query

The Object query provides information about an object that was on the heap. From here, you can navigate to the class of
the object and to the value of any object members. You can also navigate to objects that refer to the current object.
Perhaps the most valuable query is at the end: the Roots query reference chains from the rootset.

The object query also provides a stack backtrace of the point of allocation of the object.
{: .notice}

#### Instances Query

The Instances query displays all instances of a given class. The `allInstances` variant includes instances of
subclasses of the given class as well. From here, you can navigate back to the source class, or you can navigate to an
Object query on one of the instances.

#### Roots Query

The Roots query displays reference chains from the rootset to a given object. It provides one chain for each member of
the rootset from which the given object is reachable. When calculating these chains, the tool does a depth-first search,
so that it will provide reference chains of minimal length.

There are two kinds of the Roots query: one that excludes weak references (Roots), and one that includes them
(All Roots). A weak reference is a reference object that does not prevent its referent from being made finalizable,
finalized, and then reclaimed. If an object is only referred to by a weak reference, then it is usually not considered
to be retained, because the garbage collector can collect it as soon as it needs the space.

This is probably the most valuable query in `jhat` for debugging unintentional object retention. When you find an object
that is being retained, this query tells you **why** it is being retained.
{: .notice}

#### Reachable Objects Query

This query is accessible from the Object query and shows the transitive closure of all objects reachable from a given
object. This list is sorted in decreasing size, and alphabetically within each size. At the end, the total size of all
of the reachable objects is given. This can be useful for determining the total runtime footprint of an object in
memory, at least in systems with simple object topologies.

This query is most valuable when used in conjunction with the `-exclude` command-line option. This is useful, for
example, if the object being analyzed is an Observable. By default, all of its Observers would be reachable, which would
count against the total size. The `-exclude` option allows you to exclude the data members
`java.util.Observable.obs` and `java.util.Observable.arr`.

#### Instance Counts for All Classes Query

This query shows the count of instances for every class in the system, excluding platform classes. It is sorted in
descending order, by instance count. A good way to spot a problem with unintentional object retention is to run a
program for a long time with a variety of input, and then request a heap dump. Looking at the instance counts for all
classes, you may recognize a number of classes because there are more instances than you expect. Then you can analyze
them to determine why they are being retained (possibly using the Roots query). A variant of this query includes
platform classes.

For more information about platform classes, see [All Classes Query](#all-classes-query).

#### All Roots Query

This query shows all members of the rootset, including weak references.

For more information about weak references, see [Root Query](#roots-query).

#### New Instances Query

The New Instances query is available only if you invoke the `jhat` server with two heap dumps. This query is similar to
the Instances query, except that it shows only new instances. An instance is considered new if it is in the second heap
dump and there is no object of the same type with the same ID in the baseline heap dump. An object's ID is a 32-bit or
64-bit integer that uniquely identifies the object.

#### Histogram Queries

The built-in histogram and finalizer histogram queries also provide useful information.

### Custom Queries

You can develop your own custom queries with the built-in Object Query Language (OQL) interface. Click the
**Execute OQL Query** button on the first page to display the **OQL query page**, where you can create and execute your
custom queries. The OQL Help facility describes the built-in functions, as shown in example below

```
select JavaScript-expression-to-select [from [instanceof] classname identifier [where JavaScript-boolean-expression-to-filter]]

select s from java.lang.String s where s.count >= 100
```

### Heap Analysis Hints

To get useful information from `jhat` often requires some knowledge of the application and the libraries and APIs that
it uses. You can use `jhat` to answer two important questions:

#### What is Keeping an Object Alive?

When you view an object instance, you can check the objects listed in the section entitled "References to this object"
to see which objects directly reference this object. More importantly, you can use the Roots query to determine the
reference chains from the root set to the given object. These reference chains show a path from a root object to this
object. With these chains, you can quickly see how an object is reachable from the root set.

As noted earlier, the Roots query excludes weak references, whereas All Roots query includes them. A weak reference is a
reference object that does not prevent its referent from being made finalizable, finalized, and then reclaimed. If an
object is only referred to by a weak reference, then the garbage collector can collect it as soon as it needs the space.

The `jhat` tool sorts the rootset reference chains by the type of the root, in the following order:

* Static data members of Java classes
* Java local variables. For these roots, the thread responsible for them is shown. Because a thread is a Java object,
  this link is clickable. This allows you, for example, to easily navigate to the name of the thread.
* Native static values.
* Native local variables. Again, such roots are identified by their thread.

#### Where Was This Object Allocated?

When an object instance is being displayed, the section entitled "Objects allocated from" shows the allocation site in
the form of a stack trace. In this way, you can see where the object was created.

This allocation site information is available only if the heap dump was created with HPROF using the `heap=all`
option. This HPROF option includes both the `heap=dump` option and the `heap=sites` option. For more information
about HPROF and its options, see
[HPROF](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr008.html#BABGIIGB).
{: .notice}

If the leak cannot be identified using a single object dump, then another approach is to collect a series of dumps and
to focus on the objects created in the interval between each dump. The `jhat` tool provides this capability using the
`-baseline` option.

The `-baseline` option allows two dumps to be compared if they were produced by HPROF and from the same VM instance.
If the same object appears in both dumps, then it will be excluded from the list of new objects reported. One dump is
specified as a baseline, and the analysis can focus on the objects that are created in the second dump since the
baseline was obtained.

This example shows how to specify the baseline.

```bash
$ jhat -baseline snapshot.hprof#1 snapshot.hprof#2
```

When `jhat` is started with two heap dumps, the Instance Counts for All Classes query includes an additional column
that is the count of the number of new objects for that type. An instance is considered new if it is in the second heap
dump and there is no object of the same type with the same ID in the baseline. If you click a new count, then `jhat`
lists the new objects of that type. Then for each instance, you can view where it was allocated, which objects these new
objects reference, and which other objects reference the new object.

In general, the `-baseline` option can be very useful if the objects that need to be identified are created in the
interval between successive dumps.

## The `jstack` Utility

The `jstack` command-line utility attaches to the specified process or core file and **prints the stack traces** of
all threads that are attached to the virtual machine, including Java threads and VM internal threads, and optionally
native stack frames. The utility also **performs deadlock detection**.

The release of JDK 8 introduced
* [Java Mission Control](https://www.oracle.com/java/technologies/jdk-mission-control.html)
* [Java Flight Recorder](https://docs.oracle.com/javacomponents/jmc-5-4/jfr-runtime-guide/about.htm#JFRUH170>)
* `jcmd` utility for diagnosing problems with JVM and Java applications. It is suggested to use the latest utility,
  `jcmd` instead of the previous `jstack` utility for enhanced diagnostics and reduced performance overhead.
{: .notice}

The utility can also use the `jsadebugd` daemon to query a process or core file on a remote machine. Note: The output
takes longer to print in this case.

A stack trace of all threads can be useful in diagnosing a number of issues, such as deadlocks or hangs.

The `-l` option, which instructs the utility to look for ownable synchronizers in the heap and print information about
`java.util.concurrent.locks`. Without this option, the thread dump includes information only on monitors.

### Force a Stack Dump

If the `jstack` pid command does not respond because of a hung process, then the `-F` option can be used (on Oracle
Solaris and Linux operating systems only) to force a stack dump, as shown below

```bash
$ jstack -F 8321
Attaching to process ID 8321, please wait...
Debugger attached successfully.
Client compiler detected.
JVM version is 1.6.0-rc-b100
Deadlock Detection:

Found one Java-level deadlock:
==============================

"Thread2":
  waiting to lock Monitor@0x000af398 (Object@0xf819aa10, a java/lang/String),
  which is held by "Thread1"
"Thread1":
  waiting to lock Monitor@0x000af400 (Object@0xf819aa48, a java/lang/String),
  which is held by "Thread2"

Found a total of 1 deadlock.

Thread t@2: (state = BLOCKED)

Thread t@11: (state = BLOCKED)
 - Deadlock$DeadlockMakerThread.run() @bci=108, line=32 (Interpreted frame)

Thread t@10: (state = BLOCKED)
 - Deadlock$DeadlockMakerThread.run() @bci=108, line=32 (Interpreted frame)

Thread t@6: (state = BLOCKED)

Thread t@5: (state = BLOCKED)
 - java.lang.Object.wait(long) @bci=-1107318896 (Interpreted frame)
 - java.lang.Object.wait(long) @bci=0 (Interpreted frame)
 - java.lang.ref.ReferenceQueue.remove(long) @bci=44, line=116 (Interpreted frame)
 - java.lang.ref.ReferenceQueue.remove() @bci=2, line=132 (Interpreted frame)
 - java.lang.ref.Finalizer$FinalizerThread.run() @bci=3, line=159 (Interpreted frame)

Thread t@4: (state = BLOCKED)
 - java.lang.Object.wait(long) @bci=0 (Interpreted frame)
 - java.lang.Object.wait(long) @bci=0 (Interpreted frame)
 - java.lang.Object.wait() @bci=2, line=485 (Interpreted frame)
 - java.lang.ref.Reference$ReferenceHandler.run() @bci=46, line=116 (Interpreted frame)
```

### Stack Trace from a Core Dump

To obtain stack traces from a core dump, execute the `jstack` command on a core file

```bash
$ jstack $JAVA_HOME/bin/java core
```

### Mixed Stack

The `jstack` utility can also be used to print a mixed stack; that is, it can print native stack frames in addition to
the Java stack. Native frames are the C/C++ frames associated with VM code and JNI/native code.

To print a mixed stack, use the -m option:

```bash
$ jstack -m 21177
Attaching to process ID 21177, please wait...
Debugger attached successfully.
Client compiler detected.
JVM version is 1.6.0-rc-b100
Deadlock Detection:

Found one Java-level deadlock:
=============================

"Thread1":
  waiting to lock Monitor@0x0005c750 (Object@0xd4405938, a java/lang/String),
  which is held by "Thread2"
"Thread2":
  waiting to lock Monitor@0x0005c6e8 (Object@0xd4405900, a java/lang/String),
  which is held by "Thread1"

Found a total of 1 deadlock.

----------------- t@1 -----------------
0xff2c0fbc    __lwp_wait + 0x4
0xff2bc9bc    _thrp_join + 0x34
0xff2bcb28    thr_join + 0x10
0x00018a04    ContinueInNewThread + 0x30
0x00012480    main + 0xeb0
0x000111a0    _start + 0x108
----------------- t@2 -----------------
0xff2c1070    ___lwp_cond_wait + 0x4
0xfec03638    bool Monitor::wait(bool,long) + 0x420
0xfec9e2c8    bool Threads::destroy_vm() + 0xa4
0xfe93ad5c    jni_DestroyJavaVM + 0x1bc
0x00013ac0    JavaMain + 0x1600
0xff2bfd9c    _lwp_start
----------------- t@3 -----------------
0xff2c1070    ___lwp_cond_wait + 0x4
0xff2ac104    _lwp_cond_timedwait + 0x1c
0xfec034f4    bool Monitor::wait(bool,long) + 0x2dc
0xfece60bc    void VMThread::loop() + 0x1b8
0xfe8b66a4    void VMThread::run() + 0x98
0xfec139f4    java_start + 0x118
0xff2bfd9c    _lwp_start
----------------- t@4 -----------------
0xff2c1070    ___lwp_cond_wait + 0x4
0xfec195e8    void os::PlatformEvent::park() + 0xf0
0xfec88464    void ObjectMonitor::wait(long long,bool,Thread*) + 0x548
0xfe8cb974    void ObjectSynchronizer::wait(Handle,long long,Thread*) + 0x148
0xfe8cb508    JVM_MonitorWait + 0x29c
0xfc40e548    * java.lang.Object.wait(long) bci:0 (Interpreted frame)
0xfc40e4f4    * java.lang.Object.wait(long) bci:0 (Interpreted frame)
0xfc405a10    * java.lang.Object.wait() bci:2 line:485 (Interpreted frame)
... more lines removed here to reduce output...
----------------- t@12 -----------------
0xff2bfe3c    __lwp_park + 0x10
0xfe9925e4    AttachOperation*AttachListener::dequeue() + 0x148
0xfe99115c    void attach_listener_thread_entry(JavaThread*,Thread*) + 0x1fc
0xfec99ad8    void JavaThread::thread_main_inner() + 0x48
0xfec139f4    java_start + 0x118
0xff2bfd9c    _lwp_start
----------------- t@13 -----------------
0xff2c1500    _door_return + 0xc
----------------- t@14 -----------------
0xff2c1500    _door_return + 0xc
```

Frames that are prefixed with an asterisk (*) are Java frames, whereas frames that are not prefixed with an asterisk are
native C/C++ frames.

The output of the utility can be piped through `c++filt` to demangle C++ mangled symbol names. Because the Java
HotSpot VM is developed in the C++ language, the `jstack` utility prints C++ mangled symbol names for the Java HotSpot
internal functions.

The `c++filt` utility is delivered with the native C++ compiler suite: `SUNWspro` on Oracle Solaris operating system
and gnu on Linux.
