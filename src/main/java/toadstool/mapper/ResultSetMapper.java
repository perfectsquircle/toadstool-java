package toadstool.mapper;

import java.sql.ResultSet;

public interface ResultSetMapper {
    <E> E MapResultSet(ResultSet resultSet, Class<E> target) throws Exception;
}
