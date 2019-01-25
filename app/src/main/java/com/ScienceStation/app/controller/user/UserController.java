package com.ScienceStation.app.controller.user;

import com.ScienceStation.app.component.JwtTokenUtil;
import com.ScienceStation.app.controller.user.dto.request.SignUpRequestDTO;
import com.ScienceStation.app.model.User;
import com.ScienceStation.app.service.exception.EmailAlreadyExistsException;
import com.ScienceStation.app.service.exception.InvalidValueException;
import com.ScienceStation.app.service.exception.VerificationTokenInvalidException;
import com.ScienceStation.app.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public User getAuthenticatedUser(HttpServletRequest request){
        //final String requestHeader = request.getHeader(this.tokenHeader);
        String token = request.getHeader(tokenHeader).substring(7);
        String username = jwtTokenUtil.getUserNameFromToken(token);
        User user = (User)userDetailsService.loadUserByUsername(username);
        return user;
    }

    @RequestMapping(value="/create", method= RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody SignUpRequestDTO signUpRequestDTO) throws EmailAlreadyExistsException{
        try {
            User u = userService.createUser(signUpRequestDTO);
            return ResponseEntity.status(201).body(u);
        }catch (EmailAlreadyExistsException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @RequestMapping(value="/confirmAccount/{token}", method= RequestMethod.GET)
    public ResponseEntity<?> validateUser(@PathVariable("token") String token) throws VerificationTokenInvalidException{
        try {
            userService.validateToken(token);
            return ResponseEntity.ok().build();
        }catch (VerificationTokenInvalidException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value="/editors/{magazineId}/{branchId}")
    public ResponseEntity<?> getEditorForMagazineAndBranch(@PathVariable("magazineId") Long magazineId,@PathVariable("branchId") Long branchId){
        try {
            return ResponseEntity.ok().body(userService.getReviewers(magazineId, branchId));
        }catch (InvalidValueException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @RequestMapping("/availableReviewers/{journalId}")
    public ResponseEntity<?> getAllAvailableReviewers(@PathVariable("journalId") Long journalId){
        try{
            return ResponseEntity.ok().body(userService.getAvailableReviews(journalId));
        }catch (InvalidValueException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

}
