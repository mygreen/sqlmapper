package com.github.mygreen.sqlmapper.core.where.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.core.test.config.TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.EmbeddedEntity;
import com.github.mygreen.sqlmapper.core.test.entity.EntityChild;
import com.github.mygreen.sqlmapper.core.test.entity.MCustomer;
import com.github.mygreen.sqlmapper.core.test.entity.MEmbeddedEntity;
import com.github.mygreen.sqlmapper.core.test.entity.MEntityChild;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.support.SubQueryHelper;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class MetamodelWhereBuilderTest {

    @Autowired
    private EntityMetaFactory entityMetaFactory;

    @Autowired
    private Dialect dialect;

    @Test
    void test() {

        MCustomer entity = MCustomer.customer;
        Predicate condition = entity.firstName.lower().contains("taro")
                .and(entity.lastName.eq("Yamada"))
                .or(entity.birthday.after(LocalDate.of(2000, 1, 1)).and(entity.version.between(0L, 100L)))
                ;

        EntityMeta entityMeta = entityMetaFactory.create(Customer.class);

        TableNameResolver tableNameResolver = new TableNameResolver();
        tableNameResolver.prepareTableAlias(entity);

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(Map.of(entityMeta.getEntityType(), entityMeta),
                 dialect, entityMetaFactory, tableNameResolver);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(lower(T1_.FIRST_NAME) like ? and T1_.LAST_NAME = ?) or (T1_.BIRTHDAY > ? and (T1_.VERSION between ? and ?))");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", "Yamada", LocalDate.of(2000, 1, 1), 0L, 100L);

    }

    @Test
    void testLikeEscape() {

        MCustomer entity = MCustomer.customer;
        Predicate condition = entity.firstName.contains("t_ar%o", '$')
                .and(entity.lastName.starts("Ya%ma_da", '@'));

        EntityMeta entityMeta = entityMetaFactory.create(Customer.class);

        TableNameResolver tableNameResolver = new TableNameResolver();
        tableNameResolver.prepareTableAlias(entity);

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(Map.of(entityMeta.getEntityType(), entityMeta),
                 dialect, entityMetaFactory, tableNameResolver);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.FIRST_NAME like ? escape '$' and T1_.LAST_NAME like ? escape '@'");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%t$_ar$%o%", "Ya@%ma@_da%");

    }

    @Test
    void testStringConcat() {

        MCustomer entity = MCustomer.customer;
        Predicate condition = entity.firstName.concat(entity.lastName).like("yama");

        EntityMeta entityMeta = entityMetaFactory.create(Customer.class);

        TableNameResolver tableNameResolver = new TableNameResolver();
        tableNameResolver.prepareTableAlias(entity);

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(Map.of(entityMeta.getEntityType(), entityMeta),
                 dialect, entityMetaFactory, tableNameResolver);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("concat(T1_.FIRST_NAME, T1_.LAST_NAME) like ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("yama");

    }

    @Test
    void test_arithmetic() {
        MCustomer entity = MCustomer.customer;
        Predicate condition = entity.firstName.lower().contains("taro")
                .and(entity.version.add(1L).gt(2L));

        EntityMeta entityMeta = entityMetaFactory.create(Customer.class);

        TableNameResolver tableNameResolver = new TableNameResolver();
        tableNameResolver.prepareTableAlias(entity);

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(Map.of(entityMeta.getEntityType(), entityMeta),
                 dialect, entityMetaFactory, tableNameResolver);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("lower(T1_.FIRST_NAME) like ? and (T1_.VERSION + ?) > ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", 1L, 2L);
    }

    @Test
    void testInheritance() {

        MEntityChild entity = MEntityChild.entityChild;
        Predicate condition = entity.name.contains("Yamada")
                .and(entity.createAt.goe(Timestamp.valueOf("2021-01-01 12:13:14.123")));

        EntityMeta entityMeta = entityMetaFactory.create(EntityChild.class);

        TableNameResolver tableNameResolver = new TableNameResolver();
        tableNameResolver.prepareTableAlias(entity);

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(Map.of(entityMeta.getEntityType(), entityMeta),
                dialect, entityMetaFactory, tableNameResolver);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME like ? and T1_.CREATE_AT >= ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%Yamada%", Timestamp.valueOf("2021-01-01 12:13:14.123"));

    }

    @Test
    void testEmbeddedId() {

        MEmbeddedEntity entity = MEmbeddedEntity.embeddedEntity;

        Predicate condition = entity.id.key1.eq("1")
                .and(entity.name.contains("Yamada"));

        EntityMeta entityMeta = entityMetaFactory.create(EmbeddedEntity.class);

        TableNameResolver tableNameResolver = new TableNameResolver();
        tableNameResolver.prepareTableAlias(entity);

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(Map.of(entityMeta.getEntityType(), entityMeta),
                dialect, entityMetaFactory, tableNameResolver);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.KEY1 = ? and T1_.NAME like ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("1", "%Yamada%");
    }

    @Test
    void testSubQuery() {

        MCustomer entity = MCustomer.customer;
        Predicate condition = entity.firstName.contains("taro")
                .and(SubQueryHelper.from(entity).where(entity.birthday.after(LocalDate.of(2000, 1, 1))).exists());

        EntityMeta entityMeta = entityMetaFactory.create(Customer.class);

        TableNameResolver tableNameResolver = new TableNameResolver();
        tableNameResolver.prepareTableAlias(entity);

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(Map.of(entityMeta.getEntityType(), entityMeta),
                 dialect, entityMetaFactory, tableNameResolver);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.FIRST_NAME like ? and exists (select T1_.customer_id, T1_.FIRST_NAME, T1_.LAST_NAME, T1_.BIRTHDAY, T1_.VERSION from CUSTOMER T1_ where T1_.BIRTHDAY > ?)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", LocalDate.of(2000, 1, 1));


    }

    @Test
    void testCustomFunction() {
        MCustomer entity = MCustomer.customer;
        Predicate condition = entity.firstName.function("custom_format($left, 'yyyMMdd')").returnString()
                .function("custom_is_valid($left, ?)", "TEST").returnBoolean();

        EntityMeta entityMeta = entityMetaFactory.create(Customer.class);

        TableNameResolver tableNameResolver = new TableNameResolver();
        tableNameResolver.prepareTableAlias(entity);

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(Map.of(entityMeta.getEntityType(), entityMeta),
                 dialect, entityMetaFactory, tableNameResolver);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("custom_is_valid(custom_format(T1_.FIRST_NAME, 'yyyMMdd'), ?)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("TEST");

    }

}
