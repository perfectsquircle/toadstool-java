package toadstool;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.function.Function;

interface ResultSetMapper {
    public <E> Function<ResultSet, E> compileMapper(Class<E> targetClass, ResultSetMetaData resultSetMetadata)
            throws Exception;
}