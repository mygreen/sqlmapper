package com.github.mygreen.sqlmapper.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.mygreen.sqlmapper.core.SqlMapper;
import com.github.mygreen.sqlmapper.sample.entity.Customer;
import com.github.mygreen.sqlmapper.sample.entity.MCustomer;

/**
 * SqlMapperのSpringBootのサンプルアプリケーション
 *
 * @author T.TSUCHIE
 *
 */
@SpringBootApplication
public class SampleApplication implements CommandLineRunner {

    @Autowired
    private SqlMapper sqlMapper;

    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Customer cust = sqlMapper.selectFrom(MCustomer.customer)
                .where(MCustomer.customer.firstName.lower().contains("taro"))
                .getResultStream()
                .findFirst()
                .get();

        System.out.println(cust);

    }


}
