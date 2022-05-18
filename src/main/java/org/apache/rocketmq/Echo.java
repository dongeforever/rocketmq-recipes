package org.apache.rocketmq;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "This is the echo")
public class Echo extends BaseCommand {
    @Override public void doCommandInner() {
        System.out.println("Hello");
    }

    @Override public String getCmdName() {
        return "echo";
    }
}
