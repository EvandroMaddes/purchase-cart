# Purchase-cart-service

## How to (build-test) run

The full directory will be mounted under `/mnt/` folder into the docker image built from [Dockerfile](Dockerfile).

```shell
podman build -t subito/purchase-cart-service . 
```

The folder scripts contains 3 files:

* [build.sh](scripts/build.sh): it executes maven `clean` and `package` phases within the container.
    * `clean`: clean up artifacts created by prior builds
    * `package`: take the compiled code and package it in its distributable format, such as a JAR.
* [tests.sh](scripts/tests.sh): it executes maven `test` phase
    * `test`: test the compiled source code using a suitable unit testing framework
* [run.sh](scripts/run.sh): it executes `java -jar` to run the application

```shell
podman run --rm --name cart_service_build -v ${pwd}:/mnt -p 9090:9090 -w /mnt subito/purchase-cart-service scripts/build.sh
```

```shell
podman run --rm --name cart_service_test -v ${pwd}:/mnt -p 9090:9090 -w /mnt subito/purchase-cart-service scripts/tests.sh
```

```shell
podman run --rm --name cart_service_run -v ${pwd}:/mnt -p 9090:9090 -w /mnt subito/purchase-cart-service scripts/run.sh
```

- the flag `--rm` is used to delete the container after the task fot it is complete
- the flag `--name` is used to set the container name

Under the root folder are available also `mvnw` scripts:

* Allows anyone who clones / checks-out this repo to build the project without having to install Maven first.
* Ensures that the version of Maven in use is the version with which this project is compatible.