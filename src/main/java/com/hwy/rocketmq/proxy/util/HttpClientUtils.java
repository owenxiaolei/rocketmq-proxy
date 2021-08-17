package com.hwy.rocketmq.proxy.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {

	private static HttpClientUtils instance;
	private static byte[] syncroot = new byte[0];
	private static CloseableHttpClient httpClient;
	private static Logger logger = LoggerFactory.getLogger("HttpClientUtils");

	public static HttpClientUtils getInstance()  {
		if (instance == null) {
			synchronized (syncroot) {
				if (instance == null)
					instance = new HttpClientUtils();
				return instance;
			}
		}
		return instance;
	}

	private HttpClientUtils() {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(30000)// 连接超时
				.setSocketTimeout(15000)// socket超时
				.build();

		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(500);// 最大连接数
		connectionManager.setDefaultMaxPerRoute(50);// 例如默认每路由最高50并发，具体依据业务来定

		httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).setDefaultRequestConfig(config)
				.build();
	}

	public String httpGet(String url, Map<String, String> params) {
		return httpGet(url, params, "utf-8");
	}

	public String httpGet(String url, Map<String, String> params, String charset) {
		if (null == url) {
			return null;
		}
		CloseableHttpResponse response = null;
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			}
			HttpGet httpget = new HttpGet(url);
			response = httpClient.execute(httpget);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpget.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			logger.error("url : " + url, e);
		}finally{
			if(response != null){
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
