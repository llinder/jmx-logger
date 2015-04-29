# Using Filter Expressions #

JmxLogger lets you specify an expression used to further filter logs as they are gathered from the running application. Filters are expressed in the [MVEL](http://mvel.codehaus.org/) lightweight expression language. Each log message logged by the application causes the filter to be evaluated. If the expression returns true, the message is logged otherwise, JmxLogger? will ignore the log.


## Context Variables/Parameters ##
The JmxLogger expression context exposes numerous variables and parameters which can be used in the filter expression. For instance, if you only want to see errors with the word JDBCException logged, you would write:
```
exceptionName contains "JDBCException"
```


### Logging ###
These are log-specific context variables and parameters exposed with each log message.
**`source` - the logging framework's source of the the log.  In this case it is the JmxLogger appender (log4j) or handler (JUL) class.
  * `loggerName` - the name of the class that generated the log.
  * `logLevel` - the level of the log message (i.e 'INFO','WARNING', etc)
  * `sourceClassName` - the name of the class that reported the log.  Almost always the same as loggerName.
  * `sourceMethodName` - the name of the method that generated the log.
  * `threadId` - the thread ID that generated the log message.
  * `sequenceNumber` - an incremental number for each log.
  * `timestamp` - a time value associated with each log message.
  * `formattedMessage` - the log message with its format rules applied.
  * `rawMessage` - the unformatted version of the log message.
  * `exceptionName` - the name of an attached throwable if any.**

### Log Stats ###
The `logStats` context variables are stats collected with each log message generated

  * `logStats.totalLogAttempted` - total number of attempt logs
  * `logStats.totalLogCounted` - total number of actual logs (filtered)
  * `logStats.startTime` - the time since JmxLogger started
  * `logStats.{LEVEL}` - number of logs for given log level where {LEVEL} can be INFO, DEBUG, WARN, WARNING, FINE, SEVERE, etc.
  * `logStats.{LOGGER_NAME}` - number of logs for given logger where {LOGGER\_NAME} is the class generating the log.

### System Stats ###
The `systemStats` context variables contain information collected about the running system.

  * `systemStats.startTime` - time when entire system started
  * `systemStats.uptime` - number of elapsed millis since system started
  * `systemStats.threadCount` - number of threads running
  * `systemStats.peakThreadCount` - peaked thread count
  * `systemStats.deamonThreadCount` - number of deamon threads
  * `systemStats.heapMemInit` - initialized amount of heap memory
  * `systemStats.heapMemUsed` - heap memory used
  * `systemStats.heapMemMax` - maximum heap memory
  * `systemStats.heapMemCommitted` - current heap memory committed
  * `systemStats.nonHeapMemInit` - initialized amount of non-heap memory
  * `systemStats.nonHeapMemUsed` - non-heap memory used
  * `systemStats.nonHeapMemMax` - maximum amount of non-heap mem available
  * `systemStats.heapMemCommitted` - current non-heap memory committed
  * `systemStats.loadedClassCount` - number of classes loaded
  * `systemStats.unloadedClassCount` - number of unloaded classes

#### Example ####
So, if you want to see logs only errors when ERROR log level is more than 5, you would write:
```
logStats["ERROR"] > 5 and logLevel == "ERROR"
```