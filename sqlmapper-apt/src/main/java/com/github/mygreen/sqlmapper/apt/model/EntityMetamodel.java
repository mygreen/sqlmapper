package com.github.mygreen.sqlmapper.apt.model;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.MappedSuperclass;

import lombok.Data;

/**
 * APTによる処理対象のプロパティ情報。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Data
public class EntityMetamodel {

    /**
     * パッケージ名
     */
    private String packageName;

    /**
     * クラス名（パッケージ名除く）
     */
    private String className;

    /**
     * 親クラス
     */
    private Class<?> superClass;

    /**
     * エンティティ自身のクラス
     */
    private Class<?> entityClass;

    /**
     * アノテーション{@link Entity}の情報
     */
    private Entity entityAnno;

    /**
     * アノテーション{@link MappedSuperclass}の情報
     */
    private MappedSuperclass mappedSuperclassAnno;

    /**
     * プロパティ情報
     */
    private List<PropertyMetamodel> properties = new ArrayList<>();

    /**
     * エンティティのFQNを取得する。
     * @return エンティティのFQN
     */
    public String getFullName() {
        StringBuilder buff = new StringBuilder();
        if(packageName != null) {
            buff.append(packageName).append(".");
        }

        buff.append(className);

        return buff.toString();
    }

    /**
     * プロパティ情報を追加する。
     * @param property プロパティ情報
     */
    public void add(PropertyMetamodel property) {
        this.properties.add(property);
    }

    /**
     * エンティティが抽象クラスかどうか。
     * @return {@literal true}のとき抽象クラス。
     */
    public boolean isAbstract() {
        return Modifier.isAbstract(entityClass.getModifiers());
    }

    /**
     * クラスにアノテーション {@link Entity} が付与されているかどうか。
     * @return {@literal true}のときアノテーション {@link Entity} が付与されている。
     */
    public boolean isEntity() {
        return entityAnno != null;
    }

    /**
     * クラスにアノテーション {@link MappedSuperclass} が付与されているかどうか。
     * @return {@literal true}のときアノテーション {@link MappedSuperclass} が付与されている。
     */
    public boolean isMappedSuperclass() {
        return mappedSuperclassAnno != null;
    }

}
