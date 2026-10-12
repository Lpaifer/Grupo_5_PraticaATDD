// Pipeline as code inspirado no jogo-enigma-api-v1-A2-portavel-v5.1.
// Docker/Compose executam Java 26, Node e Cypress sem instalações no agente.
def runCmd(String command, boolean allowFailure = false) {
    int code = isUnix()
        ? sh(script: command, returnStatus: true)
        : bat(script: "@echo off\r\n${command}", returnStatus: true)
    if (code != 0 && !allowFailure) {
        error("Comando falhou (exit ${code}): ${command}")
    }
    return code
}

def ciCompose(String args, boolean allowFailure = false) {
    return runCmd("docker compose -p ${env.CI_PROJECT} -f docker-compose.ci.yml ${args}", allowFailure)
}

def homolCompose(String args, boolean allowFailure = false) {
    return runCmd("docker compose -p grupo5-homol -f docker-compose.homol.yml ${args}", allowFailure)
}

def collectFiles(String container, String source, String destination) {
    String localPath = isUnix() ? destination : destination.replace('/', '\\')
    runCmd(isUnix() ? "mkdir -p '${localPath}'" : "if not exist \"${localPath}\" mkdir \"${localPath}\"")
    runCmd("docker cp ${container}:${source}/. ${destination}/", true)
}

pipeline {
    agent any
    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timeout(time: 45, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '15', artifactNumToKeepStr: '10'))
    }
    parameters {
        booleanParam(name: 'DEPLOY_HOMOL', defaultValue: true,
            description: 'Implantar HOMOL e executar smoke checks/Cypress após aprovação do CI.')
    }
    environment {
        API_IMAGE = 'grupo5-api'
        FRONTEND_IMAGE = 'grupo5-frontend'
    }
    stages {
        stage('Checkout') {
            steps {
                // Evita reaproveitar relatórios de builds anteriores.
                deleteDir()
                script {
                    def revision = checkout scm
                    env.GIT_COMMIT = revision.GIT_COMMIT
                    env.CI_PROJECT = env.BUILD_TAG.toLowerCase().replaceAll('[^a-z0-9-]', '-')
                    env.IMAGE_TAG = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(12)}"
                }
            }
        }
        stage('Preflight') {
            steps {
                script {
                    timeout(time: 1, unit: 'MINUTES') {
                        runCmd('docker version')
                        runCmd('docker compose version')
                        runCmd('docker info --format "{{.ServerVersion}}"')
                        ciCompose('config --quiet')
                        homolCompose('config --quiet')
                    }
                }
            }
        }
        stage('CI - JUnit, PostgreSQL e JaCoCo') {
            steps {
                script {
                    ciCompose('build backend')
                    try {
                        ciCompose("run --name ${env.CI_PROJECT}-backend backend")
                    } finally {
                        collectFiles("${env.CI_PROJECT}-backend", '/workspace/target', 'target')
                    }
                }
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/TEST-*.xml,target/failsafe-reports/TEST-*.xml',
                        allowEmptyResults: true
                    archiveArtifacts artifacts: 'target/site/jacoco/**,target/*.jar', allowEmptyArchive: true
                    publishHTML(target: [reportDir: 'target/site/jacoco', reportFiles: 'index.html',
                        reportName: 'JaCoCo', keepAll: true, alwaysLinkToLastBuild: true, allowMissing: true])
                }
            }
        }
        stage('CI - Qualidade e build Vue') {
            steps {
                script {
                    ciCompose('build frontend')
                    ciCompose("run --name ${env.CI_PROJECT}-frontend --no-deps frontend")
                }
            }
        }
        stage('Build das imagens') {
            steps {
                script {
                    homolCompose('build api frontend')
                }
            }
        }
        stage('Homologação') {
            when { expression { params.DEPLOY_HOMOL } }
            options {
                // Serializa HOMOL inclusive entre jobs diferentes neste Jenkins.
                lock(resource: 'grupo5-homol')
            }
            stages {
                stage('Deploy HOMOL') {
                    steps {
                        script {
                            homolCompose('up -d --no-build --wait --wait-timeout 180 db api frontend pgadmin prometheus grafana')
                        }
                    }
                }
                stage('Health check e observabilidade') {
                    steps {
                        script {
                            homolCompose('--profile checks build smoke')
                            homolCompose('--profile checks run --rm --no-deps smoke')
                        }
                    }
                }
                stage('Cypress E2E') {
                    steps {
                        script {
                            homolCompose('--profile e2e build cypress')
                            try {
                                homolCompose("--profile e2e run --no-deps --name ${env.CI_PROJECT}-cypress cypress")
                            } finally {
                                collectFiles("${env.CI_PROJECT}-cypress", '/e2e/cypress', 'artifacts/cypress')
                            }
                        }
                    }
                    post {
                        always {
                            junit testResults: 'artifacts/cypress/reports/*.xml', allowEmptyResults: true
                            archiveArtifacts artifacts: 'artifacts/cypress/reports/**,artifacts/cypress/screenshots/**,artifacts/cypress/videos/**',
                                allowEmptyArchive: true
                        }
                    }
                }
            }
            post {
                always {
                    script {
                        runCmd(isUnix() ? 'mkdir -p artifacts' : 'if not exist artifacts mkdir artifacts')
                        homolCompose('ps > artifacts/homol-status.txt', true)
                        homolCompose('logs --no-color --tail=150 api frontend prometheus grafana > artifacts/homol.log', true)
                    }
                    archiveArtifacts artifacts: 'artifacts/homol*', allowEmptyArchive: true
                }
                success {
                    echo 'HOMOL aprovada: aplicação, PostgreSQL, Cypress, Prometheus e Grafana verificados.'
                }
            }
        }
    }
    post {
        always {
            script {
                if (env.CI_PROJECT) {
                    ciCompose('down --volumes --remove-orphans', true)
                    runCmd("docker rm -f ${env.CI_PROJECT}-backend ${env.CI_PROJECT}-frontend ${env.CI_PROJECT}-cypress", true)
                }
            }
        }
        success {
            echo 'Pipeline concluído. Relatórios de testes e cobertura disponíveis neste build.'
        }
        failure {
            echo 'Pipeline reprovado. Consulte o estágio, os testes e os logs arquivados; HOMOL permanece disponível para diagnóstico.'
        }
    }
}
