package com.github.mygreen.sqlmapper.apt.domain;

import com.github.mygreen.sqlmapper.core.annotation.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class DetailPK {

    public long orderId;

    public String detailId;
}
