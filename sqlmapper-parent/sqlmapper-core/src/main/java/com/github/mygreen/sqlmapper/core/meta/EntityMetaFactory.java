package com.github.mygreen.sqlmapper.core.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.sqlmapper.core.annotation.Embeddable;
import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.MappedSuperclass;
import com.github.mygreen.sqlmapper.core.annotation.Table;
import com.github.mygreen.sqlmapper.core.annotation.Version;
import com.github.mygreen.sqlmapper.core.id.TableIdGenerator;
import com.github.mygreen.sqlmapper.core.naming.NamingRule;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * エンティティのメタ情報を作成します。
 *
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class EntityMetaFactory {

    /**
     * 命名規則の定義です。
     * Springのインジェクション対象です。
     */
    @Getter
    @Setter
    @Autowired
    private NamingRule namingRule;

    /**
     * メッセージフォーマッタです。
     * Springのインジェクション対象です。
     */
    @Getter
    @Setter
    @Autowired
    private MessageFormatter messageFormatter;

    /**
     * プロパティのメタ情報を作成します。
     * Springのインジェクション対象です。
     */
    @Getter
    @Setter
    @Autowired
    private PropertyMetaFactory propertyMetaFactory;

    /**
     * エンティティのメタ情報のキャッシュ用マップです。
     * <p>key=エンティティのFQN, value=EntityMeta</p>
     */
    private ConcurrentHashMap<String, EntityMeta> entityMetaMap = new ConcurrentHashMap<>(200);

    /**
     * 作成したエンティティのメタ情報をクリアします。
     */
    public void clear() {
        this.entityMetaMap.clear();
    }

    /**
     * IDのテーブルによる自動採番のキャッシュ情報をクリアします。
     * クリアすることで、次に採番するときに、最新のDBの情報を反映した状態になります。
     * @since 0.3
     */
    public void refreshTableIdGenerator() {

        entityMetaMap.values().stream()
            .flatMap(e -> e.getIdPropertyMetaList().stream())
            .filter(p -> p.getIdGenerator().isPresent() && (p.getIdGenerator().get() instanceof TableIdGenerator))
            .map(p -> (TableIdGenerator)(p.getIdGenerator().get()))
            .forEach(g -> g.clearCache());

    }

    /**
     * エンティティ情報を元にメタ情報を組み立てます。
     * 組み立ててたメタ情報はキャッシュしており、組み立て済みの場合はキャッシュを返します。
     *
     * @param entityClass エンティティクラス
     * @return エンティティのメタ情報
     */
    public EntityMeta create(@NonNull final Class<?> entityClass) {
        return entityMetaMap.computeIfAbsent(entityClass.getName(), s -> doEntityMeta(entityClass));
    }

    /**
     * エンティティのメタ情報に対応する処理を実行します。
     * @param entityClass エンティティ情報
     * @return エンティティのメタ情報
     */
    private EntityMeta doEntityMeta(final Class<?> entityClass) {

        final Entity annoEntity = entityClass.getAnnotation(Entity.class);
        if(annoEntity == null) {
            throw new InvalidEntityException(entityClass, messageFormatter.create("entity.anno.required")
                    .paramWithClass("entityClass", entityClass)
                    .paramWithAnno("anno", Entity.class)
                    .format());
        }

        final EntityMeta entityMeta = new EntityMeta(entityClass);

        if(!annoEntity.name().isEmpty()) {
            entityMeta.setName(annoEntity.name());
        } else {
            entityMeta.setName(entityClass.getSimpleName());
        }

        doTableMeta(entityMeta, entityClass);
        doPropertyMeta(entityMeta, entityClass);

        return entityMeta;
    }

    /**
     * エンティティ情報からテーブルのメタ情報に対応する処理を実行します。
     * @param entityMeta エンティティメタ情報
     * @param entityClass エンティティ情報
     */
    private void doTableMeta(final EntityMeta entityMeta, final Class<?> entityClass) {

        final TableMeta tableMeta = new TableMeta();
        final String defaultName = namingRule.entityToTable(entityClass.getName());
        final Table annoTable = entityClass.getAnnotation(Table.class);

        if(annoTable != null) {
            if(!annoTable.name().isEmpty()) {
                tableMeta.setName(annoTable.name());
            } else {
                tableMeta.setName(defaultName);
            }

            if(!annoTable.schema().isEmpty()) {
                tableMeta.setSchema(annoTable.schema());
            }

            if(!annoTable.catalog().isEmpty()) {
                tableMeta.setCatalog(annoTable.catalog());
            }

            tableMeta.setReadOnly(annoTable.readOnly());

        } else {
            tableMeta.setName(defaultName);
        }

        entityMeta.setTableMeta(tableMeta);

    }

    /**
     * エンティティ情報からプロパティのメタ情報に対応する処理を実行します。
     * @param entityMeta エンティティメタ情報
     * @param entityClass エンティティ情報
     */
    private void doPropertyMeta(final EntityMeta entityMeta, final Class<?> entityClass) {

        final List<PropertyMeta> propertyMetaList = new ArrayList<>();
        extractProperty(entityClass, propertyMetaList, entityMeta, false);
        extractSuperClassProperty(entityClass.getSuperclass(), propertyMetaList, entityMeta);

        // 埋め込みのプロパティを抽出する
        propertyMetaList.stream()
            .filter(prop -> prop.isEmbedded())
            .forEach(prop -> doEmbeddedPropertyMeta(entityClass, prop, entityMeta));

        validateEntity(entityClass, propertyMetaList);

        propertyMetaList.stream().forEach(p -> entityMeta.addPropertyMeta(p));

    }

    /**
     * 指定したクラスからプロパティを抽出する。
     * @param targetClass 抽出対象のクラス。
     * @param propertyMetaList 抽出したプロパティ一覧
     * @param entityMeta エンティティのメタ情報
     * @param embeddedId 埋め込み型のIDのプロパティの処理かどうか。
     */
    private void extractProperty(final Class<?> targetClass, final List<PropertyMeta> propertyMetaList,
            final EntityMeta entityMeta, boolean embeddedId) {

        for(Field field : targetClass.getDeclaredFields()) {

            int modifiers = field.getModifiers();
            if(Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }

            ReflectionUtils.makeAccessible(field);

            propertyMetaList.add(propertyMetaFactory.create(field, Optional.of(entityMeta), embeddedId));

        }
    }

    /**
     * アノテーション{@literal MappedSuperclass}が付与されている親クラスのプロパティを抽出する。
     * <p>親クラスにフィールドやアノテーションが定義してある可能性があるので、それらも取得する。</p>
     * @param superClass 親クラス
     * @param propertyMetaList 抽出したプロパティ一覧
     * @param entityClass エンティティ情報
     */
    private void extractSuperClassProperty(final Class<?> superClass, final List<PropertyMeta> propertyMetaList,
            final EntityMeta entityMeta) {

        if(superClass.equals(Object.class)) {
            return;
        }

        final MappedSuperclass annoMappedSuperclass = superClass.getAnnotation(MappedSuperclass.class);
        if(annoMappedSuperclass != null) {
            extractProperty(superClass, propertyMetaList, entityMeta, false);
        }

        // 再帰的に遡っていく
        extractSuperClassProperty(superClass.getSuperclass(), propertyMetaList, entityMeta);

    }

    /**
     * 指定したプロパティから埋め込み用プロパティを設定する。
     * @param targetClass 抽出対象のクラス。
     * @param propertyMeta 処理対象のプロパティ情報
     * @param entityClass エンティティ情報
     */
    private void doEmbeddedPropertyMeta(final Class<?> entityClass, final PropertyMeta propertyMeta,
            final EntityMeta entityMeta) {

        final Class<?> embeddableClass = propertyMeta.getPropertyType();

        boolean embeddedId = propertyMeta.hasAnnotation(EmbeddedId.class);

        if(embeddableClass.getAnnotation(Embeddable.class) == null) {
            throw new InvalidEntityException(entityClass, messageFormatter.create("embeddable.anno.required")
                    .paramWithClass("entityClass", entityClass)
                    .param("property", propertyMeta.getName())
                    .paramWithClass("embeddableClass", embeddableClass)
                    .paramWithAnno("anno", Embeddable.class)
                    .format());
        }

        // 埋め込みクラスのプロパティ情報の抽出
        final List<PropertyMeta> embeddedPropertyList = new ArrayList<>();
        extractProperty(embeddableClass, embeddedPropertyList, entityMeta, embeddedId);

        validatedEmbeddedProperty(entityClass, embeddableClass, embeddedPropertyList);

        embeddedPropertyList.stream().forEach(p -> propertyMeta.addEmbeddedablePropertyMeta(p));

    }

    /**
     * 埋め込みクラスの情報の整合性などの検証
     * <p>
     *  <li></li>
     * </p>
     *
     * @param entityClass
     * @param embeddableClass
     * @param embeddedProperties
     */
    private void validatedEmbeddedProperty(final Class<?> entityClass, final Class<?> embeddableClass,
            final List<PropertyMeta> embeddedPropertyList) {

        // プロパティが存在するかどうか。
        final long propertyCount = embeddedPropertyList.stream()
                .filter(p -> !p.isTransient())
                .count();
        if(propertyCount == 0) {
            throw new InvalidEntityException(entityClass, messageFormatter.create("entity.prop.empty")
                    .paramWithClass("classType", embeddableClass)
                    .format());
        }

    }

    private void validateEntity(final Class<?> entityClass, final List<PropertyMeta> propertyMetaList) {

        // プロパティが存在するかどうか。
        final long propertyCount = propertyMetaList.stream()
                .filter(p -> !p.isTransient())
                .count();
        if(propertyCount == 0) {
            throw new InvalidEntityException(entityClass, messageFormatter.create("entity.prop.empty")
                    .paramWithClass("classType", entityClass)
                    .format());
        }

        // カラム用のプロパティが重複していないかどうか
        Set<String> existsColumnPropertyNames = new TreeSet<>();
        Set<String> douplicatedColumnPropertyNames = new TreeSet<>();
        for(PropertyMeta prop : propertyMetaList) {
            if(prop.isTransient() || !prop.isColumn()) {
                continue;
            }

            final String propName = prop.getName();
            if(!existsColumnPropertyNames.add(propName)) {
                douplicatedColumnPropertyNames.add(propName);
            }

            // 埋め込みプロパティの場合
            for(PropertyMeta embeddedProp : prop.getEmbeddedablePopertyMetaList()) {
                if(embeddedProp.isTransient() || !prop.isColumn()) {
                    continue;
                }
                final String embeddedPropName = embeddedProp.getName();
                if(!existsColumnPropertyNames.add(embeddedPropName)) {
                    douplicatedColumnPropertyNames.add(embeddedPropName);
                }
            }

        }
        if(douplicatedColumnPropertyNames.size() > 0) {
            throw new InvalidEntityException(entityClass, messageFormatter.create("entity.prop.columnDuplicated")
                    .paramWithClass("classType", entityClass)
                    .param("propertyNames", douplicatedColumnPropertyNames)
                    .format());
        }

        // EmbeddedIdが複数存在しないかどうか。
        List<PropertyMeta> embeddedIdList = propertyMetaList.stream()
                .filter(prop -> prop.isId())
                .filter(prop -> prop.isEmbedded())
                .collect(Collectors.toList());
        if(embeddedIdList.size() >= 2) {
            List<String> propertyNames = embeddedIdList.stream()
                    .map(prop -> prop.getName())
                    .collect(Collectors.toList());

            throw new InvalidEntityException(entityClass, messageFormatter.create("entity.anno.multiPropertyAnno")
                    .paramWithClass("classType", entityClass)
                    .paramWithAnno("anno", EmbeddedId.class)
                    .param("propertyNames", propertyNames)
                    .format());
        }

        // バージョンキーが複数存在しないかどうか。
        List<PropertyMeta> versionList = propertyMetaList.stream()
                .filter(prop -> prop.isVersion())
                .collect(Collectors.toList());
        if(versionList.size() >= 2) {
            List<String> propertyNames = versionList.stream()
                    .map(prop -> prop.getName())
                    .collect(Collectors.toList());

            throw new InvalidEntityException(entityClass, messageFormatter.create("entity.anno.multiPropertyAnno")
                    .paramWithClass("classType", entityClass)
                    .paramWithAnno("anno", Version.class)
                    .param("propertyNames", propertyNames)
                    .format());
        }

        // バージョンキーを持つとき主キーが存在するかどうか
        if(versionList.size() > 0) {
            boolean existsId = propertyMetaList.stream()
                    .anyMatch(prop -> prop.isId());
            if(!existsId) {
                throw new InvalidEntityException(entityClass, messageFormatter.create("entity.noIdWhenVersion")
                        .paramWithClass("classType", entityClass)
                        .format());
            }
        }

    }
}
