package com.example.charcuteria.unit.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.charcuteria.dto.address.AddressDtoRequest;
import com.example.charcuteria.model.Address;
import com.example.charcuteria.repository.address.AddressRepository;
import com.example.charcuteria.service.address.AddressService;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @Test
    void createAddress_whenMarkedDefault_shouldClearOtherDefaultsFirst() {
        AddressDtoRequest dto = new AddressDtoRequest();
        dto.setUserId(1);
        dto.setStreet("Rua A");
        dto.setNumber("10");
        dto.setCity("Toledo");
        dto.setState("PR");
        dto.setZipCode("85900-000");
        dto.setIsDefault(true);

        addressService.createAddress(dto);

        verify(addressRepository).clearDefaultByUserId(1);

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).createAddress(captor.capture());

        assertTrue(Boolean.TRUE.equals(captor.getValue().getIsDefault()));
    }

    @Test
    void createAddress_whenNotDefault_shouldNotClearOtherDefaults() {
        AddressDtoRequest dto = new AddressDtoRequest();
        dto.setUserId(1);
        dto.setStreet("Rua B");
        dto.setNumber("20");
        dto.setCity("Toledo");
        dto.setState("PR");
        dto.setZipCode("85900-000");
        dto.setIsDefault(false);

        addressService.createAddress(dto);

        verify(addressRepository, never()).clearDefaultByUserId(1);
        verify(addressRepository).createAddress(org.mockito.ArgumentMatchers.any(Address.class));
    }

    @Test
    void updateAddress_whenMarkedDefault_shouldClearOtherDefaultsExceptCurrent() {
        Address existing = new Address();
        existing.setId(10);
        existing.setUSerId(1);
        existing.setIsDefault(false);

        when(addressRepository.findById(10)).thenReturn(Optional.of(existing));

        AddressDtoRequest dto = new AddressDtoRequest();
        dto.setUserId(1);
        dto.setStreet("Rua Nova");
        dto.setNumber("11");
        dto.setCity("Toledo");
        dto.setState("PR");
        dto.setZipCode("85900-001");
        dto.setIsDefault(true);

        addressService.updateAddress(10, dto);

        verify(addressRepository).clearDefaultByUserIdExceptId(1, 10);

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).updateAddress(org.mockito.ArgumentMatchers.eq(10), captor.capture());

        assertTrue(Boolean.TRUE.equals(captor.getValue().getIsDefault()));
        assertEquals("Rua Nova", captor.getValue().getStreet());
    }

    @Test
    void updateAddress_whenDefaultFlagMissing_shouldKeepCurrentValue() {
        Address existing = new Address();
        existing.setId(12);
        existing.setUSerId(1);
        existing.setIsDefault(true);

        when(addressRepository.findById(12)).thenReturn(Optional.of(existing));

        AddressDtoRequest dto = new AddressDtoRequest();
        dto.setUserId(1);
        dto.setStreet("Rua Mantida");
        dto.setNumber("12");
        dto.setCity("Toledo");
        dto.setState("PR");
        dto.setZipCode("85900-002");

        addressService.updateAddress(12, dto);

    verify(addressRepository).clearDefaultByUserIdExceptId(1, 12);

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).updateAddress(org.mockito.ArgumentMatchers.eq(12), captor.capture());

        assertTrue(Boolean.TRUE.equals(captor.getValue().getIsDefault()));
    }

    @Test
    void setDefaultAddress_success() {
        Address existing = new Address();
        existing.setId(15);
        existing.setUSerId(1);
        existing.setIsDefault(false);

        when(addressRepository.findById(15)).thenReturn(Optional.of(existing));

        addressService.setDefaultAddress(15, 1);

        verify(addressRepository).clearDefaultByUserIdExceptId(1, 15);

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).updateAddress(org.mockito.ArgumentMatchers.eq(15), captor.capture());

        assertTrue(Boolean.TRUE.equals(captor.getValue().getIsDefault()));
    }

    @Test
    void setDefaultAddress_whenNotFound_shouldThrowException() {
        when(addressRepository.findById(99)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            addressService.setDefaultAddress(99, 1);
        });
    }

    @Test
    void setDefaultAddress_whenUnauthorized_shouldThrowException() {
        Address existing = new Address();
        existing.setId(15);
        existing.setUSerId(2);
        existing.setIsDefault(false);

        when(addressRepository.findById(15)).thenReturn(Optional.of(existing));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            addressService.setDefaultAddress(15, 1);
        });
    }
}