package com.EGM.LMS.controller;

import com.EGM.LMS.dto.OrderDTO;
import com.EGM.LMS.dto.OrderItemDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Course;
import com.EGM.LMS.model.Order;
import com.EGM.LMS.model.OrderItem;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.OrderItemRepository;
import com.EGM.LMS.repository.OrderRepository;
import com.EGM.LMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * Student: create an order from cart courseIds (for MANUAL payments).
     * Body: { "courseIds": ["...","..."] }
     */
    @PostMapping("/cart")
    public ResponseEntity<OrderDTO> createCartOrder(@RequestBody Map<String, Object> body) {
        var student = resolveAuthenticatedUser();
        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can create orders");
        }

        Object idsObj = body != null ? body.get("courseIds") : null;
        if (!(idsObj instanceof List<?> rawList) || rawList.isEmpty()) {
            throw new IllegalArgumentException("courseIds is required");
        }

        List<UUID> courseIds;
        try {
            courseIds = rawList.stream().map(String::valueOf).map(UUID::fromString).toList();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid courseIds");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        String currency = "ETB";

        Order order = Order.builder()
                .student(student)
                .gateway("MANUAL")
                .status("PENDING")
                .build();
        order = orderRepository.save(order);

        for (UUID courseId : courseIds) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
            BigDecimal lineAmount = course.getDiscountPrice() != null && course.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                    ? course.getDiscountPrice() : course.getPrice();
            if (lineAmount == null) lineAmount = BigDecimal.ZERO;
            if (currency == null || currency.isBlank()) currency = course.getCurrency() != null ? course.getCurrency() : "ETB";
            totalAmount = totalAmount.add(lineAmount);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .course(course)
                    .amount(lineAmount)
                    .build();
            orderItemRepository.save(item);
        }

        order.setTotalAmount(totalAmount);
        order.setCurrency(currency);
        order = orderRepository.save(order);

        return ResponseEntity.ok(toDto(order));
    }

    private OrderDTO toDto(Order order) {
        var items = orderItemRepository.findByOrder_Id(order.getId()).stream()
                .map(this::toItemDto)
                .toList();
        return OrderDTO.builder()
                .id(order.getId())
                .student(order.getStudent() != null ? UserDTO.builder().id(order.getStudent().getId()).build() : null)
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .gateway(order.getGateway())
                .status(order.getStatus())
                .discountAmount(order.getDiscountAmount())
                .paidAt(order.getPaidAt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(items)
                .build();
    }

    private OrderItemDTO toItemDto(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .amount(item.getAmount())
                .platformShare(item.getPlatformShare())
                .instructorShare(item.getInstructorShare())
                .course(item.getCourse() != null ? com.EGM.LMS.dto.CourseDTO.builder().id(item.getCourse().getId()).build() : null)
                .build();
    }

    private com.EGM.LMS.model.User resolveAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}

