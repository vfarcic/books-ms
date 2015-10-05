# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
    config.vm.box = "ubuntu/trusty64"

    # If you run into issues with Ansible complaining about executable permissions,
    # comment the following statement and uncomment the next one.
    config.vm.synced_folder ".", "/vagrant"
    # config.vm.synced_folder ".", "/vagrant", mount_options: ["dmode=700,fmode=600"]
    # if (/cygwin|mswin|mingw|bccwin|wince|emx/ =~ RUBY_PLATFORM) != nil
    #     config.vm.synced_folder ".", "/vagrant", mount_options: ["dmode=700,fmode=600"]
    # else
    #     config.vm.synced_folder ".", "/vagrant"
    # end

    config.vm.provider "virtualbox" do |v|
        v.memory = 2048
    end
    config.vm.define :dev do |dev|
        dev.vm.network "private_network", ip: "10.100.199.200"
        dev.vm.provision :shell, path: "bootstrap.sh"
        dev.vm.provision :shell,
            inline: 'ansible-playbook \
                /vagrant/ansible/dev.yml -c local -vv'
    end
#    if Vagrant.has_plugin?("vagrant-cachier")
#        config.cache.scope = :box
#    end
end