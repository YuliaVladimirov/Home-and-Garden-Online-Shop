package org.example.homeandgarden.service;

import org.example.homeandgarden.dto.requestdto.*;
import org.example.homeandgarden.dto.responsedto.UserResponseDto;
import org.example.homeandgarden.entity.*;
import org.example.homeandgarden.entity.enums.*;
import org.example.homeandgarden.exception.*;
import org.example.homeandgarden.mapper.*;
import org.example.homeandgarden.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mappers mappers;
    private final CartRepository cartRepository;

    @Transactional
    public void registerUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new DataAlreadyExistsException("User already exists.");
        }
        User user = mappers.convertToUser(userRequestDto);
        user.setRole(Role.CLIENT);
        user.setPasswordHash(passwordEncoder.encode(userRequestDto.getPassword()));
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        userRepository.save(user);
        cartRepository.save(cart);
    }

    @Transactional
    public void registerAdmin(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new DataAlreadyExistsException("User already exists.");
        }
        User user = mappers.convertToUser(userRequestDto);
        user.setRole(Role.ADMINISTRATOR);
        user.setPasswordHash(passwordEncoder.encode(userRequestDto.getPassword()));
        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);
        userRepository.save(user);
        cartRepository.save(cart);
    }


    public void updateUser(Long id, UserRequestDto userUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundInDataBaseException("User not found in database."));
        user.setName(userUpdateDto.getName());
        user.setPhone(userUpdateDto.getPhone());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundInDataBaseException("User not found in database."));
        if (user.getCart() != null) {
            cartRepository.delete(user.getCart());
        }
        userRepository.deleteById(id);
    }

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        UserResponseDto userResponseDto;
        if (user != null) {
            userResponseDto = mappers.convertToUserResponseDto(user);
            userResponseDto.setPasswordHash("***");
        } else {
            throw new DataNotFoundInDataBaseException("User not found in database.");
        }
        return userResponseDto;
    }

}