package com.github.mygreen.sqlmapper.apt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import com.github.mygreen.sqlmapper.apt.model.AptType;
import com.github.mygreen.sqlmapper.apt.model.EntityMetamodel;
import com.github.mygreen.sqlmapper.apt.model.PropertyMetamodel;
import com.github.mygreen.sqlmapper.core.annotation.Embeddable;
import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Lob;
import com.github.mygreen.sqlmapper.core.annotation.MappedSuperclass;
import com.github.mygreen.sqlmapper.core.annotation.Transient;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;

import lombok.RequiredArgsConstructor;

/**
 * クラス情報からメタモデルの情報を作成します。
 * プロパティやクラスの仕様は、{@link EntityMetaFactory}に準拠します。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class EntityMetamodelFactory {

    private final Types typeUtils;

    /**
     * APTの処理対象のエンティティ情報からメタ情報を抽出する。
     *
     * @param entityElement アノテーション「{@link Entity}/{@link MappedSuperclass}/{@link Embeddable}」が付与されている要素。
     * @return エンティティのモデル情報。
     * @throws ClassNotFoundException エンティティで指定したクラスが存在しない場合
     */
    public EntityMetamodel create(final TypeElement entityElement) throws ClassNotFoundException {

        final EntityMetamodel entityModel = new EntityMetamodel();
        entityModel.setClassName(entityElement.getSimpleName().toString());

        // パッケージ情報の取得
        Element enclosing = entityElement.getEnclosingElement();
        if(enclosing != null) {
            entityModel.setPackageName(enclosing.toString());
        }
        entityModel.setType(createAptType(entityElement.asType()));

        // 自身のクラス情報の取得
        entityModel.setEntityAnno(entityElement.getAnnotation(Entity.class));
        entityModel.setMappedSuperclassAnno(entityElement.getAnnotation(MappedSuperclass.class));
        entityModel.setEmbeddableAnno(entityElement.getAnnotation(Embeddable.class));

        // 親クラスの取得
        doSuperclass(entityModel, entityElement);

        // プロパティ情報の取得
        doPropety(entityModel, entityElement);

        return entityModel;
    }

    /**
     * 親クラスがアノテーション {@link MappedSuperclass} が付与されている場合、情報を付与する。
     * @param entityModel エンティティのモデル情報。
     * @param entityElement 情報作成元のエンティティ情報。
     */
    private void doSuperclass(final EntityMetamodel entityModel, final TypeElement entityElement) {

        final TypeMirror superclassType = entityElement.getSuperclass();
        if(superclassType.toString().equals(Object.class.getCanonicalName())) {
            // 継承していない場合
            return;
        }

        Element superclassElement = typeUtils.asElement(superclassType);
        if(superclassElement == null || !(superclassElement instanceof TypeElement)) {
            // 親クラスがタイプ情報でない場合
            return;
        }

        TypeElement superTypeElement = (TypeElement) superclassElement;
        if(superTypeElement.getAnnotation(MappedSuperclass.class) != null) {
            entityModel.setSuperClassType(createAptType(superclassType));
        }

    }

    /**
     * エンティティタイプを元にエンティティのメタ情報を作成する。
     * @param entityModel エンティティのメタ情報
     * @param entityElement 情報作成元のエンティティ情報。
     */
    private void doPropety(final EntityMetamodel entityModel, final TypeElement entityElement) {

        for(Element enclosedElement : entityElement.getEnclosedElements()) {
            if(!AptUtils.isInstanceField(enclosedElement)) {
                continue;
            }

            if(enclosedElement.getAnnotation(Transient.class) != null) {
                // 永続化対象外は除外
                continue;
            }

            entityModel.add(createPropertyModel((VariableElement)enclosedElement));
        }

    }

    /**
     * フィールド情報を元にプロパティのメタ情報を作成する。
     * @param fieldElement フィールド
     * @return プロパティのメタ情報
     */
    private PropertyMetamodel createPropertyModel(final VariableElement fieldElement) {

        PropertyMetamodel propertyModel = new PropertyMetamodel();

        propertyModel.setPropertyName(fieldElement.getSimpleName().toString());

        TypeMirror type = fieldElement.asType();
        propertyModel.setPropertyType(createAptType(type));

        // 埋め込み用かどうか
        propertyModel.setEmbedded(fieldElement.getAnnotation(EmbeddedId.class) != null);

        // LOBかどうか
        propertyModel.setLob(fieldElement.getAnnotation(Lob.class) != null);

        return propertyModel;

    }

    /**
     * APT処理用のタイプ情報を作成する。
     * @param typeMirror タイプ情報
     * @return APT処理用のタイプ情報
     */
    private AptType createAptType(final TypeMirror typeMirror) {

        AptType aptType = new AptType(typeMirror, Optional.ofNullable(typeUtils.asElement(typeMirror)));

        // 継承クラスの抽出
        List<TypeMirror> superTypes = new ArrayList<TypeMirror>();
        AptUtils.extractSuperClassTypes(typeMirror, typeUtils, superTypes);
        aptType.setSuperTypes(superTypes);

        return aptType;
    }
}
