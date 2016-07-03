package com.yunguchang.sam;

import com.google.common.primitives.Bytes;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.yunguchang.model.common.Fleet;
import com.yunguchang.model.common.User;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Created by gongy on 2015/10/4.
 */
@ApplicationScoped
public class JwtUtil {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    public static final String JWT_SUBJECT = "fleet-manager";
    public static final String USER_FIELD = "user";
    private byte[] signedKey;
    private byte[] encryptedKey;
    @Inject
    @ConfigProperty(name = "secrets.key", defaultValue = "default jwt key")
    private String secretsKey="default jwt key";


    private int validHours=24;

    @PostConstruct
    public void init() {
        signedKey = Bytes.ensureCapacity(secretsKey.getBytes(UTF8), 32, 0);
        encryptedKey = Arrays.copyOf(signedKey, 32);

    }

    public String getSecretsKey() {
        return secretsKey;
    }

    public void setSecretsKey(String secretsKey) {
        this.secretsKey = secretsKey;
    }

    public int getValidHours() {
        return validHours;
    }

    public void setValidHours(int validHours) {
        this.validHours = validHours;
    }

    public String sign(User user) {
        try {

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            builder.subject(JWT_SUBJECT)
                    .claim(USER_FIELD, user)
                    .expirationTime(DateTime.now().plusHours(validHours).toDate());
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), builder.build());
            signedJWT.sign(new MACSigner(signedKey));
            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
                            .contentType("JWT") // required to signal nested JWT
                            .compressionAlgorithm(CompressionAlgorithm.DEF)
                            .build(),
                    new Payload(signedJWT));
            jweObject.encrypt(new DirectEncrypter(encryptedKey));
            return jweObject.serialize();
        } catch (JOSEException e) {
            return null;
        }


    }


    public User getUser(String s) {
        try {
            JWEObject jweObject = JWEObject.parse(s);
            jweObject.decrypt(new DirectDecrypter(encryptedKey));
            SignedJWT signedJWT = jweObject.getPayload().toSignedJWT();
            if (signedJWT == null) {
                return null;
            }
            if (!signedJWT.verify(new MACVerifier(signedKey))) {
                return null;
            }
            ;

            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();


            if (!JWT_SUBJECT.equals(jwtClaimsSet.getSubject())) {
                return null;
            }
            if (jwtClaimsSet.getExpirationTime() != null && new DateTime(jwtClaimsSet.getExpirationTime()).isBeforeNow()) {
                return null;
            }
            Object content = jwtClaimsSet.getClaim(USER_FIELD);
            User user = new User();
            BeanUtils.copyProperties(user, content);
            return user;
        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        } catch (ParseException e) {
        } catch (JOSEException e) {
        }
        return null;

    }



}
