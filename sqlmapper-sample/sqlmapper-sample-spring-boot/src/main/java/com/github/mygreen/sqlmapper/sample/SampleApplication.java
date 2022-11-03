package com.github.mygreen.sqlmapper.sample;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

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

        Customer cust = createCustomer();
        System.out.printf("create=%s\n", cust);


        Customer result = sqlMapper.selectFrom(MCustomer.customer)
                .where(MCustomer.customer.firstName.lower().contains("nobunaga"))
                .getResultStream()
                .findFirst()
                .get();

        System.out.printf("select=%s\n", result);

    }

    @Transactional
    public Customer createCustomer() {

        Customer entity = new Customer();
        entity.setFirstName("Nobunaga");
        entity.setLastName("Oda");
        entity.setBirthday(LocalDate.of(1980, 6, 23));

        sqlMapper.insert(entity)
            .execute();

        return entity;
    }

}
