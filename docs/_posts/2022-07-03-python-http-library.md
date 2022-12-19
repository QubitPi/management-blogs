---
layout: post
title: HTTP Related Testing in Python
tags: [Testing]
color: rgb(8, 86, 112)
feature-img: "assets/img/post-cover/33-cover.png"
thumbnail: "assets/img/post-cover/33-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

requests
--------

### Session Objects

The Session object allows you to persist certain parameters across requests. It also persists cookies across all
requests made from the Session instance, and will use urllib3's
[connection pooling](https://urllib3.readthedocs.io/en/latest/reference/index.html#module-urllib3.connectionpool). So if
you're making several requests to the same host, the underlying TCP connection will be reused, which can result in a
significant performance increase (see
[HTTP persistent connection](https://en.wikipedia.org/wiki/HTTP_persistent_connection)).

A Session object has all the methods of the main Requests API.

Let’s persist some cookies across requests:

```python
session = requests.Session()

session.get('https://httpbin.org/cookies/set/sessioncookie/123456789')
response = session.get('https://httpbin.org/cookies')

print(response.text)
# '{"cookies": {"sessioncookie": "123456789"}}'
```

Sessions can also be used to provide default data to the request methods. This is done by providing data to the
properties on a Session object:

```python
session = requests.Session()
session.auth = ('user', 'pass')
session.headers.update({'x-test': 'true'})

# both 'x-test' and 'x-test2' are sent
session.get('https://httpbin.org/headers', headers={'x-test2': 'true'})
```

Any dictionaries that you pass to a request method will be merged with the session-level values that are set. The
method-level parameters override session parameters.

> ⚠️ Method-level parameters, however, will not be persisted across requests, even if using a session. This example will
> only send the cookies with the first request, but not the second:
>
> ```python
> session = requests.Session()
> 
> response = session.get('https://httpbin.org/cookies', cookies={'from-my': 'browser'})
> print(response.text)
> # '{"cookies": {"from-my": "browser"}}'
> 
> response = session.get('https://httpbin.org/cookies')
> print(response.text)
> # '{"cookies": {}}'
> ```

If you want to manually add cookies to your session, use the
[Cookie utility functions](https://requests.readthedocs.io/en/latest/api/#api-cookies) to manipulate `Session.cookies`.

Sessions can also be used as context managers:

```python
with requests.Session() as s:
    s.get('https://httpbin.org/cookies/set/sessioncookie/123456789')
```

This will make sure the session is closed as soon as the with block is exited, even if unhandled exceptions occurred.

> 📋 Remove a Value From a Dict Parameter
>
> Sometimes you’ll want to omit session-level keys from a dict parameter. To do this, you simply set that key’s value to
> None in the method-level parameter. It will automatically be omitted.

All values that are contained within a session are directly available to you. See the
[Session API Docs](https://requests.readthedocs.io/en/latest/api/#sessionapi) to learn more.

### Transport Adapters

Requests ships with a single **Transport Adapter**, the
[`HTTPAdapter`](https://requests.readthedocs.io/en/latest/api/#requests.adapters.HTTPAdapter). This adapter provides the default Requests interaction with HTTP and HTTPS using the powerful [urllib3](https://github.com/urllib3/urllib3)
library. Whenever a Requests [Session](#session-objects) is initialized, one of these is attached to the Session object
for HTTP, and one for HTTPS.

Requests enables users to create and use their own Transport Adapters that provide specific functionality. Once created,
a Transport Adapter can be mounted to a Session object, along with an indication of which web services it should apply
to.

```python
session = requests.Session()
session.mount('https://github.com/', MyAdapter())
```

The mount call registers a specific instance of a Transport Adapter to a prefix. Once mounted, any HTTP request made
using that session whose URL starts with the given prefix will use the given Transport Adapter.

#### Example: Specific SSL Version

The Requests team has made a specific choice to use whatever SSL version is default in the underlying library (
[urllib3](https://github.com/urllib3/urllib3)). Normally this is fine, but from time to time, you might find yourself
needing to connect to a service-endpoint that uses a version that isn't compatible with the default.

You can use Transport Adapters for this by taking most of the existing implementation of HTTPAdapter, and adding a
parameter `ssl_version` that gets passed-through to urllib3. We’ll make a Transport Adapter that instructs the library to
use SSLv3:

```python
import ssl
from urllib3.poolmanager import PoolManager

from requests.adapters import HTTPAdapter


class Ssl3HttpAdapter(HTTPAdapter):
    """"Transport adapter" that allows us to use SSLv3."""

    def init_poolmanager(self, connections, maxsize, block=False):
        self.poolmanager = PoolManager(
            num_pools=connections, maxsize=maxsize,
            block=block, ssl_version=ssl.PROTOCOL_SSLv3)
```

[requests-mock](https://requests-mock.readthedocs.io/en/latest/)
----------------------------------------------------------------

The requests library has the concept of [pluggable transport adapters](#transport-adapters). These adapters allow you to
register your own handlers for different URIs or protocols. The **requests-mock** library at its core is simply a
transport adapter that can be preloaded with responses that are returned if certain URIs are requested. This is
particularly useful in _unit tests_ where you want to return known responses from HTTP requests without making actual
calls.

### Mocker

The mocker is a loading mechanism to ensure the adapter is correctly in place to intercept calls from requests. Its goal
is to provide an interface that is as close to the real requests library interface as possible.

A simple example would be

```python
>>> import requests
>>> import requests_mock

>>> with requests_mock.Mocker() as m:
...     m.get('http://test.com', text='resp')
...     requests.get('http://test.com').text
...
'resp'
```

The code above is the same thing as using adapters:

```python
>>> import requests
>>> import requests_mock
>>> adapter = requests_mock.Adapter()
>>> session = requests.Session()
>>> session.mount('mock://', adapter)

adapter.register_uri('GET', 'http://test.com', text='resp')
```

> 📋 By default all matching is case insensitive. This can be adjusted by passing `case_sensitive=True` when creating a
> mocker or adapter, or globally by doing:
>
> ```python
> requests_mock.mock.case_sensitive = True
> ```
