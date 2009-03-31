package org.simplius.jmx.logger.integration;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.simplius.jmx.logger.JmxEventLogger;
import org.simplius.jmx.logger.LogEvent;

/**
 *
 * @author VVivien
 */
public class JmxLoggingHandler extends Handler{
    private JmxEventLogger logger;
    private boolean platformServerUsed = false;

    public JmxLoggingHandler(){
        configureJmxLogger();
        startJmxLogger();
    }

    public JmxLoggingHandler(String objectName){
        configureJmxLogger();
        setObjectName(objectName);
        startJmxLogger();
    }

    public JmxLoggingHandler(MBeanServer server){
        configureJmxLogger();
        setMBeanServer(server);
        startJmxLogger();
    }

    public JmxLoggingHandler(MBeanServer server, String objectName){
        configureJmxLogger();
        setObjectName(objectName);
        startJmxLogger();
    }

    public void setObjectName(String objName){
        logger.setObjectName(buildObjectName(objName));
    }

    public String getObjectName() {
        return (logger.getObjectName() != null) ? logger.getObjectName().toString() : null;
    }

    public void setMBeanServer(MBeanServer server){
        logger.setMBeanServer(server);
    }
    public MBeanServer getMBeanServer() {
        return logger.getMBeanServer();
    }

    public void setPlatformServerUsed(boolean flag){
        platformServerUsed = flag;
    }

    public boolean isPlatformServerUsed() {
        return platformServerUsed;
    }

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        String msg;
        try {
            msg = getFormatter().format(record);
            LogEvent event = prepareLogEvent(msg,record);
            logger.log(event);
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }

    }

    @Override
    public void flush() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() throws SecurityException {
        logger.stop();
        logger = null;
    }

    @Override
    public boolean isLoggable(LogRecord record){
        return (logger != null &&
                logger.isStarted() &&
                logger.getMBeanServer() != null &&
                logger.getObjectName() != null &&
                super.isLoggable(record)
                );
    }

    private ObjectName buildObjectName(String name){
        ObjectName objName = null;
        try {
            objName = new ObjectName(name);
        } catch (MalformedObjectNameException ex) {
            throw new RuntimeException(ex);
        } catch (NullPointerException ex) {
            throw new RuntimeException(ex);
        }
        return objName;
    }


    private void configureJmxLogger() {
        logger = (logger == null) ? JmxEventLogger.createInstance() : logger;
        configure();
    }

    private void configure() {
        LogManager manager = LogManager.getLogManager();
        String cname = getClass().getName();

        // configure level (default INFO)
        String value = manager.getProperty(cname +".level");
        super.setLevel(value != null ? Level.parse(value) : Level.INFO);

        // configure formatter (default SimpleFormatter)
        value = manager.getProperty(cname +".formatter");
        if(value != null){
            try {
                Class cls = ClassLoader.getSystemClassLoader().loadClass(value);
                super.setFormatter((Formatter)cls.newInstance());
            } catch (Exception ex) {
                // ignore it and load SimpleFormatter.
                super.setFormatter(new SimpleFormatter());
            }
        }else{
            super.setFormatter(new SimpleFormatter());
        }

        // configure internal Jmx ObjectName (default provided by JmxEventLogger)
        value = manager.getProperty(cname +".objectName");
        if(value != null){
            setObjectName(value);
        }

        // configure server used
        value = manager.getProperty(cname +".usePlatformServer");
        if(value != null){
            boolean platformUsed = Boolean.parseBoolean(value);
            setPlatformServerUsed(platformUsed);
            if(platformUsed){
                setMBeanServer(ManagementFactory.getPlatformMBeanServer());
            }else{
                value = manager.getProperty(cname + ".serverDomain");
                if(value != null){
                    // 1. look for the server, otherwise create it.
                    ArrayList<MBeanServer> servers = javax.management.MBeanServerFactory.findMBeanServer(value);
                    if(servers.size() > 0){
                        setMBeanServer(servers.get(0));
                    }else{
                        setMBeanServer(MBeanServerFactory.createMBeanServer(value));
                    }
                }
            }
        }else{
            setPlatformServerUsed(true);
            setMBeanServer(ManagementFactory.getPlatformMBeanServer());
        }
    }
    private void startJmxLogger() {
        if(logger != null & !logger.isStarted()){
            logger.start();
        }
    }

    private LogEvent prepareLogEvent(String fmtMsg, LogRecord record){
        LogEvent<LogRecord> event = new LogEvent<LogRecord>();
        event.setLogRecord(record);
        event.setSource(this);
        event.setLevelName(record.getLevel().getName());
        event.setLoggerName(record.getLoggerName());
        event.setMessage(fmtMsg);
        event.setSequenceNumber(record.getSequenceNumber());
        event.setSourceClassName(record.getSourceClassName());
        event.setSourceMethodName(record.getSourceMethodName());
        event.setSourceThreadId(record.getThreadID());
        event.setSourceThrowable(record.getThrown());
        event.setTimeStamp(record.getMillis());

        return event;
    }
}