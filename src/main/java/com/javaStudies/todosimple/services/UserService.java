package com.javaStudies.todosimple.services;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javaStudies.todosimple.models.ProfileEnum;
import com.javaStudies.todosimple.models.User;
import com.javaStudies.todosimple.models.dto.UserCreateDto;
import com.javaStudies.todosimple.models.dto.UserUpdateDto;
import com.javaStudies.todosimple.repositories.*;
import com.javaStudies.todosimple.security.UserSpringSecurity;
import com.javaStudies.todosimple.services.exceptions.AuthorizationException;
import com.javaStudies.todosimple.services.exceptions.DataBindingViolationException;
import com.javaStudies.todosimple.services.exceptions.ObjectNotFoundException;

import jakarta.validation.Valid;

@Service
public class UserService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	public User findById(Long id) {
		UserSpringSecurity userSpringSecurity = authenticated();
		if (!Objects.nonNull(userSpringSecurity)
				|| !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !id.equals(userSpringSecurity.getId()))
			throw new AuthorizationException("Acesso negado");

		Optional<User> user = this.userRepository.findById(id);
		return user.orElseThrow(() -> new ObjectNotFoundException(
				"Usuário não encontrado! Id " + id + ", Tipo: " + User.class.getName()));
	}

	@Transactional
	public User create(User obj) {
		obj.setId((null));
		obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
		obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
		obj = this.userRepository.save(obj);
		return obj;
	}

	@Transactional
	public User update(User obj) {
		User newObj = findById(obj.getId());
		newObj.setPassword(obj.getPassword());
		newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
		return this.userRepository.save(newObj);
	}

	@Transactional
	public void delete(Long id) {
		findById(id);
		try {
			this.userRepository.deleteById(id);
		} catch (Exception e) {
			throw new DataBindingViolationException("Não é possível excluir pois há entidades relacionadas");
		}
	}

	public static UserSpringSecurity authenticated() {
		try {
			return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			return null;
		}
	}
    public User fromDTO(@Valid UserCreateDto obj) {
        User user = new User();
        user.setUsername(obj.getUsername());
        user.setPassword(obj.getPassword());
        return user;
    }
    public User fromDTO(@Valid UserUpdateDto obj) {
        User user = new User();
        user.setId(obj.getId());
        user.setPassword(obj.getPassword());
        return user;
    }
}
