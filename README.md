# Purchase-cart-service

it is a demo project where a RESTful API endpoint is exposed. It returns pricing information about new order.

## Architecture overview

### Model

The application relies on a SQL database.
A brief schema is reported below (JPA persistence model):

```mermaid 
classDiagram
    direction BT
    class CartOrderEntity {
        Date creationDate
        BigDecimal priceValue
        BigDecimal vatValue
    }
    class CartOrderProductEntity {
        int quantity
    }
    class ProductEntity {
        int availableQuantity
        String description
        BigDecimal unitPrice
    }
    class VatRateEntity {
        String description
        Float percentage
    }

    CartOrderEntity "0..1" --> "0..*" CartOrderProductEntity
    CartOrderProductEntity "0..*" --> "0..1" CartOrderEntity
    CartOrderProductEntity "0..*" --> "0..1" ProductEntity
    ProductEntity "0..1" --> "0..1" VatRateEntity

```

The product table stores all details of a product and refers to a vat rate where vat details are stores.
Vat percentages are usually standardized across products, meaning there’s no need to duplicate vat values in the product
table.

The cartOrder table stores details of an order and refers to table cartOrderProduct.
Here are stored the products present in each order.

### Application

[PurchaseOrderOrchestratorService](src/main/java/com/example/demo/service/implementation/PurchaseOrderOrchestratorService.java)
class is responsible for orchestrating the entire order processing workflow.
It ensures that all steps involved in placing an order are executed in sequence while maintaining transactional
integrity.

#### Key Responsibilities

1. Order Processing Flow Management:
    - Iterates through each step required to complete an order.
    - Executes operations such as product validation, quantity reservation, price calculation, and final order creation.
2. Transactional Consistency:
    - The method executing the order workflow is marked as transactional.
    - If any step fails (e.g., insufficient quantity, database error), all previously performed operations within the
      transaction are rolled back, preventing inconsistent or partial order states.

## How to (build-test) run

The full directory will be mounted under `/mnt/` folder into the docker image built from [Dockerfile](Dockerfile).
The instructions below address two scenario:

1. Docker used on linux
2. Podman used on windows

### 1. Docker on bash (on ubuntu)

From the root of the project folder.
Give executable permissions to files in the scripts folder.
Then build the container image.

```shell
chmod +x scripts/ -R
docker build -t subito/purchase-cart-service . 
```

The folder scripts contains 3 files:

* [build.sh](scripts/build.sh): it executes maven `clean` and `package` phases within the container.
    * `clean`: clean up artifacts created by prior builds
    * `package`: take the compiled code and package it in its distributable format, such as a JAR.
* [tests.sh](scripts/tests.sh): it executes maven `test` phase
    * `test`: test the compiled source code using a suitable unit testing framework
* [run.sh](scripts/run.sh): it executes `java -jar` to run the application

Run build script:

```shell
docker run --rm --name cart_service_build -v "$(pwd)":/mnt -p 9090:9090 -w /mnt subito/purchase-cart-service ./scripts/build.sh
```

Run test script:

```shell
docker run --rm --name cart_service_tests -v "$(pwd)":/mnt -p 9090:9090 -w /mnt subito/purchase-cart-service ./scripts/tests.sh
```

Run application:

```shell
docker run --rm --name cart_service_run -v "$(pwd)":/mnt -p 9090:9090 -w /mnt subito/purchase-cart-service ./scripts/run.sh
```

- the flag `--rm` is used to delete the container after the task fot it is complete
- the flag `--name` is used to set the container name

Under the root folder are available also `mvnw` scripts:

* Allows anyone who clones / checks-out this repo to build the project without having to install Maven first.
* Ensures that the version of Maven in use is the version with which this project is compatible.

### 2. Podman on powershell (on windows)

From the root of the project folder.
Build container image:

```shell
podman build -t subito/purchase-cart-service . 
```

Run build script:

```shell
podman run --rm --name cart_service_build -v ${pwd}:/mnt -p 9090:9090 -w /mnt subito/purchase-cart-service scripts/build.sh
```

Run test script:

```shell
podman run --rm --name cart_service_test -v ${pwd}:/mnt -p 9090:9090 -w /mnt subito/purchase-cart-service scripts/tests.sh
```

Run application:

```shell
podman run --rm --name cart_service_run -v ${pwd}:/mnt -p 9090:9090 -w /mnt subito/purchase-cart-service scripts/run.sh
```

N.B. the commands reported for podman may be also suitable for docker Windows users.
Replace `podman` with `docker` in every command: e.g. `docker build -t subito/purchase-cart-service .`

### Test structure

Test structure reflects the src directory structure.
Each class presenting business logic is tested using unit tests (the one annotated with
`@ExtendWith(MockitoExtension.class)`).
Class [PurchaseOrderOrchestratorService](src/main/java/com/example/demo/service/implementation/PurchaseOrderOrchestratorService.java)
has also integration tests to validate the purchase order flow.
Class [PurchaseOrderController](src/main/java/com/example/demo/controller/PurchaseOrderController.java) has an
end-to-end test.
It simulates a http call to the exposed endpoint _'/api/v1/order'_ and validate the response data.
N.B data is used on by classes annotated with `@SpringBootTest` load databse test data from
the [populate-products-test](src/test/resources/data/populate-products-test.sql)
sql file.

### Data insertion

For this project an in memory database is used. This does not influence the application code that is agnostic on the
database implementation.
Each time the application run, it initializes the schema on startup from configuration properties of the main Hibernate EntityManagerFactory based on standard JPA properties and HibernateSettings.
On exit, it cleans all the schema (drops the tables).
This behavior can be customized on the [application.properties](src/main/resources/application.properties) file
Database data are populated by [populate-products](src/main/resources/data/populate-products.sql) sql file.
If you want to add new data then you have to add your insert statements on this file.
Then build the container image and run the build, test and run scripts (the script test is not mandatory, but it is
recommended).
