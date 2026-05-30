---
title: Reporting
summary: Describes the Reporting features of the Product Inventory Admin Portal
author:
  - Mohamed Amine Macherki
---

<style>
 .badge {
  display: inline-block;
  padding: 1px 6px;
  border-radius: 10px;
  font-size: 0.75em;
  font-weight: bold;
  color: white;
  margin: 1px 1px;
  white-space: nowrap;
}
  .badge-created   { background-color: #707C83; }
  .badge-active    { background-color: #67CB67; }
  .badge-cancelled { background-color: #FD6161; }
  .badge-terminated{ background-color: #FD6161; }
  .badge-aborted   { background-color: #FD6161; }
  .badge-sold      { background-color: #527EDB; }
</style>

# Reporting

The **Reporting** menu enables an overview of the contract products in the Product Inventory database.

Reporting can be performed on a **Daily** basis or for a specific time  **Period**.

## Daily Reporting

It is possible to generate a daily report based on either the product statuses or the product offer. Depending on the selected **Report Type**, the available input fields adapt dynamically.

### Daily Report By Status

When selecting **Report By Status**, the interface requires only the **Date** to display the number of products per status (e.g., `Created`, `Active`, `Cancelled`, `Terminated`, `Sold`, `Aborted`).

![fields daily report by status](../img/frontend/ui-reporting-daily-report-typeof-status.png){.img-zoomable}

As result, the reporting chart display the number products segregated by status.

![Admin portal reporting daily](../img/frontend/ui-reporting-daily-report-by-status.png){.img-zoomable}

### Daily Report by Product Offer

!!! info "In Progress"
    Currently, generating the report by Product Offer is not fully functional. However, the interface correctly updates to dynamically request the specific fields below.

When selecting **Report By Product Offer**, additional filtering criteria appear in the interface.

![fields daily report by product offer](../img/frontend/ui-reporting-daily-report-typeof-product-offering.png){.img-zoomable}

The selection of products to export is based on the following criteria.

| Filter | Description |
| :----- | :---------- |
| `Date` | |
| `Product Status` | Select one status at a time: <span class="badge badge-created">Created</span> <span class="badge badge-active">Active</span> <span class="badge badge-cancelled">Cancelled</span> <span class="badge badge-terminated">Terminated</span> <span class="badge badge-sold">Sold</span> <span class="badge badge-aborted">Aborted</span> |
| `Product Offering Type` | |
| `Product Offerings` | |

## Periodic Reporting

This report displays the number of products during a period of time. Similar to Daily Reporting, the input fields adapt based on the selected **Report Type**.

### Periodic Report By Status

When selecting **Report By Status**, the interface requires a date range:

- **From Date**: calendar start date of the report
- **To Date**: calendar end date of the report

![fields periodic report by status](../img/frontend/ui-reporting-periodic-report-typeof-status.png){.img-zoomable}

The generated report shows a graph representing the number of products per status on the given time period.

![Admin portal periodic reporting by status](../img/frontend/ui-reporting-periodic-report-by-status.png){.img-zoomable}

### Periodic Report By Product Offering

!!! info "In Progress"
    Similar to the daily report, generation for this report type is currently not functional, but the fields adapt as expected.

When selecting **Report By Product Offering**, additional filtering criteria are revealed.

![fields periodic report by product offering](../img/frontend/ui-reporting-periodic-report-typeof-product-offering.png){.img-zoomable}

The generation of periodic reports is based on the following criteria.

| Filter | Description |
| :----- | :---------- |
| `From Date` | Calendar start date |
| `To Date` | Calendar start date |
| `Product Status` | Select one status at a time: <span class="badge badge-created">Created</span> <span class="badge badge-active">Active</span> <span class="badge badge-cancelled">Cancelled</span> <span class="badge badge-terminated">Terminated</span> <span class="badge badge-sold">Sold</span> <span class="badge badge-aborted">Aborted</span> |
| `Product Offering Type` | |
| `Product Offering` | |
