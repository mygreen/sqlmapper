package com.github.mygreen.sqlmapper.where;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.meta.EntityMeta;
import com.github.mygreen.sqlmapper.meta.EntityMetaFactory;
import com.github.mygreen.sqlmapper.testdata.Customer;
import com.github.mygreen.sqlmapper.testdata.TestConfig;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=TestConfig.class)
public class WhereBuilderTest {

    @Autowired
    private EntityMetaFactory entityMetaFactory;

    @DisplayName("SimpleWhere - 単純なAND")
    @Test
    void testSimpleWhere_and() {

        SimpleWhere where = new SimpleWhere();
        where.eq("firstName", "Taro").eq("lastName", "Yamada");

        EntityMeta entityMeta = entityMetaFactory.create(Customer.class);

        WhereVisitor whereVisitor = new WhereVisitor(entityMeta);
        where.accept(whereVisitor);

        String sql = whereVisitor.getCriteria();
        assertEquals("FIRST_NAME = ? AND LAST_NAME = ?", sql);

    }

    @DisplayName("WhereBuilder - OR演算")
    @Test
    public void testWhereBuilder_or() {

        EntityMeta entityMeta = entityMetaFactory.create(Customer.class);

        WhereBuilder where = new WhereBuilder();
        where.eq("lastName", "Yamada").ge("birthday", LocalDate.of(2000, 8, 1)).or().starts("firstName", "T");

        WhereVisitor whereVisitor = new WhereVisitor(entityMeta);
        where.accept(whereVisitor);

        String sql = whereVisitor.getCriteria();
        assertEquals("(LAST_NAME = ? AND BIRTHDAY >= ?) OR (FIRST_NAME LIKE ?)", sql);


    }
}
