package org.joychou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;


@SuppressWarnings("LanguageDetectionInspection")
@ServletComponentScan // do filter
@SpringBootApplication
// @EnableEurekaClient  // 测试Eureka请打开注释，防止控制台一直有warning
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    /**
     * https://github.com/JoyChou93/java-sec-code
     */
    public static void main(String[] args) throws Exception {
        setProxy();
        SpringApplication.run(Application.class, args);
    }

    public static void setProxy() throws Exception {
        // 命令行设置代理, 代理端口默认80, nonProxy:不代理的主机
        // http
        // -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=8889 -Dhttp.proxyUser= -Dhttp.proxyPassword="" -Dhttp.nonProxyHosts="localhost|*.x.com|192.168.*.*"
        // https
        // -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=8889 -Dhttps.proxyUser=user -Dhttps.proxyPassword="password" -Djavax.net.ssl.trustStore=c:/cacerts -Djavax.net.ssl.trustStorePassword=changeit
        // 系统代理
        // -Djava.net.useSystemProxies=true
        // socks5, 认证参数无效, -DsocksProxySet=true -DsocksProxyVersion=5|4
        // -DsocksProxyHost=127.0.0.1 -DsocksProxyPort=8889 -Djava.net.socks.username=user -Djava.net.socks.password=""

        // 代码设置代理, 必须指定http和https代理
        // System.setProperty("proxySet", "true");

        // System.setProperty("proxyHost", "127.0.0.1");
        // System.setProperty("proxyPort", "8889");
        // System.setProperty("proxyUser", "ss");
        // System.setProperty("proxyPassword", "1");

        // System.setProperty("http.proxyHost", "127.0.0.1");
        // System.setProperty("http.proxyPort", "8888");
        // System.setProperty("https.proxyHost", "127.0.0.1");
        // System.setProperty("https.proxyPort", "8888");
        // 认证不工作
        // System.setProperty("http.proxyUser", "ss");
        // System.setProperty("http.proxyPassword", "1");
        // System.setProperty("https.proxyUser", "ss");
        // System.setProperty("https.proxyPassword", "1");


        // socks 代理默认为 socks5
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "8889");

        // System.setProperty("socksProxyVersion", "5");
        // System.setProperty("java.net.socks.username", "ss");
        // System.setProperty("java.net.socks.password", "1");

        // java.net.URL url = new java.net.URL("https://x.com");
        // java.net.URLConnection con = url.openConnection();
    }

    public static void unsetProxy() {
        // 代理使用完成后，取消代理
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyHost");
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyHost");
    }

}