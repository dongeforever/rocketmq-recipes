package org.apache.rocketmq;

import com.beust.jcommander.Parameters;
import java.util.ArrayList;
import java.util.List;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.namesrv.NamesrvController;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.tools.command.MQAdminStartup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.rocketmq.RecipesUtils.COMMAND_PREFIX;
import static org.apache.rocketmq.RecipesUtils.RECIPES_LOG;
import static org.apache.rocketmq.RecipesUtils.runCMD;

@Parameters(commandDescription = "This is the echo")
public class R001_QueryAndTrace extends BaseCommand {


    public static Logger logger = LoggerFactory.getLogger(R001_QueryAndTrace.class);

    private ServerUtils serverUtils = new ServerUtils(true);

    @Override public void doCommandInner() throws Exception {
        NamesrvController namesrvController = serverUtils.createAndStartNamesrv(null).get();
        String nsAddr = serverUtils.getNsAddr(namesrvController);
        System.setProperty(MixAll.NAMESRV_ADDR_PROPERTY, nsAddr);
        logger.info("NsAddr: {}", nsAddr);
        BrokerController brokerController = serverUtils.createAndStartBroker(nsAddr, null).get();
        String clusterName = brokerController.getBrokerConfig().getBrokerClusterName();
        brokerController.registerBrokerAll(true, false, true);
        String topic = "TopicTest";

        String CMD001 = String.format("updateTopic -c %s -t %s", clusterName, topic);
        RECIPES_LOG.info("{} {}", COMMAND_PREFIX, CMD001);
        runCMD(CMD001);

        brokerController.registerBrokerAll(true, false, true);
        UtilAll.sleep(3000);

        String CMD002 = String.format("sendMessage -t %s -p \"%s\"", topic, "HelloWorld!");
        RECIPES_LOG.info("{} {}", COMMAND_PREFIX, CMD002);
        runCMD(CMD002);

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("CID_JODIE_1",true);
        consumer.subscribe(topic, "*");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // Wrong time format 2017_0422_221800
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        Thread.sleep(1000);
    }

    @Override public List<String> getCmdName() {
        List<String> names = new ArrayList<>();
        names.add("R001");
        names.add("QueryAndTrace");
        return names;
    }



    public static void main(String[] args) throws Exception {
        new R001_QueryAndTrace().doCommandInner();
    }
}
