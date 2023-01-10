package toadstool.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import toadstool.DatabaseContext;
import toadstool.mapper.ResultSetMapper;
import toadstool.mapper.ResultSetMapperFactory;
import toadstool.mapper.SimpleResultSetMapperFactory;
import toadstool.util.Pair;
import toadstool.util.ThrowingFunction;

public class PreparedStatementBuilder implements StatementBuilder {
    private final Map<String, Object> parameters;
    private String sql;
    private DatabaseContext context;
    private ResultSetMapperFactory resultSetMapperFactory;
    private boolean closeConnection;

    private static final Pattern validParameterName = Pattern.compile("^\\w+$");
    private static final Pattern parameterPattern = Pattern.compile("(@(\\w+))");

    public PreparedStatementBuilder() {
        super();
        this.parameters = new HashMap<String, Object>();
        this.resultSetMapperFactory = new SimpleResultSetMapperFactory();
    }

    public PreparedStatementBuilder(String sql) {
        this();
        Objects.requireNonNull(sql);
        this.sql = sql;
    }

    public StatementBuilder withContext(DatabaseContext context) {
        return withContext(context, true);
    }

    public StatementBuilder withContext(DatabaseContext context, boolean closeConnection) {
        Objects.requireNonNull(context);
        this.context = context;
        this.closeConnection = closeConnection;
        return this;
    }

    public StatementBuilder withParameter(String parameterName, Object value) {
        if (!validParameterName.matcher(parameterName).matches()) {
            throw new IllegalArgumentException("Parameter name is not in allowed format.");
        }
        parameters.put(parameterName, value);
        return this;
    }

    public PreparedStatement build(Connection connection) throws SQLException {
        Objects.requireNonNull(sql);
        var pair = formatSql();
        var formattedSql = pair.left;
        var indexedParameters = pair.right;
        var preparedStatement = connection.prepareStatement(formattedSql);
        var i = 1;
        for (var parameter : indexedParameters) {
            preparedStatement.setObject(i++, parameter);
        }
        return preparedStatement;
    }

    public Pair<String, List<Object>> formatSql() {
        var formattedSql = sql;
        var indexedParameters = new ArrayList<>();

        var pattern = parameterPattern;
        Matcher matcher = null;
        int lastIndex = 0;
        while (lastIndex < formattedSql.length()) {
            matcher = pattern.matcher(formattedSql);
            if (!matcher.find(lastIndex)) {
                break;
            }
            lastIndex = matcher.end();
            var parameterName = matcher.group(2);
            if (parameters.containsKey(parameterName)) {
                formattedSql = matcher.replaceFirst("?");
                indexedParameters.add(parameters.get(parameterName));
            }
        }
        return new Pair<String, List<Object>>(formattedSql, indexedParameters);
    }

    @Override
    public <E> List<E> toListOf(Class<E> targetClass) throws SQLException {
        return withResultSet((ResultSet resultSet) -> {
            var list = new ArrayList<E>();
            ResultSetMapper mapper = null;
            while (resultSet.next()) {
                if (mapper == null) {
                    mapper = resultSetMapperFactory.CreateResultSetMapper(targetClass);
                }
                var instance = mapper.MapResultSet(resultSet, targetClass);
                list.add(instance);
            }
            return list;
        });
    }

    public <E> Stream<E> stream(Class<E> targetClass) throws SQLException {
        var connection = this.context.getConnection();
        var preparedStatement = this.build(connection);
        var resultSet = preparedStatement.executeQuery();
        var mapper = resultSetMapperFactory.CreateResultSetMapper(targetClass);
        return stream(resultSet, mapper, targetClass).onClose(() -> {
            try {
                preparedStatement.close();
                resultSet.close();
                if (closeConnection && !connection.isClosed()) {
                    connection.close();
                }
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Override
    public <E> Optional<E> first(Class<E> targetClass) throws SQLException {
        return withResultSet((ResultSet resultSet) -> {
            E instance = null;
            if (resultSet.next()) {
                var mapper = resultSetMapperFactory.CreateResultSetMapper(targetClass);
                instance = mapper.MapResultSet(resultSet, targetClass);
            }
            return Optional.ofNullable(instance);
        });
    }

    @Override
    public int execute() throws SQLException {
        try (var connection = this.context.getConnection(); var preparedStatement = this.build(connection);) {
            return preparedStatement.executeUpdate();
        }
    }

    private <E> E withResultSet(ThrowingFunction<ResultSet, E> callback) throws SQLException {
        var connection = this.context.getConnection();
        try (var preparedStatement = this.build(connection);
                var resultSet = preparedStatement.executeQuery();) {
            return callback.apply(resultSet);
        } finally {
            if (closeConnection && !connection.isClosed()) {
                connection.close();
            }
        }
    }

    private <E> Stream<E> stream(ResultSet resultSet, ResultSetMapper mapper, Class<E> targetClass) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<E>(Long.MAX_VALUE, Spliterator.ORDERED) {
            @Override
            public boolean tryAdvance(Consumer<? super E> action) {
                try {
                    if (!resultSet.next()) {
                        return false;
                    }
                    action.accept(mapper.MapResultSet(resultSet, targetClass));
                    return true;

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, false);
    }
}