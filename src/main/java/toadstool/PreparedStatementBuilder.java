package toadstool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PreparedStatementBuilder implements StatementBuilder {
    private final Map<String, Object> parameters;
    private final List<Object> indexedParameters;
    private String sql;
    private DatabaseContext context;

    public PreparedStatementBuilder() {
        super();
        this.parameters = new HashMap<String, Object>();
        this.indexedParameters = new ArrayList<>();
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
    public <E> List<E> ToListOf(Class<E> targetClass) throws SQLException {
        var mapper = new SimpleResultSetMapper();
        var connection = this.context.getConnection();
        try (var preparedStatement = this.build(connection); var resultSet = preparedStatement.executeQuery();) {
            var list = new ArrayList<E>();
            var resultSetMetadata = resultSet.getMetaData();
            var mappity = mapper.compileMapper(targetClass, resultSetMetadata);
            while (resultSet.next()) {
                var instance = mappity.apply(resultSet);
                list.add(instance);
            }
            return list;
        }
    }

    @Override
    public <E> E First() throws SQLException {
        return null;
    }

    @Override
    public int Execute() throws SQLException {
        return 0;
    }
}