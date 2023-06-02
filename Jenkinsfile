node("cd") {
    def serviceName = "books-ms"
    def prodIp = "192.168.56.200"
    def proxyIp = "192.168.56.200"
    def swarmNode = "swarm-master"
    def proxyNode = "swarm-master"
    def registryIpPort = "192.168.60.200:5000"
    def swarmPlaybook = "swarm-healing.yml"
    def proxyPlaybook = "swarm-proxy.yml"
    def instances = 1

    def flow = load "/data/scripts/workflow-util.groovy"

    git url: "https://github.com/vfarcic/${serviceName}.git"
    flow.provision(swarmPlaybook)
    flow.provision(proxyPlaybook)
    flow.buildTests(serviceName, registryIpPort)
    flow.runTests(serviceName, "tests", "")
    flow.buildService(serviceName, registryIpPort)

    def currentColor = flow.getCurrentColor(serviceName, prodIp)
    def nextColor = flow.getNextColor(currentColor)

    flow.deploySwarm(serviceName, prodIp, nextColor, instances)
    flow.runBGPreIntegrationTests(serviceName, prodIp, nextColor)
    flow.updateBGProxy(serviceName, proxyNode, nextColor)
    flow.runBGPostIntegrationTests(serviceName, prodIp, proxyIp, proxyNode, currentColor, nextColor)
    flow.updateChecks(serviceName, swarmNode)
}
