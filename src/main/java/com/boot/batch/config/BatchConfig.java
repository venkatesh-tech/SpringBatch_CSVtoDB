package com.boot.batch.config;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.boot.batch.model.Product;

@Configuration
public class BatchConfig {
	
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
}