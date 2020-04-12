package com.github.mygreen.sqlmapper.id;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import com.github.mygreen.sqlmapper.util.NameUtils;

/**
 * テーブルを使って採番する処理。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class TableIdIncrementer extends AllocatableIdGenerator {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final TableIdContext context;

    private String sqlSelect;

    private String sqlInsert;

    private String sqlUpdate;

    public TableIdIncrementer(NamedParameterJdbcTemplate namedParameterJdbcTemplate, TableIdContext context) {
        super(context.getAllocationSize());
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.context = context;

        init();
    }

    protected void init() {

        Assert.notNull(context, "tableIdContext should not be null.");

        if(context.getAllocationSize() <= 0) {
            throw new IllegalArgumentException("TableIdContext#allocationSie shoule be greater than 1.");
        }

        final String tableName = NameUtils.tableFullName(context.getTable(), context.getCatalog(), context.getSchema());
        this.sqlSelect = new StringBuilder(100)
                    .append("SELECT ")
                    .append(context.getValueColumn())
                    .append(" FROM ")
                    .append(tableName)
                    .append(" WHERE ")
                    .append(context.getPkColumn())
                    .append(" = :name")
                    .append(" FOR UPDATE")
                    .toString();

        this.sqlInsert = new StringBuilder(100)
                .append("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(context.getPkColumn())
                .append(", ")
                .append(context.getValueColumn())
                .append(" ) VALUES (:name, :value)")
                .toString();

        this.sqlUpdate = new StringBuilder(100)
                .append("UPDATE ")
                .append(tableName)
                .append(" SET ")
                .append(context.getValueColumn())
                .append(" = ")
                .append(context.getValueColumn())
                .append(" + :value WHERE ")
                .append(context.getPkColumn())
                .append(" = :name")
                .toString();
    }

    @Override
    protected long getCurrentValue(String sequenceName) {

        Long currentValue = selectTable(sequenceName);
        if(currentValue == null) {
            insertTable(sequenceName, context.getInitialValue());
            currentValue = context.getInitialValue();
        }

        return currentValue;
    }

    @Override
    protected long allocateValue(final String sequenceName, final long allocationSize) {

        updateTable(sequenceName, allocationSize);
        return selectTable(sequenceName);

    }

    private Long selectTable(final String name) {

        List<Long> result = namedParameterJdbcTemplate.queryForList(sqlSelect, Map.of("name", name), Long.class);
        if(result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    private void insertTable(final String name, final long value) {
        namedParameterJdbcTemplate.update(sqlInsert, Map.of("name", name, "value", value));
    }

    private void updateTable(final String name, final long incrementValue) {
        namedParameterJdbcTemplate.update(sqlUpdate, Map.of("name", name, "value", incrementValue));
    }

}
