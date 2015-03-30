# Pajamas Proxy - a Memcached Proxy written in Java #

[![Build Status](https://travis-ci.org/eranharel/pajamas-proxy.svg?branch=master)](https://travis-ci.org/eranharel/pajamas-proxy)

This project is developed as an easy to use alternative to [moxi](http://code.google.com/p/moxi/). It is easier to [install, configure](GettingStarted.md), and [monitor](Monitoring.md), and should be easier to use for Java developers. It also has a full compatibility with [Spymemcached](http://code.google.com/p/spymemcached/), and [Xmemcached](http://code.google.com/p/xmemcached/)  [memcached](http://www.danga.com/memcached/) Java clients.

## What is it for? ##

Pajamas Proxy is used to reduce the amount of connections to a memcached cluster between Data Centers, and thus simplifies maintenance. Future version of Pajamas-Proxy will hold an optional in-RAM cache, which should help reducing the traffic between data centers.

Pajamas Proxy can also be used to simplify the memcached clients configuration. In this use case, the clients use the proxy as a single memcached server, and the proxy delegates the requests to the cluster.

## OK, how do I get started? ##

See the [GettingStarted](GettingStarted.md) page for the installation, and usage documentation.

## Pajamas News ##

Stay tuned:
  * Follow us on [twitter](http://twitter.com/#!/pajamasproxy).
  * Join the [pajamas-proxy google group](http://groups.google.com/group/pajamas-proxy)

```
   __  _____      _                             _____                      __   
  / / |  __ \    (_)                           |  __ \                     \ \  
 / /  | |__) |_ _ _  __ _ _ __ ___   __ _ ___  | |__) | __ _____  ___   _   \ \ 
< <   |  ___/ _` | |/ _` | '_ ` _ \ / _` / __| |  ___/ '__/ _ \ \/ / | | |   > >
 \ \  | |  | (_| | | (_| | | | | | | (_| \__ \ | |   | | | (_) >  <| |_| |  / / 
  \_\ |_|   \__,_| |\__,_|_| |_| |_|\__,_|___/ |_|   |_|  \___/_/\_\\__, | /_/  
                _/ |                                                 __/ |      
               |__/                                                 |___/       
```



## ...And I couldn't do without ##
### Restructure101 ###
[![](http://www.headwaysoftware.com/images/r101x75.gif)](http://www.headwaysoftware.com/)
Headways Software kindly gave me a license for Restructure 101 :) I couldn't manage the clutter without it.

### Yourkit ###
YourKit is kindly supporting open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:
[YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp) and [YourKit .NET Profiler](http://www.yourkit.com/java/profiler/index.jsp).
