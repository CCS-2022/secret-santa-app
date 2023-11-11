pipeline {
    agent any
    environment {
        CI = true
        ARTIFACTORY_ACCESS_TOKEN = credentials('artifactory-access-token')
        SSServer = credentials('SSServerIP')
        SSUser = credentials('SSUserID')
        Port = credentials('SSBackPort')
        Artifactory = credentials('ArtifactoryIP') 
        DockerID = credentials('DockerHubUser')
        DevZone = credentials('DevBackZone') 
        KeyFrom = credentials('PathToKeyStore') 
        KeyTo = credentials('PathFromKeyStore') 
    }
    
    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/CCS-2022/secret-santa-app.git'
                //SSL Key CP
                sh '${KeyFrom} ${KeyTo}'
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
                sh 'jfrog rt upload --url http://${Artifactory}/artifactory/ --access-token ${ARTIFACTORY_ACCESS_TOKEN} ./build/libs/*SNAPSHOT.jar ss-${ENVS}/'
                }
        }


        stage('Create Image && Upload to DockerHub') {
            steps {
                script {
                    try {
                        sh 'docker image rm ${DockerID}/${DevZone}:${ENVS}-latest'
                        } catch (Exception e) {
                            echo "Image does not exist. Error: ${e}"
                        }
                    }
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh '''
                        docker build -t ${DockerID}/${DevZone}:${ENVS}-latest .
                        docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
                        docker push ${DockerID}/${DevZone}:${ENVS}-latest
                    '''
                }
            }
        }

        stage("Deploy To Container"){
            steps {
                script {
                    echo '*** Executing remote commands ***'
                    try {
                        sh 'ssh -tt ${SSUser}@${SSServer} docker stop ${DevZone}'
                    } catch (Exception e){
                        echo "Container does not exist. Error: ${e}"
                    }
                    try {
                    sh 'ssh -tt ${SSUser}@${SSServer} docker rm ${DevZone}'
                    } catch (Exception e){
                        echo "Container does not exist. Error: ${e}"
                    }
                    sh 'ssh -tt ${SSUser}@${SSServer} docker pull ${DockerID}/${DevZone}:${ENVS}-latest'
                    sh "ssh -tt ${SSUser}@${SSServer} 'docker run -d --name ${DevZone} --restart unless-stopped --network=bridge -p ${Port}:${Port} ${DockerID}/${DevZone}:${ENVS}-latest'"
                    }                
            }
        }

        stage("Cleaning Up Storage Space"){
            steps {
                sh 'docker system prune -af'
                echo '*** Cleaning remote server ***'
                sh 'ssh -tt ${SSUser}@${SSServer} docker system prune -af'                          
            }
        }
    }
}
