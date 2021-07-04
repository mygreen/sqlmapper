package com.github.mygreen.sqlmapper.core.testdata;

import java.io.Serializable;
import java.util.UUID;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Lob;

import lombok.Data;

@Data
@Entity
public class LobData implements Serializable {

    @Id
    private UUID id;

    @Lob
    private String clob;

    @Lob
    private byte[] blob;
}
