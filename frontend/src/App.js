import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {
  state = {
    isLoading: true,
    data: {}
  }

  componentDidMount() {
    fetch('data.json').then(response => {
      return response.json();
    }).then(data => {
      this.setState({ isLoading: false, data: data });
    });
  }

  render() {
    if (this.state.isLoading) {
      return (<div className="App">Loading...</div>);
    } else {
      return (
        <div className="App">
          <div className="App-header">
            <img src={logo} className="App-logo" alt="logo" />
            <h2>Welcome to Endless Sky encyclopedia</h2>
          </div>
          <p className="App-intro">
            Loaded {this.state.data.length} ships.
          </p>
        </div>
      );
    }
  }
}

export default App;
