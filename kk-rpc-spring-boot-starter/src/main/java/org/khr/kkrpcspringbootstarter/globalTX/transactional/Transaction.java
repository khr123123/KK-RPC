package org.khr.kkrpcspringbootstarter.globalTX.transactional;


import lombok.Data;
import org.khr.kkrpcspringbootstarter.globalTX.util.KKTask;

@Data
public class Transaction {

    private String groupId;
    private String transactionId;
    private TransactionType transactionType;   // commit-待提交，rollback-待回滚
    private Boolean entry = false;// 是不是入口方法
    private KKTask task;

    public Transaction(String groupId, String transactionId) {
        this.groupId = groupId;
        this.transactionId = transactionId;
        this.task = new KKTask();
    }
}
