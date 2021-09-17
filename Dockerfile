FROM tomcat:9.0
WORKDIR /usr/local/tomcat/
RUN rm -rf webapps/*
ADD /api/build/libs/certificates.war webapps/ROOT.war