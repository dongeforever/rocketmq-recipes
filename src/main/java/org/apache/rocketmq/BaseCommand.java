package org.apache.rocketmq;

import com.beust.jcommander.Parameter;
import java.util.Enumeration;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseCommand {
    protected  org.slf4j.Logger logger = LoggerFactory.getLogger(BaseCommand.class);

    @Parameter(names = {"--level", "-l"}, description = "The logger level")
    private String level = "INFO";

    public void doCommand() {
        setLogLevel(Level.toLevel(level.toUpperCase()));
        doCommandInner();
    }

    public abstract void doCommandInner();

    public boolean needWait() {
        return false;
    }

    public int needWaitTimeMs() {
        return 5 * 1000;
    }

    public void setLogLevel(Level level) {
        LogManager.getRootLogger().setLevel(level);
        Enumeration enumeration = LogManager.getCurrentLoggers();
        while (enumeration.hasMoreElements()) {
            ((Logger)enumeration.nextElement()).setLevel(level);
        }
    }
}
