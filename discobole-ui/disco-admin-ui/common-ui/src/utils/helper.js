// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

import dayjs from './dayjs-setup';

export function formatDateTimeValue(dateTime) {
    const date = new Date(dateTime);

    let year = date.getUTCFullYear();
    let month = String(date.getUTCMonth() + 1).padStart(2, '0');
    let day = String(date.getUTCDate()).padStart(2, '0');
    let hour = date.getUTCHours() + 1; // Increment hour by 1
    const minute = String(date.getUTCMinutes()).padStart(2, '0');
    const second = String(date.getUTCSeconds()).padStart(2, '0');

    if (hour === 24) {
        hour = 0;
        const nextDay = new Date(date);
        nextDay.setUTCDate(date.getUTCDate() + 1);
        day = String(nextDay.getUTCDate()).padStart(2, '0');
        month = String(nextDay.getUTCMonth() + 1).padStart(2, '0');
        year = nextDay.getUTCFullYear();
    }

    const formattedHour = String(hour).padStart(2, '0');
    return `${year}-${month}-${day}T${formattedHour}:${minute}:${second}Z`;
}

export function formatISODateTime(dateTime) {
    const date = new Date(dateTime);
    return date.toISOString();
}

export function formatDateWithTimeBoundary(dateString, filterType) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const isoDate = `${year}-${month}-${day}`;

    const boundary = filterType.split('.').pop();

    if (boundary === 'gte') {

        return isoDate + 'T00:00:00Z';
    } else if (boundary === 'lte') {
        return isoDate + 'T23:59:59Z';
    } else {
        return isoDate;
    }
}

export function formatToLocalDateTime(inputDate, {showSeconds = false} = {}) {
    if (!inputDate) return '';

    const date = dayjs(inputDate);
    if (!date.isValid()) return '';

    const format = showSeconds ? 'DD/MM/YYYY HH:mm:ss' : 'DD/MM/YYYY HH:mm';
    return date.tz(dayjs.tz.guess()).format(format);
}