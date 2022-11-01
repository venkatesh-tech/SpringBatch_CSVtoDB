package com.boot.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.boot.batch.model.Product;

@Configuration
public class BatchConfig {
	
	@Autowired
	private JobBuilderFactory jbf;
	
	@Autowired
	private StepBuilderFactory sbf;
	
	public Job job() {
		return jbf.get("j2")
		.incrementer(new RunIdIncrementer())
		.start(step())
		.build();
	}
	
	@Bean
	public Step step() {
		return sbf.get("s1")
				.<Product,Product>chunk(3)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}
	@Bean
	public ItemReader<Product> reader(){
		
		//Create reader instance
		FlatFileItemReader<Product> reader = new FlatFileItemReader<>();
		//Set input file location
		reader.setResource(new ClassPathResource("products.csv"));
		 //Configure how each line will be parsed and mapped to different values
		DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(); //taking one line and parsing it. //Reading each line separated by comma, 
		lineTokenizer.setNames("id","name","description","price"); //stores each of them in their variables
		BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>(); //will take those values and sets them
		fieldSetMapper.setTargetType(Product.class);
		 
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		
		return reader;
		
	}
	
	@Bean
	public ItemProcessor<Product, Product> processor(){
		return (p)->{
			p.setPrice(p.getPrice()-p.getPrice()*10/100);
			return p;
		};
		
	}
	
	@Bean
	public ItemWriter<Product> writer(){
		JdbcBatchItemWriter<Product> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(dataSource());
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Product>());
		writer.setSql("INSERT INTO PRODUCT (ID, NAME,DESCRIPTION,PRICE) VALUES (:id,:name,:description,:price )");
		
		return writer;
	}
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/mydb");
		dataSource.setUsername("root");
		dataSource.setPassword("Venky1234@");
		return dataSource();
	}
	
}








