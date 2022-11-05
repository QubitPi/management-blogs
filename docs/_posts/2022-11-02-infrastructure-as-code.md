---
layout: post
title: Infrastructure as Code (IaC)
tags: [IaC, CHEF]
category: FINALIZED
color: rgb(218, 25, 132)
feature-img: "assets/img/post-cover/33-cover.png"
thumbnail: "assets/img/post-cover/33-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Infrastructure as code (IaC) is the process of managing and provisioning computer data centers through machine-readable 
definition files, rather than physical hardware configuration or interactive configuration tools. The IT infrastructure 
managed by this process comprises both physical equipment, such as bare-metal servers, as well as virtual machines, and 
configuration resources. The definitions may be in a version control system. The code in the definition files may use 
either scripts or declarative definitions, rather than maintaining the code through manual processes, but IaC more often 
employs declarative approaches.

<!--more-->

* TOC
{:toc}


The Need for Automation
-----------------------

Automation is the act of building a process that will operate without human intervention. It means automating the 
configuration and management of cloud-based or on-premises computing infrastructure.

### What is Infrastructure?

When we speak of infrastructure, we are referring to the **physical and/or virtual machines that run businesses**. For
example, a major retailer may require a large number of web servers, load balancers, and database servers to run their 
retail websites.

These machines may be located in an on-premises data center, or very often, as virtual machines in "the cloud" such as 
Amazon Web Services (AWS), Microsoft Azure, or Google Cloud Platform (GCP).

![Error loading chef-infrstrucure2.png]({{ "/assets/img/chef-infrstrucure2.png" | relative_url}})

**One thing all these virtual or real machines require is server management. Management such as installing and updating 
software, initial configuration, applying security measures, and periodic server content changes. Such management can be 
labor-intensive and tedious without automation.**

### What is Automation?

As mentioned above, automation is the act of building a process that will operate without human intervention. But what
does this mean in reality?

It's about creating a system that will take care of repetitive tasks, with consistency and reliability.  It is not about 
replacing human operators, but instead freeing their time to work on more complex problems that require intelligent 
insight, rather than simple rules.

In addition, the use of automation, and the promise of consistency and reliability helps provide trust in systems, which
in turn allows for greater innovation across the company.

### Infrastructure Automation

Infrastructure Automation refers to ensuring every system is configured correctly and consistently in any cloud, VM,
and/or physical infrastructure, in an automated fashion.


Chef
----

Chef was founded in 2008. Its first product was called _Chef_ (now called _Chef Infra_) which is a set of tools that
automate the configuration of our cloud-based or on-prem server infrastructure.

Chef can automate how we build, deploy, and manage our infrastructure. For example, say a large retailer needs to deploy 
and configure 50 servers for an upcoming sale. They could use Chef Infra to automate that infrastructure deployment.

> In October 2020, Chef Software was acquired by Progress Software and Chef Software operates as a business unit of 
> Progress Software. Chef Software has been and still is a leader in DevOps and DevSecOps.

Chef Software users consist of hundreds of small businesses to large enterprises:

![Error loading chef-customers2.png]({{ "/assets/img/chef-customers2.png" | relative_url}})

