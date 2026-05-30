// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import React, {useEffect, useState} from "react";
import {MapContainer, Marker, Popup, TileLayer, useMap, useMapEvents} from "react-leaflet";
import L from "leaflet";
import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";
import useTranslations from "../../utlis/i18n/useTranslations";

L.Icon.Default.mergeOptions({
    iconRetinaUrl: markerIcon2x,
    iconUrl: markerIcon,
    shadowUrl: markerShadow,
});

// Helper to build Nominatim URL
function buildNominatimUrl(location) {
    const baseUrl = "https://nominatim.openstreetmap.org/search";
    const params = new URLSearchParams({
        format: "json",
        q: location,
        limit: "1",
        addressdetails: "1",
        "accept-language": "en"
    });
    return `${baseUrl}?${params.toString()}`;
}

async function fetchCoordinates(location) {
    if (!location) return null;
    const url = buildNominatimUrl(location);
    try {
        const response = await fetch(url);
        const data = await response.json();
        if (data && data.length > 0) {
            return {
                lat: parseFloat(data[0].lat),
                lng: parseFloat(data[0].lon),
                bounds: data[0].boundingbox
                    ? data[0].boundingbox.map(coord => parseFloat(coord))
                    : null,
            };
        }
    } catch (error) {
        console.error("Nominatim error:", error);
    }
    return null;
}

function RecenterMap({lat, lng}) {  // Remove zoom prop
    const map = useMap();
    useEffect(() => {
        // Keep current zoom level when updating position
        map.setView([lat, lng], map.getZoom());
    }, [lat, lng, map]);
    return null;
}


function FitBoundsMap({bounds}) {
    const map = useMap();
    useEffect(() => {
        if (bounds) {
            // Leaflet expects bounds as [[south, west], [north, east]]
            map.fitBounds(bounds);
        }
    }, [map, bounds]);
    return null;
}

function MapClickHandler({handleMapClick}) {
    useMapEvents({
        click(e) {
            handleMapClick(e.latlng);
        },
    });
    return null;
}

const MyMap = ({location, overrideCoords, hasLocated = false, onMapClick}) => {
    const {t} = useTranslations();
    const [coords, setCoords] = useState({
        lat: 46.603354,
        lng: 1.8883335,
        bounds: null
    });
    const [mapStyle, setMapStyle] = useState("basic");
    const [isManualPosition, setIsManualPosition] = useState(false);
    const handleMapClick = (latlng) => {
        setCoords({
            lat: latlng.lat,
            lng: latlng.lng,
            bounds: null
        });
        setIsManualPosition(true);
    };

    useEffect(() => {
        if (hasLocated && overrideCoords && overrideCoords.lat && overrideCoords.lng) {
            setCoords({lat: overrideCoords.lat, lng: overrideCoords.lng, bounds: null});
            setIsManualPosition(true);
        } else if (location && !isManualPosition) {
            (async () => {
                const result = await fetchCoordinates(location);
                if (result) {
                    setCoords(result);
                }
            })();
        }
    }, [location, hasLocated, overrideCoords, isManualPosition]);

    // Reset manual position when location changes
    useEffect(() => {
        setIsManualPosition(false);
    }, [location]);
    const getTileUrl = () => {
        switch (mapStyle) {
            case "basic":
                return "https://api.maptiler.com/maps/streets-v2/256/{z}/{x}/{y}@2x.png?key=phpgbsKKuduEshTTJopS";
            case "satellite":
                return "https://api.maptiler.com/maps/satellite/{z}/{x}/{y}.jpg?key=phpgbsKKuduEshTTJopS";
            default:
                return "https://api.maptiler.com/maps/streets-v2/256/{z}/{x}/{y}@2x.png?key=phpgbsKKuduEshTTJopS";
        }
    };

    const zoomLevel = hasLocated ? 14 : (coords.bounds && location && location !== "France" ? undefined : 6);

    return (
        <div style={{height: "435px", position: "relative"}}>
            {/* Map style switching buttons */}
            <div style={{position: "absolute", top: 10, right: 10, zIndex: 1000}}>
                <button
                    className={`btn ${mapStyle === "basic" ? "btn-primary" : "btn-primary active"}`}
                    onClick={() => setMapStyle("basic")}
                >
                    {t("eligibility.types.basic")}
                </button>
                <button
                    className={`btn ${mapStyle === "satellite" ? "btn-primary" : "btn-primary active"}`}
                    onClick={() => setMapStyle("satellite")}
                    style={{marginLeft: "5px"}}
                >
                    {t("eligibility.types.satellite")}
                </button>
            </div>
            <MapContainer
                center={[coords.lat, coords.lng]}
                zoom={zoomLevel || 6}
                style={{height: "100%", width: "100%"}}
                scrollWheelZoom={true}
            >
                <MapClickHandler handleMapClick={onMapClick}/>

                {hasLocated ? (
                    // For "Locate me" keep forced zoom 14
                    <RecenterMap lat={coords.lat} lng={coords.lng}/>
                ) : coords.bounds && location && location !== "France" ? (
                    <FitBoundsMap
                        bounds={[
                            [coords.bounds[0], coords.bounds[2]],
                            [coords.bounds[1], coords.bounds[3]],
                        ]}
                    />
                ) : (
                    // For manual positioning use current zoom
                    <RecenterMap lat={coords.lat} lng={coords.lng}/>
                )}
                <TileLayer url={getTileUrl()}/>
                <Marker position={[coords.lat, coords.lng]}>
                    <Popup>
                        {isManualPosition ?
                            `${coords.lat.toFixed(4)}, ${coords.lng.toFixed(4)}` :
                            location}
                    </Popup>
                </Marker>
            </MapContainer>
        </div>
    );
};

export default MyMap;