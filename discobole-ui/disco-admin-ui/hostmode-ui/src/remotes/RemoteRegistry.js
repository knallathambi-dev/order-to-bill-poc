// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

const remoteRegistry = new Map();

export async function getRemote(remoteName) {
    if (remoteRegistry.has(remoteName)) {
        return remoteRegistry.get(remoteName);
    }

    let mod = null;

    switch (remoteName) {
        case "order_inventory":
            mod = await import("order_inventory/App");
            break;
        case "product_inventory":
            mod = await import("product_inventory/App");
            break;
        case "order_orchestration":
            mod = await import("order_orchestration/App");
            break;
        default:
            throw new Error(`Unknown remote: ${remoteName}`);
    }

    const RemoteApp = mod.default || mod;
    remoteRegistry.set(remoteName, RemoteApp);
    return RemoteApp;
}

export function clearRemote(remoteName) {
    remoteRegistry.delete(remoteName);
}