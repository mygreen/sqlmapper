package com.github.mygreen.sqlmapper.core.meta;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.springframework.beans.BeanUtils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 埋め込み型のプロパティ値にアクセスするためのクラス。
 *
 *
 * @author T.TSUCHIE
 *
 */
@Slf4j
public class PropertyValueInvoker {

    /**
     * このプロパティに対して値を設定する。
     * @param propertyMeta 取得対象のプロパティ情報
     * @param entityObject 親のオブジェクト
     * @param propertyValue 設定するプロパティの値
     * @throws NullPointerException 引数{@literal entityObject}がnullの場合
     */
    public static void setPropertyValue(final @NonNull PropertyMeta propertyMeta,
            final @NonNull Object entityObject, final Object propertyValue) {

        if(propertyMeta.getWriteMethod().isPresent()) {
            try {
                propertyMeta.getWriteMethod().get().invoke(entityObject, propertyValue);
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                //TODO: 例外を見直す
                throw new RuntimeException("Fail set property value for writer method.", e);
            }
        } else if(propertyMeta.getField().isPresent()) {
            try {
                propertyMeta.getField().get().set(entityObject, propertyValue);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                //TODO: 例外を見直す
                throw new RuntimeException("Fail set property value for field.", e);
            }
        } else {
            log.warn("Not found saving method or field with property value in {}#{}",
                    entityObject.getClass().getName(), propertyMeta.getName());
            //TODO: フラグでquietlyをつける
            throw new IllegalStateException();
        }

    }

    /**
     * このプロパティの値を取得する。
     * @param propertyMeta 取得対象のプロパティ情報
     * @param entityObject ルートとなるエンティティオブジェクト
     * @return プロパティの値。
     * @throws NullPointerException 引数がnullのとき
     * @throws IllegalStateException 取得対象のフィールドやメソッドがない場合
     */
    public static Object getPropertyValue(final @NonNull PropertyMeta propertyMeta,
            final @NonNull Object entityObject) {

        // ルート直下のプロパティの場合
        if(propertyMeta.getReadMethod().isPresent()) {
            try {
                return propertyMeta.getReadMethod().get().invoke(entityObject);
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                //TODO: 例外を見直す
                throw new RuntimeException("Fail get property value for reader method.", e);
            }

        } else if(propertyMeta.getField().isPresent()) {
            try {
                return propertyMeta.getField().get().get(entityObject);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                //TODO: 例外を見直す
                throw new RuntimeException("Fail get property value for field.", e);
            }
        } else {
            log.warn("Not found reading method or field with property value in {}#{}",
                    entityObject.getClass().getName(), propertyMeta.getName());
            throw new IllegalStateException();
        }

    }

    /**
     * 埋め込みプロパティを考慮して値を取得する。
     * 埋め込みオブジェクトの場合は、親のオブジェクトをたどり設定していく。
     * その際に、親のオブジェクトがnullのときは、そこでたどるのを中止してnullを返す。
     *
     * @param propertyMeta 取得対象のプロパティ情報
     * @param entityObject 取得元となるルートのエンティティオブジェクト。
     * @return 取得対象のプロパティの値
     * @throws NullPointerException {@literal propertyMeta == null or entityObject == null.}
     */
    public static Object getEmbeddedPropertyValue(final @NonNull PropertyMeta propertyMeta,
            final @NonNull Object entityObject) {

        if(!propertyMeta.hasParent()) {
            return getPropertyValue(propertyMeta, entityObject);
        }

        final LinkedList<PropertyMeta> parentStack = new LinkedList<>();
        setupParentStack(propertyMeta, parentStack);

        Object parentObject = entityObject;

        // スタックの最上部（ルート）から下にたどる
        for(PropertyMeta parent : parentStack) {
            Object propertyValue = getPropertyValue(parent, parentObject);

            // 親がnullのとき、子もnullとして返す。
            if(propertyValue == null) {
                return null;
            }

            parentObject = propertyValue;
        }

        return getPropertyValue(propertyMeta, parentObject);
    }

    /**
     * 埋め込みプロパティを考慮して値を設定する。
     * 埋め込みオブジェクトの場合は、親のオブジェクトをたどり設定していく。
     * その際に、親のオブジェクトがnullのときはインスタンスを生成して設定する。
     *
     * @param propertyMeta 設定対象のプロパティ情報
     * @param entityObject ルートとなるエンティティオブジェクト。
     * @param propertyValue 設定対象のプロパティの値
     * @throws NullPointerException {@literal propertyMeta == null || entityObject == null.}
     */
    public static void setEmbeddedPropertyValue(final @NonNull PropertyMeta propertyMeta,
            final @NonNull Object entityObject, final Object propertyValue) {

        if(!propertyMeta.hasParent()) {
            setPropertyValue(propertyMeta, entityObject, propertyValue);
            return;
        }

        final LinkedList<PropertyMeta> parentStack = new LinkedList<>();
        setupParentStack(propertyMeta, parentStack);

        Object parentObject = entityObject;

        // スタックの最上部（ルート）から下にたどっていく
        for(PropertyMeta parent : parentStack) {
            Object parentPropertyValue = getPropertyValue(parent, parentObject);

            // 親のインスタンスがない場合は作成する
            if(parentPropertyValue == null) {
                parentPropertyValue = BeanUtils.instantiateClass(parent.getPropertyType());
                setPropertyValue(parent, parentObject, propertyValue);
            }
            parentObject = parentPropertyValue;
        }

        setPropertyValue(propertyMeta, parentObject, propertyValue);

    }

    /**
     * 埋め込みプロパティの親をたどるためのスタックを設定する。
     * @param propertyMeta 子のプロパティ情報
     * @param parentStack 親へのスタック（自身を含む）
     */
    private static void setupParentStack(final PropertyMeta propertyMeta,
            final LinkedList<PropertyMeta> parentStack) {

        parentStack.push(propertyMeta);

        if(propertyMeta.hasParent()) {
            setupParentStack(propertyMeta.getParent(), parentStack);
            return;
        }

    }

}
