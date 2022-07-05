import React, { Component } from "react";
import Ink from "react-ink";

class WebShare extends Component {
	state = {
		shareButton: false,
	};
	componentDidMount() {
		if (navigator.share) {
			this.setState({ shareButton: true });
		}
	}
	shareLink = (data) => {
		if (navigator.share) {
			navigator
				.share({
					url: data.link,
				})
				.then(() => console.log("Successful share"))
				.catch((error) => console.log("Error sharing", error));
		}
	};

	render() {
		return (
			<React.Fragment>
				{this.state.shareButton && (
					<button
						type="button"
						className="btn search-navs-btns nav-home-btn"
						style={{ position: "relative" }}
						onClick={() => this.shareLink(this.props)}
					>
						<i className="si si-share" />
						<Ink duration="500" />
					</button>
				)}
			</React.Fragment>
		);
	}
}

export default WebShare;
