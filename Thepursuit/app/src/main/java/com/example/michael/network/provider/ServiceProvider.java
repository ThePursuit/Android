package com.example.michael.network.provider;

import com.example.michael.network.service.GameService;

public class ServiceProvider {

    public static GameService getPositionService(){
        return new GameService(BusProvider.getBus());
    }
}
