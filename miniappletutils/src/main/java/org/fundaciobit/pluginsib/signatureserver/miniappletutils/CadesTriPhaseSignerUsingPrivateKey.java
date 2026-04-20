package org.fundaciobit.pluginsib.signatureserver.miniappletutils;


import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.fundaciobit.pluginsib.signature.api.CommonInfoSignature;
import org.fundaciobit.pluginsib.signature.api.FileInfoSignature;

import es.gob.afirma.core.signers.AOPkcs1Signer;

/**
 * 
 * @author anadal (u80067)
 * 17 abr 2026 9:17:19
 */
public class CadesTriPhaseSignerUsingPrivateKey extends AbstractCadesTriPhaseSigner {

    private final PrivateKey privateKey;

    public CadesTriPhaseSignerUsingPrivateKey(PrivateKey privateKey, CommonInfoSignature commonInfoSignature,
            FileInfoSignature fileInfo, X509Certificate certificate, String timeStampURL) {
        super(commonInfoSignature, fileInfo, certificate, timeStampURL);
        this.privateKey = privateKey;
    }

    public CadesTriPhaseSignerUsingPrivateKey(PrivateKey privateKey, CommonInfoSignature commonInfoSignature,
            FileInfoSignature fileInfo, X509Certificate certificate) {
        super(commonInfoSignature, fileInfo, certificate);
        this.privateKey = privateKey;
    }

    @Override
    public byte[] step2_SignHash(final byte[] hashToSign) throws Exception {
       
        
        final Certificate[] certificateChain = new Certificate[] { this.getCertificate() };

        MiniAppletSignInfo miniAppletSignInfo = MiniAppletUtils.convertLocalSignature(commonInfoSignature, fileInfo,
                timeStampURL, certificate);

        final Properties extraParams = miniAppletSignInfo.getProperties();

        final String algorithm = MiniAppletUtils.convertAlgorithm(fileInfo);

        AOPkcs1Signer signer = new AOPkcs1Signer();

        final byte[] pkcs1sign = signer.sign(hashToSign, algorithm, this.privateKey, certificateChain,
             // Parametros para PKCS#1
                extraParams
                
        );

        return pkcs1sign;

    }

}
