pipeline {
  agent any
  stages {
    stage('Clean') {
      steps {
        bat(script: 'mvn clean', returnStdout: true, returnStatus: true)
      }
    }

    stage('Compile') {
      steps {
        bat(script: 'mvn compile', returnStdout: true, returnStatus: true)
      }
    }

    stage('Install') {
      steps {
        bat(script: 'mvn install', returnStatus: true, returnStdout: true)
      }
    }

  }
}