package com.epam.esm.service;

import com.epam.esm.dto.OrderDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GiftCertificateRepository certificateRepository;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @BeforeAll
    static void setUp() {
        MockitoAnnotations.openMocks(OrderServiceTest.class);
    }

    @Test
    void testFindAll() {
        PageContext pageContext = new PageContext();
        when(orderRepository.findAll(pageContext)).thenReturn(provideOrders());

        List<OrderDto> expectedDtoList = provideOrderDtoList();
        List<OrderDto> actualDtoList = orderService.findAll(pageContext);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testFindByUser() {
        PageContext pageContext = new PageContext();
        long userId = 1;
        when(orderRepository.findByUser(pageContext, userId)).thenReturn(provideOrders());

        List<OrderDto> expectedDtoList = provideOrderDtoList();
        List<OrderDto> actualDtoList = orderService.findByUser(userId, pageContext);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testFindById() {
        Order order = provideOrders().get(0);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderDto expectedDto = provideOrderDtoList().get(0);
        OrderDto actualDto = orderService.findById(order.getId());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testFindByIdWhenOrderNotFound() {
        long orderId = 1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.findById(orderId));
    }

    @Test
    void testMakeOrder() {
        User user = provideUser();
        GiftCertificate certificate = provideCertificate();
        OrderDto orderDto = provideOrderDtoList().get(0);
        Order order = provideOrders().get(0);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(certificateRepository.findById(certificate.getId())).thenReturn(Optional.of(certificate));
        when(orderRepository.create(any(Order.class))).thenReturn(order);

        orderService.makeOrder(orderDto);

        verify(orderRepository).create(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertTrue(capturedOrder.getCost() != null && capturedOrder.getPurchaseDate() != null);
    }

    @Test
    void testMakeOrderWhenUserNotFound() {
        User user = provideUser();
        OrderDto orderDto = provideOrderDtoList().get(0);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.makeOrder(orderDto));
    }

    @Test
    void testMakeOrderWhenCertificateNotFound() {
        User user = provideUser();
        GiftCertificate certificate = provideCertificate();
        OrderDto orderDto = provideOrderDtoList().get(0);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(certificateRepository.findById(certificate.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.makeOrder(orderDto));
    }

    private List<Order> provideOrders() {
        User user = provideUser();
        GiftCertificate certificate = provideCertificate();

        Order firstOrder = new Order();
        firstOrder.setId(1);
        firstOrder.setUser(user);
        firstOrder.setCertificate(certificate);

        Order secondOder = new Order();
        secondOder.setId(2);
        secondOder.setUser(user);
        secondOder.setCertificate(certificate);

        Order thirdOrder = new Order();
        thirdOrder.setId(3);
        thirdOrder.setUser(user);
        thirdOrder.setCertificate(certificate);

        return List.of(firstOrder, secondOder, thirdOrder);
    }

    private List<OrderDto> provideOrderDtoList() {
        User user = provideUser();
        GiftCertificate certificate = provideCertificate();

        OrderDto firstDto = new OrderDto();
        firstDto.setId(1);
        firstDto.setUserId(user.getId());
        firstDto.setCertificateId(certificate.getId());

        OrderDto secondDto = new OrderDto();
        secondDto.setId(2);
        secondDto.setUserId(user.getId());
        secondDto.setCertificateId(certificate.getId());

        OrderDto thirdDto = new OrderDto();
        thirdDto.setId(3);
        thirdDto.setUserId(user.getId());
        thirdDto.setCertificateId(certificate.getId());

        return List.of(firstDto, secondDto, thirdDto);
    }

    private User provideUser() {
        User user = new User();
        user.setId(1);

        return user;
    }

    private GiftCertificate provideCertificate() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setId(1);
        certificate.setPrice(BigDecimal.TEN);

        return certificate;
    }
}
