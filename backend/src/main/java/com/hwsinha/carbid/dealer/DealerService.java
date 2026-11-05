package com.hwsinha.carbid.dealer;

import com.hwsinha.carbid.dealer.dto.CreateDealerRequest;
import com.hwsinha.carbid.dealer.dto.DealerResponse;
import com.hwsinha.carbid.user.Role;
import com.hwsinha.carbid.user.User;
import com.hwsinha.carbid.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DealerService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DealerRepository dealerRepository;

    @Transactional
    public DealerResponse createDealer(CreateDealerRequest request){
        String email= request.email().trim().toLowerCase(Locale.ROOT);
        if(userRepository.existsByEmail(email)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user=new User();
        user.setEmail(email);
        user.setRole(Role.DEALER);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setCity(request.city());
        user.setName(request.name());
        userRepository.save(user);
        Dealer d=new Dealer();
        d.setUser(user);
        d.setShowroomName(request.showroomName());
        d.setCity(request.city());
        dealerRepository.save(d);
        return new DealerResponse(d.getId(),user.getId(),user.getName(),user.getEmail(),d.getShowroomName(),d.getCity());
    }
}
