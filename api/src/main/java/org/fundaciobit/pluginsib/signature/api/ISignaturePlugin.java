package org.fundaciobit.pluginsib.signature.api;

import java.util.List;
import java.util.Locale;

import org.fundaciobit.pluginsib.core.v3.IPluginIB;
import org.fundaciobit.pluginsib.utils.signature.SignatureConstants;

/**
 * 
 * @author anadal
 *
 */
public interface ISignaturePlugin extends IPluginIB, SignatureConstants {


  /**
   * 
   * @param locale
   *          idioma amb que es vol el nom del plugin
   * @return Nom del plugin
   */
  public String getName(Locale locale);
  
  /**
   * @return Les operacions de firma suportades segons el tipus de firma
   * @param signType Tipus de Firma
   * @see SignatureConstants#SIGNATURE_OPERATION_SIGN = FIRMA
   * @see SignatureConstants#SIGNATURE_OPERATION_COSIGN = COFIRMA
   * @see SignatureConstants#SIGNATURE_OPERATION_COUNTERSIGN = CONTRAFIRMA
   */
  public int[] getSupportedOperationsBySignType(String signType);

  /**
   * @return Els tipus de firma suportats. Actualment només es suporta PAdES.
   * @see SignatureConstants#SIGNTYPE_PADES = "PAdES";
   * @see SignatureConstants#SIGNTYPE_XADES = "XAdES";
   * @see SignatureConstants#SIGNTYPE_CADES = "CAdES";
   * @see SignatureConstants#SIGNTYPE_FACTURAE = "FacturaE";
   * @see SignatureConstants#SIGNTYPE_OOXML = "OOXML";
   * @see SignatureConstants#SIGNTYPE_ODF = "ODF";
   */
  public String[] getSupportedSignatureTypes();

  /**
   * @param signType
   *          Tipus de Firma
   * @return Retorna els algorismes suportats segons els tipus de firma passat
   *         per paràmetre
   *  @see SignatureConstants#SIGN_ALGORITHM_SHA1
   *  @see SignatureConstants#SIGN_ALGORITHM_SHA256
   *  @see SignatureConstants#SIGN_ALGORITHM_SHA384
   *  @see SignatureConstants#SIGN_ALGORITHM_SHA512        
   */
  public String[] getSupportedSignatureAlgorithms(String signType);
  
  
  /**
   * @param signType
   *          Tipus de Firma
   * @return Retorna els modes de firma  suportats segons els tipus de firma passat
   *         per paràmetre
   * @see SignatureConstants#SIGN_MODE_ATTACHED_ENVELOPED
   * @see SignatureConstants#SIGN_MODE_ATTACHED_ENVELOPING
   * @see SignatureConstants#SIGN_MODE_DETACHED
   * @see SignatureConstants#SIGN_MODE_INTERNALLY_DETACHED
   * @see SignatureConstants.SIGN_MODE_EXTERNALLY_DETACHED
   */
  public int[] getSupportedSignatureModes(String signType);
  
  

  /**
   * @return Retorna els tipus de Barcode suportats per l'estampació del Codi
   *         Segur de Verificació (CSV). Per exemple, el tipus suportats pel
   *         plugins de PortaFIB són: BarCode128, Pdf417 i QrCode
   */
  public List<String> getSupportedBarCodeTypes();
  
  
  /**
   * 
   * @return null si no hi ha límit. Sinó el numero màxim de firmes per transacció.
   */
  public Integer getSupportedNumberOfSignaturesInBatch();


  /**
   * @param signType
   *          Tipus de Firma
   * @return true indica que el plugin accepta generadors de Segell de Temps
   *         definits dins FileInfoSignature.timeStampGenerator
   */
  public boolean acceptExternalTimeStampGenerator(String signType);

  /**
   * @param signType
   *          Tipus de Firma
   * @return true, indica que el plugin internament ofereix un generador de
   *         segellat de temps.
   */
  public boolean providesTimeStampGenerator(String signType);

  /**
   * 
   * @return true indica que el plugin accepta generadors del imatges de la
   *         Firma Visible PDF definits dins
   *         FileInfoSignature.pdfInfoSignature.rubricGenerator.
   */
  public boolean acceptExternalRubricGenerator();

  /**
   * 
   * @return true, indica que el plugin internament ofereix un generador de
   *         imatges de la Firma Visible PDF.
   */
  public boolean providesRubricGenerator();

  /**
   * 
   * @return true indica si el plugin accepta estampadors de Codi Segur de
   *         Verificació (missatge i/o codi de barres).
   */
  public boolean acceptExternalSecureVerificationCodeStamper();

  /**
   * 
   * @return true, indica que el plugin internament ofereix estampadors de Codi
   *         Segur de Verificació (missatge i/o codi de barres).
   */
  public boolean providesSecureVerificationCodeStamper();

  /**
   * @return llista de propietats disponibles per aquest plugin. 
   *    Si retorna null, es que no ha implementat aquest mètode
   */
  public List<PropertyInfo> getAvailableProperties(String propertyKeyBase);

}
