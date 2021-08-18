package org.fundaciobit.plugins.signatureserver.tester;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.fundaciobit.pluginsib.core.utils.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class SignServletIT {

    CloseableHttpClient httpclient;
    HttpPost httpPost;

    @Before
    public void setup() {
        httpclient = HttpClients.createDefault();
        httpPost = new HttpPost("http://localhost:8080/signatureservertester/sign");
    }

    @After
    public void tearDown() throws IOException {
        httpclient.close();
    }

    @Test
    public void testSignPdfAfirmaServer() throws IOException, URISyntaxException {
        HttpEntity reqEntity = getHttpEntity("/testfiles/normal.pdf", "application/pdf", "afirmaserver");
        httpPost.setEntity(reqEntity);

        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            HttpEntity resEntity = response.getEntity();
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("application/pdf", resEntity.getContentType().getValue());
            saveResult(resEntity, "afirmaserver", ".pdf");
        }
    }

    @Test
    public void testSignPdfAfirmaLibs() throws IOException, URISyntaxException {

        HttpEntity reqEntity = getHttpEntity("/testfiles/normal.pdf", "application/pdf", "afirmalibs");
        httpPost.setEntity(reqEntity);

        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            HttpEntity resEntity = response.getEntity();
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("application/pdf", resEntity.getContentType().getValue());

            saveResult(resEntity, "afirmalibs", ".pdf");
        }
    }

    @Test
    public void testSignJpegAfirmaServer() throws IOException, URISyntaxException {
        HttpEntity reqEntity = getHttpEntity("/testfiles/imatge.jpg", "image/jpeg", "afirmaserver");
        httpPost.setEntity(reqEntity);

        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            HttpEntity resEntity = response.getEntity();
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("application/octet-stream", resEntity.getContentType().getValue());
            saveResult(resEntity, "afirmaserver", ".csig");
        }
    }

    @Test
    public void testSignJpegAfirmaLibs() throws IOException, URISyntaxException {
        HttpEntity reqEntity = getHttpEntity("/testfiles/imatge.jpg", "image/jpeg", "afirmalibs");
        httpPost.setEntity(reqEntity);

        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            HttpEntity resEntity = response.getEntity();
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("application/octet-stream", resEntity.getContentType().getValue());
            saveResult(resEntity, "afirmalibs", ".csig");
        }
    }


    @Test
    public void testSignXmlAfirmaServer() throws IOException, URISyntaxException {
        HttpEntity reqEntity = getHttpEntity("/testfiles/sample.xml", "text/xml", "afirmaserver");
        httpPost.setEntity(reqEntity);

        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            HttpEntity resEntity = response.getEntity();
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("text/xml", resEntity.getContentType().getValue());
            saveResult(resEntity, "afirmaserver", ".xsig");
        }
    }

    @Test
    public void testSignXmlAfirmaLibs() throws IOException, URISyntaxException {
        HttpEntity reqEntity = getHttpEntity("/testfiles/sample.xml", "text/xml", "afirmalibs");
        httpPost.setEntity(reqEntity);

        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            HttpEntity resEntity = response.getEntity();
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Assert.assertEquals("text/xml", resEntity.getContentType().getValue());
            saveResult(resEntity, "afirmalibs", ".xsig");
        }
    }

    private HttpEntity getHttpEntity(String s, String s2, String afirmaserver) throws URISyntaxException {
        File file = getFile(s);
        return MultipartEntityBuilder.create()
                .addPart("fitxer", new FileBody(file, ContentType.create(s2)))
                .addTextBody("pluginName", afirmaserver)
                .build();
    }

    private File getFile(String resourceName) throws URISyntaxException {
        URL resource = getClass().getResource(resourceName);
        Objects.requireNonNull(resource, () -> "No s'ha trobat el recurs " + resourceName);
        return new File(resource.toURI());
    }

    private void saveResult(HttpEntity resEntity, String afirmalibs, String s) throws IOException {
        File result = new File(afirmalibs + System.currentTimeMillis() + s);
        try (OutputStream os = new FileOutputStream(result);
             InputStream is = resEntity.getContent()) {
            FileUtils.copy(is, os);
        }
    }
}
