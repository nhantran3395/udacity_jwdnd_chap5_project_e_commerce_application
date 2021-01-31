package com.example.demo.controllers;

import java.util.List;

import com.example.demo.errorhandling.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@Slf4j
@RestController
@RequestMapping("/api/item")
public class ItemController {

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		List<Item> items = itemRepository.findAll();

		log.info("GET item");
		log.info("find all items");
		log.info(items.toString());

		return ResponseEntity.ok(items);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		Item item = itemRepository.findById(id).orElseThrow(()->{return new EntityNotFoundException(Item.class,"id",id.toString()); });

		log.info("GET item/" + id);
		log.info("find item by id");
		log.info(item.toString());

		return ResponseEntity.of(itemRepository.findById(id));
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		List<Item> items = itemRepository.findByName(name);

		if(items.isEmpty()){
			throw new EntityNotFoundException(Item.class,"name",name);
		}

		log.info("GET item/name/" + name);
		log.info("find items by name");
		log.info(items.toString());

		return ResponseEntity.ok(items);
	}
	
}
