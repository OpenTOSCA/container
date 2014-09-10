package org.opentosca.util.http.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opentosca.util.http.service.IHTTPService;

/**
 * Copyright 2012 IAAS University of Stuttgart <br>
 * <br>
 * 
 * This is an implementation of the
 * {@link org.opentosca.util.http.service.IHTTPService} interface. A lot of
 * methods currently offer only very basic functionality which could be extended
 * in the future if the need arises. <br>
 * All methods make use of the Apache HttpComponents.
 * 
 * @author Nedim Karaoguz - nedim.karaoguz@developers.opentosca.org
 * 
 */
public class HttpServiceImpl implements IHTTPService {
	
	DefaultHttpClient client;
	
	
	@Override
	public HttpResponse Get(String uri, List<Cookie> cookies) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		HttpGet get = new HttpGet(uri);
		
		if (cookies != null) {
			for (Cookie c : cookies) {
				((AbstractHttpClient) this.client).getCookieStore().addCookie(c);
				
			}
		}
		
		HttpResponse response = this.client.execute(get);
		
		return response;
		// TODO Return something useful maybe... like an InputStream
	}
	
	@Override
	public HttpResponse Get(String uri) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		HttpGet get = new HttpGet(uri);
		HttpResponse response = this.client.execute(get);
		
		return response;
		// TODO Return something useful maybe... like an InputStream
	}
	
	@Override
	public HttpResponse Get(String uri, String username, String password) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		this.client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		HttpGet get = new HttpGet(uri);
		HttpResponse response = this.client.execute(get);
		
		return response;
		// TODO Return something useful maybe... like an InputStream
	}
	
	@Override
	public HttpResponse Head(String uri) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		HttpHead head = new HttpHead(uri);
		HttpResponse response = this.client.execute(head);
		return response;
	}
	
	@Override
	public HttpResponse Post(String uri, HttpEntity httpEntity) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		HttpPost post = new HttpPost(uri);
		post.setEntity(httpEntity);
		HttpResponse response = this.client.execute(post);
		return response;
	}
	
	@Override
	public HttpResponse Post(String uri, HttpEntity httpEntity, List<Cookie> cookies) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		HttpPost post = new HttpPost(uri);
		post.setEntity(httpEntity);
		if (cookies != null) {
			for (Cookie c : cookies) {
				((AbstractHttpClient) this.client).getCookieStore().addCookie(c);
				
			}
		}
		HttpResponse response = this.client.execute(post);
		return response;
	}
	
	@Override
	public List<Cookie> PostCookies(String uri, HttpEntity httpEntity) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		HttpPost post = new HttpPost(uri);
		post.setEntity(httpEntity);
		this.client.execute(post);
		List<Cookie> cookies = ((AbstractHttpClient) this.client).getCookieStore().getCookies();
		// this.client.getConnectionManager().shutdown();
		return cookies;
	}
	
	@Override
	public HttpResponse Put(String uri, HttpEntity httpEntity) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		HttpPut put = new HttpPut(uri);
		put.setEntity(httpEntity);
		HttpResponse response = this.client.execute(put);
		return response;
	}
	
	@Override
	public HttpResponse Put(String uri, HttpEntity httpEntity, String username, String password) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		this.client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		HttpPut put = new HttpPut(uri);
		put.setEntity(httpEntity);
		HttpResponse response = this.client.execute(put);
		return response;
	}
	
	@Override
	public HttpResponse Delete(String uri) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		HttpDelete del = new HttpDelete(uri);
		HttpResponse response = this.client.execute(del);
		return response;
	}
	
	@Override
	public HttpResponse Trace(String uri) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		HttpTrace trace = new HttpTrace(uri);
		HttpResponse response = this.client.execute(trace);
		return response;
	}
	
	@Override
	public HttpResponse Options(String uri) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		HttpOptions options = new HttpOptions(uri);
		HttpResponse response = this.client.execute(options);
		return response;
	}
}
