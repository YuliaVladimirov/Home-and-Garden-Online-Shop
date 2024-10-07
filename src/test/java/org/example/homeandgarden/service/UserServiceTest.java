package org.example.homeandgarden.service;

import org.example.homeandgarden.dto.requestdto.*;
import org.example.homeandgarden.dto.responsedto.UserResponseDto;
import org.example.homeandgarden.entity.*;
import org.example.homeandgarden.entity.enums.Role;
import org.example.homeandgarden.exception.*;
import org.example.homeandgarden.mapper.*;
import org.example.homeandgarden.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private Mappers mappers;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDto userCreateDto;
    private User createUser;
    private UserRequestDto userUpdateDto;
    private User updateUser;
    private UserResponseDto userResponseDto;
    private User user;

    @BeforeEach
    void setUp() {
        userCreateDto = UserRequestDto.builder()
                .name("Arne Oswald")
                .email("arneoswald@example.com")
                .phone("+111111111111")
                .password("ClientPass1$trong")
                .build();

        createUser = new User();
        createUser.setName("Arne Oswald");
        createUser.setEmail("arneoswald@example.com");
        createUser.setPhone("+111111111111");
        createUser.setPasswordHash("$2a$10$yovX4MDz2oZKpqq6DiWfrOkpJ3");

        userUpdateDto = UserRequestDto.builder()
                .name("New Arne Oswald")
                .phone("+999999999999")
                .build();

        updateUser = new User();
        updateUser.setName("Old Arne Oswald");
        updateUser.setPhone("+111111111111");

        userResponseDto = UserResponseDto.builder()
                .userId(1L)
                .name("Arne Oswald")
                .email("arneoswald@example.com")
                .phone("+496151226")
                .passwordHash("$2a$10$yovX4MDz2oZKpqq6DiWfrOkpJ3")
                .role(Role.CLIENT)
                .build();

       user = new User(1L,
                "Arne Oswald",
                "arneoswald@example.com",
                "+496151226",
                "$2a$10$yovX4MDz2oZKpqq6DiWfrOkpJ3",
                Role.CLIENT,
                null,
                null,
                null,
                null);

    }

    @Test
    void registerUser() {
        when(userRepository.existsByEmail(userCreateDto.getEmail())).thenReturn(false);
        when(mappers.convertToUser(userCreateDto)).thenReturn(createUser);
        when(passwordEncoder.encode(userCreateDto.getPassword())).thenReturn("$2a$10$yovX4MDz2oZKpqq6DiWfrOkpJ3.xzCmj8cko5vNWN8kfZamm3AdTa");

        userService.registerUser(userCreateDto);

        verify(userRepository, times(1)).save(createUser);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(mappers, times(1)).convertToUser(any(UserRequestDto.class));
        verify(passwordEncoder, times(1)).encode(userCreateDto.getPassword());
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(userCreateDto.getEmail())).thenReturn(true);

        Exception exception = assertThrows(DataAlreadyExistsException.class, () -> {
            userService.registerUser(userCreateDto);
        });

        String expectedMessage = "User already exists";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerAdmin() {
        when(userRepository.existsByEmail(userCreateDto.getEmail())).thenReturn(false);
        when(mappers.convertToUser(userCreateDto)).thenReturn(createUser);
        when(passwordEncoder.encode(userCreateDto.getPassword())).thenReturn("$2a$10$yovX4MDz2oZKpqq6DiWfrOkpJ3.xzCmj8cko5vNWN8kfZamm3AdTa");

        userService.registerAdmin(userCreateDto);

        verify(userRepository, times(1)).save(createUser);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(mappers, times(1)).convertToUser(any(UserRequestDto.class));
        verify(passwordEncoder, times(1)).encode(userCreateDto.getPassword());
    }

    @Test
    void registerAdmin_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(userCreateDto.getEmail())).thenReturn(true);

        Exception exception = assertThrows(DataAlreadyExistsException.class, () -> {
            userService.registerAdmin(userCreateDto);
        });

        String expectedMessage = "User already exists";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void updateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(updateUser));

        userService.updateUser(1L, userUpdateDto);

        assertEquals(userUpdateDto.getName(), updateUser.getName());
        assertEquals(userUpdateDto.getPhone(), updateUser.getPhone());

        verify(userRepository, times(1)).save(updateUser);
    }

    @Test
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundInDataBaseException.class, () -> {
            userService.updateUser(1L, userUpdateDto);
        });

        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser() {
        User user = new User();
        user.setUserId(1L);

        Cart cart = new Cart();
        cart.setCartId(1L);
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(cartRepository, times(1)).delete(cart);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataNotFoundInDataBaseException.class, () -> {
            userService.deleteUser(1L);
        });

        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        verify(cartRepository, never()).delete(any(Cart.class));
        verify(userRepository, never()).deleteById(anyLong());
    }


    @Test
    void getUserByEmail() {
        String email = "arneoswald@example.com";
        String wrongEmail = "123@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(mappers.convertToUserResponseDto(any(User.class))).thenReturn(userResponseDto);

        UserResponseDto actualUserResponseDto = userService.getUserByEmail(email);

        verify(userRepository, times(1)).findByEmail(email);
        verify(mappers, times(1)).convertToUserResponseDto(any(User.class));

        assertEquals(userResponseDto.getUserId(), actualUserResponseDto.getUserId());

        when(userRepository.findByEmail(wrongEmail)).thenReturn(Optional.empty());
        DataNotFoundInDataBaseException dataNotFoundInDataBaseException = assertThrows(DataNotFoundInDataBaseException.class,
                () -> userService.getUserByEmail(wrongEmail));
        assertEquals("User not found in database.", dataNotFoundInDataBaseException.getMessage());
    }

}
