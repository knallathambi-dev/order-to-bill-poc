---
title: About Catalog Administration
summary: Describes the configuration of Catalog administration data used in Product Catalog
author:
  - Catherine
  - Antoine

---

# Catalog Administration

## Currency

The **Currency** section of the Catalog Administration enables to define a Currency and to get the list of Currencies which can then be used for the **Product Offering Price** definition process.

The **Currency Dashboard** includes a **Search function**, you can use to filter the list of Currencies, and exposes the following Currency properties:

  | Column           | Description                                                                                                                        |
  | :--------------- | :--------------------------------------------------------------------------------------------------------------------------------- |
  | `Label`          | The label of the currency                                                                                                          |
  | `Code`           | The code of the currency                                                                                                           |
  | `IsDefault`      | Toogle button to specify if this currrency is the default one to be considered in the ODACAT Product                               |
  | `Last Modified`  | The date this currency was last modified                                                                                           |
  | `Action`         | The ![icon edit](../img/ui-icon-edit.png) icon to **edit it** and ![icon delete](../img/ui-icon-delete.png) icon to **delete** it. |

## Unit

The **Unit** section of the Catalog Administration enables to define a Unit and to get the list of Units which can used for characteristics.

The **Unit Dashboard** includes a **Search function**, you can use to filter the list of Units, and exposes the following Unit properties:  

  | Column                   | Description                                                                                                                              |
  | :----------------------- | :--------------------------------------------------------------------------------------------------------------------------------------- |
  | `Unit of Measure (UoM)`  | The Unit of Measure of the unit                                                                                                          |
  | `Last Modified`          | The date this unit was last modified                                                                                                     |
  | `Action`                 | Click on  ![icon edit](../img/ui-icon-edit.png) icon to **edit it** and ![icon delete](../img/ui-icon-delete.png) icon to **delete** it. |

## Frequency Type

The **Frequency Type** section of the Catalog Administration enables to define a Frequency type and to get the list of Frequency types which can be used for instance for Recurring period.  

The **Frequency Type Dashboard** includes a **Search function**, you can use to filter the list of Frequency types, and exposes the following Frequency Type properties: 

  | Column          | Description                                                                                                                             |
  | :-------------- | :-------------------------------------------------------------------------------------------------------------------------------------- |
  | `Label`         | The label of the frequency type                                                                                                         |
  | `Code`          | The code of the frequency type                                                                                                          |
  | `Last Modified` | The date this frequency type was last modified                                                                                          |
  | `Action`        | Click on ![icon edit](../img/ui-icon-edit.png) icon to **edit it** and ![icon delete](../img/ui-icon-delete.png) icon to **delete** it. |  

## Channel

The **Channel** section of the Catalog Administration enables to define a Channel and to get the list of Channels which can be used in the definition of the Product Offerings.  

The **Channel Dashboard** includes a **Search function** you can use to filter the list of Channels, and exposes the following Channel properties:

  | Column          | Description                                                                                                                            |
  | :-------------- | :------------------------------------------------------------------------------------------------------------------------------------- |
  | `ID`            | The identifier of the channel                                                                                                          |
  | `Name`          | The name of the channel                                                                                                                |
  | `Last Modified` | The date this channel was last modified                                                                                                |
  | `Action`        | Click on ![icon edit](../img/ui-icon-edit.png) icon to**edit it** and ![icon delete](../img/ui-icon-delete.png) icon to **delete** it. |  

## Market Segment

The **Market Segment** section of the Catalog Administration enables to define a Market Segment and to get the list of Marked Segments which can be used in the definition of the Product Offerings.

The **Market Segment Dashboard** includes a **Search function** you can use to filter the list of Market Segments, and exposes the following Market Segment properties:

  | Column          | Description                                                                                                                             |
  | :-------------- | :-------------------------------------------------------------------------------------------------------------------------------------- |
  | `ID`            | The identifier of the market segment                                                                                                    |
  | `Name`          | The name of the market segment                                                                                                          |
  | `Last Modified` | The date this channel was last modified                                                                                                 |
  | `Action`        | Click on ![icon edit](../img/ui-icon-edit.png) icon to **edit it** and ![icon delete](../img/ui-icon-delete.png) icon to **delete** it. |  

## Additional Settings

### Application Settings

This section aims to configure the behaviour of the Product Catalog:

- **Product Inventory Check**: If enabled, before allowing an offer retirement, Product Catalog will first check Product Inventory (CPIB) if any customer has still a valid instance of this offer.  
- **Product Specification Relationship Check**: If enabled, Product Catalog will restrict the possible relationships between Product Specifications to the inherited ones from CFS specifications for a new Product Specification creation.

### Error Messages

The **Error Messages** section of the Catalog Administration includes a list of Error messages triggered in the application. A **Search function** can be used to filter this list.  
The characteristics of the Error Messages are the following:

  | Column              | Description                                                          |
  | :------------------ | :------------------------------------------------------------------- |
  | `Code`              | The code of the error                                                |
  | `Status`            | The status of the error                                              |
  | `Reason`            | The reason associated to the error                                   |
  | `Message displayed` | The message displayed to the user when the error occurs              |  
  | `Action`            | Click on ![icon edit](../img/ui-icon-edit.png) icon to **edit it**   |  
