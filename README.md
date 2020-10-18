# hadoopcryptoledger-plus

Fork of ZuInnoTe/hadoopcryptoledger with additional functionality 
to compute block hashes and addresses as they appear in conventional block explorers.  

In addition to providing the above features, this fork of the library aims to provide a simpler, 
cleaner API adhering to object-oriented design principles; in particular:

- fewer static methods
- cleaner exception handling
- simpler unit-tests
- refactoring of duplicated code

## Building

To build and run all unit and integration tests run:

~~~bash
gradle build
~~~

### Acknowledgements

The original work was developed by Jörn Franke. Additional code is adapted from the 
[bitcoinj project](https://github.com/bitcoinj/bitcoinj).