
# Technical Test -

## Requirements
A 'forgetting' map should hold associations between a ‘key’ and some ‘content’. It should implement
at least two methods:
1. add (add an association)
2. find (find content using the specified key).
It should hold as many associations as it can, but no more than x associations at any time, with x being
a parameter passed to the constructor. Associations that are least used (in a sense of 'find') are
removed from the map as needed.

## End of Requirements

## Test/s

I found this quite an interesting test. It was a nice **real world** problem with a few potential
gotchas. I only hope I have found most of them :) Also hoping that I understood the requirements.
If not it would be good to know what I've missed or done wrong as it would be helpful in the future. 

## Unit test

**coverage report** - 

/forget-map-tech-test/target/site/jacoco/uk.co.cloudmatica/index.html

Note: Coverage excludes integration test.

For production work I would also have performance tests, e.g. Gatling, JMeter etc. However, I have
included timings in the Threaded test.


## Getting Started

I have added your emails to the access credentials, you should get a link to the project.
It is 100% private, to others.

Git clone:
 
 Not relevant as mailing it this time :) 

### Prerequisites

It's a maven project, but it has .idea folder for easy set up with Intellij (JDK version etc)

### Notes

I hope I've not been too verbose with the Javadoc and references to (Effective Java).
I thought it may help to know why I had come to certain decisions. 

e.g

```
/**
     *
     * @param key map key
     * @param value map value
     *
     *              Would be ok to remove synchronized block here. As at present it is only called from
     *              the {@link #add(Object, Object)} method in this class, which is synchronized.
     *              It has been left in as has no detrimental effects on performance, but if a new method is
     *              created and calls this without synchronized. This would create a data race.
     *              Essentially just future proofing, could be removed.
     */
```

## Author

* **Matt Trousdale**
