package org.khr;

import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * Netty 服务端处理器，用作分布式事务协调器
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // Map<groupId, Map<txId, status>>
    private static final Map<String, Map<String, String>> transactionMap = new HashMap<>();

    // 每组事务总数量
    private static final Map<String, Integer> transactionCountMap = new HashMap<>();

    // 记录事务组是否已经决议（防止重复广播）
    private static final Map<String, Boolean> resolved = new HashMap<>();

    // 记录事务组是否入口为失败（失败立即回滚）
    private static final Map<String, Boolean> entryFailed = new HashMap<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        channelGroup.add(channel);
        System.out.println("【KKRPC】客户端连接：" + channel.remoteAddress());
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("【KKRPC】接收数据: " + msg.toString());

        JSONObject jsonObject = JSONObject.parseObject((String) msg);
        String command = jsonObject.getString("command");
        String transactionId = jsonObject.getString("transactionId");
        String groupId = jsonObject.getString("groupId");
        String transactionType = jsonObject.getString("transactionType"); // commit / rollback
        Integer transactionCount = jsonObject.getInteger("totalTxCount");
        Boolean isEntry = jsonObject.getBoolean("isEntry");

        if ("create".equals(command)) {
            transactionMap.put(groupId, new HashMap<>());
            transactionCountMap.put(groupId, transactionCount);
            resolved.put(groupId, false);
            entryFailed.put(groupId, false);
            System.out.println("【KKRPC】创建事务组：" + groupId + " 总事务数：" + transactionCount);
        } else if ("add".equals(command)) {
            if (!transactionMap.containsKey(groupId)) {
                throw new RuntimeException("事务组尚未创建：" + groupId);
            }

            Map<String, String> txGroup = transactionMap.get(groupId);
            txGroup.put(transactionId, transactionType);
            System.out.println("【KKRPC】添加事务：" + transactionId + " 状态：" + transactionType + " 所属组：" + groupId);

            // 入口事务处理
            if (Boolean.TRUE.equals(isEntry)) {
                // 如果入口事务失败，立即全体回滚
                if ("rollback".equals(transactionType)) {
                    entryFailed.put(groupId, true);
                    if (!resolved.get(groupId)) {
                        JSONObject result = new JSONObject();
                        result.put("groupId", groupId);
                        result.put("command", "rollback");
                        System.out.println("【KKRPC】入口失败，立即回滚，groupId：" + groupId);
                        sendResult(result);
                        resolved.put(groupId, true);
                        return;
                    }
                }
            }

            // 如果之前已经被判定入口失败，后续进来的也立即回滚
            if (entryFailed.get(groupId)) {
                JSONObject result = new JSONObject();
                result.put("groupId", groupId);
                result.put("command", "rollback");
                System.out.println("【KKRPC】组已判定入口失败，新事务立即回滚，groupId：" + groupId);
                sendResult(result);
                System.err.println(transactionMap);
                System.err.println(transactionCountMap);
                System.err.println(resolved);
                System.err.println(entryFailed);
                return;
            }

            // 所有事务都到了才判定整体状态
            if (txGroup.size() == transactionCountMap.get(groupId)) {
                if (!resolved.get(groupId)) {
                    JSONObject result = new JSONObject();
                    result.put("groupId", groupId);

                    if (txGroup.containsValue("rollback")) {
                        result.put("command", "rollback");
                        System.out.println("【KKRPC】判定回滚，groupId：" + groupId);
                    } else {
                        result.put("command", "commit");
                        System.out.println("【KKRPC】判定提交，groupId：" + groupId);
                    }

                    sendResult(result);
                    resolved.put(groupId, true);
                    System.err.println(transactionMap);
                    System.err.println(transactionCountMap);
                    System.err.println(resolved);
                    System.err.println(entryFailed);
                }
            } else {
                System.out.println("【KKRPC】尚未全部到达，groupId：" + groupId + " 当前数量：" + txGroup.size());
            }
        }
    }

    private void sendResult(JSONObject result) {
        for (Channel channel : channelGroup) {
            channel.writeAndFlush(result.toJSONString());
        }
    }
}
