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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        PageContext pageContext = PageContext.of(null, null);
        when(orderRepository.findAll(pageContext)).thenReturn(provideOrders());

        List<OrderDto> expectedDtoList = provideOrderDtoList();
        List<OrderDto> actualDtoList = orderService.findAll(pageContext);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testFindByUser() {
        PageContext pageContext = PageContext.of(null, null);
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
        GiftCertificate certificate = provideCertificates().get(0);
        OrderDto orderDto = provideOrderDtoList().get(0);
        Order order = provideOrders().get(0);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(certificate));
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
        OrderDto orderDto = provideOrderDtoList().get(0);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.makeOrder(orderDto));
    }

    private List<Order> provideOrders() {
        User user = provideUser();
        List<GiftCertificate> certificates = provideCertificates();

        Order firstOrder = new Order();
        firstOrder.setId(1);
        firstOrder.setUser(user);
        firstOrder.setCertificates(certificates);

        Order secondOder = new Order();
        secondOder.setId(2);
        secondOder.setUser(user);
        secondOder.setCertificates(certificates);

        Order thirdOrder = new Order();
        thirdOrder.setId(3);
        thirdOrder.setUser(user);
        thirdOrder.setCertificates(certificates);

        return List.of(firstOrder, secondOder, thirdOrder);
    }

    private List<OrderDto> provideOrderDtoList() {
        User user = provideUser();
        List<Long> certificateIds = provideCertificates()
                .stream()
                .map(GiftCertificate::getId)
                .toList();

        OrderDto firstDto = new OrderDto();
        firstDto.setId(1);
        firstDto.setUserId(user.getId());
        firstDto.setCertificateIds(certificateIds);

        OrderDto secondDto = new OrderDto();
        secondDto.setId(2);
        secondDto.setUserId(user.getId());
        secondDto.setCertificateIds(certificateIds);

        OrderDto thirdDto = new OrderDto();
        thirdDto.setId(3);
        thirdDto.setUserId(user.getId());
        thirdDto.setCertificateIds(certificateIds);

        return List.of(firstDto, secondDto, thirdDto);
    }

    private User provideUser() {
        User user = new User();
        user.setId(1);

        return user;
    }

    private List<GiftCertificate> provideCertificates() {
        GiftCertificate firstCertificate = new GiftCertificate();
        firstCertificate.setId(1);
        firstCertificate.setPrice(BigDecimal.TEN);

        GiftCertificate secondCertificate = new GiftCertificate();
        secondCertificate.setId(2);
        secondCertificate.setPrice(BigDecimal.TEN);

        GiftCertificate thirdCertificate = new GiftCertificate();
        thirdCertificate.setId(3);
        thirdCertificate.setPrice(BigDecimal.TEN);

        return List.of(firstCertificate, secondCertificate, thirdCertificate);
    }
}
