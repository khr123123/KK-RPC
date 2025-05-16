package org.khr.kkrpcspringbootstarter.globalTX.transactional;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.khr.kkrpcspringbootstarter.globalTX.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class KKTransactionManager {

    private static NettyClient nettyClient;

    @Autowired
    public void setNettyClient(NettyClient nettyClient) {
        KKTransactionManager.nettyClient = nettyClient;
    }


    private static final ThreadLocal<Transaction> currentTransaction = new ThreadLocal<>();
    private static final ThreadLocal<String> currentGroupId = new ThreadLocal<>();

    private static final Map<String, Transaction> transactionMap = new HashMap<>();

    public static String createTransactionGroup(int remoteServiceCount) {
        String groupId = RandomUtil.randomString(5);
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("groupId", groupId);
        jsonObject.putOnce("command", "create");
        jsonObject.putOnce("totalTxCount", remoteServiceCount + 1); // 远程事务数量 + 入口方法的一个事务
        nettyClient.send(jsonObject);
        log.info("创建事务组: {}", groupId);
        currentGroupId.set(groupId);
        return groupId;
    }

    public static Transaction createTransaction(String groupId, boolean entry) {
        String transactionId = RandomUtil.randomString(5);
        Transaction transaction = new Transaction(groupId, transactionId);
        transaction.setEntry(entry);
        currentTransaction.set(transaction);
        transactionMap.put(groupId, transaction);
        log.info("给事务组{} 注册分支事务: {}", groupId, transactionId);
        return transaction;
    }

    public static void addTransaction(Transaction transaction, TransactionType transactionType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("groupId", transaction.getGroupId());
        jsonObject.putOnce("transactionId", transaction.getTransactionId());
        jsonObject.putOnce("transactionType", transactionType);
        jsonObject.putOnce("isEntry", transaction.getEntry());
        jsonObject.putOnce("command", "add");
        nettyClient.send(jsonObject);
        log.info("添加事务: {} | 类型: {}", transaction.getTransactionId(), transactionType);
    }

    /**
     * 获取当前线程的事务
     */
    public static Transaction getCurrentTransaction() {
        return currentTransaction.get();
    }

    public static void setCurrentGroupId(String groupId) {
        currentGroupId.set(groupId);
    }

    public static Transaction getTransaction(String groupId) {
        return transactionMap.get(groupId);
    }


    public void clear() {
        currentTransaction.remove();
        currentGroupId.remove();
    }
}
