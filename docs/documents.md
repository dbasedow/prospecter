---
title: Documents
layout: page
---

Documents are simple JSON dictionaries. Each field is a dictionary entry. The data type in the JSON dictionary depends
on the configured field type.

**String**: "strvalue"

**Integer**, **Long**: 12345

**Double**: 1.0

**FullText**: "foo bar"

**Date**: "2014-08-10T19:22:51Z" (default) or custom format

**GeoDistance**:

    {
        "lat": 53.55,
        "lng": 10
    }

Latitude, longitude (no radius).

Example
-------

    {
        "field1": "test goes here",
        "locationField": {"lat": 53.55, "lng": 10},
        "category": ["category1", "category2"],
        "price": 2000
    }
