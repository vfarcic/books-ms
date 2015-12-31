node("cd") {
    def serviceName = "books-ms"
    def registryIpPort = "10.100.198.200:5000"

    git url: "https://github.com/vfarcic/${serviceName}.git"
    def flow = load "/data/scripts/workflow-util.groovy"
    flow.buildTests(serviceName, registryIpPort)
    flow.runTests("tests", "")
    flow.buildService(serviceName, registryIpPort)
}
