package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.EmbeddedTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MEmbeddedTestEntity;

/**
 * 自動組み立てによる埋め込み型のエンティティのテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoEmbeddedEntityTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_embedded.sql");
    }

    @Test
    void testAutoSelect_byIdWIthEmbedded() {

        MEmbeddedTestEntity m_ = MEmbeddedTestEntity.testEmbedded;

        EmbeddedTestEntity entity = sqlMapper.selectFrom(m_)
                .id(new EmbeddedTestEntity.PK("001", 1l))
                .getSingleResult();

        assertThat(entity).hasFieldOrPropertyWithValue("id", new EmbeddedTestEntity.PK("001", 1l));

    }

    @Test
    void testAutoSelect_byIdWithEach() {

        MEmbeddedTestEntity m_ = MEmbeddedTestEntity.testEmbedded;

        EmbeddedTestEntity entity = sqlMapper.selectFrom(m_)
                .id("001", 1l)
                .getSingleResult();

        assertThat(entity).hasFieldOrPropertyWithValue("id", new EmbeddedTestEntity.PK("001", 1l));

    }

    @Test
    void testAutoInsert() {

        MEmbeddedTestEntity m_ = MEmbeddedTestEntity.testEmbedded;

        EmbeddedTestEntity entity = new EmbeddedTestEntity();
        entity.setId(new EmbeddedTestEntity.PK("010", 10L));
        entity.setDeleted(false);
        entity.setName("010@insert");

        int count = txNew().execute(action -> sqlMapper.insert(entity).execute());
        assertThat(count).isEqualTo(1);

        EmbeddedTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", new EmbeddedTestEntity.PK("010", 10l))
            .hasFieldOrPropertyWithValue("name", "010@insert");

    }

    @Test
    void testAutoUpdate() {

        MEmbeddedTestEntity m_ = MEmbeddedTestEntity.testEmbedded;

        EmbeddedTestEntity entity = sqlMapper.selectFrom(m_)
                .id(new EmbeddedTestEntity.PK("001", 1l))
                .getSingleResult();

        entity.setName("001@update");

        int count = txNew().execute(action -> sqlMapper.update(entity).execute());
        assertThat(count).isEqualTo(1);

        EmbeddedTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", entity.getId())
            .hasFieldOrPropertyWithValue("name", "001@update");

    }

    @Test
    void testAutoDelete() {

        MEmbeddedTestEntity m_ = MEmbeddedTestEntity.testEmbedded;

        EmbeddedTestEntity entity = new EmbeddedTestEntity();
        entity.setId(new EmbeddedTestEntity.PK("001", 1L));

        int count = txNew().execute(action -> sqlMapper.delete(entity).execute());
        assertThat(count).isEqualTo(1);

        Optional<EmbeddedTestEntity> result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getOptionalResult();
        assertThat(result).isEmpty();

    }

    @Test
    void testAutoBatchInsert() {

        MEmbeddedTestEntity m_ = MEmbeddedTestEntity.testEmbedded;

        List<EmbeddedTestEntity> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            int offset = i+10;
            EmbeddedTestEntity entity = new EmbeddedTestEntity();
            entity.setId(new EmbeddedTestEntity.PK(String.format("test@%05d", offset), offset));
            entity.setName("name@" + offset);

            entities.add(entity);
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            int offset = i+10;
            EmbeddedTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("id", new EmbeddedTestEntity.PK(String.format("test@%05d", offset), offset))
                .hasFieldOrPropertyWithValue("name", "name@" + offset);

        }

    }

    @Test
    void testAutoBatchUpdate() {

        MEmbeddedTestEntity m_ = MEmbeddedTestEntity.testEmbedded;

        List<EmbeddedTestEntity> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.key1.asc(), m_.id.key2.asc())
                .getResultList();

        for(int i=0; i < 5; i++) {
            EmbeddedTestEntity entity = entities.get(i);
            entity.setName("name@" + i);
        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.updateBatch(entities)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            EmbeddedTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasFieldOrPropertyWithValue("name", "name@" + i);

        }

    }

    @Test
    void testAutoBatchDelete() {

        MEmbeddedTestEntity m_ = MEmbeddedTestEntity.testEmbedded;

        List<EmbeddedTestEntity> entities = sqlMapper.selectFrom(m_)
                .limit(5)
                .orderBy(m_.id.key1.asc(), m_.id.key2.asc())
                .getResultList();

        int count = txNew().execute(action -> {
            return sqlMapper.deleteBatch(entities)
                    .execute();
        });

        assertThat(count).isEqualTo(5);

        for(int i=0; i < 5; i++) {
            Optional<EmbeddedTestEntity> result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getOptionalResult();
            assertThat(result).isEmpty();

        }

    }

    @Test
    void testAutoAnyDelete() {

        MEmbeddedTestEntity m_ = MEmbeddedTestEntity.testEmbedded;

        long beforeCount = sqlMapper.selectFrom(m_)
                .where(m_.id.key2.eq(1l))
                .getCount();

        int count = txNew().execute(action -> {
            return sqlMapper.deleteFrom(m_)
                .where(m_.id.key2.eq(1l))
                .execute();
        });

        assertThat(count).isEqualTo(beforeCount);

        sqlMapper.selectFrom(m_).getResultList().forEach(entity -> {
            assertThat(entity.getId()).hasFieldOrPropertyWithValue("key2", 2l);
        });


    }

}
