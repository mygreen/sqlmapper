
package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.Customer;
import com.github.mygreen.sqlmapper.core.test.entity.type.GenderType;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.EnumPath;
import com.github.mygreen.sqlmapper.metamodel.LocalDatePath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

/**
 * {@link Customer}のメタモデルクラス
 *
 *
 * @author T.TSUCHIE
 *
 */
public class MCustomer extends EntityPathBase<Customer> {

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

    public final EnumPath<GenderType> genderType = createEnum("genderType", GenderType.class);

    public final NumberPath<Long> version = createNumber("version", Long.class);
}
