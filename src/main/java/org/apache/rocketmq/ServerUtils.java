package org.apache.rocketmq;

import com.alibaba.fastjson.JSON;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.common.BrokerConfig;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.namesrv.NamesrvConfig;
import org.apache.rocketmq.namesrv.NamesrvController;
import org.apache.rocketmq.remoting.netty.NettyClientConfig;
import org.apache.rocketmq.remoting.netty.NettyServerConfig;
import org.apache.rocketmq.store.config.MessageStoreConfig;

public class ServerUtils {
    private final String SEP = File.separator;
    private String rootBaseDir = System.getProperty("user.home");
    private String baseDirPrefix = "unitteststore-";
    private String brokerNamePrefix = "TestBrokerName_";
    private AtomicInteger brokerIndex = new AtomicInteger(0);
    private List<File> baseDirs = new ArrayList<>();
    private AtomicInteger portIndex = new AtomicInteger(40000);
    private Random random = new Random();

    public ServerUtils(boolean deleteFilesWhenShutdown) {
        if (deleteFilesWhenShutdown) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        for (File file : baseDirs) {
                            if (file.exists()) {
                                UtilAll.deleteFile(file);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    }
    public ServerUtils(boolean deleteFilesWhenShutdown, String rootBaseDir, String baseDirPrefix, String brokerNamePrefix) {
        this(deleteFilesWhenShutdown);
        if (rootBaseDir != null) {
            this.rootBaseDir = rootBaseDir;
        }
        if (baseDirPrefix != null) {
            this.baseDirPrefix = baseDirPrefix;
        }
        if (brokerNamePrefix != null) {
            this.brokerNamePrefix = brokerNamePrefix;
        }
    }

    public synchronized int nextPort() {
        return portIndex.addAndGet(random.nextInt(10) + 10);
    }

    public String createBaseDir() {
        String baseDir = rootBaseDir + SEP + baseDirPrefix + UUID.randomUUID();
        final File file = new File(baseDir);
        if (file.exists()) {
            return null;
        }
        baseDirs.add(file);
        return baseDir;
    }

    public Properties getDefaultNamesrvProperties(String baseDir) {
        //All the value should be string
        Properties properties = new Properties();
        properties.put("kvConfigPath", baseDir + SEP + "namesrv" + SEP + "kvConfig.json");
        properties.put("configStorePath", baseDir + SEP + "namesrv" + SEP + "namesrv.properties");
        properties.put("listenPort", nextPort() + "");
        return properties;
    }

    public Properties getDefaultBrokerProperties(String baseDir, String nsAddr) {
        //All the value should be string
        Properties properties = new Properties();
        properties.put("brokerClusterName", "DefaultTestCluster");
        properties.put("brokerName", brokerNamePrefix  + brokerIndex.getAndIncrement());
        properties.put("brokerIP1", "127.0.0.1");
        properties.put("brokerIP2", "127.0.0.1");
        properties.put("namesrvAddr", nsAddr);
        properties.put("storePathRootDir", baseDir);
        properties.put("storePathCommitLog", baseDir + SEP + "commitlog");
        //100M
        properties.put("mappedFileSizeCommitLog", "100000000");
        //600K
        properties.put("mappedFileSizeConsumeQueue", "600000");
        //5M
        properties.put("mappedFileSizeConsumeQueueExt", "5000000");
        properties.put("maxIndexNum", "1000");
        properties.put("maxHashSlotNum", "" + 1000 * 3);
        //port
        properties.put("listenPort", nextPort() + "");
        properties.put("haListenPort", nextPort() + "");
        //enable auto create
        properties.put("autoCreateSubscriptionGroup", "true");
        properties.put("autoCreateTopicEnable", "true");
        //enable trace
        properties.put("traceTopicEnable", "true");
        //enable Property Filter
        properties.put("enablePropertyFilter", "true");
        return properties;
    }

    public CompletableFuture<NamesrvController> createAndStartNamesrv(Properties others) {
        CompletableFuture<NamesrvController> namesrvFuture = new CompletableFuture<>();
        try {
            String baseDir = createBaseDir();
            Properties properties = getDefaultNamesrvProperties(baseDir);
            if (others != null) {
                properties.putAll(others);
            }
            NamesrvConfig namesrvConfig = new NamesrvConfig();
            NettyServerConfig nameServerNettyServerConfig = new NettyServerConfig();
            MixAll.properties2Object(properties, namesrvConfig);
            MixAll.properties2Object(properties, nameServerNettyServerConfig);
            NamesrvController namesrvController = new NamesrvController(namesrvConfig, nameServerNettyServerConfig);
            if (!namesrvController.initialize()) {
                throw new RuntimeException("Init failed");
            }
            namesrvController.start();
            namesrvFuture.complete(namesrvController);
        } catch (Exception e) {
            namesrvFuture.completeExceptionally(e);
        }
        return namesrvFuture;
    }

    public CompletableFuture<BrokerController> createAndStartBroker(String nsAddr, Properties others) {
        CompletableFuture<BrokerController> brokerFuture = new CompletableFuture<>();
        try {
            String baseDir = createBaseDir();
            Properties properties = getDefaultBrokerProperties(baseDir, nsAddr);
            if (others != null) {
                properties.putAll(others);
            }
            NettyServerConfig nettyServerConfig = new NettyServerConfig();
            NettyClientConfig nettyClientConfig = new NettyClientConfig();
            BrokerConfig brokerConfig = new BrokerConfig();
            MessageStoreConfig storeConfig = new MessageStoreConfig();
            MixAll.properties2Object(properties, brokerConfig);
            MixAll.properties2Object(properties, nettyServerConfig);
            MixAll.properties2Object(properties, nettyClientConfig);
            MixAll.properties2Object(properties, storeConfig);
            BrokerController brokerController = new BrokerController(brokerConfig, nettyServerConfig, nettyClientConfig, storeConfig);
            if (!brokerController.initialize()) {
                throw new RuntimeException("Init failed");
            }
            brokerController.start();
            brokerFuture.complete(brokerController);
        } catch (Throwable t) {
            brokerFuture.completeExceptionally(t);
        }
        return brokerFuture;
    }

    public String getNsAddr(NamesrvController... nss) {
        StringBuilder nsAddr = new StringBuilder();
        for (NamesrvController ns : nss) {
            if (nsAddr.length() == 0) {
                nsAddr.append("127.0.0.1:").append(ns.getNettyServerConfig().getListenPort());
            } else {
                nsAddr.append(";").append("127.0.0.1:").append(ns.getNettyServerConfig().getListenPort());
            }
        }
        return nsAddr.toString();
    }
}
