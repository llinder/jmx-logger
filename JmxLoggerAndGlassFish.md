# Configuring GlassFish with JmxLogger #

You can configure a GlassFish doamin for remote realtime log monitoring using JmxLogger.

## 1. Configure Logging Options ##

  * Stop Domain Server

  * Update File {GLASSFISH\_HOME}/{DOMAIN\_HOME}/config/logging.properties with the following
```
#jmxlogger
jmxlogger.Handler.level=INFO
jmxlogger.Handler.objectName=jmxlogger:type=LogEmitter
```

## 2. Add Jar Files ##
  * Add files to GlassFish domain lib directory {GLASSFISH\_HOME}/{DOMAIN\_HOME}/lib/ext
    * jmxlogger-log4j-0.3.0.jar
    * mvel2-2.0.14.jar

## 3. Validate Config in Admin Console ##
  * Log into your GlassFish Admin
  * In screen Configuration > Logger Settings > Log Levels, ensure that you see the jmxlogger entry as shown beow.  You can override the level there if desired.
> ![http://jmx-logger.s3.amazonaws.com/glassfish.admin.loglevel.png](http://jmx-logger.s3.amazonaws.com/glassfish.admin.loglevel.png)

  * JMX Connectivity - in screen Configuration > Admin Service > JMX Connector, configure the JMX connector and specify port you want to use JMX client connections.  You will use that port to connect from JmxLogger console.
    * If you enable secure connection, you have to use the GlassFish Admin credentials when connecting via JMX.
    * If you enable SSL, you must start the JmxLogger console with the appropriate parameters to connect.

  * Start Domain Server

## 4. Run JmxLogger Console ##
**Start the JmxConsole application for remote log monitoring.
```
java -cp lib/*:jmxlogger-0.3.0.jar jmxlogger.tools.console.Main (*nix)
java -cp lib/*;jmxlogger-0.3.0.jar jmxlogger.tools.console.Main (Windows)
```
Adjust classpath for your use.**

![http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-glassfish.png](http://jmx-logger.s3.amazonaws.com/jmxlogger.tools.console-glassfish.png)