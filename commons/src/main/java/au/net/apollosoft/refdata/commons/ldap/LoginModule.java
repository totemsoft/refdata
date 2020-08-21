package au.net.apollosoft.refdata.commons.ldap;

import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;

/**
 * 
 * @author VChibaev
 */
public interface LoginModule {

    /**
     * @param userId
     * @param password
     * @return
     * @throws AuthenticationException
     */
    Map<String, String> authenticate(String userId, String password) throws AuthenticationException;

    /**
     * @param userId
     * @return DN, eg "CN=shibaevv,OU=Users,OU=Syd,DC=totemsoft,DC=com,DC=au"
     * @throws NamingException
     */
    String find(String userId) throws NamingException;

    /**
     * 
     * @param userId
     * @return employeeNumber
     * @throws NamingException
     */
    String employeeNumber(String userId) throws NamingException;

}