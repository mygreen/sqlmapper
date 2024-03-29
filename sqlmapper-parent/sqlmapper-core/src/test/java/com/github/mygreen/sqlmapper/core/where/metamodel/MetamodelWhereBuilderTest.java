package com.github.mygreen.sqlmapper.core.where.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.core.test.config.NoDbTestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MCustomer;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MEmbeddedTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MInheritanceTestEntity;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.support.SubQueryHelper;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=NoDbTestConfig.class)
public class MetamodelWhereBuilderTest extends MetamodelTestSupport {

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
        assertThat(params).containsExactly("%taro%", "Yamada", Date.valueOf(LocalDate.of(2000, 1, 1)), 0L, 100L);

    }

    @Test
    void testInheritance() {

        MInheritanceTestEntity entity = MInheritanceTestEntity.testInheritance;
        Predicate condition = entity.name.contains("Yamada")
                .and(entity.createAt.goe(Timestamp.valueOf("2021-01-01 12:13:14.123")));

                MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.NAME like ? and T1_.CREATE_AT >= ?");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%Yamada%", Timestamp.valueOf("2021-01-01 12:13:14.123"));

    }

    @Test
    void testEmbeddedId() {

        MEmbeddedTestEntity entity = MEmbeddedTestEntity.testEmbedded;

        Predicate condition = entity.id.key1.eq("1")
                .and(entity.name.contains("Yamada"));

        MetamodelWhereVisitor visitor = createVisitor(entity);
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

        MetamodelWhereVisitor visitor = createVisitor(entity);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("T1_.FIRST_NAME like ? and exists (select T1_.customer_id, T1_.FIRST_NAME, T1_.LAST_NAME, T1_.BIRTHDAY, T1_.GENDER_TYPE, T1_.VERSION from CUSTOMER T1_ where T1_.BIRTHDAY > ?)");

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", Date.valueOf(LocalDate.of(2000, 1, 1)));


    }

}
