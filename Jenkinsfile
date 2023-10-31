pipeline {
    agent any
    environment {
        CI = true
        ARTIFACTORY_ACCESS_TOKEN = credentials('artifactory-access-token')
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/CCS-2022/secret-santa-app.git'
            }
        }

        stage('Build') {
            steps {
                sh "./gradlew wrapper --gradle-version 8.4"
                sh "./gradlew clean build -x test"
            }
        }


/*        stage('Test') {
            steps {
                sh "./gradlew test -x test"
            }
        }
*/
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
                sh 'cd ./build/libs/'
                sh 'jfrog rt upload --url http://192.168.1.239:8082/artifactory/ --access-token ${ARTIFACTORY_ACCESS_TOKEN} ./*SNAPSHOT.jar ss-${ENVS}/'
                }
        }


        stage('Create Image && Upload to DockerHub') {
            agent any
            steps {
                script {
                    try {
                        sh 'docker image rm ccsadmindocker/ssfrontend:${ENVS}-latest'
                        } catch (Exception e) {
                            echo "Image does not exist. Error: ${e}"
                        }
                    }
                //sh 'docker build -t ccsadmindocker/ssbackend:${ENVS}-latest .'
                //sh 'docker push ccsadmindocker/ssbackend:DEV-latest'
                // Use the credentials you configured in Jenkins
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh '''
                        pwd
                        ls
                        docker build -t ccsadmindocker/ssbackend:${ENVS}-latest .
                        docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
                        docker push ccsadmindocker/ssbackend:DEV-latest
                    '''
                }
            //    sh 'sudo docker save -o /home/ssbackend.tar ccsadmindocker/ssbackend:${ENVS}-latest'
            //    sh 'scp "/home/ssbackend.tar" root@192.168.1.235:/home'
            }
        }


        stage("Deploy To Container"){
            steps {
                script {
                    echo 'Using remote command over ssh'
                    sh 'echo "Today is:" date'
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

    }
}
