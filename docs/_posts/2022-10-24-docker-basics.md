---
layout: post
title: Docker Basics
tags: [Docker, Virtualization]
category: FINALIZED
color: rgb(37, 150, 236)
feature-img: "assets/img/post-cover/32-cover.png"
thumbnail: "assets/img/post-cover/32-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}


Concepts
--------

### Containers

### Services

When we know by gut that a container is, we may deploy a container to production. So next we may scale our application
and enable load-balancing. To do this, we must go one level up in the hierarchy of a distributed application: the
**service**.

* Stack
* **Services** (we are here)
* Container

In a distributed application, different pieces of the app are called "**services**". For example, if you imagine a video
sharing site, it probably includes a service for storing application data in a database, a service for video transcoding
in the background after a user uploads something, a service for the front-end, and so on.

**Services are really just "containers in production"**. A service only runs one image, but it **codifies** the way that
image runs - what ports it should use, how many replicas of the container should run so the service has the capacity it
needs, and so on. _Scaling a service changes the number of container instances running_ that piece of software,
assigning more computing resources to the service in the process.

It's very easy to define, run, and scale services with the Docker platform -- just write a **docker-compose.yml** file.

#### docker-compose

A **docker-compose.yml** file is a YAML file that defines how Docker containers should behave in production. Here is an
example:

```yaml
version: "3"
services:
  web:
    # replace username/repo:tag with name and image details accordingly
    image: username/repo:tag
    deploy:
      replicas: 5
      resources:
        limits:
          cpus: "0.1"
          memory: 50M
      restart_policy:
        condition: on-failure
    ports:
      - "4000:80"
    networks:
      - webnet
networks:
  webnet:
```

This docker-compose.yml file tells Docker to do the following:

* Pull a image "username/repo" from registry.
* Run 5 instances of that image as a service called "web", limiting each one to use, at most, 10% of the CPU (across all 
  cores), and 50MB of RAM.
