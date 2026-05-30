---
title: Exporting
summary: Describes the Exporting feature of the Product Inventory Admin Portal
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

# Exporting Products

Through the **Exporting > Products** screen, the administrator is able to select and export products from the Product Inventory database.

![Admin portal exporting](../img/frontend/ui-exporting-product.png){.img-zoomable}

The selection of products to export is based on the following criteria.

| Filter | Description |
| :----- | :---------- |
| `Content Type` | `json` to export products in `JSON` format — suitable for system integrations<br> `csv` to export products in `CSV` format — suitable for spreadsheet tools |
| `Status` | Select one status at a time: <span class="badge badge-created">Created</span> <span class="badge badge-active">Active</span> <span class="badge badge-cancelled">Cancelled</span> <span class="badge badge-terminated">Terminated</span> <span class="badge badge-sold">Sold</span> <span class="badge badge-aborted">Aborted</span> |
| `Party ID` | The end customer identifier |
| `Product Start Date` | Filter by start date within a timeframe |
| `Product Creation Date` | Filter by creation date within a timeframe |

!!! info "Exporting Products by Status"
    Only one `Status` value can be selected at a time. To export products of multiple statuses, run the export multiple times with different status selections.
