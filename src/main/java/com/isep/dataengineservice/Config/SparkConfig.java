package com.isep.dataengineservice.Config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Bean
    public SparkConf sparkConf() {
        return new SparkConf().set("spark.ui.port", "3000");
    }

    @Bean
    public SparkSession sparkSession(SparkConf sparkConf) {
        return SparkSession.builder()
                .config(sparkConf)
                .appName("clustering")
                .master("local[*]")
                .getOrCreate();
    }

    @Bean
    public JavaSparkContext javaSparkContext(SparkSession spark) {
        return new JavaSparkContext(spark.sparkContext());
    }

}
