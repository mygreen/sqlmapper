package com.github.mygreen.sqlmapper.core.where.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.github.mygreen.sqlmapper.core.test.config.NoDbTestConfig;
import com.github.mygreen.sqlmapper.metamodel.EntityPath;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.LocalDatePath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

import lombok.Data;


/**
 * {@literal BooleanOpHandler} のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=NoDbTestConfig.class)
public class BooleanOpHandlerTest {

    @Autowired
    private EntityMetaFactory entityMetaFactory;

    @Autowired
    private Dialect dialect;

    private MetamodelWhereVisitor createVisitor(EntityPath<?> entityPath) {
        EntityMeta entityMeta = entityMetaFactory.create(entityPath.getType());

        TableNameResolver tableNameResolver = new TableNameResolver();
        tableNameResolver.prepareTableAlias(entityPath);

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(Map.of(entityMeta.getEntityType(), entityMeta),
                 dialect, entityMetaFactory, tableNameResolver);

        return visitor;

    }

    @Entity
    @Data
    public static class TestEntity {

        private long id;

        private String firstName;

        private String lastName;

        private LocalDateTime birthday;

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

        public final StringPath firstName = createString("firstName");

        public final StringPath lastName = createString("lastName");

        public final LocalDatePath birthday = createLocalDate("birthday");

    }

    @DisplayName("A and B")
    @Test
    public void testAnd() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.firstName.contains("taro")
                .and(entity.lastName.eq("Yamada"));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.FIRST_NAME like ? and T1_.LAST_NAME = ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", "Yamada");

    }

    @DisplayName("A and B and C")
    @Test
    public void testAnd2() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.firstName.contains("taro")
                .and(entity.lastName.eq("Yamada"))
                .and(entity.birthday.after(LocalDate.of(2000, 1, 2)));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.FIRST_NAME like ? and T1_.LAST_NAME = ? and T1_.BIRTHDAY > ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", "Yamada", LocalDate.of(2000, 1, 2));

    }

    @DisplayName("A or B")
    @Test
    public void testOr() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.firstName.contains("taro")
                .or(entity.lastName.eq("Yamada"));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.FIRST_NAME like ? or T1_.LAST_NAME = ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", "Yamada");
    }

    @DisplayName("A or B or C")
    @Test
    public void testOr2() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.firstName.contains("taro")
                .or(entity.lastName.eq("Yamada"))
                .or(entity.birthday.after(LocalDate.of(2000, 1, 2)));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.FIRST_NAME like ? or T1_.LAST_NAME = ? or T1_.BIRTHDAY > ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", "Yamada", LocalDate.of(2000, 1, 2));

    }

    @DisplayName("A and B or C")
    @Test
    public void testAndOr() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.firstName.contains("taro")
                .and(entity.lastName.eq("Yamada"))
                .or(entity.birthday.after(LocalDate.of(2000, 1, 2)));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(T1_.FIRST_NAME like ? and T1_.LAST_NAME = ?) or T1_.BIRTHDAY > ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", "Yamada", LocalDate.of(2000, 1, 2));

    }

    @Test
    public void testAndAnyOf() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.firstName.contains("taro")
                .andAnyOf(entity.lastName.eq("Yamada"), entity.birthday.after(LocalDate.of(2000, 1, 2)));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.FIRST_NAME like ? and (T1_.LAST_NAME = ? or T1_.BIRTHDAY > ?)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", "Yamada", LocalDate.of(2000, 1, 2));

    }

    @Test
    public void testOrAllOf() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.firstName.contains("taro")
                .orAllOf(entity.lastName.eq("Yamada"), entity.birthday.after(LocalDate.of(2000, 1, 2)));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.FIRST_NAME like ? or (T1_.LAST_NAME = ? and T1_.BIRTHDAY > ?)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", "Yamada", LocalDate.of(2000, 1, 2));

    }

}
