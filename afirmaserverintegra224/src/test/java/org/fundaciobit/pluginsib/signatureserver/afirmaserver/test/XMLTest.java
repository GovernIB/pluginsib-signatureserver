package org.fundaciobit.pluginsib.signatureserver.afirmaserver.test;

import org.junit.Assert;
import org.fundaciobit.pluginsib.core.v3.utils.FileUtils;
import org.fundaciobit.pluginsib.signatureserver.afirmaserver.XMLUtil;
import org.junit.Test;

import java.io.InputStream;

/**
 * 
 * @author anadal
 *
 */
public class XMLTest {

    @Test
    public void testIsXML() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/testfiles/sample.xml");
        byte[] byteArray = FileUtils.toByteArray(inputStream);
        Assert.assertTrue(XMLUtil.isXml(byteArray));
    }

    @Test
    public void testIsXML2() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/testfiles/prova.xml");
        byte[] byteArray = FileUtils.toByteArray(inputStream);
        Assert.assertTrue(XMLUtil.isXml(byteArray));
    }
}
