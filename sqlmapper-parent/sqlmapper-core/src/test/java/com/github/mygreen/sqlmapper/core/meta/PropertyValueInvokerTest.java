package com.github.mygreen.sqlmapper.core.meta;

import static org.assertj.core.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.test.config.NoDbTestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.EmbeddedTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.InheritanceTestEntity;

/**
 * {@link PropertyValueInvoker}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=NoDbTestConfig.class)
public class PropertyValueInvokerTest {

    @Autowired
    private EntityMetaFactory entityMetaFactory;

    @Test
    void testGetPropertyValue_innertClassEntity() {

        EntityMeta entityMeta = entityMetaFactory.create(EmbeddedTestEntity.class);

        EmbeddedTestEntity.PK entity = new EmbeddedTestEntity.PK("001", 1l);

        Object result = PropertyValueInvoker.getPropertyValue(entityMeta.getColumnPropertyMeta("key1").get(), entity);
        assertThat(result).isEqualTo("001");
    }

    @Test
    void testSetPropertyValue_innertClassEntity() {

        EntityMeta entityMeta = entityMetaFactory.create(EmbeddedTestEntity.class);

        EmbeddedTestEntity.PK entity = new EmbeddedTestEntity.PK();

        PropertyValueInvoker.setPropertyValue(entityMeta.getColumnPropertyMeta("key1").get(), entity, "001");
        assertThat(entity).hasFieldOrPropertyWithValue("key1", "001");
    }

    @Test
    void testGetEmbeddedPropertyValue_normalProperty() {

        EntityMeta entityMeta = entityMetaFactory.create(EmbeddedTestEntity.class);

        EmbeddedTestEntity entity = new EmbeddedTestEntity();
        entity.setId(new EmbeddedTestEntity.PK("001", 1l));
        entity.setName("001-1@test");

        Object result = PropertyValueInvoker.getEmbeddedPropertyValue(entityMeta.getColumnPropertyMeta("name").get(), entity);
        assertThat(result).isEqualTo("001-1@test");
    }

    @Test
    void testSetEmbeddedPropertyValue_normalProperty() {

        EntityMeta entityMeta = entityMetaFactory.create(EmbeddedTestEntity.class);

        EmbeddedTestEntity entity = new EmbeddedTestEntity();

        PropertyValueInvoker.setEmbeddedPropertyValue(entityMeta.getColumnPropertyMeta("name").get(), entity, "001-1@test");

        assertThat(entity).hasFieldOrPropertyWithValue("name", "001-1@test");
    }

    @DisplayName("埋め込みエンティティの取得")
    @Test
    void testGetEmbeddedPropertyValue_embeddedProperty() {

        EntityMeta entityMeta = entityMetaFactory.create(EmbeddedTestEntity.class);

        EmbeddedTestEntity entity = new EmbeddedTestEntity();
        entity.setId(new EmbeddedTestEntity.PK("001", 1l));
        entity.setName("001-1@test");

        Object result = PropertyValueInvoker.getEmbeddedPropertyValue(entityMeta.getColumnPropertyMeta("key1").get(), entity);
        assertThat(result).isEqualTo("001");
    }

    @DisplayName("埋め込みエンティティの取得 - 途中のエンティティがnullの場合")
    @Test
    void testGetEmbeddedPropertyValue_embeddedProperty_embeddedIsNull() {

        EntityMeta entityMeta = entityMetaFactory.create(EmbeddedTestEntity.class);

        EmbeddedTestEntity entity = new EmbeddedTestEntity();
        entity.setId(null);
        entity.setName("001-1@test");

        Object result = PropertyValueInvoker.getEmbeddedPropertyValue(entityMeta.getColumnPropertyMeta("key1").get(), entity);
        assertThat(result).isNull();
    }

    @DisplayName("埋め込みエンティティの設定")
    @Test
    void testSetEmbeddedPropertyValue_embeddedProperty() {

        EntityMeta entityMeta = entityMetaFactory.create(EmbeddedTestEntity.class);

        EmbeddedTestEntity entity = new EmbeddedTestEntity();
        entity.setId(new EmbeddedTestEntity.PK());

        PropertyValueInvoker.setEmbeddedPropertyValue(entityMeta.getColumnPropertyMeta("key1").get(), entity, "001");

        assertThat(entity.getId()).hasFieldOrPropertyWithValue("key1", "001");
    }

    @DisplayName("埋め込みエンティティの設定 - 途中のエンティティがnullの場合")
    @Test
    void testSetEmbeddedPropertyValue_embeddedProperty_ebmeddedIsNull() {

        EntityMeta entityMeta = entityMetaFactory.create(EmbeddedTestEntity.class);

        EmbeddedTestEntity entity = new EmbeddedTestEntity();

        PropertyValueInvoker.setEmbeddedPropertyValue(entityMeta.getColumnPropertyMeta("key1").get(), entity, "001");

        assertThat(entity.getId()).hasFieldOrPropertyWithValue("key1", "001");
    }

    @Test
    void testGetEmbeddedPropertyValue_inheritanceProperty() {

        EntityMeta entityMeta = entityMetaFactory.create(InheritanceTestEntity.class);

        InheritanceTestEntity entity = new InheritanceTestEntity();
        entity.setId(1l);
        entity.setBirthday(LocalDate.of(1990, 1, 10));
        entity.setCreateAt(Date.valueOf("2021-12-29"));

        {
            Object result = PropertyValueInvoker.getEmbeddedPropertyValue(entityMeta.getColumnPropertyMeta("id").get(), entity);
            assertThat(result).isEqualTo(1l);
        }

        {
            Object result = PropertyValueInvoker.getEmbeddedPropertyValue(entityMeta.getColumnPropertyMeta("create_at").get(), entity);
            assertThat(result).isEqualTo(Date.valueOf("2021-12-29"));
        }

    }

    @Test
    void testSetEmbeddedPropertyValue_inheritanceProperty() {

        EntityMeta entityMeta = entityMetaFactory.create(InheritanceTestEntity.class);

        InheritanceTestEntity entity = new InheritanceTestEntity();

        PropertyValueInvoker.setEmbeddedPropertyValue(entityMeta.getColumnPropertyMeta("id").get(), entity, 1l);
        PropertyValueInvoker.setEmbeddedPropertyValue(entityMeta.getColumnPropertyMeta("create_at").get(), entity, Date.valueOf("2021-12-29"));

        assertThat(entity).hasFieldOrPropertyWithValue("id", 1l)
            .hasFieldOrPropertyWithValue("createAt", Date.valueOf("2021-12-29"));

    }


}
