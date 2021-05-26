---
layout: post
title: Programming HBase
tags: [HBase, Hadoop]
color: rgb(250, 154, 133)
feature-img: "assets/img/post-cover/29-cover.png"
thumbnail: "assets/img/post-cover/29-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## Overview

HBase allows the four primary data model operations

1. Get
2. Put
3. Scan
4. Delete

These operations are applied on [HTable](http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/client/HTable.html)
instances.

* `Get` - Extracts certain cells from a given row.
* `Put` - Adds data to a table.
* `Scan` - Search for data based on parameters and filters.
* `Delete` -  Remove data from a table.

## Setting Up

### Installation

* Large organization usually uses a heavily customized internal version of HBase. The organization should have an
  internal version of HBase and ask teams not to download and use Apache HBase jars from open source, as the client may
  be incompatible.
* Instead, use packages from the org's internal dist. The organization should maintain a list of hbase versions
  available for download as well as a list of corresponding HBase config packages
* Both packages are needed. The former package provides code (e.g. jars) and the latter package provides most of the
  configs (eg hbase-site.xml with the ZooKeeper quorum location)
* Since each cluster can only have 1 version of HBase, organization should ensure the right version is used by client
  targeting cluster in a specific colo.
* For example, the org should have a UI that shows which version is installed in which cluster so that client teams
  know which HBase version to use in their codebase.

### Maven POM

* Once you reach the stage where you want to create a full Java project you will probably want to use Maven or another
  build system to automatically download jars and compile your project
* If you use Maven, your POM file would have something like this:

```xml
<dependency>
    <groupId>org.apache.hbase</groupId>
    <artifactId>hbase-client</artifactId>
    <version>${hbase.version}</version>
    <scope>provided</scope>
</dependency>
```

* It is important that the organization should forward the Maven lookup for `org.apache.hbase` `hbase-client` to
  org's internal artifactory and fetch the correct internal jars, instead of the external open source Maven repositories
* You will want to make sure you are downloading a fairly recent `${hbase.version}`.

## Using HBase Shell

### Starting HBase

1. From your HBase package, start HBase in standalone mode: `$ bin/start-hbase.sh`
2. Confirm that HBase is running by navigating to http://localhost:60010/master-status.
3. Start the HBase shell: `$ bin/hbase shell`

### Creating Tables

With the `hbase` shell open, run the following commands.

1. Create the table `test` with the column family name `cf`

       hbase(main):001:0> create 'test', 'cf'

2. Confirm that the table was created:

       hbase(main):002:0>  list 'test'

3. Get more information about the table: 

       hbase(main):003:0> describe 'test'

### Writing to Tables

We're just going to write some data to the columns that we created::

    hbase(main):004:0> put 'test', 'row1', 'cf:c1', 'value1'
    hbase(main):005:0> put 'test', 'row2', 'cf:c2', 'value2'

### Reading Data

We'll take a look at how to get a row of data and view all the rows in the table.

1. First, let's get a description of our table: 

       hbase(main):006:0> describe 'test'
   
2. Get the first row of your table: 

       hbase(main):007:0> get 'test', 'row1'
   
3. Display all the rows in your table: 

       hbase(main):008:0> scan 'test'

### Disable/Drop Tables

    hbase(main):009:0> disable 'test'
    hbase(main):010:0> drop 'test'

## Using Java With HBase

### Setting Up

1. Add the following entry to `/etc/hosts` 

       127.0.0.1 localhost

2. Start HBase with the HBase shell command: 

       $ bin/start-hbase.sh

3. Verify that HBase is running by opening the following URL in a browser:

       http://localhost:60010/master-status

4. After you see that the `ROOT`, `META` and `hbase:namespace` tables have been assigned (in a clean install that means
   `numberOfOnlineRegions=3`, as each table would be one region), you are ready to run the following examples.

5. With the HBase shell command, create the table 'test' with the column family 'cf1' and add some row data. We'll be
   using this table in our scripts.

       hbase(main):001:0> create 'test-table', 'cf'
       hbase(main):002:0> put 'test-table', 'row1', 'cf:c1', 'value1'
       
6. Exit the shell: 

       hbase(main):001:0> exit

### Simple Example

In this example, we're just going to scan the table we created earlier.

1. Create the file `HBaseSimpleEx.java` with the following code. This program simply scans the table `'test-table'`
   that we created.

```java
import org.apache.hadoop.hbase....;
import org.apache.hadoop.hbase.client....;
import java.io.IOException;

//Scans a table called 'test-table'
public class HBaseSimpleEx {

    public static void main(String args[]) throws IOException {
        HTable table = new HTable(HBaseConfiguration.create(), "test-table");
        ResultScanner scanner = table.getScanner(new Scan());
        for(Result res : scanner) {
            System.out.println("-->"+res);
        } 
    }   
}
```

2. Compile the script, making sure that `path-to/bin/hbase` is referencing the same HBase that you used to start the
   server.

       $ javac -cp `path-to/bin/hbase classpath` HBaseSimpleEx.java

3. Run the compiled program: `$ java -cp `path-to/bin/hbase classpath` HBaseSimpleEx`
4. In the output from the command, you should see a line similar to the one below::

       -->keyvalues={row1/cf:c1/1390610946158/Put/vlen=6/mvcc=0}

