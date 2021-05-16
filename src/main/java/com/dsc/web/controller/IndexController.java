package com.dsc.web.controller;

import com.dsc.util.CalculateUtil;
import com.dsc.util.DownloadUtil;
import com.dsc.web.service.TradeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    TradeInfoService tradeInfoService;


    @GetMapping("home")
    public String index() {
        return "index";
    }


    @RequestMapping("index")
    public ModelAndView index(ModelMap model) {
        ModelAndView mv = new ModelAndView();
        model.addAttribute("message", "Hello World");
        mv.setViewName("index"); //页面路径：/templates/index.html
        return mv;
    }


    @RequestMapping(value = "test")
    public ModelAndView test() {
        return new ModelAndView("test", "message", null);
    }


    @RequestMapping(value = "test1")
    public ModelAndView test1() {
        return new ModelAndView("test1", "message", null);
    }


    @RequestMapping(value = "getTradeMap.json")
    @ResponseBody
    public Map<String, Object> getTradeMap() {
        return tradeInfoService.showAllDataFromDB();
    }


    @GetMapping("init")
    public ModelAndView initData(@RequestParam("firstToken") String firstToken) {
        tradeInfoService.initData(firstToken);
        logger.info(" recursive find data for {}  completed. ", firstToken);
        return new ModelAndView("success", "message", null);
    }
}
