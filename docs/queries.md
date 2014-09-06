---
title: Indexing queries
layout: page
---

Queries are represented as JSON dictionaries.

**id**

The query id. This has to be an integer (signed)

**query.conditions**

An array of condition dictionaries.

Condition dictionary
--------------------
**field**

Name of the field to match in document. This field has to be available in the schema.

**condition**

How the *value* has to relate to the document field.

| Condition | Description |
| --- | --- |
| match | Used for matching FullText fields. This is the only condition that can be used with that field type. |
| radius | Used for bounding box searches. |
| eq | *Equals*: value in document field has to be the exact same value. |
| gt | *Greater than*: value in document field must be greater than this. |
| lt | *Less than*: value in document field must be less than this. |
| gte | *Greater than, equals*: value in document field must be greater than this *or* equal. |
| lte | *Less than, equals*: value in document field must be greater than this *or* equal. |

**value**

The document field will be matched against this value. Depending on field type, the data type of *value* varies.

*String*: "strvalue"

*Integer*, *Long*: 12345

*Double*: 1.0

*FullText*: "foo bar"

*Date*: "2014-08-10T19:22:51Z" (default) or custom format

*GeoDistance*:

    {
        "lat": 53.55,
        "lng": 10,
        "distance": 100000
    }

Latitude, longitude and radius in Meters.

**not**

Let's you invert a condition. If *not* is set to true on a condition only documents that do **not** match this condition
will be matched.

Example
-------
    {
        "id": 123456,
        "query": {
            "conditions": [
                {
                    "field": "description",
                    "condition": "match",
                    "value": "haunted house"
                },
                {
                    "field": "price",
                    "condition": "gte",
                    "value": 500000
                },
                {
                    "field": "location",
                    "condition": "radius",
                    "value": {
                        "lat": 53.55,
                        "lng": 10,
                        "distance": 100000
                    }
                }
            ]
        }
    }