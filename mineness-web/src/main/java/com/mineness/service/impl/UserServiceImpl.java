/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.service.impl;

//~--- non-JDK imports --------------------------------------------------------

import com.mineness.ApplicationConstants;
import com.mineness.domain.document.Role;
import com.mineness.domain.document.Social;
import com.mineness.domain.document.User;
import com.mineness.domain.document.UserSupplement;
import com.mineness.domain.dto.Principal;
import com.mineness.domain.dto.UserDto;
import com.mineness.domain.dto.UserSearchQuery;
import com.mineness.domain.enums.RegistrationType;
import com.mineness.repository.UserRepository;
import com.mineness.service.CacheService;
import com.mineness.service.RoleService;
import com.mineness.service.UserService;
import com.mineness.service.UserSupplementService;
import com.mineness.spring.security.SpringSecurityHelper;
import com.mineness.utils.number.NumberUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.jasypt.util.password.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

//~--- JDK imports ------------------------------------------------------------

//~--- classes ----------------------------------------------------------------

/**
 * Created by Bjorn Harvold
 * Date: 6/19/11
 * Time: 3:59 PM
 * Responsibility:
 */
@Service("userService")
public class UserServiceImpl extends AbstractCacheableService implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final int DEFAULT_CHUNK_SIZE = 100;

    //~--- fields -------------------------------------------------------------

    /**
     * Field description
     */
    @Value("${application.context}")
    private String context;

    /**
     * Field description
     */
    @Value("${server.port}")
    private Integer port;

    /**
     * Field description
     */
    @Value("${server.protocol}")
    private String protocol;

    /**
     * Field description
     */
    @Value("${server.name}")
    private String server;

    @Value("${amazon.access.key}")
    private String accessKey;

    @Value("${amazon.secret.key}")
    private String secretKey;

    private final PasswordEncryptor passwordEncryptor;

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final UserSupplementService userSupplementService;

    //~--- constructors -------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param cacheService          cacheService
     * @param userRepository        userRepository
     * @param passwordEncryptor     passwordEncryptor
     * @param roleService           roleService
     * @param userSupplementService userSupplementService
     */
    @Autowired
    public UserServiceImpl(CacheService cacheService,
                           UserRepository userRepository,
                           PasswordEncryptor passwordEncryptor,
                           RoleService roleService,
                           UserSupplementService userSupplementService) {
        super(cacheService);

        this.userRepository = userRepository;
        this.passwordEncryptor = passwordEncryptor;
        this.roleService = roleService;
        this.userSupplementService = userSupplementService;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param id id
     * @return Return value
     */
    @Override
    public User findUser(ObjectId id) {
        return findUser(id, true);
    }

    /**
     * Method description
     *
     * @param id     id
     * @param enrich enrich
     * @return Return value
     */
    private User findUser(ObjectId id, boolean enrich) {
        User result = userRepository.findOne(id);

        if ((result != null) && enrich) {
            enrichUser(result);
        }

        return result;
    }

    /**
     * Method description
     *
     * @param userCode checksum
     * @return Return value
     */
    @Override
    public User findUserByCode(String userCode) {
        User result = null;

        Cache.ValueWrapper wrapper = retrieveFromCache(ApplicationConstants.USER_CACHE, userCode);

        if (wrapper != null && wrapper.get() != null && wrapper.get() instanceof User) {
            result = (User) wrapper.get();
        } else {
            result = userRepository.findByCode(userCode);

            if (result != null) {
                putInCache(ApplicationConstants.USER_CACHE, userCode, result);
            }
        }

        return result;
    }

    @Override
    public void removeUserSupplement(String userCode) {
        userSupplementService.removeUserSupplement(userCode);
    }

    /**
     * Method description
     *
     * @param email email
     * @return Return value
     */
    @Override
    public User findUserByEmail(String email) {
        User result = null;

        if (StringUtils.isNotBlank(email)) {
            result = userRepository.findByEmail(email.trim().toLowerCase());
            if (result != null) {
                enrichUser(result);
            }
        }

        return result;
    }

    /**
     * Method description
     *
     * @param email email
     * @return Return value
     */
    @Override
    public UserSupplement findUserSupplementByEmail(String email) {
        return userSupplementService.findUserSupplementByEmail(email);
    }

    @Override
    public List<User> findUserIdsBySocialNetwork(String providerId, String providerUserId) {
        List<UserSupplement> list = userSupplementService.findUserIdsBySocialNetwork(providerId, providerUserId);

        return findUsersFromSupplements(list, null);
    }

    /**
     * @param list list
     * @return users
     */
    private List<User> findUsersFromSupplements(List<UserSupplement> list, List<String> fields) {
        List<String> userCodes = getUserCodesFromSupplement(list);

        return userRepository.findByCodes(userCodes, fields);
    }

    private List<UserSupplement> findUserSupplementsFromUsers(List<User> list, List<String> fieldsOnUserSupplementList) {
        List<String> userCodes = getUserCodesFromUser(list);

        return userSupplementService.findUserSupplementsByCodes(userCodes, fieldsOnUserSupplementList);
    }

    /**
     * @param list list
     * @return users
     */
    private List<User> findUsersFromSupplements(List<UserSupplement> list) {
        return findUsersFromSupplements(list, null);
    }

    /**
     * @param us us
     * @return users
     */
    private User findUserFromSupplement(UserSupplement us) {
        return userRepository.findByCode(us.getCd());
    }

    /**
     * Method description
     *
     * @param providerId      providerId
     * @param providerUserIds providerUserIds
     * @return Return value
     */
    @Override
    public List<User> findUserIdsConnectedToSocialNetwork(String providerId, Set<String> providerUserIds) {
        List<UserSupplement> list = userSupplementService.findUserIdsConnectedToSocialNetwork(providerId, providerUserIds);

        return findUsersFromSupplements(list);
    }

    /**
     * Find ALL users for social network
     *
     * @param providerId Social network provider id
     * @return list of users
     */
    @Override
    public List<User> findUsersBySocialNetwork(String providerId) {
        List<UserSupplement> list = userSupplementService.findUsersBySocialNetwork(providerId);

        return findUsersFromSupplements(list);
    }

    @PreAuthorize("hasAnyRole('RIGHT_READ_USER_AS_ADMIN')")
    @Override
    public Long findFacebookUserCount(UserSearchQuery query) {
        List<UserSupplement> users = userSupplementService.findUserSupplementsByQuery(query);
        List<String> userCodes = getUserCodesFromUserSupplements(users);
        return userSupplementService.findFacebookUserCount(userCodes);
    }

    @PreAuthorize("hasAnyRole('RIGHT_READ_USER_AS_ADMIN')")
    @Override
    public Long findFacebookUserCount() {
        return userSupplementService.findFacebookUserCount(null);
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Method description
     *
     * @param encryptedPassword encryptedPassword
     * @param rawPassword       rawPassword
     * @return Return value
     */
    @Override
    public Boolean isValid(String encryptedPassword, String rawPassword) {
        return passwordEncryptor.checkPassword(rawPassword, encryptedPassword);
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     * @param email email
     * @return Return value
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User result = findUserByEmail(email.trim());

        if (result == null) {
            throw new UsernameNotFoundException(email);
        }

        return new Principal(result);
    }

    /**
     * This will remove the User and UserSupplement entries.
     *
     */
    //@PreAuthorize("hasAnyRole('RIGHT_DELETE_USER_AS_ADMIN', 'RIGHT_CONTENT_INGEST')")
    @Override
    public void removeUser(User user) {
        if (user.getId() != null) {
            userRepository.delete(user);

            removeFromCache(ApplicationConstants.USER_CACHE, user.getCd());
        }

        removeUserSupplement(user.getCd());
    }

    /**
     * WARNING!!! This will remove the User and UserSupplement entries.
     *
     */
    @PreAuthorize("hasAnyRole('RIGHT_DELETE_USER_AS_ADMIN', 'RIGHT_CONTENT_INGEST')")
    @Override
    public void removeUser(String userCode) {
        User user = findUserByCode(userCode);

        if (user != null) {
            userRepository.delete(user);

            removeFromCache(ApplicationConstants.USER_CACHE, user.getCd());
        }

        removeUserSupplement(userCode);
    }

    /**
     * Method description
     *
     * @param user user
     * @return Return value
     */
    @Override
    public User saveUser(User user) {

        // add default role for user
        addApplicationUserRoleIfEmpty(user);

        // if this doesn't have a password, we will disable her for now
        if (StringUtils.isBlank(user.getPsswrd())) {
            user.setNbld(false);
        }

        // persist
        user = userRepository.save(user);

        removeFromCache(ApplicationConstants.USER_CACHE, user.getCd());

        return user;
    }

    /**
     * Method description
     *
     * @param password password
     * @return Return value
     */
    @Override
    public String encryptPassword(String password) {
        return passwordEncryptor.encryptPassword(password);
    }

    /**
     * Method description
     *
     * @param list list
     * @return Return value
     */
    @Override
    public List<User> saveUsers(List<User> list) {
        if ((list != null) && !list.isEmpty()) {
            for (User user : list) {

                // we need to check that we aren't inserting duplicate users
                User tmp = findUserByEmail(user.getMl());

                if (tmp == null) {
                    saveUser(user);
                } else {
                    log.warn("Trying to insert duplicate user with email: " + user.getMl());
                }
            }
        }

        return list;
    }

    /**
     * Method description
     *
     * @param user user
     */
    private void addApplicationUserRoleIfEmpty(User user) {
        if (user.getRrlnms().isEmpty()) {
            user.getRrlnms().add(ApplicationConstants.APPLICATION_USER_ROLE);
        }
    }

    /**
     * Method description
     *
     * @param user user
     */
    private void enrichUser(User user) {
        if (log.isDebugEnabled()) {
            log.debug("Enriching user...");
        }

        if (user != null) {
            if (user.getRrlnms() != null) {
                for (String urlName : user.getRrlnms()) {
                    Role role = roleService.findRoleByUrlName(urlName);

                    if (role != null) {
                        // add it to object
                        user.getRoles().add(role);
                    } else {
                        log.warn("There is no reason why this role is not available. Url name: " + urlName);
                    }
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Enriching user complete");
        }
    }

    @Override
    public void autoLogin(ObjectId userId, String remoteAddress) {
        User user = findUser(userId, true);

        user = saveUser(user);

        // set user in secure context
        Principal principal = new Principal(user);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
    }

    /**
     * Convenience method for anyone wishing to keep both the
     * user and her's security context in sync
     *
     * @param user user
     * @return user
     */
    @Override
    public User updateUserAndRefreshSecurityContext(User user) {

        if (user.getId() != null) {
            // only update principal if there already is a principal
            Principal principal = SpringSecurityHelper.getSecurityContextPrincipal();

            if (principal != null) {
                // update security context
                SpringSecurityHelper.secureChannel(new Principal(user));
            }

            // save to db
            saveUser(user);
        }

        return user;
    }

    @Override
    public List<Social> saveSocials(String userCode, List<Social> socials) {
        return userSupplementService.saveSocials(userCode, socials);
    }

    @Override
    public List<Social> findSocials(String userCode) {
        return userSupplementService.findSocials(userCode);
    }

    @Override
    public UserSupplement findUserSupplement(String userCode) {
        return userSupplementService.findUserSupplement(userCode);
    }

    @Override
    public UserSupplement findUserSupplement(String userCode, List<String> fields) {
        return userSupplementService.findUserSupplement(userCode, fields);
    }

    @Override
    public UserSupplement saveUserSupplement(UserSupplement us) {
        return userSupplementService.saveUserSupplement(us);
    }

    private List<String> getUserCodesFromSupplement(List<UserSupplement> list) {
        List<String> result = null;

        if (list != null && !list.isEmpty()) {
            result = new ArrayList<String>(list.size());

            for (UserSupplement us : list) {
                result.add(us.getCd());
            }
        }

        return result;
    }

    private List<String> getUserCodesFromUser(List<User> list) {
        List<String> result = null;

        if (list != null && !list.isEmpty()) {
            result = new ArrayList<String>(list.size());

            for (User us : list) {
                result.add(us.getCd());
            }
        }

        return result;
    }

    private List<String> getUserCodesFromUserSupplements(List<UserSupplement> list) {
        List<String> result = null;

        if (list != null && !list.isEmpty()) {
            result = new ArrayList<String>(list.size());

            for (UserSupplement us : list) {
                result.add(us.getCd());
            }
        }

        return result;
    }

    @Override
    public Long findUserCount() {
        return userRepository.count();
    }

    @Override
    public Long findUserCount(UserSearchQuery query) {
        return userSupplementService.findUserSupplementCountByQuery(query);
    }

    @Override
    public List<User> findUsersCreatedBetween(Date startDate, Date endDate) {
        List<User> result = null;
        Long count;
        Integer iterations;
        count = findUserCount();

        if (count != null && count > 0) {
            result = new ArrayList<User>(count.intValue());
            iterations = NumberUtils.calculateIterations(count, (long) DEFAULT_CHUNK_SIZE);

            // load up items in a paginated fashion from mongo
            for (int i = 0; i < iterations; i++) {
                result.addAll(userRepository.findUsersCreatedBetween(startDate, endDate, i, DEFAULT_CHUNK_SIZE, null));
            }
        }

        return result;
    }

    @Override
    public User registerFacebookUser(UserProfile dto) {
        log.info(String.format("Registering Facebook User with email %s ", dto.getEmail()));

        User user = new User(dto);

        // this gets encrypted in the user service
        user.setPsswrd(RandomStringUtils.randomAlphabetic(8));

        UserSupplement data = new UserSupplement();
        data.setMl(dto.getEmail());
        data.setFnm(dto.getFirstName());
        data.setLnm(dto.getLastName());
        data.setSrnm(dto.getUsername());

        return registerUser(user, data, RegistrationType.FACEBOOK, true, false);
    }

    @Override
    public User registerWebUser(UserDto dto) {
        log.info(String.format("Registering Web User with email %s ", dto.getMl()));

        User user = new User(dto);

        UserSupplement data = new UserSupplement();
        data.setMl(dto.getMl());
        data.setSrnm(dto.getSrnm());
        data.setFnm(dto.getFnm());
        data.setLnm(dto.getLnm());
        data.setLcl(dto.getLcl());

        return registerUser(user, data, RegistrationType.WEBSITE, true, false);
    }

    @Override
    public List<User> registerBootstrappedUsers(List<User> users) {
        List<User> result = null;

        if (users != null && !users.isEmpty()) {
            result = new ArrayList<>(users.size());

            for (User user : users) {
                log.info(String.format("Registering Bootstrapped User with email %s ", user.getMl()));

                UserSupplement data = new UserSupplement();
                data.setMl(user.getMl());
                data.setSrnm(user.getSrnm());
                data.setLcl(Locale.US);

                result.add(registerUser(user, data, RegistrationType.BOOTSTRAP, false, true));
            }
        }

        return users;
    }

    /**
     * This is the single point at which all Registration Paths MUST come together.
     *
     * @param user      user
     * @param type      type
     * @param sendEmail sendConfirmationEmail
     * @return Return value
     */
    private User registerUser(User user, UserSupplement us, RegistrationType type, boolean sendEmail, boolean updateIfExists) {
        String rawPassword = user.getPsswrd();

        if (StringUtils.isBlank(user.getMl())) {
            throw new IllegalStateException("User is missing an email");
        }

        if (StringUtils.isBlank(rawPassword)) {
            throw new IllegalStateException("User: " + user.getMl() + " is missing a password");
        }

        // Lowercase email
        user.setMl(user.getMl().toLowerCase());
        String email = user.getMl();

        // If there is already a user with this email, throw an exception
        User existingUser = findUserByEmail(email);
        if (existingUser != null && !updateIfExists) {
            throw new IllegalStateException("User already exists for email: " + email);
        }

        // encrypt password
        user.setPsswrd(passwordEncryptor.encryptPassword(rawPassword));

        // persist user and user supplement
        user = saveUser(user);

        // make sure we link the two together
        us.setCd(user.getCd());

        saveUserSupplement(us);

        // email welcome email here
        if (sendEmail) {

        }

        return user;
    }
}
