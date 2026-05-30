// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import '@testing-library/jest-dom';
import {MemoryRouter} from 'react-router-dom';
import {render} from "@testing-library/react";
import React from "react";

// Mock ResizeObserver (used by charts, tooltips, etc.)
class ResizeObserver {
    observe() {
    }

    unobserve() {
    }

    disconnect() {
    }
}

window.ResizeObserver = ResizeObserver;
global.ResizeObserver = ResizeObserver;

export const renderWithRouter = (component) => {
    return render(<MemoryRouter>{component}</MemoryRouter>);
};