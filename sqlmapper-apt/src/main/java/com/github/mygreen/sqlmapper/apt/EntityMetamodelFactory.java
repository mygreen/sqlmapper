package com.github.mygreen.sqlmapper.apt;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.lang.model.element.Element;

import org.springframework.util.ReflectionUtils;

import com.github.mygreen.sqlmapper.apt.model.EntityMetamodel;
import com.github.mygreen.sqlmapper.apt.model.PropertyMetamodel;
import com.github.mygreen.sqlmapper.core.annotation.Embeddable;
import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.MappedSuperclass;
import com.github.mygreen.sqlmapper.core.annotation.Transient;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;

import lombok.RequiredArgsConstructor;

/**
 * クラス情報からメタモデルの情報を作成します。
 * プロパティやクラスの仕様は、{@link EntityMetaFactory}に準拠します。
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class EntityMetamodelFactory {

    private final ClassLoader classLoader;

    /**
     * APTの処理対象のエンティティ情報からメタ情報を抽出する。
     *
     * @param entityElement アノテーション「{@link Entity}/{@link MappedSuperclass}/{@link Embeddable}」が付与されている要素。
     * @return エンティティのモデル情報。
     * @throws ClassNotFoundException エンティティで指定したクラスが存在しない場合
     */
    public EntityMetamodel create(final Element entityElement) throws ClassNotFoundException {

        final EntityMetamodel entityModel = new EntityMetamodel();
        entityModel.setClassName(entityElement.getSimpleName().toString());

        // パッケージ情報の取得
        Element enclosing = entityElement.getEnclosingElement();
        if(enclosing != null) {
            entityModel.setPackageName(enclosing.toString());
        }

        // 自身のクラス情報の取得
        Class<?> entityClass = classLoader.loadClass(entityModel.getFullName());
        entityModel.setEntityClass(entityClass);
        entityModel.setEntityAnno(entityClass.getAnnotation(Entity.class));
        entityModel.setMappedSuperclassAnno(entityClass.getAnnotation(MappedSuperclass.class));
        entityModel.setEmbeddableAnno(entityClass.getAnnotation(Embeddable.class));

        // 親クラスの取得
        doSuperclass(entityModel, entityClass);

        // プロパティ情報の取得
        doPropety(entityModel, entityClass);

        return entityModel;
    }

    /**
     * 親クラスがアノテーション {@link MappedSuperclass} が付与されている場合、情報を付与する。
     * @param entityModel エンティティのモデル情報。
     * @param entityClass 情報作成もとのエンティティクラス。
     */
    private void doSuperclass(final EntityMetamodel entityModel, final Class<?> entityClass) {

        final Class<?> superClass = entityClass.getSuperclass();
        if(superClass.equals(Object.class)) {
            // 継承していない場合
            return;
        }

        if(superClass.getAnnotation(MappedSuperclass.class) != null) {
            entityModel.setSuperClass(superClass);
        }

    }

    /**
     * エンティティタイプを元にエンティティのメタ情報を作成する。
     * @param entityModel エンティティのメタ情報
     * @param entityClass エンティティタイプ
     */
    private void doPropety(final EntityMetamodel entityModel, final Class<?> entityClass) {

        for(Field field : entityClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if(Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }

            ReflectionUtils.makeAccessible(field);

            if(field.getAnnotation(Transient.class) != null) {
                // 永続化対象外は除外
                continue;
            }

            entityModel.add(createPropertyModel(field));

        }

    }

    /**
     * フィールド情報を元にプロパティのメタ情報を作成する。
     * @param field フィールド
     * @return プロパティのメタ情報
     */
    private PropertyMetamodel createPropertyModel(final Field field) {

        PropertyMetamodel propertyModel = new PropertyMetamodel();

        propertyModel.setPropertyName(field.getName());
        propertyModel.setPropertyType(field.getType());

        // 埋め込み用かどうか
        propertyModel.setEmbedded(field.getAnnotation(EmbeddedId.class) != null);

        return propertyModel;

    }

}
