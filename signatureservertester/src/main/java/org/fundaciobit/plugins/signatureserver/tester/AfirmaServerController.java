package org.fundaciobit.plugins.signatureserver.tester;

import org.fundaciobit.plugins.signatureserver.afirmaserver.AfirmaServerSignatureServerPlugin;
import org.fundaciobit.plugins.signatureserver.api.ISignatureServerPlugin;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Locale;
import java.util.Properties;

@Named
@ViewScoped
public class AfirmaServerController implements Serializable {

    private static final long serialVersionUID = 1L;

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

}
