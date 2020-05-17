package com.github.mygreen.sqlmapper.query.auto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;

import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.id.IdGenerator;
import com.github.mygreen.sqlmapper.id.IdentityIdGenerator;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.meta.PropertyValueInvoker;
import com.github.mygreen.sqlmapper.query.QueryExecutorBase;
import com.github.mygreen.sqlmapper.type.ValueType;
import com.github.mygreen.sqlmapper.util.NumberConvertUtils;
import com.github.mygreen.sqlmapper.util.QueryUtils;

/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class AutoBatchInsertExecutor extends QueryExecutorBase {

    /**
     * バージョンプロパティの初期値
     */
    public static final long INITIAL_VERSION = AutoInsertExecutor.INITIAL_VERSION;

    private final AutoBatchInsert<?> query;

    /**
     * クエリのパラメータ - エンティティごとの設定
     */
    private MapSqlParameterSource[] batchParams;

    /**
     * 挿入するカラム名
     */
    private List<String> usingColumnNames = new ArrayList<>();

    /**
     * IDENTITYによる主キーの自動生成を使用するカラム名
     */
    private List<String> usingIdentityKeyColumnNames = new ArrayList<>();

    /**
     * レコードの挿入操作を行う処理
     */
    private SimpleJdbcInsert insertOperation;

    /**
     * シーケンスやテーブルによる主キーの生成したキー
     * <p>key=カラム名、value=全レコード分の生成したキーの値</p>
     */
    private Map<String, Object[]> generatedKeysMap = new HashMap<String, Object[]>();

    public AutoBatchInsertExecutor(AutoBatchInsert<?> query) {
        super(query.getContext());
        this.query = query;
    }

    @Override
    public void prepare() {

        this.batchParams = new MapSqlParameterSource[query.getEntitySize()];

        prepareSqlParam();
        prepareInsertOperation();

        completed();

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void prepareSqlParam() {

        final int dataSize = query.getEntitySize();

        for(PropertyMeta propertyMeta : query.getEntityMeta().getAllColumnPropertyMeta()) {

            final String propertyName = propertyMeta.getName();
            if(!propertyMeta.getColumnMeta().isInsertable()) {
                continue;
            }

            if(query.getExcludesProperties().contains(propertyName)) {
                continue;
            }

            if(!query.getIncludesProperties().isEmpty() && !query.getIncludesProperties().contains(propertyName)) {
                continue;
            }

            final String columnName = propertyMeta.getColumnMeta().getName();

            // 各レコードのパラメータを作成する。
            for(int i=0; i < dataSize; i++) {
                final MapSqlParameterSource paramSource = QueryUtils.get(batchParams, i);
                Object propertyValue = PropertyValueInvoker.getPropertyValue(propertyMeta, query.getEntity(i));

                Optional<GenerationType> generationType = propertyMeta.getIdGenerationType();
                if(propertyMeta.isId() && generationType.isPresent()) {
                    if(generationType.get() == GenerationType.IDENTITY) {
                        if(i == 0) {
                            //IDENTITYの場合は、クエリ実行後に取得するため、対象のカラム情報を１回だけ
                            usingIdentityKeyColumnNames.add(columnName);
                        }
                        continue;
                    } else {
                        propertyValue = getNextVal(propertyMeta.getIdGenerator().get(), propertyMeta.getColumnMeta().getName(), i);
                        PropertyValueInvoker.setPropertyValue(propertyMeta, query.getEntity(i), propertyValue);
                    }
                }

                if(propertyValue == null && propertyMeta.isVersion()) {
                    // バージョンキーが設定されていない場合、初期値設定する
                    propertyValue = NumberConvertUtils.convertNumber(propertyMeta.getPropertyType(), INITIAL_VERSION);
                    PropertyValueInvoker.setPropertyValue(propertyMeta, query.getEntity(i), propertyValue);

                }

                // クエリのパラメータの組み立て
                ValueType valueType = propertyMeta.getValueType();
                paramSource.addValue(columnName, valueType.getSqlParameterValue(propertyValue));

            }

            // IDENTITYの主キーでない場合は通常カラムとして追加
            if(!usingIdentityKeyColumnNames.contains(columnName)) {
                usingColumnNames.add(columnName);
            }

        }
    }

    /**
     * 主キーを生成する
     * @param generator 主キーの生成処理
     * @param columnName 生成対象のカラム名
     * @param index 生成対象のレコードのインデックス
     * @return 生成した主キーの値
     */
    private Object getNextVal(final IdGenerator generator, final String columnName, final int index) {

        // 1レコードの主キーをまとめてキーを生成しておき、キャッシュしておく。
        Object[] generatedKeys = generatedKeysMap.computeIfAbsent(columnName, v ->
                context.getRequiresNewTransactionTemplate().execute(action -> {
                    return generator.generateValues(query.getEntities().length);
                }));

         return generatedKeys[index];
    }

    /**
     * SQLの実行をする処理を組み立てます
     */
    private void prepareInsertOperation() {

        this.insertOperation = new SimpleJdbcInsert(context.getJdbcTemplate())
                .withTableName(query.getEntityMeta().getTableMeta().getFullName())
                .usingColumns(QueryUtils.toArray(usingColumnNames));

        if(!usingIdentityKeyColumnNames.isEmpty()) {
            insertOperation.usingGeneratedKeyColumns(QueryUtils.toArray(usingIdentityKeyColumnNames));
        }
    }

    public int[] execute() {

        assertNotCompleted("executeBatchInsert");

        if(this.usingIdentityKeyColumnNames.isEmpty()) {
            // 主キーがIDENTITYによる生成でない場合
            return insertOperation.executeBatch(batchParams);

        } else {
            // １件ずつ処理する
            int dataSize = query.getEntities().length;
            int[] res = new int[dataSize];
            for(int i=0; i < dataSize; i++) {

                final KeyHolder keyHolder = insertOperation.executeAndReturnKeyHolder(batchParams[i]);
                // 生成した主キーをエンティティに設定する
                for(Map.Entry<String, Object> entry : keyHolder.getKeys().entrySet()) {

                    if(!usingIdentityKeyColumnNames.contains(entry.getKey())) {
                        continue;
                    }

                    PropertyMeta propertyMeta = query.getEntityMeta().getColumnPropertyMeta(entry.getKey()).orElseThrow();
                    IdentityIdGenerator idGenerator = (IdentityIdGenerator) propertyMeta.getIdGenerator().get();
                    Object propertyValue = idGenerator.generateValue((Number)entry.getValue());
                    PropertyValueInvoker.setPropertyValue(propertyMeta, query.getEntity(i), propertyValue);

                }

                res[i] = 1;

            }

            return res;
        }
    }
}
