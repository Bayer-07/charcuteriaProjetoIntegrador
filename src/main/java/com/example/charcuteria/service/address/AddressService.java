package com.example.charcuteria.service.address;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.charcuteria.dto.address.AddressDtoRequest;
import com.example.charcuteria.model.Address;
import com.example.charcuteria.repository.address.AddressRepository;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void createAddress(AddressDtoRequest addressDto) {
        Address newAddress = new Address(
            addressDto.getUserId(),
            addressDto.getStreet(),
            addressDto.getNumber(),
            addressDto.getComplement(),
            addressDto.getNeighborhood(),
            addressDto.getCity(),
            addressDto.getState(),
            addressDto.getZipCode());

        boolean isDefault = Boolean.TRUE.equals(addressDto.getIsDefault());
        newAddress.setIsDefault(isDefault);

        if (isDefault) {
            addressRepository.clearDefaultByUserId(addressDto.getUserId());
        }

        addressRepository.createAddress(newAddress);
    }

    public Optional<Address> getAddressById(Integer id) {
        return addressRepository.findById(id);
    }

    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    public List<Address> getAddressesByUserId(Integer userId) {
        return addressRepository.findByUserId(userId);
    }

    public void updateAddress(Integer id, AddressDtoRequest addressDto) {
        Optional<Address> existingAddress = addressRepository.findById(id);

        if (existingAddress.isPresent()) {
            Address address = existingAddress.get();
            boolean isDefault = addressDto.getIsDefault() != null ? addressDto.getIsDefault() : Boolean.TRUE.equals(address.getIsDefault());

            if (isDefault) {
                addressRepository.clearDefaultByUserIdExceptId(addressDto.getUserId(), id);
            }

            address.setUSerId(addressDto.getUserId());
            address.setStreet(addressDto.getStreet());
            address.setNumber(addressDto.getNumber());
            address.setComplement(addressDto.getComplement());
            address.setNeighborhood(addressDto.getNeighborhood());
            address.setCity(addressDto.getCity());
            address.setState(addressDto.getState());
            address.setZipCode(addressDto.getZipCode());
            address.setIsDefault(isDefault);

            addressRepository.updateAddress(id, address);
        } else {
            throw new RuntimeException("Endereço não encontrado com ID: " + id);
        }
    }

    public void setDefaultAddress(Integer id, Integer userId) {
        Optional<Address> existingAddress = addressRepository.findById(id);

        if (existingAddress.isPresent()) {
            Address address = existingAddress.get();
            if (!address.getUserId().equals(userId)) {
                throw new RuntimeException("Você não tem permissão para alterar este endereço");
            }
            addressRepository.clearDefaultByUserIdExceptId(userId, id);
            address.setIsDefault(true);
            addressRepository.updateAddress(id, address);
        } else {
            throw new RuntimeException("Endereço não encontrado com ID: " + id);
        }
    }

    public void deleteAddress(Integer id) {
        addressRepository.deleteById(id);
    }

    public void deleteAddressesByUserId(Integer userId) {
        addressRepository.deleteByUserId(userId);
    }
}
