pipeline {
  agent any

  options {
    timestamps()
    ansiColor('xterm')
    disableConcurrentBuilds()
  }

  parameters {
    string(name: 'TAGS', defaultValue: 'smoke', description: 'JUnit5 tags expression, e.g. smoke, cart, smoke&cart, search|catalog')
    string(name: 'EXCLUDED_TAGS', defaultValue: '', description: 'Tags to exclude, e.g. e2e or flaky')
    choice(name: 'BROWSER', choices: ['chromium', 'firefox', 'webkit'], description: 'Playwright browser')
    booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser in headless mode')
  }

  environment {
    // Maven options (ускоряет и стабилизирует)
    MAVEN_OPTS = '-Dmaven.repo.local=.m2 -Dfile.encoding=UTF-8'
    // Playwright browser (мы будем читать это в TestConfig / PlaywrightManager)
    PW_BROWSER = "${params.BROWSER}"
    PW_HEADLESS = "${params.HEADLESS}"
    // Allure results location (в allure.properties target/allure-results)
    ALLURE_RESULTS = 'target/allure-results'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh '''
          echo "TAGS=${TAGS}"
          echo "EXCLUDED_TAGS=${EXCLUDED_TAGS}"
          echo "BROWSER=${PW_BROWSER}"
          echo "HEADLESS=${PW_HEADLESS}"

          mvn -U -q clean test \
            -Dgroups="${TAGS}" \
            -DexcludedGroups="${EXCLUDED_TAGS}" \
            -Dbrowser="${PW_BROWSER}" \
            -Dheadless="${PW_HEADLESS}"
        '''
      }
      post {
        always {
          // JUnit отчёт в Jenkins (если есть)
          junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'

          // Артефакты: traces/screenshots + allure-results
          archiveArtifacts allowEmptyArchive: true, artifacts: '''
            artifacts/**,
            target/surefire-reports/**,
            target/allure-results/**
          '''
        }
      }
    }

    stage('Allure Report') {
      when {
        expression { fileExists('target/allure-results') }
      }
      steps {
        allure([
          includeProperties: false,
          jdk: '',
          results: [[path: 'target/allure-results']]
        ])
      }
    }
  }

  post {
    always {
      cleanWs deleteDirs: true, notFailBuild: true
    }
  }
}