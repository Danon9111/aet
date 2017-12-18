![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# AET
<p align="center">
  <img src="https://github.com/Cognifide/aet/blob/master/misc/img/aet-logo-black.png?raw=true"
         alt="AET Logo"/>
</p>

## Vagrant

This is a pseudo-cookbook which is responsible for local environment
provisioning using Vagrant (powered by Chef + Berkshelf under the hood)

### Overview

Currently it creates a virtual machine with the following services

* Karaf
* Apache
* Tomcat
* ActiveMQ
* MongoDb
* Brosermob
* Firefox
* X environment

### AET services

All services are running using default ports. For communication please use
IP address:

* `192.168.123.100`

### General prerequisites

Make sure command line Git works for you without any issues (including SSH key
authorization). The easiest way to test that would be to open a terminal and
execute `git clone https://github.com/Cognifide/AET`.

Moreover original EOL characters must be preserved during `git clone` command.
To force that please execute `git config --global core.autocrlf false`.

By default Vagrant virtual machine needs 3 GB of RAM and 2 vCPUs, so please
make sure that you have enough memory on your machine (8 GB is minimum, 16 GB
recommended though).

### Installation

* Download and install
  [VirtualBox 5.1.14](https://www.virtualbox.org/wiki/Downloads)
* Download and install
  [Vagrant 1.9.2](https://releases.hashicorp.com/vagrant/)
* Download and install [ChefDK 1.1.16](https://downloads.chef.io/chef-dk/)

As an administrator execute the following commands:

* `vagrant plugin install vagrant-omnibus`
* `vagrant plugin install vagrant-berkshelf`
* `vagrant plugin install vagrant-hostmanager`

Whenever you'd like to keep all Vagrant related data and virtual machine disks
in non-standard directories please:

* set `VAGRANT_HOME` variable to new location (by default it is set to
  `$HOME/vagrant.d`)
* update VirtualBox settings (`File -> Preferences -> General`) to move all
  disks to other directory

### TL;DR

Once you set all described things up just execute:

```
git pull && berks update && vagrant destroy -f && vagrant up
```

### First run

Please clone AET [application](https://github.com/Cognifide/AET)
using `git clone` command if you haven't done it yet. All commands have to be
executed when you're inside an `application/vagrant` directory - it contains
`Vagrantfile` necessary to execute Vagrant.

Next please execute:

* `berks install` - downloads Chef dependencies from external sources. It acts
  as `mvn clean install`, but for Chef cookbooks.
* `vagrant up` - creates new virtual machine (`.box` file will be downloaded
  during first run), runs Chef inside it, sets domains and port forwarding up

### Updates

Whenever new version is released please execute the following:

* `git pull` to get latest version of `Vagrantfile` from Stash
* `berks update` to update Chef dependencies
* `vagrant provision` to re-run Chef on the virtual machine

### SSH access

To get into the virtual machine via SSH please execute `vagrant ssh` from the
same directory that contains `Vagrantfile`. After that please type `sudo -i`
and press ENTER to switch to `root`.

If you prefer to use PuTTY, mRemote or any other connection manager, please log
in as user `vagrant` with password `vagrant` on `localhost` port `2222`. Keep
in mind that the port may be different if you have more than one Vagrant
machine running at the same time. You can check current assignment by executing
`vagrant ssh-config` command from directory that contains your `Vagrantfile`.

### Useful Vagrant commands

* `vagrant reload` restarts Vagrant machine and re-applies settings defined in
  `Vagrantfile`. It's useful whenever you've changed port forwarding or synced
  folder configuration
* `vagrant destroy -f` deletes entire virtual machine
* `vagrant reload --provision` restarts virtual machine and re-run Chef
  afterwards
* `vagrant suspend` suspends currently running virtual machine
* `vagrant resume` resumes suspended virtual machine
* `vagrant status` show status of virtual machine described in `Vagrantfile`
* `vagrant halt` halts/turns off virtual machine

### Port forwarding

Local port (*host*) is a port exposed on your machine. You can access services
via `localhost:<PORT>`.

*guest* ports refer to ports assigned inside Vagrant's virtual machine.

Port forwarding rules can be easily changed in `Vagrantfile`.

### Known Issues

* When getting following error on deploying application to local vagrant: `What went wrong: Execution failed for task ':deployDevClearCache'. > java.net.ConnectException: Connection timed out: connect` Run `ifup eth1` command on vagrant using ssh.

### Authors

Karol Drazek (<karol.drazek@cognifide.com>)
