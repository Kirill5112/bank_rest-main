package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardResponseDto;
import com.example.bankcards.dto.CurrentUserDto;
import com.example.bankcards.dto.UserResponseDto;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.BlockingNotOwnCardException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import static com.example.bankcards.util.SessionUserHelper.getSessionUsername;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final BankCardRepository cardsRepo;
    private final ModelMapper mapper;

    public Page<UserResponseDto> getUsers(Pageable pageable) {
        return userRepo.findAll(pageable).
                map(user -> mapper.map(user, UserResponseDto.class));
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User", id.toString()));
        return mapper.map(user, UserResponseDto.class);
    }

    public boolean toggleEnabledUser(Long id) {
        User model = userRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User", id.toString()));
        model.setEnabled(!model.isEnabled());
        userRepo.save(model);
        return model.isEnabled();
    }


    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    public CurrentUserDto getCurrent(Principal principal) {
        String username = getSessionUsername(principal);
        User user = userRepo.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User", username));
        CurrentUserDto current = mapper.map(user, CurrentUserDto.class);
        BigDecimal fullBalance = BigDecimal.ZERO;
        List<BankCard> cards = cardsRepo.findByOwner(username);
        for (BankCard card : cards) {
            fullBalance = fullBalance.add(card.getBalance());
        }
        current.setFullBalance(fullBalance);
        return current;
    }

    public Page<BankCardResponseDto> getUserCards(
            Principal principal, Pageable pageable, String search) {

        String username = getSessionUsername(principal);
        Page<BankCard> cards = search == null ?
                cardsRepo.findByOwner(username, pageable) :
                cardsRepo.findByOwnerIdWithSearch(username, pageable, search);
        return cards.map(bankCard ->
                mapper.map(bankCard, BankCardResponseDto.class));
    }

    public boolean blockCurrentCard(Principal principal, Long cardId) {
        String username = getSessionUsername(principal);
        BankCard card = cardsRepo.findById(cardId).orElseThrow(() ->
                new ResourceNotFoundException("BankCard", cardId.toString()));
        if (!card.getOwner().equals(username))
            throw new BlockingNotOwnCardException();
        if (card.getStatus() != CardStatus.BLOCKED) {
            card.setStatus(CardStatus.BLOCKED);
            cardsRepo.save(card);
        }
        return true;
    }
}
