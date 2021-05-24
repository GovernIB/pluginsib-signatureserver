package org.fundaciobit.plugins.signatureserver.tester;

import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.SignaturesSet;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.fundaciobit.plugins.signatureserver.afirmaserver.AfirmaServerSignatureServerPlugin;
import org.fundaciobit.plugins.signatureserver.api.ISignatureServerPlugin;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@WebServlet("/sign")
@MultipartConfig
public class SignServlet extends HttpServlet {

    private ISignatureServerPlugin plugin;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Properties properties = new Properties();
        try (var inputStream = new FileInputStream("/opt/jboss/plugin.properties")){
            properties.load(inputStream);
        } catch (IOException ioException) {
            throw new ServletException("Error llegint plugin.properties", ioException);
        }
        plugin = new AfirmaServerSignatureServerPlugin("", properties);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part fitxer = request.getPart("fitxer");

        if (fitxer == null) {
            response.sendError(400, "No s'ha enviat fitxer per signar");
            return;
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

        if (set.getStatusSignaturesSet().getStatus() != StatusSignaturesSet.STATUS_FINAL_OK) {
            throw new ServletException("Status final: " + set.getStatusSignaturesSet().getStatus());
        }

        File signedData = set.getFileInfoSignatureArray()[0].getStatusSignature().getSignedData();
        response.setContentLengthLong(signedData.length());
        response.setContentType(FileInfoSignature.PDF_MIME_TYPE);
        try (var inputStream = new FileInputStream(signedData)) {
            inputStream.transferTo(response.getOutputStream());
        } finally {
            if (!signedData.delete()) {
                signedData.deleteOnExit();
            }
        }
    }
}
