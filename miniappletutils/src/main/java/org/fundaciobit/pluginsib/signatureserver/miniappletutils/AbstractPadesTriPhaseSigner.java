package org.fundaciobit.pluginsib.signatureserver.miniappletutils;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.fundaciobit.pluginsib.signature.api.CommonInfoSignature;
import org.fundaciobit.pluginsib.signature.api.FileInfoSignature;

/**
 * 
 * @author anadal (u80067)
 * 17 abr 2026 12:37:08
 */
public abstract class AbstractPadesTriPhaseSigner extends MiniAppletClassLoader {

    // final PdfSignResult pre
    private Object pre = null;

    // Original file with TimeStamp
    private byte[] data;

    protected final CommonInfoSignature commonInfoSignature;
    protected final FileInfoSignature fileInfo;
    protected final String timeStampURL;
    protected final X509Certificate certificate;
    protected final Date signDate;

    public AbstractPadesTriPhaseSigner(CommonInfoSignature commonInfoSignature, FileInfoSignature fileInfo,
            X509Certificate certificate) {
        super();
        this.commonInfoSignature = commonInfoSignature;
        this.fileInfo = fileInfo;
        this.certificate = certificate;
        this.signDate = new Date();
        this.timeStampURL = null;
    }

    public AbstractPadesTriPhaseSigner(CommonInfoSignature commonInfoSignature, FileInfoSignature fileInfo,
            X509Certificate certificate, String timeStampURL) {
        super();
        this.commonInfoSignature = commonInfoSignature;
        this.fileInfo = fileInfo;
        this.certificate = certificate;
        this.signDate = new Date();
        this.timeStampURL = timeStampURL;
    }

    public byte[] fullSign() throws Exception {

        // *************** PreFirma
        byte[] presign = this.step1_PreSign();

        // *************** Firma
        final byte[] interSign = this.step2_SignHash(presign);

        // *************** PostFirma
        return this.step3_PostSign(interSign);

    }

    public byte[] step1_PreSign() throws Exception {

        final byte[] inPDF = Files.readAllBytes(this.fileInfo.getFileToSign().toPath());

        final String algorithm = MiniAppletUtils.convertAlgorithm(this.fileInfo);

        final java.security.cert.Certificate[] certChain = new java.security.cert.Certificate[] { this.certificate };

        Properties params;
        {

            String timeStampUrl = null;

            MiniAppletSignInfo info;
            info = MiniAppletUtils.convertLocalSignature(this.commonInfoSignature, fileInfo, timeStampUrl,
                    this.certificate);

            params = info.getProperties();

        }

        //return signer.step1_PreSign(inPDF, algorithm, certChain, params);

        // TODO checkIText();

        final java.security.cert.Certificate[] certificateChain = Boolean
                .parseBoolean(params.getProperty("includeOnlySignningCertificate", Boolean.FALSE.toString())) ? //$NON-NLS-1$
                        new X509Certificate[] { (X509Certificate) certChain[0] } : certChain;

        final GregorianCalendar signTime = new GregorianCalendar();

        //  --------------- ORIGINAL
        // Sello de stiempo
        /*
        byte[] data;
        data = es.gob.afirma.signers.pades.PdfTimestamper.timestampPdf(inPDF, extraParams, signTime);
        
        // Prefirma
        final PdfSignResult pre;
        
        pre = PAdESTriPhaseSigner
            .preSign(algorithm, data, certificateChain, signTime, extraParams);
        
        final byte[] interSign = signHash(algorithm, pre.getSign());
        
        // Postfirma
        return PAdESTriPhaseSigner.postSign(algorithm, data, certificateChain, interSign, pre,
            null, null);
        */

        // *************** Sello de tiempo

        // data = es.gob.afirma.signers.pades.PdfTimestamper.timestampPdf(inPDF, extraParams, signTime);
        this.data = invoke_PdfTimestamper_timestampPdf(inPDF, params, signTime);

        // *************** Prefirma
        // final PdfSignResult pre = PAdESTriPhaseSigner.preSign(algorithm, data, certificateChain, signTime, extraParams);
        // final byte[] presign = pre.getSign();

        this.pre = invoke_PAdESTriPhaseSigner_preSign(algorithm, data, certificateChain, signTime, params);
        byte[] presign = invoke_PdfSignResult_getSign(pre);

        return presign;

    }

    /**
     * 
     * @param hashToSign
     * @return
     * @throws Exception
     */
    public abstract byte[] step2_SignHash(final byte[] hashToSign) throws Exception;

