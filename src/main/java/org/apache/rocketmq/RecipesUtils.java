package org.apache.rocketmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipesUtils {
    private final static Logger RECIPES_LOG = LoggerFactory.getLogger("recipes");

    public static Logger getRecipesLog() {
        return RECIPES_LOG;
    }
}
