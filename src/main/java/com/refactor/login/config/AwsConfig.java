package com.refactor.login.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AwsConfig {

    @Value("${aws.llave.acceso}")
    private String awsAccesokey;

    @Value("${aws.llave.secreta}")
    private String awsSecrectKey;

    private BasicAWSCredentials getBasicAWSCredentials() {
        return new BasicAWSCredentials(awsAccesokey, awsSecrectKey);
    }

    @Bean
    public AmazonSNS getConfiguracionSns() {
        return AmazonSNSClient
                .builder()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(this.getBasicAWSCredentials()))
                .build();
    }

    @Bean
    public SnsClient getSnsClientCreate() {
        return SnsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccesokey, awsSecrectKey)))
                .build();
    }
}
