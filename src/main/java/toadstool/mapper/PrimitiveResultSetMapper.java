package toadstool.mapper;

import java.sql.ResultSet;

class PrimitiveResultSetMapper implements ResultSetMapper {
    private PropertyMapper propertyMapper;

    @Override
    public <E> E MapResultSet(ResultSet resultSet, Class<E> targetClass) throws Exception {
        if (propertyMapper == null) {
            var resultSetMetadata = resultSet.getMetaData();
            var columnName = resultSetMetadata.getColumnName(1);
            propertyMapper = new PropertyMapper(1, columnName, null, targetClass);
        }

        var typedObject = propertyMapper.getTypedObject(resultSet);

        return (E) typedObject;
    }
}