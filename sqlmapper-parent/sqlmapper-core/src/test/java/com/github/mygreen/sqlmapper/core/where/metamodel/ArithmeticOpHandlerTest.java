package com.github.mygreen.sqlmapper.core.where.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
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
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.Predicate;

import lombok.Data;

/**
 * {@link ArithmeticOpHandler}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=NoDbTestConfig.class)
public class ArithmeticOpHandlerTest {

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

        private int count;

        private BigDecimal price;

        private BigDecimal taxRate;

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

        public final NumberPath<Integer> count = createNumber("count", Integer.class);

        public final NumberPath<BigDecimal> price = createNumber("price", BigDecimal.class);

        public final NumberPath<BigDecimal> taxRate = createNumber("taxRate", BigDecimal.class);

    }

    @DisplayName("A + B")
    @Test
    void testAdd() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.count.add(1).eq(10);

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(T1_.COUNT + ?) = ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(1, 10);

    }

    @DisplayName("A - B")
    @Test
    void testSubstract() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.count.substract(1).eq(10);

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(T1_.COUNT - ?) = ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(1, 10);

    }

    @DisplayName("A * B")
    @Test
    void testMultiply() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.count.multiply(5).eq(10);

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(T1_.COUNT * ?) = ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(5, 10);

    }

    @DisplayName("A / B")
    @Test
    void tesDivide() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.count.divide(5).eq(10);

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(T1_.COUNT / ?) = ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(5, 10);

    }

    @DisplayName("A % B")
    @Test
    void tesModulo() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.count.mod(5).eq(10);

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(mod(T1_.COUNT, ?)) = ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(5, 10);

    }


    @DisplayName("A + B - C")
    @Test
    void testCombination1() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.price.add(entity.price.substract(new BigDecimal("100"))).goe(new BigDecimal("2000"));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(T1_.PRICE + T1_.PRICE - ?) >= ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(new BigDecimal("100"), new BigDecimal("2000"));

    }

    @DisplayName("A + B * C")
    @Test
    void testCombination2() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.price.add(entity.price.multiply(entity.taxRate)).goe(new BigDecimal("2000"));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(T1_.PRICE + T1_.PRICE * T1_.TAX_RATE) >= ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(new BigDecimal("2000"));

    }

    @DisplayName("(A + B) * C")
    @Test
    void testCombination3() {

        MTestEntity entity = MTestEntity.test;
        Predicate condition = entity.taxRate.add(new BigDecimal("1.0")).multiply(entity.price).goe(new BigDecimal("2000"));

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("((T1_.TAX_RATE + ?) * T1_.PRICE) >= ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly(new BigDecimal("1.0"), new BigDecimal("2000"));

    }

}
