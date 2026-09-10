package com.mbfreire.employee_reporting.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class SupabaseConfig {

    @Bean
    public RestClient supabaseRestClient(
            @Value("${supabase.url}")
            String supabaseUrl,

            @Value("${supabase.secret-key}")
            String secretKey
    ) {

        return RestClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader(
                        "apikey",
                        secretKey
                )
                .build();
    }
}