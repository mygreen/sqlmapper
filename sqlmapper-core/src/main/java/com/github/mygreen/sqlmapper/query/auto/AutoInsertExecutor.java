package com.github.mygreen.sqlmapper.query.auto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.github.mygreen.sqlmapper.SqlMapperContext;
import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.id.IdGenerator;
import com.github.mygreen.sqlmapper.id.IdentityIdGenerator;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.query.InsertClause;
import com.github.mygreen.sqlmapper.type.ValueType;
import com.github.mygreen.sqlmapper.util.NumberConvertUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutoInsertExecutor {

    /**
     * バージョンプロパティの初期値
     */
    private static final long INITIAL_VERSION = 1L;

    private final AutoInsert<?> query;

    /**
     * INSERTのINTO句とVALUES句
     */
    private InsertClause insertClause = new InsertClause();

    /**
     * クエリのパラメータ
     */
    private MapSqlParameterSource paramSource = new MapSqlParameterSource();

    /**
     * IDENTITYによる主キーの自動生成を使用するカラム名
     */
    private List<String> usingIdentityGeneratedColumnNames = new ArrayList<>();


    /**
     * 実行の準備を行います
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void prepared() {

        for(PropertyMeta propertyMeta : query.entityMeta.getAllColumnPropertyMeta()) {

            final String propertyName = propertyMeta.getName();
            if(!propertyMeta.getColumnMeta().isInsertable()) {
                continue;
            }

            if(query.excludesProperties.contains(propertyName)) {
                continue;
            }

            if(!query.includesProperties.isEmpty() && !query.includesProperties.contains(propertyName)) {
                continue;
            }

            final String paramName = "_" + propertyName;

            // IN句の組み立て
            this.insertClause.addSql(propertyMeta.getColumnMeta().getName(), ":" + paramName);

            Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.entity);

            Optional<GenerationType> generationType = propertyMeta.getIdGenerationType();
            if(propertyMeta.isId() && generationType.isPresent()) {
                if(generationType.get() == GenerationType.IDENTITY) {
                    //IDENTITYの場合は、クエリ実行後に取得するため、対象のカラム情報を一時保存しておく。
                    usingIdentityGeneratedColumnNames.add(propertyMeta.getColumnMeta().getName());
                    propertyValue = null;
                } else {
                    propertyValue = getNextVal(propertyMeta.getIdGenerator().get());
                    PropertyValueInvoker.setPropertyValue(propertyMeta, query.entity, propertyValue);
                }
            }

            if(propertyValue == null && propertyMeta.isVersion()) {
                // バージョンキーが設定されていない場合、初期値設定する
                propertyValue = NumberConvertUtils.convertNumber(propertyMeta.getPropertyType(), INITIAL_VERSION);
                PropertyValueInvoker.setPropertyValue(propertyMeta, query.entity, propertyValue);

            }

            // クエリのパラメータの組み立て
            ValueType valueType = query.getContext().getDialect().getValueType(propertyMeta);
            valueType.bindValue(propertyValue, paramSource, paramName);

        }

    }

    /**
     * 主キーを生成する
     * @param generator 主キーの生成処理
     * @return 生成した主キーの値
     */
    private Object getNextVal(final IdGenerator generator) {

        // トランザクションは別にする。
        return query.getContext().getRequiresNewTransactionTemplate().execute(action -> {
            return generator.generateValue();
        });
    }

    /**
     * 挿入の実行
     * @return 更新した行数
     */
    public int insert() {

        final SqlMapperContext context = query.getContext();

        final String sql = "INSERT INTO "
                + query.entityMeta.getTableMeta().getFullName()
                + insertClause.toIntoSql()
                + insertClause.toValuesSql();

        final int rows;
        if(this.usingIdentityGeneratedColumnNames.isEmpty()) {
            rows = context.getNamedParameterJdbcTemplate().update(sql, this.paramSource);

        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            rows = context.getNamedParameterJdbcTemplate().update(sql, paramSource, keyHolder,
                    usingIdentityGeneratedColumnNames.toArray(new String[usingIdentityGeneratedColumnNames.size()]));

            // 生成した主キーをエンティティに設定する
            for(Map.Entry<String, Object> entry : keyHolder.getKeys().entrySet()) {

                if(!usingIdentityGeneratedColumnNames.contains(entry.getKey())) {
                    continue;
                }

                PropertyMeta propertyMeta = query.entityMeta.getColumnPropertyMeta(entry.getKey())
                            .orElseThrow();
                IdentityIdGenerator idGenerator = (IdentityIdGenerator) propertyMeta.getIdGenerator().get();
                Object propertyValue = idGenerator.generateValue((Number)entry.getValue());
                PropertyValueInvoker.setPropertyValue(propertyMeta, query.entity, propertyValue);

            }
        }

        return rows;

    }
}
