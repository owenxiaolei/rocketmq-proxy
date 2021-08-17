package com.slyvas.rocketmq.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import com.slyvas.rocketmq.proxy.config.RocketMQProxyConfig;

public class MQProxyProducer {

	public static void main(String[] args) throws Exception {
		
		Map<String, String> ipMappings = new HashMap<String, String>();
		//key：内网ip   value：外网ip
		ipMappings.put("x.x.x.x", "x.x.x.x");
		//设置内外网ip映射关系
		RocketMQProxyConfig.getInstance().setIpMappings(ipMappings);
		//开启内外网ip转换
		RocketMQProxyConfig.getInstance().setIpTransform(true);
		//RocketMQProxyConfig.getInstance().setRemoteConfigUrl("http://o_ce2.haowanyou.com/f/ip_mapping.json");
		
		// 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
    	// 设置NameServer的地址
    	producer.setNamesrvAddr("x.x.x.x:9876");
    	// 启动Producer实例
        producer.start();
        String topic = "TopicTest";
        //获取topic下所有的queues， 每次都是远程获取
        List<MessageQueue> queues = producer.fetchPublishMessageQueues(topic);
        
        
    	for (int i = 0; i < 20; i++) {
    	    // 创建消息，并指定Topic，Tag和消息体
    	    Message msg = new Message(topic /* Topic */,
        	"TagA" /* Tag */,
        	i+"", //key
        	("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
        	);
        	// 发送消息到一个Broker
            //SendResult sendResult = producer.send(msg);
            
            //根据业务key  message路由到指定的queue
            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
				
				@Override
				public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
					int id = (int)arg;
					int index = id % mqs.size();
					return mqs.get(index);
				}
			}, i /*gameid+server+paixiId*/);
            
            // 通过sendResult返回消息是否成功送达
            System.out.printf("%s%n", sendResult);
    	}
    	// 如果不再发送消息，关闭Producer实例。
    	producer.shutdown();
	}
}
