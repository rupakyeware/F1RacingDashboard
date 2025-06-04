package com.f1.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
public class F1DashboardApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        System.out.println("Starting F1 Dashboard Application");
        SpringApplication.run(F1DashboardApplication.class, args);
        System.out.println("F1 Dashboard Application started successfully");
    }
}