What is this?
=============

Prospecter is an open source implementation for "prospective search" or "persistent search". This is the opposite of
classic search in which documents get indexed and queries a executed against the index. Prospecter indexes queries and
finds matching queries by matching documents against the index.

In contrast to other reversed search implementation, like ElasticSearches percolator, Prospecter does not run all the
queries against an indexed document but actually indexes the queries. This means scaling will be a lot better. Current
performance tests look really promising. With the excite search log (>900k queries) Prospecter outperforms ElasticSearch
by an order of magnitude (\<200ms vs \>2000ms).

P.S.: ElasticSearch is infinitely excellent and everyone should use it. It's just, that the current percolator
implementation is not scaling as well as the rest of the application.

Example
=======

schema-json
-----------
    {
        "fields": {
            "description": {
                "type": "FullText"
            },
            "price": {
                "type": "Integer"
            }
        }
    }

query-json
----------
    {
        "id": 12345678,
        "query": {
            "conditions": [
                {
                    "field": "description",
                    "value": "haunted house"
                },
                {
                    "field": "price",
                    "value": 10,
                    "condition": "lte"
                }
            ]
        }
    }

document-json
-------------
    {
        "description": "This house is definitely haunted!",
        "price": 9
    }

Java Code
---------
    Schema schema = new SchemaBuilderJSON(schemaJSON).getSchema();
    Query query = schema.getQueryBuilder().buildFromJSON(queryJSON);
    schema.addQuery(query);
    DocumentBuilder builder = schema.getDocumentBuilder();
    Document doc = builder.build(documentJSON);
    Matcher matcher = schema.matchDocument(doc);
    for (Query query : matcher.getMatchedQueries()) {
        System.out.println(query.getQueryId());
    }

The above example will output the query id 12345678. You can keep adding queries and matching documents. The index has 
not to be rebuilt. Newly added queries serve immediately.
