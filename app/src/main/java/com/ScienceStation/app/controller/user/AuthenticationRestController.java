package com.ScienceStation.app.controller.user;

import com.ScienceStation.app.component.JwtTokenUtil;
import com.ScienceStation.app.controller.user.dto.request.SignInRequestDTO;
import com.ScienceStation.app.controller.user.dto.response.SignInResponseDTO;
import com.ScienceStation.app.exeption.AuthenticationException;
import com.ScienceStation.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

//Rest controller only means that there is no need to put @ResponseBody on the endpoints because it's always JSON
@RestController
public class AuthenticationRestController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping(value = "{jwt.route.authentication.path}",method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody SignInRequestDTO signInRequestDTO) throws AuthenticationException{
        final UserDetails userDetails = userDetailsService.loadUserByUsername(signInRequestDTO.getUsername());
        boolean wtf = passwordEncoder.matches(signInRequestDTO.getPassword(),userDetails.getPassword());

        authenticate(signInRequestDTO.getUsername(),signInRequestDTO.getPassword());



        final String token = jwtTokenUtil.generateToken(userDetails);



        return ResponseEntity.ok(new SignInResponseDTO(token));
    }

    @RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request){
        String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);
        String username = jwtTokenUtil.getUserNameFromToken(token);
        User user = (User)userDetailsService.loadUserByUsername(username);

        if(jwtTokenUtil.canTokenBeRefreshed(token)){
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new SignInResponseDTO(token));
        }
        else
            return ResponseEntity.badRequest().body(null);
    }

    private void authenticate(String username,String password){
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        } catch (DisabledException e) {
            throw new AuthenticationException("User is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Bad credentials!", e);
        }
    }
}
