package com.pm.orderservice.repository;

import com.pm.orderservice.model.Order;
import com.pm.orderservice.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Basic finders
    Optional<Order> findByOrderNumber(String orderNumber);
    Page<Order> findByUserId(Integer userId, Pageable pageable);
    List<Order> findByUserId(Integer userId);

    // Status-based queries
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    List<Order> findByStatus(OrderStatus status);
    long countByStatus(OrderStatus status);
    long countByUserId(Integer userId);

    // Date-based queries
    Page<Order> findByOrderDateBetween(Instant startDate, Instant endDate, Pageable pageable);
    List<Order> findByOrderDateBetween(Instant startDate, Instant endDate);

    // Payment-based queries
    Page<Order> findByPaymentStatus(String paymentStatus, Pageable pageable);
    List<Order> findByPaymentStatus(String paymentStatus);

    // User and status combination
    Page<Order> findByUserIdAndStatus(Integer userId, OrderStatus status, Pageable pageable);
    List<Order> findByUserIdAndStatus(Integer userId, OrderStatus status);

    // Search queries
    @Query("SELECT o FROM Order o WHERE " +
           "o.orderNumber LIKE %:searchTerm% OR " +
           "o.userEmail LIKE %:searchTerm% OR " +
           "o.userPhone LIKE %:searchTerm% OR " +
           "o.trackingNumber LIKE %:searchTerm%")
    Page<Order> searchOrders(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Advanced queries
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status IN :statuses")
    Page<Order> findByUserIdAndStatusIn(@Param("userId") Integer userId,
                                       @Param("statuses") List<OrderStatus> statuses,
                                       Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate AND o.status = :status")
    Page<Order> findByDateRangeAndStatus(@Param("startDate") Instant startDate,
                                        @Param("endDate") Instant endDate,
                                        @Param("status") OrderStatus status,
                                        Pageable pageable);

    // Analytics queries
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate")
    long countOrdersByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status AND o.orderDate >= :startDate AND o.orderDate <= :endDate")
    Double getTotalAmountByStatusAndDateRange(@Param("status") OrderStatus status,
                                            @Param("startDate") Instant startDate,
                                            @Param("endDate") Instant endDate);
}