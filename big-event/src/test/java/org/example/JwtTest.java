package org.example;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTest {
    @Test
    public void testGen(){
        Map<String,Object> claims = new HashMap<>();
        claims.put("id","1");
        claims.put("username","张三");
        //生成jwt
        String token = JWT.create()
                .withClaim("user",claims)//添加载荷
                .withExpiresAt(new Date(System.currentTimeMillis()+1000+60*60*12))//添加过期时间
                .sign(Algorithm.HMAC256("itheima"));//指定算法
        System.out.println(token);
    }
    @Test
    public void testParse(){
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJ1c2VyIjp7ImlkIjoiMSIsInVzZXJuYW1lIjoi5byg5LiJIn0sImV4cCI6MTczNzcxMzk0Nn0" +
                ".sKcDW9yE6qhVvTPYFhBYkdgyLiXjVbaU3F8YsRDAdGo";
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("itheima")).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);//验证token，生成一个解析后的jwt对象
        Map<String, Claim> claims = decodedJWT.getClaims();
        System.out.println(claims.get("user"));
    }
}
