
package org.fundaciobit.pluginsib.signatureserver.miniappletutils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.fundaciobit.pluginsib.signature.api.CommonInfoSignature;
import org.fundaciobit.pluginsib.signature.api.FileInfoSignature;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BEROctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.cms.SignedData;
import org.spongycastle.asn1.cms.SignerIdentifier;
import org.spongycastle.asn1.cms.SignerInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.TBSCertificate;
import org.spongycastle.cms.CMSProcessable;
import org.spongycastle.cms.CMSProcessableByteArray;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.cades.CAdESParameters;
import es.gob.afirma.signers.cades.CAdESUtils;
import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
import es.gob.afirma.signers.pkcs7.SigUtils;

/**
 * Implementaci&oacute; d'un firmant generic trif&aacute;sic CAdES
 * @author anadal (u80067)
 * 17 abr 2026 9:20:53
 */
public abstract class AbstractCadesTriPhaseSigner {

    protected final CommonInfoSignature commonInfoSignature;
    protected final FileInfoSignature fileInfo;
    protected final String timeStampURL;
    protected final X509Certificate certificate;
    protected final Date signDate;

    public AbstractCadesTriPhaseSigner(CommonInfoSignature commonInfoSignature, FileInfoSignature fileInfo,
            X509Certificate certificate) {
        super();
        this.commonInfoSignature = commonInfoSignature;
        this.fileInfo = fileInfo;
        this.certificate = certificate;
        this.signDate = new Date();
        this.timeStampURL = null;
    }

    public AbstractCadesTriPhaseSigner(CommonInfoSignature commonInfoSignature, FileInfoSignature fileInfo,
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
        byte[] presign = step1_PreSign();

        // *************** Firma
        final byte[] interSign = step2_SignHash(presign);

        // *************** PostFirma
        return step3_PostSign(interSign, presign);

    }

    /** Genera los atributos firmados CAdES (prefirma).
     * @param signerCertificateChain Cadena de certificados del firmante
     * @param signDate Fecha de la firma (debe establecerse externamente para evitar desincronismos en la firma trif&aacute;sica)
     * @param config Configuraci&oacute;n con el detalle de la firma a montar.
     * @return Atributos CAdES a firmar (prefirma) codificados en ASN.1.
     * @throws AOException Cuando se produce cualquier error durante el proceso. */
    public byte[] step1_PreSign() throws Exception {

        MiniAppletSignInfo miniAppletSignInfo = MiniAppletUtils.convertLocalSignature(commonInfoSignature, fileInfo,
                timeStampURL, certificate);

        final Properties props = miniAppletSignInfo.getProperties();

        String algorithm = MiniAppletUtils.convertAlgorithm(fileInfo);

        //final Certificate[] signerCertificateChain = new Certificate[] { certificate };

        final byte[] data = Files.readAllBytes(fileInfo.getFileToSign().toPath());

        CAdESParameters config = CAdESParameters.load(data, algorithm, props);

        // Atributos firmados
        final ASN1Set signedAttributes;
        try {
            final ASN1EncodableVector signedAttributesVector = CAdESUtils.generateSignedAttributes(certificate, config,
                    false // No es contrafirma
            );

            signedAttributes = SigUtils.getAttributeSet(new AttributeTable(signedAttributesVector));
        } catch (final Exception e) {
            throw new Exception("Error obteniendo los atributos a firmar: " + e, e); //$NON-NLS-1$
        }

        // Codificamos y devolvemos la prefirma
        try {
            return signedAttributes.getEncoded(ASN1Encoding.DER);
        } catch (final Exception ex) {
            throw new Exception("Error al codificar los datos ASN.1 a firmar finalmente", ex); //$NON-NLS-1$
        }

    }

    /**
     * Realiza la firma PKCS#1 v1.5 de los atributos firmados (hash de la prefirma).
     * @param hashToSign
     * @return
     * @throws Exception
     */
    public abstract byte[] step2_SignHash(final byte[] hashToSign) throws Exception;

