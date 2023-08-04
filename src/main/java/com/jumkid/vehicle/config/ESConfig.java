package com.jumkid.vehicle.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

@Slf4j
@Configuration
public class ESConfig {

    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.port}")
    private int esPort;

    @Value("${elasticsearch.http.protocol}")
    private String esProtocol;

    @Value("${elasticsearch.user.name}")
    private String esUserName;

    @Value("${elasticsearch.user.password}")
    private String esUserPassword;

    @Value("${elasticsearch.keystore.format}")
    private String esKeystoreFormat;

    @Value("${elasticsearch.keystore.path}")
    private String esKeystorePath;

    @Value("${elasticsearch.keystore.pass}")
    private String esKeyStorePass;

    @Value("${elasticsearch.cluster.name}")
    private String esClusterName;

    @Bean
    public ElasticsearchClient esClient(){
        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(esUserName, esUserPassword));

        try {
            SSLContext sslContext = esProtocol.equals("https") ? buildContext() : null;

            RestClient restClient = RestClient.builder(
                    new HttpHost(InetAddress.getByName(esHost), esPort, esProtocol)
            ).setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setSSLContext(sslContext)
            ).build();

            // Create the transport with a Jackson mapper
            ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

            return new ElasticsearchClient(transport);
        } catch (UnknownHostException uhe) {
            log.error("Failed to connect elasticsearch host {} ", esHost);
        } catch (Exception e) {
            log.error("Failed to build ssl context {}", e.getMessage());
        }
        return null;
    }

    private SSLContext buildContext() throws KeyStoreException, IOException, CertificateException,
            NoSuchAlgorithmException, KeyManagementException {
        Path trustStorePath = Paths.get(esKeystorePath);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        KeyStore trustStore = KeyStore.getInstance(esKeystoreFormat);

        Certificate trustedCa;
        try (InputStream is = Files.newInputStream(trustStorePath)) {
            trustedCa = factory.generateCertificate(is);
        }
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca", trustedCa);

        SSLContextBuilder sslBuilder = SSLContexts.custom()
                .loadTrustMaterial(trustStore, null);
        return sslBuilder.build();
    }
}
