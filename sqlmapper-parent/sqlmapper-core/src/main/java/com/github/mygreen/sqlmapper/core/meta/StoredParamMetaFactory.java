package com.github.mygreen.sqlmapper.core.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.sqlmapper.core.type.ValueType;
import com.github.mygreen.sqlmapper.core.type.ValueTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * ストアドプロシージャ／ストアドファンクションのパラメータのメタ情報を作成します。
 *
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class StoredParamMetaFactory {

    /**
     * メッセージフォーマッタです。
     * Springのインジェクション対象です。
     */
    @Getter
    @Setter
    @Autowired
    private MessageFormatter messageFormatter;

    @Getter
    @Setter
    @Autowired
    private ValueTypeRegistry valueTypeRegistry;

    @Getter
    @Setter
    @Autowired
    private StoredPropertyMetaFactory storedPropertyMetaFactory;

    @Getter
    @Setter
    @Autowired
    private PropertyMetaFactory propertyMetaFactory;

    /**
     * パラメータのメタ情報のキャッシュ用マップです。
     * <p>key=エンティティのFQN, value=StoredParamMeta</p>
     */
    private ConcurrentHashMap<String, StoredParamMeta> paramMetaMap = new ConcurrentHashMap<>(100);

    /**
     * 作成したパラメータのメタ情報をクリアします。
     */
    public void clear() {
        this.paramMetaMap.clear();
    }

    public StoredParamMeta create(@NonNull final Class<?> paramClass) {

        if(valueTypeRegistry.isRegisteredType(paramClass)) {
            return createAnonymounseParamMeta(paramClass);
        }

        return paramMetaMap.computeIfAbsent(paramClass.getName(), s -> doParamMeta(paramClass));

    }

    public StoredParamMeta createAnonymounseParamMeta(final Class<?> paramClass) {

        StoredParamMeta paramMeta = new StoredParamMeta(paramClass, true);

        // プロパティに対する型変換を設定します。
        ValueType<?> valueType = valueTypeRegistry.findValueType(paramClass);

        //TODO:
//        // OracleなどBoolean型を純粋にサポートしていない場合は、int型に変換するタイプに変換する。
//        valueType = dialect.getValueType(valueType);
        paramMeta.setValueType(valueType);

        return paramMeta;
    }

    private StoredParamMeta doParamMeta(final Class<?> paramClass) {

        final StoredParamMeta paramMeta = new StoredParamMeta(paramClass, false);

        doPropertyMeta(paramMeta, paramClass);

        return paramMeta;

    }

    private void doPropertyMeta(final StoredParamMeta paramMeta, final Class<?> paramClass) {

        List<StoredPropertyMeta> propertyMeataList = new ArrayList<>();
        extractProperty(paramClass, propertyMeataList);

        // ネストしたプロパティを抽出する
        propertyMeataList.stream()
            .filter(p -> !p.isSingleValue())
            .forEach(p -> doNestedPropertyMeta(p));

        propertyMeataList.stream().forEach(p -> paramMeta.addPropertyMeta(p));


    }

    private void extractProperty(final Class<?> targetClass, final List<StoredPropertyMeta> propertyMeataList) {

        for(Field field : targetClass.getDeclaredFields()) {

            int modifiers = field.getModifiers();
            if(Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }
            ReflectionUtils.makeAccessible(field);

            propertyMeataList.add(storedPropertyMetaFactory.create(field));
        }
    }

    private void doNestedPropertyMeta(final StoredPropertyMeta propertyMeta) {

        final Class<?> propertyType = propertyMeta.getPropertyType();

        final Class<?> beanType;
        if(Collection.class.isAssignableFrom(propertyType)) {
            beanType = propertyMeta.getComponentType()
                    .orElseThrow(() -> new InvalidStoredParamException(propertyMeta.getDeclaringClass(),
                            messageFormatter.create("storedParam.notDeclaredComponentType")
                            .paramWithClass("paramType", propertyMeta.getDeclaringClass())
                            .param("property", propertyMeta.getName())
                            .format()));
        } else {
            beanType = propertyType;
        }

        // プロパティを抽出する
        List<PropertyMeta> nestedPropertyMeataList = new ArrayList<>();
        for(Field field : beanType.getDeclaredFields()) {

            int modifiers = field.getModifiers();
            if(Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }
            ReflectionUtils.makeAccessible(field);

            nestedPropertyMeataList.add(propertyMetaFactory.create(field, Optional.empty()));
        }

        nestedPropertyMeataList.stream().forEach(p -> propertyMeta.addNestedPropertyMeta(p));

        if(propertyMeta.getAllNestedColumnPropertyMeta().isEmpty()) {
            /*
             * マッピング対象のカラムプロパティが存在しない場合は、JavaBean形式として不正としてエラーとする。
             * または、Mapなどのサポート対象外の形式と判断する。
             */
            throw new InvalidStoredParamException(propertyMeta.getDeclaringClass(), messageFormatter.create("storedParam.notFoundColumnProperty")
                    .paramWithClass("paramType", propertyMeta.getDeclaringClass())
                    .param("property", propertyMeta.getName())
                    .paramWithClass("propertyType", propertyType)
                    .format());
        }
    }

}
