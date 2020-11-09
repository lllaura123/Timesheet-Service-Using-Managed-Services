package com.exxeta.timesheetapproveservice.service;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class PoolingManager {
    /**
     * Verifiziert jeden Host.
     */
    private static class TrustAllHostNameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * Registry - remote interface to a simple remote object registry --> storing and retrieving remote object references
     * layered socket factory for TLS/SSL connections --> validates identity of HTTPS server against trusted certificates + authenticate to HTTPS server with private key
     * SSLConnectionSocketFactory(SSLContext sslContext, HostnameVerifier hostNameVerifier)
     * TrustSelfSignedStrategy == trust strategy, that accepts self-signed certificates as trusted
     * PlainConnectionSocketFactory == default class for creating plain(unencrypted) sockets
     * PoolingHttpClientConnectionManager - Pool of HTTP Clientconnections services connection request from multiple execution threads
     *
     * @return PoolingHttpClientConnectionManager
     */
    public PoolingHttpClientConnectionManager createPoolingManager() {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = null;

        try {
            SSLConnectionSocketFactory trustSelfSignedSocketFactory = new SSLConnectionSocketFactory(
                    new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
                    new PoolingManager.TrustAllHostNameVerifier());

            socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", trustSelfSignedSocketFactory)
                    .build();

        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }

        PoolingHttpClientConnectionManager poolingManager = (socketFactoryRegistry != null) ?
                new PoolingHttpClientConnectionManager(socketFactoryRegistry) :
                new PoolingHttpClientConnectionManager();

        return poolingManager;
    }
}
