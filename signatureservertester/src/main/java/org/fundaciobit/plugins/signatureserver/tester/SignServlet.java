package org.fundaciobit.plugins.signatureserver.tester;

import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.SignaturesSet;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.fundaciobit.plugins.signatureserver.api.ISignatureServerPlugin;
import org.fundaciobit.pluginsib.core.utils.FileUtils;
import org.fundaciobit.pluginsib.core.utils.PluginsManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@WebServlet(value = "/sign", loadOnStartup = 1)
@MultipartConfig
public class SignServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private final Map<String, ISignatureServerPlugin> pluginMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String configDir = System.getProperty("org.fundaciobit.signatureserver.path");

        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(configDir + "/plugin.properties")) {
            properties.load(inputStream);
        } catch (IOException ioException) {
            throw new ServletException("Error llegint plugin.properties", ioException);
        }

        String[] pluginNames = properties.getProperty("plugins.signatureserver").split(",");
        for (String pluginName : pluginNames) {
            String classProperty = "plugins.signatureserver." + pluginName + ".class";
            ISignatureServerPlugin plugin =
                    (ISignatureServerPlugin) PluginsManager.instancePluginByProperty(classProperty, "", properties);
            pluginMap.put(pluginName, plugin);
            log("Inicialitzat: " + pluginName + ", " + plugin.getName(Locale.getDefault()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part fitxer = request.getPart("fitxer");
        if (fitxer == null) {
            response.sendError(400, "No s'ha enviat fitxer per signar");
            return;
        }

        String pluginName = request.getParameter("pluginName");
        if (pluginName == null || !pluginMap.containsKey(pluginName)) {
            response.sendError(400, "pluginName invàlid: " + pluginName);
        }
        ISignatureServerPlugin plugin = pluginMap.get(pluginName);

        CommonInfoSignature commonInfoSignature = new CommonInfoSignature("ca", "", null, null);

        Path tempFile = Files.createTempFile("sign", "temp");
        try (OutputStream os = new FileOutputStream(tempFile.toFile());
             InputStream is = fitxer.getInputStream()) {
            FileUtils.copy(is, os);
        }

        String fileName = fitxer.getSubmittedFileName();
        String contentType = fitxer.getContentType();
        log(fileName);
        log(contentType);

        FileInfoSignature fileInfoSignature = getFileInfoSignature(tempFile, fileName, contentType);

        SignaturesSet signaturesSet = new SignaturesSet("XZY",
                commonInfoSignature,
                new FileInfoSignature[] {fileInfoSignature});

        SignaturesSet set = plugin.signDocuments(signaturesSet, null, null);

        log("Status: " + set.getStatusSignaturesSet().getStatus());

        if (set.getStatusSignaturesSet().getStatus() != StatusSignaturesSet.STATUS_FINAL_OK) {
            throw new ServletException("Status final: " + set.getStatusSignaturesSet().getStatus());
        }

        File signedData = set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData();
        response.setContentLengthLong(signedData.length());
        response.setContentType(signResultContentType(contentType));
        try (InputStream inputStream = new FileInputStream(signedData)) {
            FileUtils.copy(inputStream, response.getOutputStream());
        } finally {
            if (!signedData.delete()) {
                signedData.deleteOnExit();
            }
        }
    }

    private FileInfoSignature getFileInfoSignature(Path tempFile, String fileName, String contentType) {
        return new FileInfoSignature("1", tempFile.toFile(), null,
                contentType, fileName, null, null, null, 1, "ca",
                FileInfoSignature.SIGN_OPERATION_SIGN, signType(contentType), FileInfoSignature.SIGN_ALGORITHM_SHA256,
                FileInfoSignature.SIGN_MODE_IMPLICIT, FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT, null, null,
                null, false, null, null, null, null,
                null, null, null);
    }

    private String signType(String contentType) {
        switch (contentType) {
            case "application/pdf":
                return FileInfoSignature.SIGN_TYPE_PADES;
            case "application/xml":
            case "text/xml":
                return FileInfoSignature.SIGN_TYPE_XADES;
            default:
                return FileInfoSignature.SIGN_TYPE_CADES;
        }
    }

    private String signResultContentType(String contentType) {
        switch (contentType) {
            case "application/pdf":
            case "application/xml":
            case "text/xml":
                return contentType;
            default:
                return "application/octet-stream";
        }
    }
}
