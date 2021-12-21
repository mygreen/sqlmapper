package com.github.mygreen.sqlmapper.apt.model;

import javax.lang.model.element.Element;

import com.github.mygreen.sqlmapper.core.annotation.Column;

import lombok.Data;

/**
 * APTによる処理対象のプロパティ情報。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
public class PropertyMetamodel {

    /**
     * プロパティ名
     */
    private String propertyName;

    /**
     * プロパティのタイプ情報
     */
    private AptType propertyType;

    /**
     * アノテーション{@link Column}の情報
     */
    private Column columnAnno;

    /**
     * アノテーション{@link Column}が付与されている要素
     */
    private Element columnAnnoElemenet;

    /**
     * 埋め込み用かどうか。
     */
    private boolean embedded;

    /**
     * LOB（ラージオブジェクト）かどうか。
     */
    private boolean lob;

    /**
     * 独自のConverterが適用される場合
     */
    private boolean customType;

}
