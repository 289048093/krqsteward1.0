package logisticscenter;

import com.ejushang.steward.logisticscenter.domain.LogisticsInfo;
import com.ejushang.steward.logisticscenter.service.LogisticsInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

/**
 * User: Blomer
 * Date: 14-4-9
 * Time: 上午9:47
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-context.xml")
@Transactional
public class LogisticsInfoServiceTest {

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Test
    public void TestSave() {
        LogisticsInfo logisticsInfo = new LogisticsInfo();
        logisticsInfo.setOperatorId(11314308);
        logisticsInfo.setExpressCompany("易居尚");
        logisticsInfo.setExpressNo("23143412");
        logisticsInfo.setOrderNo("13432143214");
        logisticsInfoService.save(logisticsInfo);
    }

}
