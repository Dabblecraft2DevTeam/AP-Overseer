package net.cccm5.ApOverseer;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name = "Log4JAppender", category = "Core", elementType = "appender", printObject = true)
public class Log4JAppender extends AbstractAppender {

    public Log4JAppender() {
        super("Log4JAppender", null,
        		//PatternLayout.createLayout(
                //        "[%d{HH:mm:ss} %level]: %msg",
                //        null, null, null, null), false);
        		PatternLayout.createDefaultLayout(), false);
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public void append(LogEvent e) {
       e.getMessage();
    }
}
 