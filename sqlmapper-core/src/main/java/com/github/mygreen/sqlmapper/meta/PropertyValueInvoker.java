package com.github.mygreen.sqlmapper.meta;

import java.lang.reflect.InvocationTargetException;

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
     * @throw {@link NullPointerException} 引数{@code entityObject}がnullの場合
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
            //TODO: フラグでquitelyをつける
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

}
