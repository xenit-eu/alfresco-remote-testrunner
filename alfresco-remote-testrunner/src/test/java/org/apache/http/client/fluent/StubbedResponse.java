package org.apache.http.client.fluent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public class StubbedResponse extends Response {

    public static StatusLine HTTP_200 = new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "");
    public static StatusLine HTTP_401 = new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_UNAUTHORIZED, "Unauthorized");


    public StubbedResponse(StatusLine status) {
        super(new BasicHttpResponse(status));
    }

    public StubbedResponse(StatusLine status, HttpEntity entity) {
        super(createHttpResponse(status, entity));
    }

    private static HttpResponse createHttpResponse(StatusLine status, HttpEntity entity)
    {
        HttpResponse response = new BasicHttpResponse(status);
        response.setEntity(entity);
        return response;
    }
}
