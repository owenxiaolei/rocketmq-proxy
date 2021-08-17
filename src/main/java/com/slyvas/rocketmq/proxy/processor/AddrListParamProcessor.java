package com.slyvas.rocketmq.proxy.processor;

import java.util.ArrayList;
import java.util.List;

import com.slyvas.rocketmq.proxy.config.ConfigData;

/**
 * 连接地址列表list参数处理
 * @author slyvas
 *
 */
public class AddrListParamProcessor implements MethodProxyProcessor {

	//ip参数在参数列表中的下标索引
	private int addrParamIndex;

	public AddrListParamProcessor(int addrParamIndex) {
		this.addrParamIndex = addrParamIndex;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processParam(Object[] params) {
		if(null == params || null == params[addrParamIndex]){
			return;
		}
		
		List<String> internalAddrs = (List<String>)params[addrParamIndex];
		List<String> outAddrs = new ArrayList<String>(internalAddrs.size());
		for (String internalAddr : internalAddrs) {
			outAddrs.add(ConfigData.getInstance().getOutAddr(internalAddr));
		}
		params[addrParamIndex] = outAddrs;
	}

	@Override
	public Object processReturnData(Object returnData) {
		return returnData;
	}

}
