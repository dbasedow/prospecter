---
title: Getting started
layout: page
include_in_head: true
---

Prospecter can be used as a stand-alone server or embedded in other applications. This guide covers the usage of the
stand-alone server.

Installation
------------
You can either download a build from [Github](https://github.com/dbasedow/prospecter/releases) or clone the repository
and build it yourself:

    git clone git@github.com:dbasedow/prospecter.git
    mvn clean compile assembly:single

You then end up with a stand-alone JAR file in the target directory.

Configuration
-------------
Create a directory somewhere where you will place configuration files and data. In that directory place a file called
**server-config.json** with the following content:

    {
        "port": 8888,
        "bindInterface": "127.0.0.1",
        "homeDir": "/path/to/your/directory/schemas"
    }

Of course you can change the port and bindInterface settings. To listen on all interfaces set bindInterface to 0.0.0.0.
The homeDir is the directory where schema configurations and data are stored, make sure it exists.

You could already start Prospecter now, but what's the point without a schema.

Create a new subdirectory in your homeDir. The directory name becomes the schema name, so it is best to only use
lower-case letters, numbers and dashes.

A schema tells Prospecter what fields can be searched in documents and what type they are. So we have to create a
schema configuration file: put a file called **schema.json** in your schema directory with the following content.

    {
        "fields": {
            "description": {
                "type": "FullText",
                "options": {
                    "stopwords": "predefined"
                }
            }
        },
        "persistence": {
            "file": "queries.mapdb"
        }
    }

This will result in a schema with a single field of type text. Common english stopwords will not be indexed, which saves
memory.

That's it! You can now use prospecter by typing

    java -jar prospecter-x.x.x-SNAPSHOT-jar-with-dependencies.jar /path/to/your/directory/server-config.json

You should see some logging output. Prospecter is now waiting for connections. To stop Prospecter hit Ctrl + C or send a
"kill -HUP".


Indexing
--------
To index a query you simply send an HTTP PUT request to Prospecter:

    PUT http://127.0.0.1:8888/your-schema
    {
        "id": 1,
        "query": {
            "conditions": [
                {
                    "field': "description",
                    "condition": "match",
                    "value": "hello world"
                }
            ]
        }
    }

This can be done with cURL:

    curl -X PUT --data "{\"id\":1,\"query\":{\"conditions\":[{\"field\":\"description\",\"condition\":\"match\",\"value\":\"hello world\"}]}}" http://127.0.0.1:8888/your-schema

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