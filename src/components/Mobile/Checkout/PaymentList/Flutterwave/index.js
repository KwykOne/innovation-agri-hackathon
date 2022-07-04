import React, { Component } from "react";
import { FlutterWaveButton, closePaymentModal } from "flutterwave-react-v3";

class FlutterWave extends Component {
	render() {
		const { total, user, placeOrder, reset } = this.props;
		const config = {
			public_key: localStorage.getItem("flutterwavePublicKey"),
			tx_ref: Date.now(),
			amount: total,
			currency: localStorage.getItem("currencyId"),
			payment_options: "card,mobilemoney,ussd",
			customer: {
				email: user.data.email,
				phonenumber: user.data.phone,
				name: user.data.name,
			},
			customizations: {
				logo: window.location.origin + "/assets/img/favicons/" + localStorage.getItem("favicon-96x96"),
			},
		};

		const fwConfig = {
			...config,
			text: "Pay with Flutterwave",
			className: "flutterwave-btn",
			callback: (response) => {
				console.log(response);
				if (response.status === "successful") {
					placeOrder("", "FLUTTERWAVE");
				}

				closePaymentModal();
			},
			onClose: () => {
				console.log("flutterwave closed.");
				reset();
			},
		};
		return (
			<React.Fragment>
				<FlutterWaveButton {...fwConfig} />
			</React.Fragment>
		);
	}
}

export default FlutterWave;
