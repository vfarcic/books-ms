import groovy.json.JsonSlurper

def service = "books-ms"

node("cd") {
    git url: "https://github.com/vfarcic/${service}.git"
    if (build.toBoolean()) {
        sh "sudo docker build -t ${registry}${service}-tests -f Dockerfile.test ."
        sh "sudo docker-compose -f docker-compose-dev.yml run --rm tests"
        def app = docker.build "${service}:${GIT_BRANCH}"
    }
}