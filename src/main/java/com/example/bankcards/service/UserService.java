package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardResponseDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final BankCardRepository cardsRepo;
    private final ModelMapper mapper;

    public List<BankCardResponseDto> getUserCards(Long ownerId) {
        return cardsRepo.findByOwnerId(ownerId).stream()
                .map(bankCard ->
                        mapper.map(bankCard, BankCardResponseDto.class))
                .toList();
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepo.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        return mapper.map(user, UserResponseDto.class);
    }

    public boolean toggleEnabledUser(Long id) {
        User model = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        model.setEnabled(!model.isEnabled());
        userRepo.save(model);
        return model.isEnabled();
    }


    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

}
