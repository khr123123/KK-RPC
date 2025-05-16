package org.khr.kkrpcspringbootstarter.globalTX.netty;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.khr.kkrpcspringbootstarter.globalTX.transactional.KKTransactionManager;
import org.khr.kkrpcspringbootstarter.globalTX.transactional.Transaction;
import org.khr.kkrpcspringbootstarter.globalTX.transactional.TransactionType;

@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
        log.info("Netty client channel active: {}", ctx.channel().remoteAddress());
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("接收到数据: {}", msg);
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONUtil.parseObj((String) msg);
        } catch (Exception e) {
            log.error("消息解析失败，消息内容: {}", msg, e);
            return;  // 解析失败直接返回，避免空指针
        }

        String groupId = jsonObject.getStr("groupId");
        String command = jsonObject.getStr("command");

        log.info("处理事务命令: groupId={}, command={}", groupId, command);

        // 注意：这里你调用 getTransaction 用的是 groupId，很可能取不到
        Transaction transaction = KKTransactionManager.getTransaction(groupId);

        if (transaction == null) {
            log.warn("未找到对应的事务对象，groupId: {}", groupId);
            return;  // 找不到事务直接返回，避免空指针异常
        }

        switch (command) {
            case "rollback":
                transaction.setTransactionType(TransactionType.rollback);
                log.info("事务回滚，事务ID: {}, 组ID: {}", transaction.getTransactionId(), groupId);
                break;
            case "commit":
                transaction.setTransactionType(TransactionType.commit);
                log.info("事务提交，事务ID: {}, 组ID: {}", transaction.getTransactionId(), groupId);
                break;
            default:
                log.warn("未知的事务命令: {}", command);
                break;
        }

        try {
            transaction.getTask().signalTask();
            log.info("事务任务已通知完成，事务ID: {}", transaction.getTransactionId());
        } catch (Exception e) {
            log.error("事务任务通知失败，事务ID: {}", transaction.getTransactionId(), e);
        }
    }

    public synchronized void call(JSONObject data) {
        if (context != null) {
            log.info("发送数据: {}", data.toJSONString(0));
            context.writeAndFlush(data.toJSONString(0));
        } else {
            log.warn("ChannelHandlerContext 未初始化，无法发送数据");
        }
    }
}
