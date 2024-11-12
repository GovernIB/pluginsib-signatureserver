package org.fundaciobit.pluginsib.signatureserver.afirmaserver.test;

import org.fundaciobit.pluginsib.signature.api.CommonInfoSignature;
import org.fundaciobit.pluginsib.signature.api.FileInfoSignature;
import org.fundaciobit.pluginsib.signature.api.SignaturesSet;
import org.fundaciobit.pluginsib.signature.api.constants.SignatureTypeFormEnumForUpgrade;
import org.fundaciobit.pluginsib.signatureserver.afirmaserver.AfirmaServerSignatureServerPlugin;
import org.fundaciobit.pluginsib.signatureserver.api.ISignatureServerPlugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

import static org.fundaciobit.pluginsib.signature.api.StatusSignaturesSet.STATUS_FINAL_OK;

/**
 * 
 * @author anadal
 *
 */
public class AfirmaServerSignatureServerPluginIT {

    private static ISignatureServerPlugin plugin;

    public static String RESULTS_PATH = "./results";

    public static void main(String[] args) {

        try {

            AfirmaServerSignatureServerPluginIT.setup("org.fundaciobit.exemple.signatureserverplugins.3.");

            AfirmaServerSignatureServerPluginIT tester = new AfirmaServerSignatureServerPluginIT();

            /*
            tester.testSignBigPdf();
            
            tester.testSignPdf();
            
            tester.testSignPdfSignat();
            */

            
            tester.testSignXAdES_InternallyDetached();
            
            
            tester.testSignXAdES_Attached_Enveloping();
            
            tester.testSignXAdES_Attached_Enveloped();

            tester.testSignXAdES_Detached_No_Suportat();
            
           
            //tester.testSignCAdES_InternallyDetached_No_Suportat();

            

            //tester.testSignCAdES_Attached_Enveloped_No_Suportat();
            
            //tester.testSignCAdES_Attached_Enveloping();

            //tester.testSignCAdES_Detached();

            System.out.println(" --- FINAL --- ");

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @BeforeClass
    public static void setup() throws IOException {
        setup("");
    }

    public static void setup(String propertyBase) throws IOException {
        Properties props = new Properties();
        try (InputStream inputStream = new FileInputStream("config/plugin.properties")) {
            props.load(inputStream);
        }
        plugin = new AfirmaServerSignatureServerPlugin(propertyBase, props);
    }

    @Test
    public void testName() {
        Locale loc = new Locale("ca");
        Assert.assertEquals("Plugin de Firma en Servidor de @firma Federat", plugin.getName(loc));
    }

    // =================================
    // ============  PDF ===============
    // =================================

    @Test
    public void testSignBigPdf() throws URISyntaxException {

        long start = System.currentTimeMillis();

        try {
            signPdf("/testfiles/BIG_19Mb.pdf", "testSignBigPdf");
        } finally {
            System.err.println("Temps total: " + (System.currentTimeMillis() - start));
        }
    }

    @Test
    public void testSignPdf() throws URISyntaxException {

        signPdf("/testfiles/normal.pdf", "testSignPdf");
    }

    @Test
    public void testSignPdfSignat() throws URISyntaxException {

        signPdf("/testfiles/signat.pdf", "testSignPdfSignat");
    }

    protected void signPdf(String fitxerASignar, String resultName) throws URISyntaxException {
        File file = getFile(fitxerASignar);
        String signType = FileInfoSignature.SIGN_TYPE_PADES;
        int signMode = FileInfoSignature.SIGN_MODE_ATTACHED_ENVELOPED;
        SignaturesSet signaturesSet = getSignaturesSet(
                getFileInfoSignature(file, FileInfoSignature.PDF_MIME_TYPE, signType, signMode));
        SignaturesSet set = plugin.signDocuments(signaturesSet, null, null);
        validarStatus(set);
        File rename = new File("result_" + resultName + "_" + System.currentTimeMillis() + ".pdf");
        File signedData = set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData();
        Assert.assertTrue(signedData.renameTo(rename));

        saveResults(resultName, file, rename, signType, signMode);

        System.out.println("Fitxer resultat: " + rename);
    }

    public void saveResults(String name, File fileOriginal, File fileSigned, String signType, int signMode) {
        try {
            File resultsDir = new File(RESULTS_PATH);
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }
            Properties props = new Properties();
            props.setProperty("name", name);
            // Copiam el fitxer original al directori de resultats
            {
                File fileOriginalCopy = new File(resultsDir, name + "_" + fileOriginal.getName());
                if (fileOriginalCopy.exists()) {
                    fileOriginalCopy.delete();
                }

                Files.copy(fileOriginal.toPath(), fileOriginalCopy.toPath());
                props.setProperty("fileOriginal", fileOriginalCopy.getName());
            }
            // Copiam el fitxer signat al directori de resultats
            {
                File fileSignedCopy = new File(resultsDir, name + "_" + fileSigned.getName());
                if (fileSignedCopy.exists()) {
                    fileSignedCopy.delete();
                }
                Files.copy(fileSigned.toPath(), fileSignedCopy.toPath());
                props.setProperty("fileSigned", fileSignedCopy.getName());
            }
            props.setProperty("signType", signType);
            props.setProperty("signMode", String.valueOf(signMode));

            // Guardam les propietats dins d'un fitxer "name".properties
            File propsFile = new File(resultsDir, name + ".properties");
            FileOutputStream fis = new FileOutputStream(propsFile);
            props.store(fis, "Resultats de la signatura ");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =================================
    // ============  XAdES =============
    // =================================

    @Test
    public void testSignXAdES_InternallyDetached() throws Exception {
        signXades(FileInfoSignature.SIGN_MODE_INTERNALLY_DETACHED, "testSignXAdES_InternallyDetached");
    }

    @Test
    public void testSignXAdES_Attached_Enveloping() throws Exception {
        signXades(FileInfoSignature.SIGN_MODE_ATTACHED_ENVELOPING, "testSignXAdES_Attached_Enveloping");
    }

    @Test
    public void testSignXAdES_Attached_Enveloped() throws Exception {
        signXades(FileInfoSignature.SIGN_MODE_ATTACHED_ENVELOPED, "testSignXAdES_Attached_Enveloped");
    }

    @Test
    public void testSignXAdES_Detached_No_Suportat() throws Exception {
        try {
            signXades(FileInfoSignature.SIGN_MODE_DETACHED, "testSignXAdES_Detached_No_Suportat");
            throw new Exception("S'esperava un error de mode no suportat i no s'ha llançat cap excepció.");
        } catch (java.lang.AssertionError e) {
            // OK
            System.out.println("testSignXAdES_Detached_No_Suportat(): OK");
        }
    }

    protected void signXades(int signMode, String resultName) throws Exception {

        File file = getFile("/testfiles/sample.xml");

        final String signType = FileInfoSignature.SIGN_TYPE_XADES;

        SignaturesSet signaturesSet = getSignaturesSet(
                getFileInfoSignature(file, "application/xml", signType, signMode));
        SignaturesSet set = plugin.signDocuments(signaturesSet, null, null);
        validarStatus(set);

        File signedData = set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData();
        File rename = new File("result_" + resultName + "_" + System.currentTimeMillis() + ".xsig");
        System.out.println(" resultat: " + rename);
        Assert.assertTrue(signedData.renameTo(rename));

        saveResults(resultName, file, rename, signType, signMode);
    }

    // =================================
    // ============  CAdES =============
    // =================================

    @Test
    public void testSignCAdES_InternallyDetached_No_Suportat() throws Exception {

        final String methodName = "testSignCAdES_InternallyDetached_No_Suportat";
        try {
            signCades(FileInfoSignature.SIGN_MODE_INTERNALLY_DETACHED, methodName);
            throw new Exception("S'esperava un error de mode no suportat i no s'ha llançat cap excepció.");
        } catch (java.lang.AssertionError e) {
            // OK
            System.out.println(methodName + "(): OK");
        }
    }

    @Test
    public void testSignCAdES_Attached_Enveloping() throws Exception {
        signCades(FileInfoSignature.SIGN_MODE_ATTACHED_ENVELOPING, "testSignCAdES_Attached_Enveloping");
    }

    @Test
    public void testSignCAdES_Attached_Enveloped_No_Suportat() throws Exception {
        try {
            signCades(FileInfoSignature.SIGN_MODE_ATTACHED_ENVELOPED, "testSignCAdES_Attached_Enveloped_No_Suportat");
            throw new Exception("S'esperava un error de mode no suportat i no s'ha llançat cap excepció.");
        } catch (java.lang.AssertionError e) {
            // OK
            System.out.println("testSignXAdES_Attached_Enveloped_No_Suportat(): OK");
        }
    }

    @Test
    public void testSignCAdES_Detached() throws Exception {
        signCades(FileInfoSignature.SIGN_MODE_DETACHED, "testSignCAdES_Detached");
    }

    protected void signCades(int signMode, String resultName) throws Exception {

        File file = getFile("/testfiles/binari.bin");

        final String signType = FileInfoSignature.SIGN_TYPE_CADES;
        SignaturesSet signaturesSet = getSignaturesSet(
                getFileInfoSignature(file, "application/xml", signType, signMode));
        SignaturesSet set = plugin.signDocuments(signaturesSet, null, null);
        validarStatus(set);

        File signedData = set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData();
        File rename = new File("result_" + resultName + "_" + System.currentTimeMillis() + ".csig");
        System.out.println(" resultat: " + rename);
        Assert.assertTrue(signedData.renameTo(rename));
        
        saveResults(resultName, file, rename, signType, signMode);
    }

    // =================================
    // =======  UPGRADE PDF ============
    // =================================

    @Test
    public void testSignUpgradeSignat() throws Exception {

        File signedFile = getFile("/testfiles/signat.pdf");
        byte[] signedFileBytes = Files.readAllBytes(signedFile.toPath());

        byte[] upgradeSignatureBytes = plugin.upgradeSignature(signedFileBytes, null,
                SignatureTypeFormEnumForUpgrade.PAdES_LT_LEVEL, null, null);

        File upgradeSignatureFile = new File("upgrade" + System.currentTimeMillis() + ".pdf");
        Files.write(upgradeSignatureFile.toPath(), upgradeSignatureBytes);
    }

    // =================================
    // ===========  UTILS ==============
    // =================================

    private void validarStatus(SignaturesSet set) {

        if (STATUS_FINAL_OK != set.getStatusSignaturesSet().getStatus()) {
            String msg = "S'ha rebut un estat general diferent de OK:" + "\n - Status: "
                    + set.getStatusSignaturesSet().getStatus() + "\n - Error: "
                    + set.getStatusSignaturesSet().getErrorMsg();
            System.err.println(msg);
            throw new java.lang.AssertionError(msg, null);
        }
        FileInfoSignature[] fileInfoSignatureArray = set.getFileInfoSignatureArray();
        for (int i = 0; i < fileInfoSignatureArray.length; i++) {
            //Assert.assertEquals(fileStatus[i], fileInfoSignatureArray[i].getStatusSignature().getStatus());

            if (STATUS_FINAL_OK != fileInfoSignatureArray[i].getStatusSignature().getStatus()) {
                String msg = "S'ha rebut un estat de la firma amb ID " + fileInfoSignatureArray[i].getSignID()
                        + "  diferent de OK:" + "\n - Status: "
                        + fileInfoSignatureArray[i].getStatusSignature().getStatus() + "\n - Error: "
                        + fileInfoSignatureArray[i].getStatusSignature().getErrorMsg();
                System.err.println(msg);
                throw new java.lang.AssertionError(msg, null);
            }

        }
    }

    private SignaturesSet getSignaturesSet(FileInfoSignature... fileInfoSignatures) {
        return new SignaturesSet("XZY", getCommonInfoSignature(), fileInfoSignatures);
    }

    private CommonInfoSignature getCommonInfoSignature() {
        return new CommonInfoSignature("ca", "", null, null);
    }

    private FileInfoSignature getFileInfoSignature(File file, String mime, String signType, int signMode) {

        boolean userRequiresTimestamp = true;
        return new FileInfoSignature("1", file, null, mime, file.getName(), null, null, null, 1, "ca",
                FileInfoSignature.SIGN_OPERATION_SIGN, signType, FileInfoSignature.SIGN_ALGORITHM_SHA256, signMode,
                FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT, null, null, null, userRequiresTimestamp, null, null,
                null, null, null, null, null);
    }

    private File getFile(String resourceNAme) throws URISyntaxException {
        URL resource = getClass().getResource(resourceNAme);
        Objects.requireNonNull(resource, () -> "No s'ha trobat el recurs " + resourceNAme);
        return new File(resource.toURI());
    }
}
