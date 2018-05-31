def CONTAINER_NAME = "epmjnkns-pipeline"
def CONTAINER_TAG = "latest"
def DOCKER_HUB_USER = "andruhan"
def APP_HTTP_PORT = "5050"

node {
    stage('Initialize') {
        def dockerHome = tool 'myDocker'
        env.PATH = "${dockerHome}/bin:${env.PATH}"
    }

    stage('Checkout') {
        deleteDir()
        checkout scm
    }

    stage("Image Prune") {
        try {
            sh "docker image prune -f"
            sh "docker stop $CONTAINER_NAME"
        } catch (error) {
        }
    }

    stage('Image Build') {
        sh "docker build -t $CONTAINER_NAME:$CONTAINER_TAG  -t $CONTAINER_NAME --pull --no-cache ."
        echo "Image build complete"
    }

    stage('Image Test') {
        sh "docker run -d --rm -p $APP_HTTP_PORT:$APP_HTTP_PORT --name $CONTAINER_NAME $DOCKER_HUB_USER/$CONTAINER_NAME:$CONTAINER_TAG"
        sleep 5
        APP_IP_ADDR = sh(returnStdout: true, script: "docker inspect $CONTAINER_NAME --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'")
        APP_IP_ADDR = APP_IP_ADDR.trim()
        status = sh(returnStatus: true, script: "curl --silent --connect-timeout 15 --show-error --fail http://$APP_IP_ADDR:$APP_HTTP_PORT")
        sh(returnStatus: true, script: "echo http://$APP_IP_ADDR:$APP_HTTP_PORT")
        if (status != 0) {
            currentBuild.result = 'FAILED'
            sh "exit ${status}"
        }
    }

    stage('Push to Docker Registry') {
        withCredentials([usernamePassword(credentialsId: 'dockerHubAccount', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            sh "docker login -u $DOCKER_HUB_USER -p $PASSWORD"
            sh "docker tag $CONTAINER_NAME:$CONTAINER_TAG $DOCKER_HUB_USER/$CONTAINER_NAME:$CONTAINER_TAG"
            sh "docker push $DOCKER_HUB_USER/$CONTAINER_NAME:$CONTAINER_TAG"
            echo "Image push complete"
        }
    }

    stage('Run App') {
        try {
            sh "docker stop $CONTAINER_NAME"
            sh "docker pull $DOCKER_HUB_USER/$CONTAINER_NAME"
            sh "docker run -d --rm -p $APP_HTTP_PORT:$APP_HTTP_PORT --name $CONTAINER_NAME $DOCKER_HUB_USER/$CONTAINER_NAME:$CONTAINER_TAG"
            echo "Application started on port: ${APP_HTTP_PORT} (http)"
        } catch (error) {
        }
    }
}
