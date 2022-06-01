package org.apache.rocketmq;

import org.apache.rocketmq.tools.command.MQAdminStartup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipesUtils {
    public final static Logger RECIPES_LOG = LoggerFactory.getLogger("recipes");
    public final static String COMMAND_PREFIX = "========= COMMAND %";

    public static void runCMD(String cmd) {
        MQAdminStartup.main(cmd.split("\\s+"));

    }
}
