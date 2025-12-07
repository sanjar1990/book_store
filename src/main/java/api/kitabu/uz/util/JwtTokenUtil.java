package api.kitabu.uz.util;

import api.kitabu.uz.dto.JwtDTO;
import api.kitabu.uz.enums.ProfileRole;
import io.jsonwebtoken.*;


import java.util.Date;
import java.util.List;

public class JwtTokenUtil {

    private static final int tokenLifeTime = 1000 * 3600 * 24 * 3; // 3-day
//    private static final int tokenLifeTime = 1000 ;
    private static final String secretKey = "kitabu";

    public static String encode(String phone, ProfileRole role) {
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setIssuedAt(new Date());
        jwtBuilder.signWith(SignatureAlgorithm.HS512, secretKey);

        jwtBuilder.claim("phone", phone);
        jwtBuilder.claim("role", role);

        jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + (tokenLifeTime)));
        jwtBuilder.setIssuer("kitabu  test portali");
        return jwtBuilder.compact();
    }

    public static String encode(String phone, List<ProfileRole> roles) {
        String roleStr = "";
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setIssuedAt(new Date());
        jwtBuilder.signWith(SignatureAlgorithm.HS512, secretKey);

        jwtBuilder.claim("phone", phone);

        for (int i = 0; i < roles.size(); i++) {
            if(i == roles.size()-1){
                roleStr = roleStr + roles.get(i);
            }else
             roleStr = roleStr + roles.get(i) +", ";
        }

        jwtBuilder.claim("roles", roleStr);


        jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + (tokenLifeTime)));
        jwtBuilder.setIssuer("kitabu  test portali");
        return jwtBuilder.compact();
    }

    public static String encode(Integer profileId) {
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setIssuedAt(new Date());
        jwtBuilder.signWith(SignatureAlgorithm.HS512, secretKey);

        jwtBuilder.claim("id", profileId);
        int tokenLifeTime = 1000 * 3600 * 24; // 1-day
        jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + (tokenLifeTime)));
        jwtBuilder.setIssuer("Mazgi");

        return jwtBuilder.compact();
    }

    public static String encode(Integer profileId, String email) {
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setIssuedAt(new Date());
        jwtBuilder.signWith(SignatureAlgorithm.HS512, secretKey);

        jwtBuilder.claim("id", profileId);
        jwtBuilder.claim("email", email);
        int tokenLifeTime = 1000 * 3600 * 24; // 1-day
        jwtBuilder.setExpiration(new Date(System.currentTimeMillis() + (tokenLifeTime)));
        jwtBuilder.setIssuer("Mazgi");

        return jwtBuilder.compact();
    }

    public static JwtDTO decode(String token) {
        JwtParser jwtParser = Jwts.parser();
        jwtParser.setSigningKey(secretKey);

        Jws<Claims> jws = jwtParser.parseClaimsJws(token);

        Claims claims = jws.getBody();

        String username = (String) claims.get("login");

        String role = (String) claims.get("role");
        ProfileRole profileRole = ProfileRole.valueOf(role);

        return new JwtDTO(username, profileRole);
    }

    public static String decodePhone(String token) {

        JwtParser jwtParser = Jwts.parser();
        jwtParser.setSigningKey(secretKey);

        Jws<Claims> jws = jwtParser.parseClaimsJws(token);

        Claims claims = jws.getBody();

//        Integer id = (Integer) claims.get("phone");
        return (String) claims.get("phone");
    }


   /* public static JwtEmailChangeDTO decodeMailGetUserIdAndEmailAddress(String token) {

        JwtParser jwtParser = Jwts.parser();
        jwtParser.setSigningKey(secretKey);

        Jws<Claims> jws = jwtParser.parseClaimsJws(token);

        Claims claims = jws.getBody();

        Integer id = (Integer) claims.get("id");
        String email = (String) claims.get("login");


        return new JwtEmailChangeDTO(id,email);


    }

    */
}
