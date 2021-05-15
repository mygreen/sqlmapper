package com.github.mygreen.sqlmapper.apt.domain;

import java.math.BigDecimal;

import com.github.mygreen.sqlmapper.core.annotation.Embeddable;
import com.github.mygreen.sqlmapper.core.annotation.EmbeddedId;
import com.github.mygreen.sqlmapper.core.annotation.Entity;

import lombok.Data;

@Data
@Entity
public class Detail2 {

    @EmbeddedId
    private PK pk;

    private String name;

    private BigDecimal price;

    @Data
    @Embeddable
    public static class PK {

        public long orderId;

        public String detailId;
    }
}
