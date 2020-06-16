package org.opentosca.container.core.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.cookie.Cookie;

/**
 * This interface defines the standard HTTP commands as methods, plus some specific methods mainly created for the AAR
 * plug-in of the IAEngine, that are provided by the HTTPService.
 *
 * @see org.opentosca.iaengine.plugins.aaraxis.service.impl
 */
public interface IHTTPService {

    public HttpResponse Get(String uri, List<Cookie> cookies) throws ClientProtocolException, IOException;

    /**
     * Executes a HTTP GET command.
     *
     * @param uri - Resource URI
     * @return Response Message
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse Get(String uri) throws ClientProtocolException, IOException;

    /**
     * Executes a HTTP GET command.
     *
     * @param uri     - Resource URI
     * @param headers - map of headers and values
     * @return Response Message
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse Get(String uri, Map<String, String> headers) throws ClientProtocolException, IOException;

    /**
     * Executes a HTTP GET command.
     *
     * @param uri
     * @param username
     * @param password
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse Get(String uri, String username, String password) throws ClientProtocolException, IOException;

    /**
     * Executes a HTTP HEAD command.
     *
     * @param uri - Resource URI
     * @return Response Message
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse Head(String uri) throws ClientProtocolException, IOException;

    /**
     * Executes a HTTP POST command.
     *
     * @param uri        - Resource URI
     * @param httpEntity - Payload
     * @return Response Message
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse Post(String uri, HttpEntity httpEntity) throws ClientProtocolException, IOException;

    public HttpResponse Post(String uri, HttpEntity httpEntity, Header... header) throws ClientProtocolException,
        IOException;

    public HttpResponse Post(String uri, HttpEntity httpEntity, List<Cookie> cookies) throws ClientProtocolException,
        IOException;

    public List<Cookie> PostCookies(String uri, HttpEntity httpEntity) throws ClientProtocolException, IOException;

    /**
     * Executes a HTTP PUT command.
     *
     * @param uri        - Resource URI
     * @param httpEntity - Payload
     * @return Response Message
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse Put(String uri, HttpEntity httpEntity) throws ClientProtocolException, IOException;

    /**
     * @param uri
     * @param httpEntity
     * @param username
     * @param password
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse Put(String uri, HttpEntity httpEntity, String username,
                            String password) throws ClientProtocolException, IOException;

    /**
     * Executes a HTTP DELETE command.
     *
     * @param uri - Resource URI
     * @return Response Message
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse Delete(String uri) throws ClientProtocolException, IOException;

    /**
     * Executes a HTTP TRACE command.
     *
     * @param uri - Resource URI
     * @return Response Message
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse Trace(String uri) throws ClientProtocolException, IOException;

    /**
     * Executes a HTTP OPTIONS command.
     *
     * @param uri - Resource URI
     * @return Response Message
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResponse Options(String uri) throws ClientProtocolException, IOException;
}