### Advanced Example

This example creates a new table, instead of getting the configuration for an existing table, with two family columns,
adds records, gets a rowkey, scans the table, and then finally deletes the table.

1. Create the file `HBaseAdvEx.java` with the following code:

```java
/*
 * javac -cp `path-to/hbase classpath` HBaseAdvEx.java
 * java -cp `path-to/hbase classpath` HBaseAdvEx 
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseAdvEx {

    private static Configuration conf = null;

    /**
    * Initialization
    */
    static {
        conf = HBaseConfiguration.create();
    }

    /**
    * Create a table
    */
    public static void createTable(String tableName, String[] families) throws Exception {
        HBaseAdmin admin = new HBaseAdmin(conf);
        if (admin.tableExists(tableName)) {
            System.out.println("table already exists!");
        } else {
            HTableDescriptor tableDesc = new HTableDescriptor(tableName);
            for (int i = 0; i < families.length; i++) {
                tableDesc.addFamily(new HColumnDescriptor(families[i]));
            }
            admin.createTable(tableDesc);
            System.out.println("create table " + tableName + " ok.");
        }
    }

    /**
    * Delete a table
    */
    public static void deleteTable(String tableName) throws Exception {
        try {
            HBaseAdmin admin = new HBaseAdmin(conf);
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
            System.out.println("delete table " + tableName + " ok.");
        } catch (MasterNotRunningException e) {
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        }
    }

    /**
    * Put (or insert) a row
    */
    public static void addRecord(String tableName, String rowKey, String family, String qualifier, String value) throws Exception {
        try {
            HTable table = new HTable(conf, tableName);
            Put put = new Put(Bytes.toBytes(rowKey));
            put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
            table.put(put);
            System.out.println("insert record " + rowKey + " to table " + tableName + " ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * Delete a row
    */
    public static void delRecord(String tableName, String rowKey) throws IOException {
        HTable table = new HTable(conf, tableName);
        List<Delete> list = new ArrayList<Delete>();
        Delete del = new Delete(rowKey.getBytes());
        list.add(del);
        table.delete(list);
        System.out.println("del record " + rowKey + " ok.");
    }

    /**
    * Get a row
    */
    public static void getOneRecord (String tableName, String rowKey) throws IOException {
        HTable table = new HTable(conf, tableName);
        Get get = new Get(rowKey.getBytes());
        Result rs = table.get(get);
        for(KeyValue kv : rs.raw()){
            System.out.print(new String(kv.getRow()) + " " );
            System.out.print(new String(kv.getFamily()) + ":" );
            System.out.print(new String(kv.getQualifier()) + " " );
            System.out.print(kv.getTimestamp() + " " );
            System.out.println(new String(kv.getValue()));
        }
    }

    /**
    * Scan (or list) a table
    */
    public static void getAllRecord (String tableName) {
        try{
            HTable table = new HTable(conf, tableName);
            Scan s = new Scan();
            ResultScanner ss = table.getScanner(s);
            for(Result r:ss){
                for(KeyValue kv : r.raw()){
                    System.out.print(new String(kv.getRow()) + " ");
                    System.out.print(new String(kv.getFamily()) + ":");
                    System.out.print(new String(kv.getQualifier()) + " ");
                    System.out.print(kv.getTimestamp() + " ");
                    System.out.println(new String(kv.getValue()));
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] agrs) {
        try {
            String tablename = "scores";
            String[] families = { "grade", "course" };
            HBaseAdvEx.createTable(tablename, families);

            // Add record zkb
            HBaseAdvEx.addRecord(tablename, "zkb", "grade", "", "5");
            HBaseAdvEx.addRecord(tablename, "zkb", "course", "", "90");
            HBaseAdvEx.addRecord(tablename, "zkb", "course", "math", "97");
            HBaseAdvEx.addRecord(tablename, "zkb", "course", "art", "87");

            // Add record baoniu
            HBaseAdvEx.addRecord(tablename, "baoniu", "grade", "", "4");
            HBaseAdvEx.addRecord(tablename, "baoniu", "course", "math", "89");

            System.out.println("===========get one record========");
            HBaseAdvEx.getOneRecord(tablename, "zkb");

            System.out.println("===========show all record========");
            HBaseAdvEx.getAllRecord(tablename);

            System.out.println("===========del one record========");
            HBaseAdvEx.delRecord(tablename, "baoniu");
            HBaseAdvEx.getAllRecord(tablename);

            System.out.println("===========show all record========");
            HBaseAdvEx.getAllRecord(tablename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

2. Compile the script, making sure that `path-to/bin/hbase` is referencing the same HBase that you used to start the
   server.

       $ javac -cp `path-to/bin/hbase classpath` HBaseAdvEx.java
       
3. Run the compiled program: `$ java -cp `path-to/bin/hbase classpath` HBaseAdvEx`
4. Again, in the output from the command, you should see the inserted and fetched records as shown below::

       insert record zkb to table scores ok.
		 insert record zkb to table scores ok.
		 insert record zkb to table scores ok.
		 insert record zkb to table scores ok.
		 insert record baoniu to table scores ok.
		 insert record baoniu to table scores ok.
		 ===========get one record========
		 zkb course: 1390612871126 90
		 zkb course:art 1390612871134 87
		 zkb course:math 1390612871130 97
		 zkb grade: 1390612871117 5
		 ===========show all record========
		 baoniu course:math 1390612871138 89
		 baoniu grade: 1390612871136 4
		 zkb course: 1390612871126 90
		 zkb course:art 1390612871134 87
		 zkb course:math 1390612871130 97
		 zkb grade: 1390612871117 5
		 ===========del one record========
		 del record baoniu ok.
		 zkb course: 1390612871126 90
		 zkb course:art 1390612871134 87
		 zkb course:math 1390612871130 97
		 zkb grade: 1390612871117 5
		 ===========show all record========
		 zkb course: 1390612871126 90
		 zkb course:art 1390612871134 87
		 zkb course:math 1390612871130 97
		 zkb grade: 1390612871117 5

## Map/Reduce Operations 

### Intro

In this section, we'll run through a tutorial that shows you how to run a map/reduce job on data that is similar to a
Web log. Basically, we'll take data stored in HBase tables that contains a set of users and what Web pages they visited.
We'll use map and reduce to count the number of times users viewed each page.

To do this, we'll the create two tables from the HBase shell to store our data. Then we'll create mocked data generated
randomly with a Java program, and finally, run another Java program to run a map and then a reduce function over the
data.

### Setting Up

From the HBase shell, create the tables that your Java programs will be using:

    hbase> create 'access_logs', 'details'
    hbase> create 'summary_user', {NAME=>'details', VERSIONS=>1}

The `'access_logs'` is the table that will contain the 'raw' logs and will serve as the input source for the map and
the reduce. The `'summary_user'` table is where we will write out the final results.


### Adding Data to Tables

With the program below, we're going to generate 10000 random results for four Web pages and then save them to our
`access_logs` table. Create the file `Importer`:

```java
import java.util.Random;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class Importer {

    public static void main(String[] args) throws Exception {
				
        String [] pages = {"/", "/a.html", "/b.html", "/c.html"};
        HBaseConfiguration hbaseConfig = new HBaseConfiguration();
        HTable htable = new HTable(hbaseConfig, "access_logs");
        htable.setAutoFlush(false);
        htable.setWriteBufferSize(1024 * 1024 * 12);
				
        int totalRecords = 100000;
        int maxID = totalRecords / 1000;
        Random rand = new Random();
        System.out.println("importing " + totalRecords + " records ....");
        for (int i=0; i < totalRecords; i++) {
            int userID = rand.nextInt(maxID) + 1;
            byte [] rowkey = Bytes.add(Bytes.toBytes(userID), Bytes.toBytes(i));
            String randomPage = pages[rand.nextInt(pages.length)];
            Put put = new Put(rowkey);
            put.add(Bytes.toBytes("details"), Bytes.toBytes("page"), Bytes.toBytes(randomPage));
            htable.put(put);
        }
        htable.flushCommits();
        htable.close();
        System.out.println("done");
    }
}
```

Compile the program: `$ javac -cp `path-to/hbase classpath` Importer.java`. Then Run the program to populate our tables:
`$ java -cp `path-to/hbase classpath` Importer`

### Map and Reduce 

1. Before you run the map/reduce job on our data, confirm that the data has been saved to the tables you created. From
   the HBase shell, run a scan on the `access_logs` table:

       hbase> scan 'access_logs'

2. You should see a long list of records. Feel free to press *Ctrl-C** at any time to stop the scan job.

3. Create the file `FreqCounter.java` with the code below.

        ```java
        import org.apache.hadoop.hbase.client.Put;
        import org.apache.hadoop.hbase.client.Result;
        import org.apache.hadoop.hbase.client.Scan;
        import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
        import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
        import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
        import org.apache.hadoop.hbase.mapreduce.TableMapper;
        import org.apache.hadoop.hbase.mapreduce.TableReducer;
        import org.apache.hadoop.hbase.util.Bytes;
        import org.apache.hadoop.io.IntWritable;
        import org.apache.hadoop.mapreduce.Job;
        
        public class FreqCounter {
        
            static class Mapper extends TableMapper<ImmutableBytesWritable, IntWritable> {
        
                private int numRecords = 0;
                private static final IntWritable one = new IntWritable(1);
        
                @Override
                public void map(ImmutableBytesWritable row, Result values, Context context) throws IOException {
                    // Extract userKey from the compositeKey (userId + counter)
                    ImmutableBytesWritable userKey = new ImmutableBytesWritable(row.get(), 0, Bytes.SIZEOF_INT);
                    try {
                        context.write(userKey, one);
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                    numRecords++;
                    if ((numRecords % 10000) == 0) {
                        context.setStatus("mapper processed " + numRecords + " records so far");
                    }
                }
            }
            public static class Reducer extends TableReducer<ImmutableBytesWritable, IntWritable, ImmutableBytesWritable> {
        
                public void reduce(ImmutableBytesWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
                          int sum = 0;
                    for (IntWritable val : values) {
                        sum += val.get();
                    }
                    Put put = new Put(key.get());
                    put.add(Bytes.toBytes("details"), Bytes.toBytes("total"), Bytes.toBytes(sum));
                    System.out.println(String.format("stats :   key : %d,  count : %d", Bytes.toInt(key.get()), sum));
                    context.write(key, put);
                }
                 }
            public static void main(String[] args) throws Exception {
                HBaseConfiguration conf = new HBaseConfiguration();
                Job job = new Job(conf, "HBase_FreqCounter");
                job.setJarByClass(FreqCounter.class);
                Scan scan = new Scan();
                String columns = "details"; // comma seperated
                scan.addFamily(Bytes.toBytes(columns));
                scan.setFilter(new FirstKeyOnlyFilter());
                TableMapReduceUtil.initTableMapperJob("access_logs", scan, Mapper.class, ImmutableBytesWritable.class, IntWritable.class, job);
                TableMapReduceUtil.initTableReducerJob("summary_user", Reducer.class, job);
                System.exit(job.waitForCompletion(true) ? 0 : 1);
            }
        }
        ```
        
4. Compile the program: `$ javac -cp `path-to/hbase classpath` FreqCounter.java`
5. Run the program to run the map/reduce jobs and populate the table `summary_user`: 

      $ java -cp `path-to/hbase classpath` FreqCounter

#### Code Explanation

We're just going to give a short overview of the code we just used to run a map and reduce our data. HBase provides the
Mapper and Reduce classes `TableMapper` and `TableReduce`, which extend the Mapper and Reducer interfaces, to make
it easier to read and write from and to HBase tables. We extend these built-in classes from our custom classes
`Mapper` and `Reducer`.

Our `map` function iterates over the data, extracting the user ID from each row, and then writing the value `1` for
each user ID. After the `map` function has finished running, the `access_logs` table has 10000 rows of user IDs as
keys with the value of `1`.

In simplified terms, the table below shows the input to and the output from the `map` function:

| Input (`access_table`)      | Output       |
|-----------------------------|--------------|
| userID + timestamp (rowkey) | `(user1, 1)` |
| userID + timestamp (rowkey) | `(user2, 1)` |
| userID + timestamp (rowkey) | `(user1, 1)` |
| userID + timestamp (rowkey) | `(user3, 1)` |

The output becomes the input for the `reduce` function, which creates a list of the values for each user ID, and then
totals the values. Finally, the `reduce` function writes the user ID and its value (the total number of times seen) to
the `summary_user` table:

| Input (output from `map`) | Output       |
|---------------------------|--------------|
| `(user1, [1, 1])`         | `(user1, 2)` |
| `(user2, [1])`            | `(user2, 1)` |
| `(user3, [1])`            | `(user3, 1)` |

### Displaying Results

The last part of our exercise is to simply scan the table `summary_user` and display the results.

1. Create the file `PrintUserCount.java` with the following:

        ```java
        import org.apache.hadoop.hbase.HBaseConfiguration;
        import org.apache.hadoop.hbase.client.HTable;
        import org.apache.hadoop.hbase.client.Result;
        import org.apache.hadoop.hbase.client.ResultScanner;
        import org.apache.hadoop.hbase.client.Scan;
        import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
        import org.apache.hadoop.hbase.util.Bytes;
        
        public class PrintUserCount {
        
            public static void main(String[] args) throws Exception {
        
                HBaseConfiguration conf = new HBaseConfiguration();
                HTable htable = new HTable(conf, "summary_user");
        
                Scan scan = new Scan();
                ResultScanner scanner = htable.getScanner(scan);
                Result r;
                while (((r = scanner.next()) != null)) {
                    ImmutableBytesWritable b = r.getBytes();
                    byte[] key = r.getRow();
                    int userId = Bytes.toInt(key);
                    byte[] totalValue = r.getValue(Bytes.toBytes("details"), Bytes.toBytes("total"));
                    int count = Bytes.toInt(totalValue);
        
                    System.out.println("key: " + userId+ ",  count: " + count);
                }
                scanner.close();
                htable.close();
            }
        }
        ```

2. Compile the program: `$ javac -cp `path-to/hbase classpath` PrintUserCount.java`
3. Run the program to display the the data stored in the `summary_user` table: 

      $ java -cp `path-to/hbase classpath` PrintUserCount


### Additional Code Examples

The following sections are more advanced and aim to show specific use cases, so the setting up section and steps will be
omitted.
 

#### Writing to HDFS

This very similar to the example above, with exception that this is writing to HDFS and not another HBase table. We do
this through the `FileOutputFormat` class.

```java
Configuration config = HBaseConfiguration.create();
Job job = new Job(config,"ExampleSummaryToFile");
job.setJarByClass(MySummaryFileJob.class);     // class that contains mapper and reducer

Scan scan = new Scan();
scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
scan.setCacheBlocks(false);  // don't set to true for MR jobs
// Set other scan attrs

TableMapReduceUtil.initTableMapperJob(
    sourceTable,        // input table
    scan,               // Scan instance to control CF and attribute selection
    MyMapper.class,     // mapper class
    Text.class,         // mapper output key
    IntWritable.class,  // mapper output value
    job);
job.setReducerClass(MyReducer.class);    // reducer class
job.setNumReduceTasks(1);    // at least one, adjust as required
FileOutputFormat.setOutputPath(job, new Path("/tmp/mr/mySummaryFile"));  // adjust directories as required

boolean b = job.waitForCompletion(true);
if (!b) {
    throw new IOException("error with job!");
}
```  

#### Writing MapReduce Data to RDBMS

Sometimes it is more appropriate to generate summaries to an RDBMS. For these cases, you can generate summaries directly
to an RDBMS with a custom reducer. A `setup` method can connect to an RDBMS (the connection information can be passed
via custom parameters in the context), and the cleanup method can close the connection.

It is critical to understand that number of reducers for a job affects the way the summarizing is implemented. You'll
have to design your job accordingly, whether it is designed to run as a singleton (one reducer) or multiple reducers.
Neither is right or wrong, it depends on your use-case. Recognize that the more reducers assigned to the job, the more
simultaneous connections to the RDBMS will be created: this will scale, but only to a point.

```java
public static class MyRdbmsReducer extends Reducer<Text, IntWritable, Text, IntWritable>  {

    private Connection c = null;

    public void setup(Context context) {
        // Create DB connection...
    }

    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        // do summarizing
        // in this example the keys are Text, but this is just an example
	    } 

    public void cleanup(Context context) {
		     // close db connection
    }
}
```

## Oozie

Oozie is a workflow scheduler system to manage Apache Hadoop jobs. We're going to show you how to set up credentials and
then a couple of examples illustrating how to perform a Java action and a map reduce.

### Set Up Oozie Server with HBase Credential

### Oozie Workflow Example (Java Action With HBase Credential)

1. Put the JAR files `guava-*.jar`, `zookeeper-*.jar`, `hbase-*.jar`, `protobuf-java-*.jar`  into the `lib` directory of
   the Oozie application path.
2. For the `workflow.xml` file, do the following:

    - Add a `credentials` section. The type is `hbase`.
    - Specify the Java action to use the credential.
    - Place `hbase-site.xml` in the Oozie application path and use `<file>` in `workflow.xml` to put
      `hbase-site.xml` in the distributed cache.
    - Make sure you are using Oozie XSD version 0.3 and above for the tag. 

    Your `workflow.xml` should be similar to the XML below:

            ```xml
            <workflow-app name="foo-wf" xmlns="uri:oozie:workflow:0.3">
                <credentials>
                    <credential name="hbase.cert" type="hbase">
                    </credential>
                </credentials>
            
                <start to="java_1" />
                    <action name='java_1' cred="hbase.cert">
                        <java>
                            <job-tracker>${jobTracker}</job-tracker>
                            <name-node>${nameNode}</name-node>
                            <configuration>
                                <property>
                                    <name>dummy_key</name>
                                    <value>dummy_value</value>
                                </property>        
                                <property>
                                    <name>mapred.job.queue.name</name>
                                    <value>${queueName}</value>
                                </property>
                            </configuration>
                            <main-class>HelloHBase</main-class>
                            <arg>my_table</arg>
                            <arg>1</arg>
                            <file>hbase-site.xml#hbase-site.xml</file>
                            <capture-output/>
                        </java>
                        <ok to="decision1" />
                        <error to="fail_1" />
                    </action>
                    <decision name="decision1">
                        <switch>
                            <case to="end_1">${(wf:actionData('java_1')['RES'] == "2")}</case>
                            <default to="fail_1" />
                        </switch>
                    </decision>
                    ...
                </start>
                ...
            </workflow-app>
            ```

3. Create the example Java program `HelloHBase.java` with the following:

```java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.lang.String;

