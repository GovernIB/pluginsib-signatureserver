FROM jboss/wildfly:15.0.1.Final

USER root
COPY afirmades2_caib_es.pem /etc/pki/ca-trust/source/anchors/afirmades2_caib_es.pem
# RUN update-ca-trust extract
RUN keytool -importcert -noprompt -file /etc/pki/ca-trust/source/anchors/afirmades2_caib_es.pem \
    -alias afirmades2 -cacerts -storepass changeit

USER jboss
COPY plugin.properties /opt/jboss/
COPY integra/transformersTemplates /opt/integra/transformersTemplates
COPY target/signatureservertester.war /opt/jboss/wildfly/standalone/deployments/
