pipeline {
  agent any
  stages {
    stage('Clean') {
      steps {
        bat(script: 'mvn clean', returnStdout: true)
      }
    }

    stage('Compile') {
      steps {
        bat(script: 'mvn compile', returnStdout: true)
      }
    }

  }
}