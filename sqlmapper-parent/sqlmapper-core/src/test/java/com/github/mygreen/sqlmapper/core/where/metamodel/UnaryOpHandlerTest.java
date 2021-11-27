package com.github.mygreen.sqlmapper.core.where.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.test.config.NoDbTestConfig;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.LocalDatePath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.StringPath;
import com.github.mygreen.sqlmapper.metamodel.expression.SubQueryExpression;
import com.github.mygreen.sqlmapper.metamodel.support.SubQueryHelper;

import lombok.Data;


/**
 * {@linke UnaryOpHandler} のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=NoDbTestConfig.class)
public class UnaryOpHandlerTest extends MetamodelTestSupport {

    @Entity
    @Data
    public static class TestEntity {

        private long id;

        private String name;

        private BigDecimal salary;

        private LocalDate birthday;

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

        public final NumberPath<BigDecimal> salary = createNumber("salary", BigDecimal.class);

        public final LocalDatePath birthday = createLocalDate("birthday");

    }

    @DisplayName("not (A = B)")
    @Test
    public void testNot() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.name.eq("Yamada").not().or(entity.birthday.after(LocalDate.of(2000, 1, 1)).not().not());

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("not (T1_.NAME = ?) or not (not (T1_.BIRTHDAY > ?))");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("Yamada", LocalDate.of(2000, 1, 1));

    }

    @DisplayName("A is null")
    @Test
    public void testIsNull() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.name.isNull();

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME is null");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly();

    }

    @DisplayName("A is not null")
    @Test
    public void testIsNotNull() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.name.isNotNull();

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME is not null");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly();

    }

    @DisplayName("exists (A)")
    @Test
    public void testExists() {

        MTestEntity entity = MTestEntity.test;
        SubQueryExpression<?> subQuery = SubQueryHelper.from(entity, entity.id).where(entity.birthday.after(LocalDate.of(2020, 1, 1)));
        Predicate condition = subQuery.exists();

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("exists (select T1_.ID from TEST_ENTITY T1_ where T1_.BIRTHDAY > ?)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(LocalDate.of(2020, 1, 1));

    }

    @DisplayName("not exists (A)")
    @Test
    public void testNotExists() {

        MTestEntity entity = MTestEntity.test;
        SubQueryExpression<?> subQuery = SubQueryHelper.from(entity, entity.id).where(entity.birthday.after(LocalDate.of(2020, 1, 1)));
        Predicate condition = subQuery.notExists();

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("not exists (select T1_.ID from TEST_ENTITY T1_ where T1_.BIRTHDAY > ?)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(LocalDate.of(2020, 1, 1));

    }

}
