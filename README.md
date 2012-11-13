# dsl-in-clojure

This is my Clojure implementation of [Rainer Joswig's Common Lisp
implementation](http://www.youtube.com/watch?v=5FlHq_iiDW0) of
[Martin Fowler's C#
implementation](http://martinfowler.com/articles/languageWorkbench.html)
of a DSL.

I've taken some license and exploited Clojure's native maps and
multimethods rather than creating a "class" for each mapping but this
could be easily handled by using defrecord.
