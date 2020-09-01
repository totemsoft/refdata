package au.net.apollosoft.refdata.commons.ldap;

import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {
    "classpath:au/net/apollosoft/refdata/commons/spring-inc-ldap.xml"
})

public class LdapLoginModuleTest {

    /** logger. */
    private final static Logger LOG = LoggerFactory.getLogger(LdapLoginModuleTest.class);

    @Autowired
    private LoginModule loginModule;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("LDAP_URL", "ldap://host:389");
        System.setProperty("LDAP_BASENAME", "OU=Users,DC=totemsoft,DC=com");
        System.setProperty("LDAP_USERNAME", "admin");
        System.setProperty("LDAP_PASSWORD", "password");
    }

    @Test(expected = javax.naming.AuthenticationException.class)
    public void authenticate() throws AuthenticationException {
        Map<String, String> result = loginModule.authenticate("shibaevv", "password");
        Assert.assertTrue(result == null);
    }

    @Test
    public void find() throws NamingException {
        String username = loginModule.find("shibaevv");
        Assert.assertNotNull(username);
        //
        String usernameNotFound = loginModule.find("shibaevv2");
        Assert.assertNull(usernameNotFound);
    }

    @Test
    public void employeeNumber() throws NamingException {
        String empNo = loginModule.employeeNumber("shibaevv");
        LOG.info(empNo);
        //
        for (int i = 0; i < 10; i++) {
            empNo = loginModule.employeeNumber("shibaevv");
            LOG.info(empNo);
        }
        LOG.info(empNo);
        //
        empNo = loginModule.employeeNumber("shibaevv");
        LOG.info(empNo);
    }

}
