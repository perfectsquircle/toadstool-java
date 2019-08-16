package toadstool;

import java.security.InvalidParameterException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class PreparedStatementBuilder implements StatementBuilder {
    private final Map<String, Object> parameters;
    private String sql;
    private DatabaseContext context;
    private ResultSetMapper resultSetMapper;

    private static final Pattern validParameterName = Pattern.compile("^\\w+$");
    private static final Pattern parameterPattern = Pattern.compile("(@(\\w+))");

    public PreparedStatementBuilder() {
        super();
        this.parameters = new HashMap<String, Object>();
        this.resultSetMapper = new SimpleResultSetMapper();
    }

    public PreparedStatementBuilder(String sql) {
        this();
        this.sql = sql;
    }

    StatementBuilder withContext(DatabaseContext context) {
        this.context = context;
        return this;
    }

    public StatementBuilder withParameter(String parameterName, Object value) {
        if (!validParameterName.matcher(parameterName).matches()) {
            throw new InvalidParameterException("Parameter name is not in allowed format.");
        }
        parameters.put(parameterName, value);
        return this;
    }

    public PreparedStatement build(Connection connection) throws SQLException {
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

    private Pair<String, List<Object>> formatSql() {
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