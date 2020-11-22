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
public class PropertyModel {

    /**
     * プロパティ名
     */
    private String propertyName;

    /**
     * クラスタイプ
     */
    private String propertyType;

    /**
     * アノテーション{@link Column}の情報
     */
    private Column columnAnno;

    /**
     * アノテーション{@link Column}が付与されている要素
     */
    private Element columnAnnoElemenet;

    /**
     * 定義されている親クラス
     */
    private String declaredClassName;

}
