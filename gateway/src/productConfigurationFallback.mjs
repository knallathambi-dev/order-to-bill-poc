import crypto from "crypto";

const configurations = new Map();

// Local POC fallback for Selfcare's /v1/queryProductConfiguration path.
const findRequestedOffering = (body = {}) => {
    const items = body.requestProductConfigurationItem || [];
    const firstConfig = items[0]?.productConfiguration || {};
    return firstConfig.productOffering || firstConfig.product || {};
};

const getStoredProductConfiguration = (configuration) => (
    configuration?.requestProductConfigurationItem?.[0]?.productConfiguration
    || configuration?.computedProductConfigurationItem?.[0]?.productConfiguration
    || {}
);

export const buildProductConfiguration = (body = {}) => {
    const existingConfiguration = body.id ? configurations.get(body.id) : null;
    const storedProductConfiguration = getStoredProductConfiguration(existingConfiguration);
    const requested = findRequestedOffering(body);
    const storedOffering = storedProductConfiguration.productOffering || storedProductConfiguration.product || {};
    const configurationId = body.id || crypto.randomUUID();
    const itemId = body.requestProductConfigurationItem?.[0]?.id || `${configurationId}-fiber-broadband`;
    const incomingCharacteristics = body.requestProductConfigurationItem?.[0]?.productConfiguration?.configurationCharacteristic;
    const incomingActions = body.requestProductConfigurationItem?.[0]?.productConfiguration?.configurationAction;
    const configurationActions = incomingActions || storedProductConfiguration.configurationAction || [{action: "add", isSelected: true, "@type": "ConfigurationAction"}];
    const productOffering = {
        id: requested.id || storedOffering.id || "phase8-fiber-broadband-300",
        name: requested.name || storedOffering.name || "Fiber Broadband 300 Mbps",
        "@type": requested["@type"] || storedOffering["@type"] || "ProductOfferingRef",
        "@referredType": requested["@referredType"] || storedOffering["@referredType"] || "Contract",
    };
    const requestedProductConfiguration = {
        productOffering,
        configurationAction: configurationActions,
        "@type": "ProductConfiguration",
    };

    return {
        id: configurationId,
        state: "done",
        channel: body.channel || [{id: "Selfcare", name: "Selfcare"}],
        relatedParty: body.relatedParty || [],
        requestProductConfigurationItem: [
            {
                id: itemId,
                "@type": "QueryProductConfigurationItem",
                productConfiguration: requestedProductConfiguration,
            },
        ],
        computedProductConfigurationItem: [
            {
                id: itemId,
                "@type": "TargetQueryProductConfigurationItem",
                productConfiguration: {
                    id: `${itemId}-configuration`,
                    isVisible: true,
                    isSelected: true,
                    productOffering: requestedProductConfiguration.productOffering,
                    configurationAction: requestedProductConfiguration.configurationAction,
                    configurationCharacteristic: incomingCharacteristics || [
                        {
                            id: "installation-address",
                            name: "Installation address",
                            isConfigurable: true,
                            "@type": "AddressCharacteristic",
                            configurationCharacteristicValues: [],
                        },
                    ],
                    configurationPrice: [],
                    "@type": "ProductConfiguration",
                },
                productConfigurationItemRelationship: [],
            },
        ],
        "@type": "QueryProductConfiguration",
    };
};

export const postProductConfigurationFallback = (req, res) => {
    const configuration = buildProductConfiguration(req.body);
    configurations.set(configuration.id, configuration);
    res.json(configuration);
};

export const getProductConfigurationFallback = (req, res) => {
    const configuration = configurations.get(req.params.id);
    if (!configuration) return res.status(404).json({error: "Configuration not found"});
    res.json(configuration);
};
