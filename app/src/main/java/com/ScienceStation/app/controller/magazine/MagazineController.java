package com.ScienceStation.app.controller.magazine;

import com.ScienceStation.app.controller.magazine.dto.request.CreateMagazineRequestDTO;
import com.ScienceStation.app.model.Magazine;
import com.ScienceStation.app.model.User;
import com.ScienceStation.app.service.exception.InvalidUserException;
import com.ScienceStation.app.service.magazine.MagazineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/magazines")
public class MagazineController {

    private MagazineService magazineService;

    @Autowired
    public MagazineController(MagazineService magazineService) {
        this.magazineService = magazineService;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<Magazine> all(){
        return magazineService.getAll();
    }

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMagazine(@RequestBody CreateMagazineRequestDTO createMagazineRequestDTO) throws InvalidUserException{
        try {
            Magazine m = magazineService.createMagazine(createMagazineRequestDTO);
            return ResponseEntity.status(201).body(m);
        }catch (InvalidUserException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value="/isMember/{magazineId}",method = RequestMethod.GET)
    public boolean isUserMember(@PathVariable(value = "magazineId") Long magazineId){
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return magazineService.isUserMember(u,magazineId);
    }

    @RequestMapping(value="/registerUser/{magazineId}",method = RequestMethod.GET)
    public ResponseEntity<?> registerUserToMagazine(@PathVariable Long magazineId){
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Magazine magazineOptional = magazineService.payFee(u,magazineId);
        if(magazineOptional==null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }
}
