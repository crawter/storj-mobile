import {
    View,
    Text,
    StyleSheet,
    StatusBar
} from 'react-native';
import React, { Component } from 'react';
import { getHeight, getWidth } from '../../utils/adaptive';
import PropTypes from 'prop-types';

/**
 * Red warning on top of application with message 
 */
export default class WarningComponent extends Component {
    constructor(props) {
        super(props);
    }

    shouldComponentUpdate(nextProps) {
        return this.props.notification !== nextProps.notification;
    }

    render() {
        let props = this.props;
        console.log("render",this.props )

        if (props.notification) {
            let customColor = { backgroundColor: props.notification.color }

            return(
                <View style = { props.notification ? [styles.secretPhraseButton, customColor] : [styles.secretPhraseHide, customColor] } >
                    <StatusBar backgroundColor = { props.notification.color } barStyle = { "dark-content" }/>
                    <Text style = { styles.secretPhraseText }>{ props.notification.message }</Text>
                </View>
            );
        }

        return(
            <View style = { styles.secretPhraseHide } >
                <StatusBar backgroundColor = { '#ffffff' } barStyle = { "dark-content" }/>
            </View>
        );
    }
}

WarningComponent.propTypes = {
    notification: PropTypes.object,
};

const styles = StyleSheet.create({
    secretPhraseText: { 
        fontFamily: 'montserrat_medium', 
        fontSize: getHeight(12), 
        color: '#FFFFFF' 
    },
    secretPhraseButton: { 
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        height: getHeight(20),
        justifyContent: 'space-between',
        paddingHorizontal: getWidth(20),
        alignItems: 'center', 
        flexDirection: 'row' 
    },
    secretPhraseHide: { 
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        height: getHeight(0),
        justifyContent: 'space-between',
        paddingHorizontal: getWidth(20),
        alignItems: 'center', 
        flexDirection: 'row' 
    },
    expandImage: { 
        height: getHeight(24), 
        width: getWidth(24)
    },
});
