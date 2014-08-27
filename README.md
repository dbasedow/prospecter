What is this?
=============

Prospecter is an open source implementation for "prospective search" or "persistent search". This is the opposite of
classic search in which documents get indexed and queries a executed against the index. Prospecter indexes queries and
finds matching queries by matching documents against the index.

In contrast to other reversed search implementation, like ElasticSearches percolator, Prospecter does not run all the
queries against an indexed document but actually indexes the queries. This means scaling will be a lot better. Current
performance tests look really promising.

P.S.: ElasticSearch is infinitely excellent and everyone should use it. It's just, that the current percolator
implementation is not scaling as well as the rest of the application.

Links
-----
[Further information](http://dbasedow.github.io/prospecter/)

[Documentation](http://dbasedow.github.io/prospecter/docs/documentation/)