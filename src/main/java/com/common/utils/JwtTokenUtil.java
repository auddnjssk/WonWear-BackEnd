package com.common.utils;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtTokenUtil {
	
	@Value("${jwtSecretKey}")
	private String jwtSecretKey;
	
    private static final long EXPIRATION_ACCESS_TIME = 1000 * 60 * 60; // 1시간
    

    private static final long EXPIRATION_REFRESH_TIME = 1000 * 60 * 60 * 24 * 30; // 30일

    // Access Token 생성
    public String generateAccessToken(Map<String,Object> userInfoMap) {
        long currentTimeMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeMillis);
        Date expiryDate = new Date(currentTimeMillis + EXPIRATION_ACCESS_TIME);

        // jwtSecretKey를 SecretKey 객체로 변환
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());

        // JWT 토큰 생성
        return Jwts.builder()
                .setSubject((String) userInfoMap.get("userId"))                  // 사용자 ID (sub)
                .claim("userName", (String) userInfoMap.get("user_nm"))          // 사용자 이름 (custom claim)
                .claim("userId", (String) userInfoMap.get("user_id"))          // 사용자 이름 (custom claim)
                .setIssuedAt(now)                    // 발급 시간 (iat)
                .setExpiration(expiryDate)           // 만료 시간 (exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)  // SecretKey를 사용하여 서명
                .compact();

    }
    // Refresh Token 생성
    public String generateRefreshToken(Map<String,Object> userInfoMap) {
    	long currentTimeMillis = System.currentTimeMillis();
    	Date now = new Date(currentTimeMillis);
    	Date expiryDate = new Date(currentTimeMillis + EXPIRATION_REFRESH_TIME);
    	
    	// jwtSecretKey를 SecretKey 객체로 변환
    	SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    	
    	// JWT 토큰 생성
    	return Jwts.builder()
    			.setSubject((String) userInfoMap.get("userId"))                  // 사용자 ID (sub)
    			.claim("userName", (String) userInfoMap.get("user_nm"))          // 사용자 이름 (custom claim)
    			.setIssuedAt(now)                    // 발급 시간 (iat)
    			.setExpiration(expiryDate)           // 만료 시간 (exp)
    			.signWith(secretKey, SignatureAlgorithm.HS256)  // SecretKey를 사용하여 서명
    			.compact();
    	
    }
    
    // JWT 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
    	
	    Key key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)  // 비밀 키로 서명 검증
                    .build()
                    .parseClaimsJws(token)  // 토큰 파싱
                    .getBody();
            return claims.get("userId").toString();
        } catch (ExpiredJwtException  e) {
            // JWT 파싱 중 오류가 발생한 경우 예외 처리
            System.out.println("유효기간 만료 : " + e.getMessage());
            return null;  // 오류 발생 시 null 반환
        }catch (Exception e) {  // 그 외 예외 처리
            System.out.println("토큰 검증 실패: " + e.getMessage());
            return null;
        }
    }
    
    // String으로 들어온 Token Json으로 변환후 사용자 ID 추출
    public String getUserIdFromStringToken(String authorization) throws JsonMappingException, JsonProcessingException {
    	
    	if(authorization.length()<12) return null;
    	System.out.println("authorization.length()"+authorization.length());
    	String token =  StringTk2Json(authorization.substring(7));
    			
    	Key key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    	
    	try {
    		Claims claims = Jwts.parserBuilder()
    				.setSigningKey(key)  // 비밀 키로 서명 검증
    				.build()
    				.parseClaimsJws(token)  // 토큰 파싱
    				.getBody();
    		return claims.get("userId").toString();
    	} catch (ExpiredJwtException  e) {
    		// JWT 파싱 중 오류가 발생한 경우 예외 처리
    		System.out.println("유효기간 만료 : " + e.getMessage());
    		return null;  // 오류 발생 시 null 반환
    	}catch (Exception e) {  // 그 외 예외 처리
    		System.out.println("토큰 검증 실패: " + e.getMessage());
    		return null;
    	}
    }

    // 유효성 검증
    public boolean isTokenExpired(String token) {
    	
    	SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    	
        Date expiration = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());  // 만료 시간 비교
    }
    
    // 무결성 확인 (서명 검증)
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token);
            return true; // 유효한 토큰
        } catch (SignatureException e) {
            return false; // 서명 검증 실패
        }
    }
    
    
    /** eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmFtZSI6IuyYpOycpO2drCIsImlhdCI6MTczNjQ5MTYxNiwiZXhwIjoxNzM2NDk1MjE2fQ.TDTNudfIsFeNhx9jSWHL-n_zcem2BjAUTaWQxt3E9to
     * JWT 토큰의 Header.Payload.Signature중 Payload를 return
     * @param token
     * @return 디코딩된 Header
     */
    // 토큰은 Header.Payload.Signature 로 나뉘어져있음
    public String decodePayload(String token) {
    	
    	String[] tokenParts = token.split("\\.");
    	
        if (tokenParts.length < 2 ) {
            throw new IllegalArgumentException("올바른 JWT의 형식이 아닙니다.");
        }
    	
        return decodeBase64Url(tokenParts[1]);
        
    }
    
	private String decodeBase64Url(String tokenParts) {
		// TODO Auto-generated method stub
		
        String base64 = tokenParts.replace('-', '+').replace('_', '/');
        // 디코딩
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        		
		return new String(decodedBytes);
	}
	
	// 스트링으로 들어온 토큰 Json으로 변환
	public String StringTk2Json(String token ) throws JsonMappingException, JsonProcessingException {
		
		if(ObjectUtil.isNotEmpty(token)) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(token);
	
			String accessToken = node.get("value").asText();
			return accessToken;
		}else {
			return null;
		}
	}
	

    
}
