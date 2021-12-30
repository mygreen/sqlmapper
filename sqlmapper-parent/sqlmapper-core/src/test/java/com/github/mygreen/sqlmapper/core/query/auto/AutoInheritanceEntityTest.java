package com.github.mygreen.sqlmapper.core.query.auto;

import static org.assertj.core.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
import com.github.mygreen.sqlmapper.core.test.entity.InheritanceTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MInheritanceTestEntity;


/**
 * 自動組み立てによる継承型のエンティティのテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AutoInheritanceEntityTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_inheritance.sql");
    }

    @Test
    void testAutoSelect_where() {

        MInheritanceTestEntity m_ = MInheritanceTestEntity.testInheritance;

        InheritanceTestEntity result = sqlMapper.selectFrom(m_)
                .where(m_.createAt.eq(Timestamp.valueOf("2021-01-03 00:00:00")))
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 3l)
                .hasFieldOrPropertyWithValue("createAt", Timestamp.valueOf("2021-01-03 00:00:00"));

    }

    @Test
    void testAutoSelect_orderBy() {

        MInheritanceTestEntity m_ = MInheritanceTestEntity.testInheritance;

        List<InheritanceTestEntity> result = sqlMapper.selectFrom(m_)
                .limit(3)
                .orderBy(m_.createAt.desc())
                .getResultList();

        assertThat(result).hasSize(3);

    }

    @Test
    void testAutoSelect_version() {

        MInheritanceTestEntity m_ = MInheritanceTestEntity.testInheritance;

        InheritanceTestEntity result = sqlMapper.selectFrom(m_)
                .id(1l)
                .version(0l)
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 1l)
                .hasFieldOrPropertyWithValue("version", 0l);

    }

    @Test
    void testAutoInsert_withAutdit() {

        MInheritanceTestEntity m_ = MInheritanceTestEntity.testInheritance;

        InheritanceTestEntity entity = new InheritanceTestEntity();
        entity.setId(10l);
        entity.setName("010@insert");
        entity.setBirthday(LocalDate.of(1980, 1, 12));

        int count = txNew().execute(action -> sqlMapper.insert(entity).execute());
        assertThat(count).isEqualTo(1);

        InheritanceTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 10l)
                .hasFieldOrPropertyWithValue("name", "010@insert")
                .hasFieldOrPropertyWithValue("version", 0l)
                .hasNoNullFieldsOrProperties();

    }

    @Test
    void testAutoUpdate_withAutdit() {

        MInheritanceTestEntity m_ = MInheritanceTestEntity.testInheritance;

        InheritanceTestEntity entity = sqlMapper.selectFrom(m_)
                .id(1l)
                .getSingleResult();

        entity.setName("001@update");

        Date beforeCreateAt = entity.getCreateAt();
        Date beforeUpdateAt = entity.getUpdateAt();

        int count = txNew().execute(action -> sqlMapper.update(entity).execute());
        assertThat(count).isEqualTo(1);

        InheritanceTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();

        assertThat(result).hasFieldOrPropertyWithValue("id", 1l)
            .hasFieldOrPropertyWithValue("name", "001@update")
            .hasFieldOrPropertyWithValue("version", 1l);

        assertThat(result.getCreateAt()).isEqualTo(beforeCreateAt); // 変わらないこと。
        assertThat(result.getUpdateAt()).isNotEqualTo(beforeUpdateAt);

    }

    @Test
    void testAutoDelete() {

        MInheritanceTestEntity m_ = MInheritanceTestEntity.testInheritance;

        InheritanceTestEntity entity = sqlMapper.selectFrom(m_)
                .id(1l)
                .getSingleResult();

        int count = txNew().execute(action -> sqlMapper.delete(entity).execute());
        assertThat(count).isEqualTo(1);

        Optional<InheritanceTestEntity> result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getOptionalResult();
        assertThat(result).isEmpty();

    }

    @Test
    void testAutoBatchInsert_withAudit() {

        MInheritanceTestEntity m_ = MInheritanceTestEntity.testInheritance;

        List<InheritanceTestEntity> entities = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            int offset = i + 10;
            InheritanceTestEntity entity = new InheritanceTestEntity();
            entity.setId(offset);
            entity.setName("insert@" + offset);
            entity.setBirthday(LocalDate.of(2000, 1, offset));

            entities.add(entity);

        }

        int[] count = txNew().execute(action -> {
            return sqlMapper.insertBatch(entities)
                    .execute();
        });

        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            int offset = i + 10;
            InheritanceTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasNoNullFieldsOrProperties() // 全てのプロパティに値が設定されていること。
                    .hasFieldOrPropertyWithValue("id", (long)offset)
                    .hasFieldOrPropertyWithValue("name", "insert@" + offset);

        }

    }

    @Test
    void testAutoBatchUpdate_withAudit() {

        MInheritanceTestEntity m_ = MInheritanceTestEntity.testInheritance;

        List<InheritanceTestEntity> entities = sqlMapper.selectFrom(m_)
                    .limit(5)
                    .orderBy(m_.id.asc())
                    .getResultList();
        Date[] beforeCreateAt = new Date[entities.size()];
        Date[] beforeUpdateAt = new Date[entities.size()];

        for(int i=0; i < 5; i++) {
            int offset = i + 10;
            InheritanceTestEntity entity = entities.get(i);
            entity.setName("update@" + offset);
            entity.setBirthday(LocalDate.of(2000, 1, offset));

            beforeCreateAt[i] = entity.getCreateAt();
            beforeUpdateAt[i] = entity.getUpdateAt();

        }

        int count[] = txNew().execute(action -> sqlMapper.updateBatch(entities).execute());
        assertThat(count).containsExactly(1, 1, 1, 1, 1);

        for(int i=0; i < 5; i++) {
            int offset = i + 10;
            InheritanceTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();

            assertThat(result).hasNoNullFieldsOrProperties() // 全てのプロパティに値が設定されていること。
                    .hasFieldOrPropertyWithValue("name", "update@" + offset)
                    .hasFieldOrPropertyWithValue("version", 1l);

            assertThat(result.getCreateAt()).isEqualTo(beforeCreateAt[i]); // 変わらないこと。
            assertThat(result.getUpdateAt()).isNotEqualTo(beforeUpdateAt[i]);

        }

    }

    @Test
    void testAutoBatchDelete() {

        MInheritanceTestEntity m_ = MInheritanceTestEntity.testInheritance;

        List<InheritanceTestEntity> entities = sqlMapper.selectFrom(m_)
                    .limit(5)
                    .orderBy(m_.id.asc())
                    .getResultList();

        int count = txNew().execute(action -> sqlMapper.deleteBatch(entities).execute());
        assertThat(count).isEqualTo(5);

        for(int i=0; i < 5; i++) {
            Optional<InheritanceTestEntity> result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getOptionalResult();
            assertThat(result).isEmpty();
        }

    }

    @Test
    void testAutoAnyDelete() {

        MInheritanceTestEntity m_ = MInheritanceTestEntity.testInheritance;

        long beforeCount = sqlMapper.selectFrom(m_)
                    .where(m_.createAt.goe(Timestamp.valueOf("2021-01-03 00:00:00")))
                    .getCount();

        int count = txNew().execute(action -> {
            return sqlMapper.deleteFrom(m_)
                    .where(m_.createAt.goe(Timestamp.valueOf("2021-01-03 00:00:00")))
                    .execute();
        });

        assertThat(count).isEqualTo(beforeCount);

        sqlMapper.selectFrom(m_).getResultList().forEach(entity -> {
            assertThat(entity.getCreateAt()).isBefore(Timestamp.valueOf("2021-01-03 00:00:00"));
        });

    }

}
