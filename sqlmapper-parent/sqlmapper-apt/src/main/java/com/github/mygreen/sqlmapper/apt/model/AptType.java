package com.github.mygreen.sqlmapper.apt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * APTで処理する際のクラスタイプに対するユーティリティクラス。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public class AptType {

    /**
     * APT上のクラスタイプ
     */
    private final TypeMirror typeMirror;

    /**
     * APTのタイプ要素
     */
    private final Optional<Element> typeElement;

    /**
     * 継承しているクラス情報
     */
    @Setter
    private List<TypeMirror> superTypes = new ArrayList<TypeMirror>();

    /**
     * 静的なクラス(static class)か判定します。
     * @return 静的なクラス(static class)のとき{@literal true}を返します。
     */
    public boolean isStaticInnerClass() {

        if(typeElement.isEmpty()) {
            return false;
        }

        final Element element = typeElement.get();

        // クラスかどうか
        if(element.getKind() != ElementKind.CLASS) {
            return false;
        }

        // staticかどうか
        if(!element.getModifiers().contains(Modifier.STATIC)) {
            return false;
        }

        return true;

    }

    /**
     * 抽象クラス(static class)か判定します。
     * @return 抽象クラスのとき{@literal true}を返します。
     */
    public boolean isAbstract() {

        if(typeElement.isEmpty()) {
            return false;
        }

        final Element element = typeElement.get();

        // クラスかどうか
        if(element.getKind() != ElementKind.CLASS) {
            return false;
        }

        // abstractかどうか
        if(!element.getModifiers().contains(Modifier.ABSTRACT)) {
            return false;
        }

        return true;
    }

    /**
     * 配列型かどうか判定します。
     * @return 配列型のとき、{@literal true} を返す。
     */
    public boolean isArray() {
        return (typeMirror instanceof ArrayType);
    }

    /**
     * 列挙型かどうか判定します。
     * @return 列挙型のとき、{@literal true} を返す。
     */
    public boolean isEnum() {

        return typeElement
                .map(t -> t.getKind())
                .map(k -> k == ElementKind.ENUM)
                .orElse(false);
    }

    /**
     * プリミティブ型かどうか判定します。
     * @return プリミティブ型のとき、{@literal true} を返す。
     */
    public boolean isPrimitive() {
        return (typeMirror instanceof PrimitiveType);
    }

    /**
     * プリミティブ型の数値かどうか判定します。
     * @return プリミティブ型の数値とき、{@literal true} を返す。
     */
    public boolean isPrimitiveNumber() {
        if(!isPrimitive()) {
            return false;
        }

        switch(typeMirror.getKind()) {
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return true;
            default:
                return false;
        }
    }

    /**
     * プリミティブ型のbooleanかどうか判定します。
     * @return プリミティブ型のbooleanとき、{@literal true} を返す。
     */
    public boolean isPrimitiveBoolean() {
        if(!isPrimitive()) {
            return false;
        }

        return typeMirror.getKind() == TypeKind.BOOLEAN;
    }

    /**
     * このクラスが指定したクラスを継承しているかどうか判定します。
     * @param targetClass 継承するクラス情報
     * @return 指定したクラスを継承しているとき、{@literal true} を返す。
     */
    public boolean isInheritanceOf(final Class<?> targetClass) {

        final String targetClassName = targetClass.getCanonicalName();

        if(getCanonicalName().equals(targetClassName)) {
            return true;
        }

        for(TypeMirror superType : superTypes) {
            if(superType.toString().equals(targetClassName)) {
                return true;
            }
        }

        return false;

    }

    /**
     * FQNのクラス名を取得します。
     * @return パッケージ名付きのクラス名
     */
    public String getCanonicalName() {
        return typeMirror.toString();
    }

    /**
     * パッケージ名を除いたクラス名を取得します。
     * @return パッケージ名を除いたクラス
     */
    public String getSimpleName() {

        String name = typeMirror.toString();
        int index = name.lastIndexOf(".");
        if(index >= 0) {
            return name.substring(index+1);
        }

        return name;
    }

    /**
     * JavaPoetの{@link ClassName}に変換します。
     * @return クラス名情報。
     * @throws NoSuchElementException クラスタイプでない場合にスローされます。
     */
    public ClassName asClassName() {
        return ClassName.get((TypeElement)typeElement.orElseThrow());
    }

    /**
     * JavaPoetのタイプ情報を取得する。
     * ただし、プリミティブ型のときはラッパークラスを取得する。
     * @return JavaPoetのタイプ情報。
     */
    public TypeName getWrapperTypeName() {

        if(isPrimitive()) {
            // プリミティブ型のときはラッパー方に変える
            return TypeName.get(typeMirror).box();
        }

        return TypeName.get(typeMirror);

    }

    /**
     * JavaPoetのタイプ情報を取得する。
     * @return JavaPoetのタイプ情報。
     */
    public TypeName getTypeName() {
        return TypeName.get(typeMirror);
    }

}
