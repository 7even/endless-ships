import React, { Component } from 'react';
import { Table, Label } from 'react-bootstrap';
import NumberFormat from 'react-number-format';
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

  renderLabel(text) {
    switch (text) {
      case 'human':
      case 'Navy':
      case 'Carrier':
      case 'Cruiser':
      case 'Militia Carrier':
        return (<Label bsStyle="info">{text}</Label>);
      case 'hai':
      case 'Unfettered Militia':
        return (<Label bsStyle="primary">{text}</Label>);
      case 'quarg':
        return (<Label bsStyle="warning">{text}</Label>);
      case 'korath':
        return (<Label bsStyle="danger">{text}</Label>);
      case 'wanderer':
      case 'Wanderer':
      case 'Wanderer Military':
        return (<Label bsStyle="success">{text}</Label>);
      case 'coalition':
        return (<Label bsStyle="default">{text}</Label>);
      default:
        return (<Label>{text}</Label>);
    }
  }

  renderLicenses(ship) {
    return ship.licenses.map(license => this.renderLabel(license));
  }

  renderRows() {
    return this.state.data.map(ship => (
      <tr key={ship.name}>
        <td className="text-left">{ship.name}</td>
        <td className="text-left">
          {this.renderLabel(ship.race)}
        </td>
        <td className="text-right">
          <NumberFormat value={ship.cost}
                        displayType={'text'}
                        thousandSeparator={true} />
        </td>
        <td className="text-right">
          <NumberFormat value={ship.hull}
                        displayType={'text'}
                        thousandSeparator={true} />
        </td>
        <td className="text-right">
          <NumberFormat value={ship.shields}
                        displayType={'text'}
                        thousandSeparator={true} />
        </td>
        <td className="text-left">
          {this.renderLicenses(ship)}
        </td>
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
                <th className="text-center">Name</th>
                <th className="text-center">Race</th>
                <th className="text-center">Cost</th>
                <th className="text-center">Hull</th>
                <th className="text-center">Shields</th>
                <th className="text-center">Licenses</th>
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
