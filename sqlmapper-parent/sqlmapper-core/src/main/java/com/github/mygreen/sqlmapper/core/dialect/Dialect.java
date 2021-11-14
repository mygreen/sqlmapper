package com.github.mygreen.sqlmapper.core.dialect;

import java.sql.ResultSet;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.github.mygreen.splate.SqlTemplateEngine;
import com.github.mygreen.sqlmapper.core.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.core.query.SelectForUpdateType;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.where.metamodel.OperationHandler;
import com.github.mygreen.sqlmapper.metamodel.operator.Operator;

/**
 * SQLの方言を定義します。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public interface Dialect {

    /**
     * 方言の名称を取得します。
     * <p>この値は、{@link SqlTemplateEngine#setSuffixName(String)} にも使用されます。</p>
     *
     * @return 方言の名称。
     */
    String getName();

    /**
     * {@link GenerationType#AUTO} が指定された場合の、 デフォルトの {@link GenerationType}を返します。
     * @return {@link GenerationType} を返します。
     */
    GenerationType getDefaultGenerationType();

    /**
     * サポートする主キーの生成戦略を判定します。
     * @param generationType 主キーの生成戦略。
     * @return {@literal true}のときサポートします。
     */
    boolean supportsGenerationType(GenerationType generationType);

    /**
     * シーケンスをインクリメントする処理を取得します。
     * @param dataSource データソース
     * @param sequenceName シーケンス名
     * @return DBに対応したインクリメントする処理の実装を返します。シーケンスをサポートしない場合は{@literal null}を返します。
     */
    DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName);

    /**
     * 対応するクラスタイプに対する値タイプを返します。
     * Oracleなどのようにbooleanが存在しない場合は対応する{@link ValueType} に切り替えたりします。
     *
     * @param valueType 値タイプ
     * @return 値タイプ
     */
    ValueType<?> getValueType(ValueType<?> valueType);

    /**
     * 件数取得用のSQLのSELECT句を取得します。
     * @return SELECT句
     */
    String getCountSql();

    /**
     * ヒントコメントを返します。
     * <p>ヒント句をサポートしていないDBの場合は空文字を返します。</p>
     *
     * @param hint ヒント
     * @return ヒントコメント
     */
    String getHintComment(String hint);

    /**
     * LIMIT用SQLに変換します。
     * @param sql SQL
     * @param offset オフセット。省略する場合は {@literal -1}を指定します。
     * @param limit リミット。省略する場合は {@literal -1} を指定します。
     * @return LIMIT用SQL。
     */
    String convertLimitSql(String sql, int offset, int limit);

    /**
     * SELECT文で<code>FOR UPDATE</code>をサポートしていれば<code>true</code>を返します。
     *
     * @param type SELECT ～ FOR UPDATEのタイプ
     * @return SELECT文で<code>FOR UPDATE</code>をサポートしていれば<code>true</code>
     */
    boolean supportsSelectForUpdate(SelectForUpdateType type);

    /**
     * SELECT文に付加する<code>FOR UPDATE NOWAIT</code>相当のSQLを返します。
     *
     * @param type SELECT ～ FOR UPDATEのタイプ
     * @param waitSeconds <code>type</code>に{@link SelectForUpdateType#WAIT} が指定された場合の待機時間(秒単位)
     * @return SELECT文に付加する<code>FOR UPDATE</code>句のSQL
     */
    String getForUpdateSql(SelectForUpdateType type, int waitSeconds);

    /**
     * プロシージャの呼び出しで {@link ResultSet} に対してパラメータが必要かどうかを判定します。
     * @return {@literal true}のとき、{@link ResultSet} に対してパラメータが必要です。
     */
    boolean needsParameterForResultSet();

    /**
     * メタモデルの条件式を評価する処理のマップを返します。
     * <p>デフォルトの実装から変更するものみ設定します。
     *
     * @since 0.3
     * @return メタモデルの条件式を評価する処理のマップ
     */
    Map<Class<?>, OperationHandler<? extends Operator>> getOperationHandlerMap();
}
