package com.ejushang.steward.ordercenter.util;

import com.ejushang.steward.common.util.Money;
import com.ejushang.steward.ordercenter.domain.Order;
import com.ejushang.steward.ordercenter.vo.OrderVo;

import java.lang.reflect.Method;

/**
 * User:  Sed.Lee(李朝)
 * Date: 14-5-14
 * Time: 下午3:18
 */
public class BeanUtils {

    /**
     * 对象属性拷贝
     * <p/>
     * 可以在Money与String类型之间进行属性拷贝，对象必须符合JavaBean规范
     *
     * @param src  原始对象
     * @param dest 目标对象
     */
    public static void copyProperties(Object src, Object dest) {
        Method[] destMethods = dest.getClass().getMethods();
        Class<?> srcClazz = src.getClass();
        for (Method m : destMethods) {
            String methodName = m.getName();
            if (methodName.contains("set")) {
                String methodDesc = methodName.substring(3);
                Method readMethod = null;
                try {
                    try {
                        readMethod = srcClazz.getMethod("get" + methodDesc);
                    } catch (NoSuchMethodException e) {
                        readMethod = srcClazz.getMethod("is" + methodDesc);
                    }
                    Object returnValue = readMethod.invoke(src);
                    Class<?> parameterType = m.getParameterTypes()[0];
                    if (returnValue instanceof Money && String.class.equals(parameterType)) {
                        returnValue = returnValue.toString();
                    }
                    if (Money.class.equals(parameterType) && returnValue instanceof String) {
                        returnValue = Money.valueOf(returnValue.toString());
                    }
                    m.invoke(dest, returnValue);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    public static interface MoneryConvertor {
        String convert(Money money);

        Money convert(String moneyStr);

    }

    public static interface EnumConvertor {
        String convert(Enum e);

        Enum convert(String str);
    }

    public static void main(String[] args) {
        Order order = new Order();
        order.setBuyerMessage("hello");
        order.setActualFee(Money.valueOf(188));
        order.setGoodsFee(Money.valueOf(99));
        OrderVo vo = new OrderVo();
        copyProperties(order, vo);
        System.out.println(vo.getActualFee());
        System.out.println(vo.getBuyerMessage());
        Order o2 = new Order();
        copyProperties(vo, o2);
        System.out.println(o2.getActualFee());
        System.out.println(o2.getBuyerMessage());
    }
}