public class HelloHBase {

public static void main(String args[]) throws IOException {
    if(args.length < 2) {
        System.out.println("<table name> <limit>");
			  return;
    }
    System.out.println("DEBUG -- table name= "+args[0]+"; limit= "+args[1]);

    File file = new File(System.getProperty("oozie.action.output.properties"));
    Properties props = new Properties();

    Configuration conf = HBaseConfiguration.create(); //create(jobConf)
    //reuse conf instance so you HTable instances use the same connection
    HTable table = new HTable(conf, args[0]); 
    Scan scan = new Scan();
    ResultScanner scanner = table.getScanner(scan); 
    int limit = Integer.parseInt(args[1]);
    int n = 0;
    for(Result res: scanner) {
        if(limit-- <= 0)
            break;
            n++;
            System.out.println("DEBUG -- RESULT= "+res);
        }
        props.setProperty("RES", Integer.toString(n));
        OutputStream os = new FileOutputStream(file);
        props.store(os, "");
        os.close();
    }
}
```

## Hive

We're now going to show you how to use Hive and HBase together. We're not going to look at Hive in detail because the
purpose here is to show how you can port data from Hive into HBase and vice versa. See
`Hive: Getting Started <https://cwiki.apache.org/confluence/display/Hive/GettingStarted>`_ for comprehensive
documentation of Hive.

