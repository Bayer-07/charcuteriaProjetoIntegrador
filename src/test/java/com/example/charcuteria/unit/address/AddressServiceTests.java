package com.example.charcuteria.unit.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
public class AddressServiceTests {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private AddressDtoRequest addressDto;
    private Address mockAddress;

    @BeforeEach
    void setUp() {
        addressDto = new AddressDtoRequest();
        addressDto.setUserId(10);
        addressDto.setStreet("Rua Teste");
        addressDto.setNumber("123");
        addressDto.setComplement("Apto 1");
        addressDto.setNeighborhood("Centro");
        addressDto.setCity("Toledo");
        addressDto.setState("PR");
        addressDto.setZipCode("85900-000");
        addressDto.setIsDefault(false);

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
        mockAddress.setIsDefault(false);
    }

    @Test
    void testCreateAddress_NotDefault() {
        addressService.createAddress(addressDto);

        verify(addressRepository, never()).clearDefaultByUserId(any(Integer.class));
        verify(addressRepository).createAddress(any(Address.class));
    }

    @Test
    void testCreateAddress_IsDefault() {
        addressDto.setIsDefault(true);

        addressService.createAddress(addressDto);

        verify(addressRepository).clearDefaultByUserId(10);
        
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).createAddress(addressCaptor.capture());
        assertTrue(addressCaptor.getValue().getIsDefault());
    }

    @Test
    void testGetAddressById() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(mockAddress));

        Optional<Address> result = addressService.getAddressById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void testGetAllAddresses() {
        when(addressRepository.findAll()).thenReturn(Collections.singletonList(mockAddress));

        List<Address> result = addressService.getAllAddresses();

        assertEquals(1, result.size());
    }

    @Test
    void testGetAddressesByUserId() {
        when(addressRepository.findByUserId(10)).thenReturn(Collections.singletonList(mockAddress));

        List<Address> result = addressService.getAddressesByUserId(10);

        assertEquals(1, result.size());
    }

    @Test
    void testUpdateAddress_SuccessAndNotDefault() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(mockAddress));

        addressService.updateAddress(1, addressDto);

        verify(addressRepository, never()).clearDefaultByUserIdExceptId(any(Integer.class), any(Integer.class));
        verify(addressRepository).updateAddress(eq(1), any(Address.class));
    }

    @Test
    void testUpdateAddress_SuccessAndIsDefault() {
        addressDto.setIsDefault(true);
        when(addressRepository.findById(1)).thenReturn(Optional.of(mockAddress));

        addressService.updateAddress(1, addressDto);

        verify(addressRepository).clearDefaultByUserIdExceptId(10, 1);
        verify(addressRepository).updateAddress(eq(1), any(Address.class));
    }

    @Test
    void testUpdateAddress_NotFound() {
        when(addressRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> addressService.updateAddress(1, addressDto));
        verify(addressRepository, never()).updateAddress(any(Integer.class), any(Address.class));
    }

    @Test
    void testSetDefaultAddress_Success() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(mockAddress));

        addressService.setDefaultAddress(1, 10);

        verify(addressRepository).clearDefaultByUserIdExceptId(10, 1);
        
        ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).updateAddress(eq(1), addressCaptor.capture());
        assertTrue(addressCaptor.getValue().getIsDefault());
    }

    @Test
    void testSetDefaultAddress_NoPermission() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(mockAddress));

        assertThrows(RuntimeException.class, () -> addressService.setDefaultAddress(1, 99));
        verify(addressRepository, never()).updateAddress(any(Integer.class), any(Address.class));
    }

    @Test
    void testSetDefaultAddress_NotFound() {
        when(addressRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> addressService.setDefaultAddress(1, 10));
        verify(addressRepository, never()).updateAddress(any(Integer.class), any(Address.class));
    }

    @Test
    void testDeleteAddress() {
        addressService.deleteAddress(1);

        verify(addressRepository).deleteById(1);
    }

    @Test
    void testDeleteAddressesByUserId() {
        addressService.deleteAddressesByUserId(10);

        verify(addressRepository).deleteByUserId(10);
    }
}