---
title: Schema configuration
layout: page
---

The schema configuration tells Prospecter which fields are available for searching and how/where to store queries.

The structure looks like this:

    {
        "fields": {
            ...field definitions...
        },
        "persistence": {
            ...persistence configuration...
        }
    }

Fields
------
Each field you want to support in your documents is an entry in the fields dictionary. The field name is the key in the
dictionary and the value is another dictionary.

    "fieldName": {
        ...field configuration...
    }

**type**

Specified the type of the field. Available types are:

| Type | Description |
| --- | --- |
| DateTime | Range index for date time information. Exact matches have to match to the millisecond. |
| Double | Range index for double precision floating point values. |
| FullText | Full text index for text fields. |
| GeoDistance | Queries specify coordinates and a radius, documents contain coordinates and will match if they lie within radius of query coordinates. (At the moment a bounding box is used, so points NW, NE, SE or SW of query coordinate could be further away than radius! |
| Integer | Range index for integers (32-bit signed)|
| Long | Range index for long integers. (64-bit signed) |
| String | Index for string literals. For categories or similar fields. |

**options**

At the moment only FullText fields support options.

You can set a different Analyzer by specifying *analyzer* in the options. The value has to be a string naming a class
that implements the de.danielbasedow.prospecter.core.analysis.Analyzer interface. The default is
de.danielbasedow.prospecter.core.analysis.LuceneStandardAnalyzer.

**Available Analyzers**
| Name | Description |
| --- | --- |
| de.danielbasedow.prospecter.core.analysis.LuceneStandardAnalyzer | Default Analyzer based on Lucene's [StandardAnalyzer](http://lucene.apache.org/core/4_0_0/analyzers-common/org/apache/lucene/analysis/standard/StandardAnalyzer.html) |
| de.danielbasedow.prospecter.core.analysis.LuceneEnglishAnalyzer | Similar to default Analyzer, but does stemming. Based on [EnglishAnalyzer](http://lucene.apache.org/core/4_0_0/analyzers-common/org/apache/lucene/analysis/en/EnglishAnalyzer.html)|
| de.danielbasedow.prospecter.core.analysis.LuceneGermanAnalyzer | Analyzer for German language texts. Based on [GermanAnalyzer](http://lucene.apache.org/core/4_0_0/analyzers-common/org/apache/lucene/analysis/de/GermanAnalyzer.html) |

With the above analyzers you can further configure the stop word list that is used by specifying *stopwords*. The
following settings are available for *stopwords*:

| Setting | Description |
| --- | --- |
| none | Don't use any stopwords. This is the *default* that is also used if you specify anything not recognized. |
| predefined | Uses a predefined list of english stopwords. The list is part of Lucene. |
| ["word1", "word2", ...] | Custom stop word list. |

If you implement your own Analyzer your make() method will receive the option object during startup.

**format**

For DateTime fields you can specify in what format dates will be represented. The default is ISO8601 but you can specify
any string that can be interpreted by Java's SimpleDateFormat.


Persistence
-----------
The persistence settings tell Prospecter where to store your queries. At the moment there is only MapDB available as a
backend. The alternative is to use no backend. Queries will not be available after restarting Prospecter. This may still
be what you're looking for. To disable persistence leave out the whole *persistence* section from your configuration.

**file**

Filename in for backend to store queries in. Note that MapDB creates three files on disk, *file* is used as a prefix.

Example
-------
    {
        "fields": {
            "textField": {
                "type": "FullText",
                "options": {
                    "analyzer": "de.danielbasedow.prospecter.core.analysis.LuceneStandardAnalyzer",
                    "stopwords": "predefined"
                }
            },
            "price": {
                "type": "Integer"
            },
            "location": {
                "type": "GeoDistance"
            }
        },
        "persistence": {
            "file": "queries.mapdb"
        }
    }