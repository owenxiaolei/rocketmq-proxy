package com.hwy.rocketmq.proxy.config;

import java.util.Map;

/**
 * rocketMQ 代理配置
 * @author zhangxiaolei
 *
 */
public class RocketMQProxyConfig {

	//是否启用ip内外网转换功能， 默认不启用
	private boolean ipTransform = false;
	//内外网ip映射关系数据远程下载地址
	private String remoteConfigUrl;
	//内外网映射关系
	private Map<String/*internal ip*/, String/*out ip*/> ipMappings;

	private static RocketMQProxyConfig instance = new RocketMQProxyConfig();

	private RocketMQProxyConfig() {
	}

	public static RocketMQProxyConfig getInstance() {
		return instance;
	}

	public boolean isIpTransform() {
		return ipTransform;
	}

	public void setIpTransform(boolean ipTransform) {
		this.ipTransform = ipTransform;
	}

	public String getRemoteConfigUrl() {
		return remoteConfigUrl;
	}

	public void setRemoteConfigUrl(String remoteConfigUrl) {
		this.remoteConfigUrl = remoteConfigUrl;
	}

	public Map<String, String> getIpMappings() {
		return ipMappings;
	}

	public void setIpMappings(Map<String, String> ipMappings) {
		this.ipMappings = ipMappings;
	}
}
