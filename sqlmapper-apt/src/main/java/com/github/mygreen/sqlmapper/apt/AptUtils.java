package com.github.mygreen.sqlmapper.apt;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import com.github.mygreen.sqlmapper.apt.model.EntityMetamodel;

/**
 * APT処理のユーティリティクラスです。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class AptUtils {

    /**
     * 要素が非finalなインスタンスフィールド(非staticなフィールド)か判定します。
     * @param element 判定対象の要素
     * @return 非finalなインスタンスフィールド(非staticなフィールド)のとき{@literal true}を返します。
     */
    public static boolean isInstanceField(final Element element) {

        // フィールドかどうか
        if(element.getKind() != ElementKind.FIELD) {
            return false;
        }

        // 非static かどうか
        if(element.getModifiers().contains(Modifier.STATIC)) {
            return false;
        }

        // 非finalかどうか
        if(element.getModifiers().contains(Modifier.FINAL)) {
            return false;
        }

        return true;

    }

    /**
     * クラス名とパッケージ名のセパレータを取得します。
     * @param entityModel エンティティモデル情報
     * @return 内部クラスのとき {@literal "$"} を返し、それ以外の時は {@literal "."} を返します。
     */
    public static String getPackageClassNameSeparator(final EntityMetamodel entityModel) {
        return entityModel.getType().isStaticInnerClass() ? "$" : ".";
    }

    /**
     * 継承しているクラス情報を抽出する。
     * @param type 現在のクラス情報
     * @param typeUtils ユーティリティ。
     * @param superTypes 抽出したクラス情報。
     */
    public static void extractSuperClassTypes(final TypeMirror type, final Types typeUtils, final List<TypeMirror> superTypes) {

        if(type.toString().equals(Object.class.getCanonicalName())) {
            return;
        }

        List<? extends TypeMirror> list = typeUtils.directSupertypes(type);
        for(TypeMirror superType : list) {
            if(!superTypes.contains(superType)) {
                superTypes.add(superType);
            }

            // 再帰的に見ていく
            extractSuperClassTypes(superType, typeUtils, superTypes);
        }


    }

}
