package com.zego.controller;

import com.zego.entity.MultieloadParames;
import com.zego.entity.RechargeOrder;
import com.zego.entity.UserInfo;
import com.zego.service.RechargeOrderService;
import com.zego.service.RedisService;
import com.zego.service.UserInfoService;
import com.zego.service.impl.RabbitMqProducer;
import com.zego.service.impl.SendLogMessageMQ;
import com.zego.entity.RechargeLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;

import java.util.Date;
import java.util.List;

@RestController
public class TestController {

    private static final Logger logger = Logger.getLogger(TestController.class);

    @Autowired
    private SendLogMessageMQ sendLogMessageMQ;

    @Autowired
    private RabbitMqProducer rabbitMqProducer;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RechargeOrderService rechargeOrderService;

    @GetMapping("/test")
    public String Test(){
        logger.info("test!");
        return "test";
    }

    @GetMapping("/user/{id}")
    public List<UserInfo> user(@PathVariable(value = "id") String id){
        return userInfoService.selectUserInfo(id);
    }

    @GetMapping("/rechargeOrder")
    public RechargeOrder rechargeOrder(){
        logger.info("test!");
        return rechargeOrderService.selectByOrderNo("2202001062102340514334677001888");
    }

    @GetMapping("/updateOrder")
    public String updateOrder(){
        logger.info("test!");
        RechargeOrder rechargeOrder = new RechargeOrder();
        rechargeOrder.setOrderNo("2202001062102340514334677001888");
        rechargeOrder.setInterfaceId(12);
        rechargeOrder.setOrderStatus(10);
        try {
            rechargeOrderService.updateOrder(rechargeOrder);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping("/redisTest")
    public String redisTest(String key, String value, long timeout){
        redisService.set(key, value, timeout);
        return String.valueOf(redisService.get(key));
    }

    @GetMapping("/redis")
    public Object redis(String key){
        System.out.println(redisService.get(key));
        return redisService.get(key);
    }

    @GetMapping("/sendLogMessage")
    public String sendLogMessage(String name) {
        RechargeLog log = new RechargeLog();
        log.setUserId((long)11);
        log.setOrderId("1111");
        log.setType((short)1);
        log.setLogInfo(name);
        log.setCreateTime(new Date());
        sendLogMessageMQ.sendLogMessageMQ(log);
        return "success";
    }

    @GetMapping("/sendRabbitMessage")
    public String sendRabbitMessage() {
        MultieloadParames multieloadParames = new MultieloadParames();
        multieloadParames.setUrl("aaaaa");
        multieloadParames.setToken("aaaaaaa");
        String mobile = "000000";
        multieloadParames.setMobile(mobile);
        multieloadParames.setN(1);
        multieloadParames.setAmount("900");
        multieloadParames.setAgentId("123456");
        multieloadParames.setFmt("json");
        multieloadParames.setOpt("1");
        try {
            rabbitMqProducer.sendMessageToQueue("zegobirdRechargeQueueKey", multieloadParames);
        }catch(Exception e) {
            logger.info(e.getMessage());
        }
        return "ok";
    }
}