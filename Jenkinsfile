node("cd") {
    def serviceName = "books-ms"
    def prodIp = "10.100.198.201"
    def proxyIp = "10.100.198.201"
    def proxyNode = "prod"
    def registryIpPort = "10.100.198.200:5000"

    def flow = load "/data/scripts/workflow-util.groovy"

    def currentColor = flow.getCurrentColor(serviceName, prodIp) // New
    def nextColor = flow.getNextColor(currentColor) // New

    git url: "https://github.com/vfarcic/${serviceName}.git"
    flow.provision("prod2.yml")
    flow.buildTests(serviceName, registryIpPort)
    flow.runTests(serviceName, "tests", "")
    flow.buildService(serviceName, registryIpPort)
    flow.deployBG(serviceName, prodIp, nextColor) // Modified
    flow.runBGPreIntegrationTests(serviceName, prodIp, nextColor) // New
    flow.updateBGProxy(serviceName, proxyNode, nextColor) // Modified
    flow.runBGPostIntegrationTests(serviceName, prodIp, proxyIp, proxyNode, currentColor, nextColor) // Modified
}
