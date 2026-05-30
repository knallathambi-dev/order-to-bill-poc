// SPDX-FileCopyrightText: 2026 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

export {default as PageNotFound} from "./static/PageNotFound";
export {default as ErrorHandler} from "./static/ErrorHandler";
export {default as Unauthorized} from "./static/Unauthorized";
export {default as AppLayout} from "./layouts/AppLayout";
export {default as NavBar} from "./components/NavBar";
export {default as Header} from "./components/Header";
export {default as Pagination} from "./components/Pagination";
export {default as ErrorMessageBox} from "./components/ErrorMessageBox";
export {default as useNavigation} from "./hooks/useNavigation";
export {default as useNavigationGuard} from "./hooks/useNavigationGuard";
export * from "./utils/constants";
export {default as dayjs} from "./utils/dayjs-setup";
export * from "./utils/helper";
export * from "./context/SideMenuContext";
export {default as AuthService} from "./service/BffAuthService";
export {default as httpClient} from "./service/httpClient";
export {FederationProvider, useFederationConfig} from "./context/FederationContext";
import "./index.css";
import "boosted/dist/js/boosted.bundle.js";
import "./assets/icomoon/icomoon.css";
import "./assets/icomoon/custom/custom-icomoon.css";
import "react-toastify/dist/ReactToastify.css";

export {MfeLayoutWrapper} from "./components/MfeLayoutWrapper";
export {MonitoringPage} from "./components/MonitoringPage";
export {FilterToolbar} from "./components/FilterToolbar";
export {ReloadButton} from "./components/ReloadButton";
export {
    selectClassNames,
    selectStyles,
    CustomOption,
    CustomMultiValueRemove,
    DropdownIndicator,
} from "./components/SelectComponents.jsx";

export {default as MonitoringTabContent} from "./components/MonitoringTabContent";
export {default as MonitoringTableBody} from "./components/MonitoringTableBody";
export {default as MonitoringFilterForm} from "./components/MonitoringFilterForm";

export {
    ClearFieldButton,
    TextInput,
    DateInput,
    SelectInput,
    MultiSelectInput,
    SwitchInput,
} from "./components/FilterInputs";
export {default as BootstrapTooltip} from "./components/BootstrapTooltip";
export {default as Modal} from "./components/Modal";

export {toast, ToastContainer} from "react-toastify";

// --- Auth ---
export {createAuthContext} from "./context/createAuthContext";

export {default as LoadingIndicator} from "./components/LoadingIndicator";
export {default as StatusPanel} from "./components/StatusPanel";

export {
    getCurrencyFractionDigits,
    getCurrencySymbol,
    formatCurrencyAmount,
    roundCurrencyAmount,
    formatPriceAmount,
} from "./utils/currencyUtils";

export {default as PopoverRow} from "./components/PopoverRow";
export {default as NodePopoverBase} from "./components/NodePopoverBase";
export {default as StatusLegend} from "./components/StatusLegend";