package com.bccriskadvisory.link.connector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.RequestFactory;
import com.bccriskadvisory.jira.ao.connection.Connection;
import com.bccriskadvisory.link.rest.edgescan.EdgescanResponse;
import com.bccriskadvisory.link.rest.gson.GsonObject;
import com.bccriskadvisory.link.utility.TimedTask;
import com.google.common.base.Joiner;

public class EdgescanV1Connector {

	private static final DateTimeFormatter EDGESCAN_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	private static final String ASSET_ENDPOINT = "assets";
	private static final String VULNERABILITY_ENDPOINT = "vulnerabilities";
	
	private static final String API_V1_PREFIX = "api/v1/";
	private static final String APP_PREFIX = "app#/";
	

	private RequestFactory<?> requestFactory;
	private Connection connection;

	public EdgescanV1Connector(RequestFactory<?> requestFactory, Connection connection) {
		this.requestFactory = requestFactory;
		this.connection = connection;
	}
	
	public String vulnerabilityUrl(Integer vulnerabilityId) {
		return new RequestBuilder(VULNERABILITY_ENDPOINT).stringQuery("id", vulnerabilityId.toString()).getDisplayUrl();
	}
	
	public RequestBuilder vulnerabilities() {
		return new RequestBuilder(VULNERABILITY_ENDPOINT);
	}
	
	public RequestBuilder assets() {
		return new RequestBuilder(ASSET_ENDPOINT);
	}
	
	public EdgescanResponse test() {
		return assets().execute();
	}
	
	protected EdgescanResponse execute(final String url) {
		Request<?> request = requestFactory.createRequest(MethodType.GET, url);
		
		request.addHeader("X-API-Token", connection.getApiKey());
		
		try (TimedTask task = new TimedTask("Making request to edgescan: " + url)){
			final String response = request.execute();
			return GsonObject.fromJson(response, EdgescanResponse.class);
		} catch(final Exception e) {
			return new EdgescanResponse();
		}
	}
	
	public class RequestBuilder {
		
		private String endpoint;
		private Integer id;
		private Map<String, String> queryMap = new LinkedHashMap<>();
		
		private RequestBuilder(String endpoint){
			this.endpoint = endpoint;
		}
		
		public RequestBuilder withId(Integer id) {
			this.id = id;
			return this;
		}
		
		public RequestBuilder dateQuery(String key, ZonedDateTime zonedDateTime) {
			return stringQuery(key, EDGESCAN_FORMAT.format(zonedDateTime));
		}
		
		public RequestBuilder stringQuery(String key, String value) {
			queryMap.put("[" + key + "]", value);
			return this;
		}
		
		public String getJsonUrl() {
			return getUrl(true);
		}
		
		public String getDisplayUrl() {
			return getUrl(false);
		}
		
		private String getUrl(boolean json) {
			StringBuilder urlBuilder = new StringBuilder().append(connection.getUrl());

			if (json) {
				urlBuilder.append(API_V1_PREFIX);
			} else {
				urlBuilder.append(APP_PREFIX);
			}
			
			urlBuilder.append(endpoint);
			
			if (json && id != null) {
				urlBuilder.append("/").append(id);
			}
			
			if (json) {
				urlBuilder.append(".json");
			}
			
			if (!queryMap.isEmpty()) {
				urlBuilder.append("?c");
				
				if (json) {
					urlBuilder.append(Joiner.on("&c").withKeyValueSeparator("=").join(queryMap));
				} else {
					urlBuilder.append(urlEncodedQueryString());
				}
			}
			
			return urlBuilder.toString();
		}

		private String urlEncodedQueryString() {
			List<String> ret = new ArrayList<>();
			
			for (Entry<String, String> kvPair : queryMap.entrySet()) {
				try {
					ret.add(encode(kvPair.getKey()) + "=" + encode(kvPair.getValue()));
				} catch (UnsupportedEncodingException e) {
					ret.add(kvPair.getKey() + "=" + kvPair.getValue());
				}
			}
			
			return Joiner.on("&c").join(ret);
		}

		private String encode(String string) throws UnsupportedEncodingException {
			return URLEncoder.encode(string, "UTF-8");
		}
		
		public EdgescanResponse execute() {
			return EdgescanV1Connector.this.execute(getJsonUrl());
		}
	}
}