### Setting Up

1. Download `Hive <http://www.apache.org/dyn/closer.cgi/hive/>`_.
2. Set the environment variable HIVE_HOME to point to the installation directory: 

      $ export HIVE_HOME={{path-to/hive}}

3. Add `$HIVE_HOME/bin` to your `PATH`:: 

      $ export PATH=$HIVE_HOME/bin:$PATH

4. Set up warehouses for Hive::

      $ hadoop fs -mkdir       /tmp
      $ hadoop fs -mkdir       /user/hive/warehouse
      $ hadoop fs -chmod g+w   /tmp
      $ hadoop fs -chmod g+w   /user/hive/warehouse

5. Start Hive: `$ hive`
6. From the Hive shell, run the following commands to allow local mode::

      hive> SET mapred.job.tracker=local;
      hive> SET hive.exec.mode.local.auto=false;

### Simple Hive Example

In this simple example, we're going to use both the Hive and HBase shells to create tables, port data, and then fetch
it. In Hive,

1. From the HBase shell, create a simple table with a column family name:

        hbase> create 'test_table', 'cf1'

2. Push some rows with key-value pairs to the table:

        hbase> put 'test_table', 'row1', 'cf1:name', 'John'
        hbase> put 'test_table', 'row1', 'cf1:age', '33'

