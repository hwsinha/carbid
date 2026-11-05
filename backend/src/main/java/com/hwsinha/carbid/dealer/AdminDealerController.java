package com.hwsinha.carbid.dealer;

import com.hwsinha.carbid.dealer.dto.CreateDealerRequest;
import com.hwsinha.carbid.dealer.dto.DealerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dealers")
@RequiredArgsConstructor
public class AdminDealerController {
    private final DealerService dealerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DealerResponse> createDealer(
            @Valid @RequestBody CreateDealerRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(dealerService.createDealer(request));
    }
}
