package com.gik.csvsqlparser;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<ParkingViolations> reader() {

        String[] names = {"summonsNumber", "plateId", "registrationState"};

        return new FlatFileItemReaderBuilder<ParkingViolations>()
                .name("parkingItemReader")
                .resource(new PathResource("20172.csv")) //     "C:\\Users\\kgladii\\Downloads\\archive\\Parking_Violations_2017.csv"
                .delimited()
                .names(names)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<ParkingViolations>() {{
                    setTargetType(ParkingViolations.class);
                }})
                .build();
    }

    @Bean
    public ParkingItemProcessor processor() {
        return new ParkingItemProcessor();
    }

    @Bean
    JdbcBatchItemWriter<ParkingViolations> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<ParkingViolations>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO parking_violations (summonsNumber, plateId, registrationState) " +
                        "values (:summonsNumber, :plateId, :registrationState)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<ParkingViolations> writer) {
        return stepBuilderFactory.get("step1")
                .<ParkingViolations, ParkingViolations>chunk(10) //задаем размер обрабатываемых позиций за раз
                .reader(reader())
                .processor(processor())
                .writer(writer)
                /*.faultTolerant() // 3 строки. Игнорим exception о несоответствие полей в файле
                .skip(FlatFileParseException.class)
                .skipLimit(1000)*/
                .build();
    }
}
