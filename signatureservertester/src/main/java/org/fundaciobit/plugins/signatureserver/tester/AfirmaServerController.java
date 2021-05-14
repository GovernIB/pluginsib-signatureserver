package org.fundaciobit.plugins.signatureserver.tester;

import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.SignaturesSet;
import org.fundaciobit.plugins.signatureserver.afirmaserver.AfirmaServerSignatureServerPlugin;
import org.fundaciobit.plugins.signatureserver.api.ISignatureServerPlugin;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

@Named
@ViewScoped
public class AfirmaServerController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    FacesContext context;

    private ISignatureServerPlugin plugin;

    private String properties;
    private String propertyBase = "";
    private String pluginName;

    private Part fitxer;

    
    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public void setPropertyBase(String propertyBase) {
        this.propertyBase = propertyBase;
    }

    public Part getFitxer() {
        return fitxer;
    }

    public void setFitxer(Part fitxer) {
        this.fitxer = fitxer;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void crearPlugin() throws IOException {
        Properties props = new Properties();
        props.load(new StringReader(properties));
        plugin = new AfirmaServerSignatureServerPlugin(propertyBase, props);
        pluginName = plugin.getName(new Locale("ca"));
    }

    public void signar() throws IOException {
        if (fitxer == null) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fitxer és null", null));
        }

        CommonInfoSignature commonInfoSignature = new CommonInfoSignature("ca", "", null, null);

        Path tempFile = Files.createTempFile("asc", "temp");

        try (var os = new FileOutputStream(tempFile.toFile());
             var is = fitxer.getInputStream();) {
            is.transferTo(os);
        }

        String fileName = fitxer.getSubmittedFileName();

        FileInfoSignature fileInfoSignature = new FileInfoSignature("1", tempFile.toFile(), null,
                FileInfoSignature.PDF_MIME_TYPE, fileName, null, null, null, 1, "ca",
                FileInfoSignature.SIGN_OPERATION_SIGN, FileInfoSignature.SIGN_TYPE_PADES, FileInfoSignature.SIGN_ALGORITHM_SHA256,
                FileInfoSignature.SIGN_MODE_IMPLICIT, FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT, null, null,
                null, false, null, null, null, null,
                null, null, null);

        SignaturesSet signaturesSet = new SignaturesSet("XZY",
                commonInfoSignature,
                new FileInfoSignature[] {fileInfoSignature});

        SignaturesSet set = plugin.signDocuments(signaturesSet, null, null);

        System.out.println(set.getStatusSignaturesSet().getStatus());
        System.out.println(set.getFileInfoSignatureArray()[0].getStatusSignature().getStatus());
        System.out.println(set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData());

    }
}
