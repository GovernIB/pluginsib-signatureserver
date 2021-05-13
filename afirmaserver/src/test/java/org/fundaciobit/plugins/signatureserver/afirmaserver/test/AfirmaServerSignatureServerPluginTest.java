package org.fundaciobit.plugins.signatureserver.afirmaserver.test;

import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.SignaturesSet;
import org.fundaciobit.plugins.signatureserver.afirmaserver.AfirmaServerSignatureServerPlugin;
import org.fundaciobit.plugins.signatureserver.api.ISignatureServerPlugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import static org.fundaciobit.plugins.signature.api.StatusSignaturesSet.STATUS_FINAL_OK;

public class AfirmaServerSignatureServerPluginTest {

    private static ISignatureServerPlugin plugin;

    @BeforeClass
    public static void setup() {
        Properties props = new Properties();
        props.put("plugins.signatureserver.afirmaserver.applicationID", "CAIBDEV.PASSARELA_PFI");
        props.put("plugins.signatureserver.afirmaserver.debug", "false");
        props.put("plugins.signatureserver.afirmaserver.printxml", "false");
        props.put("plugins.signatureserver.afirmaserver.defaultAliasCertificate", "afirmades-firma");
        props.put("plugins.signatureserver.afirmaserver.TransformersTemplatesPath", "C:\\Users\\areus\\FEINA\\projectes\\portafib\\afirma\\transformersTemplates");
        props.put("plugins.signatureserver.afirmaserver.endpoint", "https://afirmades.caib.es:4430/afirmaws/services/DSSAfirmaSign");
        props.put("plugins.signatureserver.afirmaserver.endpoint_upgrade", "https://afirmades.caib.es:4430/afirmaws/services/DSSAfirmaVerify");
        props.put("plugins.signatureserver.afirmaserver.authorization.username", "passarela_pfi");
        props.put("plugins.signatureserver.afirmaserver.authorization.password", "passarela_pfi");
        plugin = new AfirmaServerSignatureServerPlugin("", props);
    }

    @Test
    public void testName() {
        Locale loc = new Locale("ca");
        Assert.assertEquals("Plugin de Firma en Servidor de @firma Federat", plugin.getName(loc));
    }

    @Test
    public void testSignPdf() throws URISyntaxException {
        CommonInfoSignature commonInfoSignature = new CommonInfoSignature("ca", "", null, null);

        URL resource = getClass().getResource("/testfiles/normal.pdf");
        assert resource != null;
        File file = new File(resource.toURI());

        FileInfoSignature fileInfoSignature = new FileInfoSignature("1", file, null,
                FileInfoSignature.PDF_MIME_TYPE, "hola.pdf", null, null, null, 1, "ca",
                FileInfoSignature.SIGN_OPERATION_SIGN, FileInfoSignature.SIGN_TYPE_PADES, FileInfoSignature.SIGN_ALGORITHM_SHA256,
                FileInfoSignature.SIGN_MODE_IMPLICIT, FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT, null, null,
                null, false, null, null, null, null,
                null, null, null);

        SignaturesSet signaturesSet = new SignaturesSet("XZY",
                commonInfoSignature,
                new FileInfoSignature[] {fileInfoSignature});

        SignaturesSet set = plugin.signDocuments(signaturesSet, null, null);

        Assert.assertEquals(STATUS_FINAL_OK, set.getStatusSignaturesSet().getStatus());
        Assert.assertEquals(STATUS_FINAL_OK, set.getFileInfoSignatureArray()[0].getStatusSignature().getStatus());

        File signedData = set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData();
        Assert.assertTrue(signedData.renameTo(new File("result" + System.currentTimeMillis() + ".pdf")));
    }

    @Test
    public void testSignXml() throws URISyntaxException {
        CommonInfoSignature commonInfoSignature = new CommonInfoSignature("ca", "", null, null);

        URL resource = getClass().getResource("/testfiles/sample.xml");
        assert resource != null;
        File file = new File(resource.toURI());

        FileInfoSignature fileInfoSignature = new FileInfoSignature("1", file, null,
                "application/xml", "sample.xml", null, null, null, 1, "ca",
                FileInfoSignature.SIGN_OPERATION_SIGN, FileInfoSignature.SIGN_TYPE_XADES, FileInfoSignature.SIGN_ALGORITHM_SHA256,
                FileInfoSignature.SIGN_MODE_IMPLICIT, FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT, null, null,
                null, false, null, null, null, null,
                null, null, null);

        SignaturesSet signaturesSet = new SignaturesSet("XZY",
                commonInfoSignature,
                new FileInfoSignature[] {fileInfoSignature});

        SignaturesSet set = plugin.signDocuments(signaturesSet, null, null);

        Assert.assertEquals(STATUS_FINAL_OK, set.getStatusSignaturesSet().getStatus());
        Assert.assertEquals(STATUS_FINAL_OK, set.getFileInfoSignatureArray()[0].getStatusSignature().getStatus());

        File signedData = set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData();
        Assert.assertTrue(signedData.renameTo(new File("result" + System.currentTimeMillis() + ".xsig")));
    }
}
