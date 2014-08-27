---
title: REST-API
layout: page
---

URLs
----
The URLs contain the schema name in the path part: http://localhost:8888/schema-name

Add query
---------
Send an HTTP PUT request to the schema you want the query added to. The request body contains the JSON document 
describing the query.

Match document
--------------
Send an HTTP POST request to the right schema for this document. The request body contains the JSON document.

Remove query
------------
Send an HTTP DELETE request to the schema you want the query removed from. You need to specify the query id in the URL
like this: http://localhost:8888/schema-name/1234 where 1234 is the id of the query you want to remove.