node("cd") {
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
