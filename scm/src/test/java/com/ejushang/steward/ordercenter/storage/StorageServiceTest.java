package com.ejushang.steward.ordercenter.storage;

import com.ejushang.steward.common.genericdao.dao.hibernate.GeneralDAO;
import com.ejushang.steward.common.service.EmployeeService;
import com.ejushang.steward.ordercenter.BaseTest;
import com.ejushang.uams.api.dto.EmployeeDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.List;

/**
 * User:  Sed.Lee(李朝)
 * Date: 14-4-28
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class StorageServiceTest extends BaseTest {

    @Autowired
    private GeneralDAO  generalDAO;

    @Autowired
    private EmployeeService employeeService;

    @Test
    @Transactional
    @Rollback(true)
    public void myTest(){
//        Product product = generalDAO.get(Product.class,1);
//        System.out.println(product.getName());
//        product.setName("testname");
//        generalDAO.saveOrUpdate(product);
//        product.setName("testname2");
//        generalDAO.saveOrUpdate(product);

        List<EmployeeDto> es = employeeService.findEmployeeByName("", "");
        System.out.println(es);
        System.out.println("id:36====="+employeeService.get(36));
//        for(Iterator<EmployeeDto> it=es.iterator();it.hasNext();){
//            it.next().getName();
//        }
    }
}
