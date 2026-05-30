// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.administration.service.impl;

import com.orange.disco.admin.Channel;
import com.orange.discobole.productcatalog.administration.exception.MissingBodyFieldException;
import com.orange.discobole.productcatalog.administration.repository.ChannelRepository;
import com.orange.discobole.productcatalog.administration.service.ChannelService;
import com.orange.discobole.productcatalog.administration.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;

    @Autowired
    public ChannelServiceImpl(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }



    @Override
    public Channel createChannel(Channel channel) {
        if (channel == null) {
            throw new MissingBodyFieldException(23, "Missing Body channel", Constants.ERROR_NOT_FOUND);
        } else if ( channel.getName().isBlank()) {
            throw new MissingBodyFieldException(23, "Name cannot be blank", "Name cannot be blank");
        }

        // Set the ID from the user request if present, else generate a random ID
        if (channel.getId() == null || channel.getId().isBlank()) {
            channel.setId(UUID.randomUUID().toString());
        }


        if (channelRepository.existsById(channel.getId())) {
            throw new MissingBodyFieldException(23, "Duplicate ID: A channel with this ID already exists", "Conflict");
        }

         channel.setLastUpdate(LocalDateTime.now());
        return channelRepository.save(channel);  // Save the channel
    }


    @Override
    public List<Channel> getAllChannel() {
        return this.channelRepository.findAll();
    }

    @Override
    public Channel getChannelById(String channelId) {
        Optional <Channel> channelDb = this.channelRepository.findById(channelId);

        if (channelDb.isPresent()) {
            return channelDb.get();
        }
        else{
            throw new MissingBodyFieldException(60,Constants.ERROR_CHANNEL_NOT_FOUND,Constants.ERROR_NOT_FOUND);
        }
    }

    @Override
    public Channel updateChannel(Channel channel) {
        Optional <Channel> channelDb = this.channelRepository.findById(channel.getId());
        if (channelDb.isPresent()) {
            if (!(channel.getName().isBlank())  ) {
                Channel channelUpdate = channelDb.get();
                channelUpdate.setId(channel.getId());
                channelUpdate.setName(channel.getName());
                channelUpdate.setHref(channel.getHref());
                channelUpdate.setAtBaseType(channel.getAtBaseType());
                channelUpdate.setAtType(channel.getAtType());
                channelUpdate.setLastUpdate(channel.getLastUpdate());
                channelRepository.save(channelUpdate);
                return channelUpdate;
            }
            else {
                throw new MissingBodyFieldException(23,"Missing Body Field ChannelName",Constants.ERROR_NOT_FOUND);

            }
        }
        else {
            throw new MissingBodyFieldException(60,Constants.ERROR_CHANNEL_NOT_FOUND,Constants.ERROR_NOT_FOUND);

        }
    }
    @Override
    public void deleteUnit(String id) {
        if(id.contains(",")){
            List<String> ids = Arrays.asList(id.split(","));
            this.channelRepository.deleteAllById(ids);
        }else{
            Optional < Channel > channelDb = this.channelRepository.findById(id);

            if (channelDb.isPresent()) {
                this.channelRepository.delete(channelDb.get());
            }
            else {

                throw new MissingBodyFieldException(60,"Channel Not Found","Not Found");
            }

        }
    }

}
