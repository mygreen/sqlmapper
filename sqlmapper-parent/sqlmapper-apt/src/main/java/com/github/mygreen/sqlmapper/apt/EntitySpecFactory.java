package com.github.mygreen.sqlmapper.apt;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Generated;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;

import com.github.mygreen.sqlmapper.apt.model.AptType;
import com.github.mygreen.sqlmapper.apt.model.EntityMetamodel;
import com.github.mygreen.sqlmapper.apt.model.PropertyMetamodel;
import com.github.mygreen.sqlmapper.core.util.NameUtils;
import com.github.mygreen.sqlmapper.metamodel.BooleanPath;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.EnumPath;
import com.github.mygreen.sqlmapper.metamodel.GeneralPath;
import com.github.mygreen.sqlmapper.metamodel.LocalDatePath;
import com.github.mygreen.sqlmapper.metamodel.LocalDateTimePath;
import com.github.mygreen.sqlmapper.metamodel.LocalTimePath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.SqlDatePath;
import com.github.mygreen.sqlmapper.metamodel.SqlTimePath;
import com.github.mygreen.sqlmapper.metamodel.SqlTimestampPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;
import com.github.mygreen.sqlmapper.metamodel.UtilDatePath;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;

import lombok.RequiredArgsConstructor;

/**
 * エンティティ用のソース生成用の{@link TypeSpec}を作成します。
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class EntitySpecFactory {

    /**
     * APT用のメッセージ出力
     */
    private final Messager messager;

    /**
     * メタモデルの生成オプション
     */
    private final MetamodelConfig metamodelConfig;

    public TypeSpec create(final EntityMetamodel entityModel) {

        // フィールド情報の作成
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        for(PropertyMetamodel propertyModel : entityModel.getProperties()) {
            fieldSpecs.add(createFieldSpec(propertyModel));
        }

        if(entityModel.isEmbeddable()) {
            return createTypeSpecAsEmbeddeable(entityModel, fieldSpecs);
        } else if(entityModel.getType().isAbstract()) {
            return createTypeSpecAsAbstract(entityModel, fieldSpecs);
        } else {
            return createTypeSpecAsNormal(entityModel, fieldSpecs);
        }


    }

    private FieldSpec createFieldSpec(final PropertyMetamodel propertyModel) {

        final AptType propertyType = propertyModel.getPropertyType();

        FieldSpec filedSpec;
        if(propertyModel.isEmbedded()) {
            // 埋め込み用のプロパティの場合
            // public final MPK id = new MPK(this, "id")
            String embeddedEntityMetamodelName = resolveEntityMetamodelName(propertyType.getSimpleName());
            ClassName embeddedEntityClassName = ClassName.bestGuess(embeddedEntityMetamodelName);
            filedSpec = FieldSpec.builder(embeddedEntityClassName, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("new $T(this, $S)", embeddedEntityClassName, propertyModel.getPropertyName())
                    .build();

        } else if(propertyType.isInheritanceOf(String.class)) {
            filedSpec = FieldSpec.builder(StringPath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createString($S)", propertyModel.getPropertyName())
                    .build();

        } else if (propertyType.isInheritanceOf(Number.class) || propertyType.isPrimitiveNumber()) {
            TypeName filedTypeName = ParameterizedTypeName.get(ClassName.get(NumberPath.class), propertyType.getWrapperTypeName());
            filedSpec = FieldSpec.builder(filedTypeName, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createNumber($S, $T.class)", propertyModel.getPropertyName(), propertyType.getWrapperTypeName())
                    .build();

        } else if (propertyType.isInheritanceOf(Boolean.class) || propertyType.isPrimitiveBoolean()) {
            filedSpec = FieldSpec.builder(BooleanPath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createBoolean($S)", propertyModel.getPropertyName())
                    .build();

        } else if(propertyType.isEnum()) {
            TypeName filedTypeName = ParameterizedTypeName.get(ClassName.get(EnumPath.class), propertyType.getTypeName());
            filedSpec = FieldSpec.builder(filedTypeName, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createEnum($S, $T.class)", propertyModel.getPropertyName(), propertyType.getTypeName())
                    .build();

        } else if(propertyType.isInheritanceOf(Timestamp.class)) {
            filedSpec = FieldSpec.builder(SqlTimestampPath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createSqlTimestamp($S)", propertyModel.getPropertyName())
                    .build();

        } else if(propertyType.isInheritanceOf(Time.class)) {
            filedSpec = FieldSpec.builder(SqlTimePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createSqlTime($S)", propertyModel.getPropertyName())
                    .build();

        } else if(propertyType.isInheritanceOf(Date.class)) {
            filedSpec = FieldSpec.builder(SqlDatePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createSqlDate($S)", propertyModel.getPropertyName())
                    .build();

        } else if(propertyType.isInheritanceOf(java.util.Date.class)) {
            filedSpec = FieldSpec.builder(UtilDatePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createUtilDate($S)", propertyModel.getPropertyName())
                    .build();

        } else if(propertyType.isInheritanceOf(LocalDate.class)) {
            filedSpec = FieldSpec.builder(LocalDatePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createLocalDate($S)", propertyModel.getPropertyName())
                    .build();

        } else if(propertyType.isInheritanceOf(LocalTime.class)) {
            filedSpec = FieldSpec.builder(LocalTimePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createLocalTime($S)", propertyModel.getPropertyName())
                    .build();

        } else if(propertyType.isInheritanceOf(LocalDateTime.class)) {
            filedSpec = FieldSpec.builder(LocalDateTimePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createLocalDateTime($S)", propertyModel.getPropertyName())
                    .build();

        } else {
            // 汎用的なGeneralPathにする
            TypeName filedTypeName = ParameterizedTypeName.get(ClassName.get(GeneralPath.class), propertyType.getTypeName());
            filedSpec = FieldSpec.builder(filedTypeName, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createGeneral($S, $T.class)", propertyModel.getPropertyName(), propertyType.getTypeName())
                    .build();
        }

        return filedSpec;

    }

    /**
     * 通常のEntityPathの定義を作成する。
     * @param entityModel エンティティモデル
     * @param fieldSpecs フィールド
     * @return クラス定義情報
     */
    private TypeSpec createTypeSpecAsNormal(final EntityMetamodel entityModel, final List<FieldSpec> fieldSpecs) {
        // エンティティのクラス
        final ClassName entityType = entityModel.getType().asClassName();

        // メタモデルのクラス名
        final String entityMetamodelName = resolveEntityMetamodelName(entityModel.getClassName());

        // 継承タイプ
        final TypeName metamodelSuperClass;
        if(entityModel.hasSuperClass()) {
            /*
             * 継承タイプ - 親が @MappedSuperclass を付与している場合
             * extends MParent<Sample>
             */
            ClassName superClass = entityModel.getSuperClassType().asClassName();
            String metamodelSuperClassName =
                    superClass.packageName()
                    + AptUtils.getPackageClassNameSeparator(entityModel)
                    + resolveEntityMetamodelName(superClass.simpleName());

            metamodelSuperClass = ParameterizedTypeName.get(ClassName.bestGuess(metamodelSuperClassName), TypeVariableName.get(entityType.simpleName()));
        } else {
            /*
             * 継承タイプ - 通常
             * extends EntityPatBase<Sample>
             */
            metamodelSuperClass = ParameterizedTypeName.get(ClassName.get(EntityPathBase.class), entityType);
        }

        /*
         * コンストラクタ
         * public MSample(Class<Sample> type, String name) {
         *    super(type, name);
         * }
         *
         */
        MethodSpec consturctor1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), entityType), "type")
                .addParameter(String.class, "name")
                .addStatement("super(type, name)")
                .build();

        /*
         * コンストラクタ
         * public MSample(String name) {
         *    super(Sample.class, name);
         * }
         *
         */
        MethodSpec consturctor2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "name")
                .addStatement("super($T.class, name)", entityType)
                .build();

        /*
         * エンティティの自身のインスタンスフィールド
         * public static final MSample sample = new MSample("sample");
         */
        ClassName entityClassName = ClassName.bestGuess(entityMetamodelName);
        final String entityFieldName = NameUtils.uncapitalize(entityModel.getClassName());
        FieldSpec entityFieldSpec = FieldSpec.builder(entityClassName, entityFieldName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $L($S)", entityMetamodelName, entityFieldName)
                .build();

        return TypeSpec.classBuilder(entityMetamodelName)
              .addModifiers(resolveEntityTypeModifiers(entityModel))
              .superclass(metamodelSuperClass)
              .addAnnotation(createGeneratorAnnoSpec())
              .addJavadoc("$L is SqlMapper's metamodel type for {@link $T}", entityMetamodelName, entityType)
              .addMethod(consturctor1)
              .addMethod(consturctor2)
              .addField(entityFieldSpec)
              .addFields(fieldSpecs)
              .addTypes(createStaticInnerTypeSpecs(entityModel))
              .build();

    }

    /**
     * 抽象クラスの場合の定義を作成する。
     * @param entityModel エンティティモデル
     * @param fieldSpecs フィールド
     * @return クラス定義情報
     */
    private TypeSpec createTypeSpecAsAbstract(final EntityMetamodel entityModel, final List<FieldSpec> fieldSpecs) {

        // エンティティのクラス名情報
        final ClassName entityType = entityModel.getType().asClassName();

        // メタモデルのクラス名
        final String entityMetamodelName = resolveEntityMetamodelName(entityModel.getClassName());

        // 継承タイプ
        final TypeName metamodelSuperClass;
        final TypeVariableName classTypeVariable;
        if(entityModel.hasSuperClass()) {
            /*
             * 継承タイプ - 親が @MappedSuperclass を付与している場合
             * MSample<E extends Sample> extends MParent<E>
             */
            ClassName superClass = entityModel.getSuperClassType().asClassName();
            String metamodelSuperClassName =
                    superClass.packageName()
                    + AptUtils.getPackageClassNameSeparator(entityModel)
                    + resolveEntityMetamodelName(superClass.simpleName());

           metamodelSuperClass = ParameterizedTypeName.get(ClassName.bestGuess(metamodelSuperClassName), TypeVariableName.get("E"));
           classTypeVariable = TypeVariableName.get("E", entityType);

        } else {
            /*
             * 継承タイプ - 自身が抽象クラス
             * MSample<E extends Sample> extends EntityPatBase<E>
             */
            metamodelSuperClass = ParameterizedTypeName.get(ClassName.get(EntityPathBase.class), TypeVariableName.get("E"));
            classTypeVariable = TypeVariableName.get("E", entityType);
        }


        /*
         * コンストラクタ
         * public MSample(Class<? extends E> type, String name) {
         *    super(type, name);
         * }
         */
        MethodSpec consturctor1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(TypeVariableName.get("E"))), "type")
                .addParameter(String.class, "name")
                .addStatement("super(type, name)")
                .build();

        return TypeSpec.classBuilder(entityMetamodelName)
              .addModifiers(resolveEntityTypeModifiers(entityModel))
              .superclass(metamodelSuperClass)
              .addTypeVariable(classTypeVariable)
              .addAnnotation(createGeneratorAnnoSpec())
              .addJavadoc("$L is SqlMapper's metamodel type for {@link $T}", entityMetamodelName, entityType)
              .addMethod(consturctor1)
              .addFields(fieldSpecs)
              .addTypes(createStaticInnerTypeSpecs(entityModel))
              .build();

    }

    /**
     * 埋め込み用のEntityPathの定義を作成する。
     * @param entityModel エンティティモデル
     * @param fieldSpecs フィールド
     * @return クラス定義情報
     */
    private TypeSpec createTypeSpecAsEmbeddeable(final EntityMetamodel entityModel, final List<FieldSpec> fieldSpecs) {
        // エンティティのクラスタイプ
        final ClassName entityType = entityModel.getType().asClassName();

        // メタモデルのクラス名
        final String entityMetamodelName = resolveEntityMetamodelName(entityModel.getClassName());

        // 継承タイプ
        final TypeName metamodelSuperClass;
        if(entityModel.hasSuperClass()) {
            /*
             * 継承タイプ - 親が @MappedSuperclass を付与している場合
             * extends MParent<Sample>
             */
            ClassName superClass = entityModel.getSuperClassType().asClassName();
            String metamodelSuperClassName =
                    superClass.packageName()
                    + AptUtils.getPackageClassNameSeparator(entityModel)
                    + resolveEntityMetamodelName(superClass.simpleName());

            metamodelSuperClass = ParameterizedTypeName.get(ClassName.bestGuess(metamodelSuperClassName), TypeVariableName.get(entityType.simpleName()));
        } else {
            /*
             * 継承タイプ - 通常
             * extends EntityPatBase<Sample>
             */
            metamodelSuperClass = ParameterizedTypeName.get(ClassName.get(EntityPathBase.class), entityType);
        }

        /*
         * コンストラクタ
         * public MSample(Class<Sample> type, EntityPathBase<?> parent, String name) {
         *    super(type, parent, name);
         * }
         *
         */
        MethodSpec consturctor1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), entityType), "type")
                .addParameter(ParameterizedTypeName.get(ClassName.get(EntityPathBase.class), WildcardTypeName.subtypeOf(Object.class)), "parent")
                .addParameter(String.class, "name")
                .addStatement("super(type, parent, name)")
                .build();

        /*
         * コンストラクタ
         * public MSample(EntityPathBase<?> parent, String name) {
         *    super(Sample.class, parent, name);
         * }
         *
         */
        MethodSpec consturctor2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(EntityPathBase.class), WildcardTypeName.subtypeOf(Object.class)), "parent")
                .addParameter(String.class, "name")
                .addStatement("super($T.class, parent, name)", entityType)
                .build();

        return TypeSpec.classBuilder(entityMetamodelName)
              .addModifiers(resolveEntityTypeModifiers(entityModel))
              .superclass(metamodelSuperClass)
              .addAnnotation(createGeneratorAnnoSpec())
              .addJavadoc("$L is SqlMapper's metamodel type for {@link $T}", entityMetamodelName, entityType)
              .addMethod(consturctor1)
              .addMethod(consturctor2)
              .addFields(fieldSpecs)
              .addTypes(createStaticInnerTypeSpecs(entityModel))
              .build();

    }

    /**
     * static内部クラスのメタモデルを作成します。
     * @param parentEntityModel 親のメタモデル情報
     * @return static内部のメタモデルクラス
     */
    private List<TypeSpec> createStaticInnerTypeSpecs(EntityMetamodel parentEntityModel) {

        List<TypeSpec> list = new ArrayList<>();

        for(EntityMetamodel staticInnerEntityModel : parentEntityModel.getStaticInnerEntities()) {
            list.add(create(staticInnerEntityModel));
        }

        return list;

    }

    /**
     * エンティティのメタモデルの修飾子を解決します。
     * @param entityModel エンティティのメタモデル。
     * @return 修飾子
     */
    private Modifier[] resolveEntityTypeModifiers(final EntityMetamodel entityModel) {
        List<Modifier> list = new ArrayList<>(1);
        list.add(Modifier.PUBLIC);

        if(entityModel.getType().isAbstract()) {
            list.add(Modifier.ABSTRACT);
        }

        if(entityModel.getType().isStaticInnerClass()) {
            list.add(Modifier.STATIC);
        }

        return list.toArray(new Modifier[list.size()]);
    }

    /**
     * エンティティのメタモデル名を解決します。
     *
     * @param simpleClassName パッケージ名のついていない単純なクラス名。
     * @return 接頭語 + クラス名 + 接尾語。
     */
    private String resolveEntityMetamodelName(String simpleClassName) {
        return metamodelConfig.getPrefix()
                + simpleClassName
                + metamodelConfig.getSuffix();
    }

    /**
     * クラスに付与する {@literal @Generated} アノテーション
     * @return
     */
    private AnnotationSpec createGeneratorAnnoSpec() {
        return AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", "SqlMapper - EntityMetamodelGenerator")
                .addMember("date", "$S", DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").format(LocalDateTime.now()))
                .build();
    }

}
