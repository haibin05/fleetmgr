package com.yunguchang.test.jwt;

import com.google.common.primitives.Bytes;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.yunguchang.model.common.Roles;
import com.yunguchang.model.common.User;
import com.yunguchang.sam.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

//import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Created by 禕 on 2015/10/28.
 */
@RunWith(JUnit4.class)
public class JtwTest {


    @Test
    public void testJOSEJWT() throws JOSEException, ParseException {

        User user = new User();
        user.setUserId("admin");
        user.setUserName("admin");
        user.setRoles(new String[]{"admin", "role2"});
        //Security.addProvider(new BouncyCastleProvider());

        Charset UTF8 = Charset.forName("UTF-8");
        //byte[] keybytes = "default jwt key".getBytes(UTF8);
        byte[] keybytes = Bytes.ensureCapacity("0123456789012345678901234567890123456789012345678901234567890123456789".getBytes(UTF8), 32, 0);;
        //byte[] keybytes = "default jwt key".getBytes(UTF8);


        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        builder.subject("alice")
                .claim("user", user)
                .expirationTime(new Date());

        //claimsSet.setSubject("alice");


        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), builder.build());
        signedJWT.sign(new MACSigner(keybytes));


        JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
                        .compressionAlgorithm(CompressionAlgorithm.DEF)
                        .contentType("JWT") // required to signal nested JWT
                        .build(),
                new Payload(signedJWT));

        jweObject.encrypt(new DirectEncrypter(Arrays.copyOf(keybytes, 32)));
        String jweString = jweObject.serialize();

        System.out.println(jweString);

        jweObject = JWEObject.parse(jweString);
        jweObject.decrypt(new DirectDecrypter(Arrays.copyOf(keybytes, 32)));
        signedJWT = jweObject.getPayload().toSignedJWT();
        assertNotNull("Payload not a signed JWT", signedJWT);
        assertTrue(signedJWT.verify(new MACVerifier(keybytes)));
        JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

        assertEquals("alice", jwtClaimsSet.getSubject());
        jwtClaimsSet.getClaim("user");
        jwtClaimsSet.getExpirationTime();

    }


    @Test
    public void generateAdminToken(){
        JwtUtil jwtUtil=new JwtUtil();
        jwtUtil.setValidHours(20*365*24);
        jwtUtil.init();
        User user = new User();
        user.setUserId("admin");
        user.setUserName("admin");
        user.setEid("admin");
        user.setRoles(Roles.ADMIN);
        String token =jwtUtil.sign(user);
        System.out.println(token);
    }


    @Test
    public void generateSyncToken(){
        JwtUtil jwtUtil=new JwtUtil();
        jwtUtil.setValidHours(20*365*24);
        jwtUtil.init();
        User user = new User();
        user.setUserId("sync");
        user.setUserName("sync");
        user.setEid("sync");
        user.setRoles(Roles.ADMIN);
        String token =jwtUtil.sign(user);
        System.out.println(token);
    }

}
