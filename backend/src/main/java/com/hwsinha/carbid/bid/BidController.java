package com.hwsinha.carbid.bid;

import com.hwsinha.carbid.bid.dto.BidResponse;
import com.hwsinha.carbid.bid.dto.PlaceBidRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class BidController {
    private final BidService bidService;

    @PostMapping("/{groupId}/bids")
    public ResponseEntity<BidResponse> placeBid(
            @PathVariable Long groupId,
            @Valid @RequestBody PlaceBidRequest request) {
        BidResponse body=bidService.placeBid(groupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
