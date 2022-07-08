/*
 * Copyright 2022 CatenaX
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.catenax.dft.entities.edc.request.asset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

@Service
public class AssetEntryRequestFactory {

    private static final String ASSET_PROP_CONTENT_TYPE = "application/json";
    private static final String ASSET_PROP_NAME_ASPECT = "Serialized Part - Submodel SerialPartTypization";
    private static final String ASSET_PROP_NAME_ASPECT_RELATIONSHIP = "Serialized Part - Submodel AssemblyPartRelationship";
    private static final String ASSET_PROP_DESCRIPTION = "...";
    private static final String ASSET_PROP_VERSION = "1.0.0";
    private static final String NAME = "";
    private static final String TYPE = "HttpData";
    @Value(value = "${dft.apiKeyHeader}")
    private String apiKeyHeader;
    @Value(value = "${dft.apiKey}")
    private String apiKey;
    @Value(value = "${dft.hostname}")
    private String dftHostname;

    public AssetEntryRequest getAspectRelationshipAssetRequest(String shellId, String subModelId, String parentUuid) {
        return buildAsset(shellId, subModelId, ASSET_PROP_NAME_ASPECT_RELATIONSHIP, parentUuid);
    }

    public AssetEntryRequest getAspectAssetRequest(String shellId, String subModelId, String uuid) {
        return buildAsset(shellId, subModelId, ASSET_PROP_NAME_ASPECT, uuid);
    }

    private AssetEntryRequest buildAsset(String shellId, String subModelId, String assetName, String uuid) {
        String assetId = shellId + "-" + subModelId;

        HashMap<String, String> assetProperties = getAssetProperties(assetId, assetName);
        AssetRequest assetRequest = AssetRequest.builder().properties(assetProperties).build();

        String uriString = assetName.equals(ASSET_PROP_NAME_ASPECT) ? getAssetPayloadUrl(uuid) : getAssetRelationshipPayloadUrl(uuid);

        HashMap<String, String> dataAddressProperties = getDataAddressProperties(shellId, subModelId, uriString);
        DataAddressRequest dataAddressRequest = DataAddressRequest.builder().properties(dataAddressProperties).build();

        return AssetEntryRequest.builder()
                .asset(assetRequest)
                .dataAddress(dataAddressRequest)
                .build();
    }

    private HashMap<String, String> getAssetProperties(String assetId, String assetName) {
        HashMap<String, String> assetProperties = new HashMap<>();
        assetProperties.put("asset:prop:id", assetId);
        assetProperties.put("asset:prop:name", assetName);
        assetProperties.put("asset:prop:contenttype", ASSET_PROP_CONTENT_TYPE);
        assetProperties.put("asset:prop:description", ASSET_PROP_DESCRIPTION);
        assetProperties.put("asset:prop:version", ASSET_PROP_VERSION);
        return assetProperties;
    }

    private HashMap<String, String> getDataAddressProperties(String shellId, String subModelId, String endpoint) {
        HashMap<String, String> dataAddressProperties = new HashMap<>();
        dataAddressProperties.put("type", TYPE);
        dataAddressProperties.put("endpoint", String.format(endpoint, shellId, subModelId));
        dataAddressProperties.put("name", NAME);
        dataAddressProperties.put("authKey", apiKeyHeader);
        dataAddressProperties.put("authCode", apiKey);
        return dataAddressProperties;
    }

    private String getAssetPayloadUrl(String uuid) {
        return UriComponentsBuilder
                .fromHttpUrl(dftHostname)
                .path("/api/aspect/")
                .path(uuid)
                .toUriString();
    }

    private String getAssetRelationshipPayloadUrl(String uuid) {
        return UriComponentsBuilder
                .fromHttpUrl(dftHostname)
                .path("/api/aspect/")
                .path(uuid)
                .path("/relationship")
                .toUriString();
    }
}
