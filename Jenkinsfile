node {
  try
  {

    // Only keep one build
    properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '5']]])
    
    // Mark the code checkout 'stage'....
    stage 'Checkout'
    checkout scm
		//    sh 'git submodule update --init'

    stage ('Clean'){
      withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {

        // Run the maven build
        sh "mvn clean  -Dtycho.mode=maven -fn"
      }}

    stage ('Build'){
      withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {

        // Run the maven build
        sh "install -Pall-platforms -PWith-IDE"
				step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
        step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
        //step([$class: 'JacocoPublisher', exclusionPattern: '**/org/overture/ast/analysis/**/*.*, **/org/overture/ast/expressions/**/*.*, **/org/overture/ast/modules/**/*.*, **/org/overture/ast/node/**/*.*,**/org/overture/ast/patterns/**/*.*, **/org/overture/ast/statements/**/*.*, **/org/overture/ast/types/**/*.*, **/org/overture/codegen/ir/**/*, **/org/overture/ide/**/*'])

        step([$class: 'TasksPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', high: 'FIXME', ignoreCase: true, low: '', normal: 'TODO', pattern: '', unHealthy: ''])
      }}

		stage ('Build NSIS Installer'){
			withEnv(["TWSIM_PATH=/var/lib/jenkins/internal-resources/20-sim"]) {
        sh """
cd ide/product

for arch in x86 x86_64
do
  echo --------------------------------------------------------
  echo Building installer for $arch
  mvn -Dtycho.mode=maven resources:resources -Dsim20.path=$TWSIM_PATH -Dinstaller.arch=$arch
  /usr/bin/makensis target/installer/${arch}/crescendo.nsi
done
"""
			}
		}

		stage ('Publish Artifactory'){
			def server = Artifactory.server "-844406945@1404457436085"
			def buildInfo = Artifactory.newBuildInfo()
			buildInfo.env.capture = true
			buildInfo.env.filter.addExclude("org/destecs/ide/**")
			def rtMaven = Artifactory.newMavenBuild()
			//rtMaven.tool = MAVEN_TOOL // Tool name from Jenkins configuration
			//rtMaven.opts = "-Denv=dev"
			rtMaven.deployer releaseRepo:'crescendo', snapshotRepo:'crescendo', server: server
			//rtMaven.resolver releaseRepo:'libs-release', snapshotRepo:'libs-snapshot', server: server

			rtMaven.run pom: 'pom.xml', goals: 'install', buildInfo: buildInfo

			buildInfo.retention maxBuilds: 10, maxDays: 7, deleteBuildArtifacts: true

		
			// Publish build info.
			server.publishBuildInfo buildInfo
		}

		stage ('Deploy'){
			withEnv(["DEST=/home/jenkins/web/crescendo/development/Build-${BUILD_NUMBER}_`date +%Y-%m-%d_%H-%M`"]) {
				sh "echo $DEST"
				sh "scp ide/product/target/products/Crescendo-*.zip jenkins@overture.au.dk:${DEST}"
				sh "scp ide/product/target/installer/x86/Crescendo-*.exe jenkins@overture.au.dk:${DEST}"
				sh "scp ide/product/target/installer/x86_64/Crescendo-*.exe jenkins@overture.au.dk:${DEST}"
			}

 
		} catch (any) {
			currentBuild.result = 'FAILURE'
			throw any //rethrow exception to prevent the build from proceeding
		} finally {
  
			stage('Reporting'){

				// Notify on build failure using the Email-ext plugin
				emailext(body: '${DEFAULT_CONTENT}', mimeType: 'text/html',
								 replyTo: '$DEFAULT_REPLYTO', subject: '${DEFAULT_SUBJECT}',
								 to: emailextrecipients([[$class: 'CulpritsRecipientProvider'],
																				 [$class: 'RequesterRecipientProvider']]))
			}}
	}
