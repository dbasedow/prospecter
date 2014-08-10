What is this?
=============

prospecter is an open source implementation for "prospective search" or "persistent search". This is the opposite of
classic search in which documents get indexed and queries a executed against the index. prospecter indexes queries and
finds matching queries by matching documents against the index.

In contrast to other reversed search implementation, like ElasticSearches percolator, prospecter does not run all the
queries against an indexed document but actually indexes the queries. This means scaling will be a lot better. Current
performance tests look really promising. With the excite search log (>900k queries) prospecter outperforms ElasticSearch
by an order of magnitude (<200ms vs >2000ms).

P.S.: ElasticSearch is infinitely excellent and everyone should use it. It's just, that the current percolator
implementation is not scaling as well as the rest of the application.