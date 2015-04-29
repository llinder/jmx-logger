# Configuring Apache Tomcat with JmxLogger #

You can configure Apache Tomcat for remote realtime log monitoring using JmxLogger.

## 1. Configure Startup Options ##
  * Stop server
  * Change start up options - update file {CATALINA\_HOME}/bin/catalina.sh  - change the JBoss startup option to enable JMX remote accessibility.
  * The following connects without authentication
```
set CATALINA_OPTS="-Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.port=7070 \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.authenticate=false"
    
```

  * If you want to support secured connection you can add
```
    -Dcom.sun.management.jmxremote.authenticate=true \
    -Dcom.sun.management.jmxremote.password.file=../conf/jmxremote.password \
    -Dcom.sun.management.jmxremote.access.file=../conf/jmxremote.access \  
```
Where jmxremote.password and jmxremote.access are JMX-specific credential files.  For more info, see [Tomcat Monitoring](http://tomcat.apache.org/tomcat-6.0-doc/monitoring.html).

## 2. Configure Logging ##
In Tomcat, you can configure war-specific logging using either Log4J or Java Util Logging.
#### Configure Log4J ####
  * Configure log4j.xml in your war for the JmxLogger
```
<log4j:configuration ... />
<appender name="jmxlogger" class="jmxlogger.integration.log4j.JmxLogAppender">
    <param name="Threshold" value="INFO"/>
    <param name="ObjectName" value="jmxlogger:type=LogEmitter"/>
    <param name="MBeanServer" value="platform"/>
    <layout class="org.apache.log4j.PatternLayout">
      <param name="ConversionPattern" value="%-5p %c{1} - %m%n"/>
    </layout>
  </appender>
...
   <root>
      <appender-ref ref="CONSOLE"/>
      <appender-ref ref="FILE"/>
      <appender-ref ref="jmxlogger"/>    
   </root>
</log4j:configuration>
```

#### Configure Logging for Java Util Logging ####
If you prefer to use Java Util Logging, configure the code for your WAR by placing logging.properties file in your classpath.
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

## 3. Add Jars ##
Add the following jar files to {CATALINA\_HOME}/lib
  * Add jmxlogger-x.x.x.jar and mvel2-2.0.14.jar if using Java Util Logging
  * Add jmxlogger-log4j-x.x.x.jar, mvel2-2.x.x.jar, and log4j.jar if using Log4J
  * Restart Tomcat

## 4. Run JmxLogger Console ##
**Start the JmxConsole application for remote log monitoring.
```
java -cp lib/*:jmxlogger-0.3.0.jar jmxlogger.tools.console.Main (*nix)
java -cp lib/*;jmxlogger-0.3.0.jar jmxlogger.tools.console.Main (Windows)
```
Adjust classpath for your use.**

![http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-glassfish.png](http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-glassfish.png)