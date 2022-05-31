package org.apache.rocketmq;

import com.beust.jcommander.Parameters;
import java.util.ArrayList;
import java.util.List;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Parameters(commandDescription = "This is the echo")
public class Echo extends BaseCommand {
    private static Logger logger = LoggerFactory.getLogger(Echo.class);
    @Override public void doCommandInner() {
        logger.info("Hello");
    }

    @Override public List<String> getCmdName() {
        List<String> names = new ArrayList<>();
        names.add("echo");
        return names;
    }

    public void produce() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        for (int i = 0; i < 128; i++)
            try {
                {
                    Message msg = new Message("TopicTest",
                        "TagA",
                        "OrderID188",
                        "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
                    SendResult sendResult = producer.send(msg);
                    System.out.printf("%s%n", sendResult);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        producer.shutdown();
    }
}
