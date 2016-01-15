node("cd") {
    def serviceName = "books-ms"
    def prodIp = "10.100.192.200" // Modified
    def proxyIp = "10.100.192.200" // Modified
    def proxyNode = "prod"
    def registryIpPort = "10.100.198.200:5000"
    def swarmPlaybook = "swarm.yml" // Modified
    def proxyPlaybook = "swarm-proxy.yml" // Added
    def instances = 1 // Added

    def flow = load "/data/scripts/workflow-util.groovy"

    git url: "https://github.com/vfarcic/${serviceName}.git"
    flow.provision(swarmPlaybook) // Modified
    flow.provision(proxyPlaybook) // Added
    flow.buildTests(serviceName, registryIpPort)
    flow.runTests(serviceName, "tests", "")
    flow.buildService(serviceName, registryIpPort)

    def currentColor = flow.getCurrentColor(serviceName, prodIp)
    def nextColor = flow.getNextColor(currentColor)

    flow.deploySwarm(serviceName, prodIp, nextColor, instances) // Modified
    flow.runBGPreIntegrationTests(serviceName, prodIp, nextColor)
    flow.updateBGProxy(serviceName, proxyNode, nextColor)
    flow.runBGPostIntegrationTests(serviceName, prodIp, proxyIp, proxyNode, currentColor, nextColor)
}
