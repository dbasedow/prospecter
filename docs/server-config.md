---
layout: page
title: Server configuration
---

A few settings are server-wide. Those mostly 

**port**

The port Prospecter will listen on.

**bindInterface**

The network interface Prospecter will listen on. To listen on all interfaces use 0.0.0.0

**homeDir**

The directory in which all schemas reside. It is easiest to use an absolute path because relative paths are not relative
to the server configuration file, but to the working directory in which Prospecter was started.

Example
-------

    {
        "port": 8888,
        "bindInterface": "127.0.0.1",
        "homeDir": "/path/to/your/directory/schemas"
    }
