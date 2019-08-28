# 🍄 Toadstool (Java)

> The Java port of the Dapper alternative that nobody asked for.

```java
// Do a SELECT then map the results to a list.
List<Customer> customers = context
    .prepareStatement("SELECT first_name, last_name FROM customer WHERE last_name = @lastName")
    .withParameter("lastName", "Cuervo")
    .toListOf(Customer.class);
```

## Testing

Prerequisites:
* JDK 12
* Gnu Make
* Docker
* Bash

Running the integration tests requires having a recent version of Docker installed. Two database servers (PostgreSQL and SQL Server) will be brought up with 

```bash
make databases
```

After the servers are up and the databases are restored, the tests can be run.

```bash
make test
```

To bring down the servers and clean up the backup files,

```bash
make clean-databases
```

---

Built with &hearts; by Calvin.

&copy; Calvin Furano