package com.energizeglobal.egsinterviewtest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import static com.energizeglobal.egsinterviewtest.config.ApplicationDefaults.*;

@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private final Security security = new Security();

    private final Cache cache = new Cache();

    private final CorsConfiguration cors = new CorsConfiguration();

    public ApplicationProperties() {
    }

    public Security getSecurity() {

        return this.security;
    }

    public Cache getCache() {

        return this.cache;
    }

    public CorsConfiguration getCors() {

        return this.cors;
    }

    public static class Security {

        private String contentSecurityPolicy = "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";
        private final ApplicationProperties.Security.ClientAuthorization clientAuthorization = new ApplicationProperties.Security.ClientAuthorization();
        private final ApplicationProperties.Security.Authentication authentication = new ApplicationProperties.Security.Authentication();

        public Security() {
        }

        public ApplicationProperties.Security.ClientAuthorization getClientAuthorization() {

            return this.clientAuthorization;
        }

        public ApplicationProperties.Security.Authentication getAuthentication() {

            return this.authentication;
        }

        public String getContentSecurityPolicy() {

            return this.contentSecurityPolicy;
        }

        public void setContentSecurityPolicy(String contentSecurityPolicy) {

            this.contentSecurityPolicy = contentSecurityPolicy;
        }

        public static class Authentication {

            private final ApplicationProperties.Security.Authentication.Jwt jwt = new ApplicationProperties.Security.Authentication.Jwt();

            public Authentication() {
            }

            public ApplicationProperties.Security.Authentication.Jwt getJwt() {
                return this.jwt;
            }

            public static class Jwt {
                private String secret;
                private String base64Secret;
                private long tokenValidityInSeconds;
                private long tokenValidityInSecondsForRememberMe;

                public Jwt() {
                    this.secret = com.energizeglobal.egsinterviewtest.config.ApplicationDefaults.Security.Authentication.Jwt.secret;
                    this.base64Secret = com.energizeglobal.egsinterviewtest.config.ApplicationDefaults.Security.Authentication.Jwt.base64Secret;
                    this.tokenValidityInSeconds = 1800L;
                    this.tokenValidityInSecondsForRememberMe = 2592000L;
                }

                public String getSecret() {
                    return this.secret;
                }

                public void setSecret(String secret) {
                    this.secret = secret;
                }

                public String getBase64Secret() {
                    return this.base64Secret;
                }

                public void setBase64Secret(String base64Secret) {
                    this.base64Secret = base64Secret;
                }

                public long getTokenValidityInSeconds() {
                    return this.tokenValidityInSeconds;
                }

                public void setTokenValidityInSeconds(long tokenValidityInSeconds) {
                    this.tokenValidityInSeconds = tokenValidityInSeconds;
                }

                public long getTokenValidityInSecondsForRememberMe() {
                    return this.tokenValidityInSecondsForRememberMe;
                }

                public void setTokenValidityInSecondsForRememberMe(long tokenValidityInSecondsForRememberMe) {
                    this.tokenValidityInSecondsForRememberMe = tokenValidityInSecondsForRememberMe;
                }
            }
        }

        public static class ClientAuthorization
        {
            private String accessTokenUri;
            private String tokenServiceId;
            private String clientId;
            private String clientSecret;

            public ClientAuthorization() {

                this.accessTokenUri = ApplicationDefaults.Security.ClientAuthorization.accessTokenUri;
                this.tokenServiceId = ApplicationDefaults.Security.ClientAuthorization.tokenServiceId;
                this.clientId = ApplicationDefaults.Security.ClientAuthorization.clientId;
                this.clientSecret = ApplicationDefaults.Security.ClientAuthorization.clientSecret;
            }

            public String getAccessTokenUri() {

                return this.accessTokenUri;
            }

            public void setAccessTokenUri(String accessTokenUri) {

                this.accessTokenUri = accessTokenUri;
            }

            public String getTokenServiceId() {

                return this.tokenServiceId;
            }

            public void setTokenServiceId(String tokenServiceId) {

                this.tokenServiceId = tokenServiceId;
            }

            public String getClientId() {

                return this.clientId;
            }

            public void setClientId(String clientId) {

                this.clientId = clientId;
            }

            public String getClientSecret() {

                return this.clientSecret;
            }

            public void setClientSecret(String clientSecret) {

                this.clientSecret = clientSecret;
            }
        }
    }

    public static class Cache {

        private final Ehcache ehcache = new Ehcache();

        public Cache() {
        }

        public Ehcache getEhcache() {

            return this.ehcache;
        }

        public static class Ehcache {

            private int timeToLiveSeconds = 3600;
            private long maxEntries = 100L;

            public Ehcache() {
            }

            public int getTimeToLiveSeconds() {

                return this.timeToLiveSeconds;
            }

            public void setTimeToLiveSeconds(int timeToLiveSeconds) {

                this.timeToLiveSeconds = timeToLiveSeconds;
            }

            public long getMaxEntries() {

                return this.maxEntries;
            }

            public void setMaxEntries(long maxEntries) {

                this.maxEntries = maxEntries;
            }
        }
    }

}
