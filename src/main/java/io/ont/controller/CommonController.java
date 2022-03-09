package io.ont.controller;

import io.ont.bean.Result;
import io.ont.entity.Holder;
import io.ont.service.CommonService;
import io.ont.utils.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping
@CrossOrigin
public class CommonController {
    @Autowired
    private CommonService commonService;

    @GetMapping("/getAssetHolder")
    public Result getAssetHolder(String qid, String contract, Integer from, Integer count) {
        String method = "getAssetHolder";
        List<Holder> result = commonService.getAssetHolder(contract, from, count);
        return new Result(qid, method, ErrorInfo.SUCCESS.code(), "", result);
    }

    @GetMapping("/getAssetHolderCount")
    public Result getAssetHolderCount(String qid, String contract) {
        String method = "getAssetHolder";
        Integer result = commonService.getAssetHolderCount(contract);
        return new Result(qid, method, ErrorInfo.SUCCESS.code(), "", result);
    }
}