With Chef, [infrastructure automation](#infrastructure-automation) typically begins with using the Chef Infra and its
tools.

In the example below, the user is uploading a set of Chef cookbooks to the Chef Infra server. We can think of Chef
cookbooks as a set of configuration files, called **recipes**, that will instantiate, configure, and maintain our
infrastructure nodes in an automated fashion. (A node is a physical or virtual machine.)

![Error loading chefinfra.png]({{ "/assets/img/chefinfra.png" | relative_url}})

The Chef Infra server in turn loads those cookbooks to the correct nodes. Chef Infra can do this on a massive scale
thus eliminating the tedious task of manually configuring your infrastructure nodes individually.

The [Chef Infra](https://github.com/chef/chef) is an open source technology that uses Ruby to develop basic building
blocks like Chef recipes and cookbooks that configure and maintain our infrastructure. Chef Infra helps in **reducing
manual and repetitive tasks for infrastructure management**.

Here is an example of a Chef Infra recipe. This little bit of code, when applied to a node in our infrastructure, will:

* install an Apache web server package (httpd)
* create a file on that node called "/var/www/html/index.html"
* enable and start the Apache web server

```
package 'httpd'

template '/var/www/html/index.html' do
  source 'index.html.erb'
end

service 'httpd' do
  action [:enable, :start]
end
```

Chef also works as a tool for implementing better DevOps. DevOps is a set of practices that combines software
development (Dev) and IT operations (Ops). DevOps is a fundamental aspect of collaboration between engineering and IT 
operations to deploy better code, faster, in an **automated** manner. DevOps helps to improve an organization's velocity 
to deliver apps and services. It's all about alignment -- alignment of engineering and IT ops via improved collaboration 
and communication.

![Error loading devops-lifecycle.png]({{ "/assets/img/devops-lifecycle.png" | relative_url}})

Open Source Software at Chef are

* [**Chef Infra**](https://community.chef.io/tools/chef-infra/), a powerful automation platform that transforms 
  infrastructure configuration into code. Whether we're operating in the cloud, on-premises, or in a hybrid environment, 
  Chef Infra _automates how infrastructure is configured, deployed, and managed across our network_.

  At the heart of this tool is the [Chef Infra Client](#chef-infra-client), which typically runs as an agent on the 
  systems managed by Chef. The client runs Chef libraries called [Cookbooks](#cookbooks), which declare the desired
  state of our system using infrastructure-as-code. The client then ensures our system is inline with the declared
  policy.
* [**Chef InSpec**](https://community.chef.io/tools/chef-inspec), which provides a language for describing security and 
  compliance rules that can be shared between software engineers, operators, and security engineers.

  > **Compliance Automation**
  >
  > Compliance automation refers to automatically ensuring our infrastructure complies with security standards set by 
  > authorities such as the [Center for Internet Security](https://www.cisecurity.org/) (CIS). Compliance automation
  > helps ensure our infrastructure is protected from malicious intrusions and other security issues.
  > 
  > ![Error loading chef-automateui.png]({{ "/assets/img/chef-automateui.png" | relative_url}})
  > 
  > The Chef Compliance solution can automatically scan our infrastructure to identify and report security compliance 
  > issues. Then we can use the Chef Infra to remediate such security issues.
  > 
  > Chef Compliance uses the Chef InSpec language to create and run compliance profiles, which contain the logic to scan 
  > for security issues.
  > 
  > Here is an example of the Chef InSpec language that tests a node for security compliance. In this example, InSpec is 
  > testing the node to ensure the ssh_config protocol should be 2. If the actual value from the node is not protocol 2,
  > a critical issue is reported and can be displayed in the Chef Automate UI shown above
  > 
  > ![Error loading chef-inspec.png]({{ "/assets/img/chef-inspec.png" | relative_url}})

  InSpec is a language used to declare security requirements, or tests, called "controls" that are packaged into groups 
  called [profiles](#profiles). These profiles can be used to describe the requirements for all the environments that
  need to be audited on a regular basis, such as production systems running business-critical applications.

* **Chef Habitat**, an open source automation solution for defining, packaging, and delivering applications to almost any environment regardless of operating system or platform.

  > **Application Automation**
  >
  > Application Automation refers to defining, packaging and delivering applications to almost any environment
  > regardless of operating system or deployment platform. At Chef, the Chef Habitat solution enables DevOps and 
  > application teams to:
  > 
  > * Build continuous delivery pipelines across all applications and all change events
  > * Create artifacts that can be deployed on-demand to bare-metal, VMs, or containers without any rewriting or
  >   refactoring
  > * Scale the adoption of agile delivery practices across development and operations

  With Chef Habitat, an application that is built and run in development will be exactly the same as what's deployed in 
  production environments. This is accomplished by declaring the build and runtime instructions for the application in a 
  Habitat Plan file. The application is then built in a cleanroom environment that bundles the application alongside its 
  deployment instructions into a single deployable Habitat artifact file (.HART). This artifact is then deployed by the 
  Habitat Supervisor, which monitors the application lifecycle, including deploying runtime configuration updates
  without having the rebuild the application.

  Habitat allows for application automation to live alongside the app's source code. This reduces misunderstandings 
  between developers and operators about how an app is built or deployed, since these groups are using the same 
  source-of-truth to define how an app works.

* **Chef Workstation** bundles together all the common software needed when building automation instructions for tools like Chef Infra and Chef InSpec. It also includes common debugging and testing tools for all our automation code. Chef Workstation includes:

  - _The Chef Workstation App_
  - _Chef Infra Client_
  - _Chef InSpec_
  - _Chef Command Line Tool_, which allows you to apply dynamic, repeatable configurations to your servers directly over
    SSH or WinRM via chef-run. This provides a quick way to apply config changes to the systems we manage whether or not 
    they're being actively managed by Chef Infra, without requiring any pre-installed software.
  - _Test Kitchen_, which can test cookbooks across any combination of platforms and test suites before we deploy those 
    cookbooks to actual infrastructure nodes
  - _Cookstyle_, which is a code linting tool that helps us write better Chef Infra cookbooks by detecting and automatically correcting style, syntax, and logic mistakes in our code
  - Plus various Test Kitchen and Knife plugins

* **Chef Automate**, an enterprise visibility and metrics tool that provides actionable insights for any systems that
  we manage using Chef tools and products. The dashboard and analytics tool enables cross-team collaboration with 
  actionable insights for configuration and compliance, such as a history of changes to environments to make audits
  simple and reliable. Chef Automate can be used with a number of Chef Software products and solutions, and segregates 
  information into separate dashboards for quick access and filtering.

  The goal of Chef Automate is to make infrastructure management, application delivery and continuous compliance
  realities by enabling cross-team collaboration using a single source-of-truth. All Chef OSS tools like Infra, InSpec
  and Habitat can be configured to report into Chef Automate to provide a window into the status and health of every 
  application and system in your organization.

  ![Error loading chef-automatenodes.png]({{ "/assets/img/chef-automatenodes.png" | relative_url}})

### Ruby Essentials

Ruby is a simple programming language. Chef uses Ruby as its reference language to define the patterns that are found in 
resources, recipes, and cookbooks. Chef also uses these patterns to configure, deploy, and manage nodes across the
network.

#### Instally Ruby

##### Mac OS

Ruby comes pre-installed on macOS. However, pre-installed Ruby might be a few versions behind. The latest version can be 
installed using a package manager like Homebrew, making it easy to install Ruby. Just run the following command.

```bash
brew install ruby
```

##### Linux

Linux and Ubuntu use the apt package manager for installation. Run the following command in the terminal to install
Ruby.

```bash
sudo apt-get install ruby-full
```

#### Ruby Syntax

##### Control Flow

Ruby's `if` statement takes an expression and executes the code based on the evaluation of that expression. If the 
expression evaluates to `true`, Ruby executes the block of code following the `if` statement. If the expression
evaluates to `false`, then it doesn't execute the code. For example,

```ruby
x = 10
if x > 7
 puts "x is greater than 7"
end
```

The `else` statement is the partner of the `if` statement. If the expression evaluates to `true`, then the statement 
following the condition is executed. If the expression evaluates to `false`, then the statement following the else 
statement is executed.

```ruby
x = 10

if x < 7
 puts "x is less than 7"
else
 puts "x is greater than 7"
end
```

The `if…else` structure keeps us restricted to two options. What if we want to have more options in our program? Here, 
`elsif` comes to the rescue and allows us to add alternatives to the traditional `if…else`.

```ruby
x = 10

if x < 7
 puts "x is less than 7"
elsif x > 7
 puts "x is greater than 7"
else
 puts "x is equal to 7"
end
```

Sometimes, instead of checking whether an expression is true, we are more interested in knowing if a condition is false 
and executing a block of code. Ruby allows us such program control using **unless**. For example

```ruby
playing = false

unless playing
 puts "We're busy learning Ruby"
else
 puts "It's time to play games"
end
```

The switch statement is a selective control flow statement. It allows us to easily control the flow of the code when an 
expression will result in one of a few anticipated values. Observe the following example.

```ruby
num = 0

case num
when 0
 puts "Zero"
when 1
 puts "One"
when 2
 puts "Two"
else
 puts "The entered number is greater than 2"
end
```

##### Logical Operators

|            |                                                                                            |
|:----------:|:------------------------------------------------------------------------------------------:|
| `&&` (and) | A condition using the `&&` operator evaluates to `true` if both operands are `true`.       |
| `||` (or)  | A condition using the `||` operator evaluates to `true` if any of the operands are `true`. |
| `!` (not)  | The `!` operator reverses the state of a single operand.                                   |

##### Loops

A `while` loop checks to see if the specified condition is `true`, and while it is, the loop keeps running. As soon as
the condition resolves to `false`, the loop stops.

```ruby
count = 1

while count < 10
 puts count
 count = count + 1
end
```

The **until** loop works similarly to the `while` loop, except it will run while the condition is `false` and stop if
the condition evaluates to `true`. For example:

```ruby
count = 6

until count > 10
 puts count
 count = count + 1
end
```

When we already know the number of times we want the loop to execute, we use a `for` loop:

```ruby
for count in 1...10
 puts count
end
```

> ⚠️ The example above uses **three-dot form** which creates a range that excludes the specified high value. For
> example, 
> 
> ```ruby
> for count in 1...10
>  puts count
> end
> ```
> 
> The snippet above produces the sequence 1 to 9.
> 
> The **double-dot form** includes the specified high value. The following example prints 1 to 10 on the screen:
> 
> ```ruby
> for count in 1..10
>  puts count
> end
> ```

Like loops, iterators are methods that loop over a given set of data and perform a specified operation on each item. For 
example, let's say we want to print the string “I am Learning Ruby” on the screen five times. Sure, we can use a for
loop, but we can also achieve the same functionality using the **times iterator**:

```ruby
5.times { puts "I am Learning Ruby!" }
```

Along with the times iterator, we also have the **each iterator**. Let's say that we have an array containing the days of 
the week and we want to print them to the screen. In order to do that, we can use an each iterator.

```ruby
Terminal: ~ - irb
days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']

days.each { |day| puts day }
```

##### Arrays

We can declare items of the array by enclosing them in square brackets while separating the items using commas. For
example:

```ruby
months = ["January", "February", "March", "April", "May", "June", "July"]
```

We can refer to each item by referring to its zero-based index.

```ruby
puts months[6]
```

To **add** more items to the array, we can use the `<<` operator or `push`.

```ruby
months << "August"
months.push("September")
```

To inset an element at a desired position, we can use the `insert` method:

```ruby
months.insert(2, "October")
```

The **pop** method will remove the last item from the array.

```ruby
months.pop
```

The `delete_at` method will remove the item at the specified index position.

```ruby
months.delete_at(2)
```

To create nested arrays:

The **include?** method checks to see if the given argument is an element of the array. For example:

```ruby
my_array = [5, 9, 8, 2, 6]
puts my_array.include?(0) # returns false
puts my_array.include?(2) # returns true
```

When called on an array, the **sort** method will return a sorted array. For example:

```ruby
my_array = [5, 9, 8, 2, 6]
print my_array.sort
# results in my_array = [2, 5, 6, 8, 9]
```

The flatten method takes nested arrays and returns a single dimensional array.

```ruby
my_array = [5, 9, [8, 2, 6], [1, 0]]
print my_array.flatten
# results in my_array = [5, 9, 8, 2, 6, 1, 0]
```

The `map` method invokes the code inside the block once for each element in the array and will create and return a new 
array that contains the values returned by the block. For example:

```ruby
my_array = [5, 9, 8, 2, 6]
print my_array.map { |item| item*2}
# results in my_array = [10, 18, 16, 4, 12]
```

##### Hash

In a hash, each item is stored with an associated key, which can be any object, and we can refer to the item as a 
key-value pair.

```ruby
person_hash = {
  "name" => "Jonathan",
  "age" => 25
 }
puts person_hash
```

Another way of creating a hash is using the keyword `new`.

```ruby
Person_hash = Hash.new
```

This will create an empty hash to which we can add values later on.

We can easily access hash values. For example:

```ruby
puts person_hash["name"]
puts person_hash["age"]
```

We can easily add to an existing hash by specifying a key-value pair.

```ruby
person_hash["gender"] = 'male'
```

We can use the **delete** function to remove items from hash.

```ruby
person_hash.delete("gender")
puts person_hash
```

To iterate over hashes

```ruby
person_hash.each do |key, value|
 puts "#{key} is #{value}"
end
```

The **has_key?** method is used to check if a hash contains a specific key and returns true if found. For example:

```ruby
puts person_hash.has_key?("name")
puts person_hash.has_key?("height")
```

The **select** method is usually used with a block and returns any key-value pairs that satisfy the condition in the
block

```ruby
puts person_hash.select{ |key, value| key == "name"}
```

##### Sets

In Ruby, sets are collections of unique elements. The order of the elements doesn't matter, so they can't be reliably 
referenced by an index. Sets are useful when we want to ensure there are no duplicate items.

Unlike any other collection, we need to add a `require` statement before making use of the Set class. The `require`
method is used to import all class and method definitions of the class. After that, we can create a set instance simply 
using the `new` keyword. For example:

```ruby
require 'set'
my_set = Set.new
```

You can also pass an array to the new method to create a set.

```ruby
my_set = Set.new([5, 2, 9, 3, 1])
```

We can use the `<<` operator to add values to the set. Unlike arrays, we use the `add` method instead of `push`.

```ruby
my_set = Set.new
my_set << 5
my_set.add 1
```

##### Function

A function is a set of statements that achieves a specific goal or performs a specific task. Ruby allows us to define
our own functions using the keyword `def`. For example:

```ruby
def greetings_with_name(name="Emily")
 puts "Hello #{name}!"
end
```

You can define methods that take any number of arguments.

```ruby
def optional_arguments(*a)
 puts a
end

optional_arguments("Hello", "World", 2021)
```

To define a function that will return the product of two variables:

```ruby
def prod(x, y)
 return x * y
end
```

Ruby also allows **implicit returns**. If a function lacks an explicit return statement, then Ruby will return the value 
of the **last** executed instruction. For example

```ruby
def prod(x, y)
 x*y
end

puts prod(2, 5)
```

###### Yield

Using **yield** inside a method will allow us to call the method with a block of code that will be inserted in place of 
the `yield` keyword. In other words, when the method gets to the `yield` keyword, it executes the block passed to the 
method, then continues with any code after the yield keyword. Once the block is finished executing, it will return to
the code in the method. The following example will make the idea clear.

```ruby
# defining a method using yield
def yielding_test
 puts "We're now in the method!"
 yield
 puts "We're back in the method!"
end

# calling the method with the block
yielding_test { puts "The method has yielded to the block and We're in the block now!" }
```

When the above code executes, you will see the following on the screen.

```
We're now in the method!
The method has yielded to the block and We're in the block now!
We're back in the method!
```

We can also pass parameters to yield. Let's see another example.

```ruby
def yield_greetings(name)
 puts "We're now in the method!"
 yield("Emily")
 puts "In between the yields!"
 yield(name)
 puts "We're back in method."
end

yield_greetings("Erick") { |n| puts "Hello #{n}." }
```

##### Classes

Just like objects in the real world, objects in programming are independent units with their own identity. For example,
an apple is an object that has its own unique identity. There could be multiple objects belonging to the same category. 
For example, apples could be green or red, but they still belong to the same apple category. This category could be 
referred to as a class.

A class contains the data and actions associated with the object. In simple words, a class is like a blueprint for an 
object. Just like we can use blueprints to construct multiple buildings in the real world, we can use classes as a 
blueprint to create multiple objects in programming.

We can define classes in Ruby by using the keyword **class** followed by the name of the class. Convention is that the 
name of the class should start with a capital letter. For example:

```ruby
class Car
 def initialize
  puts "The object is now created"
 end
end
```

You can now create objects from class `Car` using the keyword `new` as follows:

```ruby
car = Car.new
```

We can also initialize the objects with some attributes. These attributes can also be called instance variables. Each 
object of the class will have a separate copy of the instance variable.

Instance variables are preceded by **@**. For example, we can pass a parameter to the previously created initialize
method and assign its value to an instance variable as follows:

```ruby
class Car
 def initialize(brand)
  @brand = brand
 end
end
```

We can now create objects of the `Car` class by using the same `new` method, with the small difference of passing an 
argument to it.

```ruby
car = Car.new("Audi")
```

###### Instance Methods and Class Methods

Ruby has two types of methods

1. instance methods, and
2. class methods

A class method is supposed to provide functionality to the class itself and cannot be called directly on an instance, 
whereas an instance method provides functionality to the instance of the class and cannot be called on the class itself.

For example

```ruby
class Greetings
 def self.class_greetings
  puts "Hello, I'm a class method"
 end

 def instance_greetings
  puts "Hello, I'm an instance method"
 end
end
```

The Greetings class defines a class method, `self.class_greetings`, and an instance method, `instance_greetings`.

###### Instance Variables and Class Variables

Instance variables belong to the objects of the class and each object will have a separate copy of the instance
variables. On the other hand, class variables are accessible to all the objects of the class since it belongs to the
class and not a particular object.

We can declare a class variable with the prefix **@@**. For example:

```ruby
class Car
 @@count = 0
 def initialize
  @@count += 1
 end
 def self.get_instance_count
  @@count
 end
end
```

###### Mixins

It would be advantageous if we could inherit functionality from multiple places. Ruby provides this functionality using 
**mixins**. A mixin is simply a set of code wrapped in a module that can be added to one or more classes to add to its 
functionality. Once we 'mixin' a module into a class, the class can access all the methods of the module. For example

```ruby
module Greetings
 def say_hello
  puts "Hello!"
 end
end
```

Here we defined a simple module called `Greetings`. The module contains a single method which outputs "Hello!" to the 
screen. Let's include this module in a class.

```ruby
class Person
 include Greetings
end
```

The `Person` class will now access the module's method as if it were its own instance method, as made clear from the 
following.

```ruby
p1 = Person.new
p1.say_hello
# outputs Hello!
```

> ⚠️ In the example above, we can only access the module method as an _instance method_. If we try to call the method as
> a class method then we will get an error.

In order to access module methods as _class methods_, we would use **extend** keyword:

```ruby
class Person
 extend Greetings
end
```

Now we can call the module method as a class method. For example:

```ruby
Person.say_hello
# outputs Hello!
```

##### Reading from the Console

Reading from the console is a way to get user input. Ruby has a **gets** method as a companion of puts and is used to
read data from the console. For example:

```ruby
puts "what is your name?"
# getting user input and storing it into a variable i.e. name
name = gets
puts "Hello #{name}"
```

The code above will first prompt the user for their name. It then stores the entered string into a variable called
`name`, which can be used later to print a simple message to the console.

##### Creating a File

Ruby allows us to create and work with files using its built-in **File** class. Here's an example:

```ruby
test_file = File.new("test.txt", "w+")
```

The code above will create a text file named "test.txt". Specifying `w+` mode will give us read and write access. Before 
moving forward, let's take a look at different file modes.

|    |                                                                                                                                           |
|----|-------------------------------------------------------------------------------------------------------------------------------------------|
| r  | This is the default mode for files in Ruby. It provides read-only access and starts reading the file at the beginning                     |
| r+ | Specifying this mode provides read and write access and it also starts at the beginning of the file                                       |
| w  | This mode provides write-only access and specifying this mode will truncate the existing file and create a new file for writing           |
| w+ | This mode provides both read and write access but it truncates the existing file and overwrites the existing file for reading and writing |
| a  | This mode is write only and specifying this mode will append to the end of the file for writing                                           |
| a+ | This mode also provides both read and write access but it appends or reads from the end of the file                                       |

To open an existing file, use the **open** method of `File` class:

```ruby
test_file = File.open("test.txt", "w+")
```

Reading from the file is also as simple as a method call.

```ruby
File.read("test.txt")
```

Ruby allows us to write to files using either **puts** or **write**. The only difference between them is that _puts_
adds a line break to the end of the string to be added while `write` does not.

```ruby
test_file = File.open("test.txt", "w+")
test_file.puts("We're writing some text to file")
test_file.close
```

or

```ruby
File.open("test.txt", "w+") {
 |file| file.puts("This text was added using code block")
}
```

> 💡 We don't need to worry about closing the file here since Ruby will automatically close the file for us.

##### Sending HTTP Requests

Ruby comes with a built-in HTTP client **net/http** that can be used to send any kind of HTTP request we may need. We
need to require the net/http client to work with it. For example:

```ruby
require 'net/http'
http_response = Net::HTTP.get_response('www.example.com' , '/')
````

The code above will return a string with the HTML content of the specified URL. Most of the time we aren't interested in 
HTML content, but rather something simpler, such as whether the connection was successful. This can be done by checking 
the HTTP response status. For example:

```ruby
require 'net/http'
http_response = Net::HTTP.get_response('www.google.com', '/')
puts http_response.code
# 200
```

We can also get the body of the response using the **body** method.

```ruby
puts http_response.body
```

##### Parsing HTTP Response

To parse received data to JSON or XML format:

```ruby
require 'net/http'

require 'uri'
require 'json'

uri = URI('http://www.example.com/sample.json')
response = Net::HTTP.get(uri)
json_response = JSON(response)
```

Here, we are requiring two other modules, **uri** and **json**. The uri module provides classes to handle Uniform
Resource Identifiers (a string of characters that unambiguously identifies a particular resource, like a URL). The json 
module is used to parse a JSON string received by another application or generated within your existing application.

There are no less than 25 HTTP clients that [Ruby Toolbox](https://www.ruby-toolbox.com/) offers. We just barely
scratched the surface. We would encourage you to experiment with other HTTP clients as well and pick the one that suits 
you best.

##### Receiving HTTP Requests

To receive HTTP requests, we need a way to listen to HTTP requests on a network port. For this purpose, we can write our 
own HTTP server or choose one from Ruby Toolbox. To keep it simple, let's use **WEBrick**, which is included in the 
standard Ruby library and doesn't require any extra installation.

```ruby
require 'webrick'

# specifying port to listen to HTTP requests
http_server = WEBrick::HTTPServer.new(:Port => 3000)

# inheriting the functionality of WEBrick HTTPServlet
class MyHTTPServlet < WEBrick::HTTPServlet::AbstractServlet
  # outputs the requested path
 def do_GET(http_request, http_response)
  http_response.body = "You requested '#{http_request.path}'"
 end
end

http_server.mount('', MyHTTPServlet)
# stop the server using Ctrl-C
trap('INT') {http_server.shutdown}
http_server.start
```

Once the server starts running, we can make requests to the server. For instance,

```bash
curl localhost:3000
# You requested '/'
```

### Chef Infra

#### Getting Started

##### Install Workstation

In order to define the policies we want enforced in our infrastructure, you'll use the Chef language. _Chef Workstation
is a downloadable package that gives us access to the Chef language_, as well as a number of other useful development
and testing tools

Chef Workstation installs to **/opt/chef-workstation/** on MacOS/Linux and **C:\opscode\chef-workstation\** on Windows. 
These file locations help avoid interference between these components and other applications that may be running on the 
target machine.

###### MacOS Install

1. Visit the [Chef Workstation downloads page](https://www.chef.io/downloads/tools/workstation) and select the
   appropriate package for our MacOS version. Hit on the **Download** button.
2. Follow the steps to accept the license and install Chef Workstation.

Alternately, install Chef Workstation using the [Homebrew](https://brew.sh/) package manager:

```bash
brew install --cask chef-workstation
```

From command prompt, ensure that we can access Chef Workstation by running `chef --version` to verify the version.

```
$ chef --version
Chef Workstation version: 0.17.5
Chef Infra Client version: 15.8.23
Chef InSpec version: 4.18.100
Chef CLI version: 2.0.0
Test Kitchen version: 2.4.0
Cookstyle version: 5.22.6
```

##### Setup local Virtualization

When we use Chef to write code that defines our infrastructure, the code needs to be tested, just like a regular
software. For that, Chef allows us to setup a test environment when we have VirualBox and Vagrant installed on our
local machine

###### Install VirtualBox

We can [download VirtualBox](https://www.virtualbox.org/wiki/Downloads) from the Oracle website and install in a
regular way. After that, run the following command to verify VirtualBox is installed.

```bash
$ VBoxManage --version
6.1.6r137129
```

###### Install Vagrant

Similarly, we can [download Vagrant](https://www.vagrantup.com/downloads.html) from the HashiCorp website and run the 
following to verify that Vagrant is installed.

```bash
$ vagrant --version
Vagrant 2.2.8
```

##### Create a Cookbook

We will use a Chef Workstation command called **chef generate** to create the minimum file structure needed to create a 
default testing instance. Run the command **chef generate cookbook learn_chef** from command line.

```bash
$ chef generate cookbook learn_chef
Generating cookbook learn_chef
- Ensuring correct cookbook content
- Committing cookbook files to git

Your cookbook is ready. Type `cd learn_chef` to enter it.

There are several commands you can run to get started locally developing and testing your cookbook.
Type `delivery local --help` to see a full list of local testing commands.

Why not start by writing an InSpec test? Tests for the default recipe are stored at:

test/integration/default/default_test.rb

If you'd prefer to dive right in, the default recipe can be found at:

recipes/default.rb
```

Let's first look at a file in the newly created "learn_chef" directory called **kitchen.yml**. Change directories into 
"path/to/learn_chef" and view the contents of "kitchen.yml". Inside this file, we'll find several key-value pairs:

* **platforms** - The operating system(s) or target environment(s) on which our policies are to be tested. eg: Windows, 
  Ubuntu, CentOS, RHEL
* **suites** - The policies and code which will be enforced on the test instance(s).
* **driver** - The lifecycle manager responsible for implementing the instance-specific actions (in this case, Vagrant); 
  these actions can include creating, destroying, and installing the tools necessary to test our code on the test 
  instance(s).
* **provisioner** - The tool responsible for executing the suites against the test instance(s). Since we'll be learning 
  the Chef language, we'll use Chef's Test Kitchen provisioner, [chef-solo](https://docs.chef.io/chef_solo/).

```yaml
---
driver:
  name: vagrant

## The forwarded_port port feature lets you connect to ports on the VM guest via
## localhost on the host.
## see also: https://www.vagrantup.com/docs/networking/forwarded_ports.html

#  network:
#    - ["forwarded_port", {guest: 80, host: 8080}]

provisioner:
  name: chef_zero

  ## product_name and product_version specifies a specific Chef product and version to install.
  ## see the Chef documentation for more details: https://docs.chef.io/config_yml_kitchen.html
  #  product_name: chef
  #  product_version: 15

verifier:
  name: inspec

platforms:
  - name: ubuntu-18.04
  - name: centos-7

suites:
  - name: default
    verifier:
      inspec_tests:
        - test/integration/default
    attributes:
```

This file instructs Test Kitchen to use Vagrant to create two instances, one Ubuntu and one CentOS, and then to use Chef 
Infra to provision the test instances. On our command line, run the command **`kitchen list`**. This command, in
addition to validating that our "kitchen.yml" is typo-free, will list the information for each instance in our test 
environment.

```bash
$ kitchen list
Instance             Driver   Provisioner  Verifier  Transport  Last Action    Last Error
default-ubuntu-1804  Vagrant  ChefInfra    Inspec    Ssh
default-centos-7     Vagrant  ChefInfra    Inspec    Ssh
```

Finally, create our test instances by using the command **`kitchen create`**. This command will download an image of the 
appropriate operating system(s) and deploy it against our test instances; it may take a few minutes to fully complete.

```bash
$ kitchen create
-----> Starting Test Kitchen (v2.4.0)
-----> Creating ...
       Bringing machine 'default' up with 'virtualbox' provider...
       ==> default: Box 'bento/ubuntu-18.04' could not be found. Attempting to find and install...
           default: Box Provider: virtualbox
           default: Box Version: >= 0
...
```

> 💡 **Troubleshooting** - If Executing `kitchen create` results in the following error:
>
> ```
> $ kitchen create
> -----> Starting Test Kitchen
> -----> Creating <default-ubuntu-...>...
>        Bringing machine 'default' up with 'virtualbox' provider...
>        ==> default: Checking if box 'bento/ubuntu' version '...' is up to date...
>        ==> default: Machine not provisioned because `--no-provision` is specified.
>        Waiting for SSH service on 127.0.0.1:2222, retrying in 3 seconds
>        Waiting for SSH service on 127.0.0.1:2222, retrying in 3 seconds
>        Waiting for SSH service on 127.0.0.1:2222, retrying in 3 seconds
>        Waiting for SSH service on 127.0.0.1:2222, retrying in 3 seconds
>        Waiting for SSH service on 127.0.0.1:2222, retrying in 3 seconds
>        Waiting for SSH service on 127.0.0.1:2222, retrying in 3 seconds
>        Waiting for SSH service on 127.0.0.1:2222, retrying in 3 seconds
>        Waiting for SSH service on 127.0.0.1:2222, retrying in 3 seconds
>        Waiting for SSH service on 127.0.0.1:2222, retrying in 3 seconds
>        ...
> ```
> 
> there are a couple of hints that could possibly resolve this:
> 
> 1. Try an older version of platform. For example, downgrade Ubuntu 18.04 to 16.04
> 2. The issue could have to do entirely with the version matrix of Vagrant, VirtualBox, and bento boxes in play.

Once Test Kitchen has created the test instances, we can log into the machine using **`kitchen login <INSTANCE>`**, 
replacing `<INSTANCE>` with the platform you wish to test, eg. centos or ubuntu. To log back out of the test instance, 
simply run `exit`.

```bash
kitchen login centos
Last login: Wed May  6 19:35:02 2020 from 10.0.2.2
[vagrant@default-centos-7 ~]$ exit
logout
Connection to 127.0.0.1 closed.
```

Now it's time to decide what policies we actually want to enforce and codify our goals.

##### Write Policy (Define Infrastructure)

If we approach our policies, i.e, our desired infrastructure state, the same way Test Kitchen approaches its 
configuration settings, then we can think of the various packages, services, files, and other desirable ingredients on 
our systems as key-value pairs as well. In the Chef language, these ingredients are called "**resources**"

> 📋 Remember that:
>
> * **Resources** are a statement of defined policy; in other words, they're the ingredients that make up whatever
>   recipe or set of policies we want to write or enforce. 
> * **Recipes** are a collection resources intended to accomplish a similar goal; for example, to configure an Apache or 
>   IIS web server.
> * **Cookbooks** are a collection of related recipes; like a cookbook used in a real kitchen, they not only contain 
>   related recipes, but also which attributes can be manipulated, and information about how or why the recipes relate, 
>   when they were written and last updated, and who wrote or maintains them.

To store our policy, or _Chef code_, we'll create another YAML file and list the different resources we want configured 
on our system. This YAML file is called a "**recipe**" in the Chef language because it will eventually contain the 
ingredients and instructions necessary to configure a server to our standards.

We may have noticed when `chef generate` ran, it created a subdirectory called "/recipes", in which we will find a file 
called **default.rb**. When Test Kitchen installs our Chef code on the test instance, it will look to this default.rb 
first to determine how to configure the instance.

##### Deploy Policy

Finally, deploy the recipe using the Test Kitchen command **kitchen converge**. Be sure we are at
/path/to/learn_chef directory before runing this kitchen command.

```bash
$ kitchen converge
-----> Starting Test Kitchen (v2.4.0)
-----> Converging ...
       Preparing files for transfer
       Installing cookbooks for Policyfile /Users/cheftv/learn-chef-infra/learn_chef/Policyfile.rb using `chef install`
       Installing cookbooks from lock
       Installing learn_chef 0.1.0
       Preparing dna.json
...

Finished converging  (0m12.71s).
-----> Test Kitchen is finished. (0m45.63s)
```

When Test Kitchen finishes, we can use `kitchen login <INSTANCE>` to verify the state of our server.

#### Overview

So far we have a rough idea that Chef Infra is a powerful automation platform that transforms infrastructure into code. 
Whether we are operating in the cloud, on-premises, or in a hybrid environment, Chef Infra automates how infrastructure 
is configured, deployed, and managed across your network, no matter its size.

This diagram shows how we develop, test, and deploy your Chef Infra code.

![Error loading start-chef.svg]({{ "/assets/img/start-chef.svg" | relative_url}})

* **Chef Workstation** is the location where users interact with Chef Infra. With Chef Workstation, users can _author_ 
  and _test cookbooks_ using tools such as Test Kitchen and interact with the Chef Infra Server using the knife and chef 
  command line tools.
* **Chef Infra Client** Chef Infra Client runs on systems that are managed by Chef Infra. The Chef Infra Client executes 
  on a schedule to configure a system to the desired state.
* **Chef Infra Server** acts as a hub for configuration data. Chef Infra Server stores cookbooks, the policies that are 
  applied to nodes, and metadata that describes each registered node that is being managed by Chef. Nodes use the Chef 
  Infra Client to ask the Chef Infra Server for configuration details, such as recipes, templates, and file
  distributions.


##### Chef Infra Components

![Error loading chef-overview-2020.svg]({{ "/assets/img/chef-overview-2020.svg" | relative_url}})

> Note that in the diagram above, "Chef Client" is the same thing as "Chef Infra Client"

###### Cookbooks

A cookbook is the fundamental unit of configuration and policy distribution in Chef. A cookbook defines a scenario and 
contains everything that is required to support that scenario:

* Recipesthat specify which Chef built-in resources to use, as well as the order in which they are to be applied

  A recipe is the most fundamental configuration element within the organization. **A** recipe:

  - Is authored using Ruby, which is a programming language designed to read and behave in a predictable manner
  - Is mostly a collection of [resources](#resources), defined using patterns (resource names, attribute-value pairs,
    and actions); helper code is added around this using Ruby, when needed
  - Must define everything that is required to configure **part of** a system
  - Must be stored in a cookbook
  - May be included in another recipe
  - May have a dependency on one (or more) recipes
  - May use the results of a search query and read the contents of a data bag (including an encrypted data bag)
  - Must be added to a run-list before it can be used by Chef Infra Client
  - Is always executed in the same order as listed in a run-list

  The Chef Client will run a recipe only when asked. When the Chef Client runs the same recipe more than once, the 
  results will be the same system state each time. When a recipe is run against a system, but nothing has changed on 
  either the system or in the recipe, the Chef Infra Client will not change anything.

  The Chef Language is a comprehensive systems configuration language with resources and helpers for configuring 
  operating systems. The language is primarily used in Chef recipes and custom resources to tell the Chef Client what 
  action(s) to take to configure a system. The Chef Language provides resources for system-level components such as 
  packages, users, or firewalls, and it also includes helpers to allow us to make configuration decisions based on 
  operating systems, clouds, virtualization hypervisors, and more.

* Attribute values, which allow _environment-based_ configurations such as **dev** or **production**.

  An attribute can be defined in a cookbook (or a recipe) and then used to override the default settings on a node. When 
  a cookbook is loaded during a Chef Client run, these attributes are compared to the attributes that are already present 
  on the node. Attributes that are defined in attribute files are first loaded according to cookbook order. For each 
  cookbook, attributes in the _default.rb_ file are loaded first, and then additional attribute files (if present) are 
  loaded in **lexical** sort order. When the cookbook attributes take precedence over the default attributes, Chef  
  Client applies those new settings and values during a Chef Client run on the node.

* [Custom Resources](#custom-resources) for extending Chef beyond the [built-in resources](#resources). A resource is a 
  statement of configuration policy that:

  - Describes the desired state for a configuration item 
  - Declares the steps needed to bring that item to the desired state 
  - Specifies a resource type - such as package, template, or service 
  - Lists additional details (also known as resource properties), as necessary 
  - Are grouped into recipes, which describe working configurations


* Files and Templates for distributing information to systems.
* Custom Ohai Plugins for extending system configuration collection beyond the Ohai defaults.
* The "metadata.rb" file, which describes the cookbook itself and any dependencies it may have. Every cookbook requires
  a small amount of metadata. The contents of the "metadata.rb" file provides information that helps Chef Client and 
  Server correctly deploy cookbooks to each node. A "metadata.rb" file is located at the top level of a cookbook's 
  directory structure.

###### Policy

Policy maps business and operational requirements, process, and workflow to settings and objects stored on the Chef  
Server:

* Roles define server types, such as "web server" or "database server".
* Environments define process, such as "dev", “staging”, or "production"
* Certain types of data - passwords, user account data, and other sensitive items - can be placed in data bags, which
  are located in a secure sub-area on the Chef Server that can only be accessed by nodes that authenticate to the Chef  
  Server with the correct SSL certificates

#### chef-solo

**chef-solo** is a command that executes Chef Client in a way that does not require the Chef Server to converge 
cookbooks. chef-solo uses Chef Client's _Chef local mode_, and does not support the following functionality present in 
Chef Client/server configurations:

* Centralized distribution of cookbooks
* A centralized API that interacts with and integrates infrastructure components
* Authentication or authorization

chef-solo stores its node objects as JSON files on local disk. By default, chef-solo stores these files in a `nodes` 
folder in the same directory as `cookbooks` directory. We can control the location of this directory using the 
`node_path` value in configuration file.

#### Cookbooks

##### Recipes

###### Include Recipes

A recipe can include one (or more) recipes from cookbooks by using the **include_recipe** method. When a recipe is 
included, the resources found in that recipe will be inserted (in the same exact order) at the point where the 
`include_recipe` keyword is located.

As an example, the syntax for including a recipe is like this:

```ruby
include_recipe 'apache2::mod_ssl'
```

Multiple recipes can be included within a recipe. For example:

```ruby
include_recipe 'cookbook::setup'
include_recipe 'cookbook::install'
include_recipe 'cookbook::configure'
```

If a specific recipe is included more than once with the `include_recipe` method or elsewhere in the run_list directly, 
only the first instance is processed and subsequent inclusions are ignored.

To include recipes from the same cookbook, **include_recipe** uses the first part every time as the cookbook name. So we 
have to specify the cookbook - name + recipe name:

```ruby
include_recipe '::my-included-recipe'

# or

include_recipe 'my-cookbook-name::my-included-recipe
```

#### Resources

A resource is a statement of configuration policy that:

* Describes the desired state for a configuration item
* Declares the steps needed to bring that item to the desired state
* Specifies a resource type - such as package, template, or service
* Lists additional details (also known as resource properties), as necessary
* Are grouped into recipes, which describe working configurations

**A resource is a Ruby block** with 4 components:

1. a type
2. a name,
3. one (or more) properties (with values), and
4. one (or more) actions.

The syntax for a resource is like this:

```ruby
type 'name' do
  attribute 'value'
  action :type_of_action
end
```

_Every resource has its own set of actions and properties_. Most properties have default values. Some properties are 
available to all resources, for example those used to send notifications to other resources and guards that help ensure 
that some resources are idempotent.

For example, a resource that is used to install a tar.gz package for version 1.16.1 may look something like this:

```ruby
package 'tar' do
  version '1.16.1'
  action :install
end
```

All actions have a default value. Only non-default behaviors of actions and properties need to be specified. For
example, the package resource's default action is `:install` and the name of the package defaults to the name of the 
resource. Therefore, it is possible to write a resource block that installs the latest tar.gz package like this:

```ruby
package 'tar'
```

and a resource block that installs a tar.gz package for version 1.6.1 like this:

```ruby
package 'tar' do
  version '1.16.1'
end
```

In both cases, Chef Client will use the default action (`:install`) to install the tar package.

#### Policies

### Custom Resources

#### apt_update Resource

Use the **apt_update** resource to manage APT repository updates on Debian and Ubuntu platforms. The full syntax for all 
of the properties that are available to the **apt_update** resource is:

```ruby
apt_update 'name' do
  frequency      Integer # default value: 86400
  action         Symbol # defaults to :periodic if not specified
end
```

where:

* `apt_update` is the resource.
* `name` is the name given to the resource block.
* `action` identifies which steps Chef Client will take to bring the node into the desired state. The **apt_update** 
  resource has the following actions:

  - `:update` - Update the Apt repository at the start of a Chef Client run.
  - `:nothing` - This resource block does not act unless notified by another resource to take action. Once notified,
    this resource block either runs immediately or is queued up to run at the end of a Chef Client run.
  - `:periodic` - Update the Apt repository at the interval specified by the `frequency` property. (default)

* `frequency` (**Ruby Type**: Integer | **Default Value**: 86400) determines how frequently (in seconds) APT repository 
  updates are made. Use this property when the `:periodic` action is specified.

This resource can be **nameless**. Add the resource itself to a recipe to get the default behavior:

```ruby
apt_update
```

will behave the same as:

```ruby
apt_update 'update'
```

The following examples demonstrate various approaches for using the **apt_update** resource in recipes:

* Update the Apt repository at a specified interval:

  ```ruby
  apt_update 'all platforms' do
    frequency 86400
    action :periodic
  end
  ```

* Update the Apt repository at the start of a Chef Infra Client run:

  ```ruby
  apt_update 'update'
  ```
  
#### apt_package Resource

Use the **apt_package** resource to manage packages on Debian, Ubuntu, and other platforms that use the APT package
system.

> In many cases, it is better to use the package resource instead of this one. This is because when the package resource is used in a recipe, Chef Infra Client will use details that are collected by Ohai at the start of a Chef Infra Client run to determine the correct package application. Using the package resource allows a recipe to be authored in a way that allows it to be used across many platforms.

### Chef Workstation

#### chef-run (executable)

chef-run is a tool to execute ad-hoc tasks on one or more target nodes using Chef Client.

##### Running a Recipe

To run a full recipe, specify a recipe using its path:

```bash
chef-run host1 /path/to/recipe.rb
chef-run host1 recipe.rb
```

If the recipe is in a cookbook we can also specify that cookbook:

```bash
chef-run host1 /cookbooks/my_cookbook/recipes/default.rb
chef-run host1 /cookbooks/my_cookbook
```

If we specify the path to the cookbook, `chef-run` will execute the default recipe from the cookbook on the target node.

chef-run also supports looking up our cookbook in a local cookbook repository. Assuming we have our cookbook repository 
at "/cookbooks", run:

```bash
cd /cookbooks
chef-run host1 my_cookbook
chef-run host1 my_cookbook::non_default_recipe
```

chef-run reads our local Chef Workstation configuration file **~/.chef-workstation/config.toml** and Chef configuration 
file **~/.chef/config.rb**. It looks for cookbooks in the paths specified in both files. The configuration value is an 
array and looks something like this:

For `~/.chef-workstation/config.toml`:

```toml
[chef]
cookbook_repo_paths = [
  "/path/1",
  "/path/b"
]
```

and for `~/.chef/config.rb`:

```ruby
cookbook_path ['/path/1', '/path/b']
```

If we run `chef-run host1 my_cookbook` and the current directory does not have a cookbook named "my_cookbook", then 
chef-run searches the configured paths, with those configured in "~/.chef-workstation/config.toml" taking priority over 
those in "~/.chef/config.rb".

To specify the search paths as command line arguments instead of using a configuration file, use:

```bash
chef-run host1 my_cookbook --cookbook-repo-paths '/path/1,/path/b'
```

#### Test Kitchen

Use [Test Kitchen](https://kitchen.ci/) to automatically test cookbooks across any combination of platforms and test 
suites. Test suites are defined in a **kitchen.yml** file. See the
[configuration documentation](https://docs.chef.io/workstation/config_yml_kitchen/) for options and syntax information.

Test Kitchen use a comprehensive set of operating system base images from Chefs
[Bento project](https://github.com/chef/bento), which produces base testing VirtualBox, Parallels, and VMware boxes for 
multiple operating systems for use with Test Kitchen. By default, Test Kitchen uses the base images provided by Bento 
although custom images may also be built using HashiCorp Packer.

### Chef InSpec

#### Profiles