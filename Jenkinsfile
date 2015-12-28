import groovy.json.JsonSlurper

def service = "books-ms"
def registry = "10.100.198.200:5000/"
def swarmMaster = "10.100.192.200"
def proxy = "10.100.192.200"
def currentColor = getCurrentColor(swarmMaster, service)
def nextColor = getNextColor(currentColor)

node("cd") {
    git url: "https://github.com/vfarcic/${service}.git"
    if (build.toBoolean()) {
        sh "sudo docker build -t ${registry}${service}-tests -f Dockerfile.test ."
        sh "sudo docker-compose -f docker-compose-dev.yml run --rm tests"
        def app = docker.build "${service}:${GIT_BRANCH}"
    }
}