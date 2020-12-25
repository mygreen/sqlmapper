package com.github.mygreen.sqlmapper.core.where.metamodel;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.dialect.Dialect;
import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.query.TableNameResolver;
import com.github.mygreen.sqlmapper.core.testdata.Customer;
import com.github.mygreen.sqlmapper.core.testdata.MCustomer;
import com.github.mygreen.sqlmapper.core.testdata.TestConfig;
import com.github.mygreen.sqlmapper.metamodel.Predicate;


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
//        tableNameResolver.prepareTableAlias(entity);

        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(entityMeta, dialect, tableNameResolver);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        assertThat(sql).isEqualTo("(LOWER(FIRST_NAME) LIKE ? AND LAST_NAME = ?) OR (BIRTHDAY > ? AND (VERSION BETWEEN ? AND ?))");
//        System.out.println(sql);

        List<Object> params = visitor.getParamValues();
        assertThat(params).containsExactly("%taro%", "Yamada", LocalDate.of(2000, 1, 1), 0L, 100L);

    }

}
