package toadstool.mapper;

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

class ClassResultSetMapper implements ResultSetMapper {
    private Map<String, Method> columnToPropertyMap;

    @Override
    public <E> E MapResultSet(ResultSet resultSet, Class<E> targetClass) throws Exception {
        if (columnToPropertyMap == null) {
            columnToPropertyMap = createColumnToPropertyMap(targetClass, resultSet.getMetaData());
        }
        var instance = targetClass.getDeclaredConstructor().newInstance();
        var typedInstance = targetClass.cast(instance);

        for (var columnName : columnToPropertyMap.keySet()) {
            var setter = columnToPropertyMap.get(columnName);
            if (setter.canAccess(typedInstance)) {
                setter.invoke(typedInstance, resultSet.getObject(columnName));
            }
        }

        return typedInstance;
    }

    public <E> Map<String, Method> createColumnToPropertyMap(Class<E> targetClass, ResultSetMetaData resultSetMetadata)
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

    private static Collection<String> getVariants(String s) {
        return List.of(s, s.toLowerCase(), s.replaceAll("_", ""), s.replaceAll("_", "").toLowerCase());
    }
}