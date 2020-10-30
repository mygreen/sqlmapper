package com.github.mygreen.sqlmapper.core.where.simple;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.meta.EntityMeta;
import com.github.mygreen.sqlmapper.core.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.core.testdata.Customer;
import com.github.mygreen.sqlmapper.core.testdata.TestConfig;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class SimpleWhereBuilderTest {

    @Autowired
    private EntityMetaFactory entityMetaFactory;

    @DisplayName("SimpleWhere - 単純なAND")
    @Test
    void testSimpleWhere_and() {

        SimpleWhere where = new SimpleWhere();
        where.eq("firstName", "Taro").eq("lastName", "Yamada");

        EntityMeta entityMeta = entityMetaFactory.create(Customer.class);

        SimpleWhereVisitor whereVisitor = new SimpleWhereVisitor(entityMeta);
        where.accept(whereVisitor);

        String sql = whereVisitor.getCriteria();
        assertEquals("FIRST_NAME = ? AND LAST_NAME = ?", sql);

    }

    @DisplayName("WhereBuilder - OR演算")
    @Test
    public void testWhereBuilder_or() {

        EntityMeta entityMeta = entityMetaFactory.create(Customer.class);

        SimpleWhereBuilder where = new SimpleWhereBuilder();
        where.eq("lastName", "Yamada").ge("birthday", LocalDate.of(2000, 8, 1)).or().starts("firstName", "T");

        SimpleWhereVisitor whereVisitor = new SimpleWhereVisitor(entityMeta);
        where.accept(whereVisitor);

        String sql = whereVisitor.getCriteria();
        assertEquals("(LAST_NAME = ? AND BIRTHDAY >= ?) OR (FIRST_NAME LIKE ?)", sql);


    }
}
