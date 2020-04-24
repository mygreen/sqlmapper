package com.github.mygreen.sqlmapper.dialect;

import javax.sql.DataSource;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.github.mygreen.sqlmapper.annotation.GeneratedValue.GenerationType;
import com.github.mygreen.sqlmapper.meta.PropertyMeta;
import com.github.mygreen.sqlmapper.type.ValueType;

/**
 * SQLの方言を処理します。
 *
 *
 * @author T.TSUCHIE
 *
 */
public interface Dialect {

    /**
     * 方言の名称を取得します。
     * @return
     */
    String getName();

    /**
     * {@link GenerationType#AUTO} が指定された場合の、 デフォルトの {@link GenerationType}を返します。
     * @return デフォルトの {@link GenerationType}
     */
    GenerationType getDefaultGenerationType();

    /**
     * Returns true if a specific primary key generation type is supported.
     * @param generationType the pk generation type
     *
     * @return true if generation type is supported, false otherwise
     */
    boolean isSupportedGenerationType(GenerationType generationType);

    /**
     * シーケンスをインクリメントする処理を取得します。
     * @param dataSource データソース
     * @param sequenceName シーケンス名
     * @return
     */
    DataFieldMaxValueIncrementer getSequenceIncrementer(DataSource dataSource, String sequenceName);

    /**
     * 値タイプを返します。
     *
     * @param propertyMeta プロパティのメタデータ
     * @return 値タイプ
     */
    ValueType<?> getValueType(PropertyMeta propertyMeta);

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
}
