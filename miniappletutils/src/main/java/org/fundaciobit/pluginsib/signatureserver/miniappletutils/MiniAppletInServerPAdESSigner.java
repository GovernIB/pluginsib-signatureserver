package org.fundaciobit.pluginsib.signatureserver.miniappletutils;

import java.lang.reflect.Method;
import java.security.PrivateKey;
import java.util.Properties;

/**
 * 
 * @author anadal
 * 
 * @deprecated Utilitzar {@link PadesTriPhaseSignerUsingPrivateKey} en el seu lloc.
 *
 */
@Deprecated(since="2024-06")
public class MiniAppletInServerPAdESSigner extends AbstractTriFaseSigner {

  final PrivateKey privateKey;

  /**
   * @param key
   */
  public MiniAppletInServerPAdESSigner(PrivateKey privatekey) {
    super();
    this.privateKey = privatekey;
  }
  

  @Override
  public byte[] step2_signHash(final String algorithm, final byte[] hash) throws Exception {

    final Properties extraParams = null;
    final java.security.cert.Certificate[] certificateChain = null;
    
    // Firma PKCS#1
    /*
    final byte[] interSign = new AOPkcs1Signer().sign(hash, algorithm, privateKey,
        certificateChain, extraParams);
    */
    
    Class<?> AOPkcs1Signer = loadClass("es.gob.afirma.core.signers.AOPkcs1Signer");
    
    Object AOPkcs1Signer_instance = AOPkcs1Signer.getConstructor().newInstance();

    Method method  = getMethod(AOPkcs1Signer, "sign");

    
    final byte[] interSign;
    interSign = (byte[])method.invoke(AOPkcs1Signer_instance, hash, algorithm, privateKey,
        certificateChain, extraParams);
    
    return interSign;
  }

}
