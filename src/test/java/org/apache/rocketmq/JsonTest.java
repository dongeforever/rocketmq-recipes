package org.apache.rocketmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.Console;
import java.util.Date;
import org.junit.Test;

public class JsonTest {

    static class TTTT {
        private Date billDate;

        public Date getBillDate() {
            return billDate;
        }

        public void setBillDate(Date billDate) {
            this.billDate = billDate;
        }
    }

    @Test(timeout = 4000)
    public void testToJSONStringToJavaObject() throws Throwable {
        Console console  = System.console();
        System.out.println(console);
        System.out.println(console.readLine());
        /*JSONObject jsonObject = new JSONObject();
        jsonObject.put("billDate","20201130114323284");
        TTTT myObject = JSON.toJavaObject(jsonObject, TTTT.class);
        System.out.println(myObject.billDate);
        System.out.println(JSON.toJSONString(myObject));*/
        Thread.sleep(10000);
    }
}
