import React, { Component } from "react";

class Hero extends Component {
	componentDidMount() {
		if (this.props.itemId || this.props.restaurant) {
			console.log("trigger use app button");
			this.__useApp();
			setTimeout(() => {
				this.forceUpdate();
			}, 2000);
		}
	}

	__useApp = () => {
		if (this.refs.phoneView) {
			this.refs.phoneView.classList.remove("blured");
		}
		if (this.refs.phone) {
			this.refs.phone.classList.add("phone-use");
		}
		localStorage.setItem("useAppClicked", "true");
	};

	
	render() {
		console.log("window.location",window.location)
		return (
			<React.Fragment>
				<div className="container-fluid p-0 main-container-desktop">
					<div className="container">
						<div className="row">
							<div className="col-md-6 mt-50">
								<div
									className="col-md-12"
									ref="phoneView"
								>
									<div
										className="phone false phone-use"
										ref="phone"
									>
										<div className="notch" />
										<div className="iframe-wrapper">
											<iframe
												title="appIframe"
												src={window.location}
												frameBorder="0"
												id="appIframe"
											/>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</React.Fragment>
		);
	}
}

export default Hero;
