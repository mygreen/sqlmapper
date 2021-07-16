package com.github.mygreen.sqlmapper.core.id;

import com.github.mygreen.sqlmapper.core.meta.ColumnMeta;
import com.github.mygreen.sqlmapper.core.meta.TableMeta;

import lombok.Data;

/**
 * 生成対象のIDに対する情報。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
@Data
public class IdGenerationContext {

    /**
     * テーブル情報
     */
    private TableMeta tableMeta;

    /**
     * カラム情報
     */
    private ColumnMeta columnMeta;

    /**
     * 生成対象のIDプロパティが所属するエンティティ情報
     */
    private Class<?> entityType;

    /**
     * 生成対象のIDプロパティのクラスタイプ
     */
    private Class<?> propertyType;

}
