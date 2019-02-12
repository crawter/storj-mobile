import React, { Component } from 'react';
import {
    View,
    StyleSheet,
    Text,
    Image,
    TouchableOpacity,
    FlatList
} from 'react-native';
import PropTypes from 'prop-types';
import { getWidth, getHeight } from '../../utils/adaptive';

/**
 * Custom Expander component, used in main page
 */
export default class ExpanderComponent extends Component {
    constructor(props) {
        super(props); 

        this.state = {
            isExpanded: true
        };

        this.onPress = this.onPress.bind(this);
        this.keyExtractor = this.keyExtractor.bind(this);
    };

    onPress() {
        this.setState({ isExpanded: !this.state.isExpanded });
    }

    keyExtractor(item, i) {
        return "" + i;
    } 

    render() {        
        const expanderContainerStyle = this.state.isExpanded ? styles.expanderContainer : [styles.expanderContainer, styles.expanderBorder];
        return (
            <TouchableOpacity style = { expanderContainerStyle } onPress = { this.onPress }>
                <View style = { styles.expanderTextView }>
                    <Text style = { styles.expanderText }> { this.props.propName } </Text>
                    <View style = { styles.imageWrapper }>
                    {
                        this.props.isListActionsDisabled ? <View/>
                        : <Image 
                            source = { this.state.isExpanded ? require('../../images/Icons/expandList.png') : require('../../images/Icons/collapsList.png') }
                            style = { this.state.isExpanded ? styles.expanderImage : styles.collapseImage } />
                    }
                        
                    </View>
                </View>
                <View>
                { 
                     this.state.isExpanded ?
                        <FlatList
                            initialNumToRender = { 15 }
                            data = { this.props.listItems }
                            renderItem = { this.props.getItem }
                            keyExtractor = { this.keyExtractor } /> : null                          
                }   	
                </View>
            </TouchableOpacity>)
    };
}

ExpanderComponent.propTypes = {
    listItems: PropTypes.array,
    propName: PropTypes.string
};

const styles = StyleSheet.create({
    imageWrapper: {
        marginTop: getHeight(25),
        marginRight: getWidth(5),
        justifyContent: 'center',
        alignContent: 'center'
    },
    expanderTextView: {
        marginHorizontal: getWidth(10),
        height: getHeight(55),
        width: getWidth(333),
        justifyContent: 'space-between',
        flexDirection: 'row'
    },
    expanderText: {
        marginTop: getHeight(30),        
        fontFamily: 'montserrat_regular',
        fontSize: getHeight(14),
        color: 'rgba(56, 75, 101, 0.4)'
    },
    expanderImage: {
        width: getWidth(12),
        height: getHeight(7),
    },
    collapseImage: {
        width: getWidth(7),
        height: getHeight(12),
    },
    expanderContainer: {
        marginHorizontal: getWidth(10)
    },
    expanderBorder: {
        borderBottomWidth: getHeight(0.5),
        borderColor: 'rgba(56, 75, 101, 0.2)'
    }
});