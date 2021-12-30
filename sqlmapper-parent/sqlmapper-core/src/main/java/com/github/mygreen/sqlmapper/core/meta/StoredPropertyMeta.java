package com.github.mygreen.sqlmapper.core.meta;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.annotation.Nullable;

import org.springframework.util.LinkedCaseInsensitiveMap;

import com.github.mygreen.sqlmapper.core.annotation.In;
import com.github.mygreen.sqlmapper.core.annotation.InOut;
import com.github.mygreen.sqlmapper.core.annotation.Out;
import com.github.mygreen.sqlmapper.core.annotation.ResultSet;
import com.github.mygreen.sqlmapper.core.type.ValueType;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * ストアドプロシージャ／ストアドファンクションのDTO形式のパラメータのプロパティ。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
@Slf4j
public class StoredPropertyMeta extends PropertyBase {

    /**
     * パラメータ名。
     */
    @Getter
    @Setter
    private String paramName;

    /**
     * 値の変換処理
     */
    private Optional<ValueType<?>> valueType = Optional.empty();

    /**
     * Bean形式ではない1つのオブジェクトの場合
     */
    @Getter
    @Setter
    private boolean singleValue;

    /**
     * ネストしたBean形式のプロパティの情報
     * <p>key=プロパティ名</p>
     */
    private LinkedCaseInsensitiveMap<PropertyMeta> nestedPropertyMetaMap = new LinkedCaseInsensitiveMap<>();

    /**
     * ネストしたBean形式のプロパティの情報
     * <p>key=カラム名</p>
     */
    private LinkedCaseInsensitiveMap<PropertyMeta> nestedColumnPropertyMetaMap = new LinkedCaseInsensitiveMap<>();

    /**
     * Beanの親のプロパティ情報
     */
    private Optional<StoredPropertyMeta> parent = Optional.empty();

    /**
     * Genericsのコンポーネントタイプ
     */
    @Getter
    private Optional<Class<?>> componentType = Optional.empty();

    /**
     * プロパティのインスタンス情報を作成します。
     * @param name プロパティ名
     * @param propertyType プロパティのクラスタイプ
     */
    public StoredPropertyMeta(String name, Class<?> propertyType) {
        super(name, propertyType);
    }

    /**
     * {@literal IN}用のパラメータかどうか。
     * @return 他のパラメータ(OUT/IN-OUT/ResultSet)でない場合も該当します。
     */
    public boolean isIn() {
        return hasAnnotation(In.class)
                || (!isOut() && !isInOut() && !isResultSet());
    }

    /**
     * {@literal OUT}用のパラメータかどうか。
     * @return
     */
    public boolean isOut() {
        return hasAnnotation(Out.class);
    }

    /**
     * {@literal IN-OUT}用のパラメータかどうか。
     * @return
     */
    public boolean isInOut() {
        return hasAnnotation(InOut.class);
    }

    /**
     * {@literal ResultSet}用のパラメータかどうか。
     * @return
     */
    public boolean isResultSet() {
        return hasAnnotation(ResultSet.class);
    }

    /**
     * このプロパティに対して値を設定する。
     * @param entityObject 親のオブジェクト
     * @param propertyValue 設定するプロパティの値
     * @throws NullPointerException 引数{@literal entityObject}がnullの場合
     */
    public void setPropertyValue(final @NonNull Object entityObject, final Object propertyValue) {

        if(getWriteMethod().isPresent()) {
            try {
                getWriteMethod().get().invoke(entityObject, propertyValue);
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                //TODO: 例外を見直す
                throw new RuntimeException("Fail set property value for writer method.", e);
            }
        } else if(getField().isPresent()) {
            try {
                getField().get().set(entityObject, propertyValue);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                //TODO: 例外を見直す
                throw new RuntimeException("Fail set property value for field.", e);
            }
        } else {
            log.warn("Not found saving method or field with property value in {}#{}",
                    entityObject.getClass().getName(), getName());
            //TODO: フラグでquietlyをつける
            throw new IllegalStateException();
        }

    }

