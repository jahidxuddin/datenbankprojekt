package de.ju.api.user;

import de.ju.api.security.JwtService;
import de.ju.api.user.exception.UserAlreadyExistsException;
import de.ju.api.user.model.AccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository repository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AppUser> findAllUser() {
        return repository.findAll();
    }

    public AppUser findUserById(UUID id) throws UsernameNotFoundException {
        return repository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    public AppUser findUserByToken(String token) throws UsernameNotFoundException {
        String username = jwtService.extractUsername(token);

        if (username == null) {
            throw new UsernameNotFoundException("Benutzer existiert nicht.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isValid(token, userDetails)) {
            throw new UsernameNotFoundException("Benutzer existiert nicht.");
        }

        return appUserRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Benutzer existiert nicht."));
    }

    public String updateUserAccountByToken(String token, AccountRequest account) throws RuntimeException {
        AppUser user = findUserByToken(token);

        if (!user.getEmail().equals(account.email())) {
            Optional<AppUser> userOptional = repository.findByEmail(account.email());
            if (userOptional.isPresent()) {
                throw new UserAlreadyExistsException("Benutzer existiert bereits.");
            }
        }

        user.setFirstName(account.firstName());
        user.setLastName(account.lastName());
        user.setEmail(account.email());
        if (!account.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(account.password()));
        }

        AppUser updatedUser = repository.save(user);

        return jwtService.generateToken(updatedUser);
    }
}
