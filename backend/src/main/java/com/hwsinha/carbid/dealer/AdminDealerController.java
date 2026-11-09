package com.hwsinha.carbid.dealer;

import com.hwsinha.carbid.dealer.dto.CreateDealerRequest;
import com.hwsinha.carbid.dealer.dto.DealerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin", description = "Admin specific endpoints")
public class AdminDealerController {
    private final DealerService dealerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a dealer account (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dealer created"),
            @ApiResponse(responseCode = "409", description = "Email is already registered with another account")
    })
    public ResponseEntity<DealerResponse> createDealer(
            @Valid @RequestBody CreateDealerRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(dealerService.createDealer(request));
    }
}
