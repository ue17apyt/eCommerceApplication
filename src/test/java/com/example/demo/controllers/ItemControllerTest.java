package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    private Item firstItem;
    private Item secondItem;
    private List<Item> items = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        openMocks(this);

        this.firstItem = new Item();
        this.firstItem.setId(1L);
        this.firstItem.setName("firstTestItem");
        this.firstItem.setPrice(new BigDecimal(1.23));
        this.firstItem.setDescription("First Item Description");

        this.secondItem = new Item();
        this.secondItem.setId(2L);
        this.secondItem.setName("secondTestItem");
        this.secondItem.setPrice(new BigDecimal(4.56));
        this.secondItem.setDescription("Second Item Description");

        this.items.add(this.firstItem);
        this.items.add(this.secondItem);

        when(this.itemRepository.findAll()).thenReturn(this.items);
        when(this.itemRepository.findById(1L)).thenReturn(ofNullable(this.firstItem));
        when(this.itemRepository.findById(2L)).thenReturn(ofNullable(this.secondItem));
        when(this.itemRepository.findByName("Items")).thenReturn(this.items);

    }

    @Test
    public void testGetAllItems() {

        final ResponseEntity<List<Item>> itemsResponseEntity = this.itemController.getItems();
        assertNotNull(itemsResponseEntity);
        assertEquals(OK, itemsResponseEntity.getStatusCode());
        assertEquals(2, itemsResponseEntity.getBody().size());

        List<Item> testItems = itemsResponseEntity.getBody();
        assertNotNull(testItems);
        assertArrayEquals(this.items.toArray(), testItems.toArray());

    }

    @Test
    public void testGetItemByCorrectId() {

        final ResponseEntity<Item> itemResponseEntity = this.itemController.getItemById(1L);
        assertNotNull(itemResponseEntity);
        assertEquals(OK, itemResponseEntity.getStatusCode());

        Item testItem = itemResponseEntity.getBody();
        assertNotNull(testItem);
        assertEquals("firstTestItem", testItem.getName());
        assertEquals(1.23, testItem.getPrice().doubleValue(), 0.001);
        assertEquals("First Item Description", testItem.getDescription());

    }

    @Test
    public void testGetItemByWrongId() {
        final ResponseEntity<Item> itemResponseEntity = this.itemController.getItemById(3L);
        assertNotNull(itemResponseEntity);
        assertEquals(NOT_FOUND, itemResponseEntity.getStatusCode());
    }

    @Test
    public void testGetItemsByCorrectName() {

        final ResponseEntity<List<Item>> itemsResponseEntity = this.itemController.getItemsByName("Items");
        assertNotNull(itemsResponseEntity);
        assertEquals(OK, itemsResponseEntity.getStatusCode());
        assertEquals(2, itemsResponseEntity.getBody().size());

        List<Item> testItems = itemsResponseEntity.getBody();
        assertNotNull(testItems);
        assertArrayEquals(this.items.toArray(), testItems.toArray());

    }

    @Test
    public void testGetItemsByWrongName() {
        final ResponseEntity<List<Item>> itemsResponseEntity = this.itemController.getItemsByName("AnotherItems");
        assertNotNull(itemsResponseEntity);
        assertEquals(NOT_FOUND, itemsResponseEntity.getStatusCode());
    }

}