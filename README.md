# hadoopcryptoledger-plus

This is a library of components allowing raw files from crypto wallets to be loaded into big data frameworks
such as Hadoop and Apache Spark.  The core classes provide implementations of Hadoop's [`RecordReader`](https://hadoop.apache.org/docs/current/api/org/apache/hadoop/mapred/RecordReader.html) interface, allowing, e.g. raw `blk*.dat` files to be easily loaded into Apache Spark.

The library is a fork of ZuInnoTe/hadoopcryptoledger enhanced with:

- additional functionality to compute BTC block hashes and addresses as they appear in conventional block explorers.  
- representation of unsigned 32-bit values, such as block nonce and time, with full range.

In addition to providing the above features, this fork of the library aims to provide a simpler, 
cleaner API adhering to object-oriented design principles; in particular:

- fewer static methods
- cleaner exception handling
- simpler unit-tests
- refactoring of duplicated code
- adherence to Java style guide

## Adding to your build

### Maven

~~~xml
<dependency>
  <groupId>com.mesonomics</groupId>
  <artifactId>hadoopcryptoledger-plus-fileformat</artifactId>
  <version>0.6</version>
</dependency>
~~~

## Building the project from source

To build and run all unit and integration tests run:

~~~bash
gradle build
~~~

## Acknowledgements

The original work was developed by Jörn Franke. Additional code is adapted from the 
[bitcoinj project](https://github.com/bitcoinj/bitcoinj).
