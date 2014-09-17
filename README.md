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

Installation
------------
Download the latest release from [Github](https://github.com/dbasedow/prospecter/releases). Unzip it and you're done!
The archive contains a sample configuration and schema to play around with.

The default configuration uses the data/schemas directory for schemas and listens on 127.0.0.1:8888

Starting the Server
-------------------
If you are using OSX, Linux or any other UNIX-like operating system you can start the server using the bundled shell
script.

    cd prospecter-0.2.0
    ./start-server.sh

The script checks if Java is installed and then starts the Prospecter server using data/server.json as a config file
and a memory limit for the JVM of 512MB.

If you're on Windows you have to start Prospecter by executing:

    cd prospecter-0.2.0
    java -Xmx512m -server -jar prospecter.jar data/server.json

You should see some logging output. Prospecter is now waiting for connections. To stop Prospecter hit Ctrl + C or send a
"kill -HUP".


Indexing
--------
To index a query you simply send an HTTP PUT request to Prospecter:

    PUT http://127.0.0.1:8888/your-schema
    {
        "id": 1,
        "query": {
            "and": [
                {
                    "field': "description",
                    "condition": "match",
                    "value": "hello world"
                }
            ]
        }
    }

This can be done with cURL:

    curl -X PUT --data "{\"id\":1,\"query\":{\"and\":[{\"field\":\"description\",\"condition\":\"match\",\"value\":\"hello world\"}]}}" http://127.0.0.1:8888/your-schema

Matching
--------
Now that you have a query in your index you can send documents to Prospecter to find matching queries. A document is a
JSON object with named fields and values.

    {
        "description": "A \"Hello, world!\" program is a computer program that outputs \"Hello, World!\" (or some
        variant thereof) on a display device. Because it is typically one of the simplest programs possible in most
        programming languages, it is by tradition often used to illustrate to beginners the most basic syntax of a
        programming language. It is also used to verify that a language or system is operating correctly."
    }

This document has a single field called description and it contains the first paragraph from
[this Wikipedia article](http://en.wikipedia.org/wiki/Hello_World). To match this, simply HTTP POST it to your schema:

    curl -X POST --data "{\"description\":\"A \\\"Hello, world! \\\" program is a computer program that outputs \\\"Hello, World! \\\" (or some variant thereof) on a display device. Because it is typically one of the simplest programs possible in most programming languages, it is by tradition often used to illustrate to beginners the most basic syntax of a programming language. It is also used to verify that a language or system is operating correctly.\"}" http://127.0.0.1:8888/your-schema

You should see something like this:

    {"matches":[{"id":1}],"count":1}

The "matches" element contains an array of matched query ids and count tells you how many queries matched the document.

Links
-----
[Further information](http://dbasedow.github.io/prospecter/)

[Documentation](http://dbasedow.github.io/prospecter/docs/documentation/)