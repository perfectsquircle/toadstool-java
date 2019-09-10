package toadstool.mapper;

public interface ResultSetMapperFactory {
    <E> ResultSetMapper CreateResultSetMapper(Class<E> target);
}