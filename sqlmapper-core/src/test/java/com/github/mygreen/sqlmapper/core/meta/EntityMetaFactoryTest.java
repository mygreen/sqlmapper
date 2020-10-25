package com.github.mygreen.sqlmapper.meta;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.annotation.Column;
import com.github.mygreen.sqlmapper.annotation.Entity;
import com.github.mygreen.sqlmapper.annotation.Id;
import com.github.mygreen.sqlmapper.annotation.Transient;
import com.github.mygreen.sqlmapper.annotation.Version;
import com.github.mygreen.sqlmapper.testdata.NoDbTestConfig;

import lombok.Data;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=NoDbTestConfig.class)
public class EntityMetaFactoryTest {

    @Autowired
    private EntityMetaFactory entityMetaFactory;

    @Test
    void testStandard() {

        EntityMeta entityMeta = entityMetaFactory.create(StandardEntity.class);

        assertThat(entityMeta.getName()).isEqualTo("StandardEntity");

        TableMeta tableMeta = entityMeta.getTableMeta();
        assertThat(tableMeta.getName()).isEqualTo("STANDARD_ENTITY");
        assertThat(tableMeta.getSchema()).isNull();
        assertThat(tableMeta.getCatalog()).isNull();
        assertThat(tableMeta.getFullName()).isEqualTo("STANDARD_ENTITY");

        {
            PropertyMeta propertyMeta = entityMeta.getVersionPropertyMeta().get();
            assertThat(propertyMeta.getName()).isEqualTo("version");
            assertThat(propertyMeta.getPropertyType()).isEqualTo(long.class);

        }

        {
            PropertyMeta propertyMeta = entityMeta.getPropertyMeta("fullName").get();
            assertThat(propertyMeta.getName()).isEqualTo("fullName");
            assertThat(propertyMeta.getColumnMeta()).isNull();

        }

        assertThat(entityMeta.getAllColumnPropertyMeta()).hasSize(5);
        for(PropertyMeta propertyMeta : entityMeta.getAllColumnPropertyMeta()) {

            if(propertyMeta.getName().equals("id")) {
                assertThat(propertyMeta.isId()).isTrue();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("ID");

            } else if(propertyMeta.getName().equals("firstName")) {
                assertThat(propertyMeta.isId()).isFalse();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("name1");

            } else if(propertyMeta.getName().equals("lastName")) {
                assertThat(propertyMeta.isId()).isFalse();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("NAME2");

            } else if(propertyMeta.getName().equals("age")) {
                assertThat(propertyMeta.isId()).isFalse();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("AGE");

            } else if(propertyMeta.getName().equals("version")) {
                assertThat(propertyMeta.isId()).isFalse();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("VERSION");
                assertThat(propertyMeta.isVersion()).isTrue();
            }

        }

    }

    @Entity
    @Data
    public static class StandardEntity {

        @Id
        private long id;

        @Column(name = "name1")
        private String firstName;

        @Column(name = "NAME2")
        private String lastName;

        private Integer age;

        @Version
        private long version;

        @Transient
        private String fullName;

    }
}
