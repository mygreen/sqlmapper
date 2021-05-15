package com.github.mygreen.sqlmapper.apt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.github.mygreen.sqlmapper.apt.model.EntityMetamodel;
import com.github.mygreen.sqlmapper.core.annotation.Embeddable;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.MappedSuperclass;
import com.squareup.javapoet.JavaFile;
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

    /**
     * APT用のファイラー - 文字コード設定などはAPTの定義を採用
     */
    private Filer filer;

    /**
     * APT用のメッセージ出力
     */
    private Messager messager;

    /**
     * APTのオプション設定を扱うクラス。
     */
    private MetamodelConfig metamodelConfig;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        this.metamodelConfig = new MetamodelConfig(processingEnv.getOptions());
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

        messager.printMessage(Kind.NOTE, "Running ");
        log.info("Running {}", getClass().getSimpleName());

        if(roundEnv.processingOver() || annotations.isEmpty()) {
            return false;
        }

        if(roundEnv.getRootElements() == null || roundEnv.getRootElements().isEmpty()) {
            log.info("No sources to process.");
            return false;
        }

        // アノテーションからエンティティ情報を組み立てます
        List<EntityMetamodel> entityModeles = new ArrayList<>();
        processEntityAnno(roundEnv, entityModeles);

        // ソースの生成
        EntitySpecFactory specFactory = new EntitySpecFactory(messager, metamodelConfig);
        for(EntityMetamodel entityModel : entityModeles) {
            TypeSpec typeSpec = specFactory.create(entityModel);
            generateEntityMetaModel(typeSpec, entityModel);
        }

        return false;
    }

    /**
     * エンティティクラスとして処理する。
     * @param roundEnv 環境情報
     * @param entityModeles 今まで作成してきたエンティティのメタモデル情報
     */
    private void processEntityAnno(final RoundEnvironment roundEnv, final List<EntityMetamodel> entityModeles) {

        EntityMetamodelFactory factory = new EntityMetamodelFactory(this.getClass().getClassLoader());

        // @Entityが付与されたクラスの処理
        for(Element element : roundEnv.getElementsAnnotatedWith(Entity.class)) {
            try {
                entityModeles.add(factory.create(element));
            } catch(ClassNotFoundException e) {
                messager.printMessage(Kind.ERROR, e.getMessage(), element);
                log.error("fail entity meta model for @Entity.", e);
            }
        }

        // @MappedSuperclassが付与されたクラスの処理
        for(Element element : roundEnv.getElementsAnnotatedWith(MappedSuperclass.class)) {
            try {
                entityModeles.add(factory.create(element));
            } catch(ClassNotFoundException e) {
                messager.printMessage(Kind.ERROR, e.getMessage(), element);
                log.error("fail entity meta model for @MappedSuperclass.", e);
            }
        }

        // @Embeddableが付与されたクラスの処理
        for(Element element : roundEnv.getElementsAnnotatedWith(Embeddable.class)) {
            try {
                entityModeles.add(factory.create(element));
            } catch(ClassNotFoundException e) {
                messager.printMessage(Kind.ERROR, e.getMessage(), element);
                log.error("fail entity meta model for @Embeddable.", e);
            }
        }

        // 内部クラスのとき、親クラスに付け替える。
        List<EntityMetamodel> innerEntityModeles = new ArrayList<>();
        for(Iterator<EntityMetamodel> itr = entityModeles.iterator(); itr.hasNext(); ) {
            EntityMetamodel entityModel = itr.next();

            // static 内部クラスのモデル情報を抽出する。
            if(entityModel.isStaticInnerClass()) {
                innerEntityModeles.add(entityModel);
                itr.remove();
            }
        }

        for(EntityMetamodel innerModel : innerEntityModeles) {
            // 親のエンティティモデルに関連付けする。
            for(EntityMetamodel parentModel : entityModeles) {
                if(innerModel.getPackageName().equals(parentModel.getFullName())) {
                    // パッケージ名が親のFQNと一致する場合
                    parentModel.add(innerModel);
                }
            }
        }

    }

    /**
     * エンティティのメタモデルクラスのソースを生成します。
     * @param typeSpec 生成するメタモデルのクラス情報
     * @param entityModel 生成するメタモデル情報
     */
    private void generateEntityMetaModel(final TypeSpec typeSpec, final EntityMetamodel entityModel) {
        JavaFile javaFile = JavaFile.builder(entityModel.getPackageName(), typeSpec)
                .indent(metamodelConfig.getIndent())
                .build();

        try {
            javaFile.writeTo(filer);
            log.info("generate Metamodel class - {}", entityModel.getFullName());

        } catch (IOException e) {
            messager.printMessage(Kind.ERROR, e.toString());
            log.error("fail write Metamodel class.", e);
        }
    }

}
