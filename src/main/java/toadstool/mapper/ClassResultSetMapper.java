package toadstool.mapper;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import toadstool.ToadstoolException;

class ClassResultSetMapper implements ResultSetMapper {
    private List<PropertyMapper> propertyMappers;

    @Override
    public <E> E MapResultSet(ResultSet resultSet, Class<E> targetClass) throws Exception {
        var declaredConstructor = targetClass.getDeclaredConstructor();
        if (declaredConstructor == null) {
            throw new ToadstoolException("Target class must have no-args constructor.");
        }
        var instance = declaredConstructor.newInstance();
        var typedInstance = targetClass.cast(instance);

        if (propertyMappers == null) {
            propertyMappers = createPropertyMappers(targetClass, resultSet.getMetaData());
        }

        for (var propertyMapper : propertyMappers) {
            propertyMapper.mapProperty(resultSet, typedInstance);
        }

        return typedInstance;
    }

    private static <E> List<PropertyMapper> createPropertyMappers(Class<E> targetClass,
            ResultSetMetaData resultSetMetadata)
            throws SQLException, IntrospectionException, ClassNotFoundException {
        var list = new ArrayList<PropertyMapper>();
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

            var property = match.get();
            var writeMethod = property.getWriteMethod();
            if (writeMethod == null) {
                continue;
            }
            var parameterTypes = writeMethod.getParameterTypes();
            if (parameterTypes.length != 1) {
                continue;
            }
            var mapper = new PropertyMapper()
                    .WithColumnName(columnName)
                    .WithPropertySetter(writeMethod)
                    .WithPropertyType(parameterTypes[0]);
            list.add(mapper);
        }

        return list;
    }

    private static Collection<String> getVariants(String s) {
        return List.of(
                s,
                s.toLowerCase(),
                s.replaceAll("_", ""),
                s.replaceAll("_", "").toLowerCase());
    }
}