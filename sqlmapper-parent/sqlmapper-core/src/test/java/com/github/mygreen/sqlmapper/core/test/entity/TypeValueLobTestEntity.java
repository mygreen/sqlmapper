package com.github.mygreen.sqlmapper.core.test.entity;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Lob;
import com.github.mygreen.sqlmapper.core.annotation.Table;

import lombok.Data;

@Data
@Table(name = "test_type_value_lob")
@Entity
public class TypeValueLobTestEntity implements Serializable {

    @Id
    private long id;

    private Clob clobData1;

    @Lob
    private String clobData2;

    private Blob blobData1;

    @Lob
    private byte[] blobData2;

    private String comment;
}
