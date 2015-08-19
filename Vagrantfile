# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
    config.vm.box = "ubuntu/trusty64"
    config.vm.synced_folder ".", "/vagrant"
    config.vm.provider "virtualbox" do |v|
        v.memory = 2048
    end
    config.vm.define :dev do |dev|
        dev.vm.provision :shell, path: "bootstrap.sh"
        dev.vm.provision :shell,
            inline: 'ansible-playbook \
                /vagrant/ansible/dev.yml -c local -v'
    end
    if Vagrant.has_plugin?("vagrant-cachier")
        config.cache.scope = :box
    end
end