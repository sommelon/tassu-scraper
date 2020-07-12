pipeline {
  agent any
  stages {
    stage('Clean') {
      steps {
        sh 'mvn clean'
      }
    }

    stage('Compile') {
      steps {
        sh 'mvn compile'
      }
    }

    stage('Install') {
      steps {
        sh 'mvn install'
      }
    }

    stage('Success') {
      steps {
        echo 'Success!'
      }
    }

  }
}