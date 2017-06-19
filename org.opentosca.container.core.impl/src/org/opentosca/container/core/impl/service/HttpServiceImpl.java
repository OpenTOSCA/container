package org.opentosca.container.core.impl.service;

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
import org.opentosca.container.core.service.IHTTPService;

/**
 * This is an implementation of the
 * {@link org.opentosca.util.http.service.IHTTPService} interface. A lot of
 * methods currently offer only very basic functionality which could be extended
 * in the future if the need arises. All methods make use of the Apache
 * HttpComponents.
 */
public class HttpServiceImpl implements IHTTPService {
	
	DefaultHttpClient client;
	
	
	@Override
	public HttpResponse Get(final String uri, final List<Cookie> cookies) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		final HttpGet get = new HttpGet(uri);
		
		if (cookies != null) {
			for (final Cookie c : cookies) {
				((AbstractHttpClient) this.client).getCookieStore().addCookie(c);
				
			}
		}
		
		final HttpResponse response = this.client.execute(get);
		
		return response;
		// TODO Return something useful maybe... like an InputStream
	}
	
	@Override
	public HttpResponse Get(final String uri) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		final HttpGet get = new HttpGet(uri);
		final HttpResponse response = this.client.execute(get);
		
		return response;
		// TODO Return something useful maybe... like an InputStream
	}
	
	@Override
	public HttpResponse Get(final String uri, final String username, final String password) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		this.client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		final HttpGet get = new HttpGet(uri);
		final HttpResponse response = this.client.execute(get);
		
		return response;
		// TODO Return something useful maybe... like an InputStream
	}
	
	@Override
	public HttpResponse Head(final String uri) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		final HttpHead head = new HttpHead(uri);
		final HttpResponse response = this.client.execute(head);
		return response;
	}
	
	@Override
	public HttpResponse Post(final String uri, final HttpEntity httpEntity) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		final HttpPost post = new HttpPost(uri);
		post.setEntity(httpEntity);
		final HttpResponse response = this.client.execute(post);
		return response;
	}
	
	@Override
	public HttpResponse Post(final String uri, final HttpEntity httpEntity, final List<Cookie> cookies) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		final HttpPost post = new HttpPost(uri);
		post.setEntity(httpEntity);
		if (cookies != null) {
			for (final Cookie c : cookies) {
				((AbstractHttpClient) this.client).getCookieStore().addCookie(c);
				
			}
		}
		final HttpResponse response = this.client.execute(post);
		return response;
	}
	
	@Override
	public List<Cookie> PostCookies(final String uri, final HttpEntity httpEntity) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		final HttpPost post = new HttpPost(uri);
		post.setEntity(httpEntity);
		this.client.execute(post);
		final List<Cookie> cookies = ((AbstractHttpClient) this.client).getCookieStore().getCookies();
		// this.client.getConnectionManager().shutdown();
		return cookies;
	}
	
	@Override
	public HttpResponse Put(final String uri, final HttpEntity httpEntity) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		final HttpPut put = new HttpPut(uri);
		put.setEntity(httpEntity);
		final HttpResponse response = this.client.execute(put);
		return response;
	}
	
	@Override
	public HttpResponse Put(final String uri, final HttpEntity httpEntity, final String username, final String password) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		this.client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		final HttpPut put = new HttpPut(uri);
		put.setEntity(httpEntity);
		final HttpResponse response = this.client.execute(put);
		return response;
	}
	
	@Override
	public HttpResponse Delete(final String uri) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		final HttpDelete del = new HttpDelete(uri);
		final HttpResponse response = this.client.execute(del);
		return response;
	}
	
	@Override
	public HttpResponse Trace(final String uri) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		final HttpTrace trace = new HttpTrace(uri);
		final HttpResponse response = this.client.execute(trace);
		return response;
	}
	
	@Override
	public HttpResponse Options(final String uri) throws ClientProtocolException, IOException {
		this.client = new DefaultHttpClient();
		final HttpOptions options = new HttpOptions(uri);
		final HttpResponse response = this.client.execute(options);
		return response;
	}
}
