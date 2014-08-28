package com.ejushang.steward.scm.web;

import com.ejushang.steward.common.exception.StewardBusinessException;
import com.ejushang.steward.common.util.JsonResult;
import com.ejushang.steward.ordercenter.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: tin
 * Date: 14-4-10
 * Time: 下午2:38
 */
@Controller
@RequestMapping("/province")
public class ProvinceController {
    @Autowired
    private ProvinceService provinceService;

    @RequestMapping("/findAll")
    @ResponseBody
    public JsonResult findProvinceAll() {
        return new JsonResult(true).addList(provinceService.findProvinceAll());

    }

    @RequestMapping("/list_city")
    @ResponseBody
    public JsonResult listCityByProvinceId(Integer provinceId) {
        if (provinceId == null) {
            throw new StewardBusinessException("省份id不能为空");
        }
        return new JsonResult(true).addList(provinceService.listCityByProvinceId(provinceId));
    }

    @RequestMapping("/list_area")
    @ResponseBody
    public JsonResult listAreaByCityId(Integer cityId) {
        if (cityId == null) {
            throw new StewardBusinessException("市id不能为空");
        }
        return new JsonResult(true).addList(provinceService.listAreaByCityId(cityId));
    }

}
