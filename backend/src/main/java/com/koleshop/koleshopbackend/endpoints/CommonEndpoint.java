package com.koleshop.koleshopbackend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.koleshop.koleshopbackend.db.models.Brand;
import com.koleshop.koleshopbackend.db.models.ImageUploadRequest;
import com.koleshop.koleshopbackend.db.models.KoleResponse;
import com.koleshop.koleshopbackend.services.KoleshopCloudStorageService;
import com.koleshop.koleshopbackend.services.ProductService;
import com.koleshop.koleshopbackend.services.SessionService;
import com.koleshop.koleshopbackend.db.models.ParentProductCategory;
import com.koleshop.koleshopbackend.db.models.ProductCategory;
import com.koleshop.koleshopbackend.db.models.deprecated.ProductVarietyAttributeMeasuringUnit;
import com.koleshop.koleshopbackend.db.models.RestCallResponse;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Gundeep on 30/05/15.
 */

@Api(name = "commonEndpoint", version = "v1", namespace = @ApiNamespace(ownerDomain = "api.koleshop.com", ownerName = "koleshopserver", packagePath = ""))
public class CommonEndpoint {

    private static final Logger logger = Logger.getLogger(CommonEndpoint.class.getName());

    @ApiMethod(name = "getProductCategories")
    public List<ParentProductCategory> getProductCategories() {
        return new ProductService().getProductCategories();
    }

    @ApiMethod(name = "storeImage")
    public RestCallResponse storeImage() {
        return null;
    }

    @ApiMethod(name = "getAllProductCategories")
    public List<ProductCategory> getAllProductCategories(@Named("userId") Long userId, @Named("sessionId") String sessionId) {
        try {
            if (SessionService.verifyUserAuthenticity(userId, sessionId)) {
                return new ProductService().getAllProductCategories();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @ApiMethod(name = "getAllBrands")
    public List<Brand> getAllBrands(@Named("userId") Long userId, @Named("sessionId") String sessionId) {
        try {
            if (SessionService.verifyUserAuthenticity(userId, sessionId)) {
                return new ProductService().getAllBrands();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    @ApiMethod(name = "uploadImage", httpMethod = ApiMethod.HttpMethod.POST)
    public KoleResponse uploadImage(@Named("userId") Long userId, @Named("sessionId") String sessionId, ImageUploadRequest imageUploadRequest) {
        KoleResponse koleResponse = new KoleResponse();
        try {
            if (SessionService.verifyUserAuthenticity(userId, sessionId)) {
                boolean imageUploaded = KoleshopCloudStorageService.uploadImage(imageUploadRequest);
                if(imageUploaded) {
                    koleResponse.setSuccess(true);
                    koleResponse.setData("image uploaded");
                } else {
                    koleResponse.setSuccess(false);
                    koleResponse.setData(null);
                }
            } else {
                koleResponse.setSuccess(false);
                koleResponse.setData(null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "problem while uploading image", e);
            koleResponse.setSuccess(false);
            koleResponse.setData(null);
        }
        return koleResponse;
    }

    @Deprecated
    @ApiMethod(name = "getMeasuringUnits")
    public List<ProductVarietyAttributeMeasuringUnit> getMeasuringUnits(@Named("userId") Long userId, @Named("sessionId") String sessionId) {
        try {
            if (SessionService.verifyUserAuthenticity(userId, sessionId)) {
                return new ProductService().getMeasuringUnits();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

}