3. Open the Hive shell, and run the following command to import the data from your
   HBase table

        hive> CREATE EXTERNAL TABLE hbase_test(key INT, name STRING) STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' 
             WITH SERDEPROPERTIES ("hbase.columns.mapping" = ":key, cf1:val, cf1:val") TBLPROPERTIES("hbase.table.name" = "test_table");

   Note that using **EXTERNAL** allows Hive to access an existing HBase table.
   
4. Run a simple Hive query to confirm that the external table was created:

        hive> SELECT * from hbase_test;

### Advanced Hive Examples

Before we look at a more advanced example, it's important to understand the mapping between HBase columns to Hive
columns. The simple example above gives you an idea, but to create more complex, realistic Hive tables, you should have
a firmer grasp on the guidelines for mapping columns.

#### Mapping Column Principles

- There are two `SERDEPROPERTIES` that control the mapping of HBase columns to Hive:
   
    - `hbase.columns.mapping`
    - `hbase.table.default.storage.type` - This can have a value of either string (the default) or binary, this option
      is only available as of Hive 0.9 and the string behavior is the only one available in earlier versions.

- Because of the cumbersome and restrictive column mapping support currently, you need to be aware of the following:

    - For each Hive column, the table creator must specify a corresponding entry in the comma-delimited
      `hbase.columns.mapping` string. So, for a Hive table with n columns, the string should have n entries; whitespace
      should not be used in between entries since these will be interpreted as part of the column name, which is almost
      certainly not what you want.
    - A mapping entry must be either `:key` or of the form `column-family-name:[column-name][#(binary|string)` The
      type specification that delimited by `#` was added in Hive 0.9.0, earlier versions interpreted everything as
      strings. 
      - If no type specification is given the value from `hbase.table.default.storage.type` will be used.
      - Any prefixes of the valid values are valid, too. For example, #b instead of #binary.
      - If you specify a column as binary the bytes in the corresponding HBase cells are expected to be of the form that
        HBase's Bytes class yields.
    - There must be exactly one :key mapping (compound keys not supported yet).
    - Before HIVE-1228 in Hive 0.6, `:key` was not supported, and the first Hive column implicitly mapped to the key; as
      of Hive 0.6, it is now strongly recommended that you always specify the key explicitly; we will drop support for
      implicit key mapping in the future.
    - If no column-name is given, then the Hive column will map to all columns in the corresponding HBase column family,
      and the Hive MAP data type must be used to allow access to these (possibly sparse) columns.
    - There is currently no way to access the HBase timestamp attribute, and queries always access data with the latest
      timestamp.
    - Since HBase does not associate data type information with columns, the serde converts everything to string
      representation before storing it in HBase; there is currently no way to plug in a custom serde per column.
    - It is not necessary to reference every HBase column family, but those that are not mapped will be inaccessible via
      the Hive table; it's possible to map multiple Hive tables to the same HBase table.

