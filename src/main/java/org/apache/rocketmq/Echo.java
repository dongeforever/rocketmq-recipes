package org.apache.rocketmq;

public class Echo extends BaseCommand {
    @Override public void doCommandInner() {
        System.out.println("Hello");
    }
}
