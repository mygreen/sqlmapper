package com.github.mygreen.sqlmapper.metamodel;


/**
 * パスのタイプ
 *
 *
 * @author T.TSUCHIE
 *
 */
public enum PathType {

    /**
     * 親を持つパス（プロパティ）
     */
    PROPERTY,
    /**
     * 親を持たないルートのパス
     */
    ROOT
    ;
}
