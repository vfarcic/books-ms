MongoDB
=======

[https://registry.hub.docker.com/u/dockerfile/mongodb/](https://registry.hub.docker.com/u/dockerfile/mongodb/)

```bash
mkdir -p /data/db
sudo docker run -d --name mongodb \
  -p 27017:27017 \
  -v /data/db:/data/db \
  dockerfile/mongodb
```

Test
====

```bash
sbt ~test-quick
```

```bash
sbt test
```

Package
=======

```bash
sbt assembly
sudo docker build -t books-service .
```