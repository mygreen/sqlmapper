package com.github.mygreen.sqlmapper.core.id;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.github.mygreen.sqlmapper.core.util.NameUtils;

import lombok.NonNull;

/**
 * テーブルを用いてIDの採番を行います。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class TableIdIncrementer extends AllocatableIdGenerator {

    private final JdbcTemplate jdbcTemplate;

    /**
     * テーブル情報
     */
    private final TableIdContext context;

    /**
     * 採番用レコードを抽出するためのSQL
     */
    private String sqlSelect;

    /**
     * 採番用レコードを作成するためのSQL
     */
    private String sqlInsert;

    /**
     * 採番用レコードを更新するためのSQL
     */
    private String sqlUpdate;

    public TableIdIncrementer(@NonNull JdbcTemplate jdbcTemplate, @NonNull TableIdContext context) {
        super(context.getAllocationSize());
        this.jdbcTemplate = jdbcTemplate;
        this.context = context;

        init();
    }

    /**
     * {@link TableIdContext} を元にこのクラスの初期化を行います。
     * <p>実行するSQLの組み立てを行います。</p>
     */
    private void init() {

        Assert.notNull(context, "tableIdContext should not be null.");

        if(context.getAllocationSize() <= 0) {
            throw new IllegalArgumentException("TableIdContext#allocationSize shoule be greater than 1.");
        }

        if(context.getInitialValue() < 0) {
            throw new IllegalArgumentException("TableIdContext#initialValue shoule be greater than 0.");
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

    /**
     * 現在の採番値を取得します。
     * @param name 採番名。
     * @return 採番値。該当する採番名のレコードが存在しない場合は、{@literal null} を返します。
     */
    private Long selectTable(final String name) {

        List<Long> result = jdbcTemplate.queryForList(sqlSelect, Long.class, name);
        if(result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * 新たな採番情報を作成します。
     * @param name 採番名
     * @param value 採番値。
     */
    private void insertTable(final String name, final long value) {
        jdbcTemplate.update(sqlInsert, name, value);
    }

    /**
     * 既存の採番値を更新します。
     * @param name 採番名
     * @param incrementValue 採番値に対する加算する値
     */
    private void updateTable(final String name, final long incrementValue) {
        jdbcTemplate.update(sqlUpdate, incrementValue, name);
    }

}
