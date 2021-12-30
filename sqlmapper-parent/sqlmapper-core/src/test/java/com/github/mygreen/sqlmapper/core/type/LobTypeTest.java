package com.github.mygreen.sqlmapper.core.type;


import static org.assertj.core.api.Assertions.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.core.test.QueryTestSupport;
import com.github.mygreen.sqlmapper.core.test.config.H2TestConfig;
import com.github.mygreen.sqlmapper.core.test.entity.TypeValueLobTestEntity;
import com.github.mygreen.sqlmapper.core.test.entity.meta.MTypeValueLobTestEntity;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=H2TestConfig.class)
public class LobTypeTest extends QueryTestSupport {

    @Autowired
    SqlMapper sqlMapper;

    @Autowired
    LobHandler lobHandler;

    @BeforeEach
    void beforeMethod() {
        resetData();
    }

    @Test
    void testAutoInsertAndSelect() throws Exception {

        MTypeValueLobTestEntity m_ = MTypeValueLobTestEntity.testLob;

        TypeValueLobTestEntity entity = new TypeValueLobTestEntity();
        entity.setId(1L);

        entity.setClobData1(new SerialClob("test data1".toCharArray()));
        entity.setClobData2("テストデータ2");

        entity.setBlobData1(new SerialBlob(loadBinaryResource("data/image001.jpg")));
        entity.setBlobData2(loadBinaryResource("data/image002.jpg"));

        entity.setComment("insert");

        int count = txNew().execute(action -> sqlMapper.insert(entity)
                .execute());

        assertThat(count).isEqualTo(1);

        TypeValueLobTestEntity result = sqlMapper.selectFrom(m_)
                .id(1L)
                .getSingleResult();

        assertThat(result)/*.hasFieldOrPropertyWithValue("clobData1", entity.getClobData1())*/
                .hasFieldOrPropertyWithValue("clobData2", entity.getClobData2())
//            .hasFieldOrPropertyWithValue("blobData1", entity.getBlobData1())
                .hasFieldOrPropertyWithValue("blobData2", entity.getBlobData2())
                ;

    }

    @Test
    void testAutoUpdate() throws Exception {

        MTypeValueLobTestEntity m_ = MTypeValueLobTestEntity.testLob;

        // 内容を空で作成
        TypeValueLobTestEntity entity = new TypeValueLobTestEntity();
        entity.setId(1L);

        entity.setComment("init");

        int count = txNew().execute(action -> sqlMapper.insert(entity)
                .execute());
        assertThat(count).isEqualTo(1);

        // 更新する
        entity.setClobData1(new SerialClob("test data1".toCharArray()));
        entity.setClobData2("テストデータ2");

        entity.setBlobData1(new SerialBlob(loadBinaryResource("data/image001.jpg")));
        entity.setBlobData2(loadBinaryResource("data/image002.jpg"));

        entity.setComment("update");

        int count2 = txNew().execute(action -> sqlMapper.update(entity)
                .execute());
        assertThat(count2).isEqualTo(1);

        TypeValueLobTestEntity result = sqlMapper.selectFrom(m_)
                .id(1L)
                .getSingleResult();

        assertThat(result)/*.hasFieldOrPropertyWithValue("clobData1", entity.getClobData1())*/
                .hasFieldOrPropertyWithValue("clobData2", entity.getClobData2())
//        .hasFieldOrPropertyWithValue("blobData1", entity.getBlobData1())
                .hasFieldOrPropertyWithValue("blobData2", entity.getBlobData2())
                .hasFieldOrPropertyWithValue("comment", "update")
                ;


    }

    private byte[] loadBinaryResource(String path) {
        Resource resource = resourceLoader.getResource(path);

        try(DataInputStream in = new DataInputStream(resource.getInputStream())) {
            return in.readAllBytes();

        } catch(IOException e) {
            throw new UncheckedIOException("fail load binary file : " + path, e);
        }
    }


}
