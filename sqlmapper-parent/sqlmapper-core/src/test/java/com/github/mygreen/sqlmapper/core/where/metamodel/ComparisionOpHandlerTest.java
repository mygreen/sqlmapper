package com.github.mygreen.sqlmapper.core.where.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.sql.Date;
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
 * {@link ComparisionOpHandler}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=NoDbTestConfig.class)
public class ComparisionOpHandlerTest extends MetamodelTestSupport {

    @Entity
    @Data
    public static class TestEntity {

        private long id;

        private int point;

        private String name;

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

        public final NumberPath<Integer> point = createNumber("point", Integer.class);

        public final StringPath name = createString("name");

        public final LocalDatePath birthday = createLocalDate("birthday");

    }

    @DisplayName("A = B")
    @Test
    public void testEq() {

        MTestEntity entity = MTestEntity.test;
        MTestEntity entity2 = new MTestEntity("test2");

        Predicate condition = entity.name.eq("Yamada").or(entity.point.eq(entity2.point));

        MetamodelWhereVisitor visitor = createVisitor(entity, entity2);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME = ? or T1_.POINT = T2_.POINT");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("Yamada");

    }

    @DisplayName("A != B")
    @Test
    public void testNotEq() {

        MTestEntity entity = MTestEntity.test;
        MTestEntity entity2 = new MTestEntity("test2");

        Predicate condition = entity.name.ne("Yamada").or(entity.point.ne(entity2.point));

        MetamodelWhereVisitor visitor = createVisitor(entity, entity2);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME <> ? or T1_.POINT <> T2_.POINT");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("Yamada");

    }


    @DisplayName("A >= B")
    @Test
    public void testGoe() {

        MTestEntity entity = MTestEntity.test;
        MTestEntity entity2 = new MTestEntity("test2");

        Predicate condition = entity.name.goe("Yamada").or(entity.point.goe(entity2.point));

        MetamodelWhereVisitor visitor = createVisitor(entity, entity2);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME >= ? or T1_.POINT >= T2_.POINT");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("Yamada");

    }

    @DisplayName("A > B")
    @Test
    public void testGt() {

        MTestEntity entity = MTestEntity.test;
        MTestEntity entity2 = new MTestEntity("test2");

        Predicate condition = entity.name.gt("Yamada").or(entity.point.gt(entity2.point));

        MetamodelWhereVisitor visitor = createVisitor(entity, entity2);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME > ? or T1_.POINT > T2_.POINT");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("Yamada");

    }

    @DisplayName("A <= B")
    @Test
    public void testLoe() {

        MTestEntity entity = MTestEntity.test;
        MTestEntity entity2 = new MTestEntity("test2");

        Predicate condition = entity.name.loe("Yamada").or(entity.point.loe(entity2.point));

        MetamodelWhereVisitor visitor = createVisitor(entity, entity2);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME <= ? or T1_.POINT <= T2_.POINT");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("Yamada");

    }

    @DisplayName("A <= B")
    @Test
    public void testLt() {

        MTestEntity entity = MTestEntity.test;
        MTestEntity entity2 = new MTestEntity("test2");

        Predicate condition = entity.name.lt("Yamada").or(entity.point.lt(entity2.point));

        MetamodelWhereVisitor visitor = createVisitor(entity, entity2);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME < ? or T1_.POINT < T2_.POINT");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("Yamada");

    }

    @DisplayName("A in (B, C, D)")
    @Test
    public void testIn() {

        MTestEntity entity = MTestEntity.test;
        MTestEntity entity2 = new MTestEntity("test2");
        SubQueryExpression<Integer> subQuery = SubQueryHelper.from(entity2, entity2.point).where(entity2.birthday.after(LocalDate.of(2020, 1, 1)));

        Predicate condition = entity.name.in("Yamada", "Tanaka", "Suzuki").or(entity.point.in(subQuery));

        MetamodelWhereVisitor visitor = createVisitor(entity, entity2);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME in (?, ?, ?) or T1_.POINT in (select T2_.POINT from TEST_ENTITY T2_ where T2_.BIRTHDAY > ?)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("Yamada", "Tanaka", "Suzuki", Date.valueOf(LocalDate.of(2020, 1, 1)));

    }

    @DisplayName("A not in (B, C, D)")
    @Test
    public void testNotIn() {

        MTestEntity entity = MTestEntity.test;
        MTestEntity entity2 = new MTestEntity("test2");
        SubQueryExpression<Integer> subQuery = SubQueryHelper.from(entity2, entity2.point).where(entity2.birthday.after(LocalDate.of(2020, 1, 1)));

        Predicate condition = entity.name.notIn("Yamada", "Tanaka", "Suzuki").or(entity.point.notIn(subQuery));

        MetamodelWhereVisitor visitor = createVisitor(entity, entity2);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME not in (?, ?, ?) or T1_.POINT not in (select T2_.POINT from TEST_ENTITY T2_ where T2_.BIRTHDAY > ?)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("Yamada", "Tanaka", "Suzuki", Date.valueOf(LocalDate.of(2020, 1, 1)));

    }

    @DisplayName("A between B and C")
    @Test
    public void testBetween() {

        MTestEntity entity = MTestEntity.test;
        MTestEntity entity2 = new MTestEntity("test2");

        Predicate condition = entity.point.between(5, 10).or(entity.point.between(entity2.point, entity2.point));

        MetamodelWhereVisitor visitor = createVisitor(entity, entity2);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(T1_.POINT between ? and ?) or (T1_.POINT between T2_.POINT and T2_.POINT)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(5, 10);

    }

}
