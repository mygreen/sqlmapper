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

        if(entityModel.isAbstract()) {
            return createTypeSpecAsAbstract(entityModel, fieldSpecs);
        } else {
            return createTypeSpecAsNormal(entityModel, fieldSpecs);
        }


    }

    private FieldSpec createFieldSpec(final PropertyMetamodel propertyModel) {

        final Class<?> propertyType = propertyModel.getPropertyType();

        FieldSpec filedSpec;
        if(String.class.isAssignableFrom(propertyType)) {
            filedSpec = FieldSpec.builder(StringPath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createString($S)", propertyModel.getPropertyName())
                    .build();

        } else if (Number.class.isAssignableFrom(propertyType) || AptUtils.isPrimitiveNumber(propertyType)) {
            TypeName filedTypeName = ParameterizedTypeName.get(NumberPath.class, AptUtils.getWrapperClass(propertyType));
            filedSpec = FieldSpec.builder(filedTypeName, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createNumber($S, $T.class)", propertyModel.getPropertyName(), AptUtils.getWrapperClass(propertyType))
                    .build();

        } else if (Boolean.class.isAssignableFrom(propertyType) || boolean.class.isAssignableFrom(propertyType)) {
            filedSpec = FieldSpec.builder(BooleanPath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createBoolean($S)", propertyModel.getPropertyName())
                    .build();

        } else if(propertyType.isEnum()) {
            TypeName filedTypeName = ParameterizedTypeName.get(EnumPath.class, propertyType);
            filedSpec = FieldSpec.builder(filedTypeName, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createEnum($S, $T.class)", propertyModel.getPropertyName(), propertyType)
                    .build();

        } else if(Timestamp.class.isAssignableFrom(propertyType)) {
            filedSpec = FieldSpec.builder(SqlTimestampPath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createSqlTimestamp($S)", propertyModel.getPropertyName())
                    .build();

        } else if(Time.class.isAssignableFrom(propertyType)) {
            filedSpec = FieldSpec.builder(SqlTimePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createSqlTime($S)", propertyModel.getPropertyName())
                    .build();

        } else if(Date.class.isAssignableFrom(propertyType)) {
            filedSpec = FieldSpec.builder(SqlDatePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createSqlDate($S)", propertyModel.getPropertyName())
                    .build();

        } else if(java.util.Date.class.isAssignableFrom(propertyType)) {
            filedSpec = FieldSpec.builder(UtilDatePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createUtilDate($S)", propertyModel.getPropertyName())
                    .build();

        } else if(LocalDate.class.isAssignableFrom(propertyType)) {
            filedSpec = FieldSpec.builder(LocalDatePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createLocalDate($S)", propertyModel.getPropertyName())
                    .build();

        } else if(LocalTime.class.isAssignableFrom(propertyType)) {
            filedSpec = FieldSpec.builder(LocalTimePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createLocalTime($S)", propertyModel.getPropertyName())
                    .build();

        } else if(LocalDateTime.class.isAssignableFrom(propertyType)) {
            filedSpec = FieldSpec.builder(LocalDateTimePath.class, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createLocalDateTime($S)", propertyModel.getPropertyName())
                    .build();

        } else {
            // 汎用的なGeneralPathにする
            TypeName filedTypeName = ParameterizedTypeName.get(GeneralPath.class, propertyType);
            filedSpec = FieldSpec.builder(filedTypeName, propertyModel.getPropertyName(), Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("createGeneral($S, $T.class)", propertyModel.getPropertyName(), propertyType)
                    .build();
        }

        return filedSpec;

    }

    /**
     * 通常のEntityPathの定義を作成する。
     * @param entityModel エンティティモデル
     * @param fieldSpecs フィールド
     * @return
     */
    private TypeSpec createTypeSpecAsNormal(final EntityMetamodel entityModel, final List<FieldSpec> fieldSpecs) {
        // エンティティのクラスタイプ
        final Class<?> entityType = AptUtils.getClassByName(entityModel.getFullName());

        // メタモデルのクラス名
        final String entityMetamodelName = getEntityMetamodelName(entityModel.getClassName());

        // 継承タイプ
        final TypeName superClass;
        if(entityModel.getSuperClass() != null) {
            /*
             * 継承タイプ - 親が @MappedSuperclass を付与している場合
             * extends MParent<Sample>
             */
            String superClassName =
                    entityModel.getSuperClass().getPackageName()
                    + "."
                    + getEntityMetamodelName(entityModel.getSuperClass().getSimpleName());

            superClass = ParameterizedTypeName.get(ClassName.bestGuess(superClassName), TypeVariableName.get(entityType));
        } else {
            /*
             * 継承タイプ - 通常
             * extends EntityPatBase<Sample>
             */
            superClass = ParameterizedTypeName.get(EntityPathBase.class, entityType);
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
                .addParameter(ParameterizedTypeName.get(Class.class, entityType), "type")
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
              .addModifiers(Modifier.PUBLIC)
              .superclass(superClass)
              .addAnnotation(createGeneratorAnnoSpec())
              .addJavadoc("$L is SqlMapper's metamodel type for {@link $T}", entityMetamodelName, entityType)
              .addMethod(consturctor1)
              .addMethod(consturctor2)
              .addField(entityFieldSpec)
              .addFields(fieldSpecs)
              .build();

    }

    /**
     * 抽象クラスの場合の定義を作成する。
     * @param entityModel エンティティモデル
     * @param fieldSpecs フィールド
     * @return
     */
    private TypeSpec createTypeSpecAsAbstract(final EntityMetamodel entityModel, final List<FieldSpec> fieldSpecs) {

        // エンティティのクラスタイプ
        final Class<?> entityType = AptUtils.getClassByName(entityModel.getFullName());

        // メタモデルのクラス名
        final String entityMetamodelName = getEntityMetamodelName(entityModel.getClassName());

        // 継承タイプ
        final TypeName superClass;
        final TypeVariableName classTypeVariable;
        if(entityModel.getSuperClass() != null) {
            /*
             * 継承タイプ - 親が @MappedSuperclass を付与している場合
             * MSample<E extends Sample> extends MParent<E>
             */
            String superClassName =
                    entityModel.getSuperClass().getPackageName()
                    + "."
                    + getEntityMetamodelName(entityModel.getSuperClass().getSimpleName());

           superClass = ParameterizedTypeName.get(ClassName.bestGuess(superClassName), TypeVariableName.get("E"));
           classTypeVariable = TypeVariableName.get("E", ClassName.get(entityType));

        } else {
            /*
             * 継承タイプ - 自身が抽象クラス
             * MSample<E extends Sample> extends EntityPatBase<E>
             */
            superClass = ParameterizedTypeName.get(ClassName.get(EntityPathBase.class), TypeVariableName.get("E"));
            classTypeVariable = TypeVariableName.get("E", ClassName.get(entityType));
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
              .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
              .superclass(superClass)
              .addTypeVariable(classTypeVariable)
              .addAnnotation(createGeneratorAnnoSpec())
              .addJavadoc("$L is SqlMapper's metamodel type for {@link $T}", entityMetamodelName, entityType)
              .addMethod(consturctor1)
              .addFields(fieldSpecs)
              .build();

    }


    /**
     * エンティティのメタモデル名を取得する。
     *
     * @param simpleClassName パッケージ名のついていない単純なクラス名。
     * @return 接頭語 + クラス名 + 接尾語。
     */
    private String getEntityMetamodelName(String simpleClassName) {
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
