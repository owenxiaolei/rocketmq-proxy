package com.slyvas.rocketmq.proxy.config;

import com.google.gson.Gson;
import com.slyvas.rocketmq.proxy.util.HttpClientUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 配置信息
 *
 * @author slyvas
 */
public class ConfigData {

	private static Logger logger = LoggerFactory.getLogger("ConfigData");

	private static AtomicReference<Map<String/*internal ip*/, String/*out ip*/>> internalIPs = new AtomicReference<Map<String, String>>();
	private static AtomicReference<Map<String/*out ip*/, String/*internal ip*/>> outIPs = new AtomicReference<Map<String, String>>();

	private static Map<String/*original addr*/, String/*target addr*/> cache = new ConcurrentHashMap<>();

	private static ConfigData instance = new ConfigData();

	private ConfigData() {
	}

	static {
		loadIps();
	}

	@SuppressWarnings("unchecked")
	private static void loadIps() {
		Map<String, String> ipsMap = null;
		if (null != RocketMQProxyConfig.getInstance().getIpMappings()) {
			ipsMap = RocketMQProxyConfig.getInstance().getIpMappings();
		} else {
			String remoteConfigUrl = RocketMQProxyConfig.getInstance().getRemoteConfigUrl();
			if (null != remoteConfigUrl) {
				String data = HttpClientUtils.getInstance().httpGet(remoteConfigUrl, null);
				Gson gson = new Gson();
				ipsMap = gson.fromJson(data, Map.class);
			}
		}

		if (null != ipsMap && !ipsMap.isEmpty()) {
			Map<String, String> outIPMap = new HashMap<String, String>();
			for (Map.Entry<String, String> entry : ipsMap.entrySet()) {
				String internalIP = entry.getKey();
				String outIP = entry.getValue();
				outIPMap.put(outIP, internalIP);
			}
			internalIPs.set(ipsMap);
			outIPs.set(outIPMap);
		}
	}

	public static ConfigData getInstance() {
		return instance;
	}

	public String getOutAddr(String addr) {
		return searchAddr(addr, SearchType.INTERNAL_TO_OUT);
	}

	public String getInternalAddr(String addr) {
		return searchAddr(addr, SearchType.OUT_TO_INTERNAL);
	}

	/**
	 * 查找映射后的连接地址
	 *
	 * @param addr 原始地址 ip:port
	 * @param type 查找类型： <br/>
	 *             SearchType.INTERNAL_TO_OUT：通过内网IP查找对应的外网ip <br/>
	 *             SearchType.OUT_TO_INTERNAL：通过外网IP查询对应的内网ip
	 * @return
	 */
	private String searchAddr(String addr, SearchType type) {
		return searchAddr(addr, type, 1);
	}

	/**
	 * 查找映射后的连接地址
	 * 
	 * @param addr 原始地址 ip:port
	 * @param type 查找类型： <br/>
	 *             SearchType.INTERNAL_TO_OUT：通过内网IP查找对应的外网ip <br/>
	 *             SearchType.OUT_TO_INTERNAL：通过外网IP查询对应的内网ip
	 * @param recursionCount 当前递归次数
	 *
	 * @return
	 */
	private String searchAddr(String addr, SearchType type, int recursionCount) {
		if (null == addr || !RocketMQProxyConfig.getInstance().isIpTransform()) {
			return addr;
		}

		String result = cache.get(addr);
		if (null != result) {
			return result;
		}

		String[] arr = addr.split(":");
		if (arr.length != 2) {
			logger.error("searchIP ip parse error pls check, addr=" + addr);
			return addr;
		}

		String ip = arr[0];
		String port = arr[1];
		String resultIP = null;
		//通过内网IP查找对应的外网ip
		if (SearchType.INTERNAL_TO_OUT == type) {
			resultIP = internalIPs.get().get(ip);
		} else {
			resultIP = outIPs.get().get(ip);
		}

		if (null == resultIP) {
			//如果匹配不到映射的外网ip， 则重新load一次映射关系
			if (recursionCount >= 2) {
				return addr;
			}
			loadIps();
			searchAddr(addr, type, 2);
		}

		//如果匹配不到映射的外网ip，则原样返回地址
		result = null == resultIP ? addr : resultIP + ":" + port;
		if (logger.isInfoEnabled()) {
			logger.info("searchIP addr=" + addr + ", result=" + result);
		}

		cache.put(addr, result);

		return result;
	}

	/**
	 * 搜索类型
	 *
	 * @author slyvas
	 */
	private enum SearchType {
		/**
		 * 通过内外IP查找对应的外网ip
		 */
		INTERNAL_TO_OUT,
		/**
		 * 通过外网IP查询对应的内网ip
		 */
		OUT_TO_INTERNAL
	}

}