#### Example Mapping Multiple Columns and Families

The example below has three Hive columns and two HBase column families, with two of the Hive columns (`v1` and `v2`)
corresponding to one of the column families (`a` with HBase column names `b` and `c`), and the other Hive column
corresponding to a single column (`e`) in its own column family (`d`). Because we're not creating an **external**
table, we are actually creating a new HBase table.

```
CREATE TABLE hbase_table_1(key int, value1 string, value2 int, value3 int) 
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES (
    "hbase.columns.mapping" = ":key,a:b,a:c,d:e"
);
```

## Pig

### Setting Up

- Follow the instructions in `Pig Setup <http://pig.apache.org/docs/r0.9.2/start.html#Pig+Setup>`_ if you don't have Pig
  installed and your environment set up.
- Set `PIG_CLASSPATH` with the following line in `.bashrc` or running the command from a shell:

        export PIG_CLASSPATH="`hbase classpath`:$PIG_CLASSPATH"

### Getting Started

`[Complete the PigTutorial](https://cwiki.apache.org/confluence/display/PIG/PigTutorial).

### Pig With HBase

We're going to look at examples using the HBase and Grunt shell, then a simple Java example, and end with a more
advanced example that shows most of what you would do with Pig and HBase.

#### Simple Grunt Example

In this example, we're simply going to use the HBase shell to create a table and then load the data, manipulate, and
dump the data in Grunt.

1. From the HBase shell, create the table `actors` with the column family `info`:

        hbase> create 'actors', 'info'

2. Create three more tables with the same column families:

        hbase> create 'actors_s', 'info'
        hbase> create 'actresses', 'info'
        hbase> create 'actresses_s', 'info'
        hbase> create 'actors_actresses_s', 'info'

3. Create rows with the `info` column family and the column keys `fname`, `lname`, `gender` for several actors:

        hbase> put 'actors', 'a1', 'info:fname', 'Kevin'
        hbase> put 'actors', 'a1', 'info:lname', 'Bacon'
        hbase> put 'actors', 'a2', 'info:fname', 'Billy'
        hbase> put 'actors', 'a2', 'info:lname', 'Crystal'
        hbase> put 'actors', 'a3', 'info:fname', 'Humphrey'
        hbase> put 'actors', 'a3', 'info:lname', 'Bogart'
        
4. Close your HBase shell and open up Grunt. 
5. Load the data from the `actors` table and display the data with the following commands:
   
        grunt> actors = LOAD 'hbase://actors' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage(
              'info:fname info:lname', '-loadKey true') AS (id:bytearray, fname:chararray, lname:chararray);
        grunt> describe actors;
        grunt> dump actors;

6. You should see a lot of logs from the map-reduce jobs, the inputs, outputs, counters,
   and finally the tuples containing your data as shown below:

       (a1,Kevin,Bacon)
       (a2,Billy,Crystal)
       (a3,Humphrey,Bogart)
       
7. Put the names in alphabetical order:

        grunt> sorted = ORDER actors BY lname ASC;

8. Load the sorted actors into the table `actors_s` with the following command:

        grunt> STORE sorted INTO 'hbase://actors_s' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('info:fname info:lname');

9. Close Grunt for now and go back to your HBase shell. Scan the tables `actors` and `actors_s` to confirm that Pig has
   done its job.
10. Congratulations, you've used Pig to load data from HBase and store into HBase. In the :ref:`Advanced Pig Example`,
    you're going create a Pig script to do a few more operations.

#### Advanced Pig Example

In this example, we're going to have Pig load a CSV file, load an HBase table, merge the data, and then write it to a
table.

1. Create the CSV file `actresses.csv` with the following comma-delimited records:

        as1, Sandra, Bullock
        as2, Meryl, Streep
        as3, Demi, Moore

2. Copy the file `actresses.csv` to HDFS:

        $ hadoop fs -copyFromLocal actresses.csv .

3. Create the pig script `merge_actors_actresses.pig` with the following, making sure you use the correct path to the
   file you created in the last step::

        -- Load the actress data from file
        actresses = LOAD 'actresses.csv' USING PigStorage(',') AS (
        id: bytearray,
        lname: chararray,
        fname: chararray);
        
        -- Load the actor data from file
        actors = LOAD 'hbase://actors' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage(
              'info:fname info:lname', '-loadKey true') AS (id:bytearray, fname:chararray, lname:chararray);
        
        -- Sort two lists by lname
        aa_s = ORDER (UNION actors, actresses) BY lname ASC;
        
        -- Store data from Pig into the HBase table
        STORE aa_s INTO 'hbase://actors_actresses_s' USING
        org.apache.pig.backend.hadoop.hbase.HBaseStorage (
        'info:fname info:lname');


4. Run the pig script: `$ pig merge_actors_actresses.pig`
5. From your HBase shell, confirm that the `actors_actresses_s` table has been populated with the sorted merge of the
   `actors` and `actresses` tables:
  
        hbase> scan 'merged_actors_s'

## Stargate: HBase REST Client/Server
==================================

Stargate is the HBase REST Client and Server that lets you make HTTP REST calls to HBase. Where possible, you should
instead prefer to use native RPCs in Java to access HBase, as the performance is better and the API feature set is
larger.

We'll go through a short tutorial, look at the structure of resource identifiers, and then give some sample code for
making HTTP requests.

### Quick Walkthrough

The following steps should be run locally.

1. From your `hbase` directory, start the HBase master and region servers: `$ bin/start-hbase.sh`
2. Start Stargate now in the background: `$ bin/hbase-daemon.sh start rest -p 8001` When run in the background, the
   output is directed to its own log under $HBASE_LOGS_DIR. The default port for Stargate is 8080, but we're using 8001
   to show how to specify a port.
3. Start the HBase shell and create the following table: 

        hbase> create 'test_table', 'cf'
        
4. Add some data to the table:

        hbase> put 'test_table', 'r1', 'cf:forecast', 'cold, cloudy'
        hbase> put 'test_table', 'r1', 'cf:temp', '25'

5. From the shell command-line, use cURL to make a simple HTTP GET request to the Stargate server:

        $ curl http://localhost:8001/version

   You should see either Stargate or rest as the service name, following by the JVM, OS, and Jetty version.
6. Check the status of the cluster and ask for XML: 

        $ curl -H "Accept: text/xml" http://localhost:8001/status/cluster

   You should get a `<ClusterStatus>` XML object that has child nodes for dead and live cluster nodes.
7. We're finally going to take a look at the table we created by running the following:

        $ curl -H "Accept: text/plain" http://localhost:8001/

   You should see your table `test_table` as a simple string.
8. We still haven't looked at the syntax for resource IDs, but you can infer the basic syntax from the following command
   which retrieves data from `cf:temp` from `r1`:

        $ curl -H "Accept: application/json" http://localhost:8001/test_table/r1/cf:temp/

   Unfortunately, the returned value is a bit unreadable as it's in base64 encoded. You can use decodebase64.com to
   decode the string.

9. Use a POST call to create a new row with a new value for `cf:forecast`:

       $ curl -H "Content-Type: application/json" -d '{"Row": { "@key":"r2", "Cell": { "@column":"cf:forecast", "$":"c3Vubnk=" } } }'  
       -X POST 'http://localhost:8001/test_table/r2/cf:forecast'
10. Make another POST call but send an XML request body to add a value for `cf:temp` for row `r2`:

        $ curl -H "Content-Type: text/xml" -d '<CellSet><Row key="cm93Mg=="><Cell column="Y2Y6dGVtcA==">ODA=</Cell></Row></CellSet>'  
        -X POST 'http://localhost:8001/test_table/r2/cf:temp'

11. From the HBase shell, scan your table to see the new values. You'll see the plain text version of the base64 encoded
   string `"c3Vubnk="`.
12. You can also scan the table using Stargate. Create a scanner with the following cURL command:

        $ curl -ik -H "Content-Type: text/xml" -d '<Scanner batch="1"/>' -X PUT 'http://localhost:8001/test_table/scanner'
   
   Stargate will return a `Location` with the URL for getting the scan object. Save the URL as you'll be making a
   `GET` call next.

13. Make a GET call to the URL returned to you to fetch the scanned data (it'll be base64 encoded):

       $ curl -ik -H "Accept: application/json" -X GET 'http://localhost:8001/test_table/scanner/{returned_id}'

   To get both rows, you'll need to set `batch="2"`.

14. You've used most of the functionality of the Stargate API, so go ahead and delete `test_table`:

        $ curl -ik -X DELETE 'http://localhost:8001/test_table/schema'

15. Confirm from the HBase shell that the table has been deleted.

        hbase> scan 'test_table'

### Resource Identifiers

Stargate exposes HBase tables, rows, cells, and metadata as URL specified resources.

#### Cell/Rows (GET)

    path := '/' <table> 
                '/' <row> 
                    ( '/' ( <column> ( ':' <qualifier> )? 
                    ( ',' <column> ( ':' <qualifier> )? )+ )? 
                    ( '/' ( <start-timestamp> ',' )? <end-timestamp> )? )? 
    query := ( '?' 'v' '=' <num-versions> )? 

#### Single Value Store (PUT)

Address with table, row, column (and optional qualifier), and optional timestamp.

    path := '/' <table> '/' <row> '/' <column> ( ':' <qualifier> )? 
              ( '/' <timestamp> )? 

#### Multiple (Batched) Value Store (PUT)

    path := '/' <table> '/' <false-row-key> 

#### Row/Column/Cell (DELETE)

    path := '/' <table> 
            '/' <row> 
            ( '/' <column> ( ':' <qualifier> )? 
              ( '/' <timestamp> )? )? 

#### Table Creation / Schema Update (PUT/POST), Schema Query (GET), or Delete (DELETE)

    path := '/' <table> / 'schema' 

#### Scanner Creation (POST)

    path := '/' <table> '/' 'scanner' 

#### Scanner Next Item (GET)

    path := '/' <table> '/' 'scanner' '/' <scanner-id> 

#### Scanner Deletion (DELETE)

    path := '/' <table> '/' '%scanner' '/' <scanner-id> 

### Request Body

#### JSON

This is example request body you would send in a POST request to assign values for one or more column family/column
pairs. Notice that the actual value is base64 encoded.

```
{
    "Row":
    [
        {
            "key":"row1",
            "Cell":
            [
                {
                    "column":"column_family:column_name1",
                    "$":"c29tZURhdGE="
                },
                {
                    "column":"column_family:column_name2",
                    "$":"bW9yZURhdGE="
                }
            ]
        }
    ]
}
```

#### XML

This is the same request body as shown above but remember that `"row`", `"column_family:column_name1"`, and
`"column_family:column_name2"` must be base64-encoded when you make an HTTP POST request to Stargate.

