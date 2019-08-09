package toadstool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PreparedStatementBuilder implements StatementBuilder {
    private Map<String, Object> parameters;
    private List<Object> indexedParameters;
    private String sql;

    public PreparedStatementBuilder() {
        super();
        this.parameters = new HashMap<String, Object>();
        this.indexedParameters = new ArrayList<>();
    }

    public PreparedStatementBuilder(String sql) {
        this();
        this.sql = sql;
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
}