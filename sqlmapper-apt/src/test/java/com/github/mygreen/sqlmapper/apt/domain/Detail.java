package com.github.mygreen.sqlmapper.apt.domain;

import java.math.BigDecimal;

import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.Entity;

import lombok.Data;

@Data
@Entity
public class Detail {

    @EmbeddedId
    private DetailPK pk;

    private String name;

    private BigDecimal price;
}
