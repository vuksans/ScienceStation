package com.ScienceStation.app.service.user;

import com.ScienceStation.app.component.EmailSender;
import com.ScienceStation.app.component.JwtTokenUtil;
import com.ScienceStation.app.controller.user.dto.request.SignUpRequestDTO;
import com.ScienceStation.app.model.*;
import com.ScienceStation.app.model.enumeration.NotificationStatus;
import com.ScienceStation.app.model.enumeration.NotificationType;
import com.ScienceStation.app.model.enumeration.Role;
import com.ScienceStation.app.repository.*;
import com.ScienceStation.app.service.exception.EmailAlreadyExistsException;
import com.ScienceStation.app.service.exception.InvalidValueException;
import com.ScienceStation.app.service.exception.VerificationTokenInvalidException;
import com.ScienceStation.app.service.journal.JournalService;
import com.ScienceStation.app.service.notification.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    //private final PasswordEncoder passwordEncoder;

    private ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailSender sender;

    private final VerificationTokenRepository verificationTokenRepository;

    private final MagazineRepository magazineRepository;

    private final ScienceBranchRepository scienceBranchRepository;

    private final NotificationRepository notificationRepository;

    private final JournalRepository journalRepository;

    private final JournalReviewTaskRepository journalReviewTaskRepository;
    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserService(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passwordEncoder,
                       EmailSender sender, VerificationTokenRepository verificationTokenRepository, MagazineRepository magazineRepository, ScienceBranchRepository scienceBranchRepository,
                       NotificationRepository notificationRepository,JournalRepository journalRepository,JournalReviewTaskRepository journalReviewTaskRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sender = sender;
        this.verificationTokenRepository = verificationTokenRepository;
        this.magazineRepository = magazineRepository;
        this.scienceBranchRepository = scienceBranchRepository;
        this.notificationRepository = notificationRepository;
        this.journalRepository = journalRepository;
        this.journalReviewTaskRepository = journalReviewTaskRepository;
    }

    public User getAuthenticatedUser(String token){
        String username = jwtTokenUtil.getUserNameFromToken(token);
        User user = (User)userDetailsService.loadUserByUsername(username);
        return user;
    }

    @Transactional
    public User createUser(SignUpRequestDTO signUpRequestDTO) throws EmailAlreadyExistsException{
        Optional<User> userOptional = userRepository.findUserByEmail(signUpRequestDTO.getEmail());
        if(userOptional.isPresent())
            throw new EmailAlreadyExistsException();
        User user = new User();
        modelMapper.map(signUpRequestDTO,user);
        String password = signUpRequestDTO.getPassword();
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setRole(Role.ROLE_AUTHOR);
        //user.setEmailVerified(true);
        userRepository.save(user);
        generateAndSendToken(user);
        return user;
    }

    public void generateAndSendToken(User u){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setUser(u);
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(verificationToken);

        sender.confirmRegistration(verificationToken.getToken(),u.getEmail());
    }

    public VerificationToken validateToken(String token) throws VerificationTokenInvalidException{
        Optional<VerificationToken> verificationTokenOptional = verificationTokenRepository.findOneByToken(token);
        if(verificationTokenOptional.isPresent()){
            if(LocalDateTime.now().isAfter(verificationTokenOptional.get().getExpiryDate()))
                throw new VerificationTokenInvalidException();
        }
        else
            throw new VerificationTokenInvalidException();

        User u = verificationTokenOptional.get().getUser();
        u.setEmailVerified(true);
        userRepository.save(u);
        return verificationTokenOptional.get();
    }

    public List<User> getReviewers(Long magazineId,Long branchId) throws InvalidValueException {
        Optional<Magazine> magazine = magazineRepository.findById(magazineId);
        Optional<ScienceBranch> scienceBranch = scienceBranchRepository.findById(branchId);
        if(!magazine.isPresent() || !scienceBranch.isPresent())
            throw new InvalidValueException();

        List<User> filteredList = new ArrayList<>();
        for(User u:magazine.get().getReviewers()){
            if(u.getBranch().equals(scienceBranch.get()))
                filteredList.add(u);
        }

        return filteredList;
    }

    public List<User> getAvailableReviews(Long journalId)throws InvalidValueException{
        Optional<Journal> journalOptional = journalRepository.findById(journalId);
        if(!journalOptional.isPresent())
            throw new InvalidValueException();

        Journal journal = journalOptional.get();
        Magazine m = journal.getMagazine();

        Optional<JournalReviewTask> journalReviewTask = journalReviewTaskRepository.findByActiveAndJournal_Id(true,journalId);

        List <User> availableReviewers = new ArrayList<User>();

        if(!journalReviewTask.isPresent()){
            for(User u:m.getReviewers()){
                if(!checkIfReviewActive(u,journal))
                    if(u.getBranch().getId().equals(journal.getBranch().getId()))
                        availableReviewers.add(u);
            }
        }
        else{
            for(User u:m.getReviewers()){
                if(!checkIfReviewActive(u,journal)) {
                    if (u.getBranch().getId().equals(journal.getBranch().getId())) {
                        boolean flag = false;
                        for (Comment c : journalReviewTask.get().getComments()) {
                            if (c.getCreator().getId().equals(u.getId()))
                                flag = true;
                        }
                        if(!flag)
                            availableReviewers.add(u);
                    }
                }
            }
        }
        return availableReviewers;

    }

    public boolean checkIfReviewActive(User u,Journal j){
        List<Notification> list = notificationRepository.findAllByUser_IdAndStatus(u.getId(), NotificationStatus.PENDING);
        for(Notification n:list){
            if(n.getJournal().getId().equals(j.getId()) && n.getNotificationType().equals(NotificationType.REVIEW_JOURNAL))
                return true;
        }
        return false;
    }
}
