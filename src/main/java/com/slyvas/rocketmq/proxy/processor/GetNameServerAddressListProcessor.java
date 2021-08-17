package com.slyvas.rocketmq.proxy.processor;

import java.util.ArrayList;
import java.util.List;

import com.slyvas.rocketmq.proxy.config.ConfigData;

/**
 * getNameServerAddressList方法返回值处理
 * @author slyvas
 *
 */
public class GetNameServerAddressListProcessor implements MethodProxyProcessor {

	@Override
	public void processParam(Object[] params) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object processReturnData(Object returnData) {
		if (null == returnData) {
			return returnData;
		}
		List<String> outAddrs = (List<String>) returnData;
		List<String> internalAddrs = new ArrayList<String>(outAddrs.size());
		for (String outAddr : outAddrs) {
			internalAddrs.add(ConfigData.getInstance().getInternalAddr(outAddr));
		}
		return internalAddrs;
	}

}