```xml
<CellSet>
    <Row key="row1">
        <Cell column="column_family:column_name1">
            c29tZURhdGE=
        </Cell>
    </Row>
    <Row key="row1">
        <Cell column="column_family:column_name2">
            bW9yZURhdGE=
        </Cell>
    </Row>
</CellSet>
```

## Storm With HBase

### Overview

[Storm](http://storm.incubator.apache.org/) allows you to process real-time data running 
[bolts (processes akin to MapReduce)](http://storm.incubator.apache.org/apidocs/backtype/storm/topology/IBasicBolt.html)  
over [spouts (data stream sources)](http://storm.incubator.apache.org/apidocs/backtype/storm/spout/ISpout.html). 
HBase is ideal for storing large amounts of loosely structured data, but Hadoop is solely meant for batch processing of
large sets of distributed data, not for processing a live data stream. (For a detailed explanation about data streams,
see the **Stream** section in the [Storm Tutorial](http://storm.incubator.apache.org/documentation/Tutorial.html).)

On the other hand, Storm, called the "Hadoop of real time", specializes in processing data that is continuous flux. In
addition, Storm is fault tolerant (automatically starts workers when they die) and highly scalable (inherent parallelism
and low latency). With real-time data processing, fault tolerance, and scalability, Storm is the perfect complement to
Hadoop.
