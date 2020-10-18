# hadoopcryptoledger-plus

Fork of ZuInnoTe/hadoopcryptoledger enhanced with:

- additional functionality to compute block hashes and addresses as they appear in conventional block explorers.  
- representation of unsigned 32-bit values, such as block nonce and time, with full range.

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