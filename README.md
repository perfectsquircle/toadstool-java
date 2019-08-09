# 🍄 Toadstool (Java)

> The Java port of the Dapper alternative that nobody asked for.

```java
// Do a SELECT then map the results to a list.
List<Customer> customers = context
    .prepareStatement("SELECT first_name, last_name FROM customer WHERE last_name = @lastName")
    .withParameter("lastName", "Cuervo")
    .toListOf(Customer.class);
```