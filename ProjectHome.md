**Note: We moved repository to [GitHub](https://github.com/lucene-gosen/lucene-gosen)**

**Note: Download link move to Google Drive For version >= 4.6.1. See Links section.**

Features:

  * Improved integration of Lucene/Solr with [gosen](https://itadaki.svn.sourceforge.net/svnroot/itadaki/GoSen/)
  * Works with Lucene/Solr 3.6 and [4.10.0](http://code.google.com/p/lucene-gosen/source/browse/#svn%2Fbranches%2F4x)
  * No hassle: single jar file you add to solr/lib or lucene classpath
  * Attributes-based analysis: [attributes](http://code.google.com/p/lucene-gosen/source/browse/#svn%2Ftrunk%2Fsrc%2Fjava%2Forg%2Fapache%2Flucene%2Fanalysis%2Fja%2FtokenAttributes) such as [part of speech](http://lucene-gosen.googlecode.com/svn/trunk/example/stoptags_ja.txt), readings exposed for downstream processing
  * [Configurable analysis pipeline](http://lucene-gosen.googlecode.com/svn/trunk/example/schema.xml.snippet) for tuning to your needs
  * Performance improvements.
  * Utilizes [Lucene's test framework](http://blog.mikemccandless.com/2011/03/your-test-cases-should-sometimes-fail.html) to reduce bugs.
  * Maintained, reviewed by Lucene/Solr committers: [Koji Sekiguchi](http://www.rondhuit.com/), [Uwe Schindler](http://www.thetaphi.de/), [Robert Muir](http://www.twitter.com/rcmuir)

![http://lucene-gosen.googlecode.com/svn/wiki/lucene-gosen.png](http://lucene-gosen.googlecode.com/svn/wiki/lucene-gosen.png)

Apache Lucene, Solr and their logos are trademarks of the Apache Software Foundation.