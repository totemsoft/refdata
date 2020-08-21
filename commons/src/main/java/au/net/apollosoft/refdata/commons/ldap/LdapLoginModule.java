package au.net.apollosoft.refdata.commons.ldap;

import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Component;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@Component
public class LdapLoginModule implements LoginModule {

    @Value("OU=Users,OU=Syd,DC=totemsoft,DC=com,DC=au")
    private String baseDn;

    @Value("DC=totemsoft,DC=com,DC=au")
    private String searchBase;

    private final LdapTemplate ldapTemplate;

    public LdapLoginModule(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    @Override
    public Map<String, String> authenticate(String userId, String password) throws AuthenticationException {
        // Do-not allow blank password
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Password is Blank");
        }
        if (userId == null || userId.isEmpty()) {
            throw new AuthenticationException("User is Blank");
        }
        // DN CN=shibaevv,OU=Users,OU=Syd,DC=totemsoft,DC=com,DC=au
        if (userId.contains(",")) {
            for (String part : userId.split(",")) {
                String[] entry = part.split("=");
                if (entry[0].equalsIgnoreCase("CN")) {
                    userId = entry[1];
                    break;
                }
            }
        }
        //
        if (ldapTemplate.authenticate(searchBase, filter(userId).encode(), password)) {
            return null;
        }
        String error = "User [" + userId + "] authentication failed.";
        throw new AuthenticationException(error);
    }

    @Override
    public String find(String userId) throws NamingException {
        return searchUserAttribute(userId, "distinguishedName");
    }

    @Override
    public String employeeNumber(String userId) throws NamingException {
        return searchUserAttribute(userId, "employeeID");
    }

    private AndFilter filter(String userId) {
        return new AndFilter()
            .and(new EqualsFilter("objectClass", "user"))
            .and(new EqualsFilter("sAMAccountName", userId));
    }

    private String searchUserAttribute(String userId, final String attrId) throws NamingException {
        List<String> results = ldapTemplate.search(searchBase,
            filter(userId).encode(),
            SearchControls.SUBTREE_SCOPE,
            new AttributesMapper<String>() {
                public String mapFromAttributes(Attributes attrs) throws NamingException {
                    return attrs.get(attrId).get().toString();
                }
            }
        );
        return results.isEmpty() ? null : results.get(0);
    }

}