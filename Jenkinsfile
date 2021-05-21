pipeline {
    agent any

    stages {

        stage("Assemble") {
            steps {
                sh "./gradlew assemble"
            }
        }

        stage("Test") {
            steps {
                sh "./gradlew check"
            }
        }

        stage('Publish Jars') {
            when {
                anyOf {
                    branch "master*"
                    branch "release*"
                }
            }
            environment {
                SONATYPE_CREDENTIALS = credentials('sonatype')
                GPGPASSPHRASE = credentials('gpgpassphrase')
            }
            steps {
                script {
                    sh "./gradlew publish"+
                        " -Ppublish_username=${SONATYPE_CREDENTIALS_USR} -Ppublish_password=${SONATYPE_CREDENTIALS_PSW}"+
                        " -PkeyId=DF8285F0 -Ppassword=${GPGPASSPHRASE} -PsecretKeyRingFile=/var/jenkins_home/secring.gpg"
                }
            }
        }

        stage("Publish plugin") {
            when {
                anyOf {
                    branch "release*"
                }
            }
            environment {
                GRADLE_PUBLISH = credential('gradle-plugin-portal')
            }
            steps {
                script {
                    sh "./gradlew publishPlugins" +
                        " -Pgradle.publish.key=${GRADLE_PUBLISH_USR} -Pgradle.publish.secret=${GRADLE_PUBLISH_PSW}"
                }
            }

        }

    }

    post {
        always {
            junit '*/build/test-results/**/*.xml'
        }

        aborted {
            sh "./gradlew composeDownForced"
        }
    }
}
