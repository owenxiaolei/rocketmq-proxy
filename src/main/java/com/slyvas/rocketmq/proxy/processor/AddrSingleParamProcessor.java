package com.slyvas.rocketmq.proxy.processor;

import com.slyvas.rocketmq.proxy.config.ConfigData;

/**
 * 单个连接地址参数处理
 * @author slyvas
 *
 */
public class AddrSingleParamProcessor implements MethodProxyProcessor {

	//ip参数在参数列表中的下标索引
	private int addrParamIndex;

	public AddrSingleParamProcessor(int addrParamIndex) {
		this.addrParamIndex = addrParamIndex;
	}

	@Override
	public void processParam(Object[] params) {
		if (null == params || null == params[addrParamIndex]) {
			return;
		}

		String internalAddr = params[addrParamIndex].toString();
		String outAddr = ConfigData.getInstance().getOutAddr(internalAddr);
		params[addrParamIndex] = outAddr;
	}

	@Override
	public Object processReturnData(Object returnData) {
		return returnData;
	}

}
