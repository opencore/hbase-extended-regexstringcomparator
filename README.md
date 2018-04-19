This project implements an `ExtendedRegexStringComparator` for HBase that has additional RegEx engines.

The default upstream HBase version includes two:
* JAVA
* JONI

This projects implements a few more:
* [RE2J](https://github.com/google/re2j)
* [BRICS](http://www.brics.dk/automaton/index.html)
  * Uses [Automaton](http://www.brics.dk/automaton/doc/dk/brics/automaton/Automaton.html)
* FAST_BRICS
  * Uses [RunAutomaton](http://www.brics.dk/automaton/doc/dk/brics/automaton/RunAutomaton.html) with `tableize` set to `false`
* FASTEST_BRICS
  * Uses [RunAutomaton](http://www.brics.dk/automaton/doc/dk/brics/automaton/RunAutomaton.html) with `tableize` set to `true`
  
Please note that all of them support slightly different syntaxes.  
  
Please note that the brics / Automaton one works differently than the others. The others use `find()` on `Pattern` to find substrings. As far as I can tell Automaton does not support this so the regex needs to match the whole string!

You can build the project using Maven:

    mvn clean package

The resulting JAR file needs to be deployed in the HBase classpath.

This is completely untested. Use at your own risk!
