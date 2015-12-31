node("cd") {
    def serviceName = "books-ms"
    def prodIp = "10.100.198.201"
    def proxyIp = "10.100.198.201"
    def registryIpPort = "10.100.198.200:5000"

    git url: "https://github.com/vfarcic/${serviceName}.git"
    def flow = load "/data/scripts/workflow-util.groovy"
    flow.provision("prod2.yml")
    flow.buildTests(serviceName, registryIpPort)
    flow.runTests("tests", "")
    flow.buildService(serviceName, registryIpPort)
    flow.deploy(prodIp)
    flow.updateProxy(serviceName, "prod")
    flow.runTests("integ", "-e DOMAIN=http://${proxyIp}")
}
