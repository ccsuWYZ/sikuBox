package com.siku.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Hashtable;
import java.util.Map;

public class WeChatUtil {
    private static final String APP_ID = "";
    private static final String APP_SECRET = "";
    private static final String GRANT_TYPE = "";

    /**
     * 像微信服务器发送请求，获取 sessionKey、openId
     * @param code
     *        本次登录请求的小程序传来的标识（保证传来的 code 绝不为空或是空字符串）
     * @return
     *        Map key:sessionKey、openId。如果不能正确地获取到 sessionKey、openId，
     *        返回的 map 中，值为 null。
     */
    public static Map<String, String> acquireSessionKeyAndOpenId(String code){
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new Hashtable<>();
        try{
            String url = String.format("https://api.weixin.qq.com/sns/jscode2session" +
                    "?appid=%s&secret=%s&js_code=%s&grant_type=%s", APP_ID, APP_SECRET, code, GRANT_TYPE);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<String> response = restTemplate.getForEntity( url, String.class);
            String[] results = response.getBody().split("\"");
            map.put("sessionKey", results[4]);
            map.put("openid", results[7]);
        } catch (Exception e){
            map.put("sessionKey", null);
            map.put("openid", null);
        }
        return map;
    }

}
