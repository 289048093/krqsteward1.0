package com.ejushang.steward.ordercenter.service;

import com.ejushang.steward.common.domain.Area;
import com.ejushang.steward.common.domain.City;
import com.ejushang.steward.common.domain.Province;
import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.common.genericdao.search.Search;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: tin
 * Date: 14-4-10
 * Time: 下午2:24
 */
@Service
@Transactional
public class ProvinceService {

    private static final Logger logger = Logger.getLogger(ProvinceService.class);
    @Autowired
    private GeneralDAO generalDAO;

    public List<Province> findProvinceAll() {
        if (logger.isInfoEnabled()) {
            logger.info("ProvinceService类中的findProvinceAll查询全部");
        }
        Search search = new Search(Province.class).setCacheable(true);
        return generalDAO.search(search);
    }

    /**
     * 查询省下面的所有市
     *
     * @param provinceId
     * @return
     */
    public List<City> listCityByProvinceId(Integer provinceId) {
        //noinspection unchecked
        return generalDAO.search(new Search(City.class).addFilterEqual("provinceId", provinceId));
    }

    /**
     * 查询市下面的所有区
     *
     * @param cityId
     * @return
     */
    public List<Area> listAreaByCityId(Integer cityId) {
        //noinspection unchecked
        return generalDAO.search(new Search(Area.class).addFilterEqual("cityId", cityId));

    }

    public Province getProvince(String id) {
        if (logger.isInfoEnabled()) {
            logger.info("ProvinceService类中的findProvinceAll查询全部");
        }
        return generalDAO.get(Province.class, id);
    }
}
