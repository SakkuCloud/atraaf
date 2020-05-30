package io.github.makbn.atraaf.resource.user;

import io.github.makbn.atraaf.api.business.User;
import io.github.makbn.atraaf.api.common.AtraafResponse;
import io.github.makbn.atraaf.api.common.imp.AtraafResponseImp;
import io.github.makbn.atraaf.api.request.LoginReq;
import io.github.makbn.atraaf.api.request.SignUpReq;
import io.github.makbn.atraaf.core.crud.UserCRUD;
import io.github.makbn.atraaf.core.entity.UserEntity;
import io.github.makbn.atraaf.core.exception.AccessDeniedException;
import io.github.makbn.atraaf.core.exception.InternalServerException;
import io.github.makbn.atraaf.core.exception.ResourceNotFoundException;
import io.github.makbn.atraaf.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;


@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserResource {

    private final UserCRUD userCRUD;
    private final UserProvider userProvider;

    @Autowired
    public UserResource(UserCRUD userCRUD, UserProvider userProvider) {
        this.userCRUD = userCRUD;
        this.userProvider = userProvider;
    }


    @GetMapping()
    public AtraafResponse<User> getCurrentUser() throws ResourceNotFoundException, AccessDeniedException, InternalServerException, IOException {

        return AtraafResponseImp.<User>builder()
                .result(getUser(true))
                .build();
    }

    @PostMapping()
    public AtraafResponse<String> signUp(@RequestBody SignUpReq signUpReq) throws ResourceNotFoundException, AccessDeniedException, InternalServerException, IOException {

        return AtraafResponseImp.<String>builder()
                .result(userProvider.signUpUser(signUpReq))
                .build();
    }

    @PostMapping("/token")
    public AtraafResponse<String> getNewToken(@RequestBody LoginReq loginReq) throws ResourceNotFoundException, AccessDeniedException, InternalServerException, IOException {

        return AtraafResponseImp.<String>builder()
                .result(userProvider.getNewToken(loginReq, loginReq.getPodToken()))
                .build();
    }


    /**
     * retrieve user from security context
     *
     * @return current User entity
     * @throws AccessDeniedException if user not found in database
     */
    public UserEntity getUserEntity(boolean required) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<UserEntity> user = userCRUD.findUserById(Long.valueOf(userId));
        if (user.isPresent())
            return user.get();
        else if (required)
            throw AccessDeniedException.builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("user not found!")
                    .build();
        else
            return null;
    }


    private User getUser(boolean required) {
        return UserService.getUser(getUserEntity(required));
    }


}
