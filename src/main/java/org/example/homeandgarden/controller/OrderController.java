package org.example.homeandgarden.controller;

import jakarta.validation.constraints.Pattern;
import org.example.homeandgarden.dto.requestdto.OrderRequestDto;
import org.example.homeandgarden.dto.responsedto.OrderResponseDto;
import org.example.homeandgarden.security.jwt.JwtAuthentication;
import org.example.homeandgarden.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Order controller", description = "Controller for managing user's orders")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/orders")
@Validated
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Getting order by id", description = "Provides functionality for getting user's order by order id")
    @SecurityRequirement(name = "JWT")
    @GetMapping(value = "/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponseDto getOrderById(@PathVariable
                                         @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                                         @Parameter(description = "Order identifier") Long orderId) {
        final JwtAuthentication jwtInfoToken = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String email = jwtInfoToken.getEmail();

        return orderService.getOrderById(orderId, email);
    }

    @Operation(summary = "Getting order history", description = "Provides functionality for getting all orders of a user ")
    @SecurityRequirement(name = "JWT")
    @GetMapping(value = "/history")
    @ResponseStatus(HttpStatus.OK)
    public Set<OrderResponseDto> getOrderHistory() {

        final JwtAuthentication jwtInfoToken = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String email = jwtInfoToken.getEmail();

        return orderService.getOrderHistory(email);
    }

    @Operation(summary = "Inserting a new order", description = "Provides functionality for inserting a new order")
    @SecurityRequirement(name = "JWT")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void insertOrder(@RequestBody @Valid OrderRequestDto orderRequestDto) {

        final JwtAuthentication jwtInfoToken = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String email = jwtInfoToken.getEmail();

        orderService.insertOrder(orderRequestDto, email);
    }

    @Operation(summary = "Changing an order status ", description = "Provides functionality for changing the status of an already placed order")
    @SecurityRequirement(name = "JWT")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    @PutMapping(value = "/change")
    @ResponseStatus(HttpStatus.OK)
    public void changeOrderStatus(@RequestParam("id")
                                  @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                                  @Parameter(description = "Order identifier") Long orderId,
                                  @RequestParam("status")
                                  @Pattern(regexp = "^(PENDING_PAYMENT|PAID|ON_THE_WAY|DELIVERED)$", message = "Invalid order status: Must be one of the: PENDING_PAYMENT, PAID, ON_THE_WAY or DELIVERED")
                                  @Parameter(description = "Status to which the order should be changed: <code>PENDING_PAYMENT</code>, <code>PAID</code>, <code>ON_THE_WAY</code> or <code>DELIVERED</code>") String status) {

        orderService.changeOrderStatus(orderId, status);
    }

    @Operation(summary = "Canceling an order", description = "Provides functionality for canceling an already placed order")
    @SecurityRequirement(name = "JWT")
    @PutMapping(value = "/cancel/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public void cancelOrder(@PathVariable
                            @Min(value = 1, message = "Invalid ID: Id must be greater than or equal to 1")
                            @Parameter(description = "Order identifier") Long orderId) {

        final JwtAuthentication jwtInfoToken = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String email = jwtInfoToken.getEmail();

        orderService.cancelOrder(orderId, email);
    }
}
