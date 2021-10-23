package com.github.mygreen.sqlmapper.core.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * JavaBeanのプロパティの基本的な機能を提供します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
@RequiredArgsConstructor
public abstract class PropertyBase {

    /**
     * プロパティ名
     */
    @Getter
    protected final String name;

    /**
     * プロパティタイプ
     */
    @Getter
    protected final Class<?> propertyType;

    /**
     * フィールド情報
     */
    protected Optional<Field> field = Optional.empty();

    /**
     * setterメソッド
     */
    protected Optional<Method> writeMethod = Optional.empty();

    /**
     * getterメソッド
     */
    protected Optional<Method> readMethod = Optional.empty();

    /**
     * アノテーションの情報
     */
    protected Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();

    /**
     * プロパティが定義されているクラス情報を取得します。
     * @return プロパティが定義されているクラス情報
     * @throws IllegalStateException クラス情報を取得するための情報が不足している場合。
     */
    public Class<?> getDeclaringClass() {

        if(field.isPresent()) {
            return field.get().getDeclaringClass();
        }

        if(readMethod.isPresent()) {
            return readMethod.get().getDeclaringClass();
        }

        if(writeMethod.isPresent()) {
            return writeMethod.get().getDeclaringClass();
        }

        throw new IllegalStateException("not found availabeld info.");

    }

    /**
     * 読み込み可能なプロパティか判定する。
     * <p>getterメソッドまたはpublicなフィールドが存在する場合</p>
     * @return {@literal true}のとき読み込み可能。
     */
    public boolean isReadable() {

        if(readMethod.isPresent()) {
            return true;
        }

        if(field.isPresent()) {
            if(Modifier.isPublic(field.get().getModifiers())) {
                return true;
            }
        }

        return false;

    }

    /**
     * 書込み可能なプロパティか判定する。
     * <p>setterメソッドまたはpublicなフィールドが存在する場合</p>
     * @return {@literal true}のと書き込み可能。
     */
    public boolean isWritable() {

        if(writeMethod.isPresent()) {
            return true;
        }

        if(field.isPresent()) {
            if(Modifier.isPublic(field.get().getModifiers())) {
                return true;
            }
        }

        return false;

    }

    /**
     * プロパティに対するフィールドを設定します。
     * @param field フィールド（nullを許容します）
     */
    public void setField(Field field) {
        this.field = Optional.ofNullable(field);
    }

    /**
     * プロパティに対するフィールドを情報を取得します。
     * @return プロパティに対するフィールド情報
     */
    public Optional<Field> getField() {
        return field;
    }

    /**
     * プロパティに対するsetterメソッドを設定します。
     * @param method setterメソッド（nullを許容します）
     */
    public void setWriteMethod(Method method) {
        this.writeMethod = Optional.ofNullable(method);
    }

    /**
     * プロパティに対するsetterメソッドを取得します。
     * @return プロパティに対するsetterメソッド。
     */
    public Optional<Method> getWriteMethod() {
        return writeMethod;
    }

    /**
     * プロパティに対するgetterメソッドを設定します。
     * @param method getterメソッド（nullを許容します）
     */
    public void setReadMethod(Method method) {
        this.readMethod = Optional.ofNullable(method);
    }

    /**
     * プロパティに対するgetterメソッドを取得します。
     * @return プロパティに対するgetterメソッド
     */
    public Optional<Method> getReadMethod() {
        return readMethod;
    }

    /**
     * アノテーションを追加します。
     * @param annoClass アノテーションのタイプ
     * @param anno 追加するアノテーション
     */
    public void addAnnotation(@NonNull Class<? extends Annotation> annoClass, @NonNull Annotation anno) {
        this.annotationMap.put(annoClass, anno);
    }

    /**
     * 指定したアノテーションを持つか判定します。
     * @param <A> アノテーションのタイプ。
     * @param annoClass アノテーションのクラスタイプ。
     * @return trueの場合、アノテーションを持ちます。
     */
    public <A extends Annotation> boolean hasAnnotation(@NonNull Class<A> annoClass) {
        return annotationMap.containsKey(annoClass);
    }

    /**
     * タイプを指定して、アノテーションを取得する。
     * @param <A> アノテーションのタイプ。
     * @param annoClass アノテーションのクラスタイプ。
     * @return 存在しない場合、空を返します。
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annoClass) {
        return Optional.ofNullable((A)annotationMap.get(annoClass));
    }
}
