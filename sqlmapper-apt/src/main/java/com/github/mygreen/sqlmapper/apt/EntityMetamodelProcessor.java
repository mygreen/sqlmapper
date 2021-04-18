package com.github.mygreen.sqlmapper.apt;

import java.io.IOException;
import java.util.ArrayList;
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

        // アノテーションからエンティティ情報を組み立てます
        List<EntityMetamodel> entityModeles = new ArrayList<>();
        processEntityAnno(roundEnv, entityModeles);

        // ソースの生成
        EntitySpecFactory specFactory = new EntitySpecFactory(messager);
        for(EntityMetamodel entityModel : entityModeles) {
            TypeSpec typeSpec = specFactory.create(entityModel);
            generateEntityMetaModel(typeSpec, entityModel);
        }

        return false;
    }

    private void processEntityAnno(final RoundEnvironment roundEnv, List<EntityMetamodel> entityModeles) {

        EntityMetamodelFactory factory = new EntityMetamodelFactory(this.getClass().getClassLoader());

        for(Element element : roundEnv.getElementsAnnotatedWith(Entity.class)) {
            try {
                entityModeles.add(factory.create(element));
            } catch(ClassNotFoundException e) {
                messager.printMessage(Kind.ERROR, e.getMessage(), element);
            }
        }

        for(Element element : roundEnv.getElementsAnnotatedWith(MappedSuperclass.class)) {
            try {
                entityModeles.add(factory.create(element));
            } catch(ClassNotFoundException e) {
                messager.printMessage(Kind.ERROR, e.getMessage(), element);
            }
        }

    }

    private void generateEntityMetaModel(final TypeSpec typeSpec, final EntityMetamodel entityModel) {
        JavaFile javaFile = JavaFile.builder(entityModel.getPackageName(), typeSpec)
                .indent("    ")
                .build();

        try {
            javaFile.writeTo(filer);
            log.info("generate Metamodel class - {}", entityModel.getFullName());

        } catch (IOException e) {
            messager.printMessage(Kind.ERROR, e.toString());
            e.printStackTrace();
        }
    }

}
