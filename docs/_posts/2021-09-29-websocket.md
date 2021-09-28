---
layout: post
title: Websocket
tags: [Java, Webservice, JavaEE]
color: rgb(240,78,35)
feature-img: "assets/img/post-cover/3-cover.png"
thumbnail: "assets/img/post-cover/3-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---


<!--more-->

## Java API for WebSocket

### Introduction to WebSocket

In the traditional request-response model used in HTTP, the client requests resources, and the server provides
responses. The exchange is always initiated by the client; the server cannot send any data without the client requesting
it first. This model worked well for the World Wide Web when clients made occasional requests for documents that changed
infrequently, but the limitations of this approach are increasingly relevant as content changes quickly and users expect
a more interactive experience on the Web. The WebSocket protocol addresses these limitations by providing a full-duplex
communication channel between the client and the server. Combined with other client technologies, such as JavaScript and
HTML5, WebSocket enables web applications to deliver a richer user experience.

In a WebSocket application, the server publishes a WebSocket endpoint, and the client uses the endpoint's URI to connect
to the server. The WebSocket protocol is symmetrical after the connection has been established; the client and the
server can send messages to each other at any time while the connection is open, and they can close the connection at
any time. Clients usually connect only to one server, and servers accept connections from multiple clients.

The WebSocket protocol has two parts:

1. handshake
2. data transfer
   
The client initiates the handshake by sending a request to a WebSocket endpoint using its URI. The handshake is
compatible with existing HTTP-based infrastructure: web servers interpret it as an HTTP connection upgrade request. An
example handshake from a client looks like this:

```
GET /path/to/websocket/endpoint HTTP/1.1
Host: localhost
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: xqBt3ImNzJbYqRINxEFlkg==
Origin: http://localhost
Sec-WebSocket-Version: 13
```

An example handshake from the server in response to the client looks like this:

```
vHTTP/1.1 101 Switching Protocols
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: K7DJLdLooIwIG/MOpvWFB3y3FE8=
```

The server applies a known operation to the value of the `Sec-WebSocket-Key` header to generate the value of the
`Sec-WebSocket-Accept header`. The client applies the same operation to the value of the Sec-WebSocket-Key header, and
the connection is established successfully if the result matches the value received from the server. The client and the
server can send messages to each other after a successful handshake.

WebSocket supports text messages (encoded as UTF-8) and binary messages. The control frames in WebSocket are close,
ping, and pong (a response to a ping frame). Ping and pong frames may also contain application data.

WebSocket endpoints are represented by URIs that have the following form:

    ws://host:port/path?query
    wss://host:port/path?query

* The `ws` scheme represents an unencrypted WebSocket connection
* The `wss` scheme represents an encrypted connection
* The port component is optional. The default port number is 80 for unencrypted connections and 443 for encrypted
  connections.
* The path component indicates the location of an endpoint within a server 
* The query component is optional.

Modern web browsers implement the WebSocket protocol and provide a JavaScript API to connect to endpoints, send
messages, and assign callback methods for WebSocket events (such as opened connections, received messages, and closed
connections).

### Creating WebSocket Applications in the Java EE Platform

