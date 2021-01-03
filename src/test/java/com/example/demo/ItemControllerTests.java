package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTests {
    private ItemRepository itemRepository = mock(ItemRepository.class);

    private ItemController itemController = new ItemController(itemRepository);

    @Test
    public void getItemsByName_ItemsListIsNull() {
        when(itemRepository.findByName(anyString())).thenReturn(null);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("unknown_name");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }


    @Test
    public void getItemsByName_ItemsListIsEmpty() {
        when(itemRepository.findByName(anyString())).thenReturn(Collections.emptyList());
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("username");

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getItemsByName_Success() {
        List<Item> items = createItems();

        when(itemRepository.findByName(anyString())).thenReturn(items);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("username");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        List<Item> responseList = responseEntity.getBody();
        assertNotNull(responseList);

        assertEquals(items.size(), responseList.size());
        assertEquals(items.get(0).getId(), responseList.get(0).getId());
    }

    private List<Item> createItems() {
        Item item = new Item();
        item.setId(1L);

        return Collections.singletonList(item);
    }

}
