FROM tomcat:9.0-jdk16-openjdk
WORKDIR /usr/local/tomcat/
RUN rm -rf webapps/*
ADD /web/build/libs/certificates.war webapps/ROOT.war