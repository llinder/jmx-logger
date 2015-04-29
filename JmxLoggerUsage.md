# Using JmxLogger #

JmxLogger is easy to integrate with your new or existing application.  You simply configure JmxLogger in your logging infrastructure using Log4J or Java Util Logging (JUL).  JmxLogger supports both of those frameworks and aautomatically captures and braodcasts your event logs to any registered JMX client.

## JmxLogger Dependencies ##
JmxLogger has a small footprint
  * jmxlogger-x.x.x.jar
  * jmxlogger-log4j.jar if using Log4j JmxLogger Appender
  * Mvel2 (mvel2-2.z.z.jar)
  * Log4J's
The binary distribution contains all jars.  When using Log4J, ensure that the log4j-x.z.y.jar is on your classpath and make sure to use the appropriate jmxlogger jar as well.

## JmxLogger Settings ##
When configuring JmxLogger(for either log4j or Java Util Logging), you can configure the logger with several setting values to control how it behaves

  * **Level** - use this setting to specify the log level for JmxLogger.  If left unspecified, it will default to DEBUG for log4j or FINE for JUL.
  * **Layout/Formatter** - this settings specifies a Layout (log4j) / Formatter (JUL) class used to format the log message.
  * **FilterExpression** - use this to set a filter expression used to narrow down the type of messages being filtered.  This must be expressed in MVEL and must evaluate to a boolean.  See the "Filtering Your Log" section for more information.
  * **ObjectName** - this setting specifies the JMX's MBean name for the JmxLogger object.  This ID is used to identify the JmxLogger component in the MBeanServer.  The name must be a valid JMX ObjectName.  It used by by client applications to connect the logger.
  * **MBeanServer** - use this setting to specify the MBeanServer instance to use.  By default, JmxLogger will use the platform MBeanServer is none is specified or if this property is set to "platform".  Or you can specify the name of the MBeanServer's domain and JmxLogger will attempt to use that.

### Log4J Example ###
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
  <appender name="console" class="org.apache.log4j.ConsoleAppender">
    <param name="Target" value="System.out"/>
    <layout class="org.apache.log4j.PatternLayout">
      <param name="ConversionPattern" value="%-5p %c{1} - %m%n"/>
    </layout>
  </appender>

  <appender name="jmxlogger1" class="jmxlogger.integration.log4j.JmxLogAppender">
    <param name="Threshold" value="INFO"/>
    <param name="ObjectName" value="jmxlogger:type=LogEmitter1"/>
    <param name="MBeanServer" value="platform"/>
    <layout class="org.apache.log4j.PatternLayout">
      <param name="ConversionPattern" value="%-5p %c{1} - %m%n"/>
    </layout>
  </appender>

  <appender name="jmxlogger2" class="jmxlogger.integration.log4j.JmxLogAppender">
    <param name="FilterExpression" value="rawMessage contains 'trouble'"/>
    <param name="ObjectName" value="jmxlogger:type=LogEmitter2"/>
    <param name="MBeanServer" value="platform"/>
    <layout class="org.apache.log4j.PatternLayout">
      <param name="ConversionPattern" value="%-5p %c{1} - %m%n"/>
    </layout>
  </appender>

  <category name="demo.agent">
      <priority value="DEBUG"/>
      <appender-ref ref="jmxlogger2" />
  </category>
  
  <root>
    <priority value="INFO"/>
    <appender-ref ref="console" />
    <appender-ref ref="jmxlogger1" />
  </root>

</log4j:configuration>
```

### Java Util Logging Example ###
```
# *********************** Java Util Logging ************************
handlers=jmxlogger.integration.logutil.JmxLogHandler, java.util.logging.ConsoleHandler

# Default global logging level.
.level=INFO

# Console log handler
java.util.logging.ConsoleHandler.level = INFO
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter

