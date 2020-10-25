package com.github.mygreen.sqlmapper.metamodel;

public class PathMetaUtils {

    public static PathMeta forProperty(Path<?> parent, String propertyName) {
        return new PathMeta(parent, propertyName, PathType.PROPERTY);
    }
}
