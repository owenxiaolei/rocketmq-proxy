package com.hwy.rocketmq.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import com.hwy.rocketmq.proxy.config.RocketMQProxyConfig;

public class MQProxyConsumer {

	public static void main(String[] args) throws Exception {
		
		Map<String, String> ipMappings = new HashMap<String, String>();
		//key：内网ip   value：外网ip
		ipMappings.put("10.0.8.133", "49.234.48.57");
		//设置内外网ip映射关系
		RocketMQProxyConfig.getInstance().setIpMappings(ipMappings);
		//开启内外网ip转换
		RocketMQProxyConfig.getInstance().setIpTransform(true);

		// 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");

    	// 设置NameServer的地址
        consumer.setNamesrvAddr("49.234.48.57:9876");

    	// 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe("TopicTest", "*");
    	// 注册回调实现类来处理从broker拉取回来的消息
        /*consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                // 标记该消息已经被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });*/
        
        consumer.registerMessageListener(new MessageListenerOrderly() {
			
			@Override
			public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
				String m = new String(msgs.get(0).getBody());
				System.out.printf("%s Receive New Messages: %s %s %n", Thread.currentThread().getName(), m, msgs);
				return ConsumeOrderlyStatus.SUCCESS;
			}
		});
        
        // 启动消费者实例
        consumer.start();
        System.out.printf("Consumer Started.%n");

	}

}
