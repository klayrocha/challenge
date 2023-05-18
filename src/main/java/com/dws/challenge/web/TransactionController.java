package com.dws.challenge.web;

import com.dws.challenge.service.AccountsService;
import com.dws.challenge.vo.TransactionRequestVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/transaction")
@Slf4j
@RequiredArgsConstructor
public class TransactionController {

    private final AccountsService accountsService;

    /**
     * Method responsible for transferring money
     *
     * @param vo Information transaction
     * @return status
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> transfer(@RequestBody @Valid TransactionRequestVo vo) {
        accountsService.transfer(vo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