The Java EE platform includes the Java API for WebSocket ([JSR 356](http://www.jcp.org/en/jsr/detail?id=356)), which
enables you to create, configure, and deploy WebSocket endpoints in web applications. The WebSocket client API specified
in [JSR 356](http://www.jcp.org/en/jsr/detail?id=356) also enables you to access remote WebSocket endpoints from any
Java application.

The Java API for WebSocket consists of the following packages:

* The `javax.websocket.server` package contains annotations, classes, and interfaces to create and configure server
  endpoints.
* The `javax.websocket` package contains annotations, classes, interfaces, and exceptions that are common to client and
  server endpoints.

WebSocket endpoints are instances of the `javax.websocket.Endpoint` class. The Java API for WebSocket enables you to
create two kinds of endpoints

1. programmatic endpoints
2. annotated endpoints
   
To create a programmatic endpoint, you extend the `Endpoint` class and override its lifecycle methods. To create an
annotated endpoint, you decorate a Java class and some of its methods with the annotations provided by the packages
mentioned previously. After you have created an endpoint, you deploy it to an specific URI in the application so that
remote clients can connect to it.

> 📋 In most cases, it is easier to create and deploy an annotated endpoint than a programmatic endpoint. This post
> provides a simple example of a programmatic endpoint, but it focuses on annotated endpoints.

#### Creating and Deploying a WebSocket Endpoint

The process for creating and deploying a WebSocket endpoint is

1. Create an endpoint class.
2. Implement the lifecycle methods of the endpoint.
3. Add your business logic to the endpoint.
4. Deploy the endpoint inside a web application.

> 📋 As opposed to servlets, **WebSocket endpoints are instantiated multiple times**. **The container creates an
> instance of an endpoint per connection** to its deployment URI. Each instance is associated with one and only one
> connection. This facilitates keeping user state for each connection and makes development easier, because there is
> only one thread executing the code of an endpoint instance at any given time.

### Programmatic Endpoints

The following example shows how to create an endpoint by extending the `Endpoint` class:

```java
public class EchoEndpoint extends Endpoint {
    
    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String msg) {
                try {
                    session.getBasicRemote().sendText(msg);
                } catch (IOException e) {
                    ...
                }
            }
        });
    }
}
```

This endpoint echoes every message received. The Endpoint class defines three lifecycle methods

1. `onOpen`
2. `onClose`
3. `onError`
   
The `EchoEndpoint` class implements the `onOpen` method, which is the only abstract method in the `Endpoint` class.

The `Session` parameter represents a conversation between this endpoint and the remote endpoint. The `addMessageHandler`
method registers message handlers, and the `getBasicRemote` method returns an object that represents the remote
endpoint. The Session interface is covered in detail later.

The message handler is implemented as an anonymous inner class. The `onMessage` method of the message handler is invoked
when the endpoint receives a text message.

To deploy this programmatic endpoint, use the following code in your Java EE application:

```java
ServerEndpointConfig.Builder.create(EchoEndpoint.class, "/echo").build();
```

When you deploy your application, the endpoint is available at `ws://<host>:<port>/<application>/echo`; for example,
`ws://localhost:8080/echoapp/echo`.

### Annotated Endpoints

The following example shows how to create the same endpoint from [Programmatic Endpoints](#programmatic-endpoints) using
annotations instead:

```java
@ServerEndpoint("/echo")
public class EchoEndpoint {
    @OnMessage
    public void onMessage(Session session, String msg) {
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            ...
        }
    }
}
```

The annotated endpoint is simpler than the equivalent programmatic endpoint, and it is deployed automatically with the
application to the relative path defined in the `ServerEndpoint` annotation. Instead of having to create an additional
class for the message handler, this example uses the `OnMessage` annotation to designate the method invoked to handle
messages.

The list below lists the annotations available in the `javax.websocket` package to designate the methods that handle
lifecycle events. The examples in the table show the most common parameters for these methods. See the API reference for
details on what combinations of parameters are allowed in each case.

* `@OnOpen` Connection opened

  ```java
  @OnOpen
  public void open(Session session, EndpointConfig conf) {
      ...
  }  
  ```
  
* `@OnMessage` Message received

  ```java
  @OnMessage
  public void message(Session session, String msg) {
      ...
  }
  ```

* `@OnError` Connection error
  
  ```java
  @OnError
  public void error(Session session, Throwable error) {
     ...
  }
  ```

* `@OnClose` Connection closed

  ```java
  @OnClose
  public void close(Session session, CloseReason reason) {{
     ...
  }
  ```
  
### Sending and Receiving Messages

WebSocket endpoints can send and receive text and binary messages. In addition, they can also send ping frames and
receive pong frames. This section describes how to use the `Session` and `RemoteEndpoint` interfaces to send messages to
the connected peer and how to use the `OnMessage` annotation to receive messages from it.

#### Sending Messages

Follow these steps to send messages in an endpoint.

1. **Obtain the `Session` object from the connection** The Session object is available as a parameter in the annotated
   lifecycle methods of the endpoint. When your message is a response to a message from the peer, you have the `Session`
   object available inside the method that received the message (the method annotated with `@OnMessage`). If you have to
   send messages that are not responses, store the `Session` object as an instance variable of the endpoint class in the
   method annotated with `@OnOpen` so that you can access it from other methods.
2. **Use the `Session` object to obtain a `RemoteEndpoint` object** The `Session.getBasicRemote` method and the
   `Session.getAsyncRemote` method return `RemoteEndpoint.Basic` and `RemoteEndpoint.Async` objects, respectively. The
   `RemoteEndpoint.Basic` interface provides blocking methods to send messages; the `RemoteEndpoint.Async` interface
   provides nonblocking methods.
3. **Use the `RemoteEndpoint` object to send messages to the peer** The following list shows some of the methods you can
   use to send messages to the peer
   - `void RemoteEndpoint.Basic.sendText(String text)` Send a text message to the peer. This method blocks until the
     whole message has been transmitted
   - `void RemoteEndpoint.Basic.sendBinary(ByteBuffer data)` Send a binary message to the peer. This method blocks until
     the whole message has been transmitted
   - `void RemoteEndpoint.sendPing(ByteBuffer appData)` Send a ping frame to the peer
   - `void RemoteEndpoint.sendPong(ByteBuffer appData)` Send a pong frame to the peer

The example in [Annotated Endpoints](#annotated-endpoints) demonstrates how to use this procedure to reply to every
incoming text message.

##### Sending Messages to All Peers Connected to an Endpoint

Each instance of an endpoint class is associated with one and only one connection and peer; however, there are cases in
which an endpoint instance needs to send messages to all connected peers. Examples include chat applications and online
auctions. The `Session` interface provides the `getOpenSessions` method for this purpose. The following example
demonstrates how to use this method to forward incoming text messages to all connected peers:

```java
@ServerEndpoint("/echoall")
public class EchoAllEndpoint {
    
    @OnMessage
    public void onMessage(Session session, String msg) {
        try {
            for (Session sess : session.getOpenSessions()) {
                if (sess.isOpen()) {
                    sess.getBasicRemote().sendText(msg);
                }
            }
        } catch (IOException e) {
            ...
        }
    }
}
```

#### Receiving Messages

The `OnMessage` annotation designates methods that handle incoming messages. You can have at most three methods
annotated with `@OnMessage` in an endpoint, one for each message type: text, binary, and pong. The following example
demonstrates how to designate methods to receive all three types of messages:

```java
@ServerEndpoint("/receive")
public class ReceiveEndpoint {
    
    @OnMessage
    public void textMessage(Session session, String msg) {
        System.out.println("Text message: " + msg);
    }
    
    @OnMessage
    public void binaryMessage(Session session, ByteBuffer msg) {
        System.out.println("Binary message: " + msg.toString());
    }
    
    @OnMessage
    public void pongMessage(Session session, PongMessage msg) {
        System.out.println("Pong message: " + msg.getApplicationData().toString());
    }
}
```

### Maintaining Client State

Because the container creates an instance of the endpoint class for every connection, you can define and use instance
variables to store client state information. In addition, the `Session.getUserProperties` method provides a modifiable
map to store user properties. For example, the following endpoint replies to incoming text messages with the contents of
the previous message from each client:

```java
@ServerEndpoint("/delayedecho")
public class DelayedEchoEndpoint {
    
    @OnOpen
    public void open(Session session) {
        session.getUserProperties().put("previousMsg", " ");
    }
    
    @OnMessage
    public void message(Session session, String msg) {
        String prev = (String) session.getUserProperties().get("previousMsg");
        session.getUserProperties().put("previousMsg", msg);
        try {
            session.getBasicRemote().sendText(prev);
        } catch (IOException e) {
            ...
        }
    }
}
```

### Using Encoders and Decoders

The Java API for WebSocket provides support for converting between WebSocket messages and custom Java types using
encoders and decoders. An encoder takes a Java object and produces a representation that can be transmitted as a
WebSocket message; for example, encoders typically produce JSON, XML, or binary representations. A decoder performs the
reverse function; it reads a WebSocket message and creates a Java object.

This mechanism simplifies WebSocket applications, because it decouples the business logic from the serialization and
deserialization of objects.

#### Implementing Encoders to Convert Java Objects into WebSocket Messages

The procedure to implement and use encoders in endpoints follows.

1. Implement one of the following interfaces:

    * `Encoder.Text<T>` for text messages
    * `Encoder.Binary<T>` for binary messages

    These interfaces specify the "encode" method. Implement an encoder class for each custom Java type that you want to
    send as a WebSocket message.
   
2. Add the names of your encoder implementations to the encoders optional parameter of the ServerEndpoint annotation.
   (see example below)
   
3. Use the `sendObject(Object data)` method of the `RemoteEndpoint.Basic` or `RemoteEndpoint.Async` interfaces to send
   your objects as messages. The container looks for an encoder that matches your type and uses it to convert the object
   to a WebSocket message.

For example, if you have two Java types (`MessageA` and `MessageB`) that you want to send as text messages, implement
the `Encoder.Text<MessageA>` and `Encoder.Text<MessageB>` interfaces as follows:

```java
public class MessageATextEncoder implements Encoder.Text<MessageA> {
    
    @Override
    public void init(EndpointConfig ec) {
        ...
    }
    
    @Override
    public void destroy() {
        ...
    }
    
    @Override
    public String encode(MessageA msgA) throws EncodeException {
        // Access msgA's properties and convert to JSON text...
        return msgAJsonString;
    }
}
```

Implement `Encoder.Text<MessageB>` similarly. Then, add the encoders parameter to the `ServerEndpoint` annotation as
follows:

```java
@ServerEndpoint(
    value = "/myendpoint",
    encoders = { MessageATextEncoder.class, MessageBTextEncoder.class }
)
public class EncEndpoint {
    
    ...
}
```

Now, you can send `MessageA` and `MessageB` objects as WebSocket messages using the `sendObject` method as follows:

```java
MessageA msgA = new MessageA(...);
MessageB msgB = new MessageB(...);
session.getBasicRemote.sendObject(msgA);
session.getBasicRemote.sendObject(msgB);
```

As in this example, you can have more than one encoder for text messages and more than one encoder for binary messages.
Like endpoints, encoder instances are associated with one and only one WebSocket connection and peer, so there is only
one thread executing the code of an encoder instance at any given time.

#### Implementing Decoders to Convert WebSocket Messages into Java Objects

The procedure to implement and use decoders in endpoints is

1. Implement one of the following interfaces:

    * `Decoder.Text<T>` for text messages
    * `Decoder.Binary<T>` for binary messages

    These interfaces specify the `willDecode` and `decode` methods.

2. Add the names of your decoder implementations to the `decoders` optional parameter of the `ServerEndpoint`
   annotation.

3. Use the `OnMessage` annotation in the endpoint to designate a method that takes your custom Java type as a parameter.
   When the endpoint receives a message that can be decoded by one of the decoders you specified, the container calls
   the method annotated with `@OnMessage` that takes your custom Java type as a parameter if this method exists.

For example, if you have two Java types (`MessageA` and `MessageB`) that you want to send and receive as text messages,
define them so that they extend a common class (`Message`). **Because you can only define one decoder for text
messages**, implement a decoder for the Message class as follows:

```java
public class MessageTextDecoder implements Decoder.Text<Message> {
    
    @Override
    public void init(EndpointConfig ec) {
        ...
    }
    
    @Override
    public void destroy() {
        ...
    }
    
    @Override
    public Message decode(String string) throws DecodeException {
        // Read message...
        if ( /* message is an A message */ ) {
            return new MessageA(...);
        } else {
            // message is a B message
            return new MessageB(...);
        }
    }
    
    @Override
    public boolean willDecode(String string) {
        // Determine if the message can be converted into either a
        // MessageA object or a MessageB object...
        return canDecode;
    }
}
```

Then, add the `decoder` parameter to the `ServerEndpoint` annotation as follows:

```java
@ServerEndpoint(
    value = "/myendpoint",
    encoders = { MessageATextEncoder.class, MessageBTextEncoder.class },
    decoders = { MessageTextDecoder.class }
)
public class EncDecEndpoint {
    
    ...
}
```

Now, define a method in the endpoint class that receives `MessageA` and `MessageB` objects as follows:

```java
@OnMessage
public void message(Session session, Message msg) {
    if (msg instanceof MessageA) {
        // We received a MessageA object...
    } else {
        // We received a MessageB object...
    }
}
```

Like endpoints, decoder instances are associated with one and only one WebSocket connection and peer, so there is only
one thread executing the code of a decoder instance at any given time.

### Path Parameters

The `ServerEndpoint` annotation enables you to use URI templates to specify parts of an endpoint deployment URI as
application parameters. For example, consider this endpoint:

```java
@ServerEndpoint("/chatrooms/{room-name}")
public class ChatEndpoint {
    ...
}
```

If the endpoint is deployed inside a web application called `chatapp` at a local Java EE server in port 8080, clients
can connect to the endpoint using any of the following URIs:

* http://localhost:8080/chatapp/chatrooms/currentnews
* http://localhost:8080/chatapp/chatrooms/music
* http://localhost:8080/chatapp/chatrooms/cars
* http://localhost:8080/chatapp/chatrooms/technology

Annotated endpoints can receive path parameters as arguments in methods annotated with `@OnOpen`, `@OnMessage`, and
`@OnClose`. In this example, the endpoint uses the parameter in the `@OnOpen` method to determine which chat room the
client wants to join:

```java
@ServerEndpoint("/chatrooms/{room-name}")
public class ChatEndpoint {
    
    @OnOpen
    public void open(Session session, EndpointConfig config, @PathParam("room-name") String roomName) {
        // Add the client to the chat room of their choice ...
    }
}
```

The path parameters used as arguments in these methods can be strings, primitive types, or the corresponding wrapper
types.

### Handling Errors

To designate a method that handles errors in an annotated WebSocket endpoint, decorate it with `@OnError`:

```java
@ServerEndpoint("/testendpoint")
public class TestEndpoint {

    ...
    
    @OnError
    public void error(Session session, Throwable t) {
        ...
    }
}
```

This method is invoked when there are connection problems, runtime errors from message handlers, or conversion errors
when decoding messages.

### Specifying an Endpoint Configurator Class

The Java API for WebSocket enables you to configure how the container creates server endpoint instances. You can provide
custom endpoint configuration logic in order to:

* access the details of the initial HTTP request for a WebSocket connection
* perform custom checks on the `origin` HTTP header
* modify the WebSocket handshake response
* choose a WebSocket subprotocol from those requested by the client
* control the instantiation and initialization of endpoint instances

