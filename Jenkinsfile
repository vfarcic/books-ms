import groovy.json.JsonSlurper

def service = "books-ms"

node("cd") {
    sh "echo 'sudo docker build -t ${service}-tests -f Dockerfile.test .'"
    sh "echo 'sudo docker-compose -f docker-compose-dev.yml run --rm tests'"
}