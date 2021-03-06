package jpa.app.shop.domain;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "orders")
@Setter
@Getter
@ToString
public class Order {

	@Id
	@GeneratedValue
	@Column(name = "order_id")
	private Long id;

	@JsonIgnore
	@JoinColumn(name = "member_id")
	@ManyToOne(fetch = LAZY, cascade = ALL)
	@ToString.Exclude
	private Member member;

	@JsonIgnore
	@JoinColumn(name = "delivery_id")
	@OneToOne(fetch = LAZY, cascade = ALL)
	@ToString.Exclude
	private Delivery delivery;

	@JsonIgnore
	@OneToMany(cascade = ALL)
	@ToString.Exclude
	private final List<OrderItem> orderItems = new ArrayList<>();

	private LocalDateTime orderDate;

	@Enumerated(STRING)
	private OrderStatus status;

	protected Order() {
	}

	//==연관관계 메서드==//
	public void setMember(Member member) {
		this.member = member;
		member.getOrders().add(this);
	}

	public void addOrderItem(OrderItem orderItem) {
		orderItems.add(orderItem);
		orderItem.setOrder(this);
	}

	//==생성 메서드==//
	public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
		Order order = new Order();
		order.setMember(member);
		order.setDelivery(delivery);
		order.setStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());
		for (OrderItem orderItem : orderItems) {
			order.addOrderItem(orderItem);
		}
		order.setStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());

		return order;
	}

	//== 비즈니스 로직 ==//
	public void cancel() {
		if (delivery.getStatus() == DeliveryStatus.COMP) {
			throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
		}

		this.setStatus(OrderStatus.CANCEL);
		for (OrderItem orderItem : orderItems) {
			orderItem.cancel();
		}
	}

	//== 조회 로직 ==//

	/**
	 * 전체 주문 가격 조회
	 */
	public int getTotalPrice() {
		return orderItems.stream()
			.mapToInt(OrderItem::getTotalPrice)
			.sum();
	}
}
