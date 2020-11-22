package com.github.mygreen.sqlmapper.apt;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Generated;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import com.github.mygreen.sqlmapper.apt.model.EntityModel;
import com.github.mygreen.sqlmapper.apt.model.PropertyModel;
import com.github.mygreen.sqlmapper.core.annotation.Column;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.util.NameUtils;
import com.github.mygreen.sqlmapper.metamodel.BooleanPath;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.EnumPath;
import com.github.mygreen.sqlmapper.metamodel.LocalDatePath;
import com.github.mygreen.sqlmapper.metamodel.LocalDateTimePath;
import com.github.mygreen.sqlmapper.metamodel.LocalTimePath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.SqlDatePath;
import com.github.mygreen.sqlmapper.metamodel.SqlTimePath;
import com.github.mygreen.sqlmapper.metamodel.SqlTimestampPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import lombok.extern.slf4j.Slf4j;

/**
 * エンティティのメタモデルクラスを生成するアノテーションプロセッサ。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
@SupportedAnnotationTypes({"com.github.mygreen.sqlmapper.core.annotation.*"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class EntityMetamodelProcessor extends AbstractProcessor {

    private Filer filer;

    private Messager messager;

//    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
//        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        messager.printMessage(Kind.NOTE, "Running ");
        log.info("Running {}", getClass().getSimpleName());

        if(roundEnv.processingOver() || annotations.isEmpty()) {
            return false;
        }

        if(roundEnv.getRootElements() == null || roundEnv.getRootElements().isEmpty()) {
            log.info("No sources to process.");
            return false;
        }

        // アノテーションから、エンティティ情報を組み立てます。
        Map<String, EntityModel> entityMap = new LinkedHashMap<>();
        processEntityAnno(roundEnv, entityMap);
        processColumnAnno(roundEnv, entityMap);

        // ソースの生成
        for(EntityModel entityModel : entityMap.values()) {
            try {
                generateEntityMetaModel(entityModel);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * {@literal @Entity} 処理し、エンティティ情報を組み立てます。
     *
     * @param roundEnv 環境情報
     * @param entityMap 組み立てたエンティティ情報を保持するマップ
     */
    private void processEntityAnno(final RoundEnvironment roundEnv, final Map<String, EntityModel> entityMap) {

        for(Element element : roundEnv.getElementsAnnotatedWith(Entity.class)) {

            EntityModel entity = new EntityModel();

            entity.setEntityAnno(element.getAnnotation(Entity.class));

            entity.setClassName(element.getSimpleName().toString());

            // パッケージ情報の取得
            Element enclosing = element.getEnclosingElement();
            if(enclosing != null) {
                entity.setPackageName(enclosing.toString());
            }

            entityMap.put(entity.getFullName(), entity);

            log.debug("processEntityAnno - {}", entity.toString());
        }

    }

    /**
     * {@literal @Column} 処理し、プロパティ情報を組み立てます。
     *
     * @param roundEnv 環境情報
     * @param entityMap 組み立てたエンティティ情報を保持するマップ
     */
    private void processColumnAnno(final RoundEnvironment roundEnv, final Map<String, EntityModel> entityMap) {

        for(Element element : roundEnv.getElementsAnnotatedWith(Column.class)) {

            // フィールド以外はスキップ
            if(element.getKind() != ElementKind.FIELD) {
                continue;
            }

            PropertyModel property = new PropertyModel();
            property.setColumnAnno(element.getAnnotation(Column.class));
            property.setColumnAnnoElemenet(element);

            property.setPropertyName(element.getSimpleName().toString());

            // クラスタイプの取得
            TypeMirror type = element.asType();
            property.setPropertyType(type.toString());

            // 親クラス情報の取得
            Element enclosing = element.getEnclosingElement();
            String declaredClassName = enclosing.getEnclosingElement().toString() + "." + enclosing.getSimpleName().toString();
            property.setDeclaredClassName(declaredClassName);

            log.debug("processColumnAnno - {}", property.toString());

            // エンティティに紐づけ
            EntityModel entity = entityMap.get(declaredClassName);
            if(entity == null) {
                messager.printMessage(Kind.ERROR, "フィールドが定義されているクラスに@Entityが付与されていない可能性があります。", element);
                continue;
            } else {
                entity.add(property);
            }

        }

    }

    /**
     * エンティティのメタモデルクラスを生成します。
     *
     * @param entityModel メタモデルの情報
     */
    private void generateEntityMetaModel(final EntityModel entityModel) {

        // プロパティ用のフィールドの定義
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        for(PropertyModel propertyModel : entityModel.getProperties()) {
            Class<?> propertyType = AptUtils.getClassByName(propertyModel.getPropertyType());

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
                        .initializer("createLocaDatelTime($S)", propertyModel.getPropertyName())
                        .build();

            } else {
                messager.printMessage(Kind.ERROR, "サポート対象外のクラスタイプです", propertyModel.getColumnAnnoElemenet());
                throw new RuntimeException("サポート対象外のクラスタイプです。" + propertyType);
            }

            fieldSpecs.add(filedSpec);
        }

        // エンティティのクラスタイプ
        final Class<?> entityType = AptUtils.getClassByName(entityModel.getFullName());

        // メタモデルのクラス名
        final String entityMetamodelName = "M" + entityModel.getClassName();

        // コンストラクタ
        MethodSpec consturctor1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(Class.class, entityType), "type")
                .addParameter(String.class, "name")
                .addStatement("super(type, name)")
                .build();

        MethodSpec consturctor2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "name")
                .addStatement("super($T.class, name)", entityType)
                .build();


        // エンティティのフィールド
        ClassName entityClassName = ClassName.bestGuess(entityMetamodelName);
        final String entityFieldName = NameUtils.uncapitalize(entityModel.getClassName());
        FieldSpec entityFieldSpec = FieldSpec.builder(entityClassName, entityFieldName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $L($S)", entityMetamodelName, entityFieldName)
                .build();

        // 生成用のアノテーション
        AnnotationSpec generatedAnno = AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", "SqlMapper - EntityMetamodelGenerator")
                .addMember("date", "$S", DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").format(LocalDateTime.now()))
                .build();

        // クラスの定義
        TypeSpec typeSpec = TypeSpec.classBuilder(entityMetamodelName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(EntityPathBase.class, entityType))
                .addAnnotation(generatedAnno)
                .addJavadoc("$L is SqlMapper's metamodel type for {@link $T}", entityMetamodelName, entityType)
                .addMethod(consturctor1)
                .addMethod(consturctor2)
                .addField(entityFieldSpec)
                .addFields(fieldSpecs)
                .build();

        JavaFile javaFile = JavaFile.builder(entityModel.getPackageName(), typeSpec)
                .indent("    ")
                .build();

        try {
            javaFile.writeTo(filer);
            log.info("generate Metamodel class - {}", entityClassName.toString());

        } catch (IOException e) {
            messager.printMessage(Kind.ERROR, e.toString());
            e.printStackTrace();
        }

    }

}
