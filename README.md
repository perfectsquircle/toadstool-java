# 🍄 Toadstool (Java)

> The Java port of the Dapper alternative that nobody asked for.

```java
// Do a SELECT then map the results to a list.
List<Customer> customers = context
    .prepareStatement("SELECT first_name, last_name FROM customer WHERE last_name = @lastName")
    .withParameter("lastName", "Cuervo")
    .toListOf(Customer.class);
```

## Usage

### DbContext

The entrypoint into the Toadstool API is the `DatabaseContext`. Typically, only one of these should be created per database in your application lifetime (or HTTP Request lifetime.)

The context must be able to create new instances of `Connection`, so we pass it the URL of our database server.

```java
var context = new SimpleDatabaseContext("jdbc:postgresql://localhost:5432/postgres");
```

### Prepared Statements

```java
class Customer {
    private String firstName;
    private String lastName;

    // ... getters and setters
}

// Do a SELECT then map the results to a list.
List<Customer> customers = context
    .prepareStatement("SELECT first_name, last_name FROM customer WHERE last_name = @lastName")
    .withParameter("lastName", "Cuervo")
    .toListOf(Customer.class);

// Execute an INSERT
int rowsAffected = context
    .prepareStatement("INSERT INTO customer(fist_name, last_name) VALUES (@firstName, @lastName)")
    .withParameter("firstName", "Jose")
    .withParameter("lastName", "Cuervo")
    .execute();

// Execute an UPDATE
int rowsAffected = context
    .prepareStatement("UPDATE customer SET first_name = @firstName where last_name = @lastName")
    .withParameter("firstName", "Jerry")
    .withParameter("lastName", "Cuervo")
    .execute();

// Execute a DELETE
int rowsAffected = context
    .prepareStatement("DELETE FROM customer where last_name = @lastName")
    .withParameter("lastName", "Cuervo")
    .execute();
```

### Transactions

To start a new database transaction, call `beginTransaction` on `DatabaseContext`. This returns a `TransactionContext`.

```java
try (var transaction = context.beginTransaction()) {
    int rowsAffected = transaction
        .prepareStatement("INSERT INTO customer(fist_name, last_name) VALUES (@firstName, @lastName)")
        .withParameter("firstName", firstName)
        .withParameter("lastName", lastName)
        .execute();

    int rowsAffected = transaction
        .prepareStatement("INSERT INTO order(fist_name, last_name) VALUES (@productId, @quantity)")
        .withParameter("productId", productId)
        .withParameter("quantity", quantity)
        .execute();

    transaction.commit();
}
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