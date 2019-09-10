package toadstool.mapper;

public class SimpleResultSetMapperFactory implements ResultSetMapperFactory {
    @Override
    public <E> ResultSetMapper CreateResultSetMapper(Class<E> target) {
        // TODO: detect primitives

        return new ClassResultSetMapper();
    }
}