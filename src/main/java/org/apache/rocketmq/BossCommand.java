package org.apache.rocketmq;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BossCommand {

    private static Logger logger = LoggerFactory.getLogger(BossCommand.class);

    public static void main(String args[]) throws Exception {
        Map<String, BaseCommand> commands = new HashMap<>();
        commands.put(Echo.class.getSimpleName(), new Echo());

        JCommander.Builder builder = JCommander.newBuilder();
        for (String cmd : commands.keySet()) {
            builder.addCommand(cmd, commands.get(cmd));
        }
        JCommander jc = builder.build();
        jc.parse(args);

        if (jc.getParsedCommand() == null) {
            jc.usage();
        } else {
            BaseCommand command = commands.get(jc.getParsedCommand());
            if (command != null) {
                JCommander jCommander = jc.getCommands().get(jc.getParsedCommand());
                for (ParameterDescription description: jCommander.getParameters()) {
                    String name = description.getLongestName().substring(2);
                    Object value = null;
                    if (!description.isAssigned()) {
                        value = description.getDefault();
                    } else {
                        Object object = description.getObject();
                        Field field;
                        try {
                            field = object.getClass().getDeclaredField(name);
                        } catch (NoSuchFieldException e) {
                            field = object.getClass().getSuperclass().getDeclaredField(name);
                        }
                        field.setAccessible(true);
                        value = field.get(object);
                    }
                    logger.info("Parameter {} = {}", name, value);
                }
                if (command.needWait()) {
                    logger.info("Will wait {} ms to begin execute", command.needWaitTimeMs());
                    Thread.sleep(command.needWaitTimeMs());
                }
                command.doCommand();
            } else {
                jc.usage();
            }
        }
    }
}
