package com.github.mygreen.sqlmapper.metamodel;

/**
 * {@link PathMeta}に関するユーティリティ。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class PathMetaUtils {

    /**
     * 指定したプロパティのメタ情報を作成します。
     *
     * @param parent 親（エンティティ）のパス情報
     * @param propertyName プロパティ情報
     * @return プロパティのメタ情報。
     */
    public static PathMeta forProperty(Path<?> parent, String propertyName) {
        return new PathMeta(parent, propertyName, PathType.PROPERTY);
    }
}
