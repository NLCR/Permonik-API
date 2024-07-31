package cz.incad.nkp.inprove.permonikapi.config.security.auth.shibboleth;

import cz.incad.nkp.inprove.permonikapi.config.security.user.UserDelegate;
import cz.incad.nkp.inprove.permonikapi.config.security.user.UserDetailsServiceImpl;
import cz.incad.nkp.inprove.permonikapi.config.security.user.UserProducer;
import cz.incad.nkp.inprove.permonikapi.user.User;
import cz.incad.nkp.inprove.permonikapi.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
@Slf4j
@AllArgsConstructor
public class ShibbolethService {

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;

    //    private final List<String> allowedIdentityProviders = asList(
//            "https://shibboleth.mzk.cz/simplesaml/metadata.xml",
//            "https://shibboleth.nkp.cz/idp/shibboleth",
//            "https://svkul.cz/idp/shibboleth",
//            "https://shibo.vkol.cz/idp/shibboleth");

    public void shibbolethLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, SolrServerException {
        String idp = (String) request.getAttribute("Shib-Identity-Provider");
//        if (!allowedIdentityProviders.contains(idp)) {
//            throw new ForbiddenException("This IDP is not allowed");
//        }

        String eppn = (String) request.getAttribute("eduPersonPrincipalName");

        User user = userService.findUserByUserName(eppn);

        if (user == null) {
            user = createNewShibbolethUser(request, eppn);
        }

        loadUserIntoSecurityContext(user, request);

        response.sendRedirect(response.encodeRedirectURL("/?shibbolethAuth=true"));
    }

    private void loadUserIntoSecurityContext(User user, HttpServletRequest request) {
        Set<GrantedAuthority> authorities = userDetailsService.getGrantedAuthorities(user);
        UserDelegate shibUserDelegate = new UserDelegate(user, authorities, true);
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(
                shibUserDelegate, "", authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
        HttpSession session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
    }

    private User createNewShibbolethUser(HttpServletRequest request, String eppn) throws SolrServerException, IOException {
        String firstName = decodeAndRepairCaseForName((String) request.getAttribute("firstName"));
        String lastName = decodeAndRepairCaseForName((String) request.getAttribute("lastName"));
        String owner = eppn.split("@")[1].split("\\.")[0].toUpperCase();
        String email = (String) request.getAttribute("email");
        String eduPersonScopedAffiliation = (String) request.getAttribute("eduPersonScopedAffiliation");

        String a = (String) request.getAttribute("authorized_by_idp");

        // TODO: handle owner id from owners core
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .userName(eppn)
                .firstName(firstName)
                .lastName(lastName)
                .role("user")
                .active(true)
//                .owners(List.of(owner))
                .build();


        return userService.createUser(user);
    }

    private String decodeAndRepairCaseForName(String name) {
        name = new String(name.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        name = name.toLowerCase();
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        return name;
    }

    public void shibbolethLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDelegate userDelegate = UserProducer.getCurrentUserDelegate();
        String redirectUrl = userDelegate != null && userDelegate.getIsShibbolethAuth() ?
                "/Shibboleth.sso/Logout?return=/" : "/";

        HttpSession session = request.getSession(false);
        if (session != null && request.isRequestedSessionIdValid()) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        removeSessionCookie(request, response);

        response.sendRedirect(response.encodeRedirectURL(redirectUrl));
    }

    private static void removeSessionCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    cookie.setMaxAge(0);
                    cookie.setValue(null);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                }
            }
        }
    }
}
