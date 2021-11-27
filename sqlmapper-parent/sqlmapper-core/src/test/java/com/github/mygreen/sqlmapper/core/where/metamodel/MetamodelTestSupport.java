package com.github.mygreen.sqlmapper.core.where.metamodel;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;

/**
 * メタモデルのテストサポートをするクラス
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class MetamodelTestSupport {

    @Autowired
    protected EntityMetaFactory entityMetaFactory;

    @Autowired
    protected Dialect dialect;

    /**
     * エンティティパスから、{@link MetamodelWhereVisitor}を作成します。
     * @param entityPaths 条件式に含まれるエンティティパス。
     * @return {@link MetamodelWhereVisitor}のインスタンス
     */
    protected MetamodelWhereVisitor createVisitor(EntityPath<?>... entityPaths) {
        Map<Class<?>, EntityMeta> entityMetaMap = new HashMap<>();
        TableNameResolver tableNameResolver = new TableNameResolver();

        for(EntityPath<?> entityPath : entityPaths) {
            EntityMeta entityMeta = entityMetaFactory.create(entityPath.getType());
            tableNameResolver.prepareTableAlias(entityPath);
            entityMetaMap.put(entityPath.getType(), entityMeta);
        }

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(entityMetaMap, dialect, entityMetaFactory, tableNameResolver);

        return visitor;

    }
}