    /** Realiza una firma CAdES completa.
     * @param signatureAlgorithm Algoritmo de firma electr&oacute;nica.
     * @param content Datos a firmar (usar <code>null</code> si no se desean a&ntilde;adir a la firma).
     * @param signerCertificateChain Cadena de certificados del firmante.
     * @param signatureValue Firma PKCS#1 v1.5 de los atributos firmados.
     * @param signedAttributes Atributos firmados (prefirma).
     * @return Firma CAdES completa.
     * @throws AOException Cuando se produce cualquier error durante el proceso. */
    public byte[] step3_PostSign(

            final byte[] signatureValue, final byte[] signedAttributes) throws Exception {

        String signatureAlgorithm = MiniAppletUtils.convertAlgorithm(fileInfo);

        final String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(signatureAlgorithm);

        final TBSCertificate tbsCertificateStructure;
        try {
            tbsCertificateStructure = TBSCertificate
                    .getInstance(ASN1Primitive.fromByteArray(((X509Certificate) certificate).getTBSCertificate()));
        } catch (final Exception e) {
            throw new Exception("No se ha podido crear la estructura de certificados", e); //$NON-NLS-1$
        }

        final SignerIdentifier signerIdentifier = new SignerIdentifier(
                new IssuerAndSerialNumber(X500Name.getInstance(tbsCertificateStructure.getIssuer()),
                        tbsCertificateStructure.getSerialNumber().getValue()));

        // Algoritmo de huella digital
        final AlgorithmIdentifier digestAlgorithmOID;
        try {
            digestAlgorithmOID = SigUtils.makeAlgId(AOAlgorithmID.getOID(digestAlgorithmName));
        } catch (final Exception e) {
            throw new Exception("Error obteniendo el OID en ASN.1 del algoritmo de huella digital: " + e, e); //$NON-NLS-1$
        }

        // EncryptionAlgorithm
        final AlgorithmIdentifier keyAlgorithmIdentifier;
        try {
            //TODO: En RSA seria conveniente usar el OID del algoritmo de huella, y no solo el de RSA
            keyAlgorithmIdentifier = SigUtils.makeAlgId(signatureAlgorithm.contains("withRSA") ? //$NON-NLS-1$
                    AOAlgorithmID.getOID("RSA") : //$NON-NLS-1$
                    AOAlgorithmID.getOID(signatureAlgorithm));
        } catch (final Exception e) {
            throw new Exception("Error al codificar el algoritmo de cifrado: " + e, e); //$NON-NLS-1$
        }

        // Firma PKCS#1 codificada
        final ASN1OctetString encodedPKCS1Signature = new DEROctetString(signatureValue);

        // Atributos firmados
        final ASN1Set asn1SignedAttributes;
        try {
            asn1SignedAttributes = (ASN1Set) ASN1Primitive.fromByteArray(signedAttributes);
        } catch (final IOException e) {
            throw new Exception("Error en la inclusion de la recuperacion de los SignedAttibutes", e); //$NON-NLS-1$
        }

        // SignerInfo
        final ASN1EncodableVector signerInfo = new ASN1EncodableVector();
        signerInfo.add(new SignerInfo(signerIdentifier, digestAlgorithmOID, asn1SignedAttributes,
                keyAlgorithmIdentifier, encodedPKCS1Signature, null));

        // ContentInfo
        final ContentInfo contentInfo;

        final byte[] content = Files.readAllBytes(fileInfo.getFileToSign().toPath());

        if (content != null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final CMSProcessable msg = new CMSProcessableByteArray(content);
            try {
                msg.write(baos);
            } catch (final Exception e) {
                throw new Exception("Error en la escritura del contenido implicito en el ContentInfo", e); //$NON-NLS-1$
            }
            contentInfo = new ContentInfo(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.data.getId()),
                    new BEROctetString(baos.toByteArray()));
        } else {
            contentInfo = new ContentInfo(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.data.getId()), null);
        }

        // Certificados
        final List<ASN1Encodable> ce = new ArrayList<>();
        Certificate[] signerCertificateChain = new Certificate[] { certificate };
        for (final Certificate cert : signerCertificateChain) {
            try {
                ce.add(org.spongycastle.asn1.x509.Certificate
                        .getInstance(ASN1Primitive.fromByteArray(cert.getEncoded())));
            } catch (final Exception e) {
                Logger.getLogger("es.gob.afirma").severe( //$NON-NLS-1$
                        "Error insertando el certificado '" + AOUtil.getCN((X509Certificate) cert) //$NON-NLS-1$
                                + "' en la cadena de confianza: " + e //$NON-NLS-1$
                );
            }
        }
        final ASN1Set certificates = SigUtils.createBerSetFromList(ce);

        // Algoritmos de huella digital
        final ASN1EncodableVector digestAlgorithms = new ASN1EncodableVector();
        digestAlgorithms.add(digestAlgorithmOID);

        try {
            return new ContentInfo(PKCSObjectIdentifiers.signedData, new SignedData(new DERSet(digestAlgorithms),
                    contentInfo, certificates, null, new DERSet(signerInfo))).getEncoded(ASN1Encoding.DER);
        } catch (final IOException e) {
            throw new Exception("Error creando el ContentInfo de CAdES: " + e, e); //$NON-NLS-1$
        }

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

}
