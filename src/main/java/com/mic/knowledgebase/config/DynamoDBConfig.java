package com.mic.knowledgebase.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
// import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.mic.knowledgebase.repository")
public class DynamoDBConfig {

        @Value("${amazon.dynamodb.endpoint:}")
        private String amazonDynamoDBEndpoint;

        @Value("${amazon.aws.accesskey:}")
        private String amazonAWSAccessKey;

        @Value("${amazon.aws.secretkey:}")
        private String amazonAWSSecretKey;

        @Value("${amazon.aws.region}")
        private String amazonAWSRegion;

        @Bean(name = "amazonDynamoDB")
        @Profile("dev")
        public AmazonDynamoDB amazonDynamoDBDev() {
                return AmazonDynamoDBClientBuilder.standard()
                                .withEndpointConfiguration(
                                                new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint,
                                                                amazonAWSRegion))
                                .withCredentials(new AWSStaticCredentialsProvider(
                                                new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey)))
                                .build();
        }

        @Bean(name = "amazonDynamoDB")
        @Profile("!dev")
        public AmazonDynamoDB amazonDynamoDBProd() {
                return AmazonDynamoDBClientBuilder.standard()
                                .withRegion(amazonAWSRegion)
                                .build();
        }
}