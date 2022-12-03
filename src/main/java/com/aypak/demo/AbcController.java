package com.aypak.demo;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
public class AbcController {

    private List<String> TOKEN = new ArrayList<>();

    @GetMapping("/abc")
    public Dict test(HttpServletRequest req) {
        String token = req.getHeader("token");
        if (!this.TOKEN.contains(token)) {
            throw new RuntimeException("没有登录");
        }
        System.out.println("来了老弟" + token);
        return Dict.create().set("aa", "vbb").set("bb", "bb");
    }

    @PostMapping("/login")
    public Dict login(HttpServletResponse res, HttpServletRequest req) {
        Map<String, String[]> parameterMap = req.getParameterMap();
        parameterMap.forEach((a, b) -> {
            System.out.println(a + ":" + Arrays.toString(b));
        });
        String token = RandomUtil.randomString(20);
        log.info("token:{}", token);
        res.setHeader("token", token);
        this.TOKEN.add(token);
        if (this.TOKEN.size() > 5) {
            this.TOKEN.remove(4);
        }
        System.out.println("token：" + token);
        return Dict.create().set("suc", "ok").set("token", token);
    }

    AtomicInteger index = new AtomicInteger(1);

}
