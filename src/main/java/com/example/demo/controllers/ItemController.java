package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final Logger logger = getLogger(ItemController.class);

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping
    public ResponseEntity<List<Item>> getItems() {
        this.logger.info("TASK: Get all items");
        return ok(this.itemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        this.logger.info("TASK: Get item by a specified ID");
        Optional<Item> optionalItem = this.itemRepository.findById(id);
        if (!optionalItem.isPresent()) {
            this.logger.error("ERROR: Item {} is not found. Failure to get the item.", id);
            return notFound().build();
        }
        this.logger.info("COMPLETION: Get item {} successfully.", id);
        return ok(optionalItem.get());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
        this.logger.info("TASK: Get all items by a specified name");
        List<Item> items = this.itemRepository.findByName(name);
        if (items == null || items.isEmpty()) {
            this.logger.error("ERROR: Items are not found by name {}. Failure to get the item.", name);
            return notFound().build();
        }
        this.logger.info("COMPLETION: Get all items by name {} successfully.", name);
        return ok(items);
    }

}