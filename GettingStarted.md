# Introduction #

Pajamas-Proxy was tested on several Linux platforms (Ubuntu 10.10, CentOS 5.5, both 64 bit version), but it should run on any [platform supported by Java Service Wrapper](http://wrapper.tanukisoftware.com/doc/english/supported-platforms-350.html).

# Requirements #

## Hardware ##

  * 10MB of free disk space for the binary distribution

## Operating System ##

  * See here: http://wrapper.tanukisoftware.com/doc/english/supported-platforms-350.html

## Environment ##

  * [JDK 6](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or above must be installed

# Installing and using on a Linux machine #

The steps below explain how to download and install the binary distribution on a Linux system. It should be rather trivial to do the same on any other platform.

  1. Fetch the latest distribution from the [downloads](http://code.google.com/p/pajamas-proxy/downloads/list) page.
  1. Unpack the archive into a directory of your choice: `<install-dir>`.
  1. Edit the `pajamas.properties` file located at `<install-dir>/conf`
    1. You must at least specify the `pajamas.remoteHosts` property to point to your memcached hosts.
    1. Most properties have defaults set to a reasonable value. See the docs inside the `pajamas.properties` file.
  1. In case java is not in your path, specify the `wrapper.java.command` in the `<pajamas-install-dir>/conf/wrapper.conf` file
    1. You may also change other settings for the pajamas daemon in the `<pajamas-install-dir>/conf/wrapper.conf` file

## Starting Pajamas-Proxy (daemon) ##

```
cd [pajamas-install-dir]
bin/pajamasproxy start
```

## Starting Pajamas-Proxy (console) ##

```
cd [pajamas-install-dir]
bin/pajamasproxy console
```

## Stopping Pajamas-Proxy (daemon) ##

```
cd [pajamas-install-dir]
bin/pajamasproxy stop
```

## Restarting Pajamas-Proxy ##

```
cd [pajamas-install-dir]
bin/pajamasproxy restart
```

## Pajamas-Proxy status ##

```
cd [pajamas-install-dir]
bin/pajamasproxy status
```