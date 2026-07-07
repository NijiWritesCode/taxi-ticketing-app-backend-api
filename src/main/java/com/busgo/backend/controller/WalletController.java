package com.busgo.backend.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/wallet")
public class WalletController {

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance() {
        return ResponseEntity.ok(Map.of("balance", 45000, "currency", "NGN"));
    }

    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getTransactions() {
        return ResponseEntity.ok(Map.of(
            "transactions", List.of(
                Map.of("id", "txn_abc123", "type", "DEBIT", "title", "Ticket Purchase", "date", "2026-07-10T09:30:00Z", "amount", 25000, "status", "SUCCESS"),
                Map.of("id", "txn_def456", "type", "CREDIT", "title", "Wallet Top-up", "date", "2026-07-09T14:00:00Z", "amount", 50000, "status", "SUCCESS")
            ),
            "page", 1,
            "totalPages", 3,
            "totalItems", 42
        ));
    }

    @PostMapping("/fund")
    public ResponseEntity<Map<String, Object>> fundWallet(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(Map.of(
            "balance", 95000,
            "transaction", Map.of("id", "txn_ghi789", "type", "CREDIT", "title", "Wallet Top-up", "date", "2026-07-10T15:30:00Z", "amount", request.get("amount"), "status", "SUCCESS")
        ));
    }
}
