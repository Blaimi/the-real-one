package de.we2.am.therealone.web.filter;

import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.util.ErrorUtil;
import de.we2.am.therealone.web.to.error.ErrorCodeTO;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Priority(Priorities.AUTHENTICATION)
public class JWTAuthenticationFilter implements ContainerRequestFilter {

    private final Logger logger = LogManager.getLogger(JWTAuthenticationFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String header = requestContext.getHeaderString("Authorization");
        if (header == null) {
            return;
        }

        if (!header.startsWith("Bearer ")) {
            // We don't log the header for (hopefully obvious reasons)
            StringMapMessage log = new StringMapMessage()
                    .with(Constant.KEY_MESSAGE, "Unsupported authentication method")
                    .with(Constant.KEY_REQUEST_URI, requestContext.getUriInfo().getRequestUri())
                    .with(Constant.KEY_REQUEST_METHOD, requestContext.getMethod());

            requestContext.abortWith(ErrorUtil.error(Response.Status.UNAUTHORIZED, ErrorCodeTO.NOT_AUTHORIZED, "Unsupported authentication method", "Only Bearer with JWT is allowed", log, logger));
            return;
        }

        String token = header.substring("Bearer ".length());
        try {
            Algorithm algorithm = createAlgorithm();

            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);

            Claim sub = jwt.getClaim("sub");
            SecurityContext oldContext = requestContext.getSecurityContext();
            requestContext.setSecurityContext(new SecurityContext() {
                private final UserPrincipal userPrincipal = sub::asString;

                @Override
                public Principal getUserPrincipal() {
                    return userPrincipal;
                }

                @Override
                public boolean isUserInRole(String role) {
                    // TODO: 17.11.23 Load roles from somewhere
                    return true;
                }

                @Override
                public boolean isSecure() {
                    return oldContext.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return "Bearer";
                }
            });
        } catch (AlgorithmMismatchException | SignatureVerificationException | MissingClaimException |
                 MalformedURLException e) {
            // Those errors are application errors
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            StringMapMessage log = new StringMapMessage()
                    .with(Constant.KEY_MESSAGE, "Unexpected error during authentication")
                    .with(Constant.KEY_ERROR_STACK_TRACE, stringWriter);
            requestContext.abortWith(ErrorUtil.error(Response.Status.UNAUTHORIZED, ErrorCodeTO.NO_NEED_TO_KNOW, "Unexpected authentication error", "Please report this to the server administrator and include the error trace", log, logger::error));
        } catch (TokenExpiredException e) {
            // Those errors are user errors
            StringMapMessage log = new StringMapMessage()
                    .with(Constant.KEY_MESSAGE, "User token has expired")
                    .with(Constant.KEY_TOKEN_EXPIRED_ON, e.getExpiredOn());
            requestContext.abortWith(ErrorUtil.error(Response.Status.UNAUTHORIZED, ErrorCodeTO.NOT_AUTHORIZED, "JWT expired", String.format("Your token expired on %s", e.getExpiredOn()), log, logger));
        }
    }


    private Algorithm createAlgorithm() throws MalformedURLException {
        // TODO: 17.11.23 Make url more configuration
        JwkProvider jwkProvider = new JwkProviderBuilder(new URL(String.format("http://%s/auth/realms/%s/protocol/openid-connect/certs", System.getenv("KEYCLOAK_HOST"), System.getenv("KEYCLOAK_REALM")))).build();
        return Algorithm.RSA256(new RSAKeyProvider() {
            @Override
            public RSAPublicKey getPublicKeyById(String kid) {
                try {
                    return (RSAPublicKey) jwkProvider.get(kid).getPublicKey();
                } catch (JwkException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public RSAPrivateKey getPrivateKey() {
                throw new UnsupportedOperationException("Provider can only be used to verify jwt tokens");
            }

            @Override
            public String getPrivateKeyId() {
                throw new UnsupportedOperationException("Provider can only be used to verify jwt tokens");
            }
        });
    }
}
