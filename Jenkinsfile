class Config {
	public static String versionPrefix = '3.1.'
	public static String gitRepo = 'https://github.com/monochromata/jactr.git'
}

// TODO: Even this might be moved into workflowLibs, passing just a Config instance
node("1gb") {
   installToolsIfNecessary()
   withCredentials([[$class: 'FileBinding', credentialsId: 'settings.xml', variable: 'PATH_TO_SETTINGS_XML'],
   					[$class: 'FileBinding', credentialsId: 'jarsigner.keystore', variable: 'PATH_TO_JARSIGNER_KEYSTORE'],
   					[$class: 'FileBinding', credentialsId: 'pubring.gpg', variable: 'PATH_TO_GPG_PUBLIC_KEYRING'],
   					[$class: 'FileBinding', credentialsId: 'secring.gpg', variable: 'PATH_TO_GPG_SECRET_KEYRING'],
   					[$class: 'FileBinding', credentialsId: 'upload.server.ssh.signature.file', variable: 'PATH_TO_UPLOAD_SERVER_SSH_FINGERPRINT_FILE'],
   					[$class: 'StringBinding', credentialsId: 'upload.server.name', variable: 'UPLOAD_SERVER_NAME'],]) {
   					
		withEnv(["PATH+MAVEN=${tool 'mvn'}/bin", 
				 "PATH+JAVA=${tool 'jdk8'}/bin"]) {
		   stage 'Checkout'
		   git url: Config.gitRepo
		   
		   stage 'Set versions'
		   maven("--file parent/pom.xml \
				  versions:set")
	       
	       stage "Clean & verify"
	       maven("clean verify")
	
	       stage name:"Deploy", concurrency: 1
	       sh '''touch ~/.ssh/known_hosts \
	       		 && ssh-keygen -f ~/.ssh/known_hosts -R $UPLOAD_SERVER_NAME \
	       		 && cat $PATH_TO_UPLOAD_SERVER_SSH_FINGERPRINT_FILE >> ~/.ssh/known_hosts'''
	       // Retry is necessary because upload is unreliable
	       retry(3) {
	       		maven('''-DskipTests=true \
	       				 -DskipITs=true \
	       				 deploy''')
	       }
	             
	       stage name:"Site deploy", concurrency: 1
	       // Retry is necessary because upload is unreliable
	       retry(3) {
	       		maven('''-DskipTests=true \
	       				 -DskipITs=true \
	       				 site-deploy''')
	     	}
         }
   }
}

// TODO: Move to workflowLibs
def maven(String optionsAndGoals) {
   def newVersion=getNextVersion()
   sh '''mvn \
   		 -Djarsigner.keystore.path=$PATH_TO_JARSIGNER_KEYSTORE \
   		 -Dgpg.publicKeyring=$PATH_TO_GPG_PUBLIC_KEYRING \
   		 -Dgpg.secretKeyring=$PATH_TO_GPG_SECRET_KEYRING \
         --errors \
         --settings $PATH_TO_SETTINGS_XML \
         -DnewVersion='''+newVersion+''' \
         '''+optionsAndGoals
}

// TODO: Move to workflowLibs
// TODO: Auto-assign version numbers via an algorithm that
//		 * does not yield un-deployed versions for failed builds
// 		 * permits major and minor numbers to be incremented via tags in the commit message
//		 * starts with a patch number of 0 if the minor was incremented (same for minor if major was incremented)
def getNextVersion() {
	return Config.versionPrefix+env.BUILD_NUMBER
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