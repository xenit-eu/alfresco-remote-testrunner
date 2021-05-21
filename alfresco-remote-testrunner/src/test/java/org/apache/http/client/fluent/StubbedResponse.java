/**
 * Copyright 2021 Xenit Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
