package com.example.wiki.service;

import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.wiki.api.PageRs;
import com.example.wiki.api.user.UserRq;
import com.example.wiki.api.user.UserRs;
import com.example.wiki.api.user.UserUpdateRq;
import com.example.wiki.entity.UserEntity;
import com.example.wiki.error.AlreadyExistsException;
import com.example.wiki.error.NotFoundException;
import com.example.wiki.error.PasswordConfirmationException;
import com.example.wiki.repository.UserRepository;
import com.example.wiki.security.UserPrincipal;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public UserEntity getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, id));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public UserEntity getEntityByLogin(String login) {
        return repository.findByLoginIgnoreCase(login)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, "login", login));
    }

    @Transactional(readOnly = true)
    public PageRs<UserRs> getAll(Pageable pageable) {
        return PageRs.from(repository.findAll(pageable), UserRs::from);
    }

    @Transactional(readOnly = true)
    public UserRs get(String login) {
        final UserEntity entity = getEntityByLogin(login);
        return UserRs.from(entity);
    }

    @Transactional
    public UserRs create(UserRq dto) {
        if (repository.findByLoginIgnoreCase(dto.login()).isPresent()) {
            throw new AlreadyExistsException(UserEntity.class, dto.login());
        }
        if (!Objects.equals(dto.password(), dto.passwordConfirm())) {
            throw new PasswordConfirmationException();
        }
        UserEntity entity = new UserEntity(dto.login(), passwordEncoder.encode(dto.password()));
        entity = repository.save(entity);
        return UserRs.from(entity);
    }

    @Transactional
    public UserRs update(String login, UserUpdateRq dto) {
        UserEntity entity = getEntityByLogin(login);
        if (!passwordEncoder.matches(dto.oldPassword(), entity.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        if (!Objects.equals(dto.newPassword(), dto.newPasswordConfirm())) {
            throw new PasswordConfirmationException();
        }
        entity.setPassword(passwordEncoder.encode(dto.newPassword()));
        entity = repository.save(entity);
        return UserRs.from(entity);
    }

    @Transactional
    public UserRs delete(String login, String password) {
        final UserEntity entity = getEntityByLogin(login);
        if (!passwordEncoder.matches(password, entity.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect");
        }
        repository.delete(entity);
        return UserRs.from(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            final UserEntity existsUser = getEntityByLogin(username);
            return new UserPrincipal(existsUser);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

}
