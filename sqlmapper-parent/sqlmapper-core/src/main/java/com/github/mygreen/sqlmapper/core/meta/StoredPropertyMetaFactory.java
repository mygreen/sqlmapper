package com.github.mygreen.sqlmapper.core.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.github.mygreen.sqlmapper.core.annotation.Column;
import com.github.mygreen.sqlmapper.core.annotation.In;
import com.github.mygreen.sqlmapper.core.annotation.InOut;
import com.github.mygreen.sqlmapper.core.annotation.Out;
import com.github.mygreen.sqlmapper.core.annotation.ResultSet;
import com.github.mygreen.sqlmapper.core.naming.NamingRule;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.type.ValueTypeRegistry;
import com.github.mygreen.sqlmapper.core.util.ClassUtils;
import com.github.mygreen.sqlmapper.core.util.NameUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * ストアドプロシージャ／ファンクション
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class StoredPropertyMetaFactory {

    @Getter
    @Setter
    @Autowired
    private ValueTypeRegistry valueTypeRegistry;

    @Getter
    @Setter
    @Autowired
    private NamingRule namingRule;

    /**
     * フィールド情報を元に、ストアド用のエンティティ形式のパラメータのプロパティ情報を作成します。
     *
     * @param field フィールド情報
     * @return プロパティ情報
     */
    public StoredPropertyMeta create(final Field field) {

        final StoredPropertyMeta propertyMeta = new StoredPropertyMeta(field.getName(), field.getType());
        doField(propertyMeta, field);

        // フィールドに対するgetter/setterメソッドを設定します。
        final Class<?> declaringClass = field.getDeclaringClass();
        for(Method method : declaringClass.getMethods()) {
            ReflectionUtils.makeAccessible(method);

            int modifiers = method.getModifiers();
            if(Modifier.isStatic(modifiers)) {
                continue;
            }

            if(ClassUtils.isSetterMethod(method)) {
                doSetterMethod(propertyMeta, method);

            } else if(ClassUtils.isGetterMethod(method) || ClassUtils.isBooleanGetterMethod(method)) {
                doGetterMethod(propertyMeta, method);
            }
        }

        if(propertyMeta.isIn() || propertyMeta.isOut() || propertyMeta.isInOut()) {

            // プロパティに対する型変換を設定します。
            ValueType<?> valueType = valueTypeRegistry.findValueType(propertyMeta);

            //TODO: Oracleなどの純粋なタイプ
            propertyMeta.setValueType(valueType);
            propertyMeta.setSingleValue(true);

            // パラメータ名の設定
            Optional<String> paramName = propertyMeta.getAnnotation(In.class).map(a -> a.name())
                    .or(() -> propertyMeta.getAnnotation(Out.class).map(a -> a.name()))
                    .or(() -> propertyMeta.getAnnotation(InOut.class).map(a -> a.name()));
            propertyMeta.setParamName(paramName.map(p -> StringUtils.hasLength(p) ? p : namingRule.propertyToStoredParam(propertyMeta.getName()))
                    .orElseGet(() -> namingRule.propertyToStoredParam(propertyMeta.getName())));


        } else if(propertyMeta.isResultSet()) {
            // ResultSetの場合
            if(valueTypeRegistry.isRegisteredType(propertyMeta.getPropertyType())) {
                // 通常の値である場合
                ValueType<?> valueType = valueTypeRegistry.findValueType(propertyMeta);

                //TODO: Oracleなどの純粋なタイプ
                propertyMeta.setValueType(valueType);
                propertyMeta.setSingleValue(true);

            } else {
                // JavaBeanの場合
                // JavaBeanのプロパティの抽出は、StoredParamMetaFactoryで行う。
                propertyMeta.setSingleValue(false);

                // 総称型の設定
                doComponentType(propertyMeta, field);
            }

            // パラメータ名の設定
            Optional<String> paramName = propertyMeta.getAnnotation(ResultSet.class).map(a -> a.name());
            propertyMeta.setParamName(paramName.map(p -> StringUtils.hasLength(p) ? p : namingRule.propertyToStoredParam(propertyMeta.getName()))
                    .orElseGet(() -> namingRule.propertyToStoredParam(propertyMeta.getName())));


        } else {
            // ResultSetのネストしたプロパティの場合
            ValueType<?> valueType = valueTypeRegistry.findValueType(propertyMeta);
            propertyMeta.setValueType(valueType);
            propertyMeta.setSingleValue(true);

            // パラメータ名の設定
            Optional<String> paramName = propertyMeta.getAnnotation(Column.class).map(a -> a.name());
            propertyMeta.setParamName(paramName.map(p -> StringUtils.hasLength(p) ? p : namingRule.propertyToColumn(propertyMeta.getName()))
                    .orElseGet(() -> namingRule.propertyToColumn(propertyMeta.getName())));

        }

        return propertyMeta;

    }

    /**
     * プロパティのメタ情報に対する処理を実行します。
     * @param propertyMeta プロパティのメタ情報
     * @param field フィールド情報
     */
    private void doField(final StoredPropertyMeta propertyMeta, final Field field) {

        propertyMeta.setField(field);

        final Annotation[] annos = field.getAnnotations();
        for(Annotation anno : annos) {
            if(!isSupportedAnnotation(anno)) {
                continue;
            }

            final Class<? extends Annotation> annoClass = anno.annotationType();
            propertyMeta.addAnnotation(annoClass, anno);
        }
    }

    /**
     * サポートするアノテーションか判定する。
     * <p>確実に重複するJava標準のアノテーションは除外するようにします。</p>
     *
     * @param anno 判定対象のアノテーション
     * @return tureのときサポートします。
     */
    private boolean isSupportedAnnotation(final Annotation anno) {

        final String name = anno.annotationType().getName();
        if(name.startsWith("java.lang.annotation.")) {
            return false;
        }

        return true;
    }

    /**
     * setterメソッドの情報を処理する。
     * @param propertyMeta プロパティのメタ情報
     * @param method setterメソッド
     */
    private void doSetterMethod(final StoredPropertyMeta propertyMeta, final Method method) {

        final String methodName = method.getName();
        final String propertyName = NameUtils.uncapitalize(methodName.substring(3));

        if(!propertyMeta.getName().equals(propertyName)) {
            // プロパティ名が一致しない場合はスキップする
            return;
        }

        propertyMeta.setWriteMethod(method);

    }

    /**
     * getterメソッドの情報を処理する。
     * @param propertyMeta プロパティのメタ情報
     * @param method getterメソッド
     */
    private void doGetterMethod(final StoredPropertyMeta propertyMeta, final Method method) {

        final String methodName = method.getName();
        final String propertyName;
        if(methodName.startsWith("get")) {
            propertyName = NameUtils.uncapitalize(methodName.substring(3));
        } else {
            // 「is」から始まる場合
            propertyName = NameUtils.uncapitalize(methodName.substring(2));
        }

        if(!propertyMeta.getName().equals(propertyName)) {
            // プロパティ名が一致しない場合はスキップする
            return;
        }

        propertyMeta.setReadMethod(method);

    }

    /**
     * JavaBean型の場合、総称型を設定します。
     * @param propertyMeta プロパティのメタ情報
     * @param field フィールド情報
     */
    private void doComponentType(final StoredPropertyMeta propertyMeta, final Field field) {

        Class<?> fieldType = field.getType();
        if(Collection.class.isAssignableFrom(fieldType)) {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            propertyMeta.setComponentType((Class<?>)type.getActualTypeArguments()[0]);

        } else if(fieldType.isArray()) {
            propertyMeta.setComponentType(fieldType.getComponentType());

        }

    }
}
