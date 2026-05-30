// SPDX-FileCopyrightText: 2025 - 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import api from "./ProductInventoryAPI.js";

const MAX_UPLOAD_SIZE = 5 * 1024 * 1024; // 5MB

const triggerDownload = (fileName, urlOrBlob) => {
    const link = document.createElement("a");
    link.download = fileName;
    link.href = urlOrBlob instanceof Blob ? URL.createObjectURL(urlOrBlob) : urlOrBlob;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
};

class ProductInventoryService {

    /* ---- Product Export ---- */

    exportProducts = async (fields, contentType) => {
        const params = new URLSearchParams();

        Object.entries(fields).forEach(([key, value]) => {
            if (value && key !== "contentType") params.set(key, value);
        });
        params.set("contentType", contentType);

        const {blob, fileName} = await api.downloadProductExport(params);
        triggerDownload(fileName, blob);
        return {fileName};
    };

    downloadJobExport = async (id) => {
        const {url} = await api.getExportFileInfo(id);
        triggerDownload("", url);
    };

    uploadFile = (file, url, onProgress = () => {
    }) => {
        if (!file) throw new Error("No file selected.");
        if (file.size > MAX_UPLOAD_SIZE) throw new Error("File size must be below 5MB.");

        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            xhr.open("PUT", url, true);

            xhr.upload.onprogress = (event) => {
                if (event.lengthComputable) {
                    onProgress(Math.round((event.loaded / event.total) * 100));
                }
            };

            xhr.onload = () => {
                if (xhr.status < 200 || xhr.status >= 300) {
                    return reject(new Error(`Upload failed (status ${xhr.status})`));
                }
                onProgress(100);
                if (!xhr.responseText) {
                    return resolve({message: "Upload successful."});
                }
                try {
                    resolve(JSON.parse(xhr.responseText));
                } catch {
                    reject(new Error("Invalid JSON response from server."));
                }
            };

            xhr.onerror = () => reject(new Error("Upload failed."));
            xhr.send(file);
        });
    };
}

export default new ProductInventoryService();