* Immediately restart containers if one fails.
* Map port 4000 on the host to web's port 80.
* Instruct web’s containers to share port 80 via a load-balanced network called "webnet". (Internally, the containers 
  themselves publish to web's port 80 at an ephemeral port.)

Define the webnet network with the default settings (which is a load-balanced overlay network).

### Docker Compose

#### Networking in Compose

By default Compose sets up a single [network](https://docs.docker.com/engine/reference/commandline/network_create/) for 
our app. Each container for a service joins the default network and is both reachable by other containers on that
network, and **_discoverable by them at a hostname identical to the container name_**.

> 💡 Our app's network is given a name based on the "project name", which is based on the _name of the directory it
> lives in_. We can override the project name with either the
> [**--project-name** flag](https://docs.docker.com/compose/reference/) or the
> [**COMPOSE_PROJECT_NAME** environment variable](https://docs.docker.com/compose/reference/envvars/#compose_project_name).

For example, suppose our app is in a directory called "myapp", and our "docker-compose.yml" looks like this:

```yaml
version: "3.9"
services:
  web:
    build: .
    ports:
      - "8000:8000"
  db:
    image: postgres
    ports:
      - "8001:5432"
```

When we run `docker compose up`, the following happens:

A network called "myapp_default" is created.
A container is created using `web`'s configuration. It joins the network "myapp_default" under the name "**web**".
A container is created using `db`'s configuration. It joins the network "myapp_default" under the name "**db**".

Each container can now look up the hostname **web** or **db** and get back the appropriate container's IP address. For 
example, **web**'s application code could connect to the URL **postgres://db:5432** and start using the Postgres
database.

It is important to note the distinction between **HOST_PORT** and **CONTAINER_PORT**. In the example above, for **db**, 
the HOST_PORT is 8001 and the container port is 5432 (postgres default). **Networked service-to-service communication
uses the CONTAINER_PORT**. When HOST_PORT is defined, the service is accessible outside the swarm as well.

Within the **web** container, the connection string to **db** would look like "postgres://db:5432", and from the host 
machine, the connection string would look like "postgres://{DOCKER_IP}:8001".

### Swarms

A **swarm** is a group of machines that are running Docker and joined into a cluster. After that has happened, we
continue to run the Docker commands we are used to, but now they are executed on a cluster by a **swarm manager**. _The 
machines in a swarm can be physical or virtual_. After joining a swarm, they are referred to as **nodes**.

Swarm managers can use several strategies to run containers, such as "emptiest node" -- which fills the least utilized 
machines with containers. Or "global", which ensures that each machine gets exactly one instance of the specified 
container. We instruct the swarm manager to use these strategies in the Compose file, just like the one we have already 
been using.

Swarm managers are the only machines in a swarm that can execute your commands, or authorize other machines to join the 
swarm as workers. Workers are just there to provide capacity and do not have the authority to tell any other machine
what it can and cannot do.

### Volumes

Volumes are the preferred mechanism for persisting data generated by and used by Docker containers. While
[bind mounts](https://docs.docker.com/storage/bind-mounts/) are dependent on the directory structure and OS of the host 
machine, volumes are completely managed by Docker. Volumes have several advantages over bind mounts:

* Volumes are easier to back up or migrate than bind mounts.
* You can manage volumes using Docker CLI commands or the Docker API.
* Volumes work on both Linux and Windows containers.
* Volumes can be more safely shared among multiple containers.
* Volume drivers let you store volumes on remote hosts or cloud providers, to encrypt the contents of volumes, or to add 
  other functionality.
* New volumes can have their content pre-populated by a container.
* Volumes on Docker Desktop have much higher performance than bind mounts from Mac and Windows hosts.

In addition, volumes are often a better choice than persisting data in a container’s writable layer, because a volume
does not increase the size of the containers using it, and the volume’s contents exist outside the lifecycle of a given 
container.

![Error loading types-of-mounts-volume.png]({{ "/assets/img/types-of-mounts-volume.png" | relative_url}})

If your container generates non-persistent state data, consider using a
[tmpfs mount](https://docs.docker.com/storage/tmpfs/) to avoid storing the data anywhere permanently, and to **increase
the container's performance** by avoiding writing into the container’s writable layer.

Volumes use rprivate bind propagation, and bind propagation is not configurable for volumes.

> 📋 **Bind propagation** refers to whether or not mounts created within a given bind-mount can be propagated to
> replicas of that mount. Consider a mount point `/mnt`, which is also mounted on `/tmp`. The propagation settings
> control whether a mount on `/tmp/a` would also be available on `/mnt/a`. Each propagation setting has a recursive 
> counterpoint. In the case of recursion, consider that `/tmp/a` is also mounted as `/foo`. The propagation settings 
> control whether `/mnt/a` and/or `/tmp/a` would exist.
> 
> Meanwhile "**rprivate**" means that any changes to the mount or mounts underneath that mount point are prevented from 
> affecting the host.

#### Create and Manage Volumes

Unlike a bind mount, you can create and manage volumes outside the scope of any container. To create a volume

```bash
docker volume create my-vol
```

To list volumes

```bash
docker volume ls

local               my-vol
```

To inspect a volume:

```bash
docker volume inspect my-vol
[
    {
        "Driver": "local",
        "Labels": {},
        "Mountpoint": "/var/lib/docker/volumes/my-vol/_data",
        "Name": "my-vol",
        "Options": {},
        "Scope": "local"
    }
]
```

To remove a volume:

```bash
docker volume rm my-vol
```

#### Start a Container with a Volume

If we start a container with a volume that does not yet exist, Docker creates the volume for us. The following example 
mounts the volume `myvol2` into `/app/` in the container.

```bash
docker run -d --name devtest -v myvol2:/app nginx:latest
```

Use `docker inspect devtest` to verify that the volume was created and mounted correctly. Look for the Mounts section:

```json
"Mounts": [
    {
        "Type": "volume",
        "Name": "myvol2",
        "Source": "/var/lib/docker/volumes/myvol2/_data",
        "Destination": "/app",
        "Driver": "local",
        "Mode": "",
        "RW": true,
        "Propagation": ""
    }
],
```

This shows that the mount is a volume, it shows the correct source and destination, and that the mount is read-write.

Stop the container and remove the volume. Note volume removal is a separate step.

```bash
docker container stop devtest
docker container rm devtest
docker volume rm myvol2
```

#### Use a Volume with docker-compose

A single docker compose service with a volume looks like this:

```yaml
version: "3.9"
services:
  frontend:
    image: node:lts
    volumes:
      - myapp:/home/node/app
volumes:
  myapp:
```

On the first invocation of `docker-compose up` the volume will be created. The same volume will be reused on following 
invocations.

A volume may be created directly outside of compose with docker volume create and then referenced inside
docker-compose.yml as follows:

```yaml
version: "3.9"
services:
  frontend:
    image: node:lts
    volumes:
      - myapp:/home/node/app
volumes:
  myapp:
    external: true
```

For more information about using volumes with compose see
[the compose reference](https://docs.docker.com/compose/compose-file/compose-file-v3/#volume-configuration-reference).

#### Start a Service with Volumes

### Backup, Restore, or Migrate Data Volumes

Volumes are useful for backups, restores, and migrations. It is, however, not a good idea to keep the volume on the
host machine. It is a single point of failure. We need to "back up the container backup" or "back up the volume"

It is a must to move the volume to cloud storage or any other backup location. So that, even if the container crashes
we will have all the data.

#### Back Up a Volume

Suppose we have a running container whose name is "my-container" eactly as we seen in 

```
$ docker ps -a
CONTAINER ID   IMAGE             COMMAND                  CREATED         STATUS         PORTS                    NAMES
h32y87888we2   my-image          ...                      59 minutes ago  Up 59 minutes  0.0.0.0:3456->3456/tcp   my-container
```

This container started with a volume called "my-volume". This volume has been mounted to the `/app-data` directory
inside "my-container"

Then in the next volume backup command, we will:

* launch a new Ubuntu container and mount "my-volume" to that container
* mount the _current_ local host directory to the Ubuntu container at "/backup" in the container

  > 💡 **Named Volumes** v.s. **Path Based Volumes**
  > 
  > * **Named volumes** look like this `my-volume:/var/lib/postgresql/data`. Docker will manage the volume for us. On
  > Linux, that volume will get saved to `/var/lib/docker/volumes/my-volume/_data`. On Windows or MacOS, however, it
  > will get saved to
  > [somewhere else](https://nickjanetakis.com/blog/docker-tip-70-gain-access-to-the-mobylinux-vm-on-windows-or-macos), 
  > but the moral of the story is, we don't need to worry about it. We can set it and forget it, and it will work across 
  > all systems.
  > * **Path based volumes** serve the same purpose as named volumes, except we are responsible for managing where the 
  > volume gets saved on the Docker host. For example if we do `./my-volume:/var/lib/postgresql/data` then a my-volume/ 
  > _directory_ would get created in the _current directory_ on the Docker host.

* Pass, inside the Ubuntu container, a command that compresses (`tar`) the contents of the volume to a **backup.tar**
  file which will be located at `/backup/backup.tar` in the Ubuntu container

```bash
docker run --rm --volumes-from my-container -v $(pwd):/backup ubuntu tar cvf /backup/backup.tar /app-data
```

> 💡 Note that the Ubuntu container has 2 volumes mounted and, therefore, has access to paths in both volumes:
> 
> 1. "my-volume" from "my-container"
> 2. a path-based volume located at `$(pwd)` of out host machine
> 
> What the `tar` command just did was compressing the data in "my-volume" and put the compressed in the path-based
> volume.

When the command completes and the container stops, we are left with a backup of our "my-volume" located `$(pwd)` on
our host machine

#### Restore Volume From Backup

With the backup just created, we can restore it to a new container of the same image.

Let's say "my-container" from the [previous section](#back-up-a-volume) is gone (crashed, or somebody had some fun with 
`sudo rm -rf /` inside container, or we simply want to restore the container to a previous backed up state), and we just
instantiated a new container (whose name is, for example, "**new-container**") of the same image. We will now load our 
backup data into the "new-container"

The "new-container" was created with a new volume (let's give it a name "new-volume") mounted at the same container path
of "/app-data"

We also have our `backup.tar` we [just created](#back-up-a-volume) located at `$(pwd)`. Assuming "new-container" is
properly running now, executing the command below will effectively put un-compressed content of `backup.tar`, i.e. the
original backed up data, into the `/app-data` directory in "new-container":

> 💡 For some images, we might need to restart container so that it will reload "app-data", which has been out backup
> data

```bash
docker run --rm --volumes-from new-container -v $(pwd):/backup ubuntu bash -c "cd /app-data && mv /backup/backup.tar . && tar xvf backup.tar --strip 1"
```

> 📋 Note that at this moment we've also successfully loaded backup data into "new-volume", which links to the
> "new-container"

We can use this technique to automate backup, migration and restore testing using our preferred tools.

> 💡 **Fixing "scp: Permission denied" issue**
> 
> The migration often involves loading the backup file (i.e. "backup.tar") onto the new server using 'scp'. The
> permission error occurs when the backup file already existed in the scp target location and the existing file had 
> read-only permissions (preventing the file from being overwritten). In this case, we simply logged into the server and 
> deleted the existing file and that will resolve the problem.

Docker cAdvisor
---------------

[cAdvisor](https://github.com/google/cadvisor) (Container Advisor) provides Docker container users an understanding of
the resource usage and performance characteristics of their running containers. It is a running daemon that collects, 
aggregates, processes, and exports information about running containers. Specifically, for each container it keeps 
resource isolation parameters, historical resource usage, histograms of complete historical resource usage and network 
statistics. This data is exported by container and machine-wide.

Although [cAdvisor](https://github.com/google/cadvisor) has some prelimilary (useful though) UI. It also offers

1. [RESTful API to query container stats](https://github.com/google/cadvisor/blob/master/docs/api.md)
2. [Export capability to common data storage, such as Elasticsearch](https://github.com/google/cadvisor/blob/master/docs/storage/README.md)

To pull the image and run it:

    sudo docker run   --volume=/:/rootfs:ro   --volume=/var/run/docker.sock:/var/run/docker.sock:rw  --volume=/sys:/sys:ro   --volume=/var/lib/docker/:/var/lib/docker:ro   --volume=/dev/disk/:/dev/disk:ro   --publish=8080:8080   --detach=true   --name=cadvisor   --privileged   --device=/dev/kmsg   gcr.io/cadvisor/cadvisor:v0.36.0

![cAdvisor Screenshot 1]({{ "/assets/img/cadvisor-1.png" | relative_url}})
![cAdvisor Screenshot 2]({{ "/assets/img/cadvisor-2.png" | relative_url}})

### [docker-container-stats](https://github.com/virtualzone/docker-container-stats)

[cAdvisor](https://github.com/google/cadvisor) is good for customizing container monitoring, but it's heavy. A
quick-and-lightweight option would be [docker-container-stats](https://github.com/virtualzone/docker-container-stats)

![docker-container-stats Screenshot]({{ "/assets/img/docker-container-stats.png" | relative_url}})


Docker Troubleshooting
----------------------

### Container

If a container is **not running**:

```
$ docker ps -a
CONTAINER ID   IMAGE             COMMAND                  CREATED          STATUS                      PORTS     NAMES
234rgfq34tg3   image/name          "…"                18 minutes ago   Exited (1) 18 minutes ago             container-name
```

We can retrospect it using

    docker logs 234rgfq34tg3

or

    docker logs container-name

**To enter in a Docker container** already running with a new TTY, with docker 1.3, there is a new command
[**docker exec**](https://docs.docker.com/engine/reference/commandline/exec/). This allows us to enter a running
container:

    docker exec -it [container-id] bash

> This assumes `bash` is installed on our container. We may run `sh` or whatever interactive shell is installed on the
> container.


### Proxy

```bash
% docker pull ubuntu:latest
Error response from daemon: Get https://registry-1.docker.io/v2/: net/http: request canceled while waiting for
connection (Client.Timeout exceeded while awaiting headers)
```

This usually means you need to setup proxy on your local machine. Ignoring all details about proxy, you need to make
the following Docker Desktop configuration. To do it(on Mac), click the little Docker logo on the top bar of your
screen. Click "Resources" and then hit "PROXIES" in the dropdown menu. You should see a configuration window like the
following:

![Error loading docker-proxy-config.png]({{ "/assets/img/docker-proxy-config.png" | relative_url}})

Note that configuring proxy in `~/.bashrc` is not the same thing as doing the config above. Docker Desktop doesn't look
at the `~/.bashrc` for proxy settings.
