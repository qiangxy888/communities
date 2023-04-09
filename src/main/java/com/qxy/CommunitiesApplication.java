package com.qxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.qxy.community.dao.elasticsearch")
public class CommunitiesApplication {
    @PostConstruct
    public void init(){
        //解决netty启动冲突的问题
        //Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
    public static void main(String[] args) {
        SpringApplication.run(CommunitiesApplication.class, args);
    }

}
