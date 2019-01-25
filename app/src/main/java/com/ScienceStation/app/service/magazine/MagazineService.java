package com.ScienceStation.app.service.magazine;

import com.ScienceStation.app.controller.magazine.dto.request.CreateMagazineRequestDTO;
import com.ScienceStation.app.model.Magazine;
import com.ScienceStation.app.model.User;
import com.ScienceStation.app.repository.MagazineRepository;
import com.ScienceStation.app.repository.UserRepository;
import com.ScienceStation.app.service.exception.InvalidUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MagazineService {

    private MagazineRepository magazineRepository;

    private UserRepository userRepository;

    @Autowired
    public MagazineService(MagazineRepository magazineRepository,UserRepository userRepository) {
        this.magazineRepository = magazineRepository;
        this.userRepository = userRepository;
    }

    public Magazine payFee(User u,Long magazineId){
        Optional<Magazine> m = magazineRepository.findById(magazineId);
        //Optional<User> u = userRepository.findUserById(registerUserToMagazineDTO.getUserId());
        if(!m.get().getMembers().contains(u)) {
            m.get().getMembers().add(u);
            magazineRepository.save(m.get());
            return m.get();
        }
        return null;
    }

    public boolean isUserMember(User u,Long magazineId){
        boolean isMember = false;
        Optional<Magazine> m = magazineRepository.findById(magazineId);
        //Optional<User> u = userRepository.findUserById(requestDTO.getUserId());
        for (User user:m.get().getMembers()) {
            if(user.equals(u))
                isMember= true;
        }
        return isMember;
    }

    public List<Magazine> getAll(){
        return magazineRepository.findAll();
    }

    public Magazine createMagazine(CreateMagazineRequestDTO createMagazineRequestDTO) throws InvalidUserException{
        Optional<User> userOptional = userRepository.findUserById(createMagazineRequestDTO.getMainEditorId());
        if(!userOptional.isPresent())
            throw new InvalidUserException();

        Magazine m = new Magazine();
        m.setIssn(createMagazineRequestDTO.getIssn());
        m.setName(createMagazineRequestDTO.getName());
        m.setOpenAccess(createMagazineRequestDTO.isOpenAccess());
        m.setMainEditor(userOptional.get());

        magazineRepository.save(m);

        return m;
    }
}
