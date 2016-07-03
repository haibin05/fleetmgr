package com.yunguchang.resteasy;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * Created by gongy on 2015/11/20.
 */
public class ResteasyClientProducer {

    private static final int REST_CALL_TIMEOUT = 5;//sec

    @Produces
    public ResteasyClient getResteasyClient(InjectionPoint injectionPoint) {
        try {
            int poolSize = 0;
            RestClient restClientAnno = injectionPoint.getAnnotated().getAnnotation(RestClient.class);
            if (restClientAnno != null) {
                poolSize = restClientAnno.poolSize();
            }
            if (poolSize == 0) {
                poolSize = 10;
            }
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });
            ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilder()
                    .connectionPoolSize(poolSize)
                    .disableTrustManager()
                    .establishConnectionTimeout(REST_CALL_TIMEOUT, TimeUnit.SECONDS)
                    .connectionCheckoutTimeout(REST_CALL_TIMEOUT, TimeUnit.SECONDS)
                    .socketTimeout(REST_CALL_TIMEOUT, TimeUnit.SECONDS)
                    .sslContext(sslContextBuilder.build());

            String proxyHost = System.getProperty("http.proxyHost");
            String proxyPort = System.getProperty("http.proxyPort");

            if (proxyHost != null && proxyPort != null) {
                try {
                    resteasyClientBuilder.defaultProxy(proxyHost, Integer.parseInt(proxyPort));
                } catch (NumberFormatException e) {

                }
            }
            ResteasyClient client = resteasyClientBuilder.build();
            client.register(JacksonJaxbJsonProvider.class);
            return client;
        } catch (NoSuchAlgorithmException e) {

        } catch (KeyStoreException e) {

        } catch (KeyManagementException e) {

        }
        return null;
    }
}
