package com.github.mygreen.sqlmapper.core.meta;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.annotation.Column;
import com.github.mygreen.sqlmapper.core.annotation.Embeddable;
import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.MappedSuperclass;
import com.github.mygreen.sqlmapper.core.annotation.Transient;
import com.github.mygreen.sqlmapper.core.annotation.Version;
import com.github.mygreen.sqlmapper.core.testdata.EntityBase;
import com.github.mygreen.sqlmapper.core.testdata.NoDbTestConfig;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


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
        int assertCount = 0;
        for(PropertyMeta propertyMeta : entityMeta.getAllColumnPropertyMeta()) {

            if(propertyMeta.getName().equals("id")) {
                assertThat(propertyMeta.isId()).isTrue();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("ID");
                assertCount++;

            } else if(propertyMeta.getName().equals("firstName")) {
                assertThat(propertyMeta.isId()).isFalse();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("name1");
                assertCount++;

            } else if(propertyMeta.getName().equals("lastName")) {
                assertThat(propertyMeta.isId()).isFalse();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("NAME2");
                assertCount++;

            } else if(propertyMeta.getName().equals("age")) {
                assertThat(propertyMeta.isId()).isFalse();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("AGE");
                assertCount++;

            } else if(propertyMeta.getName().equals("version")) {
                assertThat(propertyMeta.isId()).isFalse();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("VERSION");
                assertThat(propertyMeta.isVersion()).isTrue();
                assertCount++;
            }

        }

        assertThat(assertCount).isEqualTo(5);

    }

    @Test
    void testEmbedded() {

        EntityMeta entityMeta = entityMetaFactory.create(EmbeddedEntity.class);

        assertThat(entityMeta.getName()).isEqualTo("EmbeddedEntity");

        TableMeta tableMeta = entityMeta.getTableMeta();
        assertThat(tableMeta.getName()).isEqualTo("EMBEDDED_ENTITY");

        assertThat(entityMeta.getVersionPropertyMeta()).isEmpty();

        assertThat(entityMeta.getIdPropertyMetaList()).hasSize(2);
        for(PropertyMeta propertyMeta : entityMeta.getIdPropertyMetaList()) {

            assertThat(propertyMeta.getParent().getName()).isEqualTo("id");

            if(propertyMeta.getName().equals("id1")) {
                assertThat(propertyMeta.isId()).isTrue();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("ID1");
                assertThat(propertyMeta.isEmbedded()).isFalse();

            } else if(propertyMeta.getName().equals("id2")) {
                assertThat(propertyMeta.isId()).isTrue();
                assertThat(propertyMeta.getColumnMeta().getName()).isEqualTo("pk2");
                assertThat(propertyMeta.isEmbedded()).isFalse();

            }

        }

    }

    @Test
    void testInheritance() {

        EntityMeta entityMeta = entityMetaFactory.create(InheritanceEntity.class);

        // プロパティの属性のチェック
        assertThat(entityMeta.getAllColumnPropertyMeta()).hasSize(5);

        int assertCount = 0;
        for(PropertyMeta propertyMeta : entityMeta.getAllColumnPropertyMeta()) {

            if(propertyMeta.getName().equals("id")) {
                assertThat(propertyMeta.isId()).isTrue();
                assertThat(propertyMeta.getDeclaringClass()).isEqualTo(InheritanceBaseEntity.class);

                assertCount++;

            } else if(propertyMeta.getName().equals("code")) {
                assertThat(propertyMeta.getDeclaringClass()).isEqualTo(InheritanceBaseEntity.class);

                assertCount++;

            } else if(propertyMeta.getName().equals("createAt")) {
                assertThat(propertyMeta.isCreatedAt()).isTrue();

                assertThat(propertyMeta.getDeclaringClass()).isEqualTo(EntityBase.class);

                assertCount++;


            } else if(propertyMeta.getName().equals("updateAt")) {
                assertThat(propertyMeta.isUpdatedAt()).isTrue();
                assertThat(propertyMeta.getDeclaringClass()).isEqualTo(EntityBase.class);

                assertCount++;


            } else if(propertyMeta.getName().equals("version")) {
                assertThat(propertyMeta.isVersion()).isTrue();
                assertThat(propertyMeta.getDeclaringClass()).isEqualTo(EntityBase.class);

                assertCount++;
            }

        }

        assertThat(assertCount).isEqualTo(5);


        // プロパティの参照／更新のチェック
        PropertyMeta codePropertyMeta = entityMeta.getColumnPropertyMeta("code").get();
        {

            InheritanceEntity entity = new InheritanceEntity();
            entity.setCode("abc");

            Object value = PropertyValueInvoker.getEmbeddedPropertyValue(codePropertyMeta, entity);
            assertThat(value).isEqualTo("abc");

        }

        {
            InheritanceEntity entity = new InheritanceEntity();
            PropertyValueInvoker.setEmbeddedPropertyValue(codePropertyMeta, entity, "abc");

            assertThat(entity.getCode()).isEqualTo("abc");

        }

    }

    /**
     * 継承なしの通常のエンティティ
     *
     */
    @Entity
    @Data
    static class StandardEntity {

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

    /**
     * 埋め込み型のエンティティ
     *
     */
    @Entity
    @Data
    public static class EmbeddedEntity {

        @EmbeddedId
        private PK id;

        private String name;

        @Embeddable
        @Data
        public static class PK {
            private String id1;

            @Column(name = "pk2")
            private long id2;
        }

    }

    /**
     * 継承したエンティティ
     * GAPパターン用のベースクラス。
     *
     */
    @MappedSuperclass
    static abstract class InheritanceBaseEntity extends EntityBase {

        @Getter
        @Setter
        @Id
        private long id;

        @Getter
        @Setter
        private String code;

    }

    /**
     * 継承したエンティティ
     * 最終的な実装用のエンティティ
     *
     */
    @Entity
    static class InheritanceEntity extends InheritanceBaseEntity {

        @Transient
        private Map<String, Integer> map;

    }

}
