package org.fundaciobit.pluginsib.signatureserver.miniappletutils.test;

import org.fundaciobit.pluginsib.core.v3.utils.CertificateUtils;
import org.fundaciobit.pluginsib.signatureserver.miniappletutils.MiniAppletUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.security.cert.X509Certificate;

/**
 * 
 * @author anadal
 * @author areus
 */
public class MiniAppletUtilsTest {

  @Test
  public void testDNIeAmbFiltreCoincident() throws Exception {
    InputStream certstream = MiniAppletUtilsTest.class.getResourceAsStream("/Ciudadano_firma_activo.cer");
    assert certstream != null;
    X509Certificate certificate1 = CertificateUtils.decodeCertificate(certstream);

    String filter = "filters.1=issuer.rfc2254:|(cn=AC DNIE 001)(cn=AC DNIE 002)(cn=AC DNIE 003)(cn=AC DNIE 004)";

    Assert.assertTrue(MiniAppletUtils.matchFilter(certificate1, filter));
  }

  @Test
  public void testDNIeSenseFiltre() throws Exception {
    InputStream certstream = MiniAppletUtilsTest.class.getResourceAsStream("/Ciudadano_firma_activo.cer");
    assert certstream != null;
    X509Certificate certificate1 = CertificateUtils.decodeCertificate(certstream);

    // Quan no tenim cap filtre, hauria de passar? Que passam el filtre
    Assert.assertTrue(MiniAppletUtils.matchFilter(certificate1, null));
  }

  @Test
  public void testDNIeAmbFiltreExcloent() throws Exception {
    InputStream certstream = MiniAppletUtilsTest.class.getResourceAsStream("/Ciudadano_firma_activo.cer");
    assert certstream != null;
    X509Certificate certificate1 = CertificateUtils.decodeCertificate(certstream);

    String filter = "filters.1=issuer.rfc2254:(cn=AC CACA)";

    Assert.assertFalse(MiniAppletUtils.matchFilter(certificate1, filter));
  }
  
 


}
