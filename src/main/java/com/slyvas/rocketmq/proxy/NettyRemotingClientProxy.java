package com.slyvas.rocketmq.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slyvas.rocketmq.proxy.processor.AddrListParamProcessor;
import com.slyvas.rocketmq.proxy.processor.AddrSingleParamProcessor;
import com.slyvas.rocketmq.proxy.processor.GetNameServerAddressListProcessor;
import com.slyvas.rocketmq.proxy.processor.MethodProxyProcessor;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * NettyRemotingClient 代理类， 处理内外网IP转换
 * @author slyvas
 *
 */
public class NettyRemotingClientProxy implements MethodInterceptor {

	private static Logger logger = LoggerFactory.getLogger("NettyRemotingClientProxy");

	private static NettyRemotingClientProxy call = new NettyRemotingClientProxy();

	private static Map<String/*method name*/, MethodProxyProcessor> processorMap = new HashMap<String, MethodProxyProcessor>();

	private NettyRemotingClientProxy() {
	}

	static {
		//需要代理的方法， 注意：private方法无法被cglib代理
		AddrSingleParamProcessor addrSingleParamProcessor = new AddrSingleParamProcessor(0);
		processorMap.put("closeChannel", addrSingleParamProcessor);
		processorMap.put("invokeSync", addrSingleParamProcessor);
		processorMap.put("invokeAsync", addrSingleParamProcessor);
		processorMap.put("invokeOneway", addrSingleParamProcessor);
		processorMap.put("isChannelWritable", addrSingleParamProcessor);
		processorMap.put("updateNameServerAddressList", new AddrListParamProcessor(0));
		processorMap.put("getNameServerAddressList", new GetNameServerAddressListProcessor());
	}

	@SuppressWarnings("rawtypes")
	public static Object createProxy(Class targetObject, Object... params) {
		//增强类
		Enhancer enhancer = new Enhancer();
		//作为传参目标类子类
		enhancer.setSuperclass(targetObject);
		//回调设置为当前类
		enhancer.setCallback(call);

		Class[] clazz = new Class[params.length];
		for (int i = 0; i < params.length; i++) {
			clazz[i] = params[i].getClass();
		}

		return enhancer.create(clazz, params);
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		String methodName = method.getName();
		MethodProxyProcessor processor = processorMap.get(methodName);

		if (null != methodName && null != processor) {
			logger.info("intercept method=" + methodName + ",with processor=" + processor + ", process params");
			processor.processParam(args);
		}

		Object ret = proxy.invokeSuper(obj, args);

		if (null != processor) {
			logger.info("intercept method=" + methodName + ",with processor=" + processor + ", process retrurn value");
			ret = processor.processReturnData(ret);
		}

		return ret;
	}

}
