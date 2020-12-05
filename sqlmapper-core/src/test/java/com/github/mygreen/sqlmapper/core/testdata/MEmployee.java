package com.github.mygreen.sqlmapper.core.testdata;

import com.github.mygreen.sqlmapper.metamodel.EntityPathBase;
import com.github.mygreen.sqlmapper.metamodel.EnumPath;
import com.github.mygreen.sqlmapper.metamodel.NumberPath;
import com.github.mygreen.sqlmapper.metamodel.StringPath;

public class MEmployee extends EntityPathBase<Employee> {

    public static final MEmployee employee = new MEmployee("employee");

    public MEmployee(Class<? extends Employee> type, String name) {
        super(type, name);
    }

    public MEmployee(String name) {
        super(Employee.class, name);
    }

    public final StringPath id = createString("id");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final NumberPath<Long> version = createNumber("version", Long.class);
}
