package org.fundaciobit.plugins.signatureserver.afirmaserver.test;

import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.SignaturesSet;
import org.fundaciobit.plugins.signature.api.StatusSignature;
import org.fundaciobit.plugins.signatureserver.afirmaserver.AfirmaServerSignatureServerPlugin;
import org.fundaciobit.plugins.signatureserver.api.ISignatureServerPlugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

import static org.fundaciobit.plugins.signature.api.StatusSignaturesSet.STATUS_FINAL_OK;

public class AfirmaServerSignatureServerPluginIT {

    private static ISignatureServerPlugin plugin;

    @BeforeClass
    public static void setup() throws IOException {
        Properties props = new Properties();
        try (var inputStream = new FileInputStream("plugin.properties")) {
            props.load(inputStream);
        }
        plugin = new AfirmaServerSignatureServerPlugin("", props);
    }

    @Test
    public void testName() {
        Locale loc = new Locale("ca");
        Assert.assertEquals("Plugin de Firma en Servidor de @firma Federat", plugin.getName(loc));
    }

    @Test
    public void testSignPdf() throws URISyntaxException {

        File file = getFile("/testfiles/normal.pdf");

        SignaturesSet signaturesSet = getSignaturesSet(
                getFileInfoSignature(file, FileInfoSignature.PDF_MIME_TYPE, FileInfoSignature.SIGN_TYPE_PADES)
        );
        SignaturesSet set = plugin.signDocuments(signaturesSet, null, null);
        validarStatus(set, STATUS_FINAL_OK, STATUS_FINAL_OK);

        System.out.println(set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData().getName());
        File signedData = set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData();
        Assert.assertTrue(signedData.renameTo(new File("result" + System.currentTimeMillis() + ".pdf")));
    }

    @Test
    public void testSignXml() throws URISyntaxException {

        File file = getFile("/testfiles/sample.xml");

        SignaturesSet signaturesSet = getSignaturesSet(
                getFileInfoSignature(file,"application/xml", FileInfoSignature.SIGN_TYPE_XADES)
        );

        SignaturesSet set = plugin.signDocuments(signaturesSet, null, null);

        validarStatus(set, STATUS_FINAL_OK, STATUS_FINAL_OK);

        System.out.println(set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData().getName());
        File signedData = set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData();
        Assert.assertTrue(signedData.renameTo(new File("result" + System.currentTimeMillis() + ".xsig")));
    }

    private void validarStatus(SignaturesSet set, int statusGeneral, int... fileStatus) {
        Assert.assertEquals(statusGeneral, set.getStatusSignaturesSet().getStatus());
        FileInfoSignature[] fileInfoSignatureArray = set.getFileInfoSignatureArray();
        for (int i = 0; i < fileInfoSignatureArray.length; i++) {
            Assert.assertEquals(fileStatus[i], fileInfoSignatureArray[i].getStatusSignature().getStatus());
        }
    }

    private SignaturesSet getSignaturesSet(FileInfoSignature... fileInfoSignatures) {
        return new SignaturesSet("XZY",
                getCommonInfoSignature(),
                fileInfoSignatures);
    }

    private CommonInfoSignature getCommonInfoSignature() {
        return new CommonInfoSignature("ca", "", null, null);
    }

    private FileInfoSignature getFileInfoSignature(File file, String mime, String signType) {

        return new FileInfoSignature("1", file, null,
                mime, file.getName(), null, null, null, 1, "ca",
                FileInfoSignature.SIGN_OPERATION_SIGN, signType, FileInfoSignature.SIGN_ALGORITHM_SHA256,
                FileInfoSignature.SIGN_MODE_IMPLICIT, FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT, null, null,
                null, false, null, null, null, null,
                null, null, null);
    }

    private File getFile(String resourceNAme) throws URISyntaxException {
        URL resource = getClass().getResource(resourceNAme);
        Objects.requireNonNull(resource, () -> "No s'ha trobat el recurs " + resourceNAme);
        return new File(resource.toURI());
    }
}
