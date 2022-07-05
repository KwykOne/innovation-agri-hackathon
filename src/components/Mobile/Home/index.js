import React, { Component } from "react";
import { connect } from "react-redux";
import { Redirect } from "react-router";
import { Link } from "react-router-dom";
import { getUserNotifications } from "../../../services/alert/actions";
import { resetBackup, resetInfo, resetItems } from "../../../services/items/actions";
import { getSingleLanguageData } from "../../../services/languages/actions";
import { saveNotificationToken } from "../../../services/notification/actions";
import { getPromoSlides } from "../../../services/promoSlider/actions";
import RestaurantList from "./RestaurantList";


// import moment from "moment";

class Home extends Component {
	static contextTypes = {
		router: () => null,
	};

	async componentDidMount() {
		this.props.resetItems();
		this.props.resetInfo();
		this.props.resetBackup();

		// this.props.getPromoSlides();
	}

	componentWillReceiveProps(nextProps) {
		if (this.props.languages !== nextProps.languages) {
			if (localStorage.getItem("userPreferedLanguage")) {
				this.props.getSingleLanguageData(localStorage.getItem("userPreferedLanguage"));
			} else {
				if (nextProps.languages.length) {
					// console.log("Fetching Translation Data...");
					const id = nextProps.languages.filter((lang) => lang.is_default === 1)[0].id;
					this.props.getSingleLanguageData(id);
				}
			}
		}
	}

	componentWillUnmount() {
		// navigator.serviceWorker.removeEventListener("message", message => console.log(message));
	}

	render() {
		if (window.innerWidth > 768) {
			return <Redirect to="/" />;
		}

		return (
			<React.Fragment>
				<div className="height-100-percent bg-white mb-50">
					{localStorage.getItem("mockSearchOnHomepage") === "true" && (
						<div className="mock-search-block pt-3"  style={{ position: "relative" }}>
						<Link to="explore">
							<div
								className="mock-search-block px-15 pb-10 pt-15"
							>
								<div className="px-15 d-flex justify-content-between">
									<div>
										<span>{localStorage.getItem("mockSearchPlaceholder")}</span>
									</div>
									<div>
										<i className="si si-magnifier" />
									</div>
								</div>
							</div>
						</Link>
						</div>
					)}
					{/* Passing slides as props to PromoSlider */}
					

					{localStorage.getItem("customHomeMessage") !== "<p><br></p>" &&
						localStorage.getItem("customHomeMessage") !== "null" &&
						(localStorage.getItem("customHomeMessage") !== "" && (
							<div
								style={{
									position: "relative",
									background: "#f8f9fa",
								}}
								dangerouslySetInnerHTML={{
									__html: localStorage.getItem("customHomeMessage"),
								}}
							/>
						))}
					<RestaurantList />
					{/* <CategoryList user={user} slides={promo_slides.mainSlides}  otherSlides={promo_slides.otherSlides} otherSecondSlides={promo_slides.otherSecondSlides}/>
					<Footer active_nearme={true} /> */}
				</div>
			</React.Fragment>
		);
	}
}

const mapStateToProps = (state) => ({
	promo_slides: state.promo_slides.promo_slides,
	user: state.user.user,
	locations: state.locations.locations,
	languages: state.languages.languages,
});

export default connect(
	mapStateToProps,
	{
		getPromoSlides,
		saveNotificationToken,
		getSingleLanguageData,
		getUserNotifications,
		resetInfo,
		resetItems,
		resetBackup,
	}
)(Home);
