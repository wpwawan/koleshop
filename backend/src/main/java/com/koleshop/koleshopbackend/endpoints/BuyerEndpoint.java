package com.koleshop.koleshopbackend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.koleshop.koleshopbackend.common.Constants;
import com.koleshop.koleshopbackend.db.models.InventoryCategory;
import com.koleshop.koleshopbackend.db.models.KoleResponse;
import com.koleshop.koleshopbackend.db.models.SellerSettings;
import com.koleshop.koleshopbackend.services.BuyerService;
import com.koleshop.koleshopbackend.services.SessionService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Gundeep on 16/02/16.
 */


@Api(name = "buyerEndpoint",
        version = "v1",
        namespace = @ApiNamespace(ownerDomain = "api.koleshop.com", ownerName = "koleshopserver", packagePath = ""))
public class BuyerEndpoint {

    private static final Logger logger = Logger.getLogger(BuyerEndpoint.class.getName());

    @ApiMethod(name = "getNearbyShops", httpMethod = ApiMethod.HttpMethod.POST)
    public KoleResponse getNearbyShops(@Nullable @Named("customerId") Long customerId, @Nullable @Named("sessionId") String sessionId,
                                       @Named("gpsLong") Double gpsLong, @Named("gpsLat") Double gpsLat, @Named("homeDeliveryOnly") boolean homeDeliveryOnly,
                                       @Named("openShopsOnly") boolean openShopsOnly, @Named("limit") int limit, @Named("offset") int offset) {

        KoleResponse response = new KoleResponse();
        List<SellerSettings> listOfNearbyShops = null;
        try {
            listOfNearbyShops = new BuyerService().getNearbyShops(customerId, gpsLat, gpsLong, homeDeliveryOnly, openShopsOnly, limit, offset);
        } catch (Exception e) {
            response.setData(e.getLocalizedMessage());
        }
        if (listOfNearbyShops != null) {
            response.setSuccess(true);
            response.setData(listOfNearbyShops);
        } else {
            response.setSuccess(false);
        }
        return response;
    }

}