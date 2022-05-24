package org.apache.rocketmq;

import com.beust.jcommander.Parameter;
import java.util.List;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.logging.InternalLoggerFactory;
import org.apache.rocketmq.logging.inner.Level;

public abstract class BaseCommand {
    protected InternalLogger logger = InternalLoggerFactory.getLogger(BaseCommand.class);

    @Parameter(names = {"--level", "-l"}, description = "The logger level")
    private String level = "INFO";

    public void doCommand() throws Exception {
        setLogLevel(Level.toLevel(level.toUpperCase()));
        doCommandInner();
    }

    public abstract void doCommandInner() throws Exception;

    public abstract List<String> getCmdName();

    public boolean needWait() {
        return false;
    }

    public int needWaitTimeMs() {
        return 5 * 1000;
    }

    public void setLogLevel(Level level) {
        //TODO
    }
}
