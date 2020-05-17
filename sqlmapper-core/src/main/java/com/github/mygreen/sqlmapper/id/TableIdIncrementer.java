package com.github.mygreen.sqlmapper.id;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
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

    private final JdbcTemplate jdbcTemplate;

    private final TableIdContext context;

    private String sqlSelect;

    private String sqlInsert;

    private String sqlUpdate;

    public TableIdIncrementer(JdbcTemplate jdbcTemplate, TableIdContext context) {
        super(context.getAllocationSize());
        this.jdbcTemplate = jdbcTemplate;
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
                    .append(" = ?")
                    .append(" FOR UPDATE")
                    .toString();

        this.sqlInsert = new StringBuilder(100)
                .append("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(context.getPkColumn())
                .append(", ")
                .append(context.getValueColumn())
                .append(" ) VALUES (?, ?)")
                .toString();

        this.sqlUpdate = new StringBuilder(100)
                .append("UPDATE ")
                .append(tableName)
                .append(" SET ")
                .append(context.getValueColumn())
                .append(" = ")
                .append(context.getValueColumn())
                .append(" + ? WHERE ")
                .append(context.getPkColumn())
                .append(" = ?")
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

        List<Long> result = jdbcTemplate.queryForList(sqlSelect, Long.class, name);
        if(result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    private void insertTable(final String name, final long value) {
        jdbcTemplate.update(sqlInsert, name, value);
    }

    private void updateTable(final String name, final long incrementValue) {
        jdbcTemplate.update(sqlUpdate, name, incrementValue);
    }

}
