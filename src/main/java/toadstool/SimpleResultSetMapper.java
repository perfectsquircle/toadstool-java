package toadstool;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SimpleResultSetMapper implements ResultSetMapper {
    public <E> ThrowingFunction<ResultSet, E> compileMapper(Class<E> targetClass, ResultSetMetaData resultSetMetadata)
            throws Exception {
        var columnToPropertyMap = createColumnToPropertyMap(targetClass, resultSetMetadata);

        return (resultSet) -> {
            var instance = targetClass.getDeclaredConstructor().newInstance();
            var typedInstance = targetClass.cast(instance);

            for (var columnName : columnToPropertyMap.keySet()) {
                var setter = columnToPropertyMap.get(columnName);
                if (setter.canAccess(typedInstance)) {
                    setter.invoke(typedInstance, resultSet.getObject(columnName));
                }
            }

            return typedInstance;
        };
    }

    private <E> Map<String, Method> createColumnToPropertyMap(Class<E> targetClass, ResultSetMetaData resultSetMetadata)
            throws SQLException, IntrospectionException {
        var map = new HashMap<String, Method>();
        var beanInfo = Introspector.getBeanInfo(targetClass);
        var propertyDescriptors = Arrays.asList(beanInfo.getPropertyDescriptors());

        var columnCount = resultSetMetadata.getColumnCount();
        for (var i = 1; i <= columnCount; i++) {
            var columnName = resultSetMetadata.getColumnName(i);
            var columnNameVariants = getVariants(columnName);

            var match = propertyDescriptors.stream().filter(propertyDescriptor -> {
                var propertyNameVariants = getVariants(propertyDescriptor.getName());
                return propertyNameVariants.stream()
                        .anyMatch(propertyName -> columnNameVariants.contains(propertyName));
            }).findFirst();

            if (!match.isPresent()) {
                continue;
            }

            var writeMethod = match.get().getWriteMethod();
            if (writeMethod == null) {
                continue;
            }
            map.put(columnName, writeMethod);
        }

        return map;
    }

    // @formatter:off
    private Collection<String> getVariants(String s) {
        return List.of(
            s,
            s.toLowerCase(),
            s.replaceAll("_", ""),
            s.replaceAll("_", "").toLowerCase()
        );
    }
    // @formatter:on
}