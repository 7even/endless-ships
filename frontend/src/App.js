import React, { Component } from 'react';
import { Table, Label } from 'react-bootstrap';
import logo from './logo.svg';
import 'bootstrap/dist/css/bootstrap.css';
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

  renderLicenses(ship) {
    return ship.licenses.map(license => (
      <Label key={license}>{license}</Label>
    ));
  }

  renderRows() {
    return this.state.data.map(ship => (
      <tr key={ship.name}>
        <td>{ship.name}</td>
        <td>{ship.race}</td>
        <td>{ship.cost}</td>
        <td>{ship.hull}</td>
        <td>{ship.shields}</td>
        <td>{this.renderLicenses(ship)}</td>
      </tr>
    ));
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
          <Table striped bordered condensed hover>
            <thead>
              <tr>
                <th>Name</th>
                <th>Race</th>
                <th>Cost</th>
                <th>Hull</th>
                <th>Shields</th>
                <th>Licenses</th>
              </tr>
            </thead>
            <tbody>
              {this.renderRows()}
            </tbody>
          </Table>
        </div>
      );
    }
  }
}

export default App;
