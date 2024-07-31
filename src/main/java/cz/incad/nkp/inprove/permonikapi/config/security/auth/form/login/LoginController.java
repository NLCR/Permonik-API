package cz.incad.nkp.inprove.permonikapi.config.security.auth.form.login;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name = "Auth API", description = "API for login")
public class LoginController {

    private LoginService loginService;


    @Operation(summary = "Login with username and password")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping("/login/basic")
    public void basicLogin(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request) {
        loginService.basicLogin(loginDto, request);
    }

    @Operation(summary = "Login with username and password")
    @ApiResponse(responseCode = "200", description = "OK")
//    @SecurityRequirement(name = BASIC_SECURITY_SCHEME)
    @GetMapping("/login/basic")
    public void login() {
        /**
         * This method is not implemented, because it is handled by {@link cz.incad.nkp.inprove.permonikapi.config.security.auth.form.PermHttpBasicFormSecurityConfiguration}
         */
    }

}
