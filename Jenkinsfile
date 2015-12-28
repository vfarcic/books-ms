import groovy.json.JsonSlurper

def service = "books-ms"

node("cd") {
    sh "sudo docker build -t ${registry}${service}-tests -f Dockerfile.test ."
    sh "sudo docker-compose -f docker-compose-dev.yml run --rm tests"
}