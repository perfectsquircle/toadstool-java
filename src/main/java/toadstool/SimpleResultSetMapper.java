package toadstool;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.function.Function;

class SimpleResultSetMapper implements ResultSetMapper {
    public <E> Function<ResultSet, E> CompileMapper(Class<E> targetClass, ResultSetMetaData resultSetMetadata) {

        Function<ResultSet, E> x = (resultSet) -> {
            try {
                var instance = targetClass.getDeclaredConstructor().newInstance();
                var typedInstance = targetClass.cast(instance);
                return typedInstance;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return x;
    }
}