    /**
     * 
     * @param interSign
     * @return
     * @throws Exception
     */
    public byte[] step3_PostSign(final byte[] interSign) throws Exception {

        final String algorithm = MiniAppletUtils.convertAlgorithm(this.fileInfo);

        final java.security.cert.Certificate[] certChain = new java.security.cert.Certificate[] { this.certificate };

        Properties xParams;
        {

            String timeStampUrl = null;

            MiniAppletSignInfo info;
            info = MiniAppletUtils.convertLocalSignature(this.commonInfoSignature, fileInfo, timeStampUrl,
                    this.certificate);

            xParams = info.getProperties();

        }

        final Properties extraParams = xParams != null ? xParams : new Properties();
        final java.security.cert.Certificate[] certificateChain = Boolean
                .parseBoolean(extraParams.getProperty("includeOnlySignningCertificate", Boolean.FALSE.toString())) ? //$NON-NLS-1$
                        new X509Certificate[] { (X509Certificate) certChain[0] } : certChain;

        // byte[] result = PAdESTriPhaseSigner.postSign(algorithm, data, certificateChain, interSign, pre, null, null);
        byte[] result = invoke_PAdESTriPhaseSigner_postSign(algorithm, data, certificateChain, interSign, pre, null,
                null);
        return result;

    }

    public CommonInfoSignature getCommonInfoSignature() {
        return commonInfoSignature;
    }

    public FileInfoSignature getFileInfo() {
        return fileInfo;
    }

    public String getTimeStampURL() {
        return timeStampURL;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public Date getSignDate() {
        return signDate;
    }

    // ---------------------------------------------

    protected byte[] invoke_PdfTimestamper_timestampPdf(final byte[] inPDF, final Properties extraParams,
            final Calendar signTime) throws Exception {

        Class<?> PdfTimestamper = loadClass("es.gob.afirma.signers.pades.PdfTimestamper");

        Method method;
        method = PdfTimestamper.getDeclaredMethod("timestampPdf", byte[].class, Properties.class, Calendar.class);

        return (byte[]) method.invoke(null, inPDF, extraParams, signTime);
        //data = PdfTimestamper.timestampPdf(inPDF, extraParams, signTime);
    }

    /**
     * 
     // final PdfSignResult pre = PAdESTriPhaseSigner.preSign(algorithm, data, certificateChain, signTime, extraParams);
     // final byte[] presign = pre.getSign();
     * 
     * 
     * @param algorithm
     * @param inPDF
     * @param signerCertificateChain
     * @param signTime
     * @param xParams
     * @return
     * @throws Exception
     */
    protected Object invoke_PAdESTriPhaseSigner_preSign(final String algorithm, final byte[] inPDF,
            final Certificate[] signerCertificateChain, final GregorianCalendar signTime, final Properties xParams)
            throws Exception {

        /* No BORRAR indica el codi de la cridada a invoke
        PdfSignResult preSign(final String digestAlgorithmName,
          final byte[] inPDF,
          final Certificate[] signerCertificateChain,
          final GregorianCalendar signTime,
          final Properties xParams) throws IOException,
                                           AOException,
                                           DocumentException {
        */
        Class<?> PAdESTriPhaseSigner = loadClass("es.gob.afirma.signers.pades.PAdESTriPhaseSigner");

        //System.out.println(" PAdESTriPhaseSigner class = " + PAdESTriPhaseSigner);

        Method method = getMethod(PAdESTriPhaseSigner, "preSign");

        //System.out.println(" PAdESTriPhaseSigner.preSigg  method = " + method);

        Object pre = method.invoke(null, algorithm, inPDF, signerCertificateChain, signTime, xParams);

        return pre;

    }

    protected byte[] invoke_PdfSignResult_getSign(Object pre) throws Exception {

        // ---- PART 2

        Class<?> PdfSignResult = loadClass("es.gob.afirma.signers.pades.PdfSignResult");

        Method method2 = getMethod(PdfSignResult, "getSign");

        return (byte[]) method2.invoke(pre, new Object[] {});

    }

    protected byte[] invoke_PAdESTriPhaseSigner_postSign(final String digestAlgorithmName, final byte[] inPdf,
            final Certificate[] signerCertificateChain, final byte[] pkcs1Signature, final Object preSign, // PdfSignResult
            final Object enhancer, // SignEnhancer
            final Properties enhancerConfig) throws Exception {

        // byte[] result = PAdESTriPhaseSigner.postSign(algorithm, data, certificateChain, interSign, pre, null, null);

        Class<?> PAdESTriPhaseSigner = loadClass("es.gob.afirma.signers.pades.PAdESTriPhaseSigner");

        Method method = getMethod(PAdESTriPhaseSigner, "postSign");

        byte[] result;
        result = (byte[]) method.invoke(null, digestAlgorithmName, inPdf, signerCertificateChain, pkcs1Signature,
                preSign, enhancer, enhancerConfig);
        return result;
    }

}
