import React, { Component } from "react";
import Hero from "../Hero";

class V1 extends Component {
	render() {
		return (
			<React.Fragment>
				<Hero restaurant={this.props.restaurant} itemId={this.props.itemId} />
			</React.Fragment>
		);
	}
}

export default V1;
