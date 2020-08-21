package au.net.apollosoft.refdata.commons.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
public abstract class BeanUtils {

    /**
     * <p>Return the entire set of properties for which the specified bean provides a read/write method.</p>
     *
     * @param bean Bean whose properties are to be extracted
     * @return Map of property descriptors
     *
     * @exception ServiceException if the caller does not have access to the property accessor method
     * @exception ServiceException if the property accessor method throws an exception
     * @exception ServiceException if an accessor method for this property cannot be found
     * @see org.apache.commons.beanutils.BeanUtils#describe 
     */
    public static Map<String, Object> describe(Object bean) throws Exception {
        final String IGNORE = "password";
        Map<String, Object> result = new TreeMap<>();
        if (bean == null) {
            return result;
        }
        result.put("className", bean.getClass().getName());
        final BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
        PropertyUtilsBean propertyUtilsBean = beanUtilsBean.getPropertyUtils();
        PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(bean);
        Class<? extends Object> clazz = bean.getClass();
        for (int i = 0; i < descriptors.length; i++) {
            PropertyDescriptor descriptor = descriptors[i];
            String name = descriptor.getName();
            if (name != null && name.contains(IGNORE)) {
                continue;
            }
            Method readMethod = MethodUtils.getAccessibleMethod(clazz, descriptor.getReadMethod());
            Method writeMethod = MethodUtils.getAccessibleMethod(clazz, descriptor.getWriteMethod());
            if (readMethod != null && writeMethod != null) {
                String value = beanUtilsBean.getProperty(bean, name);
                if (value != null && value.contains(IGNORE)) {
                    continue;
                }
                result.put(name, value);
            }
        }
        return result;
    }

}