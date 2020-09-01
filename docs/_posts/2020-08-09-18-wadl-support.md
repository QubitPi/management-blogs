---
layout: post
title: WADL Support
tags: [WADL]
author: QubitPi
excerpt_separator: <!--more-->
---

## WADL introduction

Jersey contains support for [Web Application Description Language (WADL)](https://javaee.github.io/wadl/). WADL is a XML
description of a deployed RESTful web application. It contains model of the deployed resources, their structure,
supported media types, HTTP methods and so on. In a sense, WADL is similar to the WSDL (Web Service Description
maLanguage) which describes SOAP web services. WADL is however specifically designed to describe RESTful Web resources. 
 
<!--more-->

---
**Important**

Since Jersey 2.5.1 the WADL generated by default is WADL in shorter form without additional extension resources
(`OPTIONS` methods, WADL resource). In order to get full WADL use the query parameter `detail=true`. 

---

Let's start with the simple WADL example. In the example there is a simple `CountryResource` deployed and we request a
wadl of this resource. The context root path of the application is `http://localhost:9998`.

```java
@Path("country/{id}")
public static class CountryResource {
 
    private CountryService countryService;
 
    public CountryResource() {
        // init countryService
    }
 
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Country getCountry(@PathParam("countryId") int countryId) {
       return countryService.getCountry(countryId);
    }
}
```

The WADL of a Jersey application that contains the resource above can be requested by a HTTP `GET` request to
`http://localhost:9998/application.wadl`. The Jersey will return a response with a WADL content similar to the one in
the following example: 

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<application xmlns="http://wadl.dev.java.net/2009/02">
    <doc xmlns:jersey="http://jersey.java.net/" jersey:generatedBy="Jersey: 2.5-SNAPSHOT 2013-12-20 17:14:21"/>
    <grammars/>
    <resources base="http://localhost:9998/">
        <resource path="country/{id}">
            <param xmlns:xs="http://www.w3.org/2001/XMLSchema" type="xs:int" style="template" name="countryId"/>
            <method name="GET" id="getCountry">
                <response>
                    <representation mediaType="application/xml"/>
                </response>
            </method>
        </resource>
    </resources>
</application>
```

The returned WADL is a XML that contains element `resource` with path `country/{id}`. This resource has one inner
method element with http method as attribute, name of java method and its produced representation. This description
corresponds to defined java resource. Now let's look at more complex example. 

The previous WADL does not actually contain all resources exposed in our API. There are other resources that are
available and are hidden in the previous WADL. The previous WADL shows only resources that are provided by the user. In
the following example, the WADL is generated using query parameter detail:
`http://localhost:9998/application.wadl?detail`. Note that usage of
`http://localhost:9998/application.wadl?detail=true` is also valid. This will produce the WADL with all resource
available in the application: 

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<application xmlns="http://wadl.dev.java.net/2009/02">
    <doc xmlns:jersey="http://jersey.java.net/" jersey:generatedBy="Jersey: 2.5-SNAPSHOT 2013-12-20 17:14:21"/>
    <doc xmlns:jersey="http://jersey.java.net/" jersey:hint="To get simplified WADL with user's resources only use the query parameter 'simple=true'. Link: http://localhost:9998/application.wadl?detail=true&amp;simple=true"/>
    <grammars/>
    <resources base="http://localhost:9998/">
        <resource path="country/{id}">
            <param xmlns:xs="http://www.w3.org/2001/XMLSchema" type="xs:int" style="template" name="countryId"/>
            <method name="GET" id="getCountry">
                <response>
                    <representation mediaType="application/xml"/>
                </response>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="application/vnd.sun.wadl+xml"/>
                </response>
                <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="text/plain"/>
                </response>
                <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="*/*"/>
                </response>
                <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
            </method>
        </resource>
        <resource path="application.wadl">
            <method name="GET" id="getWadl">
                <response>
                    <representation mediaType="application/vnd.sun.wadl+xml"/>
                    <representation mediaType="application/xml"/>
                </response>
                <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="text/plain"/>
                </response>
                <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="*/*"/>
                </response>
                <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
            </method>
            <resource path="{path}">
                <param xmlns:xs="http://www.w3.org/2001/XMLSchema" type="xs:string" style="template" name="path"/>
                <method name="GET" id="geExternalGrammar">
                    <response>
                        <representation mediaType="application/xml"/>
                    </response>
                    <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
                </method>
                <method name="OPTIONS" id="apply">
                    <request>
                        <representation mediaType="*/*"/>
                    </request>
                    <response>
                        <representation mediaType="text/plain"/>
                    </response>
                    <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
                </method>
                <method name="OPTIONS" id="apply">
                    <request>
                        <representation mediaType="*/*"/>
                    </request>
                    <response>
                        <representation mediaType="*/*"/>
                    </response>
                    <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
                </method>
                <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
            </resource>
            <jersey:extended xmlns:jersey="http://jersey.java.net/">true</jersey:extended>
        </resource>
    </resources>
</application>
```

In the example above the returned application WADL is shown in full. WADL schema is defined by the WADL specification,
so let's look at it in more details. The root WADL document element is the `application`. It contains global information
about the deployed JAX-RS application. Under this element there is a nested element `resources` which contains zero or
more `resource` elements. Each `resource` element describes a single deployed resource. In our example, there are only
two root resources - `"country/{id}"` and `"application.wadl"`. The "application.wadl" resource is the resource that was
just requested in order to receive the application WADL document. Even though WADL support is an additional feature in
Jersey it is still a resource deployed in the resource model and therefore it is itself present in the returned WADL
document. The first resource element with the `path="country/{id}"` is the element that describes our custom deployed
resource. This resource contains a `GET` method and three `OPTIONS` methods. The `GET` method is our `getCountry()`
method defined in the sample. There is a method name in the id attribute and `@Produces` is described in the
`response/representation` WADL element. `OPTIONS` methods are the methods that are automatically added by Jersey to each
resource. There is an `OPTIONS` method returning `"text/plain"` media type, that will return a response with a string
entity containing the list of methods deployed on this resource (this means that instead of WADL you can use this
`OPTIONS` method to get similar information in a textual representation). Another `OPTIONS` method returning `*/*` will
return a response with no entity and `Allow` header that will contain list of methods as a String. The last `OPTIONS`
method producing `"application/vnd.sun.wadl+xml"` returns a WADL description of the resource `"country/{id}"`. As you
can see, all `OPTIONS` methods return information about the resource to which the HTTP `OPTIONS` request is made. 

Second resource with a path "application.wadl" has, again, similar `OPTIONS` methods and one `GET` method which return
this WADL. There is also a sub-resource with a path defined by path param `{path}`. This means that you can request a
resource on the URI `http://localhost:9998/application.wadl/something`. This is used only to return an external grammar
if there is any attached. Such an external grammar can be for example an `XSD` schema of the response entity which if
the response entity is a JAXB bean. An external grammar support via ***Jersey extended WADL support*** is described in
sections below. 

All resource that were added in this second example into the WADL contains element `extended`. This means that this
resource is not a part of a core RESTful API and is rather a helper resource. If you need to mark any your own resource
are extended, annotate it with `@ExtendedResource`. Note that there might be methods visible in the default simple WADL
even the user has not added them. This is for example the case of MVC added methods which were added by `ModelProcessor`
but are still intended to be used by the client to achieve their primary use case of getting formatted data. 

Let's now send a HTTP `OPTIONS` request to `"country/{id}"` resource using the the `curl` command: 

    curl -X OPTIONS -H "Allow: application/vnd.sun.wadl+xml" -v http://localhost:9998/country/15

We should see a WADL returned similar to this one:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<application xmlns="http://wadl.dev.java.net/2009/02">
    <doc xmlns:jersey="http://jersey.java.net/"
        jersey:generatedBy="Jersey: 2.0-SNAPSHOT ${buildNumber}"/>
    <grammars/>
    <resources base="http://localhost:9998/">
        <resource path="country/15">
            <method name="GET" id="getCountry">
                <response>
                    <representation mediaType="application/xml"/>
                </response>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="application/vnd.sun.wadl+xml"/>
                </response>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="text/plain"/>
                </response>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="*/*"/>
                </response>
            </method>
        </resource>
    </resources>
</application>
```

The returned WADL document has the standard WADL structure that we saw in the WADL document returned for the whole
Jersey application earlier. The main difference here is that the only resource is the `resource` to which the `OPTIONS`
HTTP request was sent. The resource has now path `"country/15"` and not `"country/{id}"` as the path parameter `{id}`
was already specified in the request to this concrete resource.

Another, a more complex WADL example is shown in the next example.

```java
@Path("customer/{id}")
public static class CustomerResource {
    private CustomerService customerService;
 
    @GET
    public Customer get(@PathParam("id") int id) {
        return customerService.getCustomerById(id);
    }
 
    @PUT
    public Customer put(Customer customer) {
        return customerService.updateCustomer(customer);
    }
 
    @Path("address")
    public CustomerAddressSubResource getCustomerAddress(@PathParam("id") int id) {
        return new CustomerAddressSubResource(id);
    }
 
    @Path("additional-info")
    public Object getAdditionalInfoSubResource(@PathParam("id") int id) {
        return new CustomerAddressSubResource(id);
    }
 
}
 
 
public static class CustomerAddressSubResource {
    private final int customerId;
    private CustomerService customerService;
 
    public CustomerAddressSubResource(int customerId) {
        this.customerId = customerId;
        this.customerService = null; // init customer service here
    }
 
    @GET
    public String getAddress() {
        return customerService.getAddressForCustomer(customerId);
    }
 
    @PUT
    public void updateAddress(String address) {
        customerService.updateAddressForCustomer(customerId, address);
    }
 
    @GET
    @Path("sub")
    public String getDeliveryAddress() {
        return customerService.getDeliveryAddressForCustomer(customerId);
    }
}
```

The `GET` request to `http://localhost:9998/application.wadl` will return the following WADL document: 

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<application xmlns="http://wadl.dev.java.net/2009/02">
    <doc xmlns:jersey="http://jersey.java.net/"
        jersey:generatedBy="Jersey: 2.0-SNAPSHOT ${buildNumber}"/>
    <grammars/>
    <resources base="http://localhost:9998/">
        <resource path="customer/{id}">
            <param xmlns:xs="http://www.w3.org/2001/XMLSchema"
                type="xs:int" style="template" name="id"/>
            <method name="GET" id="get">
                <response/>
            </method>
            <method name="PUT" id="put">
                <response/>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="application/vnd.sun.wadl+xml"/>
                </response>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="text/plain"/>
                </response>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="*/*"/>
                </response>
            </method>
            <resource path="additional-info">
                <param xmlns:xs="http://www.w3.org/2001/XMLSchema"
                    type="xs:int" style="template" name="id"/>
            </resource>
            <resource path="address">
                <param xmlns:xs="http://www.w3.org/2001/XMLSchema"
                    type="xs:int" style="template" name="id"/>
                <method name="GET" id="getAddress">
                    <response/>
                </method>
                <method name="PUT" id="updateAddress"/>
                <resource path="sub">
                    <method name="GET" id="getDeliveryAddress">
                        <response/>
                    </method>
                </resource>
            </resource>
        </resource>
        <resource path="application.wadl">
            <method name="GET" id="getWadl">
                <response>
                    <representation mediaType="application/vnd.sun.wadl+xml"/>
                    <representation mediaType="application/xml"/>
                </response>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="text/plain"/>
                </response>
            </method>
            <method name="OPTIONS" id="apply">
                <request>
                    <representation mediaType="*/*"/>
                </request>
                <response>
                    <representation mediaType="*/*"/>
                </response>
            </method>
            <resource path="{path}">
                <param xmlns:xs="http://www.w3.org/2001/XMLSchema"
                    type="xs:string" style="template" name="path"/>
                <method name="GET" id="geExternalGrammar">
                    <response>
                        <representation mediaType="application/xml"/>
                    </response>
                </method>
                <method name="OPTIONS" id="apply">
                    <request>
                        <representation mediaType="*/*"/>
                    </request>
                    <response>
                        <representation mediaType="text/plain"/>
                    </response>
                </method>
                <method name="OPTIONS" id="apply">
                    <request>
                        <representation mediaType="*/*"/>
                    </request>
                    <response>
                        <representation mediaType="*/*"/>
                    </response>
                </method>
            </resource>
        </resource>
    </resources>
</application>
```

The `resource` with `path="customer/{id}"` is similar to the country resource from the previous example. There is a path
parameter which identifies the customer by `id`. The resource contains 2 user-declared methods and again auto-generated
`OPTIONS` methods added by Jersey. The resource declares 2 sub-resource locators which are represented in the returned
WADL document as nested `resource` elements. Note that the sub-resource locator `getCustomerAddress()` returns a type
`CustomerAddressSubResource` in the method declaration and also in the WADL there is a resource element for such a sub
resource with full internal description. ***The second method `getAdditionalInfoSubResource()` returns only an `Object`
in the method declaration. While this is correct from the JAX-RS perspective as the real returned type can be computed
from a request information, it creates a problem for WADL generator because WADL is generated based on the static
configuration of the JAX-RS application resources. The WADL generator does not know what type would be actually returned
to a request at run time. That is the reason why the nested `resource` element with `path="additional-info"` does not
contain any information about the supported resource representations***. 

The `CustomerAddressSubResource` sub-resource described in the nested element `<resource path="address">` does not
contain an `OPTIONS` method. While these methods are in fact generated by Jersey for the sub-resource, Jersey WADL
generator does not currently support adding these methods to the sub-resource description. This should be addressed in
the near future. Still, there are two user-defined resource methods handling HTTP `GET` and `PUT` requests. The
sub-resource method `getDeliveryAddress()` is represented as a separate nested resource with `path="sub"`. Should there
be more sub-resource methods defined with `path="sub"`, then all these method descriptions would be placed into the same
`resource` element. In other words, sub-resource methods are grouped in WADL as sub-resources based on their `path`
value.

## Configuration

WADL generation is enabled in Jersey by default. This means that `OPTIONS` methods are added by default to each resource
and an auto-generated `/application.wadl` resource is deployed too. To override this default behavior and disable WADL
generation in Jersey, setup the configuration property in your application: 

    jersey.config.server.wadl.disableWadl=true
    
This property can be setup in a `web.xml` if the Jersey application is deployed in the servlet with `web.xml` or the
property can be returned from the `Application.getProperties()`. See
[Deployment chapter](https://qubitpi.github.io/jersey-guide/2020/07/26/4-application-deployment-and-runtime-environments.html)
for more information on setting the application configuration properties in various deployments.

WADL support in Jersey is implemented via `ModelProcessor` extension. This implementation enhances the application
resource model by adding the WADL providing resources. WADL `ModelProcessor` priority value is high (i.e. the priority
is low) as it should be executed as one of the last model processors. Therefore, any `ModelProcessor` executed before
will not see WADL extensions in the resource model. WADL handling resource model extensions (resources and `OPTIONS`
resource methods) are not added to the application resource model if there is already a matching resource or a resource
method detected in the model. In other words, if you define for example your own `OPTIONS` method that would produce
`"application.wadl"` response content, this method will not be overridden by WADL model processor. See
[Resource builder chapter](https://qubitpi.github.io/jersey-guide/2020/08/05/14-programmatic-api-for-building-resources.html)
for more information on `ModelProcessor` extension mechanism. 