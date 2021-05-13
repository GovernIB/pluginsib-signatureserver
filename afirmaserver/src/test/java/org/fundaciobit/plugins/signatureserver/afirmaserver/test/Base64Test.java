package org.fundaciobit.plugins.signatureserver.afirmaserver.test;

import es.gob.afirma.transformers.TransformersException;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Test {

    @Test
    public void testEncodeWithLines() throws TransformersException {
        String cadena = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n" +
                "<Peticion>\n" +
                "    <Process>\n" +
                "        <IdTel>TEL51</IdTel>\n" +
                "        <NumRegEnt/>\n" +
                "        <AnhoRegEnt/>\n" +
                "        <FechaEntradaReg/>\n" +
                "        <sIdOperador>04</sIdOperador>\n" +
                "        <Nombre>Pruebàs</Nombre>\n" +
                "        <Apellido1>Eidas</Apellido1>\n" +
                "        <Apellido2>Certificado</Apellido2>\n" +
                "        <NumIdentificador>99999999R</NumIdentificador>\n" +
                "        <FileRequest>envio_baleares.xml</FileRequest>\n" +
                "        <ClientSignature/>\n" +
                "        <ServerSignature/>\n" +
                "    </Process>\n" +
                "    <FileAttachements>\n" +
                "        <Filename>envio_baleares.xml</Filename>\n" +
                "        <Signature>\n" +
                "            <![CDATA[FIRMA]]>\n" +
                "        </Signature>\n" +
                "    </FileAttachements>\n" +
                "</Peticion>";

        String expected = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iSVNPLTg4NTktMSIgc3RhbmRhbG9uZT0ibm8i\n" +
                "Pz4KPFBldGljaW9uPgogICAgPFByb2Nlc3M+CiAgICAgICAgPElkVGVsPlRFTDUxPC9JZFRlbD4K\n" +
                "ICAgICAgICA8TnVtUmVnRW50Lz4KICAgICAgICA8QW5ob1JlZ0VudC8+CiAgICAgICAgPEZlY2hh\n" +
                "RW50cmFkYVJlZy8+CiAgICAgICAgPHNJZE9wZXJhZG9yPjA0PC9zSWRPcGVyYWRvcj4KICAgICAg\n" +
                "ICA8Tm9tYnJlPlBydWViw6BzPC9Ob21icmU+CiAgICAgICAgPEFwZWxsaWRvMT5FaWRhczwvQXBl\n" +
                "bGxpZG8xPgogICAgICAgIDxBcGVsbGlkbzI+Q2VydGlmaWNhZG88L0FwZWxsaWRvMj4KICAgICAg\n" +
                "ICA8TnVtSWRlbnRpZmljYWRvcj45OTk5OTk5OVI8L051bUlkZW50aWZpY2Fkb3I+CiAgICAgICAg\n" +
                "PEZpbGVSZXF1ZXN0PmVudmlvX2JhbGVhcmVzLnhtbDwvRmlsZVJlcXVlc3Q+CiAgICAgICAgPENs\n" +
                "aWVudFNpZ25hdHVyZS8+CiAgICAgICAgPFNlcnZlclNpZ25hdHVyZS8+CiAgICA8L1Byb2Nlc3M+\n" +
                "CiAgICA8RmlsZUF0dGFjaGVtZW50cz4KICAgICAgICA8RmlsZW5hbWU+ZW52aW9fYmFsZWFyZXMu\n" +
                "eG1sPC9GaWxlbmFtZT4KICAgICAgICA8U2lnbmF0dXJlPgogICAgICAgICAgICA8IVtDREFUQVtG\n" +
                "SVJNQV1dPgogICAgICAgIDwvU2lnbmF0dXJlPgogICAgPC9GaWxlQXR0YWNoZW1lbnRzPgo8L1Bl\n" +
                "dGljaW9uPg==";

        byte[] bytes = cadena.getBytes(StandardCharsets.UTF_8);

        //String resultat = new String(Base64Coder.encodeBase64(bytes));
        //String resultat = java.util.Base64.getMimeEncoder().encodeToString(bytes);
        String resultat = Base64.getMimeEncoder(76, new byte[] {'\n'}).encodeToString(bytes);

        Assert.assertEquals(expected, resultat);
    }

    @Test
    public void testEncodeWithoutLines() {
        String cadena = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n" +
                "<Peticion>\n" +
                "    <Process>\n" +
                "        <IdTel>TEL51</IdTel>\n" +
                "        <NumRegEnt/>\n" +
                "        <AnhoRegEnt/>\n" +
                "        <FechaEntradaReg/>\n" +
                "        <sIdOperador>04</sIdOperador>\n" +
                "        <Nombre>Pruebàs</Nombre>\n" +
                "        <Apellido1>Eidas</Apellido1>\n" +
                "        <Apellido2>Certificado</Apellido2>\n" +
                "        <NumIdentificador>99999999R</NumIdentificador>\n" +
                "        <FileRequest>envio_baleares.xml</FileRequest>\n" +
                "        <ClientSignature/>\n" +
                "        <ServerSignature/>\n" +
                "    </Process>\n" +
                "    <FileAttachements>\n" +
                "        <Filename>envio_baleares.xml</Filename>\n" +
                "        <Signature>\n" +
                "            <![CDATA[FIRMA]]>\n" +
                "        </Signature>\n" +
                "    </FileAttachements>\n" +
                "</Peticion>";

        byte[] bytes = cadena.getBytes(StandardCharsets.UTF_8);

        String expected = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iSVNPLTg4NTktMSIgc3RhbmRhbG9uZT0ibm8i" +
                "Pz4KPFBldGljaW9uPgogICAgPFByb2Nlc3M+CiAgICAgICAgPElkVGVsPlRFTDUxPC9JZFRlbD4K" +
                "ICAgICAgICA8TnVtUmVnRW50Lz4KICAgICAgICA8QW5ob1JlZ0VudC8+CiAgICAgICAgPEZlY2hh" +
                "RW50cmFkYVJlZy8+CiAgICAgICAgPHNJZE9wZXJhZG9yPjA0PC9zSWRPcGVyYWRvcj4KICAgICAg" +
                "ICA8Tm9tYnJlPlBydWViw6BzPC9Ob21icmU+CiAgICAgICAgPEFwZWxsaWRvMT5FaWRhczwvQXBl" +
                "bGxpZG8xPgogICAgICAgIDxBcGVsbGlkbzI+Q2VydGlmaWNhZG88L0FwZWxsaWRvMj4KICAgICAg" +
                "ICA8TnVtSWRlbnRpZmljYWRvcj45OTk5OTk5OVI8L051bUlkZW50aWZpY2Fkb3I+CiAgICAgICAg" +
                "PEZpbGVSZXF1ZXN0PmVudmlvX2JhbGVhcmVzLnhtbDwvRmlsZVJlcXVlc3Q+CiAgICAgICAgPENs" +
                "aWVudFNpZ25hdHVyZS8+CiAgICAgICAgPFNlcnZlclNpZ25hdHVyZS8+CiAgICA8L1Byb2Nlc3M+" +
                "CiAgICA8RmlsZUF0dGFjaGVtZW50cz4KICAgICAgICA8RmlsZW5hbWU+ZW52aW9fYmFsZWFyZXMu" +
                "eG1sPC9GaWxlbmFtZT4KICAgICAgICA8U2lnbmF0dXJlPgogICAgICAgICAgICA8IVtDREFUQVtG" +
                "SVJNQV1dPgogICAgICAgIDwvU2lnbmF0dXJlPgogICAgPC9GaWxlQXR0YWNoZW1lbnRzPgo8L1Bl" +
                "dGljaW9uPg==";

        //String resultat = Base64.encode(bytes);
        String resultat = Base64.getEncoder().encodeToString(bytes);

        Assert.assertEquals(expected, resultat);
    }


}
