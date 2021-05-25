FROM jboss/wildfly:15.0.1.Final

COPY config /opt/signatureserver/
COPY target/signatureservertester.war /opt/jboss/wildfly/standalone/deployments/

# Hem de fixar l'usuari root per poder importar el certificat de afirmades2.caib.es
USER root
RUN keytool -importcert -noprompt -file /opt/signatureserver/afirmades2_caib_es.pem \
    -alias afirmades2 -cacerts -storepass changeit

USER jboss
WORKDIR /opt/jboss/wildfly
RUN bin/jboss-cli.sh --file=/opt/signatureserver/set-system-properties.cli
RUN rm -rf standalone/configuration/standalone_xml_history/current

