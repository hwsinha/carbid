package com.hwsinha.carbid.bid;

import com.hwsinha.carbid.bid.dto.BidResponse;
import com.hwsinha.carbid.bid.dto.PlaceBidRequest;
import com.hwsinha.carbid.dealer.Dealer;
import com.hwsinha.carbid.dealer.DealerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class BidController {
    private final BidService bidService;
    private final DealerRepository dealerRepository;

    @PostMapping("/{groupId}/bids")
    @PreAuthorize("hasRole('DEALER')")
    @Operation(summary = "Place a bid on a group (dealers only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bid placed"),
            @ApiResponse(responseCode = "403", description = "Not a dealer, or dealer is outside the group's city"),
            @ApiResponse(responseCode = "409", description = "Group not accepting bids, or bid not lower than the current lowest")
    })
    public ResponseEntity<BidResponse> placeBid(
            @PathVariable Long groupId,
            @Parameter(hidden=true) @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PlaceBidRequest request) {
        Long userId= Long.valueOf(jwt.getSubject());
        Dealer dealer=dealerRepository.findByUserId(userId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN,"No dealer profile for this user."));
        BidResponse body=bidService.placeBid(groupId, request, dealer);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
