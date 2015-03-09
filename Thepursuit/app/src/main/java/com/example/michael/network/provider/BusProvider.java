package com.example.michael.network.provider;

import com.squareup.otto.Bus;

/**
 * Created by christofer on 2/23/15.
 */
public class BusProvider {

    private static final Bus BUS = new Bus();

    private BusProvider(){}

    public static Bus getBus(){return BUS;}

}
