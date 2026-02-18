package org.example.automarket.security;

import lombok.RequiredArgsConstructor;
import org.example.automarket.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Foydalanuvchi topilmadi: " + username
                ));
    }

}
