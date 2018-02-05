import React, { Component } from 'react';
import { connect } from 'react-redux';

class Reference extends Component {
  componentDidMount() {
    window.fetch('/data.json')
      .then(response => response.json())
      .then(data => this.props.dispatch({ type: 'load-data', data: data }));
  }

  render() {
    if (this.props.isLoading) {
      return <div className="app">Loading...</div>;
    } else {
      return this.props.children;
    }
  }
}

const mapStateToProps = state => ({
  isLoading: state.isLoading
});

export default connect(mapStateToProps)(Reference);
