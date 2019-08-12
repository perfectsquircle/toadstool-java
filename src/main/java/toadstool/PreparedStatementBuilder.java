package toadstool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class PreparedStatementBuilder implements StatementBuilder {
    private final Map<String, Object> parameters;
    private final List<Object> indexedParameters;
    private String sql;
    private DatabaseContext context;
    private ResultSetMapper resultSetMapper;

    public PreparedStatementBuilder() {
        super();
        this.parameters = new HashMap<String, Object>();
        this.indexedParameters = new ArrayList<>();
        this.resultSetMapper = new SimpleResultSetMapper();
    }

    public PreparedStatementBuilder(String sql) {
        this();
        this.sql = sql;
    }

    public StatementBuilder withContext(DatabaseContext context) {
        this.context = context;
        return this;
    }

    public StatementBuilder withParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public PreparedStatement build(Connection connection) throws SQLException {
        var formattedSql = formatSql();
        var preparedStatement = connection.prepareStatement(formattedSql);
        var i = 1;
        for (var parameter : indexedParameters) {
            preparedStatement.setObject(i++, parameter);
        }
        return preparedStatement;
    }

    private String formatSql() {
        var formattedSql = sql;
        for (var key : parameters.keySet()) {
            formattedSql = formattedSql.replaceFirst("@" + key, "?");
            indexedParameters.add(parameters.get(key));
        }
        return formattedSql;
    }

    @Override
    public <E> List<E> toListOf(Class<E> targetClass) throws SQLException {
        return withResultSet((ResultSet resultSet) -> {
            var list = new ArrayList<E>();
            Function<ResultSet, E> mapper = null;
            while (resultSet.next()) {
                if (mapper == null) {
                    mapper = resultSetMapper.compileMapper(targetClass, resultSet.getMetaData());
                }
                var instance = mapper.apply(resultSet);
                list.add(instance);
            }
            return list;
        });
    }

    public <E> Stream<E> stream(Class<E> targetClass) throws SQLException {
        return withResultSet((ResultSet resultSet) -> {
            var resultSetMetadata = resultSet.getMetaData();
            var mapper = resultSetMapper.compileMapper(targetClass, resultSetMetadata);
            return stream(resultSet, mapper);
        });
    }

    @Override
    public <E> Optional<E> first(Class<E> targetClass) throws SQLException {
        return withResultSet((ResultSet resultSet) -> {
            E instance = null;
            if (resultSet.next()) {
                var resultSetMetadata = resultSet.getMetaData();
                var mapper = resultSetMapper.compileMapper(targetClass, resultSetMetadata);
                instance = mapper.apply(resultSet);
            }
            return Optional.of(instance);
        });
    }

    @Override
    public int execute() throws SQLException {
        try (var connection = this.context.getConnection(); var preparedStatement = this.build(connection);) {
            return preparedStatement.executeUpdate();
        }
    }

    private <E> E withResultSet(ThrowingFunction<ResultSet, E> callback) throws SQLException {
        try (var connection = this.context.getConnection();
                var preparedStatement = this.build(connection);
                var resultSet = preparedStatement.executeQuery();) {
            return callback.apply(resultSet);
        }
    }

    private <E> Stream<E> stream(ResultSet resultSet, Function<ResultSet, E> mapper) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<E>(Long.MAX_VALUE, Spliterator.ORDERED) {
            @Override
            public boolean tryAdvance(Consumer<? super E> action) {
                try {
                    if (!resultSet.next())
                        return false;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                action.accept(mapper.apply(resultSet));
                return true;
            }
        }, false);
    }
}