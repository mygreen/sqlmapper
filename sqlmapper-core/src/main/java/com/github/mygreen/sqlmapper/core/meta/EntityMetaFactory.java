package com.github.mygreen.sqlmapper.core.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import com.github.mygreen.messageformatter.MessageFormatter;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.MappedSuperclass;
import com.github.mygreen.sqlmapper.core.annotation.Table;
import com.github.mygreen.sqlmapper.core.annotation.Version;
import com.github.mygreen.sqlmapper.core.naming.NamingRule;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * エンティティのメタ情報を作成します。
 *
 *
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
        extractProperty(entityClass, propertyMetaList, entityMeta);
        extractSuperClassProperty(entityClass.getSuperclass(), propertyMetaList, entityMeta);

        validateEntity(entityClass, propertyMetaList);

        propertyMetaList.stream().forEach(p -> entityMeta.addPropertyMeta(p));

    }

    /**
     * 指定したクラスからプロパティを抽出する。
     * @param targetClass 抽出対象のクラス。
     * @param propertyMetaList 抽出したプロパティ一覧
     * @param entityMeta エンティティのメタ情報
     */
    private void extractProperty(final Class<?> targetClass, final List<PropertyMeta> propertyMetaList,
            final EntityMeta entityMeta) {

        for(Field field : targetClass.getDeclaredFields()) {

            int modifiers = field.getModifiers();
            if(Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }

            ReflectionUtils.makeAccessible(field);

            propertyMetaList.add(propertyMetaFactory.create(field, entityMeta));

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
            extractProperty(superClass, propertyMetaList, entityMeta);
        }

        // 再帰的に遡っていく
        extractSuperClassProperty(superClass.getSuperclass(), propertyMetaList, entityMeta);

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

        }
        if(douplicatedColumnPropertyNames.size() > 0) {
            throw new InvalidEntityException(entityClass, messageFormatter.create("entity.prop.columnDuplicated")
                    .paramWithClass("classType", entityClass)
                    .param("propertyNames", douplicatedColumnPropertyNames)
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
