package com.github.mygreen.sqlmapper.apt.domain;

import java.math.BigDecimal;

import com.github.mygreen.sqlmapper.core.annotation.Entity;
import com.github.mygreen.sqlmapper.core.annotation.Id;
import com.github.mygreen.sqlmapper.core.annotation.Lob;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * テスト用のドメイン。
 * 継承している
 *
 *
 * @author T.TSUCHIE
 *
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class Order extends EntityBase {

    @Getter
    @Setter
    @Id
    private long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private BigDecimal price;

    @Lob
    @Setter
    @Getter
    private byte[] blob;

}
