package toadstool.mapper;

import java.util.Set;

public class SimpleResultSetMapperFactory implements ResultSetMapperFactory {
    private static final Set<Class<?>> primitiveTypes = Set.of(
            String.class,
            Boolean.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            byte[].class,
            java.time.Instant.class,
            java.time.LocalDate.class,
            java.sql.Date.class,
            java.sql.Time.class,
            java.sql.Timestamp.class,
            java.io.InputStream.class);

    @Override
    public <E> ResultSetMapper CreateResultSetMapper(Class<E> target) {
        if (primitiveTypes.contains(target)) {
            return new PrimitiveResultSetMapper();
        }

        return new ClassResultSetMapper();
    }
}