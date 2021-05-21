package eu.xenit.testing.integrationtesting.internal.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;

public class TestResultsReceiver {

    private final BufferedReader inputStream;

    public TestResultsReceiver(InputStream inputStream) {
        this.inputStream = new BufferedReader(new InputStreamReader(inputStream));

    }

    public Message receive() throws IOException {
        String line = inputStream.readLine();
        if(line == null) {
            return null;
        }
        int firstSpace = line.indexOf(' ');
        MessageType messageType = MessageType.valueOf(line.substring(0, firstSpace));
        String serializedObject = line.substring(firstSpace+1);
        Object object = SerializationUtils.deserialize(Base64.decodeBase64(serializedObject));

        return new Message(messageType, object);
    }

}
