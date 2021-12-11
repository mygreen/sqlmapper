package com.github.mygreen.sqlmapper.core.test.entity.meta;

import com.github.mygreen.sqlmapper.core.test.entity.CustomerAddress;
import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

/**
 * {@link CustomerAddress}のメタモデルクラス
 *
 *
 * @author T.TSUCHIE
 *
 */
public class MCustomerAddress  extends EntityPathBase<CustomerAddress> {

    public static final MCustomerAddress customerAddress = new MCustomerAddress("customerAddress");

    public MCustomerAddress(Class<? extends CustomerAddress> type, String name) {
        super(type, name);
    }

    public MCustomerAddress(String name) {
        super(CustomerAddress.class, name);
    }

    public final StringPath customerId = createString("customerId");

    public final StringPath telNumber = createString("telNumber");

    public final StringPath address = createString("address");

    public final NumberPath<Long> version = createNumber("version", Long.class);

}
