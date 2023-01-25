package crs.fcl.eim.sftp.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import crs.fcl.eim.sftp.dto.UserRegistrationDto;
import crs.fcl.eim.sftp.model.User;

public interface MyUserDetailsService extends UserDetailsService {

    User findByEmail(String email);
    
    User save(UserRegistrationDto registration);

    void updatePassword(String password, Long userId);
    
}
