package org.fundaciobit.signatureserver;

import es.gob.afirma.keystores.filters.CertFilterManager;
import es.gob.afirma.keystores.filters.CertificateFilter;
import org.fundaciobit.pluginsib.core.utils.CertificateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Properties;

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

    String filter =
            "filters=nonexpired:\n" +
                    "filters.1=issuer.rfc2254:|(cn=AC DNIE 001)(cn=AC DNIE 002)(cn=AC DNIE 003)(cn=AC DNIE 004)";

    Assert.assertTrue(matchFilter(certificate1, filter));
  }

  @Test
  public void testDNIeSenseFiltre() throws Exception {
    InputStream certstream = MiniAppletUtilsTest.class.getResourceAsStream("/Ciudadano_firma_activo.cer");
    assert certstream != null;
    X509Certificate certificate1 = CertificateUtils.decodeCertificate(certstream);

    // Quan no tenim cap filtre, hauria de passar? Que passam el filtre
    Assert.assertTrue(matchFilter(certificate1, null));
  }

  @Test
  public void testDNIeAmbFiltreExcloent() throws Exception {
    InputStream certstream = MiniAppletUtilsTest.class.getResourceAsStream("/Ciudadano_firma_activo.cer");
    assert certstream != null;
    X509Certificate certificate1 = CertificateUtils.decodeCertificate(certstream);

    String filter = "filters.1=issuer.rfc2254:(cn=AC CACA)";

    Assert.assertFalse(matchFilter(certificate1, filter));
  }
  
  public static boolean matchFilter(X509Certificate certificate, String filter) throws IOException {
      if (filter == null || filter.trim().isEmpty()) {
          return true;
      }

      Properties propertyFilters = new Properties();
      propertyFilters.load(new StringReader(filter));

      CertFilterManager filterManager = new CertFilterManager(propertyFilters);
      List<CertificateFilter> filters = filterManager.getFilters();

      if (filters.isEmpty()) {
          return true;
      }

      for (CertificateFilter f : filters) {
          if (f.matches(certificate)) {
              return true;
          }
      }
      return false;
  }


}
