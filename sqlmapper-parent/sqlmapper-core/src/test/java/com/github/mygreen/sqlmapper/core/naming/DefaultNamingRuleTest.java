package com.github.mygreen.sqlmapper.core.naming;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DefaultNamingRuleTest extends DefaultNamingRule {

    @Test
    void testEntityToTable() {

        assertEquals("EMPLOYEE", entityToTable("sample.entity.Employee"));

        assertEquals("EMPLOYEE", entityToTable("Employee"));

        assertEquals("EMPLOYEE_DETAIL", entityToTable("EmployeeDetail"));

    }

    @Test
    void testPropertyToColumn() {

        assertEquals("ID", propertyToColumn("id"));
        assertEquals("FIRST_NAME", propertyToColumn("firstName"));
    }

    @Test
    void testColumnToProperty() {

        assertEquals("id", columnToProperty("ID"));
        assertEquals("id", columnToProperty("id"));

        assertEquals("firstName", columnToProperty("FIRST_NAME"));
        assertEquals("firstName", columnToProperty("first_name"));

    }
}
