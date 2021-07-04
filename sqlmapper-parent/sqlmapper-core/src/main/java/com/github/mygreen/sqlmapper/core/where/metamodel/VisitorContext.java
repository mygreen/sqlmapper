package com.github.mygreen.sqlmapper.core.where.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 式であるノードを巡回する際に持ち回るコンテキスト。
 * <p>設定値や組み立て中のSQL/パラメータを保持します。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class VisitorContext {

    /**
     * 検索対象となるテーブルのエンティティ情報のマップ
     */
    @Getter
    private final Map<Class<?>, EntityMeta> entityMetaMap;

    /**
     * SQLの方言情報
     */
    @Getter
    private final Dialect dialect;

    /**
     * エンティティのメタ情報を作成する。
     */
    @Getter
    private final EntityMetaFactory entityMetaFactory;

    /**
     * テーブル名のエイリアス管理
     */
    @Getter
    private final TableNameResolver tableNameResolver;

    /**
     * SQL中のパラメータ変数。
     *
     */
    @Getter
    private List<Object> paramValues = new ArrayList<>();

    /**
     * 組み立てたクライテリア
     */
    private StringBuilder criteria = new StringBuilder();

    /**
     * 親の情報を引き継いでインスタンスを作成します。
     * <p>引き継ぐ情報。
     * <ul>
     *  <li>{@link #entityMetaMap}</li>
     *  <li>{@link #dialect}</li>
     *  <li>{@link #tableNameResolver}</li>
     * </ul>
     * @param parent 親のコンテキスト
     */
    public VisitorContext(VisitorContext parent) {
        this.entityMetaMap = parent.entityMetaMap;
        this.dialect = parent.dialect;
        this.entityMetaFactory = parent.entityMetaFactory;
        this.tableNameResolver = parent.tableNameResolver;
    }

    /**
     * SQLのプレースホルダーとして設定する値を追加します。
     * @param value SQLに渡す値。
     */
    public void addParamValue(Object value) {
        this.paramValues.add(value);
    }

    /**
     *  SQLのプレースホルダーとして設定する複数の値を追加します。
     * @param values SQLに渡す値のコレクション。
     */
    public void addParamValues(Collection<?> values) {
        this.paramValues.addAll(values);
    }

    /**
     * SQLを追加します。
     * @param sql SQL
     * @return 現在組み立て中のSQLのバッファー。
     */
    public StringBuilder appendSql(String sql) {
        this.criteria.append(sql);
        return criteria;
    }

    /**
     * 組み立てたSQL(クライテリア)を文字列として取得します。
     * @return 組み立てたSQL
     */
    public String getCriteria() {
        return criteria.toString();
    }

}
