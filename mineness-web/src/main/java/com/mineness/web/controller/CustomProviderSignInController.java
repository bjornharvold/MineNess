/*
 * Copyright (c) 2012. Purple Door Systems, BV.
 */

package com.mineness.web.controller;

import com.mineness.domain.document.User;
import com.mineness.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.support.URIBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Bjorn Harvold
 * Date: 6/5/12
 * Time: 7:21 PM
 * Responsibility:
 */
@Controller
@RequestMapping("/signin")
public class CustomProviderSignInController extends AbstractController {
    private final static Logger logger = LoggerFactory.getLogger(CustomProviderSignInController.class);
    private static final String LOGIN_REDIRECT = "/login/redirect";
    private static final String HOMEPAGE_REDIRECT = "/";

    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final UsersConnectionRepository usersConnectionRepository;
    private final SignInAdapter signInAdapter;
    private final UserService userService;

    private final String signUpUrl;
    private final String signInUrl = "/signin";
    private final ConnectSupport webSupport = new ConnectSupport();

    @Autowired
    public CustomProviderSignInController(ConnectionFactoryLocator connectionFactoryLocator,
                                          UsersConnectionRepository usersConnectionRepository,
                                          SignInAdapter signInAdapter,
                                          UserService userService,
                                          @Value("${application.url}") String applicationUrl,
                                          @Value("${signup.url}") String signUpUrl) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.usersConnectionRepository = usersConnectionRepository;
        this.signInAdapter = signInAdapter;
        this.userService = userService;
        this.webSupport.setUseAuthenticateUrl(true);
        this.webSupport.setApplicationUrl(applicationUrl);
        this.signUpUrl = signUpUrl;
    }

    /**
     * Process a sign-in form submission by commencing the process of establishing a connection to the provider on behalf of the user.
     * For OAuth1, fetches a new request token from the provider, temporarily stores it in the session, then redirects the user to the provider's site for authentication authorization.
     * For OAuth2, redirects the user to the provider's site for authentication authorization.
     */
    @RequestMapping(value="/{providerId}", method= RequestMethod.POST)
    public RedirectView signIn(@PathVariable String providerId, NativeWebRequest request) {
        ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
        try {
            return new RedirectView(webSupport.buildOAuthUrl(connectionFactory, request));
        } catch (Exception e) {
            return redirect(URIBuilder.fromUri(signInUrl).queryParam("error", "provider").build().toString());
        }
    }

    /**
     * Process the authentication callback from an OAuth 1 service provider.
     * Called after the member authorizes the authentication, generally done once by having he or she click "Allow" in their web browser at the provider's site.
     * Handles the provider sign-in callback by first determining if a local user account is associated with the connected provider account.
     * If so, signs the local user in by delegating to {@link org.springframework.social.connect.web.SignInAdapter#signIn(String, org.springframework.social.connect.Connection, org.springframework.web.context.request.NativeWebRequest)}
     * If not, redirects the user to a signup page to create a new account with {@link org.springframework.social.connect.web.ProviderSignInAttempt} context exposed in the HttpSession.
     * @see org.springframework.social.connect.web.ProviderSignInAttempt
     * @see org.springframework.social.connect.web.ProviderSignInUtils
     */
    @RequestMapping(value="/{providerId}", method= RequestMethod.GET, params="oauth_token")
    public RedirectView oauth1Callback(@PathVariable String providerId, NativeWebRequest request) {
        try {
            OAuth1ConnectionFactory<?> connectionFactory = (OAuth1ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
            Connection<?> connection = webSupport.completeConnection(connectionFactory, request);
            return handleSignIn(connection, request);
        } catch (Exception e) {
            return redirect(URIBuilder.fromUri(signInUrl).queryParam("error", "provider").build().toString());
        }
    }

    /**
     * Process the authentication callback from an OAuth 2 service provider.
     * Called after the user authorizes the authentication, generally done once by having he or she click "Allow" in their web browser at the provider's site.
     * Handles the provider sign-in callback by first determining if a local user account is associated with the connected provider account.
     * If so, signs the local user in by delegating to {@link org.springframework.social.connect.web.SignInAdapter#signIn(String, org.springframework.social.connect.Connection, org.springframework.web.context.request.NativeWebRequest)}.
     * If not, redirects the user to a signup page to create a new account with {@link org.springframework.social.connect.web.ProviderSignInAttempt} context exposed in the HttpSession.
     * @see org.springframework.social.connect.web.ProviderSignInAttempt
     * @see org.springframework.social.connect.web.ProviderSignInUtils
     */
    @RequestMapping(value="/{providerId}", method= RequestMethod.GET, params="code")
    public RedirectView oauth2Callback(@PathVariable String providerId, @RequestParam("code") String code, NativeWebRequest request) {
        try {
            OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
            Connection<?> connection = webSupport.completeConnection(connectionFactory, request);
            return handleSignIn(connection, request);
        } catch (Exception e) {
            logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + "). Redirecting to " + signInUrl, e);
            return redirect(URIBuilder.fromUri(signInUrl).queryParam("error", "provider").build().toString());
        }
    }

    /**
     * Process the authentication callback when neither the oauth_token or code parameter is given, likely indicating that the user denied authorization with the provider.
     * Redirects to application's sign in URL, as set in the signInUrl property.
     */
    @RequestMapping(value="/{providerId}", method= RequestMethod.GET)
    public RedirectView canceledAuthorizationCallback() {
        return redirect(signInUrl);
    }

    private RedirectView handleSignIn(Connection<?> connection, NativeWebRequest nativeRequest) {

        // Determine if the user exists
        RedirectView redirect = redirect(LOGIN_REDIRECT);
        List<User> existingUsers = userService.findUserIdsBySocialNetwork(connection.getKey().getProviderId(), connection.getKey().getProviderUserId());

        // this method doesn't just query for a user but also creates one if it isn't there
        List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
        if (userIds.size() == 0) {
            ProviderSignInAttempt signInAttempt = new ProviderSignInAttempt(connection, connectionFactoryLocator, usersConnectionRepository);

            // SESSION_ATTRIBUTE variable wasn't accessible so used ProviderSignInAttempt.class.getName() instead
            nativeRequest.setAttribute(ProviderSignInAttempt.class.getName(), signInAttempt, RequestAttributes.SCOPE_SESSION);
            redirect = redirect(signUpUrl);
        } else if (userIds.size() == 1) {
            usersConnectionRepository.createConnectionRepository(userIds.get(0)).updateConnection(connection);
            String springSecurityPriorUrl = signInAdapter.signIn(userIds.get(0), connection, nativeRequest);

            HttpServletRequest request = nativeRequest.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse response = nativeRequest.getNativeResponse(HttpServletResponse.class);
            HttpSession session = request.getSession();

            // custom lela code
            if (existingUsers == null || existingUsers.isEmpty()) {
                redirect = redirect(HOMEPAGE_REDIRECT);
            }

            if (StringUtils.hasText(springSecurityPriorUrl)) {
                redirect = redirect(springSecurityPriorUrl);
            }
        } else {
            redirect = redirect(URIBuilder.fromUri(signInUrl).queryParam("error", "multiple_users").build().toString());
        }

        return redirect;
    }

    private RedirectView redirect(String url) {
        return new RedirectView(url, true);
    }


}
