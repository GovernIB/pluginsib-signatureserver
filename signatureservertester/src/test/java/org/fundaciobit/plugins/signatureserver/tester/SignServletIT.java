package org.fundaciobit.plugins.signatureserver.tester;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class SignServletIT {

    @Test
    public void testSignPdf() throws IOException, URISyntaxException {

        try (var httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("http://localhost:8080/signatureservertester/sign");

            File file = getFile("/testfiles/normal.pdf");
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("fitxer", new FileBody(file))
                    .build();

            httpPost.setEntity(reqEntity);

            try (var response = httpclient.execute(httpPost)) {
                HttpEntity resEntity = response.getEntity();
                Assert.assertEquals(25320, resEntity.getContentLength());
                Assert.assertEquals("application/pdf", resEntity.getContentType().getValue());

                File result = new File("result" + System.currentTimeMillis() + ".pdf");
                try (var os = new FileOutputStream(result);
                     var is = resEntity.getContent()) {
                    is.transferTo(os);
                }
            }
        }

    }

    private File getFile(String resourceNAme) throws URISyntaxException {
        URL resource = getClass().getResource(resourceNAme);
        Objects.requireNonNull(resource, () -> "No s'ha trobat el recurs " + resourceNAme);
        return new File(resource.toURI());
    }
}
