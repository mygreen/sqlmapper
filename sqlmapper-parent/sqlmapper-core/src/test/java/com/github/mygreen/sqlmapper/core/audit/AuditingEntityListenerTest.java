package com.github.mygreen.sqlmapper.core.audit;

import static org.assertj.core.api.Assertions.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.AuditTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MAuditTestEntity;


/**
 * {@link AuditingEntityListener}のテスタ
 *
 *
 * @author T.TSUCHIE
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class AuditingEntityListenerTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @Autowired
    AuditingEntityListener autAuditingEntityListener;

    ThreadLocal<String> currentUser = new ThreadLocal<String>();

    void boundCurrentUser(String user) {
        this.currentUser.set(user);
    }

    void unboundCurrentUser() {
        this.currentUser.remove();
    }

    @Bean
    AuditorProvider<String> auditorProvider() {
        return new AuditorProvider<String>() {

            @Override
            public Optional<String> getCurrentAuditor() {
                return Optional.ofNullable(currentUser.get());
            }
        };
    }

    @BeforeEach
    void beforeMethod() {
        resetData();
        executeSqlFileAndCommit("insert_data_audit.sql");
        autAuditingEntityListener.setAuditorProvider(auditorProvider());
    }

    @AfterEach
    void afterMethod() {
        unboundCurrentUser();

        // リセットする
        autAuditingEntityListener.setAuditorProvider(null);
        autAuditingEntityListener.afterPropertiesSet();
    }

    @Test
    void testInsert() {
        String user = "@insert";
        boundCurrentUser(user);

        MAuditTestEntity m_ = MAuditTestEntity.testAudit;

        AuditTestEntity entity = new AuditTestEntity();
        entity.setId(10l);
        entity.setComment("inset-test");

        int count = txNew().execute(action -> sqlMapper.insert(entity).execute());
        assertThat(count).isEqualTo(1);

        AuditTestEntity result = sqlMapper.selectFrom(m_)
                .id(10l)
                .getSingleResult();


        // 値が設定されていること
        assertThat(result).hasFieldOrPropertyWithValue("createUser", user)
            .hasFieldOrPropertyWithValue("updateUser", user);

        assertThat(result.getCreateDatetime()).isNotNull();
        assertThat(result.getUpdateDatetime()).isNotNull();

        // エンティティが書き換えられているので比較する。
        assertThat(result).isEqualTo(entity);


    }

    @Test
    void testUpdate() {

        String user = "@update";
        boundCurrentUser(user);

        MAuditTestEntity m_ = MAuditTestEntity.testAudit;

        AuditTestEntity entity = sqlMapper.selectFrom(m_)
                .id(1l)
                .getSingleResult();
        entity.setComment("update-test");

        String beforeCreateUser = entity.getCreateUser();
        Timestamp beforeCreateDateTime = entity.getCreateDatetime();
        Timestamp beforeUpdateDateTime = entity.getUpdateDatetime();

        int count = txNew().execute(action -> sqlMapper.update(entity).execute());
        assertThat(count).isEqualTo(1);

        AuditTestEntity result = sqlMapper.selectFrom(m_)
                .id(entity.getId())
                .getSingleResult();


        // 値が設定されていること
        assertThat(result).hasFieldOrPropertyWithValue("createUser", beforeCreateUser)  // 変わらないこと
            .hasFieldOrPropertyWithValue("updateUser", user);

        assertThat(result.getCreateDatetime()).isEqualTo(beforeCreateDateTime); // 変わらないこと
        assertThat(result.getUpdateDatetime()).isNotEqualTo(beforeUpdateDateTime);

        // エンティティが書き換えられているので比較する。
        assertThat(result).isEqualTo(entity);

    }

    @Test
    void testBatchInsert() {

        String user = "@batch-insert";
        boundCurrentUser(user);

        MAuditTestEntity m_ = MAuditTestEntity.testAudit;

        List<AuditTestEntity> entities = new ArrayList<>();
        for(int i=0; i < 2; i++) {
            AuditTestEntity entity = new AuditTestEntity();
            entity.setId(10l + (long)i);
            entity.setComment(i + "@batch-inset-test");

            entities.add(entity);
        }

        int[] count = txNew().execute(action -> sqlMapper.insertBatch(entities).execute());
        assertThat(count).containsExactly(1, 1);

        for(int i=0; i < 2; i++) {
            AuditTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();


            // 値が設定されていること
            assertThat(result).hasFieldOrPropertyWithValue("createUser", user)
                .hasFieldOrPropertyWithValue("updateUser", user);

            assertThat(result.getCreateDatetime()).isNotNull();
            assertThat(result.getUpdateDatetime()).isNotNull();

            // エンティティが書き換えられているので比較する。
            assertThat(result).isEqualTo(entities.get(i));

        }

    }

    @Test
    void testBatchUpdate() {

        String user = "@batch-update";
        boundCurrentUser(user);

        MAuditTestEntity m_ = MAuditTestEntity.testAudit;

        List<AuditTestEntity> entities = sqlMapper.selectFrom(m_)
                .limit(2)
                .orderBy(m_.id.asc())
                .getResultList();

        String[] beforeCreateUser = new String[entities.size()];
        Timestamp[] beforeCreateDateTime = new Timestamp[entities.size()];
        Timestamp[] beforeUpdateDateTime = new Timestamp[entities.size()];
        for(int i=0; i < entities.size(); i++) {
            AuditTestEntity entity = entities.get(i);
            entity.setComment(i + "@batch-update-test");

            beforeCreateUser[i] = entity.getCreateUser();

            beforeCreateDateTime[i] = entity.getCreateDatetime();
            beforeUpdateDateTime[i] = entity.getUpdateDatetime();

        }

        int count[] = txNew().execute(action -> sqlMapper.updateBatch(entities).execute());
        assertThat(count).containsExactly(1, 1);

        for(int i=0; i < 2; i++) {
            AuditTestEntity result = sqlMapper.selectFrom(m_)
                    .id(entities.get(i).getId())
                    .getSingleResult();


            // 値が設定されていること
            assertThat(result).hasFieldOrPropertyWithValue("createUser", beforeCreateUser[i])  // 変わらないこと
                    .hasFieldOrPropertyWithValue("updateUser", user);

            assertThat(result.getCreateDatetime()).isEqualTo(beforeCreateDateTime[i]); // 変わらないこと
            assertThat(result.getUpdateDatetime()).isNotEqualTo(beforeUpdateDateTime[i]);

            // エンティティが書き換えられているので比較する。
            assertThat(result).isEqualTo(entities.get(i));

        }

    }
}