# jmxlogger handler
jmxlogger.Handler.level=INFO
jmxlogger.Handler.filterExpression=rawMessage contains 'java util logging'
jmxlogger.Handler.formatter=java.util.logging.SimpleFormatter
jmxlogger.Handler.objectName=jmxlogger:type=LogEmitter
jmxlogger.Handler.server=platform
```

## Remote Logging with JmxLogger ##
To enable remote logging of a running application, you must do two things:
  * setup the JmxLogger in your logging configuration (see above)
  * start your process with JMX remote access enabled
The second item can be done both programmatically and via command-line VM system properties.  We will show a simple example of the latter as it is beyond (way beyond) the scope of this document to cover JMX Remote accessibility.

Suppose you have a Java application called MyProcess with classpath my:class:path, you can start it with JMX enabled with remote access as follows

```
java -cp your:class:path \
    -Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.port=7070 \
    -Dcom.sun.management.jmxremote.authenticate=false \
your.process.main.MyProcess
```

This command (wrapped for readability), starts MyProcess and enables JMX remote access on port 7070.  It also turns off authenticate (default is true).  If you need to setup user authentication, you can do it as follows

```
java -cp your:class:path \
    -Dcom.sun.management.jmxremote.port=7070 \
    -Dcom.sun.management.jmxremote.authenticate=true
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.access.file=../access.properties \
    -Dcom.sun.management.jmxremote.password.file=../password.properties 
your.process.main.MyProcess

```

In this setup we are
  * enabling remote access on port 7070
  * enabling security by turning authentication on
  * ensure we are not using ssl, by setting it to false
  * provide an access file and a password file that define credentials

NOTE `-Dcom.sun.management.jmxremote` is only required if the process is running under Java 1.5. As of version 1.6, it is not required.

For more on how to use JMX remote connectivity see - http://java.sun.com/j2se/1.5.0/docs/guide/management/agent.html

## Running the JmxLogger Console ##
JmxLogger comes with its own Swing-based console.  The console is a JMX client that allows users to remotely connect and view logs collected by the configured JmxLogger agent (running as Log4J appender or JUL handler).  To launch the console, do:
```
java -cp mvel2-mvel2-2.0.14.jar:jmxlogger-0.3.0.jar jmxlogger.tools.console.Main (*nix)
java -cp mvel2-mvel2-2.0.14.jar;jmxlogger-0.3.0.jar jmxlogger.tools.console.Main (Windows)
```
The console's main class is located in `jmxlogger.tools.console.Main`.  Adjust the classpath accordingly.  When the logger starts, you will see

![http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-start.png](http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-start.png)

### Console Toolbar ###
The console provides a toolbar with several buttons to operate it

![http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-buttons.png](http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-buttons.png)

From right to left
  * Connect - opens the connection dialog
  * Disconnect - ends current connection to remote
  * Resume - resumes the logging from remote JmxLogger
  * Pause - pauses logging from remote JmxLogger
  * Refresh - refreshes the screen with settings from JmxLogger

### Connecting to a JmxLogger Agent ###
After you have started your process with JMX remote connectivity enabled, you can connect to it from the console.  Click on the Connect button which will reveal the connection dialog:
![http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-login.png](http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-login.png)
  * Address - this is the hostname and port number of the process running with JMX connectivity activated.  The port number must match that setup on your command line that started the process.
  * Username / Password - provide user credential to connect to the JmxLogger if authentication was enabled in the command line that started the remote process.  If authentication is set to false, then these values can be left blank.
  * MBean - this is the ObjectName value that was setup for the JmxLogger during logging configuration.  The name is case-sensitive and must match exactly with the configured value.

## Using Filter Expressions ##
JmxLogger lets you specify an expression used to further filter logs as they are gathered from the running application.  Filters are expressed in the MVEL lightweight expression language [(see MVEL for detail)](http://mvel.codehaus.org/).  Each log message logged by the application causes the filter to be evaluated.  If the expression returns true, the message is logged otherwise, JmxLogger will ignore the log.

See [Using Filter Expressions](UsingFilterExpressions.md) for more detail.