    /**
     * このプロパティの値を取得する。
     * @param entityObject ルートとなるエンティティオブジェクト
     * @return プロパティの値。
     * @throws NullPointerException 引数がnullのとき
     * @throws IllegalStateException 取得対象のフィールドやメソッドがない場合
     */
    public Object getPropertyValue(final @NonNull Object entityObject) {

        // ルート直下のプロパティの場合
        if(getReadMethod().isPresent()) {
            try {
                return getReadMethod().get().invoke(entityObject);
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                //TODO: 例外を見直す
                throw new RuntimeException("Fail get property value for reader method.", e);
            }

        } else if(getField().isPresent()) {
            try {
                return getField().get().get(entityObject);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                //TODO: 例外を見直す
                throw new RuntimeException("Fail get property value for field.", e);
            }
        } else {
            log.warn("Not found reading method or field with property value in {}#{}",
                    entityObject.getClass().getName(), getName());
            throw new IllegalStateException();
        }

    }

    /**
     * プロパティに対する {@link ValueType} を取得します。
     * @return JavaBean({@link #isSingleValue()} = false) のとき、{@literal null} を返します。
     */
    public ValueType<?> getValueType() {
        return valueType.orElse(null);
    }

    /**
     * プロパティに対する {@link ValueType} を設定します。
     * @param valueType プロパティに対する {@link ValueType}。
     */
    public void setValueType(@NonNull ValueType<?> valueType) {
        this.valueType = Optional.of(valueType);
    }

    /**
     * JavaBean形式のネストしたプロパティ情報を追加する
     * @param nestedPropertyMeta JavaBean形式のネストしたプロパティ
     */
    public void addNestedPropertyMeta(@NonNull PropertyMeta nestedPropertyMeta) {

        this.nestedPropertyMetaMap.put(nestedPropertyMeta.getName(), nestedPropertyMeta);

        if(nestedPropertyMeta.getColumnMeta() != null) {
            this.nestedColumnPropertyMetaMap.put(nestedPropertyMeta.getColumnMeta().getName(), nestedPropertyMeta);
        }

    }

    /**
     * JavaBean形式のネストしたクラスのプロパティかどか判定する。
     * @return 埋め込み用のクラスのプロパティの場合trueを変す。
     */
    public boolean hasParent() {
        return parent.isPresent();
    }

    /**
     * JavaBean形式のネストしたクラスのプロパティの親情報を取得する。
     * @return 親情のプロパティ情報
     * @throws NoSuchElementException 親が存在しないときにスローされます。
     */
    public StoredPropertyMeta getParent() {
        return parent.get();
    }

    /**
     * JavaBean形式のネストしたプロパティの一覧を取得する。
     * @return
     */
    public Collection<PropertyMeta> getAllNestedPopertyMetaList() {
        return nestedPropertyMetaMap.values();
    }

    /**
     * カラムに紐づく全てのプロパティメタ情報を取得します。
     * @return カラムに紐づく全てのプロパティメタ情報
     */
    public Collection<PropertyMeta> getAllNestedColumnPropertyMeta() {
        return nestedColumnPropertyMetaMap.values();
    }

    /**
     * 指定したカラム名に一致するネストしたプロパティを探索します。
     *
     *
     * @param columnName カラム名
     * @return 一致するプロパティ情報
     */
    public Optional<PropertyMeta> findNestedColumnPropertyMeta(final String columnName) {

        PropertyMeta foundPropertyMeta = nestedColumnPropertyMetaMap.get(columnName);
        if(foundPropertyMeta != null) {
            return Optional.of(foundPropertyMeta);
        }

        return Optional.empty();

    }

    /**
     * コンポーネントタイプを設定します。
     * <p>CollectionのときなどのGenericsのタイプを取得します。
     * @param componentType コンポーネントタイプ
     */
    public void setComponentType(@Nullable Class<?> componentType) {
        this.componentType = Optional.of(componentType);
    }


}
