package com.example.charcuteria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.charcuteria.model.Address;

@Repository
public class AddressRepository {
    private final JdbcTemplate jdbcTemplate;

    public AddressRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Address> addressRowMapper = (rs, rowNum) -> {
        Address addr = new Address();
        addr.setId(rs.getInt("id"));
        addr.setUSerId(rs.getInt("user_id"));
        addr.setStreet(rs.getString("street"));
        addr.setNumber(rs.getString("number"));
        addr.setComplement(rs.getString("complement"));
        addr.setNeighborhood(rs.getString("neighborhood"));
        addr.setCity(rs.getString("city"));
        addr.setState(rs.getString("state"));
        addr.setZipCode(rs.getString("zip_code"));
        addr.setIsDefault(rs.getBoolean("is_default"));
        return addr;
    };

    public void createAddress(Address address) {
        String sql = "INSERT INTO addresses (user_id, street, number, complement, neighborhood, city, state, zip_code, is_default) VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUE)";

        jdbcTemplate.update(
                sql,
                address.getUserId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode());
    }

    public Optional<Address> findById(Integer id) {
        String sql = "SELECT * FROM addresses WHERE id = ?";
        try {
            Address address = jdbcTemplate.queryForObject(sql, addressRowMapper, id);
            return Optional.ofNullable(address);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Address> findByUserId(Integer userId) {
        String sql = "SELECT * FROM addresses WHERE user_id = ? ORDER BY is_default DESC";
        return jdbcTemplate.query(sql, addressRowMapper, userId);
    }

    public List<Address> findAll() {
        String sql = "SELECT * FROM addresses";
        return jdbcTemplate.query(sql, addressRowMapper);
    }

    public void updateAddress(Integer id, Address address) {
        String sql = "UPDATE addresses SET user_id = ?, street = ?, number = ?, complement = ?, neighborhood = ?, city = ?, state = ?, zip_code = ?, is_default = ? WHERE id = ?";

        jdbcTemplate.update(
                sql,
                address.getUserId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getIsDefault(),
                id);
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM addresses WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteByUserId(Integer userId) {
        String sql = "DELETE FROM addresses WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}
