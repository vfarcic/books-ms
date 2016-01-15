node("cd") {
    def serviceName = "books-ms"
    def prodIp = "10.100.198.201" // Remove
    def swarmIp = "10.100.192.200" // Added
    def proxyIp = "10.100.198.201" // Remove
    def proxyNode = "prod"
    def registryIpPort = "10.100.198.200:5000"
    def playbook = "swarm.yml"
    def instances = 1 // Added

    def flow = load "/data/scripts/workflow-util.groovy"

    git url: "https://github.com/vfarcic/${serviceName}.git"
    flow.provision(playbook)
    flow.buildTests(serviceName, registryIpPort)
    flow.runTests(serviceName, "tests", "")
    flow.buildService(serviceName, registryIpPort)

    def currentColor = flow.getCurrentColor(serviceName, swarmIp)
    def nextColor = flow.getNextColor(currentColor)

    flow.deploySwarm(serviceName, swarmIp, nextColor, instances) // Modified
    flow.runBGPreIntegrationTests(serviceName, swarmIp, nextColor)
    flow.updateBGProxy(serviceName, proxyNode, nextColor)
    flow.runBGPostIntegrationTests(serviceName, swarmIp, proxyIp, proxyNode, currentColor, nextColor)
}
