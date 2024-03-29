package com.github.mygreen.sqlmapper.apt.model;

import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.sqlmapper.apt.AptUtils;
import com.github.mygreen.sqlmapper.core.annotation.Embeddable;
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
     * 自身のクラス情報
     */
    private AptType type;

    /**
     * 親クラス(パッケージ名含む)
     */
    private AptType superClassType;

    /**
     * アノテーション{@link Entity}の情報
     */
    private Entity entityAnno;

    /**
     * アノテーション{@link MappedSuperclass}の情報
     */
    private MappedSuperclass mappedSuperclassAnno;

    /**
     * アノテーション{@link Embeddable}の情報
     */
    private Embeddable embeddableAnno;

    /**
     * プロパティ情報
     */
    private List<PropertyMetamodel> properties = new ArrayList<>();

    /**
     * staticな内部クラスのエンティティの場合
     */
    private List<EntityMetamodel> staticInnerEntities = new ArrayList<>();

    /**
     * エンティティのFQNを取得する。
     * @return エンティティのFQN
     */
    public String getFullName() {
        StringBuilder buff = new StringBuilder();

        if(packageName != null) {
            buff.append(packageName)
                .append(AptUtils.getPackageClassNameSeparator(this));
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
     * staticな内部クラスのエンティティ情報を追加する。
     * @param entity エンティティ情報
     */
    public void add(EntityMetamodel entity) {
        if(!entity.getType().isStaticInnerClass()) {
            throw new IllegalArgumentException("entity is not static inner class : " + entity.getFullName());
        }

        this.staticInnerEntities.add(entity);
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

    /**
     * クラスにアノテーション {@link Embeddable} が付与されているかどうか。
     * @return {@literal true}のときアノテーション {@link Embeddable} が付与されている。
     */
    public boolean isEmbeddable() {
        return embeddableAnno != null;
    }

    /**
     * {@link MappedSuperclass}が付与された親クラスを持つかどうか。
     * @return {@literal true}のときき{@link MappedSuperclass}が付与された親クラス
     */
    public boolean hasSuperClass() {
        return superClassType != null;
    }

}
