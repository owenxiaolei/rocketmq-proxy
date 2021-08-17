package com.slyvas.rocketmq.proxy.processor;

/**
 * 代理方法处理器
 * @author slyvas
 *
 */
public interface MethodProxyProcessor {

	/**
	 * 处理参数
	 * @param params 方法原始参数
	 */
	public void processParam(Object[] params);

	/**
	 * 处理返回值
	 * @param returnData 方法原始返回值
	 * @return
	 */
	public Object processReturnData(Object returnData);

}
