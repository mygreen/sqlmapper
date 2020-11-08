package com.github.mygreen.sqlmapper.core.where.metamodel;

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
import com.github.mygreen.sqlmapper.core.testdata.Customer;
import com.github.mygreen.sqlmapper.core.testdata.TestConfig;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.LocalDatePath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.Predicate;
import com.github.mygreen.sqlmapper.metamodel.StringPath;


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
        MetamodelWhereVisitor visitor = new MetamodelWhereVisitor(entityMeta, dialect);
        visitor.visit(new MetamodelWhere(condition));

        String sql = visitor.getCriteria();
        System.out.println(sql);

        List<Object> params = visitor.getParamValues();


    }

    /**
     * エンティティのメタモデル
     *
     */
    static class MCustomer extends EntityPathBase<Customer> {

        public static final MCustomer customer = new MCustomer("customer");

        public MCustomer(Class<? extends Customer> type, String name) {
            super(type, name);
        }

        public MCustomer(String name) {
            super(Customer.class, name);
        }

        public final StringPath id = createString("id");

        public final StringPath firstName = createString("firstName");

        public final StringPath lastName = createString("lastName");

        public final LocalDatePath birthday = createLocalDate("birthday");

        public final NumberPath<Long> version = createNumber("version", Long.class);



    }
}
