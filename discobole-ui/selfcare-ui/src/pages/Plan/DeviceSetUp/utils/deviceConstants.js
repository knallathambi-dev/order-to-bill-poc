// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import phoneBlack from "../../../../assests/imgs/devices-colors/phone-black.png";
import phoneGreen from "../../../../assests/imgs/devices-colors/phone-green.png";
import phonePurple from "../../../../assests/imgs/devices-colors/phone-purple.png";
import budsYellow from "../../../../assests/imgs/devices-colors/buds-yellow.jpg";
import budsSilver from "../../../../assests/imgs/devices-colors/buds-silver.jpg";
import VR from "../../../../assests/imgs/VR.jpg";
import defaultImage from "../../../../assests/imgs/Discobole.png";

export const A55caseImages = [
    require("../../../../assests/imgs/A55-case/case1.png"),
    require("../../../../assests/imgs/A55-case/case2.png"),
    require("../../../../assests/imgs/A55-case/case3.png"),
    require("../../../../assests/imgs/A55-case/case4.png"),
];

export const colorVariations = ["colour", "color", "couleur"];

export const deviceImages = {
    phone: {
        black: phoneBlack,
        green: phoneGreen,
        purple: phonePurple,
    },
    buds: {
        yellow: budsYellow,
        silver: budsSilver,
    },
    VR: VR,
    Default: defaultImage,
};