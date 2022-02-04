package org.opentosca.container.core.impl.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.opentosca.container.core.service.IHTTPService;
import org.springframework.stereotype.Service;

/**
 * This is an implementation of the {@link IHTTPService} interface. A lot of methods currently offer only very basic
 * functionality which could be extended in the future if the need arises. All methods make use of the Apache
 * HttpComponents.
 */
@Service
public class HttpServiceImpl implements IHTTPService {

    @Override
    public HttpResponse Get(final String uri, final List<Cookie> cookies) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        client.setRedirectStrategy(new LaxRedirectStrategy());
        final HttpGet get = new HttpGet(uri);

        if (cookies != null) {
            for (final Cookie c : cookies) {
                client.getCookieStore().addCookie(c);
            }
        }

        final HttpResponse response = client.execute(get);

        return response;
        // TODO Return something useful maybe... like an InputStream
    }

    @Override
    public HttpResponse Get(final String uri, final Map<String, String> headers) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        client.setRedirectStrategy(new LaxRedirectStrategy());
        final HttpGet get = new HttpGet(uri);

        for (final String header : headers.keySet()) {
            get.addHeader(header, headers.get(header));
        }

        final HttpResponse response = client.execute(get);

        return response;
        // TODO Return something useful maybe... like an InputStream
    }

    @Override
    public HttpResponse Get(final String uri) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        client.setRedirectStrategy(new LaxRedirectStrategy());
        final HttpGet get = new HttpGet(uri);
        final HttpResponse response = client.execute(get);

        return response;
        // TODO Return something useful maybe... like an InputStream
    }

    @Override
    public HttpResponse Get(final String uri, final String username, final String password) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        client.setRedirectStrategy(new LaxRedirectStrategy());
        final HttpGet get = new HttpGet(uri);
        final HttpResponse response = client.execute(get);

        return response;
        // TODO Return something useful maybe... like an InputStream
    }

    @Override
    public HttpResponse Post(final String uri, final HttpEntity httpEntity) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        client.setRedirectStrategy(new LaxRedirectStrategy());
        final HttpPost post = new HttpPost(uri);
        post.setEntity(httpEntity);
        final HttpResponse response = client.execute(post);
        return response;
    }

    @Override
    public HttpResponse Post(final String uri, final HttpEntity httpEntity, final Header... header) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        client.setRedirectStrategy(new LaxRedirectStrategy());
        final HttpPost post = new HttpPost(uri);
        post.setEntity(httpEntity);
        post.setHeaders(header);
        final HttpResponse response = client.execute(post);
        return response;
    }

    @Override
    public HttpResponse Post(final String uri, final HttpEntity httpEntity,
                             final List<Cookie> cookies) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        client.setRedirectStrategy(new LaxRedirectStrategy());
        final HttpPost post = new HttpPost(uri);
        post.setEntity(httpEntity);
        if (cookies != null) {
            for (final Cookie c : cookies) {
                client.getCookieStore().addCookie(c);
            }
        }
        final HttpResponse response = client.execute(post);
        return response;
    }

    @Override
    public HttpResponse Put(final String uri, final HttpEntity httpEntity) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        client.setRedirectStrategy(new LaxRedirectStrategy());
        final HttpPut put = new HttpPut(uri);
        put.setEntity(httpEntity);
        final HttpResponse response = client.execute(put);
        return response;
    }

    @Override
    public HttpResponse Options(final String uri) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        client.setRedirectStrategy(new LaxRedirectStrategy());
        final HttpOptions options = new HttpOptions(uri);
        final HttpResponse response = client.execute(options);
        return response;
    }
}
