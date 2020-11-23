package com.cafe24.app.sample.controller;

import com.cafe24.app.sample.dto.CodeQueryString;
import com.cafe24.app.sample.dto.MallInfo;
import com.cafe24.app.sample.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class AppController {
    private static final Logger log = LoggerFactory.getLogger(AppController.class);

    @Autowired
    AppService service;

    /**
     * 인덱스 페이지
     *
     * @return
     */
    @GetMapping("/")
    public String index() {
        log.info("index");

        return "index";
    }

    /**
     * Cafe24 인증 요청
     *
     * @param request
     * @param MallInfo
     * @return
     */
    @GetMapping("/auth")
    public String authorization(HttpServletRequest request, MallInfo MallInfo) {
        log.info("authorization Query string : {}", request.getQueryString());
        log.info("authorization MallInfo : {}", MallInfo.toString());

        String result = "error";
        HttpSession session = request.getSession();
        session.setAttribute("MallInfo", MallInfo);

        boolean is_valid_hmac = service.validationCheck(request.getQueryString(), MallInfo.getHmac());

        if (!is_valid_hmac) return result;

        if (service.isValidAccessToken(MallInfo.getMall_id())) {
            request.setAttribute("MallInfo", MallInfo);
            result = "admin";
        } else {
            result = service.getCodeRedirectUrl(MallInfo.getMall_id(), request.getRequestedSessionId());
        }

        log.info("authorization return url : {}", result);

        return result;
    }

    /**
     * 관리자 페이지
     *
     * @param codeQueryStr
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/admin")
    public String admin(CodeQueryString codeQueryStr, HttpSession session, Model model) {
        log.info("admin codeQueryStr : {}", codeQueryStr);

        //데이터위변조 체크 - CSRF 공격대비
        if(!codeQueryStr.getState().equals(session.getId()))return "error";

        MallInfo mallInfo = (MallInfo) session.getAttribute("MallInfo");
        log.info("admin session.MallInfo : {}", mallInfo.toString());

        if (!service.isValidAccessToken(mallInfo.getMall_id()))
            service.setAccessToken(mallInfo.getMall_id(), codeQueryStr.getCode());

        model.addAttribute("MallInfo", mallInfo);

        return "admin";
    }

}

