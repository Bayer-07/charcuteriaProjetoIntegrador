package com.example.charcuteria.unit.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.example.charcuteria.model.Address;
import com.example.charcuteria.repository.address.AddressRepository;

@ExtendWith(MockitoExtension.class)
public class AddressRepositoryTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AddressRepository addressRepository;

    private Address mockAddress;

    @BeforeEach
    void setUp() {
        mockAddress = new Address();
        mockAddress.setId(1);
        mockAddress.setUSerId(10);
        mockAddress.setStreet("Rua Teste");
        mockAddress.setNumber("123");
        mockAddress.setComplement("Apto 1");
        mockAddress.setNeighborhood("Centro");
        mockAddress.setCity("Toledo");
        mockAddress.setState("PR");
        mockAddress.setZipCode("85900-000");
        mockAddress.setIsDefault(true);
    }

    @Test
    void testCreateAddress() {
        addressRepository.createAddress(mockAddress);

        verify(jdbcTemplate).update(
            anyString(),
            eq(10),
            eq("Rua Teste"),
            eq("123"),
            eq("Apto 1"),
            eq("Centro"),
            eq("Toledo"),
            eq("PR"),
            eq("85900-000"),
            eq(true)
        );
    }

    @Test
    void testFindById_Success() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Address>>any(), eq(1)))
            .thenReturn(mockAddress);

        Optional<Address> result = addressRepository.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals(10, result.get().getUserId());
    }

    @Test
    void testFindById_Exception() {
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Address>>any(), eq(1)))
            .thenThrow(new RuntimeException("Data access error"));

        Optional<Address> result = addressRepository.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUserId() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Address>>any(), eq(10)))
            .thenReturn(Collections.singletonList(mockAddress));

        List<Address> result = addressRepository.findByUserId(10);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getUserId());
    }

    @Test
    void testFindAll() {
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Address>>any()))
            .thenReturn(Collections.singletonList(mockAddress));

        List<Address> result = addressRepository.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void testUpdateAddress() {
        addressRepository.updateAddress(1, mockAddress);

        verify(jdbcTemplate).update(
            anyString(),
            eq(10),
            eq("Rua Teste"),
            eq("123"),
            eq("Apto 1"),
            eq("Centro"),
            eq("Toledo"),
            eq("PR"),
            eq("85900-000"),
            eq(true),
            eq(1)
        );
    }

    @Test
    void testClearDefaultByUserId() {
        addressRepository.clearDefaultByUserId(10);

        verify(jdbcTemplate).update(anyString(), eq(10));
    }

    @Test
    void testClearDefaultByUserIdExceptId() {
        addressRepository.clearDefaultByUserIdExceptId(10, 1);

        verify(jdbcTemplate).update(anyString(), eq(10), eq(1));
    }

    @Test
    void testDeleteById() {
        addressRepository.deleteById(1);

        verify(jdbcTemplate).update(anyString(), eq(1));
    }

    @Test
    void testDeleteByUserId() {
        addressRepository.deleteByUserId(10);

        verify(jdbcTemplate).update(anyString(), eq(10));
    }
}