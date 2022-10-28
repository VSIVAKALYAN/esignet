/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.mosip.idp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import io.mosip.idp.core.util.Constants;
import io.mosip.kernel.keymanagerservice.dto.KeyPairGenerateRequestDto;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaRepositories(basePackages = {"io.mosip.idp.repository", "io.mosip.kernel.keymanagerservice.repository"})
@EntityScan(basePackages = {"io.mosip.idp.entity", "io.mosip.kernel.keymanagerservice.entity"})
@Slf4j
public class AppConfig implements ApplicationRunner {


    @Value("${mosip.idp.default.httpclient.connections.max.per.host:20}")
    private int defaultMaxConnectionPerRoute;

    @Value("${mosip.idp.default.httpclient.connections.max:100}")
    private int defaultTotalMaxConnection;


    @Autowired
    private KeymanagerService keymanagerService;

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new AfterburnerModule())
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setMaxConnPerRoute(defaultMaxConnectionPerRoute)
                .setMaxConnTotal(defaultTotalMaxConnection)
                .disableCookieManagement();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClientBuilder.build());
        return new RestTemplate(requestFactory);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("===================== IDP_SERVICE ROOT KEY CHECK ========================");
        String objectType = "CSR";
        KeyPairGenerateRequestDto rootKeyRequest = new KeyPairGenerateRequestDto();
        rootKeyRequest.setApplicationId(Constants.ROOT_KEY);
        keymanagerService.generateMasterKey(objectType, rootKeyRequest);
        log.info("===================== IDP_SERVICE MASTER KEY CHECK ========================");
        KeyPairGenerateRequestDto masterKeyRequest = new KeyPairGenerateRequestDto();
        masterKeyRequest.setApplicationId(Constants.IDP_SERVICE_APP_ID);
        keymanagerService.generateMasterKey(objectType, masterKeyRequest);
        log.info("===================== IDP_PARTNER MASTER KEY CHECK ========================");
        KeyPairGenerateRequestDto partnerMasterKeyRequest = new KeyPairGenerateRequestDto();
        partnerMasterKeyRequest.setApplicationId(Constants.IDP_PARTNER_APP_ID);
        keymanagerService.generateMasterKey(objectType, partnerMasterKeyRequest);
        log.info("===================== IDP KEY SETUP COMPLETED ========================");
    }
}
