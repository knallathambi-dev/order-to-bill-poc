// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.controller;

import com.orange.disco.admin.Channel;
import com.orange.discobole.productcatalog.administration.service.ChannelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Channel", description = "Channels supported by the front ends.")
@Validated
public class ChannelController {
    private final ChannelService channelService;

    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }


    @PostMapping("/channel")
    public ResponseEntity<Channel> createChannel(@RequestBody(required = true) @Valid Channel channel) {
        return ResponseEntity.ok().body(channelService.createChannel(channel));
    }

    @GetMapping("/channel")
    public ResponseEntity <List< Channel >> getAllChannel() {
        return ResponseEntity.ok().body(channelService.getAllChannel());
    }

    @GetMapping("/channel/{id}")
    public ResponseEntity < Channel > getChannelById(@PathVariable String id) {
        return ResponseEntity.ok().body(channelService.getChannelById(id));
    }

    @PutMapping("/channel/{id}")
    public ResponseEntity < Channel > updateChannel(@PathVariable String id, @RequestBody Channel channel) {
        channel.setId(id);
        return ResponseEntity.ok().body(this.channelService.updateChannel(channel));
    }



    @DeleteMapping("/channel/{id}")
    public HttpStatus deleteUnit(@PathVariable String id) {
        this.channelService.deleteUnit(id);
        return HttpStatus.OK;
    }

}
