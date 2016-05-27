node {
   installToolsIfNecessary()
   def newVersion="3.1."+env.BUILD_NUMBER
   withCredentials([[$class: 'FileBinding', credentialsId: 'settings.xml', variable: 'PATH_TO_SETTINGS_XML'],
   					[$class: 'FileBinding', credentialsId: 'jarsigner.keystore', variable: 'PATH_TO_JARSIGNER_KEYSTORE'],
   					[$class: 'FileBinding', credentialsId: 'pubring.gpg', variable: 'PATH_TO_GPG_PUBLIC_KEYRING'],
   					[$class: 'FileBinding', credentialsId: 'secring.gpg', variable: 'PATH_TO_GPG_SECRET_KEYRING'],
   					[$class: 'FileBinding', credentialsId: 'upload.server.ssh.signature.file', variable: 'PATH_TO_UPLOAD_SERVER_SSH_FINGERPRINT_FILE'],
   					[$class: 'StringBinding', credentialsId: 'upload.server.name', variable: 'UPLOAD_SERVER_NAME'],]) {
   					
		withEnv(["PATH+MAVEN=${tool 'mvn'}/bin", 
				 "PATH+JAVA=${tool 'jdk8'}/bin"]) {
		   stage 'Checkout'
		   git url: 'https://github.com/monochromata/jactr.git'
		
		   stage 'Set versions'
	       sh '''mvn \
	             --errors \
	             --settings $PATH_TO_SETTINGS_XML \
	             --file parent/pom.xml \
	             -DnewVersion='''+newVersion+''' \
	             versions:set'''
	       
	       stage "Clean & verify"
	       sh '''mvn \
	       		 -Djarsigner.keystore.path=$PATH_TO_JARSIGNER_KEYSTORE \
	       		 -Dgpg.publicKeyring=$PATH_TO_GPG_PUBLIC_KEYRING \
	       		 -Dgpg.secretKeyring=$PATH_TO_GPG_SECRET_KEYRING \
	             --errors \
	             --settings $PATH_TO_SETTINGS_XML \
	             -DnewVersion='''+newVersion+''' \
	             clean verify'''
	
	       stage name:"Deploy & site deploy", concurrency: 1
	       sh '''touch ~/.ssh/known_hosts \
	       		 && ssh-keygen -f ~/.ssh/known_hosts -R $UPLOAD_SERVER_NAME \
	       		 && cat $PATH_TO_UPLOAD_SERVER_SSH_FINGERPRINT_FILE >> ~/.ssh/known_hosts'''
	       sh '''mvn \
	       		 -Djarsigner.keystore.path=$PATH_TO_JARSIGNER_KEYSTORE \
	       		 -Dgpg.publicKeyring=$PATH_TO_GPG_PUBLIC_KEYRING \
	       		 -Dgpg.secretKeyring=$PATH_TO_GPG_SECRET_KEYRING \
	             --errors \
	             --settings $PATH_TO_SETTINGS_XML \
	             -DnewVersion='''+newVersion+''' \
	             -DskipTests=true \
				 -DskipITs=true \
	             deploy site-deploy'''
		}
    }
   
   // TODO: Publish JUnit test reports ... **/target/surefire-reports/*.xml ?
}

// TODO: Move to workflowLibs
def installToolsIfNecessary() {
   sh '''echo "deb http://http.debian.net/debian jessie-backports main" > /etc/apt/sources.list.d/jessie-backports.list \
        && apt-get update \
        && apt-get remove --yes openjdk-7-jdk \
        && apt-get install --yes openjdk-8-jre-headless openjdk-8-jdk \
        && /usr/sbin/update-java-alternatives -s java-1.8.0-openjdk-amd64 \
        && apt-get install --yes git maven'''
}