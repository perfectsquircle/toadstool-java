package toadstool.mapper;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import toadstool.util.ToadstoolException;

class PropertyMapper {
    private final int columnIndex;
    private final String columnName;
    private final Method propertySetter;
    private final Class<?> propertyType;

    public PropertyMapper(int columnIndex, String columnName, Method propertySetter, Class<?> propertyType) {
        super();
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.propertySetter = propertySetter;
        this.propertyType = propertyType;
    }

    public <E> void mapProperty(ResultSet resultSet, E typedInstance)
            throws Exception {
        if (!propertySetter.canAccess(typedInstance)) {
            return;
        }
        var typedObject = getTypedObject(resultSet);
        try {
            propertySetter.invoke(typedInstance, typedObject);
        } catch (IllegalArgumentException e) {
            throw new ToadstoolException(
                    String.format("Cannot call setter %s with column \"%s\" (%s)",
                            propertySetter,
                            columnName,
                            typedObject.getClass()));
        }
    }

    public Object getTypedObject(ResultSet resultSet) throws SQLException {
        if (propertyType.isAssignableFrom(String.class)) {
            return resultSet.getString(columnIndex);
        }
        if (propertyType.isAssignableFrom(boolean.class)) {
            return resultSet.getBoolean(columnIndex);
        }
        if (propertyType.isAssignableFrom(byte.class)) {
            return resultSet.getByte(columnIndex);
        }
        if (propertyType.isAssignableFrom(short.class)) {
            return resultSet.getShort(columnIndex);
        }
        if (propertyType.isAssignableFrom(int.class)) {
            return resultSet.getInt(columnIndex);
        }
        if (propertyType.isAssignableFrom(long.class)) {
            return resultSet.getLong(columnIndex);
        }
        if (propertyType.isAssignableFrom(float.class)) {
            return resultSet.getFloat(columnIndex);
        }
        if (propertyType.isAssignableFrom(double.class)) {
            return resultSet.getDouble(columnIndex);
        }
        if (propertyType.isAssignableFrom(byte[].class)) {
            return resultSet.getBytes(columnIndex);
        }
        if (propertyType.isAssignableFrom(java.sql.Date.class)) {
            return resultSet.getDate(columnIndex);
        }
        if (propertyType.isAssignableFrom(java.sql.Time.class)) {
            return resultSet.getTime(columnIndex);
        }
        if (propertyType.isAssignableFrom(java.sql.Timestamp.class)) {
            return resultSet.getTimestamp(columnIndex);
        }
        if (propertyType.isAssignableFrom(java.io.InputStream.class)) {
            return resultSet.getBinaryStream(columnIndex);
        }
        if (propertyType.isAssignableFrom(java.time.Instant.class)) {
            return resultSet.getTimestamp(columnIndex).toInstant();
        }
        if (propertyType.isAssignableFrom(java.time.LocalDate.class)) {
            return resultSet.getDate(columnIndex).toLocalDate();
        }

        return resultSet.getObject(columnIndex);
    }
}