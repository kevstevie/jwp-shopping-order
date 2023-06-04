package cart.application;

import cart.dao.CartItemDao;
import cart.dao.MemberDao;
import cart.dao.OrderDao;
import cart.dao.OrderItemDao;
import cart.domain.CartItem;
import cart.domain.Member;
import cart.domain.Order;
import cart.domain.PointPolicy;
import cart.dto.request.CartItemRequest;
import cart.dto.request.OrderCreateRequest;
import cart.dto.response.CartPointsResponse;
import cart.dto.response.OrderItemResponse;
import cart.dto.response.OrderResponse;
import cart.entity.OrderEntity;
import cart.entity.OrderItemEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Transactional
@Service
public class OrderService {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final CartItemDao cartItemDao;
    private final MemberDao memberDao;

    public OrderService(
            final OrderDao orderDao,
            final OrderItemDao orderItemDao,
            final CartItemDao cartItemDao,
            final MemberDao memberDao
    ) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.cartItemDao = cartItemDao;
        this.memberDao = memberDao;
    }

    public Long createOrder(final OrderCreateRequest orderCreateRequest, final Member member) {
        final List<Long> cartItemIds = orderCreateRequest.toCartItemIds();
        final List<CartItem> cartItems = cartItemDao.findByIds(cartItemIds);
        final Map<Long, Integer> requestProductIdQuantity = orderCreateRequest.getCartItems().stream()
                .collect(toMap(CartItemRequest::getProductId, CartItemRequest::getQuantity));

        for (final CartItem cartItem : cartItems) {
            cartItem.isSameProductAndQuantity(requestProductIdQuantity);
        }

        final Order order = new Order(orderCreateRequest.getUsedPoints(), cartItems, member.getPoints());
        final Long id = orderDao.save(order, PointPolicy.getSavingRate(), member);
        updateMember(member, order);
        cartItemDao.deleteAll(cartItemIds);

        return id;
    }

    private void updateMember(final Member member, final Order order) {
        final int savingPoints = PointPolicy.calculateSavingPoints(order.getPoints(), order.getCartItems());
        final Member updatedMember = member.updatePoints(savingPoints, order.getPoints());
        memberDao.updateMember(updatedMember);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(final Long orderId, final Member member) {
        final OrderEntity orderEntity = orderDao.findById(orderId, member.getId());
        final List<OrderItemEntity> orderItemEntities = orderItemDao.findByOrderId(orderId);
        final List<OrderItemResponse> orderItemResponses = parseOrderItemEntitiesToResponses(orderItemEntities);
        return new OrderResponse(orderEntity.getId(), orderEntity.getSavingRate(), orderEntity.getPoints(), orderItemResponses);
    }

    private List<OrderItemResponse> parseOrderItemEntitiesToResponses(final List<OrderItemEntity> orderItemEntities) {
        return orderItemEntities.stream()
                .map(orderItemEntity -> new OrderItemResponse(
                        orderItemEntity.getProductId(),
                        orderItemEntity.getProductName(),
                        orderItemEntity.getProductPrice(),
                        orderItemEntity.getProductQuantity(),
                        orderItemEntity.getProductImageUrl()
                )).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll(final Member member) {
        List<OrderEntity> orderEntities = orderDao.findAll(member.getId());

        List<OrderResponse> orderResponses = new ArrayList<>();
        for (final OrderEntity orderEntity : orderEntities) {
            List<OrderItemEntity> orderItemEntities = orderItemDao.findByOrderId(orderEntity.getId());
            final List<OrderItemResponse> orderItemResponses = parseOrderItemEntitiesToResponses(orderItemEntities);
            orderResponses.add(new OrderResponse(
                    orderEntity.getId(),
                    orderEntity.getSavingRate(),
                    orderEntity.getPoints(),
                    orderItemResponses
            ));
        }
        return orderResponses;
    }

    public CartPointsResponse calculatePoints(final Member member) {
        final List<CartItem> cartItems = cartItemDao.findByMemberIdAndChecked(member.getId());
        final int savingPoints = PointPolicy.calculateSavingPoints(0, cartItems);

        return new CartPointsResponse(PointPolicy.getSavingRate(), savingPoints);
    }
}
