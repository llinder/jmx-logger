# Configuring JBoss with JmxLogger #
You can configure JBoss for remote log monitoring using the JmxLogger Log4J appender.


## 1. Configure JBoss Start Options ##
  * Shutdown JBoss first

  * Change JBoss startup option - update {JBOSS\_HOME}/bin/run.conf - change the JBoss startup option to enable JMX remote accessibility.
```
# Enable the jconsole agent
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote"

# Force JBoss to use the platform MBean server
#JAVA_OPTS="$JAVA_OPTS -Djboss.platform.mbeanserver"

# Enable remote connectivity on port 7070
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=7070"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=false"
```
In this example, we turned off authentication.  However, in a production env, you would need to setup proper JMX secured access either with password files or SSL.

## 2. Update Log4J file jboss-log4j.xml ##
Locate configuration file {JBOSS\_HOME}/{JBOSS\_INSTANCE}/conf/jboss-log4j.xml to add the JmxLogger appender.
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

## 3. Add Jars ##
  * Update jars - add jmxlogger-log4j-0.3.0.jar and mvel2-2.0.14.jar to {JBOSS\_HOME}/{JBOSS\_INSTANCE}/lib.

  * Restart JBoss

## 4. Run JmxLogger Console ##
**At this point, you can start the JmxLogger console and connect to the JmxLogger instance on the JMX connection port specified.
```
java -cp lib/*:jmxlogger-0.3.0.jar jmxlogger.tools.console.Main (*nix)
java -cp lib/*;jmxlogger-0.3.0.jar jmxlogger.tools.console.Main (Windows)
```
Adjust classpath for your use.**

![http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-jboss.png](http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-jboss.png)