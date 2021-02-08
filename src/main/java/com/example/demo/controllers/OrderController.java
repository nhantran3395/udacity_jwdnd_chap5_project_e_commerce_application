package com.example.demo.controllers;

import java.util.List;

import com.example.demo.errorhandling.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		log.info("POST order/submit/" + username);
		log.info("create order for user with name: " + username);

		User user = userRepository.findByUsername(username).orElseThrow(()->{return new EntityNotFoundException(User.class,"username",username); });

		UserOrder order = orderRepository.save(UserOrder.createFromCart(user.getCart()));

		log.info(order.toString());

		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		log.info("GET order/history/" + username);
		log.info("find all orders for user with name: " + username);

		User user = userRepository.findByUsername(username).orElseThrow(()->{return new EntityNotFoundException(User.class,"username",username); });

		List<UserOrder> orders = orderRepository.findByUser(user);
		log.info(orders.toString());

		return ResponseEntity.ok(orders);
	}
}
