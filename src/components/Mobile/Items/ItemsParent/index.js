import React, { Component } from "react";
import { connect } from "react-redux";
import Desktop from "../../../Desktop";
import Items from "../../Items";
import {
	getRestaurantInfo,
	getRestaurantItems,
} from "../../../../services/items/actions";

class ItemsParent extends Component {
	componentDidMount() {
		console.log("REST",this.props.match.params.restaurant)
		this.props.getRestaurantInfo(this.props.match.params.restaurant);
		
		this.props.getRestaurantItems(this.props.match.params.restaurant);
					
	}
	render() {
		return (
			<React.Fragment>
				{window.innerWidth >= 768 ? (
					<Desktop restaurant={this.props.match.params.restaurant} />
				) : (
					this.props.restaurant_items.hasOwnProperty("items") &&
					<Items restaurant={this.props.match.params.restaurant} history={this.props.history} />
				)}
			</React.Fragment>
		);
	}
}
const mapStateToProps = (state) => ({
	restaurant_info: state.items.restaurant_info,
	restaurant_items: state.items.restaurant_items,
});

export default connect(
	mapStateToProps,
	{
		getRestaurantInfo,
		getRestaurantItems,
	}
)(ItemsParent);
