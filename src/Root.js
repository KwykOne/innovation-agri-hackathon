import React from "react";
import { Offline, Online } from "react-detect-offline";
import PWAPrompt from "react-ios-pwa-prompt";
import { Provider } from "react-redux";
import CheckVersion from "./components/CheckVersion";
import CustomCssProvider from "./components/CustomCssProvider";
import OfflineComponent from "./components/Mobile/OfflineComponent";
import PWAInstallation from "./components/Mobile/PWAInstallation";
import store from "./services/store";
import * as serviceWorker from "./serviceWorker";



const polling = {
	enabled: false,
};

const Root = ({ children, initialState = {} }) => (
	<React.Fragment>
		<Provider store={store(initialState)}>
			<CustomCssProvider />
			<Online polling={polling}>
				{children}
				<img className="cart-empty-img hidden" src="/assets/img/various/offline.png" alt="offline" />
				<CheckVersion />
				<PWAInstallation />
				{localStorage.getItem("enIOSPWAPopup") === "true" && (
					<PWAPrompt
						delay={2500}
						copyTitle={localStorage.getItem("iOSPWAPopupTitle")}
						copyBody={localStorage.getItem("iOSPWAPopupBody")}
						copyShareButtonLabel={localStorage.getItem("iOSPWAPopupShareButtonLabel")}
						copyAddHomeButtonLabel={localStorage.getItem("iOSPWAPopupAddButtonLabel")}
						copyClosePrompt={localStorage.getItem("iOSPWAPopupCloseButtonLabel")}
					/>
				)}
			</Online>
			<Offline polling={polling}>
				<OfflineComponent />
			</Offline>
		</Provider>
	</React.Fragment>
);

serviceWorker.register();

export default Root;
