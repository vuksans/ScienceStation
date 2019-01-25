package com.ScienceStation.app.util;

        import com.ScienceStation.app.model.Magazine;
        import com.ScienceStation.app.model.ScienceBranch;
        import com.ScienceStation.app.model.User;
        import com.ScienceStation.app.model.enumeration.Role;
        import com.ScienceStation.app.repository.MagazineRepository;
        import com.ScienceStation.app.repository.ScienceBranchRepository;
        import com.ScienceStation.app.repository.UserRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.context.event.ApplicationReadyEvent;
        import org.springframework.context.event.EventListener;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Service;

        import java.util.ArrayList;
        import java.util.Optional;

@Service
public class FillDataBaseService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MagazineRepository magazineRepository;

    @Autowired
    private ScienceBranchRepository scienceBranchRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void addTestUsers(){
        Optional<User> userOptional = userRepository.findUserByEmail("admin@gmail.com");

        if(userOptional.isPresent())
            return;

        ScienceBranch scienceBranch1 = new ScienceBranch();
        scienceBranch1.setName("Economics");

        ScienceBranch scienceBranch2 = new ScienceBranch();
        scienceBranch2.setName("Politics");

        ScienceBranch scienceBranch3 = new ScienceBranch();
        scienceBranch3.setName("Math");

        ScienceBranch scienceBranch4 = new ScienceBranch();
        scienceBranch4.setName("Chemistry");

        ScienceBranch scienceBranch5 = new ScienceBranch();
        scienceBranch5.setName("Programming");




        User u1 = new User();
        u1.setFirstName("Admin");
        u1.setLastName("Admirovic");
        u1.setEmail("admin@gmail.com");
        u1.setCountry("Germany");
        u1.setCity("Berlin");
        u1.setEmailVerified(true);
        u1.setHashedPassword(passwordEncoder.encode("admin"));
        u1.setRole(Role.ROLE_ADMIN);
        u1.setDeleted(false);

        User u2 = new User();
        u2.setFirstName("Reviewer");
        u2.setLastName("Reviewervic");
        u2.setEmail("reviewer@gmail.com");
        u2.setCountry("Hungary");
        u2.setCity("Budapest");
        u2.setEmailVerified(true);
        u2.setHashedPassword(passwordEncoder.encode("123"));
        u2.setRole(Role.ROLE_REVIEWER);
        u2.setDeleted(false);
        u2.setBranch(scienceBranch1);

        User u3 = new User();
        u3.setFirstName("Editor");
        u3.setLastName("Editorovic");
        u3.setEmail("editor@gmail.com");
        u3.setCountry("Serbia");
        u3.setCity("Belgrade");
        u3.setEmailVerified(true);
        u3.setHashedPassword(passwordEncoder.encode("123"));
        u3.setRole(Role.ROLE_EDITOR);
        u3.setDeleted(false);
        u3.setBranch(scienceBranch1);

        User u4 = new User();
        u4.setFirstName("Author");
        u4.setLastName("Authorovic");
        u4.setEmail("vuksawow@gmail.com");
        u4.setCountry("Montenegro");
        u4.setCity("Podgorica");
        u4.setEmailVerified(true);
        u4.setHashedPassword(passwordEncoder.encode("123"));
        u4.setRole(Role.ROLE_AUTHOR);
        u4.setDeleted(false);

        User u5 = new User();
        u5.setFirstName("Reviewer2");
        u5.setLastName("Reviewerovic2");
        u5.setEmail("reviewer2@gmail.com");
        u5.setCountry("Montenegro");
        u5.setCity("Podgorica");
        u5.setEmailVerified(true);
        u5.setHashedPassword(passwordEncoder.encode("123"));
        u5.setRole(Role.ROLE_REVIEWER);
        u5.setDeleted(false);
        u5.setBranch(scienceBranch1);

        User u6 = new User();
        u6.setFirstName("MainEditor");
        u6.setLastName("MainEditor");
        u6.setEmail("nikola.vukasinovic@smart.edu.rs");
        u6.setCountry("Montenegro");
        u6.setCity("Podgorica");
        u6.setEmailVerified(true);
        u6.setHashedPassword(passwordEncoder.encode("123"));
        u6.setRole(Role.ROLE_MAIN_EDITOR);
        u6.setDeleted(false);
        u6.setBranch(scienceBranch1);

        User u10 = new User();
        u10.setFirstName("Reviewer123");
        u10.setLastName("Reviewer123");
        u10.setEmail("reviewer123@gmail.com");
        u10.setCountry("Germany");
        u10.setCity("Berlin");
        u10.setEmailVerified(true);
        u10.setHashedPassword(passwordEncoder.encode("123"));
        u10.setRole(Role.ROLE_REVIEWER);
        u10.setDeleted(false);
        u10.setBranch(scienceBranch5);

        ArrayList<User> editors = new ArrayList<>();
        editors.add(u3);

        ArrayList<User> reviewers = new ArrayList<>();
        reviewers.add(u5);
        reviewers.add(u2);
        reviewers.add(u10);

        Magazine magazine1 = new Magazine();
        magazine1.setMainEditor(u6);
        magazine1.setOpenAccess(true);
        magazine1.setName("Forbes");
        magazine1.setIssn(Long.parseLong("123112421123"));
        magazine1.setReviewers(reviewers);
        magazine1.setEditors(editors);

        scienceBranchRepository.save(scienceBranch1);
        scienceBranchRepository.save(scienceBranch2);
        scienceBranchRepository.save(scienceBranch3);
        scienceBranchRepository.save(scienceBranch4);
        scienceBranchRepository.save(scienceBranch5);

        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);
        userRepository.save(u4);
        userRepository.save(u5);
        userRepository.save(u6);

        magazineRepository.save(magazine1);

    }
}
