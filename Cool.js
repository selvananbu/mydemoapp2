import React, { Component, PropTypes } from 'react';
import Dimensions from 'Dimensions';
import {
	StyleSheet,
	TouchableOpacity,
	Text,
	Animated,
	Easing,
	Image,
	Alert,
	View,
} from 'react-native';
import Wallpaper from './Wallpaper';
// import { Actions, ActionConst } from 'react-native-router-flux';

import spinner from './Lilogo.png';

// var customData = require('../../../profile.json');

const DEVICE_WIDTH = Dimensions.get('window').width;
const DEVICE_HEIGHT = Dimensions.get('window').height;
const MARGIN = 140;

export default class Cool extends Component {
	constructor() {
		super();

		this.state = {
			isLoading: false,
		};

		this.buttonAnimated = new Animated.Value(0);
		this.growAnimated = new Animated.Value(0);
		this._onPress = this._onPress.bind(this);
	}

	_onPress() {
		if (this.state.isLoading) return;

		this.setState({ isLoading: true });
		Animated.timing(
			this.buttonAnimated,
			{
				toValue: 1,
				duration: 1000,     //button shrinking
				easing: Easing.linear
			}
		).start();

		setTimeout(() => {
			this._onGrow();
		}, 1400); 					//Do not set more than Opening timeout()

		setTimeout(() => {
			// Actions.drawerScreen();                         //Navigation after login
			this.setState({ isLoading: false });
			this.buttonAnimated.setValue(0);
			this.growAnimated.setValue(0);
		}, 1900);					// Do not set below ._onGrow()
	}

	_onGrow() {
		Animated.timing(
			this.growAnimated,
			{
				toValue: 1,
				duration: 1000,
				// easing: Easing.linear
			}
		).start();
	}

	render() {
		const changeWidth = this.buttonAnimated.interpolate({
	    inputRange: [0, 1],
	    outputRange: [DEVICE_WIDTH - MARGIN, MARGIN]
	  });
	  const changeScale = this.growAnimated.interpolate({
	    inputRange: [0, 1],
	    outputRange: [1, MARGIN]
	  });

		return (
            <Wallpaper>
			<View style={styles.container}>
				<Animated.View style={{width: changeWidth}}>
					<TouchableOpacity style={styles.button}
						onPress={this._onPress}
						activeOpacity={1} >
							{this.state.isLoading ?
								<Image source={spinner} style={styles.image} />
								:
								<Text style={styles.text}>WELCOME EEasdasd!</Text>
							}
					</TouchableOpacity>
					<Animated.View style={[ styles.circle, {transform: [{scale: changeScale}]} ]} />
				</Animated.View>
			</View>
            </Wallpaper>
		);
	}
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		top: 20,
		alignItems: 'center',
        justifyContent: 'center',
        backgroundColor:'rgba(134,27,76,0.7)'
	},
	button: {
		alignItems: 'center',
		justifyContent: 'center',
		backgroundColor: '#000',
		height: MARGIN,
		borderRadius: 82,
		zIndex: 300,

	},
	circle: {
		height: MARGIN,
		width: MARGIN,
		marginTop: -MARGIN,
		borderWidth: 1,
		borderColor: '#881B4C',
		borderRadius: 100,
		alignSelf: 'center',
		zIndex: 99,
		backgroundColor: '#881B4C',
	},
	text: {
		color: '#fff',
        backgroundColor: 'transparent',
        fontSize: 22
	},
	image: {
		width:244,
		height: 44,
	},
});