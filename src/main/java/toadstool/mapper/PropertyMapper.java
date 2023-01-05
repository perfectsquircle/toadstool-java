package toadstool.mapper;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import toadstool.util.ToadstoolException;

class PropertyMapper {
    private String columnName;
    private Method propertySetter;
    private Class<?> propertyType;

    public PropertyMapper() {
        super();
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

    private Object getTypedObject(ResultSet resultSet) throws SQLException {
        if (propertyType.isAssignableFrom(String.class)) {
            return resultSet.getString(columnName);
        }
        if (propertyType.isAssignableFrom(boolean.class)) {
            return resultSet.getBoolean(columnName);
        }
        if (propertyType.isAssignableFrom(byte.class)) {
            return resultSet.getByte(columnName);
        }
        if (propertyType.isAssignableFrom(short.class)) {
            return resultSet.getShort(columnName);
        }
        if (propertyType.isAssignableFrom(int.class)) {
            return resultSet.getInt(columnName);
        }
        if (propertyType.isAssignableFrom(long.class)) {
            return resultSet.getLong(columnName);
        }
        if (propertyType.isAssignableFrom(float.class)) {
            return resultSet.getFloat(columnName);
        }
        if (propertyType.isAssignableFrom(double.class)) {
            return resultSet.getDouble(columnName);
        }
        if (propertyType.isAssignableFrom(byte[].class)) {
            return resultSet.getBytes(columnName);
        }
        if (propertyType.isAssignableFrom(java.sql.Date.class)) {
            return resultSet.getDate(columnName);
        }
        if (propertyType.isAssignableFrom(java.sql.Time.class)) {
            return resultSet.getTime(columnName);
        }
        if (propertyType.isAssignableFrom(java.sql.Timestamp.class)) {
            return resultSet.getTimestamp(columnName);
        }
        if (propertyType.isAssignableFrom(java.io.InputStream.class)) {
            return resultSet.getBinaryStream(columnName);
        }

        if (propertyType.isAssignableFrom(java.time.Instant.class)) {
            return resultSet.getTimestamp(columnName).toInstant();
        }
        if (propertyType.isAssignableFrom(java.time.LocalDate.class)) {
            return resultSet.getDate(columnName).toLocalDate();
        }

        return resultSet.getObject(columnName);
    }

    public PropertyMapper withColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public PropertyMapper withPropertySetter(Method propertySetter) {
        this.propertySetter = propertySetter;
        return this;
    }

    public PropertyMapper withPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
        return this;
    }
}