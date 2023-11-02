pipeline {
    agent any
    environment {
        CI = true
        ARTIFACTORY_ACCESS_TOKEN = credentials('artifactory-access-token')
    }
    
    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/CCS-2022/secret-santa-app.git'
                sh 'cp /var/sslkeys/keystore.p12 /var/lib/jenkins/workspace/SS-BackEnd/src/main/resources/keystore.p12'
            }
        }

        stage('Building Project') {
            steps {
                sh "./gradlew wrapper --gradle-version 8.4"
                sh "./gradlew clean build -x test"
            }
        }

        stage('Testing Build') {
            steps {
                sh "./gradlew test -x test"
            }
        }

        stage('Code Quality Check via SonarQube') {
            steps {
                withSonarQubeEnv("SecretSantaSonar") {
                    sh "./gradlew sonar"
                }
            }
        }

        stage('Upload to Artifactory') {
            agent {
                docker {
                    image 'releases-docker.jfrog.io/jfrog/jfrog-cli-v2:2.2.0' 
                    reuseNode true
                }
            }
            steps {
                sh 'jfrog rt upload --url http://192.168.1.239:8082/artifactory/ --access-token ${ARTIFACTORY_ACCESS_TOKEN} ./build/libs/*SNAPSHOT.jar ss-${ENVS}/'
                }
        }


        stage('Create Image && Upload to DockerHub') {
            steps {
                script {
                    try {
                        sh 'docker image rm ccsadmindocker/ssfrontend:${ENVS}-latest'
                        } catch (Exception e) {
                            echo "Image does not exist. Error: ${e}"
                        }
                    }
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh '''
                        docker build -t ccsadmindocker/ssbackend:${ENVS}-latest .
                        docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
                        docker push ccsadmindocker/ssbackend:DEV-latest
                    '''
                }
            }
        }


        stage("Deploy To Container"){
            steps {
                script {
                    echo '*** Executing remote commands ***'
                    try {
                        sh 'ssh -tt secretsanta@192.168.1.235 docker stop ssbackend'
                    } catch (Exception e){
                        echo "Container does not exist. Error: ${e}"
                    }
                    try {
                    sh 'ssh -tt secretsanta@192.168.1.235 docker rm ssbackend'
                    } catch (Exception e){
                        echo "Container does not exist. Error: ${e}"
                    }
                    sh 'ssh -tt secretsanta@192.168.1.235 docker pull ccsadmindocker/ssbackend:DEV-latest'
                    sh "ssh -tt secretsanta@192.168.1.235 'docker run -d --name ssbackend --network=bridge -p 8080:8080 ccsadmindocker/ssbackend:${ENVS}-latest'"
                    }                
            }
        }

        stage("Cleaning Up Storage Space"){
            steps {
                sh 'docker system prune -f'
                echo '*** Cleaning remote server ***'
                sh 'ssh -tt secretsanta@192.168.1.235 docker system prune -f'                          
            }
        }
    }
}
