package net.cccm5.ApOverseer;
import java.io.Serializable;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;

@Plugin(name = "Log4JAppender", category = "Core", elementType = "appender", printObject = true)
public class Log4JAppender extends AbstractAppender {

    public Log4JAppender(String arg0, Filter arg1, Layout<? extends Serializable> arg2) {
        //super("Log4JAppender", null,
        
        //PatternLayout.createLayout(
        //        "[%d{HH:mm:ss} %level]: %msg",
        //        null, null, null, null), false);
        
        //		PatternLayout.newBuilder().withPattern("[%d{HH:mm:ss} %level]: %msg").build(), false);
        super(arg0,arg1,arg2,false);
        //this.start();
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
