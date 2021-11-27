package com.github.mygreen.sqlmapper.core.where.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.core.test.config.TestConfig;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

import lombok.Data;

/**
 * {@link LikeOpHandler}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class LikeOpHandlerTest {

    @Autowired
    private EntityMetaFactory entityMetaFactory;

    @Autowired
    private Dialect dialect;


    private MetamodelWhereVisitor createVisitor(EntityPath<?>... entityPaths) {
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

    @Entity
    @Data
    public static class TestEntity {

        private long id;

        private String name;

        private String address;

    }

    public static class MTestEntity extends EntityPathBase<TestEntity> {

        public static final MTestEntity test = new MTestEntity("test");

        public MTestEntity(Class<? extends TestEntity> type, String name) {
            super(type, name);
        }

        public MTestEntity(String name) {
            super(TestEntity.class, name);
        }

        public final NumberPath<Long> id = createNumber("id", Long.class);

        public final StringPath name = createString("name");

        public final StringPath address = createString("address");

    }

    @DisplayName("A like '%B%'")
    @Test
    public void testContains() {

        MTestEntity entity = MTestEntity.test;

        Predicate condition = entity.name.contains("Ya%ma_da").or(entity.address.contains("I_zu%mo", '@'));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME like ? or T1_.ADDRESS like ? escape '@'");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%Ya\\%ma\\_da%", "%I@_zu@%mo%");

    }

    @DisplayName("A like 'B%'")
    @Test
    public void testStarts() {

        MTestEntity entity = MTestEntity.test;

        Predicate condition = entity.name.starts("Ya%ma_da").or(entity.address.starts("I_zu%mo", '@'));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME like ? or T1_.ADDRESS like ? escape '@'");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("Ya\\%ma\\_da%", "I@_zu@%mo%");

    }

    @DisplayName("A like '%B'")
    @Test
    public void testEnds() {

        MTestEntity entity = MTestEntity.test;

        Predicate condition = entity.name.ends("Ya%ma_da").or(entity.address.ends("I_zu%mo", '@'));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME like ? or T1_.ADDRESS like ? escape '@'");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%Ya\\%ma\\_da", "%I@_zu@%mo");

    }

    @DisplayName("A like B")
    @Test
    public void testLike() {

        MTestEntity entity = MTestEntity.test;

        Predicate condition = entity.name.like("%I@_zu@%mo", '@').or(entity.address.like(entity.name.lower()));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME like ? escape '@' or T1_.ADDRESS like lower(T1_.NAME)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%I@_zu@%mo");

    }

}
